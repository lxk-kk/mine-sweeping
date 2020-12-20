package mine_sweeping;

import java.util.Random;

/**
 * 雷盘数据封装
 *
 * @author 10652
 */
public class MineBoard {

    protected Station[][] boardArray;
    protected int[] blockX;
    protected int[] blockY;
    protected static int blockNum;
    protected static int currentBlockNum;
    private static int blockAround;
    private static int newX;
    private static int newY;

    public MineBoard(int X, int Y, int N) {
        blockNum = 0;
        currentBlockNum = 0;
        boardArray = new Station[X + 1][Y + 1];
        blockX = new int[N];
        blockY = new int[N];
        blockNum = N;

        // 初始化雷盘
        for (int i = 1; i <= X; ++i) {
            for (int j = 1; j <= Y; ++j) {
                boardArray[i][j] = Station.zero;
            }
        }

        // 放雷
        Random blockCreator = new Random();
        while (currentBlockNum < blockNum) {
            newX = blockCreator.nextInt(X) + 1;
            newY = blockCreator.nextInt(Y) + 1;
            if (judge(newX, newY)) {
                boardArray[newX][newY] = Station.mine;
                blockX[currentBlockNum] = newX;
                blockY[currentBlockNum] = newY;
                currentBlockNum++;
            }
        }
        // 计算周围雷数
        for (int i = 1; i <= X; ++i) {
            for (int j = 1; j <= Y; ++j) {
                if (boardArray[i][j] != Station.mine) {
                    blockAround = 0;
                    if (i - 1 >= 1 && j - 1 >= 1 && boardArray[i - 1][j - 1] == Station.mine) {
                        blockAround++;
                    }
                    if (i - 1 >= 1 && boardArray[i - 1][j] == Station.mine) {
                        blockAround++;
                    }
                    if (i - 1 >= 1 && j + 1 <= Y && boardArray[i - 1][j + 1] == Station.mine) {
                        blockAround++;
                    }
                    if (j - 1 >= 1 && boardArray[i][j - 1] == Station.mine) {
                        blockAround++;
                    }
                    if (j + 1 <= Y && boardArray[i][j + 1] == Station.mine) {
                        blockAround++;
                    }
                    if (i + 1 <= X && j - 1 >= 1 && boardArray[i + 1][j - 1] == Station.mine) {
                        blockAround++;
                    }
                    if (i + 1 <= X && boardArray[i + 1][j] == Station.mine) {
                        blockAround++;
                    }
                    if (i + 1 <= X && j + 1 <= Y && boardArray[i + 1][j + 1] == Station.mine) {
                        blockAround++;
                    }
                    boardArray[i][j] = Station.getStationByValue(blockAround);
                }
            }
        }
    }

    private boolean judge(int X, int Y) {
        if (boardArray[X][Y] == Station.mine) {
            return false;
        }
        return true;
    }
}