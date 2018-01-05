package gui.draft;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author by XuLiang
 * @Date 2018/01/04 16:26
 * @Email stanxu526@gmail.com
 */
public class CreateConfigDialog {
    private final static Log log = LogFactory.getLog(CreateConfigDialog.class);
    private String tabName;
    private DefaultTableModel defaultTableModel;
    public CreateConfigDialog(String tabName, DefaultTableModel defaultTableModel){
        this.tabName = tabName;
        this.defaultTableModel = defaultTableModel;
    }
    public CreateConfigDialog(){}
    private static CreateConfigDialog createConfigDialog = null;
    public static CreateConfigDialog getInstance(){
        if (createConfigDialog == null){
            createConfigDialog = new CreateConfigDialog();
        }
        return createConfigDialog;
    }


    /**
     * create dialog
     * @return
     */
    public static JPanel createDialog(String tabName, final String[] labelName, final List<String> commonFields, final DefaultTableModel defautTableModel){
        final JDialog jDialog = new JDialog();
        int realLength = 0;
        JPanel jPanel = new JPanel(null);
        jPanel.setBorder((BorderFactory.createTitledBorder("UBNT( "+tabName+") 配置")));
        jPanel.setLayout(null);

        JTextField inputBoxes = null;
        //the update config data button
        JButton jButton = new JButton("M2");
        jButton.setFont(new Font(null,Font.BOLD,14));
        jButton.setLocation(145,310);
        jButton.setSize(105,30);


        final String[] positions = new String[]{"左线","右线"};
        final JComboBox<String> jComboBox = new JComboBox<String>(positions);
        final JTextField KM = new JTextField(SwingConstants.RIGHT);
        final JTextField meter = new JTextField(SwingConstants.RIGHT);
        final List<JTextField> jTextFields = new ArrayList<JTextField>();
        if (tabName != null && tabName.trim().equals("位置")){

            JLabel way = new JLabel("设定线路 :",SwingConstants.LEFT);
            way.setFont(new Font(null, 1, 16));
            way.setLocation(10,40);
            way.setSize(115,30);

            jComboBox.setLocation(120,40);
            jComboBox.setSize(200,30);
            jComboBox.setFont(new Font(null, 1, 16));
            jComboBox.setSelectedIndex(0);

            JLabel specificPosition = new JLabel("具体位置 :",SwingConstants.LEFT);
            specificPosition.setFont(new Font(null, 1, 16));
            specificPosition.setLocation(10,90);
            specificPosition.setSize(115,30);

            JLabel DK = new JLabel("DK ");
            DK.setLocation(120,90);
            DK.setSize(30,30);
            DK.setFont(new Font(null,1,16));

            KM.setLocation(155,90);
            KM.setSize(70,30);
            KM.setFont(new Font(null,1,16));

            JLabel plus = new JLabel("+");
            plus.setFont(new Font(null,0,15));
            plus.setLocation(233,90);
            plus.setSize(20,30);

            meter.setFont(new Font(null,1,16));
            meter.setSize(70,30);
            meter.setLocation(250,90);

            jPanel.add(way);
            jPanel.add(jComboBox);
            jPanel.add(specificPosition);
            jPanel.add(DK);
            jPanel.add(KM);
            jPanel.add(plus);
            jPanel.add(meter);
        }else {
            if (tabName != null && tabName.trim().equals("M2")){
                realLength = labelName.length-2;
            }
            else if (tabName != null && tabName.trim().equals("M5_AP") || tabName.trim().equals("M5_ST")){
                realLength = labelName.length-1;
            }

            for (int i = 1; i <= realLength; i++) {

                if (tabName != null && i == realLength && !tabName.trim().equals("M2")){
                    if (tabName.trim().equals("M5_AP")){
                        labelName[i-1] = "频率(MHz) :";
                    }else if (tabName.trim().equals("M5_ST")){
                        labelName[i-1] = "Mac 地址: ";
                    }

                    inputBoxes = new JTextField(SwingConstants.RIGHT);
                    inputBoxes.setFont(new Font(null, Font.PLAIN, 14));
                    inputBoxes.setLocation(120,40*i);
                    inputBoxes.setSize(200,30);
                    jPanel.add(inputBoxes);
                    jTextFields.add(inputBoxes);

                }else {
                    JMIPV4AddressField IP = new JMIPV4AddressField();
                    IP.setFont(new Font(null, Font.PLAIN, 14));
                    IP.setLocation(120,40*i);
                    IP.setSize(200,30);
                    jPanel.add(IP);
                    jTextFields.add(IP);
                }
                //setup labels
                JLabel labels = new JLabel(labelName[i-1],SwingConstants.LEFT);
                labels.setFont(new Font(null, 1, 16));
                labels.setLocation(10,40*i);
                labels.setSize(115,30);
                jPanel.add(labels);
            }
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
                //get position
                String position = jComboBox.getSelectedItem().toString();
                String DK = KM.getText() + " + " + meter.getText();

                int progress = 0;
                if (buttonText.trim().equals("M2")){
                    String M2_IP = jTextFields.get(0).getText();
                    log.info("M2_IP is " + M2_IP);
//                    cellValue.add(M2_IP);
                    int targetRow = defautTableModel.getRowCount();
                    log.info("the real row count is :" + targetRow);
                    defautTableModel.setValueAt(M2_IP,targetRow-1,3);

                    //ssid = commonFields.get(1); netmask = commonFields.get(3); gatewayIP = commonFields.get(2);
//                    M2_Configuration.configM2(commonFields.get(1),M2_IP,commonFields.get(3),commonFields.get(2));
                }
                else if (buttonText.trim().equals("M5_AP")){
                    String M5_AP_IP = jTextFields.get(0).getText();
                    String M5_AP_Fruq = jTextFields.get(1).getText();
                    log.info("M5_AP_IP is " + M5_AP_IP);
                    log.info("M5_AP_Fruq is " + M5_AP_Fruq);
                    defautTableModel.setValueAt(M5_AP_IP,defautTableModel.getRowCount()-1,4);
                    defautTableModel.setValueAt(M5_AP_Fruq,defautTableModel.getRowCount()-1,5);
                    //M5_Configuration.configM5("AP",commonFields.get(1),M5_AP_IP,commonFields.get(3),commonFields.get(2),M5_AP_Fruq,null);
                }
                else if (buttonText.trim().equals("M5_ST")){
                    String M5_ST_IP = jTextFields.get(0).getText();
                    String M5_AP_Mac = jTextFields.get(1).getText();
                    log.info("M5_ST_IP is " + M5_ST_IP);
                    log.info("M5_AP_Mac is " + M5_AP_Mac);

                    defautTableModel.setValueAt(M5_AP_Mac,defautTableModel.getRowCount()-1,6);
                    defautTableModel.setValueAt(M5_ST_IP,defautTableModel.getRowCount()-1,7);
                    defautTableModel.setValueAt(M5_AP_Mac,defautTableModel.getRowCount()-1,8);
                    //M5_Configuration.configM5("ST",commonFields.get(1),M5_ST_IP,commonFields.get(3),commonFields.get(2),null,M5_AP_Mac);
                }else if (buttonText.trim().equals("位置")){
                    defautTableModel.setValueAt(position,defautTableModel.getRowCount()-1,1);
                    defautTableModel.setValueAt(DK,defautTableModel.getRowCount()-1,2);
                    log.info("ssid is " + commonFields.get(0));
                    log.info("M2 gatewayIP is " + commonFields.get(1));
                    log.info("M2 netmask is " + commonFields.get(2));
                    log.info("M5-AP gatewayIP is " + commonFields.get(3));
                    log.info("M5-AP netmask is " + commonFields.get(4));
                }

                //to make sure using the right flag
                if (buttonText.trim().substring(0,2).equals("M2")){
//                    progress = M2_Configuration.progress;
                }
                else if (buttonText.trim().substring(0,2).equals("M5")){
//                    progress = M5_Configuration.progress;
                }

                //setup the popup window to let the user know the configuration is successful or not
//                if (progress == 1){
//                    JOptionPane.showMessageDialog(
//                            jFrame,
//                            "配置成功 !",
//                            "配置结果",
//                            JOptionPane.INFORMATION_MESSAGE
//                    );
//                }else {
//                    JOptionPane.showMessageDialog(
//                            jFrame,
//                            "配置失败，请重新配置 !",
//                            "配置结果",
//                            JOptionPane.WARNING_MESSAGE
//                    );
//                }
                jDialog.dispose();
            }
        });


        return jPanel;

    }




}
