package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @Author by XuLiang
 * @Date 2017/12/22 14:03
 * @Email stanxu526@gmail.com
 */
public class Test02 extends JFrame {

    private static final long serialVersionUID = 1L;

    private JPanel jPanel;// 实例化一个面板
    private JLabel jLabel;//实例一个label
    private JButton jButton;//实例一个按钮
//    private String[] textValueTemp = textValue;//实例默认值

    public Test02() {//构造函数
        super();
        initFrame();//初始化
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Test02 test02 = new Test02();
                test02.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                test02.setVisible(true);
                test02.setLocation(100, 100);
                test02.setResizable(false);
            }
        });
    }

    public void initFrame() {
        setTitle("The demo of the iphone send data");//设置窗口标题
        setSize(350, 222);

        jPanel = new JPanel();
        jPanel.setBorder((BorderFactory.createTitledBorder("Send type struct by key : value")));//设置标题样式
        jPanel.setLayout(new GridLayout(10, 2));//设置布局采用网格的样式  行*列

        JPanel panel = new JPanel(); //再实例化一个装按钮的面板
        jButton = new JButton("UBNT_M2_Configuration");

        jButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
//                M2_Configuration.configM2();
            }
        });

        panel.add(jButton);
        add(panel, BorderLayout.CENTER);//把这个面板放到最下面

        //这块是边界布局
        JPanel panelN = new JPanel();
        add(panelN, BorderLayout.NORTH);
        JPanel panelW = new JPanel();
        add(panelW, BorderLayout.WEST);
        JPanel panelE = new JPanel();
        add(panelE, BorderLayout.EAST);

        // 给JPanel追加垂直滚动条
//        addScroll();
    }

//    public void addScroll() {
//        Container scrollPanel = getContentPane();//获得整个面板内容
//        JScrollPane jScrollPane = new JScrollPane(jPanel);//给这个面板变成有滚动条的面板
//        scrollPanel.add(jScrollPane);//然后把带有滚动条的面板加到这个最终的面板上
//        scrollPanel.setVisible(true);
//    }



}
