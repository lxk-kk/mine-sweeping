package util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * 以函数式编程的方式实现 鼠标监听器！
 *
 * @author 10652
 */
@FunctionalInterface
public interface MouseListenerFactory extends MouseListener {
    @Override
    default void mousePressed(MouseEvent e) {

    }

    @Override
    default void mouseReleased(MouseEvent e) {

    }

    @Override
    default void mouseEntered(MouseEvent e) {

    }

    @Override
    default void mouseExited(MouseEvent e) {

    }
}
