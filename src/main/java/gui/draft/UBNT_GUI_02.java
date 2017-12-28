package gui.draft;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author by XuLiang
 * @Date 2017/12/22 19:14
 * @Email stanxu526@gmail.com
 */
public class UBNT_GUI_02 {
    private final static Log log = LogFactory.getLog(UBNT_GUI_02.class);
    private static String[] labelName = {"SSID name :","IP Address :","Netmask :","Gateway IP :","Frequecy :","Mac address: "};

    public static void main(String[] args) {
        new UBNT_GUI_02().run();
    }
    private String tabName = "M2";
//    private static JPanel jPanel = new JPanel();
    private JButton jButton = new JButton("M2");



    public JPanel generateJPnel(String tabName){
       // private static\
        JPanel m2_jPanel = new JPanel();
        //private static
        JPanel m5_ap_jPanel = new JPanel();
       // private static
        JPanel m5_st_jPanel = new JPanel();
        JTextField inputBoxes = null;
        List<JTextField> jTextFields = new ArrayList<JTextField>();
        if (tabName.trim().equals("M2")){
            for (int i = 1; i <= 4; i++) {
                //setup labels
                JLabel labels = new JLabel(labelName[i-1],SwingConstants.LEFT);
                labels.setFont(new Font("ITALIC", 1, 16));
                labels.setLocation(10,40*i);
                labels.setSize(100,30);
                m2_jPanel.add(labels);

                //setup input boxes
                inputBoxes = new JTextField(SwingConstants.RIGHT);
                inputBoxes.setFont(new Font(null, Font.PLAIN, 14));
                inputBoxes.setLocation(10,40*i);
                inputBoxes.setSize(200,30);
                m2_jPanel.add(inputBoxes);
                //jTextFields.add(inputBoxes);
            }
            return m2_jPanel;
        }
        else if (tabName.trim().equals("M5_AP")){
            for (int i = 1; i <= 5; i++) {

                //setup labels
                JLabel labels = new JLabel(labelName[i-1],SwingConstants.LEFT);
                labels.setFont(new Font("ITALIC", 1, 16));
                labels.setLocation(50,50*i);
                labels.setSize(100,30);
                m5_ap_jPanel.add(labels);

                //setup input boxes
                inputBoxes = new JTextField(SwingConstants.RIGHT);
                inputBoxes.setFont(new Font(null, Font.PLAIN, 14));
                inputBoxes.setLocation(120,40*i);
                inputBoxes.setSize(200,30);
                m5_ap_jPanel.add(inputBoxes);
                //jTextFields.add(inputBoxes);
            }
            return m5_ap_jPanel;
        }
        else if (tabName.trim().equals("M5_ST")){
            for (int i = 1; i <= 6; i++) {

                //setup labels
                JLabel labels = new JLabel(labelName[i-1],SwingConstants.LEFT);
                labels.setFont(new Font("ITALIC", 1, 16));
                labels.setLocation(10,40*i);
                labels.setSize(100,30);
                m5_st_jPanel.add(labels);

                //setup input boxes
                inputBoxes = new JTextField(SwingConstants.RIGHT);
                inputBoxes.setFont(new Font(null, Font.PLAIN, 14));
                inputBoxes.setLocation(120,40*i);
                inputBoxes.setSize(200,30);
                m5_st_jPanel.add(inputBoxes);
               // jTextFields.add(inputBoxes);
            }
            return m5_st_jPanel;
        }
        return null;
    }

    public void run() {

        final JFrame jf = new JFrame("Config UBNT automatically");
        jf.setSize(400, 500);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JMenuBar jMenuBar = new JMenuBar();
        JMenu m2 = new JMenu("M2");
        m2.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        m2.setFont(new Font(null,Font.BOLD,17));
        JMenu m5_ap = new JMenu("M5_AP");
        m5_ap.setFont(new Font(null,Font.BOLD,17));
        m5_ap.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        JMenu m5_st = new JMenu("M5_ST");
        m5_st.setFont(new Font(null,Font.BOLD,17));
        m5_st.setBorder(BorderFactory.createRaisedSoftBevelBorder());

        jMenuBar.add(m2);
        jMenuBar.add(m5_ap);
        jMenuBar.add(m5_st);

        JPanel jPanel = generateJPnel(tabName);

        jPanel.setBorder((BorderFactory.createTitledBorder("UBNT Configuration")));
        jPanel.setLayout(null);

        m2.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                JMenu jMenu = (JMenu) e.getSource();
                tabName= jMenu.getText();
                jButton.setText(tabName);
                jf.setContentPane(generateJPnel(tabName));
                jf.setVisible(true);
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });
        m5_ap.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                JMenu jMenu = (JMenu) e.getSource();
                tabName= jMenu.getText();
                jButton.setText(tabName);
                jf.setContentPane(generateJPnel(tabName));
                jf.setVisible(true);
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });
        m5_st.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                JMenu jMenu = (JMenu) e.getSource();
                tabName= jMenu.getText();
                jButton.setText(tabName);
                jf.setContentPane(generateJPnel(tabName));
                jf.setVisible(true);
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });




//
//
//        jButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JButton jb = (JButton)e.getSource();
//                String buttonText = jb.getText();
//                log.info(buttonText);
//
//                String ssid = jTextFields.get(0).getText();
//                String ip = jTextFields.get(1).getText();
//                String netmask = jTextFields.get(2).getText();
//                String gatewayIP = jTextFields.get(3).getText();
////                String frequency = jTextFields.get(4).getText();
////                String macAddress = jTextFields.get(5).getText();
//                if (buttonText.trim().equals("M2")){
//                    M2_Configuration.configM2(ssid,ip,netmask,gatewayIP);
//                }
////                else if (buttonText.trim().equals("M5_AP")){
////                    M5_Configuration.configM5("AP",ssid,ip,netmask,gatewayIP,frequency,null);
////                }
////                else if (buttonText.trim().equals("M5_ST")){
////                    M5_Configuration.configM5("ST",ssid,ip,netmask,gatewayIP,frequency,macAddress);
////                }
//                int progress = M2_Configuration.progress;
//                if (progress == 1){
//                    JOptionPane.showMessageDialog(
//                            jf,
//                            "Configuration successful !",
//                            "Configuration result",
//                            JOptionPane.INFORMATION_MESSAGE
//                            );
//                }else {
//                    JOptionPane.showMessageDialog(
//                            jf,
//                            "Configuration failed !",
//                            "Configuration result",
//                            JOptionPane.WARNING_MESSAGE
//                    );
//                }
//            }
//        });



        //setup button
        jButton.setFont(new Font(null,Font.BOLD,14));
        jButton.setLocation(145,300);
        jButton.setSize(105,30);

        generateJPnel(tabName).add(jButton);
//        jPanel.add(M5_AP);
        jf.setJMenuBar(jMenuBar);
        jf.setContentPane(generateJPnel(tabName));
        jf.setVisible(true);

    }

}
