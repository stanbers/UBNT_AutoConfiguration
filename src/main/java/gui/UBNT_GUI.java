package gui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ubnt.m2.M2_Configuration;
import ubnt.m5.M5_Configuration;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    private static String[] labelName = {"SSID name :","IP Address :","Netmask :","Gateway IP :","Frequecy :","Mac address: "};
    private static JButton jButton = new JButton("M2");
    private static int realLength;
    private static JFrame jf = new JFrame("Config UBNT automatically");

    /**
     * To create the panel component under each tab, the panel includes JLabel/JTextField and JButton
     * @param tabName  the tab name
     * @return  the rendered panel with JLabel/JTextField and JButton
     */
    private static JComponent createTextPanel(String tabName){
        JPanel jPanel = new JPanel(null);
        jPanel.setBorder((BorderFactory.createTitledBorder("UBNT( "+tabName+") Configuration")));
        jPanel.setLayout(null);

        JTextField inputBoxes = null;

        jButton.setFont(new Font(null,Font.BOLD,14));
        jButton.setLocation(145,310);
        jButton.setSize(105,30);


        if (tabName.trim().equals("M2")){
            realLength = labelName.length-2;
        }
        else if (tabName.trim().equals("M5_AP") || tabName.trim().equals("M5_ST")){
            realLength = labelName.length-1;
        }

        final List<JTextField> jTextFields = new ArrayList<JTextField>();
        for (int i = 1; i <= realLength; i++) {

            //setup labels
            if (i == realLength && tabName.trim().equals("M5_AP")){
                labelName[i-1] = "Frequecy :";
            }
            else if (i == realLength && tabName.trim().equals("M5_ST")){
                labelName[i-1] = "Mac address: ";
            }
            JLabel labels = new JLabel(labelName[i-1],SwingConstants.LEFT);
            labels.setFont(new Font("ITALIC", 1, 16));
            labels.setLocation(10,40*i);
            labels.setSize(115,30);
            jPanel.add(labels);

            //setup input boxes
            inputBoxes = new JTextField(SwingConstants.RIGHT);
            inputBoxes.setFont(new Font(null, Font.PLAIN, 14));
            inputBoxes.setLocation(120,40*i);
            inputBoxes.setSize(200,30);
            jPanel.add(inputBoxes);
            jTextFields.add(inputBoxes);
        }

        jButton = new JButton(tabName);
        jPanel.add(jButton);

        //setup button action listener
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton jb = (JButton)e.getSource();
                String buttonText = jb.getText();
                log.info(buttonText);

                int progress = 0;
                if (buttonText.trim().equals("M2")){
                    M2_Configuration.configM2(jTextFields.get(0).getText(),jTextFields.get(1).getText(),
                            jTextFields.get(2).getText(),jTextFields.get(3).getText());
                }
                else if (buttonText.trim().equals("M5_AP")){
                    log.info(jTextFields.get(0).getText() + "-----"+jTextFields.get(1).getText());
                    M5_Configuration.configM5("AP",jTextFields.get(0).getText(),jTextFields.get(1).getText(),
                            jTextFields.get(2).getText(),jTextFields.get(3).getText(),jTextFields.get(4).getText(),null);
                }
                else if (buttonText.trim().equals("M5_ST")){
                    M5_Configuration.configM5("ST",jTextFields.get(0).getText(),jTextFields.get(1).getText(),jTextFields.get(2).getText(),
                            jTextFields.get(3).getText(),null,jTextFields.get(4).getText());
                }

                //to make sure you are using the right flag
                if (buttonText.trim().substring(0,2).equals("M2")){
                    progress = M2_Configuration.progress;
                }
                else if (buttonText.trim().substring(0,2).equals("M5")){
                    progress = M5_Configuration.progress;
                }

                //setup the popup window to let the user know the configuration is successful or not
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


        return jPanel;
    }

    /**
     * the main run method
     */
    public static void run() {

        jf.setSize(400, 500);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
        jf.setIconImage(icon);

        //setup JTabbedPane
        final JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.setFont(new Font("ITALIC", 1, 16));
        jTabbedPane.add("M2",createTextPanel("M2"));
        jTabbedPane.add("M5_AP",createTextPanel("M5_AP"));
        jTabbedPane.add("M5_ST",createTextPanel("M5_ST"));

        //add tab event change listener
        jTabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane tab = (JTabbedPane) e.getSource();
                log.info(tab.getTitleAt(tab.getSelectedIndex()));
                if (tab.getTitleAt(tab.getSelectedIndex()).trim().equals("M2")){
                    realLength = labelName.length-2;
                }
                else if (tab.getTitleAt(tab.getSelectedIndex()).trim().equals("M5_AP")){
                    realLength = labelName.length-1;
                }
                else if (tab.getTitleAt(tab.getSelectedIndex()).trim().equals("M5_ST")){
                    realLength = labelName.length;
                }

            }
        });

        jTabbedPane.setSelectedIndex(0);

        createTextPanel("M2");

        jf.setContentPane(jTabbedPane);
        jf.setVisible(true);

    }

}
