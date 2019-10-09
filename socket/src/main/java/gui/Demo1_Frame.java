package gui;

import java.awt.*;
import java.awt.event.*;

public class Demo1_Frame {
    public static void main(String[] args) {
        Frame f = new Frame("我的第一个窗口");
        f.setSize(400, 600);
        f.setLocation(900, 200);

        Button b1 = new Button("按钮1");
        f.add(b1);
        f.setLayout(new FlowLayout());
        f.addWindowListener(new WindowAdapter() {           // 窗体监听
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        b1.addMouseListener(new MouseAdapter() {            // 鼠标监听
            @Override
            public void mouseReleased(MouseEvent e) {
                System.exit(0);
            }
        });
        b1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE){
                    System.exit(0);
                }

            }
        });
        f.setVisible(true);
    }
}

class MyWindowAdaptor extends WindowAdapter{

    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }
}

class  MyWindowListener implements WindowListener{

    public void windowOpened(WindowEvent e) {

    }

    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    public void windowClosed(WindowEvent e) {

    }

    public void windowIconified(WindowEvent e) {

    }

    public void windowDeiconified(WindowEvent e) {

    }

    public void windowActivated(WindowEvent e) {

    }

    public void windowDeactivated(WindowEvent e) {

    }
}
