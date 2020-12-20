package mine_sweeping;

import java.awt.*;

/**
 * @author 10652
 */
public class MineSweepingBootstrap {
    /**
     * 所有的 Swing 组件必须由“事件分派线程”进行配置，线程将鼠标和按键控制转移到用户接口组件
     * Swing 程序也可以在主线程中初始化用户界面，但无法保证安全性（低概率）
     *
     * @param args
     */
    public static void main(String[] args) {
        // 事件分派线程中初始化用户界面
        EventQueue.invokeLater(MineWindow::getInstance);

        // 主线程中直接初始化用户界面
        // MineWindow HB = MineWindow.getInstance();
    }
}