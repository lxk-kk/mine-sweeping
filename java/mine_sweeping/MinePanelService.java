package mine_sweeping;

import util.Constant;
import util.MouseListenerFactory;
import util.Station;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author 10652
 */
public class MinePanelService extends MineBoardService {
    private static final long serialVersionUID = 299452544351890642L;
    private final JPanel[][] panelArr;
    private final JLabel[][] labelArr;
    private final JButton[][] buttonArr;
    private final JLabel[][] flagArr;
    private final JLabel[][] correctFlagArr;


    public MinePanelService(int X, int Y, int N) {
        super(X, Y, N);
        this.setLayout(new GridLayout(X, Y, 0, 0));
        // 雷盘
        panelArr = new JPanel[X + 1][Y + 1];
        // 按钮
        buttonArr = new JButton[X + 1][Y + 1];
        // 标签
        labelArr = new JLabel[X + 1][Y + 1];
        // 标记
        flagArr = new JLabel[X + 1][Y + 1];
        // 正确标记
        correctFlagArr = new JLabel[X + 1][Y + 1];

        for (int i = 1; i <= X; ++i) {
            for (int j = 1; j <= Y; ++j) {
                // 创建格子面板
                panelArr[i][j] = new JPanel();
                // 设置格子面板的布局为：卡片布局
                panelArr[i][j].setLayout(new CardLayout());

                // -----------------------------
                // 按钮设置：可点击
                buttonArr[i][j] = new JButton();
                // 设置按钮边框：具有凸出斜面边缘的边框！（视觉效果）
                buttonArr[i][j].setBorder(BorderFactory.createRaisedBevelBorder());
                // 为按钮添加监听器
                buttonArr[i][j].addMouseListener((MouseListenerFactory) e -> {
                    // System.out.println("wight = " + buttonArr[1][1].getWidth() + "; height = " + buttonArr[1][1]
                    // .getHeight());
                    for (int idx = 1; idx <= X; ++idx) {
                        for (int idy = 1; idy <= Y; ++idy) {
                            if (e.getSource() == buttonArr[idx][idy]) {
                                // 左键事件
                                if (e.getButton() == MouseEvent.BUTTON1) {
                                    solveLeftButtonEvents(idx, idy);
                                }
                                // 右键事件
                                if (e.getButton() == MouseEvent.BUTTON3) {
                                    solveRightButtonEvents(idx, idy);
                                }
                            }
                        }
                    }
                });

                // -----------------------------
                // 标签设置：仅显示，不可点击
                labelArr[i][j] = new JLabel();
                // 设置标签边框：具有凹入斜面边缘的边框
                labelArr[i][j].setBorder(BorderFactory.createLoweredBevelBorder());
                labelArr[i][j].setFont(new java.awt.Font("微软雅黑", 1, 14));
                // 雷格子：标签显示地雷图标
                if (boardArray[i][j] == Station.mine) {
                    labelArr[i][j].setIcon(getImage(Constant.MINE_ICON));
                } else if (boardArray[i][j] == Station.zero) {
                    // 周围无雷：不显示
                    labelArr[i][j].setText(Station.zero.getText());
                } else {
                    // 周围有雷：显示周围雷数（设置背景、设置数量）
                    labelArr[i][j].setText(boardArray[i][j].getText());
                    switch (boardArray[i][j]) {
                        default:
                            // todo 弹窗显示系统错误：逻辑上不会有问题
                            System.out.println("Error");
                            break;
                        case one:
                            labelArr[i][j].setForeground(Color.BLUE);
                            break;
                        case two:
                            labelArr[i][j].setForeground(Color.ORANGE);
                            break;
                        case three:
                            labelArr[i][j].setForeground(Color.RED);
                            break;
                        case four:
                            labelArr[i][j].setForeground(Color.cyan);
                            break;
                        case five:
                            labelArr[i][j].setForeground(Color.BLACK);
                            break;
                        case six:
                            labelArr[i][j].setForeground(Color.MAGENTA);
                            break;
                        case seven:
                            labelArr[i][j].setForeground(Color.green);
                            break;
                        case eight:
                            labelArr[i][j].setForeground(Color.darkGray);
                            break;
                    }
                }

                // -----------------------------
                // 标记设置：可点击
                flagArr[i][j] = new JLabel();
                flagArr[i][j].setBorder(BorderFactory.createLoweredBevelBorder());
                flagArr[i][j].setIcon(getImage(Constant.FLAG_ICON));
                flagArr[i][j].addMouseListener((MouseListenerFactory) e -> {
                    for (int idx = 1; idx <= X; ++idx) {
                        for (int idy = 1; idy <= Y; ++idy) {
                            if (e.getSource() == flagArr[idx][idy]) {
                                // 鼠标右键
                                if (e.getButton() == MouseEvent.BUTTON3) {
                                    solveRightButtonEvents(idx, idy);
                                }
                            }
                        }
                    }
                });


                // -----------------------------
                // 正确标记设置
                correctFlagArr[i][j] = new JLabel();
                correctFlagArr[i][j].setBorder(BorderFactory.createLoweredBevelBorder());
                correctFlagArr[i][j].setIcon(getImage(Constant.FLAG_TWO_ICON));

                // -----------------------------
                // 将按钮、标签、标记、正确标记 等组件添加到格子面板中
                panelArr[i][j].add(buttonArr[i][j], Constant.BUTTON);

                panelArr[i][j].add(labelArr[i][j], Constant.LABEL);
                panelArr[i][j].add(flagArr[i][j], Constant.FLAG);
                panelArr[i][j].add(correctFlagArr[i][j], Constant.CORRECT_FLAG);
                this.add(panelArr[i][j]);
            }
        }
    }

    /**
     * 图标缩放
     *
     * @param file 图标文件路径
     * @return image icon
     */
    private ImageIcon getImage(String file) {
        ImageIcon img = new ImageIcon(file);
        img.setImage(img.getImage().getScaledInstance(Constant.WIDTH, Constant.HEIGHT, Image.SCALE_DEFAULT));
        return img;
    }

    /**
     * 左键事件：走一步！
     *
     * @param x
     * @param y
     */
    public void solveLeftButtonEvents(int x, int y) {
        stepByLeftButton(x, y);
    }

    /**
     * 右键事件：标记！
     *
     * @param x
     * @param y
     */
    public void solveRightButtonEvents(int x, int y) {
        stepByRightButton(x, y);
    }

    /**
     * 显示 Button
     *
     * @param X
     * @param Y
     */
    public void first(int X, int Y) {
        for (int i = 1; i <= X; ++i) {
            for (int j = 1; j <= Y; ++j) {
                showButtonIcon(i, j);
            }
        }
    }

    /**
     * 清理面板
     */
    public void clear() {
        for (int i = 1; i <= x; ++i) {
            for (int j = 1; j <= y; ++j) {
                panelArr[i][j].remove(buttonArr[i][j]);
                panelArr[i][j].remove(flagArr[i][j]);
                panelArr[i][j].remove(labelArr[i][j]);
                this.remove(panelArr[i][j]);
            }
        }
    }

    @Override
    void showButtonIcon(int x, int y) {
        CardLayout layout = (CardLayout) panelArr[x][y].getLayout();
        layout.show(panelArr[x][y], Constant.BUTTON);
    }

    @Override
    void showLabelIcon(int x, int y) {
        CardLayout layout = (CardLayout) panelArr[x][y].getLayout();
        layout.show(panelArr[x][y], Constant.LABEL);
    }

    @Override
    void showFlagIcon(int x, int y) {
        CardLayout layout = (CardLayout) panelArr[x][y].getLayout();
        layout.show(panelArr[x][y], Constant.FLAG);
    }

    @Override
    void showCorrectFlagIcon(int x, int y) {
        CardLayout layout = (CardLayout) panelArr[x][y].getLayout();
        layout.show(panelArr[x][y], Constant.CORRECT_FLAG);
    }

    @Override
    void showGameOver() {
        // 雷盘摊牌：显示雷区（注意：标记正确的区域）
        for (int i = 1; i <= x; ++i) {
            for (int j = 1; j <= y; ++j) {
                // 标记成功：显示标记结果！
                if (boardArray[i][j] == Station.mine && currentArr[i][j] == Station.mine) {
                    showCorrectFlagIcon(i, j);
                } else if (boardArray[i][j] == Station.mine && currentArr[i][j] != Station.mine) {
                    showLabelIcon(i, j);
                }
                buttonArr[i][j].setEnabled(false);
            }
        }
    }
}