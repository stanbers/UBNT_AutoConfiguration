package gui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ubnt.m2.M2_Configuration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author by XuLiang
 * @Date 2017/12/22 19:14
 * @Email stanxu526@gmail.com
 */
public class UBNT_GUI {
    private final static Log log = LogFactory.getLog(UBNT_GUI.class);
    private static String[] labelName = {"SSID name :","IP Address :","Netmask :","Gateway IP :"};

    public static void run() {

        final JFrame jf = new JFrame("Config UBNT automatically");
        jf.setSize(400, 500);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        jPanel.setBorder((BorderFactory.createTitledBorder("UBNT_M2 Configuration")));
        jPanel.setLayout(null);

        JTextField inputBoxes = null;
        final List<JTextField> jTextFields = new ArrayList<JTextField>();
        for (int i = 1; i <= labelName.length; i++) {
            //setup labels
            JLabel labels = new JLabel(labelName[i-1],SwingConstants.LEFT);
            labels.setFont(new Font("ITALIC", 1, 16));
            labels.setLocation(10,40*i);
            labels.setSize(100,30);
            jPanel.add(labels);

            //setup input boxes
            inputBoxes = new JTextField(SwingConstants.RIGHT);
            inputBoxes.setFont(new Font(null, Font.PLAIN, 14));
            inputBoxes.setLocation(120,40*i);
            inputBoxes.setSize(200,30);
            jPanel.add(inputBoxes);
            jTextFields.add(inputBoxes);
        }

        //setup button
        JButton jButton = new JButton("M2");
        jButton.setFont(new Font(null,Font.BOLD,14));
        jButton.setLocation(145,300);
        jButton.setSize(105,30);

        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton jb = (JButton)e.getSource();
                String buttonText = jb.getText();
                log.info(buttonText);

                String ssid = jTextFields.get(0).getText();
                String ip = jTextFields.get(1).getText();
                String netmask = jTextFields.get(2).getText();
                String gatewayIP = jTextFields.get(3).getText();
//                String frequency = jTextFields.get(4).getText();
//                String macAddress = jTextFields.get(5).getText();
                if (buttonText.trim().equals("M2")){
                    M2_Configuration.configM2(ssid,ip,netmask,gatewayIP);
                }
//                else if (buttonText.trim().equals("M5_AP")){
//                    M5_Configuration.configM5("AP",ssid,ip,netmask,gatewayIP,frequency,null);
//                }
//                else if (buttonText.trim().equals("M5_ST")){
//                    M5_Configuration.configM5("ST",ssid,ip,netmask,gatewayIP,frequency,macAddress);
//                }
                int progress = M2_Configuration.progress;
                if (progress == 1){
                    JOptionPane.showMessageDialog(
                            jf,
                            "Configuration successful !",
                            "Configuration result",
                            JOptionPane.INFORMATION_MESSAGE
                            );
                }else {
                    JOptionPane.showMessageDialog(
                            jf,
                            "Configuration failed !",
                            "Configuration result",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        });

        jPanel.add(jButton);
        jf.setContentPane(jPanel);
        jf.setVisible(true);

    }

}
