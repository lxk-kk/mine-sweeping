package mine_sweeping;

import util.Station;

import java.util.Random;

/**
 * 机器人自动扫雷
 *
 * @author 10652
 */
public class RobotPlayer {
    public Station[][] currentArray;
    private static int xDim;
    private static int yDim;
    private int mineAround;
    private int blankAreaAround;
    private int mineFoundAround;
    private static final double eps = 1e-10;

    public RobotPlayer(int xDim, int yDim) {
        RobotPlayer.xDim = xDim;
        RobotPlayer.yDim = yDim;
        currentArray = new Station[xDim + 1][yDim + 1];
        for (int x = 1; x <= xDim; ++x) {
            for (int y = 1; y <= yDim; ++y) {
                currentArray[x][y] = Station.unknown;
            }
        }
    }


    /**
     * 自动扫雷策略：
     * 情况 1：根据已知区域，能够准确找到某一格无雷
     * 情况 2：所有格子无法直接判断，则通过 算法 找出有雷概率最大的格子
     * 情况 3：随意点击一个格子（例如：第一次试探，无雷，且无法判断下一次可试探的格子）
     */
    public void robotPlay() {
        flushCurrentState();
        while (flushPlayState()) {
            if (!bruteForce() && !gaussLiner()) {
                randomPick();
            }
        }
    }

    public boolean flushPlayState() {
        return MineWindow.getInstance().minePanel.isPlaying();
    }

    public void flushCurrentState() {
        currentArray = MineWindow.getInstance().minePanel.getCurrentArr();
    }

    /**
     * 高斯分布求概率
     *
     * @return
     */
    private boolean gaussLiner() {
        int tot = xDim * yDim;
        double[][] matrix = new double[tot + 1][tot + 2];
        for (int i = 1; i <= tot; ++i) {
            for (int j = 1; j <= tot + 1; ++j) {
                matrix[i][j] = 0.0;
            }
        }
        int currentRow;
        int currentCol;
        for (int x = 1; x <= xDim; ++x) {
            for (int y = 1; y <= yDim; ++y) {
                // todo 当前格子被标记、当前格子被查看且周围无雷 等情况都不计算！
                if (currentArray[x][y].getValue() <= 0) {
                    continue;
                }
                currentRow = (x - 1) * yDim + y;
                mineAround = currentArray[x][y].getValue();
                if (x > 1 && y > 1) {
                    if (currentArray[x - 1][y - 1] == Station.unknown) {
                        currentCol = (x - 2) * yDim + y - 1;
                        matrix[currentRow][currentCol] = 1.0;
                    }
                    if (currentArray[x - 1][y - 1] == Station.flag) {
                        mineAround--;
                    }
                }
                if (x > 1) {
                    if (currentArray[x - 1][y] == Station.unknown) {
                        currentCol = (x - 2) * yDim + y;
                        matrix[currentRow][currentCol] = 1.0;
                    }
                    if (currentArray[x - 1][y] == Station.flag) {
                        mineAround--;
                    }
                }
                if (x > 1 && y < yDim) {
                    if (currentArray[x - 1][y + 1] == Station.unknown) {
                        currentCol = (x - 2) * yDim + y + 1;
                        matrix[currentRow][currentCol] = 1.0;
                    }
                    if (currentArray[x - 1][y + 1] == Station.flag) {
                        mineAround--;
                    }
                }
                if (y > 1) {
                    if (currentArray[x][y - 1] == Station.unknown) {
                        currentCol = (x - 1) * yDim + y - 1;
                        matrix[currentRow][currentCol] = 1.0;
                    }
                    if (currentArray[x][y - 1] == Station.flag) {
                        mineAround--;
                    }
                }
                if (y < yDim) {
                    if (currentArray[x][y + 1] == Station.unknown) {
                        currentCol = (x - 1) * yDim + y + 1;
                        matrix[currentRow][currentCol] = 1.0;
                    }
                    if (currentArray[x][y + 1] == Station.flag) {
                        mineAround--;
                    }
                }
                if (x < xDim && y > 1) {
                    if (currentArray[x + 1][y - 1] == Station.unknown) {
                        currentCol = x * yDim + y - 1;
                        matrix[currentRow][currentCol] = 1.0;
                    }
                    if (currentArray[x + 1][y - 1] == Station.flag) {
                        mineAround--;
                    }
                }
                if (x < xDim) {
                    if (currentArray[x + 1][y] == Station.unknown) {
                        currentCol = x * yDim + y;
                        matrix[currentRow][currentCol] = 1.0;
                    }
                    if (currentArray[x + 1][y] == Station.flag) {
                        mineAround--;
                    }
                }
                if (x < xDim && y < yDim) {
                    if (currentArray[x + 1][y + 1] == Station.unknown) {
                        currentCol = x * yDim + y + 1;
                        matrix[currentRow][currentCol] = 1.0;
                    }
                    if (currentArray[x + 1][y + 1] == Station.flag) {
                        mineAround--;
                    }
                }
                matrix[currentRow][tot + 1] = (double) mineAround;
            }
        }
        int cur, pos = 1;
        for (int i = 1; i <= tot; ++i) {
            for (int j = pos; j <= tot; ++j) {
                if (Math.abs(matrix[j][i]) > eps) {
                    for (int k = i; k <= tot + 1; ++k) {
                        double tmp = matrix[j][k];
                        matrix[j][k] = matrix[pos][k];
                        matrix[pos][k] = tmp;
                    }
                    break;
                }
            }
            if (Math.abs(matrix[pos][i]) < eps) {
                //		pos++;
                continue;
            }
            for (int j = 1; j <= tot; ++j) {
                if (j != pos && Math.abs(matrix[j][i]) > eps) {
                    double tmp = matrix[j][i] / matrix[pos][i];
                    for (int k = i; k <= tot + 1; ++k) {
                        matrix[j][k] -= tmp * matrix[pos][k];
                    }
                }
            }
            pos++;
        }
        for (int i = 1; i <= tot; ++i) {
            cur = 0;
            for (int j = 1; j <= tot; ++j) {
                if (Math.abs(matrix[i][j]) > eps) {
                    cur++;
                    pos = j;
                }
            }
            //find an answer
//			if(cur == 1) {
//				System.out.println("find");
//				currentRow = pos / yDim + 1;
//				currentCol = pos % yDim;
//				if(currentCol == 0) {
//					currentRow--;
//					currentCol = yDim;
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
                if (matrix[i][j] > eps) {
                    positiveN += matrix[i][j];
                }
                if (matrix[i][j] < -eps) {
                    negativeN += matrix[i][j];
                }
            }
            for (int j = 1; j <= tot; ++j) {
                currentRow = j / yDim + 1;
                currentCol = j % yDim;
                if (currentCol == 0) {
                    currentRow--;
                    currentCol = yDim;
                }
                if (matrix[i][j] > eps) {
                    // let it be 0, find it can not be 0, so it must be 1
                    if (positiveN - matrix[i][j] - matrix[i][tot + 1] < -eps) {
                        stepRightButton(currentRow, currentCol);
                        //	System.out.println(positiveN + " " + negativeN + " " + Matrix[i][tot + 1] + " find1");
                        return true;
                    }
                    // let it be 1, find it can not be 1, so it must be 0
                    if (matrix[i][j] + negativeN - matrix[i][tot + 1] > eps) {
                        stepLeftButton(currentRow, currentCol);
                        //	System.out.println(positiveN + " " + negativeN + " " + Matrix[i][tot + 1] + " find2");
                        return true;
                    }
                }
                if (matrix[i][j] < -eps) {
                    // let it be 0, find it can not be 0, so it must be 1
                    if (negativeN - matrix[i][j] - matrix[i][tot + 1] > eps) {
                        stepRightButton(currentRow, currentCol);
                        //	System.out.println(positiveN + " " + negativeN + " " + Matrix[i][tot + 1] + " find3");
                        return true;
                    }
                    // let it be 1, find it can not be 1, so it must be 0
                    if (matrix[i][j] + positiveN - matrix[i][tot + 1] < -eps) {
                        stepLeftButton(currentRow, currentCol);
                        //	System.out.println(positiveN + " " + negativeN + " " + Matrix[i][tot + 1] + " find4");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void randomPick() {
        /*int count = 0;
        for (int x = 1; x <= xDim; x++) {
            for (int y = 1; y <= yDim; y++) {
                if (currentArray[x][y] == Station.unknown) {
                    System.out.println("i + j :" + x + " " + y);
                    stepLeftButton(x, y);
                    return;
                }
                if (currentArray[x][y] == Station.flag) {
                    count++;
                }
            }
        }
        System.out.println("Count" + count);*/

        // System.out.println("Wrong!");

        Random blockCreator = new Random();
        int newX, newY;
        while (true) {

            newX = blockCreator.nextInt(xDim) + 1;
            newY = blockCreator.nextInt(yDim) + 1;
            if (currentArray[newX][newY] == Station.unknown) {
                stepLeftButton(newX, newY);
                break;
            }
        }
    }

    /**
     * 直接判断
     *
     * @return
     */
    private boolean bruteForce() {
        for (int i = 1; i <= xDim; ++i) {
            for (int j = 1; j <= yDim; ++j) {
                // 当前格子已经访问，且周围有雷，则尝试点击！
                if (currentArray[i][j].getValue() > 0 && tryClick(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryClick(int x, int y) {
        // getBlockAround(i, j); 使用下面这句代替
        mineAround = currentArray[x][y].getValue();
        blankAreaAround = 0;
        mineFoundAround = 0;
        if (x - 1 >= 1 && y - 1 >= 1) {
            query(x - 1, y - 1);
        }
        if (x - 1 >= 1) {
            query(x - 1, y);
        }
        if (x - 1 >= 1 && y + 1 <= yDim) {
            query(x - 1, y + 1);
        }
        if (y - 1 >= 1) {
            query(x, y - 1);
        }
        if (y + 1 <= yDim) {
            query(x, y + 1);
        }
        if (x + 1 <= xDim && y - 1 >= 1) {
            query(x + 1, y - 1);
        }
        if (x + 1 <= xDim) {
            query(x + 1, y);
        }
        if (x + 1 <= xDim && y + 1 <= yDim) {
            query(x + 1, y + 1);
        }
        if (blankAreaAround == 0) {
            return false;
        }
        // 周围有雷的数量 - 周围找到的雷数量：剩余雷的数量 == 周围找到的未开发区域，说明当前格子一定无雷！
        // 右键处理？？标记？？
        if (blankAreaAround == mineAround - mineFoundAround) {
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
        if (mineAround == mineFoundAround) {
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
        if (x >= 1 && y >= 1 && x <= xDim && y <= yDim) {
            if (currentArray[x][y] == Station.unknown) {
                return true;
            }
        }
        return false;
    }

    private void query(int x, int y) {
        if (currentArray[x][y] == Station.unknown) {
            blankAreaAround++;
        } else if (currentArray[x][y] == Station.flag) {
            mineFoundAround++;
        }
    }

    public void stepLeftButton(int x, int y) {
        flushCurrentState();
        leftButtonEvent(x, y);
    }

    public void stepRightButton(int x, int y) {
        flushCurrentState();
        rightButtonEvent(x, y);
    }

    public void leftButtonEvent(int x, int y) {
        MineWindow.getInstance().minePanel.solveLeftButtonEvents(x, y);
    }

    public void rightButtonEvent(int x, int y) {
        MineWindow.getInstance().minePanel.solveRightButtonEvents(x, y);
    }

    private void getBlockAround(int x, int y) {
        switch (currentArray[x][y]) {
            case one:
                mineAround = 1;
                break;
            case two:
                mineAround = 2;
                break;
            case three:
                mineAround = 3;
                break;
            case four:
                mineAround = 4;
                break;
            case five:
                mineAround = 5;
                break;
            case six:
                mineAround = 6;
                break;
            case seven:
                mineAround = 7;
                break;
            case eight:
                mineAround = 8;
                break;
            default:
                System.out.println("Error");
                break;
        }
    }
}