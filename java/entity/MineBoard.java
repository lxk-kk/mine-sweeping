package entity;

import util.Station;

import javax.swing.*;
import java.util.Random;

/**
 * 雷盘数据封装
 *
 * @author 10652
 */
public class MineBoard extends JPanel {
    public Station[][] boardArray;
    public Station[][] boardArrayPre;

    public int xDim;
    public int yDim;
    public int blockNum;


    public MineBoard(int xDim, int yDim, int blockNum) {
        this.xDim = xDim;
        this.yDim = yDim;
        this.blockNum = blockNum;
        createBoard();
    }

    public int getMineCount() {
        int count = 0;
        for (int i = 1; i <= xDim; i++) {
            for (int j = 1; j <= yDim; j++) {
                if (boardArray[i][j] == Station.mine) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 重置雷盘数据
     */
    void flushBoard() {
        createBoard();
    }

    /**
     * 创建雷盘
     */
    private void createBoard() {
        boardArray = new Station[xDim + 1][yDim + 1];
        boardArrayPre = new Station[xDim + 1][yDim + 1];
        // 初始化雷盘
        for (int i = 1; i <= xDim; ++i) {
            for (int j = 1; j <= yDim; ++j) {
                boardArray[i][j] = Station.zero;
            }
        }
        // 放雷
        randomPutMine(blockNum, true);
        // 计算周围雷数
        calculateAroundMine(true);
    }

    /**
     * 随机放雷
     */
    private void randomPutMine(int blockNum, boolean preCopy) {
        Random blockCreator = new Random();
        int currentBlockNum = 0;
        while (currentBlockNum < blockNum) {
            int newX = blockCreator.nextInt(xDim) + 1;
            int newY = blockCreator.nextInt(yDim) + 1;
            // 如果当前格子不是雷，则放雷
            if (boardArray[newX][newY] != Station.mine) {
                boardArray[newX][newY] = Station.mine;
                if(preCopy) {
                    boardArrayPre[newX][newY] = Station.mine;
                }
                currentBlockNum++;
            }
        }
    }

    public void rePutMine(int x, int y) {
        if (x - 1 >= 1 && y - 1 >= 1 && boardArray[x - 1][y - 1] != Station.mine) {
            boardArray[x - 1][y - 1] = Station.mine;
        } else if (x - 1 >= 1 && boardArray[x - 1][y] != Station.mine) {
            boardArray[x - 1][y] = Station.mine;
        } else if (x - 1 >= 1 && y + 1 <= yDim && boardArray[x - 1][y + 1] != Station.mine) {
            boardArray[x - 1][y + 1] = Station.mine;
        } else if (y - 1 >= 1 && boardArray[x][y - 1] != Station.mine) {
            boardArray[x][y - 1] = Station.mine;
        } else if (y + 1 <= yDim && boardArray[x][y + 1] != Station.mine) {
            boardArray[x][y + 1] = Station.mine;
        } else if (x + 1 <= xDim && y - 1 >= 1 && boardArray[x + 1][y - 1] != Station.mine) {
            boardArray[x + 1][y - 1] = Station.mine;
        } else if (x + 1 <= xDim && boardArray[x + 1][y] != Station.mine) {
            boardArray[x + 1][y] = Station.mine;
        } else if (x + 1 <= xDim && y + 1 <= yDim && boardArray[x + 1][y + 1] != Station.mine) {
            boardArray[x + 1][y + 1] = Station.mine;
        } else {
            randomPutMine(1, false);
        }
    }

    /**
     * 计算周围雷数
     */
    public void calculateAroundMine(boolean preCopy) {
        int count = 0;
        for (int x = 1; x <= xDim; ++x) {
            for (int y = 1; y <= yDim; ++y) {
                if (boardArray[x][y] != Station.mine) {
                    int blockAround = 0;
                    if (x - 1 >= 1 && y - 1 >= 1 && boardArray[x - 1][y - 1] == Station.mine) {
                        blockAround++;
                    }
                    if (x - 1 >= 1 && boardArray[x - 1][y] == Station.mine) {
                        blockAround++;
                    }
                    if (x - 1 >= 1 && y + 1 <= yDim && boardArray[x - 1][y + 1] == Station.mine) {
                        blockAround++;
                    }
                    if (y - 1 >= 1 && boardArray[x][y - 1] == Station.mine) {
                        blockAround++;
                    }
                    if (y + 1 <= yDim && boardArray[x][y + 1] == Station.mine) {
                        blockAround++;
                    }
                    if (x + 1 <= xDim && y - 1 >= 1 && boardArray[x + 1][y - 1] == Station.mine) {
                        blockAround++;
                    }
                    if (x + 1 <= xDim && boardArray[x + 1][y] == Station.mine) {
                        blockAround++;
                    }
                    if (x + 1 <= xDim && y + 1 <= yDim && boardArray[x + 1][y + 1] == Station.mine) {
                        blockAround++;
                    }
                    boardArray[x][y] = Station.getStationByValue(blockAround);
                    if (preCopy) {
                        boardArrayPre[x][y] = Station.getStationByValue(blockAround);
                    }
                } else {
                    count++;
                }
            }
        }
        // System.out.println("count ------------- " + count);
    }
}