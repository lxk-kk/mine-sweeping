package test;

import mine_sweeping.AbstractMineBoardService;
import util.Constant;

/**
 * 测试胜率
 *
 * @author 10652
 */
public class MineBoardTester extends AbstractMineBoardService {
    private static final long serialVersionUID = 2601421245255819320L;

    MineBoardTester() {
        super(Constant.X_DIMENSION, Constant.Y_DIMENSION, Constant.MINE_TOTAL);
    }

    public boolean isFirstStep() {
        return firstStep;
    }

    @Override
    public void firstStepChangeFace() {
    }

    @Override
    public void showButtonIcon(int x, int y) {
    }

    @Override
    public void showLabelIcon(int x, int y) {

    }

    @Override
    public void showFlagIcon(int x, int y) {

    }

    @Override
    public void showCorrectFlagIcon(int x, int y) {

    }

    @Override
    public void showGameOver() {
    }
}
