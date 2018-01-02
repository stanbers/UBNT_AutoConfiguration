package gui.draft;

import gui.main.UBNT_GUI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ubnt.m2.M2_Configuration;
import ubnt.m5.M5_Configuration;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static java.awt.Font.BOLD;

/**
 * @Author by XuLiang
 * @Date 2017/12/29 13:56
 * @Email stanxu526@gmail.com
 */
public class Test05 {
    private final static Log log = LogFactory.getLog(UBNT_GUI.class);
    //the container
    private JFrame jFrame = new JFrame("配置界面");
    private String projectId;
    private Vector<String> commonFields = new Vector<String>();
    //the project item should be added dynamically
    final Vector<String> projects = new Vector<String>();
    private Object[] columnNames = {"编号","位置","M2 IP", "M5_Ap IP", "M5_AP 频率", "M5_AP mac地址", "M5_ST IP","M5_ST 锁定mac地址","操作"};  // 9 columns
    private String[] labelName = {"IP 地址 :","频率(MHz) :","Mac 地址 :"};
    private static String M2_IP,M5_AP_IP,M5_AP_Fruq,M5_AP_Mac,M5_ST_IP,position;
    //table page panel
    private JPanel tablePanel = new JPanel(null);

    private int realLength;
    private JButton jButton = new JButton("M2");
    private DefaultTableModel defautTableModel;
    private final JDialog jDialog = new JDialog();


    /**
     * to show the homepage
     */
    public  void show() {
        //setup container's size and location
        jFrame.setSize(950, 600);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
        jFrame.setIconImage(icon);

        //create the homepage panel
        final JPanel homepagePanel = new JPanel(null);

        //init combo box
        projects.add("请选择项目");

        //create a select project combo box, the default item is "请选择项目“.
        JComboBox projectListComboBox = new JComboBox(projects);
        projectListComboBox.setLocation(250,220);
        projectListComboBox.setSize(200,60);
        projectListComboBox.setFont(new Font(null, Font.PLAIN, 25));
        projectListComboBox.setSelectedIndex(0);


        //add combo box item changed listener
        projectListComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String selectedItem = (String) e.getItem();
                //TODO: here need navigate to the main page to show the table
                homepagePanel.setVisible(false);
                SwingUtilities.updateComponentTreeUI(jFrame);
                jFrame.repaint();
                jFrame.setContentPane(tablePanel);

            }
        });

        //setup project button
        JButton createPojectButton = new JButton("+ 新建项目");
        createPojectButton.setLocation(500,220);
        createPojectButton.setSize(200,60);
        createPojectButton.setFont(new Font(null, BOLD, 25));

        //add button event listener
        createPojectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //to invoke new project dialog
                generateNewProjectDilog();

            }
        });

        homepagePanel.add(projectListComboBox);
        homepagePanel.add(createPojectButton);



        defautTableModel = new DefaultTableModel(null,columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable jTable = new JTable(defautTableModel);

        jTable.setLocation(20,60);
        jTable.setSize(850,400);
        jTable.setRowHeight(25);

        //setup column width
        jTable.getColumn("编号").setMaxWidth(40);
        jTable.getColumn("位置").setMaxWidth(45);
        jTable.getColumn("M5_AP 频率").setPreferredWidth(40);
        jTable.getColumn("M2 IP").setPreferredWidth(30);
        jTable.getColumn("M5_Ap IP").setPreferredWidth(40);
        jTable.getColumn("M5_ST IP").setPreferredWidth(50);
        jTable.getColumn("M5_ST 锁定mac地址").setPreferredWidth(100);
        jTable.getColumn("操作").setMaxWidth(45);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        jTable.setFont(new Font(null, Font.PLAIN, 15));

        JTableHeader jTableHeader = jTable.getTableHeader();
        jTableHeader.setLocation(20,30);
        jTableHeader.setSize(850,30);
        jTableHeader.setFont(new Font(null, Font.BOLD, 16));
        jTableHeader.setResizingAllowed(true);
        jTableHeader.setReorderingAllowed(true);

//        jPanel.setBorder((BorderFactory.createTitledBorder(tabName+" 配置")));
        tablePanel.setLayout(null);
        tablePanel.add(jTableHeader,BorderLayout.NORTH);
        tablePanel.add(jTable,BorderLayout.CENTER);

        JButton add = new JButton("选择配置");
        add.setFont(new Font(null,Font.BOLD,14));
        add.setLocation(700,350);
        add.setSize(100,40);
        jTable.add(add);
        JButton export = new JButton("导出数据");
        export.setFont(new Font(null,Font.BOLD,14));
        export.setLocation(40,350);
        export.setSize(100,40);
        jTable.add(export);

        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //ToDo: to generate a new row with all data, but how ?  ---> generate the data frist!
                rowGenerator();
            }
        });















        jFrame.setContentPane(homepagePanel);
        jFrame.setVisible(true);
    }

    /**
     * generate rows
     */
    public void rowGenerator(){
        final JDialog jDialog = new JDialog();
        jDialog.setSize(500,500);
        jDialog.setLocationRelativeTo(jFrame);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
        jDialog.setIconImage(icon);

//        JPanel jPanel = new JPanel(null);

        //setup JTabbedPane
        final JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.setFont(new Font("ITALIC", 1, 16));
        jTabbedPane.add("位置",createTextPanelOverlay("位置"));
        jTabbedPane.add("M2",createTextPanelOverlay("M2"));
        jTabbedPane.add("M5_AP",createTextPanelOverlay("M5_AP"));
        jTabbedPane.add("M5_ST",createTextPanelOverlay("M5_ST"));

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
        createTextPanelOverlay("M2");

        jDialog.setContentPane(jTabbedPane);
        jDialog.setVisible(true);

    }

    /**
     * the final config overlay
     * @param tabName  the tab name
     * @return  the overlay panel content
     */
    private JPanel createTextPanelOverlay(String tabName){
        JPanel jPanel = new JPanel(null);
        jPanel.setBorder((BorderFactory.createTitledBorder("UBNT( "+tabName+") 配置")));
        jPanel.setLayout(null);

        JTextField inputBoxes = null;

        jButton.setFont(new Font(null,Font.BOLD,14));
        jButton.setLocation(145,310);
        jButton.setSize(105,30);


        String[] positions = new String[]{"左线","右线"};
        final JComboBox<String> jComboBox = new JComboBox<String>(positions);
        if (tabName.trim().equals("位置")){

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

            JTextField KM = new JTextField(SwingConstants.RIGHT);
            KM.setLocation(155,90);
            KM.setSize(70,30);
            KM.setFont(new Font(null,1,16));

            JLabel plus = new JLabel("+");
            plus.setFont(new Font(null,0,15));
            plus.setLocation(233,90);
            plus.setSize(20,30);

            JTextField meter = new JTextField(SwingConstants.RIGHT);
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
        }else if (tabName.trim().equals("M2")){
            realLength = labelName.length-2;
        }
        else if (tabName.trim().equals("M5_AP") || tabName.trim().equals("M5_ST")){
            realLength = labelName.length-1;
        }

        final List<JTextField> jTextFields = new ArrayList<JTextField>();

        for (int i = 1; i <= realLength; i++) {

            //setup labels
            if (i == realLength && tabName.trim().equals("M5_AP")){
                labelName[i-1] = "频率(MHz) :";
            }
            else if (i == realLength && tabName.trim().equals("M5_ST")){
                labelName[i-1] = "Mac 地址: ";
            }
            JLabel labels = new JLabel(labelName[i-1],SwingConstants.LEFT);
            labels.setFont(new Font(null, 1, 16));
            labels.setLocation(10,40*i);
            labels.setSize(115,30);
            jPanel.add(labels);

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
                //get position
                position = jComboBox.getSelectedItem().toString();

                int progress = 0;
                if (buttonText.trim().equals("M2")){
                    M2_IP = jTextFields.get(0).getText();
                    log.info("M2_IP is " + M2_IP);
                    //ssid = commonFields.get(1); netmask = commonFields.get(3); gatewayIP = commonFields.get(2);
                    M2_Configuration.configM2(commonFields.get(1),M2_IP,commonFields.get(3),commonFields.get(2));
                }
                else if (buttonText.trim().equals("M5_AP")){
                    M5_AP_IP = jTextFields.get(0).getText();
                    M5_AP_Fruq = jTextFields.get(1).getText();
                    log.info("M5_AP_IP is " + M5_AP_IP);
                    log.info("M5_AP_Fruq is " + M5_AP_Fruq);
                    M5_Configuration.configM5("AP",commonFields.get(1),M5_AP_IP,commonFields.get(3),commonFields.get(2),M5_AP_Fruq,null);
                }
                else if (buttonText.trim().equals("M5_ST")){
                    M5_ST_IP = jTextFields.get(0).getText();
                    M5_AP_Mac = jTextFields.get(1).getText();
                    log.info("M5_ST_IP is " + M5_ST_IP);
                    log.info("M5_AP_Mac is " + M5_AP_Mac);
                    M5_Configuration.configM5("ST",commonFields.get(1),M5_ST_IP,commonFields.get(3),commonFields.get(2),null,M5_AP_Mac);
                }

                //to make sure using the right flag
                if (buttonText.trim().substring(0,2).equals("M2")){
                    progress = M2_Configuration.progress;
                }
                else if (buttonText.trim().substring(0,2).equals("M5")){
                    progress = M5_Configuration.progress;
                }

                //setup the popup window to let the user know the configuration is successful or not
                if (progress == 1){
                    JOptionPane.showMessageDialog(
                            jFrame,
                            "配置成功 !",
                            "配置结果",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }else {
                    JOptionPane.showMessageDialog(
                            jFrame,
                            "配置失败，请重新配置 !",
                            "配置结果",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
                final Vector<String> realValue = new Vector<String>();
                realValue.add(null);
                realValue.add(position);
                realValue.add(M2_IP);
                realValue.add(M5_AP_IP);
                realValue.add(M5_AP_Fruq);
                realValue.add(M5_AP_Mac);
                realValue.add(M5_ST_IP);
                realValue.add(M5_AP_Mac);
                log.info("the first text field input value is :" + M2_IP);
                defautTableModel.addRow(realValue);
                jDialog.dispose();
            }
        });


        return jPanel;
    }





    /**
     * Generate new project dialog
     * @return the project dialog
     */
    public JDialog generateNewProjectDilog(){
        final JDialog jDialog = new JDialog();
        jDialog.setSize(470,580);
        jDialog.setLocationRelativeTo(jFrame);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
        jDialog.setIconImage(icon);

        JPanel jPanel = new JPanel(null);
        jPanel.setBorder(BorderFactory.createTitledBorder("新建项目："));

        //project id label and corresponding text field
        JLabel projectIdLabel = new JLabel("项目 id :");
        final JTextField projectIdInputBox = new JTextField();  // I need the input text, it will be used later
        projectIdLabel.setLocation(50,40);
        projectIdLabel.setSize(120,40);
        projectIdLabel.setFont(new Font(null, 1, 18));
        projectIdInputBox.setLocation(190,40);
        projectIdInputBox.setSize(200,40);
        projectIdInputBox.setFont(new Font(null, Font.PLAIN, 18));
        jPanel.add(projectIdLabel);
        jPanel.add(projectIdInputBox);

        //ssid name label and corresponding text field
        JLabel ssidLabel = new JLabel("SSID :");
        final JTextField ssidInputBox = new JTextField();
        ssidLabel.setLocation(50,90);
        ssidLabel.setSize(120,40);
        ssidLabel.setFont(new Font(null, BOLD,18));
        ssidInputBox.setLocation(190,90);
        ssidInputBox.setSize(200,40);
        jPanel.add(ssidLabel);
        jPanel.add(ssidInputBox);

        //subway wifi panel include netmask and gateIP
        JPanel innerJPanelWifi = new JPanel();
        innerJPanelWifi.setLocation(50,140);
        innerJPanelWifi.setSize(340,150);
        innerJPanelWifi.setBorder(BorderFactory.createTitledBorder(null,"隧道无线网络", TitledBorder.LEFT,TitledBorder.TOP,new Font(null, BOLD,18)));
        innerJPanelWifi.setLayout(null);

        //gateway IP label and corresponding text field
        JLabel gatewayIP = new JLabel("无线网关 :");
        final JTextField gatewayIPInputBox = new JTextField();  // I need the input text, it will be used later
        gatewayIP.setLocation(60,180);
        gatewayIP.setSize(120,40);
        gatewayIP.setFont(new Font(null, 1, 18));
        gatewayIPInputBox.setLocation(190,180);
        gatewayIPInputBox.setSize(180,40);
        jPanel.add(gatewayIP);
        jPanel.add(gatewayIPInputBox);

        //net mask label and corresponding text field
        JLabel netMaskLabel = new JLabel("子网掩码 :");
        final JTextField netMaskInputBox = new JTextField();
        netMaskLabel.setLocation(60,230);
        netMaskLabel.setSize(120,40);
        netMaskLabel.setFont(new Font(null, BOLD,18));
        netMaskInputBox.setLocation(190,230);
        netMaskInputBox.setSize(180,40);
        jPanel.add(netMaskLabel);
        jPanel.add(netMaskInputBox);

        //subway M5 bridge panel include netmask and gateIP
        JPanel innerJPanel_M5 = new JPanel();
        innerJPanel_M5.setLocation(50,300);
        innerJPanel_M5.setSize(340,150);
        innerJPanel_M5.setBorder(BorderFactory.createTitledBorder(null,"M5 网桥", TitledBorder.LEFT,TitledBorder.TOP,new Font(null, BOLD,18)));
        innerJPanel_M5.setLayout(null);

        //M5 bridge gateway IP label and corresponding text field
        JLabel gatewayIP_M5 = new JLabel("网段 :");
        final JTextField gatewayIP_M5_InputBox = new JTextField();  // I need the input text, it will be used later
        gatewayIP_M5.setLocation(60,340);
        gatewayIP_M5.setSize(120,40);
        gatewayIP_M5.setFont(new Font(null, 1, 18));
        gatewayIP_M5_InputBox.setLocation(190,340);
        gatewayIP_M5_InputBox.setSize(180,40);
        jPanel.add(gatewayIP_M5);
        jPanel.add(gatewayIP_M5_InputBox);

        //M5 bridge net mask label and corresponding text field
        JLabel netMaskLabel_M5 = new JLabel("子网掩码 :");
        final JTextField netMask_M5_InputBox = new JTextField();
        netMaskLabel_M5.setLocation(60,390);
        netMaskLabel_M5.setSize(120,40);
        netMaskLabel_M5.setFont(new Font(null, BOLD,18));
        netMask_M5_InputBox.setLocation(190,390);
        netMask_M5_InputBox.setSize(180,40);
        jPanel.add(netMaskLabel_M5);
        jPanel.add(netMask_M5_InputBox);

        //ok button
        JButton okButton = new JButton("创建");
        okButton.setFont(new Font(null,Font.BOLD,16));
        okButton.setLocation(100,465);
        okButton.setSize(85,40);

        //cancel button
        JButton cancelButton = new JButton("取消");
        cancelButton.setFont(new Font(null,Font.BOLD,16));
        cancelButton.setLocation(265,465);
        cancelButton.setSize(85,40);

        //ok button event listener
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //save those all fields, those fields will be used to update the config file except project_id.
                projectId = projectIdInputBox.getText();
                log.info("the project id is "+projectIdInputBox.getText());
                commonFields.add(ssidInputBox.getText());
                commonFields.add(netMaskInputBox.getText());
                commonFields.add(gatewayIPInputBox.getText());
                commonFields.add(netMask_M5_InputBox.getText());
                commonFields.add(gatewayIP_M5_InputBox.getText());
                jDialog.dispose();
                projects.add(projectId);
                SwingUtilities.updateComponentTreeUI(jFrame);
            }
        });

        //cancel button event listener
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jDialog.dispose();
            }
        });




        jPanel.add(okButton);
        jPanel.add(cancelButton);

        jPanel.add(innerJPanelWifi);
        jPanel.add(innerJPanel_M5);

        jDialog.setContentPane(jPanel);
        jDialog.setVisible(true);
        return jDialog;

    }
}
