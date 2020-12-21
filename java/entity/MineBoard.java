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

    public int x;
    public int y;
    public int blockNum;


    public MineBoard(int x, int y, int blockNum) {
        this.x = x;
        this.y = y;
        this.blockNum = blockNum;
        createBoard();
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
        boardArray = new Station[x + 1][y + 1];
        // 初始化雷盘
        for (int i = 1; i <= x; ++i) {
            for (int j = 1; j <= y; ++j) {
                boardArray[i][j] = Station.zero;
            }
        }
        // 放雷
        putMine();
        // 计算周围雷数
        calculateAroundMine();
    }

    /**
     * 随机放雷
     */
    private void putMine() {
        Random blockCreator = new Random();
        int currentBlockNum = 0;
        while (currentBlockNum < blockNum) {
            int newX = blockCreator.nextInt(x) + 1;
            int newY = blockCreator.nextInt(y) + 1;
            // 如果当前格子是雷，则放雷
            if (boardArray[x][y] != Station.mine) {
                boardArray[newX][newY] = Station.mine;
                currentBlockNum++;
            }
        }
    }

    /**
     * 计算周围雷数
     */
    private void calculateAroundMine() {
        for (int i = 1; i <= x; ++i) {
            for (int j = 1; j <= y; ++j) {
                if (boardArray[i][j] != Station.mine) {
                    int blockAround = 0;
                    if (i - 1 >= 1 && j - 1 >= 1 && boardArray[i - 1][j - 1] == Station.mine) {
                        blockAround++;
                    }
                    if (i - 1 >= 1 && boardArray[i - 1][j] == Station.mine) {
                        blockAround++;
                    }
                    if (i - 1 >= 1 && j + 1 <= y && boardArray[i - 1][j + 1] == Station.mine) {
                        blockAround++;
                    }
                    if (j - 1 >= 1 && boardArray[i][j - 1] == Station.mine) {
                        blockAround++;
                    }
                    if (j + 1 <= y && boardArray[i][j + 1] == Station.mine) {
                        blockAround++;
                    }
                    if (i + 1 <= x && j - 1 >= 1 && boardArray[i + 1][j - 1] == Station.mine) {
                        blockAround++;
                    }
                    if (i + 1 <= x && boardArray[i + 1][j] == Station.mine) {
                        blockAround++;
                    }
                    if (i + 1 <= x && j + 1 <= y && boardArray[i + 1][j + 1] == Station.mine) {
                        blockAround++;
                    }
                    boardArray[i][j] = Station.getStationByValue(blockAround);
                }
            }
        }
    }
}