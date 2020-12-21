package mine_sweeping;

import util.Constant;

import javax.swing.*;
import java.awt.*;

/**
 * JFrame 描述顶层窗口，是极少数几个不绘制在画布上的 Swing 组件之一。
 *
 * @author 10652
 */
public class MineWindow extends JFrame {
    private static MineWindow instance;
    /**
     * 8 * 8 10, 16 * 16 40, 30 * 16 99
     */
    private static final int X = 16;
    private static final int Y = 30;
    private static final int N = 99;

    JButton restartButton;
    JPanel mainWindowPanel;
    JPanel northLayoutPanel;
    MinePanelService minePanel;
    JButton robotButton;

    /**
     * 单例
     *
     * @return 实例
     */
    public static MineWindow getInstance() {
        if (instance == null) {
            instance = new MineWindow();
        }
        return instance;
    }

    /**
     * 设置整个面板显示格式
     */
    private MineWindow() {
        /*
         * 界面风格
         * 1、Metal风格 (默认) String lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
         * 2、Windows风格 String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
         * 3、Windows Classic风格 String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";
         * 4、Motif风格 String lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
         */
        String UI = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
        try {
            UIManager.setLookAndFeel(UI);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // --------------------------
        // 初始化窗口面板（框架内部属性）
        this.readyMainWinPanel();
        // 框架中添加窗口面板
        this.add(mainWindowPanel);

        // --------------------------
        // 设置框架外部属性
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("扫雷");
        // 设置组件位置和大小
        this.setBounds();
        this.setResizable(false);
        // 显示一个组件
        this.setVisible(true);
    }

    /**
     * 初始化窗口面板（所有内容）
     */
    private void readyMainWinPanel() {
        // 窗口面板
        mainWindowPanel = new JPanel();
        mainWindowPanel.setLayout(new BorderLayout());

        // 内容面板：用雷盘数据初始化内容面板
        minePanel = new MinePanelService(X, Y, N);

        // --------------------------
        // 顶部按钮面板（重新开始、机器人）
        northLayoutPanel = new JPanel();
        northLayoutPanel.setLayout(new GridLayout());

        // --------------------------
        // 按钮
        restartButton = new JButton(Constant.RESTART_BUTTON_FLAG);
        robotButton = new JButton(Constant.ROBOT_BUTTON_FLAG);

        restartButton.addActionListener(e -> {
            if (e.getSource() == restartButton) {
                restart();
            }
        });
        robotButton.addActionListener(e -> {
            if (e.getSource() == robotButton) {
                RobotPlayer robot = new RobotPlayer(X, Y);
                // steps = robot.getSteps();
            }
        });
        // 顶部按钮加入按钮面板中
        northLayoutPanel.add(restartButton);
        northLayoutPanel.add(robotButton);

        // --------------------------
        // 窗口面板（子面板“装箱”）
        mainWindowPanel.add(minePanel, BorderLayout.CENTER);
        mainWindowPanel.add(northLayoutPanel, BorderLayout.NORTH);
    }

    /**
     * 设置游戏窗口位置、大小
     */
    private void setBounds() {
        // Toolkit 类包含很多与本地窗口系统打交道的方法
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        // 设置组件（特别是 JFrame）位置和大小。
        this.setBounds(screenWidth / 4, screenHeight / 4, screenWidth / 2, screenHeight / 2);
    }


    /**
     * 扫雷重新开始：
     * 移除绘制的内容面板，再初始化新内容面板，加入窗口面板中，最后刷新整个窗口面板
     */
    public void restart() {
        minePanel.clear();
        mainWindowPanel.remove(minePanel);
        minePanel = new MinePanelService(X, Y, N);
        // minePanel.first(X, Y);
        mainWindowPanel.add(minePanel, BorderLayout.CENTER);
        // 刷新整个面板
        mainWindowPanel.revalidate();
    }
}