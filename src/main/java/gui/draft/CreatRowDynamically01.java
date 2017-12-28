package gui.draft;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ubnt.m2.M2_Configuration;
import ubnt.m5.M5_Configuration;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @Author by XuLiang
 * @Date 2017/12/28 7:58
 * @Email stanxu526@gmail.com
 */
public class CreatRowDynamically01 {
    private final static Log log = LogFactory.getLog(CreatRowDynamically01.class);
    private static JFrame jf = new JFrame();
    private static Object[] columnNames = {"编号","位置","M2 IP", "M5_Ap IP", "M5_AP 频率", "M5_AP mac地址", "M5_ST IP","M5_ST 锁定mac地址","操作"};  // 7 columns
    private static JButton jButton = new JButton("M2");
    private static int realLength;
    private static String[] labelName = {"位置 :","SSID :","IP 地址 :","子网掩码 :","网关 IP :","频率(MHz) :","Mac 地址 :"};
    private static String M2_IP,M5_AP_IP,M5_AP_Fruq,M5_AP_Mac,M5_ST_IP,position;
    private static final JDialog jDialog = new JDialog();
    public static void main(String[] args) {
        jf.setSize(940, 600);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
        jf.setIconImage(icon);

        final JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.setFont(new Font(null, Font.PLAIN, 18));
        jTabbedPane.add("中继",createTextPanel("中继",null));  // the next step is to convert null to the dynamic row data

        jTabbedPane.setSelectedIndex(0);
        jf.setContentPane(jTabbedPane);
        jf.setVisible(true);
    }
    private static DefaultTableModel defautTableModel;
    public static JPanel createTextPanel(String tabName,Object[][] rowData){

        defautTableModel = new DefaultTableModel(rowData,columnNames){
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

        JPanel jPanel = new JPanel(null);
        jPanel.setBorder((BorderFactory.createTitledBorder(tabName+" 配置")));
        jPanel.setLayout(null);
        jPanel.add(jTableHeader,BorderLayout.NORTH);
        jPanel.add(jTable,BorderLayout.CENTER);


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


        return jPanel;
    }

    private static JPanel createTextPanelOverlay(String tabName){
        JPanel jPanel = new JPanel(null);
        jPanel.setBorder((BorderFactory.createTitledBorder("UBNT( "+tabName+") 配置")));
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
        String[] positions = new String[]{"左线","右线"};
        final JComboBox<String> jComboBox = new JComboBox<String>(positions);
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

            if (i == 1){
                jComboBox.setLocation(120,40*i);
                jComboBox.setSize(200,30);
                jComboBox.setSelectedIndex(0);
                jPanel.add(jComboBox);

            }else{
                if (i==2){
                    inputBoxes = new JTextField("SSID name",SwingConstants.RIGHT);
                }else if (i==4){
                    inputBoxes = new JTextField("255.255.255.0",SwingConstants.RIGHT);
                }
                else {
                    inputBoxes = new JTextField(SwingConstants.RIGHT);
                }
                inputBoxes.setFont(new Font(null, Font.PLAIN, 14));
                inputBoxes.setLocation(120,40*i);
                inputBoxes.setSize(200,30);
                jPanel.add(inputBoxes);
                jTextFields.add(inputBoxes);
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
                position = jComboBox.getSelectedItem().toString();

                int progress = 0;
                if (buttonText.trim().equals("M2")){
                    M2_IP = jTextFields.get(1).getText();
                    M2_Configuration.configM2(jTextFields.get(0).getText(),M2_IP,
                            jTextFields.get(2).getText(),jTextFields.get(3).getText());
                }
                else if (buttonText.trim().equals("M5_AP")){
                    log.info(jTextFields.get(1).getText() + "-----"+jTextFields.get(2).getText());
                    M5_AP_IP = jTextFields.get(1).getText();
                    M5_AP_Fruq = jTextFields.get(4).getText();
                    M5_Configuration.configM5("AP",jTextFields.get(0).getText(),M5_AP_IP,
                            jTextFields.get(2).getText(),jTextFields.get(3).getText(),M5_AP_Fruq,null);
                }
                else if (buttonText.trim().equals("M5_ST")){
                    M5_ST_IP = jTextFields.get(1).getText();
                    M5_AP_Mac = jTextFields.get(4).getText();
                    M5_Configuration.configM5("ST",jTextFields.get(0).getText(),M5_ST_IP,jTextFields.get(2).getText(),
                            jTextFields.get(3).getText(),null,M5_AP_Mac);
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
                            jf,
                            "配置成功 !",
                            "配置结果",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }else {
                    JOptionPane.showMessageDialog(
                            jf,
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
     * Collect cell information from the overlay, include M2 and M5 configuration info.
     *
     */
    public static void rowGenerator(){
        final JDialog jDialog = new JDialog();
        jDialog.setSize(500,500);
        jDialog.setLocationRelativeTo(jf);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
        jDialog.setIconImage(icon);

//        JPanel jPanel = new JPanel(null);

        //setup JTabbedPane
        final JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.setFont(new Font("ITALIC", 1, 16));
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

//        JButton okBtn = new JButton("确定");
//        okBtn.setLocation(350,400);
//        okBtn.setSize(200,30);

//        final Vector<String> realValue = new Vector<String>();
//        realValue.add(null);
//        realValue.add(position);
//        okBtn.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                realValue.add(M2_IP);
//                realValue.add(M5_AP_IP);
//                realValue.add(M5_AP_Fruq);
//                realValue.add(M5_AP_Mac);
//                realValue.add(M5_ST_IP);
//                realValue.add(M5_AP_Mac);
//                log.info("the first text field input value is :" + M2_IP);
////                defaultTableModel.addRow(realValue);
//                jDialog.dispose();
//            }
//        });



        jDialog.setContentPane(jTabbedPane);
        jDialog.setVisible(true);

    }

}
