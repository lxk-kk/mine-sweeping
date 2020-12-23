package mine_sweeping;

import entity.MineBoard;
import util.Station;

/**
 * 抽象雷盘
 * JPanel 是一个可以包含其他组件的容器，同样也可以在 JPanel 上进行绘制，可以扩展 JPanel
 *
 * @author 10652
 */
public abstract class AbstractMineBoardService extends MineBoard {
    private static final long serialVersionUID = 315256889121532853L;
    protected boolean play;
    protected Station[][] currentArr;
    protected boolean firstStep;
    protected boolean result;

    public boolean getResult() {
        return result;
    }

    /**
     * @param x
     * @param y
     * @param n
     */
    public AbstractMineBoardService(int x, int y, int n) {
        super(x, y, n);
        this.currentArr = new Station[x + 1][y + 1];
        this.play = true;
        this.firstStep = true;

        for (int i = 1; i <= x; ++i) {
            for (int j = 1; j <= y; ++j) {
                this.currentArr[i][j] = Station.unknown;
            }
        }
    }

    /**
     * 遵循 windows xp 扫雷规则
     *
     * @param x
     * @param y
     */
    public void firstStepMine(int x, int y) {
        firstStep = false;
        rePutMine(x, y);
        boardArray[x][y] = Station.unknown;
        calculateAroundMine(false);
        firstStepChangeFace();
    }

    /**
     * 左键
     *
     * @param x
     * @param y
     */
    public void stepByLeftButton(int x, int y) {
        if (firstStep) {
            firstStep = false;
            if (boardArray[x][y] == Station.mine) {
                firstStepMine(x, y);
            }
        }
        // 是否踩雷
        if (boardArray[x][y] != Station.mine) {
            // 没有踩雷：显示标签
            showLabelIcon(x, y);
            if (boardArray[x][y] == Station.zero) {
                // 如果周围无雷，则显示周围无雷区域
                dfs(x, y);
            } else {
                // 记录当前格子
                currentArr[x][y] = boardArray[x][y];
            }
            // 每查看一个格子，都需要检查是否扫雷结束
            check();
        } else {
            // 否则：踩雷，游戏结束！
            // System.out.println("loser");
            result = false;
            this.play = false;
            gameOver();
        }
    }

    /**
     * 右键
     *
     * @param x
     * @param y
     */
    public void stepByRightButton(int x, int y) {
        if (currentArr[x][y] == Station.flag) {
            // 二次点击：恢复成按钮图标！
            currentArr[x][y] = Station.unknown;
            showButtonIcon(x, y);
        } else {
            // 第一次点击：标记小红旗图标！
            currentArr[x][y] = Station.flag;
            showFlagIcon(x, y);
        }
    }

    /**
     * 检查是否扫雷结束
     *
     * @return 查过的格子数
     */
    private int check() {
        int currentVisited = 0;
        int count = 0;
        for (int i = 1; i <= xDim; ++i) {
            for (int j = 1; j <= yDim; ++j) {
                // 当前格子没有访问，且当前格子不是标记！
                if (currentArr[i][j].getValue() >= Station.zero.getValue()) {
                    currentVisited++;
                }
                if (currentArr[i][j] == Station.flag) {
                    count++;
                }
            }
        }
        // System.out.println("check currentVisited : " + currentVisited + " / " + count + " / " + getMineCount());
        if (currentVisited == xDim * yDim - blockNum) {
            // System.out.println("you win");
            result = true;
            this.play = false;
        }
        return currentVisited;
    }

    /**
     * 打开当前格子，并判断当前格子的周围是否无雷！
     * 注意：当前格子一定无雷！！！
     *
     * @param x
     * @param y
     */
    public void dfsStep(int x, int y) {
        if (currentArr[x][y].getValue() >= Station.zero.getValue()) {
            // 减枝：避免相互之间 dfs，还未 dfs 探测过的格子，可能是 unknown、可能被 flag（标记成 雷）
            // 其余情况一定都已经探测过！
            return;
        }
        // 无论当前格子是否被标记，都直接显示，因为当前格子一定无雷！
        currentArr[x][y] = boardArray[x][y];
        showLabelIcon(x, y);
        if (boardArray[x][y] == Station.zero) {
            dfs(x, y);
        }
    }

    /**
     * 仅当 x、y 对应的格子周围无雷，才进入 dfs 方法
     * 打开当前格子的周围
     *
     * @param x
     * @param y
     */
    public void dfs(int x, int y) {
        currentArr[x][y] = boardArray[x][y];
        if (x - 1 >= 1 && y - 1 >= 1) {
            dfsStep(x - 1, y - 1);
        }
        if (x - 1 >= 1) {
            dfsStep(x - 1, y);
        }
        if (x - 1 >= 1 && y + 1 <= this.yDim) {
            dfsStep(x - 1, y + 1);
        }
        if (y - 1 >= 1) {
            dfsStep(x, y - 1);
        }
        if (y + 1 <= this.yDim) {
            dfsStep(x, y + 1);
        }
        if (x + 1 <= this.xDim && y - 1 >= 1) {
            dfsStep(x + 1, y - 1);
        }
        if (x + 1 <= this.xDim) {
            dfsStep(x + 1, y);
        }
        if (x + 1 <= this.xDim && y + 1 <= this.yDim) {
            dfsStep(x + 1, y + 1);
        }
    }

    /**
     * 显示画面：游戏结束
     */
    public void gameOver() {
        showGameOver();
    }

    public boolean isPlaying() {
        return this.play;
    }

    public Station[][] getCurrentArr() {
        return currentArr;
    }

    /**
     * 抽象方法：交由子类修改图形界面
     */
    protected abstract void firstStepChangeFace();

    /**
     *
     */
    public abstract void showButtonIcon(int x, int y);

    /**
     * 显示画面：标签
     */
    public abstract void showLabelIcon(int x, int y);

    /**
     * 显示画面：标记
     */
    public abstract void showFlagIcon(int x, int y);

    /**
     * 显示画面：正确标记
     */
    public abstract void showCorrectFlagIcon(int x, int y);

    /**
     * 显示画面：游戏结束
     */
    public abstract void showGameOver();
}
