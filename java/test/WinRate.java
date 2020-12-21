package test;

import util.Constant;

import java.util.Scanner;

/**
 * @author 10652
 */
public class WinRate {
    static int step;

    public static void main(String[] args) {
        System.out.println("测试次数：");
        Scanner in = new Scanner(System.in);
        step = in.nextInt();
        int count = 0;
        for (int i = 0; i < step; i++) {
            if (robotWin()) {
                count++;
            }
        }
        double rate = count * 1.0 / step;
        System.out.println("胜率：" + rate);
    }

    private static boolean robotWin() {
        RobotTester tester = new RobotTester(Constant.X_DIMENSION, Constant.Y_DIMENSION);
        tester.robotPlay();
        return tester.getResult();
    }
}
