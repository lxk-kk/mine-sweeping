package test;

import mine_sweeping.RobotPlayer;

/**
 * @author 10652
 */
public class RobotTester extends RobotPlayer {
    MineBoardTester tester;

    public RobotTester(int xDim, int yDim) {
        super(xDim, yDim);
        tester = new MineBoardTester();
    }

    public boolean getResult() {
        return tester.getResult();
    }

    @Override
    public void leftButtonEvent(int x, int y) {
        tester.stepByLeftButton(x, y);
    }

    @Override
    public void rightButtonEvent(int x, int y) {
        tester.stepByRightButton(x, y);
    }

    @Override
    public boolean flushPlayState() {
        return tester.isPlaying();
    }

    @Override
    public void flushCurrentState() {
        currentArray = tester.getCurrentArr();
    }
}
