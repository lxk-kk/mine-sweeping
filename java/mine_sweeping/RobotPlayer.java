package mine_sweeping;

import game_face.MineWindow;
import util.Station;

import java.util.Random;

/**
 * 机器人自动扫雷
 *
 * @author 10652
 */
public class RobotPlayer {
    public Station[][] currentArray;

    /**
     * 周围格子对当前格子的雷概率分布情况！
     * zero : 102
     * one ~ eight : 100，若周围格子试探完成，则为 103
     * flag : 101
     * unknown : 概率 （初始标记为 -1）
     * mine : 无！
     */
    public double[][] probMine;
    private static int xDim;
    private static int yDim;
    private int mineAround;
    private int blankAreaAround;
    private int mineFoundAround;
    private static final double eps = 1e-10;
    private static final double eps2 = 1e-6;

    public RobotPlayer(int xDim, int yDim) {
        RobotPlayer.xDim = xDim;
        RobotPlayer.yDim = yDim;
        currentArray = new Station[xDim + 1][yDim + 1];
        probMine = new double[xDim + 1][yDim + 1];

        for (int x = 1; x <= xDim; ++x) {
            for (int y = 1; y <= yDim; ++y) {
                currentArray[x][y] = Station.unknown;
            }
        }
    }


    /**
     * 自动扫雷策略：
     * 情况 1：根据已知区域，能够准确找到某一格无雷
     * 情况 2：所有格子无法直接判断，则通过 解非齐次线性方程组 找出有雷概率最大的格子
     * 情况 3：随意点击一个格子（例如：第一次试探，无雷，且无法判断下一次可试探的格子）
     */
    public void robotPlay() {
        flushCurrentState();
        while (flushPlayState()) {
            if (!bruteForce() && !gaussLiner()) {
                experienceStep();
            }
        }
    }

    /**
     * 高斯消元法解多元线性方程组！
     * 根据多个数字之间的关系来确定空格子是雷还是空，想让这些数字建立起关系的方式就是根据每个数字，将其周围的 8个格子分别设一个未知数。
     * 如果是雷我们让解为 1，如果是空解就为 0，这样可以连立出一个线性方程组。
     * 只要解出这个线性方程组，高斯消元，就可以做出决策。
     * 注意：这里出现多解是很有可能的事，这里有一个不可忽略的条件，任意未知数只能为 0或 1。
     *
     * @return
     */
    private boolean gaussLiner() {
        int tot = xDim * yDim;
        double[][] matrix = new double[tot + 1][tot + 2];
        createMatrix(tot, matrix);
        // System.out.println("创建矩阵：");
        // printMatrix(tot, matrix);
        matrixReduction(tot, matrix);
        // System.out.println("矩阵变换：");
        // printMatrix(tot, matrix);
        return solveEquationAndStep(tot, matrix);
    }

    private void printMatrix(int tot, double[][] matrix) {
        System.out.println(" -------------------- ");
        for (int i = 1; i <= tot; i++) {
            System.out.print("i: " + i + " = ");
            for (int j = 1; j <= tot + 1; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println(" -------------------- ");
    }

    /**
     * 生成线性方程组：AX=Y
     * 矩阵 A 中，元素 a = 1，表示 unknown 格子
     * 矩阵 X 中，元素 x = 1，则表示该格子有雷
     * 向量 Y 中，元素 y 表示，第 i 个格子周围的剩余雷数
     *
     * @param tot
     * @param matrix
     */
    private void createMatrix(int tot, double[][] matrix) {
        for (int i = 1; i <= tot; ++i) {
            for (int j = 1; j <= tot + 1; ++j) {
                matrix[i][j] = 0;
            }
        }
        int matrixRow;
        for (int x = 1; x <= xDim; ++x) {
            for (int y = 1; y <= yDim; ++y) {
                // 保证当前格子周围一定有雷！
                if ((mineAround = currentArray[x][y].getValue()) <= 0) {
                    continue;
                }
                matrixRow = (x - 1) * yDim + y;
                // 左上角！
                if (x > 1 && y > 1) {
                    matrixAssignment(x - 1, y - 1, matrixRow, matrix);
                }
                // 正上方
                if (x > 1) {
                    matrixAssignment(x - 1, y, matrixRow, matrix);
                }
                // 右上角
                if (x > 1 && y < yDim) {
                    matrixAssignment(x - 1, y + 1, matrixRow, matrix);
                }
                // 左边
                if (y > 1) {
                    matrixAssignment(x, y - 1, matrixRow, matrix);
                }
                // 右边
                if (y < yDim) {
                    matrixAssignment(x, y + 1, matrixRow, matrix);
                }
                // 左下角
                if (x < xDim && y > 1) {
                    matrixAssignment(x + 1, y - 1, matrixRow, matrix);
                }
                // 正下方
                if (x < xDim) {
                    matrixAssignment(x + 1, y, matrixRow, matrix);
                }
                // 右下角
                if (x < xDim && y < yDim) {
                    matrixAssignment(x + 1, y + 1, matrixRow, matrix);
                }
                // 将当前格子周围还剩余的雷数保存，作为 Y 向量
                matrix[matrixRow][tot + 1] = mineAround;
            }
        }
    }

    /**
     * 根据 currentArray 中当前格子的状态，对矩阵赋值
     * 若当前格子为 unknown，则矩阵对应元素赋值为 1，表示该格子对应的 x 系数为 1
     * 若当前格子为 flag，则对应的 Y 的值减一，表示周围未知雷数减一
     *
     * @param x
     * @param y
     * @param matrixRow
     * @param matrix
     */
    private void matrixAssignment(int x, int y, int matrixRow, double[][] matrix) {
        if (currentArray[x][y] == Station.unknown) {
            int matrixCol = (x - 1) * yDim + y;
            matrix[matrixRow][matrixCol] = 1.0;
        } else if (currentArray[x][y] == Station.flag) {
            mineAround--;
        }
    }

    /**
     * 矩阵行列式初等变换（行变换）化简行列式。
     *
     * @param tot
     * @param matrix
     */
    private void matrixReduction(int tot, double[][] matrix) {
        int targetRow = 1;
        /*
            从第 1 列开始，若第 col 列中的元素存在 1，则以该 1 所在行为基准做初等行变换
            保证该列只有 1 个元素有值，其余都为 0，然后将 该元素所在行 放置到 targetRow 指定的行（形成上三角）！
        */
        for (int col = 1; col <= tot; ++col) {

            // ------------------------------------------
            // 两层循环：找到第 col 列中第一个不为 0 的元素，并将该元素所在行交换到 targetRow 指定的行
            for (int row = targetRow; row <= tot; ++row) {
                if (Math.abs(matrix[row][col]) > eps) {
                    // 从第 col 列开始交换，因为，col 列之前的元素全为 0，没必要
                    for (int c = col; c <= tot + 1; ++c) {
                        double tmp = matrix[row][c];
                        matrix[row][c] = matrix[targetRow][c];
                        matrix[targetRow][c] = tmp;
                    }
                    // 找到并交换后，便退出该过程！
                    break;
                }
            }

            // ------------------------------------------
            // 如果第 col 列中，元素全为 0，则直接下一列！
            if (Math.abs(matrix[targetRow][col]) < eps) {
                continue;
            }

            // ------------------------------------------
            /*
                以第 targetRow 行为基准，行变换消去第 col 列上的非零元素！
                todo
                  row 从第 1 行开始，但是列元素消零时，是从第 col 列开始的
                  这么说，第 1 行到第 targetRow 行的前 col 列都不会改变，不合理啊！
             */
            for (int row = 1; row <= tot; ++row) {
                if (row != targetRow && Math.abs(matrix[row][col]) > eps) {
                    double tmp = matrix[row][col] / matrix[targetRow][col];
                    for (int c = col; c <= tot + 1; ++c) {
                        matrix[row][c] -= tmp * matrix[targetRow][c];
                    }
                }
            }
            targetRow++;
        }
    }

    /**
     * 解多元线性方程组，找出确定解，并标记或打开相应格子
     * 方程组的解，要么是 1 要么是 0
     * 1：表示当前格子有雷
     * 0：表示当前格子无雷
     *
     * @param tot
     * @param matrix
     * @return false：没有找到确定解
     */
    private boolean solveEquationAndStep(int tot, double[][] matrix) {
        int x;
        int y;
        // 每一行表示，每个格子对所有格子的影响！
        for (int row = 1; row <= tot; ++row) {
            double positiveN = 0;
            double negativeN = 0;

            // ------------------------------------------
            // 分别计算第 row行 方程的 正负系数之和！
            for (int col = 1; col <= tot; ++col) {
                if (matrix[row][col] > eps) {
                    positiveN += matrix[row][col];
                }
                if (matrix[row][col] < -eps) {
                    negativeN += matrix[row][col];
                }
            }
            if (positiveN < eps && negativeN > -eps) {
                // todo 是否所有非零方程都被移到顶部呢？ 结果似乎并不影响！
                return false;
            }

            // ------------------------------------------
            // 遍历第 row行 的每一列，假设为1或者0，判断解是否合理！
            for (int col = 1; col <= tot; ++col) {
                y = (y = col % yDim) == 0 ? yDim : y;
                x = (col - y) / yDim + 1;

                // unknown 格子系数为正
                if (matrix[row][col] > eps) {
                    // 若无雷，则 x 必然为 0，否则必然有雷（右键）！
                    if (positiveN - matrix[row][col] - matrix[row][tot + 1] < -eps) {
                        stepRightButton(x, y);
                        return true;
                    }
                    // 若有雷，则 x 必然为 1，否则必然无雷（左键）！
                    if (matrix[row][col] + negativeN - matrix[row][tot + 1] > eps) {
                        stepLeftButton(x, y);
                        return true;
                    }
                }
                // unknown 格子系数为负
                if (matrix[row][col] < -eps) {
                    // 若无雷，则 x 必然为 0，否则必然有雷（右键）！
                    if (negativeN - matrix[row][col] - matrix[row][tot + 1] > eps) {
                        stepRightButton(x, y);
                        return true;
                    }
                    // 若有雷，则 x 必然为 1，否则必然无雷（左键）！
                    if (matrix[row][col] + positiveN - matrix[row][tot + 1] < -eps) {
                        stepLeftButton(x, y);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 刷新每个格子是雷的概率
     */
    private void flushAllProbability() {
        for (int x = 1; x <= xDim; x++) {
            for (int y = 1; y <= yDim; y++) {
                probMine[x][y] = 0.0;
            }
        }
    }

    /**
     * 刷新单个格子中有雷的概率
     *
     * @param x
     * @param y
     * @param station
     */
    private void stepOneProbability(int x, int y, Station station) {
        if (station == Station.flag) {
            probMine[x][y] = -2;
            // 周围格子记录的 prob - 1
        }
        probMine[x][y] = 0;
        if (station.getValue() > Station.zero.getValue()) {
            // 周围有 n 个雷！
            // 周围格子记录的 prob + 1
        }
    }


    /**
     * 经验猜
     */
    private void experienceStep() {
        if (!cornerStepTwo()) {
            // 4
            // fiveSquareStep();
            // 5
            // stepOneByProb();
            // 6
            randomStep();
        }
    }

    private void fiveSquareStep() {
        int five = 5;
        // 已知计数
        int[] knownCount = new int[yDim + 1];

        // 1~5 行，中各列总和
        for (int x = 1; x <= five; x++) {
            for (int y = 1; y <= yDim; y++) {
                if (currentArray[x][y] != Station.unknown) {
                    knownCount[y]++;
                }
            }
        }
        int minX = 0, minY = 0;
        int count = -1;
        int[] total = new int[yDim - five + 1 + 1];
        // 1~5 列的总和！
        for (int i = 1; i <= five; ++i) {
            total[1] += knownCount[i];
        }
        // 初始设置！
        if (total[1] < 25) {
            count = total[1];
            minX = 1;
            minY = 1;
        }
        // total[1] = currentArray[1~5][1~5] 的总和

        // 2~yDim 列中，每五列的总和！
        for (int y = five + 1; y <= yDim; ++y) {
            int idy = y - five + 1;
            total[idy] = total[idy - 1] + knownCount[y] - knownCount[y - five];
            // 做记录：5 * 5
            if (total[idy] < 25 && total[idy] > count) {
                count = total[idy];
                minX = 1;
                minY = idy;
            }
        }

        //  2~xDim 行，中每 5*5 的总和！
        for (int x = five + 1; x <= xDim; x++) {
            // 某一行中，针对每一列更新 knownCount 值
            for (int y = 1; y <= yDim; y++) {
                // 两个格子都已知
                if (currentArray[x][y] != currentArray[x - five][y]
                        && (currentArray[x][y] == Station.unknown || currentArray[x - five][y] == Station.unknown)) {
                    // 如果当前已知，则说明 x-five 未知，则已知计数 + 1
                    if (currentArray[x][y] != Station.unknown) {
                        knownCount[y]++;
                    } else {
                        knownCount[y]--;
                    }
                }
            }
            total[1] = 0;
            // 1~5 列的总和！
            for (int y = 1; y <= five; ++y) {
                total[1] += knownCount[y];
            }
            if (total[1] < 25 && total[1] > count) {
                count = total[1];
                minX = x - five + 1;
                minY = 1;
            }
            // 2~yDim 列中，每五列的总和！
            for (int y = five + 1; y <= yDim; ++y) {
                int idy = y - five + 1;
                total[idy] = total[idy - 1] + knownCount[y] - knownCount[y - five];
                // 做记录：5 * 5
                if (total[idy] < 25 && total[idy] > count) {
                    count = total[idy];
                    minX = x - five + 1;
                    minY = idy;
                }
            }
            if (count == 24) {
                // System.out.println(minX + " - " + minY + " - " + count);
                randomStep(minX, minY, count);
                return;
            }
        }
        if (count <= 15) {
            randomStep();
        } else {
            // System.out.println(minX + " - " + minY + " - " + count);
            randomStep(minX, minY, count);
        }
    }

    private void randomStep(int xStart, int yStart, int count) {
        Random blockCreator = new Random();
        int newX, newY;
        while (true) {
            newX = blockCreator.nextInt(5) + xStart;
            newY = blockCreator.nextInt(5) + yStart;
            // System.out.println(xStart + " " + yStart + " | " + newX + " " + newY + " - " + count);
            if (currentArray[newX][newY] == Station.unknown) {
                stepLeftButton(newX, newY);
                return;
            }
        }
    }

    private void randomStep() {
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

    private boolean cornerStepTwo() {
        if (currentArray[1][1] == Station.unknown) {
            stepLeftButton(1, 1);
            return true;
        } else if (currentArray[1][yDim] == Station.unknown) {
            stepLeftButton(1, yDim);
            return true;
        } else if (currentArray[xDim][1] == Station.unknown) {
            stepLeftButton(xDim, 1);
            return true;
        } else if (currentArray[xDim][yDim] == Station.unknown) {
            stepLeftButton(xDim, yDim);
            return true;
        }
        return false;
    }

    /**
     * 四角点击！
     * 1、四个角中一般而言，有 1 个角有雷，因此，如果已经标记了这个角，那么后续三个角都可以认为无雷
     * 2、如果最早点击了两个角无雷，那么，不再尝试点击角，除非有雷角被标记！
     *
     * @return false 不可点击
     */
    private boolean cornerStep() {
        if (currentArray[1][1] == Station.unknown) {
            stepLeftButton(1, 1);
            return true;
        }
        int[] corner = new int[]{1, xDim, 1, yDim};
        int unknown = 0;
        int flag = 0;
        if (currentArray[1][1] == Station.flag) {
            flag++;
        }
        if (currentArray[1][yDim] == Station.unknown) {
            unknown++;
        } else if (currentArray[1][yDim] == Station.flag) {
            flag++;

        }
        if (currentArray[xDim][1] == Station.unknown) {
            unknown++;
        } else if (currentArray[xDim][1] == Station.flag) {
            flag++;
        }
        if (currentArray[xDim][yDim] == Station.unknown) {
            unknown++;
        } else if (currentArray[xDim][yDim] == Station.flag) {
            flag++;
        }
        if (flag == 0 && unknown <= 1 || unknown == 0) {
            // 角落未发现有雷，但是，已经点开了至少两个角，此时，为了安全起见，不点击
            // 四个全部发现！
            return false;
        }
        Random random = new Random();
        int xIdx, yIdx;
        while (true) {
            xIdx = random.nextInt(2);
            yIdx = random.nextInt(2) + 2;
            if (currentArray[corner[xIdx]][corner[yIdx]] == Station.unknown) {
                stepLeftButton(corner[xIdx], corner[yIdx]);
                break;
            }
        }
        return true;
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
                // todo 可优化！很多格子周围有雷，但是已经全部被标记出！
                if (currentArray[i][j].getValue() > Station.zero.getValue() && tryClick(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryClick(int x, int y) {
        aroundSituation(x, y);
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

    /**
     * 计算周围情况！
     *
     * @param x
     * @param y
     */
    private void aroundSituation(int x, int y) {
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
    }

    private void query(int x, int y) {
        if (currentArray[x][y] == Station.unknown) {
            blankAreaAround++;
        } else if (currentArray[x][y] == Station.flag) {
            mineFoundAround++;
        }
    }

    private boolean canClick(int x, int y) {
        if (x >= 1 && y >= 1 && x <= xDim && y <= yDim) {
            if (currentArray[x][y] == Station.unknown) {
                return true;
            }
        }
        return false;
    }

    private void calculateAroundSituation() {
        for (int x = 1; x <= xDim; x++) {
            for (int y = 1; y <= yDim; y++) {
                if (probMine[x][y] - 100.1 > eps) {
                    continue;
                }
                if (currentArray[x][y].getValue() > Station.zero.getValue()) {
                    probMine[x][y] = 100;
                    aroundSituation(x, y);
                    if (blankAreaAround == 0) {
                        // 周围有雷，但周围已全部试探完！
                        probMine[x][y] = 103;
                    } else if (mineAround - mineFoundAround > 0) {
                        // 剩余空格中有雷！
                        calculateAroundProb(x, y, (mineAround - mineFoundAround) * 1.0 / blankAreaAround);
                    }
                    // 不用考虑其他情况！
                } else if (currentArray[x][y] == Station.flag) {
                    // 当前标记是雷！
                    probMine[x][y] = 101;
                } else if (currentArray[x][y] == Station.zero) {
                    // 周围无雷！
                    probMine[x][y] = 102;
                }
            }
        }
    }

    private void flushAroundProb() {
        for (int x = 1; x <= xDim; x++) {
            for (int y = 1; y <= yDim; y++) {
                if (probMine[x][y] < 99) {
                    probMine[x][y] = 50;
                }
            }
        }
    }

    private boolean stepOneMinProb() {
        int xProb = 1;
        int yProb = 1;
        double prob = probMine[xProb][yProb];
        int count = 0;
        // System.out.println(" --------------------------- ");
        for (int x = 1; x <= xDim; x++) {
            for (int y = 1; y <= yDim; y++) {
                // System.out.print(probMine[x][y] + " ");
                if (probMine[x][y] < 50 && probMine[x][y] < prob && Math.abs(probMine[x][y] - prob) < eps2) {
                    xProb = x;
                    yProb = y;
                }
                // < 49 表示不包含概率试探不到的 unknown 格子
            }
            // System.out.println();
        }
        // System.out.println(xProb + " " + yProb + "  / " + probMine[xProb][yProb] + " / " + count);

        // System.out.println(" --------------------------- ");
        if (probMine[xProb][yProb] > 99) {
            randomStep();
        } else {
            stepLeftButton(xProb, yProb);
        }
        return true;
    }

    private boolean stepOneByProb() {
        flushAroundProb();
        calculateAroundSituation();
        return stepOneMinProb();
    }

    private void calculateAroundProb(int x, int y, double prob) {
        // 左上
        if (x > 1 && y > 1 && currentArray[x - 1][y - 1] == Station.unknown) {
            if (probMine[x - 1][y - 1] > 49) {
                probMine[x - 1][y - 1] = prob;
            } else {
                probMine[x - 1][y - 1] += prob;
            }
        }
        // 正上
        if (x > 1 && currentArray[x - 1][y] == Station.unknown) {
            if (probMine[x - 1][y] > 49) {
                probMine[x - 1][y] = prob;
            } else {
                probMine[x - 1][y] += prob;
            }
        }
        // 右上
        if (x > 1 && y < yDim && currentArray[x - 1][y + 1] == Station.unknown) {
            if (probMine[x - 1][y + 1] > 49) {
                probMine[x - 1][y + 1] = prob;
            } else {
                probMine[x - 1][y + 1] += prob;
            }
        }
        // 左
        if (y > 1 && currentArray[x][y - 1] == Station.unknown) {
            if (probMine[x][y - 1] > 49) {
                probMine[x][y - 1] = prob;
            } else {
                probMine[x][y - 1] += prob;
            }
        }
        // 右
        if (y < yDim && currentArray[x][y + 1] == Station.unknown) {
            if (probMine[x][y + 1] > 49) {
                probMine[x][y + 1] = prob;
            } else {
                probMine[x][y + 1] += prob;
            }
        }
        // 左下
        if (x < xDim && y > 1 && currentArray[x + 1][y - 1] == Station.unknown) {
            if (probMine[x + 1][y - 1] > 49) {
                probMine[x + 1][y - 1] = prob;
            } else {
                probMine[x + 1][y - 1] += prob;
            }
        }
        // 下
        if (x < xDim && currentArray[x + 1][y] == Station.unknown) {
            if (probMine[x + 1][y] > 49) {
                probMine[x + 1][y] = prob;
            } else {
                probMine[x + 1][y] += prob;
            }
        }
        // 右下
        if (x < xDim && y < yDim && currentArray[x + 1][y + 1] == Station.unknown) {
            if (probMine[x + 1][y + 1] > 49) {
                probMine[x + 1][y + 1] = prob;
            } else {
                probMine[x + 1][y + 1] += prob;
            }
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
        MineWindow.getInstance().getMinePanel().solveLeftButtonEvents(x, y);
    }

    public void rightButtonEvent(int x, int y) {
        MineWindow.getInstance().getMinePanel().solveRightButtonEvents(x, y);
    }

    public boolean flushPlayState() {
        return MineWindow.getInstance().getMinePanel().isPlaying();
    }

    public void flushCurrentState() {
        currentArray = MineWindow.getInstance().getMinePanel().getCurrentArr();
    }
}