package mine_sweeping;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * 内容面板
 * JPanel 是一个可以包含其他组件的容器，同样也可以在 JPanel 上进行绘制，可以扩展 JPanel
 *
 * @author 10652
 */
public class MinePanel extends JPanel {
    private static final long serialVersionUID = 299452544351890642L;
    private JPanel[][] panelArr;
    private JLabel[][] labelArr;
    private JButton[][] buttonArr;
    private JLabel[][] flagArr;
    private JLabel[][] correctFlagArr;
    private int X;
    private int Y;
    private int blockNum;
    private boolean play;
    private Station[][] boardArray;
    private Station[][] currentArr;
    private static boolean result;

    public MinePanel(Station[][] boardArray, int X, int Y, int N) {
        this.X = X;
        this.Y = Y;
        this.boardArray = boardArray;
        this.blockNum = N;
        this.currentArr = new Station[X + 1][Y + 1];
        this.play = true;

        for (int i = 1; i <= X; ++i) {
            for (int j = 1; j <= Y; ++j) {
                this.currentArr[i][j] = Station.unknown;
            }
        }
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
                    labelArr[i][j].setIcon(getImage(Constant.MINE_ICON, Constant.WIDTH, Constant.HEIGHT));
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
                // ImageIcon img = new ImageIcon(Constant.FLAG_ICON);
                flagArr[i][j].setIcon(getImage(Constant.FLAG_ICON, Constant.WIDTH, Constant.HEIGHT));
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
                correctFlagArr[i][j].setIcon(getImage(Constant.FLAG_TWO_ICON, Constant.WIDTH, Constant.HEIGHT));

                // -----------------------------
                // 将按钮、标签、标记、正确标记 等组件添加到格子面板中
                // todo 常量替换
                panelArr[i][j].add(buttonArr[i][j], Constant.BUTTON);

                panelArr[i][j].add(labelArr[i][j], Constant.MINE);
                panelArr[i][j].add(flagArr[i][j], Constant.FLAG);
                panelArr[i][j].add(correctFlagArr[i][j], Constant.CORRECT_FLAG);
                this.add(panelArr[i][j]);
            }
        }
    }

    private ImageIcon getImage(String file, int width, int height) {
        ImageIcon img = new ImageIcon(file);
        img.setImage(img.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
        return img;
    }

    /**
     * 左键事件：todo card？
     *
     * @param x
     * @param y
     */
    public void solveLeftButtonEvents(int x, int y) {
        // 是否踩雷
        if (boardArray[x][y] == Station.mine) {
            System.out.println("you lose");
            result = false;
            gameOver();
            return;
        }
        CardLayout cardLayout = (CardLayout) panelArr[x][y].getLayout();
        cardLayout.show(panelArr[x][y], Constant.MINE);
        if (boardArray[x][y] == Station.zero) {
            // 如果周围无雷，则显示周围无雷区域
            dfsForAutoClick(x, y);
        } else {
            // 标记
            currentArr[x][y] = boardArray[x][y];
        }
        // 每查看一个格子，都需要检查是否扫雷结束
        check();
    }

    /**
     * 检查是否扫雷结束
     *
     * @return 查过的格子数
     */
    public int check() {
        int currentVisited = 0;
        for (int i = 1; i <= X; ++i) {
            for (int j = 1; j <= Y; ++j) {
                if (currentArr[i][j] != Station.unknown && currentArr[i][j] != Station.mine) {
                    currentVisited++;
                }
            }
        }
        if (currentVisited == X * Y - blockNum) {
            // todo
            System.out.println("you win");
            result = true;
            this.play = false;
        }
        return currentVisited;
    }

    public void solveDFSpoint(int x, int y) {
        if (currentArr[x][y] != Station.unknown) {
            return;
        }
        CardLayout layout = (CardLayout) panelArr[x][y].getLayout();
        layout.show(panelArr[x][y], Constant.MINE);
        if (boardArray[x][y] == Station.zero) {
            dfsForAutoClick(x, y);
        } else {
            currentArr[x][y] = boardArray[x][y];
        }
    }

    public void dfsForAutoClick(int x, int y) {
        currentArr[x][y] = boardArray[x][y];
        if (x - 1 >= 1 && y - 1 >= 1) {
            solveDFSpoint(x - 1, y - 1);
        }
        if (x - 1 >= 1) {
            solveDFSpoint(x - 1, y);
        }
        if (x - 1 >= 1 && y + 1 <= Y) {
            solveDFSpoint(x - 1, y + 1);
        }
        if (y - 1 >= 1) {
            solveDFSpoint(x, y - 1);
        }
        if (y + 1 <= Y) {
            solveDFSpoint(x, y + 1);
        }
        if (x + 1 <= X && y - 1 >= 1) {
            solveDFSpoint(x + 1, y - 1);
        }
        if (x + 1 <= X) {
            solveDFSpoint(x + 1, y);
        }
        if (x + 1 <= X && y + 1 <= Y) {
            solveDFSpoint(x + 1, y + 1);
        }
    }

    /**
     * 右键事件：标记！
     *
     * @param x
     * @param y
     */
    public void solveRightButtonEvents(int x, int y) {
        CardLayout layout = (CardLayout) panelArr[x][y].getLayout();
        if (currentArr[x][y] == Station.mine) {
            // 二次点击（恢复成 BUTTON）
            currentArr[x][y] = Station.unknown;
            layout.show(panelArr[x][y], Constant.BUTTON);
        } else {
            // 第一次点击（显示小红旗）
            currentArr[x][y] = Station.mine;
            layout.show(panelArr[x][y], Constant.FLAG);
        }
    }

    /**
     * 游戏结束
     */
    public void gameOver() {
        this.play = false;
        // 雷盘摊牌：显示雷区（注意：标记正确的区域）
        for (int i = 1; i <= X; ++i) {
            for (int j = 1; j <= Y; ++j) {
                CardLayout layout = (CardLayout) panelArr[i][j].getLayout();
                // 标记成功：显示标记结果！
                if (boardArray[i][j] == Station.mine && currentArr[i][j] == Station.mine) {
                    layout.show(panelArr[i][j], Constant.CORRECT_FLAG);
                } else if (boardArray[i][j] == Station.mine && currentArr[i][j] != Station.mine) {
                    layout.show(panelArr[i][j], Constant.MINE);
                }
                buttonArr[i][j].setEnabled(false);
            }
        }
    }

    public boolean isPlaying() {
        return this.play;
    }

    public Station[][] getCurrentArr() {
        return currentArr;
    }

    public void setcurrentArray(int x, int y, Station cur) {
        currentArr[x][y] = cur;
    }

    public void first(int X, int Y) {
        for (int i = 1; i <= X; ++i) {
            for (int j = 1; j <= Y; ++j) {
                CardLayout clayout = (CardLayout) panelArr[i][j].getLayout();
                clayout.first(panelArr[i][j]);
            }
        }
    }

    public boolean getResult() {
        return result;
    }

    public void clear() {
        for (int i = 1; i <= X; ++i) {
            for (int j = 1; j <= Y; ++j) {
                panelArr[i][j].remove(buttonArr[i][j]);
                panelArr[i][j].remove(flagArr[i][j]);
                panelArr[i][j].remove(labelArr[i][j]);
                this.remove(panelArr[i][j]);
            }
        }
    }
}