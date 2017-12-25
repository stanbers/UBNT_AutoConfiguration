package gui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @Author by XuLiang
 * @Date 2017/12/22 19:14
 * @Email stanxu526@gmail.com
 */
public class UBNT_GUI_03 {
    private final static Log log = LogFactory.getLog(UBNT_GUI_03.class);
    private static String[] labelName = {"SSID name :","IP Address :","Netmask :","Gateway IP :","Frequecy :","Mac address: "};

//    private static JButton jButton = new JButton("M2");
    public static void main(String[] args) {
        run();
    }


    public static void run(){
        JFrame jf = new JFrame("Config UBNT automatically");
        jf.setSize(400, 500);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();

        JButton jButton1 = new JButton("M2");
        //setup button
        jButton1.setFont(new Font(null, Font.BOLD,14));
        jButton1.setLocation(10,10);
        jButton1.setSize(105,30);

        jPanel.add(jButton1);

//        JButton jButton2 = new JButton("M5_AP");
//        //setup button
//        jButton2.setFont(new Font(null, Font.BOLD,14));
//        jButton2.setLocation(115,10);
//        jButton2.setSize(105,30);
//        jPanel.add(jButton2);


        jf.setContentPane(jPanel);
        jf.setVisible(true);

    }


}
