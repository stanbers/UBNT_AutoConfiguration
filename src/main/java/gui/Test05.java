package gui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @Author by XuLiang
 * @Date 2017/12/22 19:14
 * @Email stanxu526@gmail.com
 */
public class Test05 {
    private final static Log log = LogFactory.getLog(Test05.class);
    private static String[] labelName = {"SSID name :","IP Address :","Netmask :","Gateway IP :"};
    public static void main(String[] args) {

        JFrame jf = new JFrame("Config UBNT automatically");
        jf.setSize(400, 500);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        jPanel.setBorder((BorderFactory.createTitledBorder("UBNT_M2 Configuration")));
        jPanel.setLayout(null);

        for (int i = 1; i <= labelName.length; i++) {
            //setup labels
            JLabel labels = new JLabel(labelName[i-1],SwingConstants.LEFT);
            labels.setFont(new Font("ITALIC", 1, 16));
            labels.setLocation(10,40*i);
            labels.setSize(100,30);
            jPanel.add(labels);

            //setup input boxes
            JTextField inputBoxes = new JTextField(SwingConstants.RIGHT);
            inputBoxes.setFont(new Font(null, Font.PLAIN, 14));
            inputBoxes.setLocation(120,40*i);
            inputBoxes.setSize(200,30);
            jPanel.add(inputBoxes);
        }

        //setup button
        JButton jButton = new JButton("UBNT_M2");
        jButton.setFont(new Font(null,Font.BOLD,14));
        jButton.setLocation(145,300);
        jButton.setSize(105,30);

        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                String IPAddress = ssidName_input.getText();
//                String
//                log.info(IPAddress);
//                M2_Configuration.configM2(IPAddress);
            }
        });


        jPanel.add(jButton);


        jf.setContentPane(jPanel);
        jf.setVisible(true);

















    }
}
