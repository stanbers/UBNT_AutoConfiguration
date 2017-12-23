package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * @Author by XuLiang
 * @Date 2017/12/22 17:27
 * @Email stanxu526@gmail.com
 */
public class Test03 {
    private static final long serialVersionUID = 1L;

    private static String[] labelName = { "SSIDName",
            "IP",
            "Netmask",
            "GatewayIP",
    };  //标题名字数组

//    private String[] textValue = {"tp",
//            "xxxx",
//            "1234",
//            "www",
//            "00001",
//            "0123456789",
//            "abc",
//            "123",
//            "20120917090233"
//    };//

//    private static JPanel jPanel;// 实例化一个面板
    private static JLabel jLabel;//实例一个label
    private static JTextField jTextField;//实例一个文本编辑字段
    private static JButton jButton;//实例一个按钮
//    private String[] textValueTemp = textValue;//实例默认值

//    public Test03() {//构造函数
//        super();
//        initFrame();//初始化
//    }

    public static void main(String[] args) {
        //this should be render the JFrame
//        EventQueue.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                Test03 test03 = new Test03();
//                test03.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                test03.setVisible(true);
//                test03.setLocation(100, 100);
//                test03.setResizable(false);
//            }
//        });
        initFrame();
    }

    public static void initFrame() {
        JFrame jf = new JFrame();
        jf.setSize(500, 222);
//        jf.setTitle("Config UBNT_M2/M5 automatically");//设置窗口标题
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setLocation(100, 100);

        GridLayout layout = new GridLayout(2,2);
        JPanel jPanel = new JPanel();
        jPanel.setBorder((BorderFactory.createTitledBorder("UBNT_M2 Configuration")));//设置标题样式
        jPanel.setLayout(layout);//设置布局采用网格的样式  行*列

//        JButton btn01 = new JButton("按钮01");
//        JButton btn02 = new JButton("按钮02");
//
//        jPanel.add(btn01);
//        jPanel.add(btn02);

        jf.setContentPane(jPanel);
        jf.setVisible(true);
        // 动态追加JLabel和JTextField
        for (int i = 0; i < 4; i++) {
            jLabel = new JLabel(labelName[i], SwingConstants.LEFT);//设置Label左对齐
            jLabel.setFont(new Font("ITALIC", 0, 14));

            jTextField = new JTextField("", SwingConstants.RIGHT);
            jTextField.setFont(new Font("Dialog", 0, 12));//根据指定名称、样式和磅值大小，创建一个新 Font。
            // 让动态追加的JLabel和JTextField相互匹配
            jTextField.setName(labelName[i]);

            // 给动态追加的JTextField绑定值变更事件   也就是把改变的值重新绑定下
            jTextField.addFocusListener(new FocusListener() {
                String valChangedBef = "";
                String valChangedAft = "";
                String focusName = "";
                JTextField focusValue;

                @Override
                public void focusLost(FocusEvent e) {
                    focusValue = (JTextField) e.getComponent();
                    valChangedAft = focusValue.getText();
                    if(!valChangedBef.equals(valChangedAft)){
                        focusName = e.getComponent().getName().toString();//获得label的名字
                        for (int j = 0; j < labelName.length; j++) {
                            if (focusName.equals(labelName[j])) {
//                                textValueTemp[j] = valChangedAft;
                            }
                        }
                    }
                }

                @Override
                public void focusGained(FocusEvent e) {
                    focusValue = (JTextField)e.getComponent();
                    valChangedBef = focusValue.getText();
                }
            });

            jPanel.add(jLabel);
            jPanel.add(jTextField);
            //jf.setContentPane(jPanel);
//            jf.add(jPanel, BorderLayout.CENTER);//实例化边界布局
        }

        JPanel panel = new JPanel(); //再实例化一个装按钮的面板
        JButton jButton = new JButton("Send　Data");

        jButton.addActionListener(new ActionListener() {

            @Override
        public void actionPerformed(ActionEvent e) {
                String sendDataStr = "";
                for (int k = 0; k < labelName.length; k++) {
//                    sendDataStr += "'" + labelName[k] + "':'" + textValueTemp[k] + "',";
                }

                sendDataStr = "{" + sendDataStr.substring(0, sendDataStr.length() - 1) + "}";
                // 响应JButton事件，发送所有与JLabel一一对应的JTextField数据
//                sendData(sendDataStr);
        }
    });

//        panel.add(jButton);
//        jf.add(panel, BorderLayout.SOUTH);//把这个面板放到最下面

        //这块是边界布局
//        JPanel panelN = new JPanel();
//        add(panelN, BorderLayout.NORTH);
//        JPanel panelW = new JPanel();
//        add(panelW, BorderLayout.WEST);
//        JPanel panelE = new JPanel();
//        add(panelE, BorderLayout.EAST);

        // 给JPanel追加垂直滚动条
//        addScroll();
    }

//    public void sendData(String sendData) {
//
//        System.out.println("strMsgjson: " + sendData);
//    }

//    public void addScroll() {
//        Container scrollPanel = getContentPane();//获得整个面板内容
//        JScrollPane jScrollPane = new JScrollPane(jPanel);//给这个面板变成有滚动条的面板
//        scrollPanel.add(jScrollPane);//然后把带有滚动条的面板加到这个最终的面板上
//        scrollPanel.setVisible(true);
//    }
}
