package mine_sweeping;

import entity.MineBoard;

/**
 * 测试胜率
 *
 * @author 10652
 */
public class TestRobot extends MineBoardService {
    private static final int X = 16;
    private static final int Y = 30;
    private static final int N = 99;


    TestRobot() {
        super(X, Y, N);
    }

    public static void main(String[] args) {
    }

    @Override
    void showButtonIcon(int x, int y) {
    }

    @Override
    void showLabelIcon(int x, int y) {

    }

    @Override
    void showFlagIcon(int x, int y) {

    }

    @Override
    void showCorrectFlagIcon(int x, int y) {

    }

    @Override
    void showGameOver() {
    }


    /**
     * 扫雷重新开始：
     * 移除绘制的内容面板，再初始化新内容面板，加入窗口面板中，最后刷新整个窗口面板
     */
    public void restart() {

    }
}
