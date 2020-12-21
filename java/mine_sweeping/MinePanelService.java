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
public class MinePanelService extends AbstractMineBoardService {
    private static final long serialVersionUID = 299452544351890642L;
    private final JPanel[][] panelArr;
    private final JLabel[][] labelArr;
    private final JButton[][] buttonArr;
    private final JLabel[][] flagArr;
    private final JLabel[][] correctFlagArr;


    public MinePanelService(int x, int y, int n) {
        super(x, y, n);
        this.setLayout(new GridLayout(xDim, yDim, 0, 0));
        // 雷盘
        panelArr = new JPanel[xDim + 1][yDim + 1];
        // 按钮
        buttonArr = new JButton[xDim + 1][yDim + 1];
        // 标签
        labelArr = new JLabel[xDim + 1][yDim + 1];
        // 标记
        flagArr = new JLabel[xDim + 1][yDim + 1];
        // 正确标记
        correctFlagArr = new JLabel[xDim + 1][yDim + 1];
        createMinePanel();
    }

    public void createMinePanel() {
        for (int x = 1; x <= xDim; ++x) {
            for (int y = 1; y <= yDim; ++y) {
                // 创建格子面板
                panelArr[x][y] = new JPanel();
                // 设置格子面板的布局为：卡片布局
                panelArr[x][y].setLayout(new CardLayout());
                // 按钮设置：可点击
                createMineButton(x, y);
                // 标签设置：仅显示，不可点击
                createMineLabel(x, y);
                // 标记设置：可点击
                createMineFlag(x, y);
                // 正确标记设置
                createMineCorFlag(x, y);
                // 将按钮、标签、标记、正确标记 等组件添加到格子面板中
                this.add(panelArr[x][y]);
            }
        }
    }

    private void createMineButton(int x, int y) {
        // 按钮设置：可点击
        buttonArr[x][y] = new JButton();
        // 设置按钮边框：具有凸出斜面边缘的边框！（视觉效果）
        buttonArr[x][y].setBorder(BorderFactory.createRaisedBevelBorder());
        // 为按钮添加监听器
        buttonArr[x][y].addMouseListener((MouseListenerFactory) e -> {
            for (int idx = 1; idx <= xDim; ++idx) {
                for (int idy = 1; idy <= yDim; ++idy) {
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
        panelArr[x][y].add(buttonArr[x][y], Constant.BUTTON);
    }

    private void createMineLabel(int x, int y) {
        labelArr[x][y] = new JLabel();
        // 设置标签边框：具有凹入斜面边缘的边框
        labelArr[x][y].setBorder(BorderFactory.createLoweredBevelBorder());
        labelArr[x][y].setFont(new java.awt.Font("微软雅黑", 1, 14));
        // 雷格子：标签显示地雷图标
        if (boardArray[x][y] == Station.mine) {
            labelArr[x][y].setIcon(getImage(Constant.MINE_ICON));
        } else if (boardArray[x][y] == Station.zero) {
            // 周围无雷：不显示
            labelArr[x][y].setText(Station.zero.getText());
        } else {
            // 周围有雷：显示周围雷数（设置背景、设置数量）
            labelArr[x][y].setText(boardArray[x][y].getText());
            switch (boardArray[x][y]) {
                default:
                    System.out.println("Error");
                    break;
                case one:
                    labelArr[x][y].setForeground(Color.BLUE);
                    break;
                case two:
                    labelArr[x][y].setForeground(Color.ORANGE);
                    break;
                case three:
                    labelArr[x][y].setForeground(Color.RED);
                    break;
                case four:
                    labelArr[x][y].setForeground(Color.cyan);
                    break;
                case five:
                    labelArr[x][y].setForeground(Color.BLACK);
                    break;
                case six:
                    labelArr[x][y].setForeground(Color.MAGENTA);
                    break;
                case seven:
                    labelArr[x][y].setForeground(Color.green);
                    break;
                case eight:
                    labelArr[x][y].setForeground(Color.darkGray);
                    break;
            }
        }
        panelArr[x][y].add(labelArr[x][y], Constant.LABEL);
    }

    private void createMineFlag(int x, int y) {
        flagArr[x][y] = new JLabel();
        flagArr[x][y].setBorder(BorderFactory.createLoweredBevelBorder());
        flagArr[x][y].setIcon(getImage(Constant.FLAG_ICON));
        flagArr[x][y].addMouseListener((MouseListenerFactory) e -> {
            for (int idx = 1; idx <= xDim; ++idx) {
                for (int idy = 1; idy <= yDim; ++idy) {
                    if (e.getSource() == flagArr[idx][idy]) {
                        // 鼠标右键
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            solveRightButtonEvents(idx, idy);
                        }
                    }
                }
            }
        });
        panelArr[x][y].add(flagArr[x][y], Constant.FLAG);
    }

    private void createMineCorFlag(int x, int y) {
        correctFlagArr[x][y] = new JLabel();
        correctFlagArr[x][y].setBorder(BorderFactory.createLoweredBevelBorder());
        correctFlagArr[x][y].setIcon(getImage(Constant.FLAG_TWO_ICON));
        panelArr[x][y].add(correctFlagArr[x][y], Constant.CORRECT_FLAG);
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

    @Override
    public void firstStepChangeFace() {
        for (int x = 1; x <= xDim; x++) {
            for (int y = 1; y <= yDim; y++) {
                if (boardArray[x][y].getValue() != boardArrayPre[x][y].getValue()) {
                    flushMineLabel(x, y);
                }
            }
        }
    }

    /**
     * 刷新面板：仅仅刷新面板中的标签即可！
     */
    public void flushMineLabel(int x, int y) {
        panelArr[x][y].remove(labelArr[x][y]);
        createMineLabel(x, y);
    }

    @Override
    public void showButtonIcon(int x, int y) {
        CardLayout layout = (CardLayout) panelArr[x][y].getLayout();
        layout.show(panelArr[x][y], Constant.BUTTON);
    }

    @Override
    public void showLabelIcon(int x, int y) {
        CardLayout layout = (CardLayout) panelArr[x][y].getLayout();
        layout.show(panelArr[x][y], Constant.LABEL);
    }

    @Override
    public void showFlagIcon(int x, int y) {
        CardLayout layout = (CardLayout) panelArr[x][y].getLayout();
        layout.show(panelArr[x][y], Constant.FLAG);
    }

    @Override
    public void showCorrectFlagIcon(int x, int y) {
        CardLayout layout = (CardLayout) panelArr[x][y].getLayout();
        layout.show(panelArr[x][y], Constant.CORRECT_FLAG);
    }

    @Override
    public void showGameOver() {
        // 雷盘摊牌：显示雷区（注意：标记正确的区域）
        for (int x = 1; x <= xDim; ++x) {
            for (int y = 1; y <= yDim; ++y) {
                // 标记成功：显示标记结果！
                if (boardArray[x][y] == Station.mine && currentArr[x][y] == Station.mine) {
                    showCorrectFlagIcon(x, y);
                } else if (boardArray[x][y] == Station.mine && currentArr[x][y] != Station.mine) {
                    showLabelIcon(x, y);
                }
                buttonArr[x][y].setEnabled(false);
            }
        }
    }
}