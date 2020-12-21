package mine_sweeping;

import util.Station;

import java.util.Random;

/**
 * 机器人自动扫雷（1次）
 *
 * @author 10652
 */
public class RobotPlayer{
    private Station[][] currentArray;
    private static int X;
    private static int Y;
    private static int tot;
    private boolean play = true;
    private int blockAround;
    private int blankAreaAround;
    private int blockFoundAround;
    private double[][] Matrix;
    private static int currentRow;
    private static int currentCol;
    private static double eps = 1e-10;

    public RobotPlayer(int X, int Y) {
        RobotPlayer.X = X;
        RobotPlayer.Y = Y;
        currentArray = new Station[X + 1][Y + 1];
        for (int i = 1; i <= X; ++i) {
            for (int j = 1; j <= Y; ++j) {
                currentArray[i][j] = Station.unknown;
            }
        }
        while (play) {
            robotPlay();
        }
    }

    public void flushCurrentState() {
        play = MineWindow.getInstance().minePanel.isPlaying();
        currentArray = MineWindow.getInstance().minePanel.getCurrentArr();
    }

    /**
     * 自动扫雷策略：
     * 情况 1：根据已知区域，能够准确找到某一格无雷
     * 情况 2：所有格子无法直接判断，则通过 算法 找出有雷概率最大的格子
     * 情况 3：随意点击一个格子（例如：第一次试探，无雷，且无法判断下一次可试探的格子）
     */
    public void robotPlay() {
        flushCurrentState();
        if (!play) {
            return;
        }
        if (!bruteForce() && !GaussLiner()) {
            randomPick();
           // System.out.println("xixi");
        }
    }

    /**
     * 高斯分布求概率
     *
     * @return
     */
    private boolean GaussLiner() {
        tot = X * Y;
        Matrix = new double[tot + 1][tot + 2];
        for (int i = 1; i <= tot; ++i) {
            for (int j = 1; j <= tot + 1; ++j) {
                Matrix[i][j] = 0.0;
            }
        }
        for (int i = 1; i <= X; ++i) {
            for (int j = 1; j <= Y; ++j) {
                if (currentArray[i][j] == Station.mine || currentArray[i][j] == Station.unknown || currentArray[i][j] == Station.zero) {
                    continue;
                }
                currentRow = (i - 1) * Y + j;
                // getBlockAround(i, j); 使用下面这句代替
                blockAround = currentArray[i][j].getValue();
                if (i > 1 && j > 1) {
                    if (currentArray[i - 1][j - 1] == Station.unknown) {
                        currentCol = (i - 2) * Y + j - 1;
                        Matrix[currentRow][currentCol] = 1.0;
                    }
                    if (currentArray[i - 1][j - 1] == Station.mine) {
                        blockAround--;
                    }
                }
                if (i > 1) {
                    if (currentArray[i - 1][j] == Station.unknown) {
                        currentCol = (i - 2) * Y + j;
                        Matrix[currentRow][currentCol] = 1.0;
                    }
                    if (currentArray[i - 1][j] == Station.mine) {
                        blockAround--;
                    }
                }
                if (i > 1 && j < Y) {
                    if (currentArray[i - 1][j + 1] == Station.unknown) {
                        currentCol = (i - 2) * Y + j + 1;
                        Matrix[currentRow][currentCol] = 1.0;
                    }
                    if (currentArray[i - 1][j + 1] == Station.mine) {
                        blockAround--;
                    }
                }
                if (j > 1) {
                    if (currentArray[i][j - 1] == Station.unknown) {
                        currentCol = (i - 1) * Y + j - 1;
                        Matrix[currentRow][currentCol] = 1.0;
                    }
                    if (currentArray[i][j - 1] == Station.mine) {
                        blockAround--;
                    }
                }
                if (j < Y) {
                    if (currentArray[i][j + 1] == Station.unknown) {
                        currentCol = (i - 1) * Y + j + 1;
                        Matrix[currentRow][currentCol] = 1.0;
                    }
                    if (currentArray[i][j + 1] == Station.mine) {
                        blockAround--;
                    }
                }
                if (i < X && j > 1) {
                    if (currentArray[i + 1][j - 1] == Station.unknown) {
                        currentCol = i * Y + j - 1;
                        Matrix[currentRow][currentCol] = 1.0;
                    }
                    if (currentArray[i + 1][j - 1] == Station.mine) {
                        blockAround--;
                    }
                }
                if (i < X) {
                    if (currentArray[i + 1][j] == Station.unknown) {
                        currentCol = i * Y + j;
                        Matrix[currentRow][currentCol] = 1.0;
                    }
                    if (currentArray[i + 1][j] == Station.mine) {
                        blockAround--;
                    }
                }
                if (i < X && j < Y) {
                    if (currentArray[i + 1][j + 1] == Station.unknown) {
                        currentCol = i * Y + j + 1;
                        Matrix[currentRow][currentCol] = 1.0;
                    }
                    if (currentArray[i + 1][j + 1] == Station.mine) {
                        blockAround--;
                    }
                }
                Matrix[currentRow][tot + 1] = (double) blockAround;
            }
        }
        int cur, pos = 1;
        for (int i = 1; i <= tot; ++i) {
            for (int j = pos; j <= tot; ++j) {
                if (Math.abs(Matrix[j][i]) > eps) {
                    for (int k = i; k <= tot + 1; ++k) {
                        double tmp = Matrix[j][k];
                        Matrix[j][k] = Matrix[pos][k];
                        Matrix[pos][k] = tmp;
                    }
                    break;
                }
            }
            if (Math.abs(Matrix[pos][i]) < eps) {
                //		pos++;
                continue;
            }
            for (int j = 1; j <= tot; ++j) {
                if (j != pos && Math.abs(Matrix[j][i]) > eps) {
                    double tmp = Matrix[j][i] / Matrix[pos][i];
                    for (int k = i; k <= tot + 1; ++k) {
                        Matrix[j][k] -= tmp * Matrix[pos][k];
                    }
                }
            }
            pos++;
        }
        for (int i = 1; i <= tot; ++i) {
            cur = 0;
            for (int j = 1; j <= tot; ++j) {
                if (Math.abs(Matrix[i][j]) > eps) {
                    cur++;
                    pos = j;
                }
            }
            //find an answer
//			if(cur == 1) {
//				System.out.println("find");
//				currentRow = pos / y + 1;
//				currentCol = pos % y;
//				if(currentCol == 0) {
//					currentRow--;
//					currentCol = y;
//				}
//				if(Math.abs(Matrix[i][tot + 1] - Matrix[i][pos]) < eps) {
//                  stepRightButton(currentRow, currentCol);
//					return true;
//				}
//				else if(Math.abs(Matrix[i][tot + 1]) < eps){
//                  stepLeftButton(currentRow, currentCol);
//					return true;
//				}
//			}

            //That answer should be either 0 or 1 can be used to find answer
            double positiveN = 0;
            double negativeN = 0;
            for (int j = 1; j <= tot; ++j) {
                if (Matrix[i][j] > eps) {
                    positiveN += Matrix[i][j];
                }
                if (Matrix[i][j] < -eps) {
                    negativeN += Matrix[i][j];
                }
            }
            for (int j = 1; j <= tot; ++j) {
                currentRow = j / Y + 1;
                currentCol = j % Y;
                if (currentCol == 0) {
                    currentRow--;
                    currentCol = Y;
                }
                if (Matrix[i][j] > eps) {
                    // let it be 0, find it can not be 0, so it must be 1
                    if (positiveN - Matrix[i][j] - Matrix[i][tot + 1] < -eps) {
                        stepRightButton(currentRow, currentCol);
                        //	System.out.println(positiveN + " " + negativeN + " " + Matrix[i][tot + 1] + " find1");
                        return true;
                    }
                    // let it be 1, find it can not be 1, so it must be 0
                    if (Matrix[i][j] + negativeN - Matrix[i][tot + 1] > eps) {
                        stepLeftButton(currentRow, currentCol);
                        //	System.out.println(positiveN + " " + negativeN + " " + Matrix[i][tot + 1] + " find2");
                        return true;
                    }
                }
                if (Matrix[i][j] < -eps) {
                    // let it be 0, find it can not be 0, so it must be 1
                    if (negativeN - Matrix[i][j] - Matrix[i][tot + 1] > eps) {
                        stepRightButton(currentRow, currentCol);
                        //	System.out.println(positiveN + " " + negativeN + " " + Matrix[i][tot + 1] + " find3");
                        return true;
                    }
                    // let it be 1, find it can not be 1, so it must be 0
                    if (Matrix[i][j] + positiveN - Matrix[i][tot + 1] < -eps) {
                        stepLeftButton(currentRow, currentRow);
                        //	System.out.println(positiveN + " " + negativeN + " " + Matrix[i][tot + 1] + " find4");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void randomPick() {
        Random blockCreator = new Random();
        int newX, newY;
        while (true) {
            newX = blockCreator.nextInt(X) + 1;
            newY = blockCreator.nextInt(Y) + 1;
            if (currentArray[newX][newY] == Station.unknown) {
                stepLeftButton(newX, newY);
                break;
            }
        }
    }

    private boolean bruteForce() {
        for (int i = 1; i <= X; ++i) {
            for (int j = 1; j <= Y; ++j) {
                // todo 这是做什么判断？？？？已经 试探过的格子不用 tryClick？
                if (currentArray[i][j] == Station.unknown
                        || currentArray[i][j] == Station.mine
                        || currentArray[i][j] == Station.zero) {
                    continue;
                }
                if (tryClick(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryClick(int x, int y) {
        // getBlockAround(i, j); 使用下面这句代替
        blockAround = currentArray[x][y].getValue();
        blankAreaAround = 0;
        blockFoundAround = 0;
        if (x - 1 >= 1 && y - 1 >= 1) {
            query(x - 1, y - 1);
        }
        if (x - 1 >= 1) {
            query(x - 1, y);
        }
        if (x - 1 >= 1 && y + 1 <= Y) {
            query(x - 1, y + 1);
        }
        if (y - 1 >= 1) {
            query(x, y - 1);
        }
        if (y + 1 <= Y) {
            query(x, y + 1);
        }
        if (x + 1 <= X && y - 1 >= 1) {
            query(x + 1, y - 1);
        }
        if (x + 1 <= X) {
            query(x + 1, y);
        }
        if (x + 1 <= X && y + 1 <= Y) {
            query(x + 1, y + 1);
        }
        if (blankAreaAround == 0) {
            return false;
        }
        // 周围有雷的数量 - 周围找到的雷数量：剩余雷的数量 == 周围找到的未开发区域，说明当前格子一定无雷！
        // 右键处理？？标记？？
        if (blankAreaAround == blockAround - blockFoundAround) {
            if (canClick(x - 1, y - 1)) {
                stepRightButton(x - 1, y - 1);
                return true;
            }
            if (canClick(x - 1, y)) {
                stepRightButton(x - 1, y);
                return true;
            }
            if (canClick(x - 1, y + 1)) {
                stepRightButton(x - 1, y + 1);
                return true;
            }
            if (canClick(x, y - 1)) {
                stepRightButton(x, y - 1);
                return true;
            }
            if (canClick(x, y + 1)) {
                stepRightButton(x, y + 1);
                return true;
            }
            if (canClick(x + 1, y - 1)) {
                stepRightButton(x + 1, y - 1);
                return true;
            }
            if (canClick(x + 1, y)) {
                stepRightButton(x + 1, y);
                return true;
            }
            if (canClick(x + 1, y + 1)) {
                stepRightButton(x + 1, y + 1);
                return true;
            }
        }
        // 周围雷的数量 == 找到的雷的数量：当前一定无雷！
        // 左键事件处理！
        if (blockAround == blockFoundAround) {
            if (canClick(x - 1, y - 1)) {
                stepLeftButton(x - 1, y - 1);
                return true;
            }
            if (canClick(x - 1, y)) {
                stepLeftButton(x - 1, y);
                return true;
            }
            if (canClick(x - 1, y + 1)) {
                stepLeftButton(x - 1, y + 1);
                return true;
            }
            if (canClick(x, y - 1)) {
                stepLeftButton(x, y - 1);
                return true;
            }
            if (canClick(x, y + 1)) {
                stepLeftButton(x, y + 1);
                return true;
            }
            if (canClick(x + 1, y - 1)) {
                stepLeftButton(x + 1, y - 1);
                return true;
            }
            if (canClick(x + 1, y)) {
                stepLeftButton(x + 1, y);
                return true;
            }
            if (canClick(x + 1, y + 1)) {
                stepLeftButton(x + 1, y + 1);
                return true;
            }
        }
        return false;
    }

    private boolean canClick(int x, int y) {
        if (x >= 1 && y >= 1 && x <= X && y <= Y) {
            if (currentArray[x][y] == Station.unknown) {
                return true;
            }
        }
        return false;
    }

    private void query(int x, int y) {
        if (currentArray[x][y] == Station.unknown) {
            blankAreaAround++;
        } else if (currentArray[x][y] == Station.mine) {
            blockFoundAround++;
        }
    }

    private void stepLeftButton(int x, int y) {
        MineWindow.getInstance().minePanel.solveLeftButtonEvents(x, y);
    }

    private void stepRightButton(int x, int y) {
        MineWindow.getInstance().minePanel.solveRightButtonEvents(x, y);
    }

    private void getBlockAround(int x, int y) {
        switch (currentArray[x][y]) {
            case one:
                blockAround = 1;
                break;
            case two:
                blockAround = 2;
                break;
            case three:
                blockAround = 3;
                break;
            case four:
                blockAround = 4;
                break;
            case five:
                blockAround = 5;
                break;
            case six:
                blockAround = 6;
                break;
            case seven:
                blockAround = 7;
                break;
            case eight:
                blockAround = 8;
                break;
            default:
                System.out.println("Error");
                break;
        }
    }
}