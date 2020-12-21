package mine_sweeping;

import entity.MineBoard;
import util.Station;

/**
 * 抽象雷盘
 * JPanel 是一个可以包含其他组件的容器，同样也可以在 JPanel 上进行绘制，可以扩展 JPanel
 *
 * @author 10652
 */
public abstract class MineBoardService extends MineBoard {
    private static final long serialVersionUID = 315256889121532853L;
    int blockNum;
    boolean play;
    Station[][] currentArr;
    static boolean result;

    /**
     * @param x
     * @param y
     * @param n
     */
    MineBoardService(int x, int y, int n) {
        super(x, y, n);
        this.currentArr = new Station[x + 1][y + 1];
        this.play = true;

        for (int i = 1; i <= x; ++i) {
            for (int j = 1; j <= y; ++j) {
                this.currentArr[i][j] = Station.unknown;
            }
        }
    }

    /**
     * 左键
     *
     * @param x
     * @param y
     */
    public void stepByLeftButton(int x, int y) {
        // 是否踩雷
        if (boardArray[x][y] != Station.mine) {
            // 没有踩雷：显示标签
            showLabelIcon(x, y);
            if (boardArray[x][y] == Station.zero) {
                // 如果周围无雷，则显示周围无雷区域
                dfs(x, y);
            } else {
                // 标记
                currentArr[x][y] = boardArray[x][y];
            }
            // 每查看一个格子，都需要检查是否扫雷结束
            check();
        } else {
            System.out.println("loser");
            result = false;
            gameOver();
        }
    }

    /**
     * 检查是否扫雷结束
     *
     * @return 查过的格子数
     */
    private int check() {
        int currentVisited = 0;
        for (int i = 1; i <= x; ++i) {
            for (int j = 1; j <= y; ++j) {
                if (currentArr[i][j] != Station.unknown && currentArr[i][j] != Station.mine) {
                    currentVisited++;
                }
            }
        }
        if (currentVisited == x * y - blockNum) {
            System.out.println("you win");
            result = true;
            this.play = false;
        }
        return currentVisited;
    }

    public void dfsStep(int x, int y) {
        if (currentArr[x][y] != Station.unknown) {
            return;
        }
        showLabelIcon(x, y);
        if (boardArray[x][y] == Station.zero) {
            dfs(x, y);
        } else {
            currentArr[x][y] = boardArray[x][y];
        }
    }

    public void dfs(int x, int y) {
        currentArr[x][y] = boardArray[x][y];
        if (x - 1 >= 1 && y - 1 >= 1) {
            dfsStep(x - 1, y - 1);
        }
        if (x - 1 >= 1) {
            dfsStep(x - 1, y);
        }
        if (x - 1 >= 1 && y + 1 <= this.y) {
            dfsStep(x - 1, y + 1);
        }
        if (y - 1 >= 1) {
            dfsStep(x, y - 1);
        }
        if (y + 1 <= this.y) {
            dfsStep(x, y + 1);
        }
        if (x + 1 <= this.x && y - 1 >= 1) {
            dfsStep(x + 1, y - 1);
        }
        if (x + 1 <= this.x) {
            dfsStep(x + 1, y);
        }
        if (x + 1 <= this.x && y + 1 <= this.y) {
            dfsStep(x + 1, y + 1);
        }
    }

    /**
     * 右键
     *
     * @param x
     * @param y
     */
    public void stepByRightButton(int x, int y) {
        if (currentArr[x][y] == Station.mine) {
            // 二次点击：恢复成按钮图标！
            currentArr[x][y] = Station.unknown;
            showButtonIcon(x, y);
        } else {
            // 第一次点击：标记小红旗图标！
            currentArr[x][y] = Station.mine;
            showFlagIcon(x, y);
        }
    }

    /**
     * 显示画面：游戏结束
     */
    public void gameOver() {
        this.play = false;
        showGameOver();
    }

    public boolean isPlaying() {
        return this.play;
    }

    public Station[][] getCurrentArr() {
        return currentArr;
    }

    /**
     * 显示画面：按钮
     */
    abstract void showButtonIcon(int x, int y);

    /**
     * 显示画面：标签
     */
    abstract void showLabelIcon(int x, int y);

    /**
     * 显示画面：标记
     */
    abstract void showFlagIcon(int x, int y);

    /**
     * 显示画面：正确标记
     */
    abstract void showCorrectFlagIcon(int x, int y);

    /**
     * 显示画面：游戏结束
     */
    abstract void showGameOver();
}
