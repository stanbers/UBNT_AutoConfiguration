package gui.version.camera;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utility.JMIPV4AddressField;
import utility.LimitedDocument;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static java.awt.Font.BOLD;

/**
 * @Author by XuLiang
 * @Date 2018/01/24 16:38
 * @Email stanxu526@gmail.com
 */
public class CameraGUI {
    private final static Log log = LogFactory.getLog(CameraGUI.class);

    private JFrame mainFrame = new JFrame("配置页面");

    //create for store the common fields
    private List<String> commonFields = new ArrayList<String>();

    //init record index
    private int recordIndex = 1;

    //initialize camera table header
    final String[] columns_camera = {"编号","线路","位置","摄像头 IP","设备 ID","设备型号"};

    //define camera table model
    private DefaultTableModel tableModel_camera = new DefaultTableModel(null,columns_camera){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    //define the global fields
    private String pName;
    private String pNumber;

    //the older IP which waiting for update, need this older ip to login
    private String olderIP_camera;

    //store ip
    List<String> ipList = null;

    /**
     * Show config records
     * @param pName_camera
     * @param pNumber_camera
     * @param tableModel
     * @param commonFields
     * @return the camera config records GUI
     */
    public JPanel showConfigRecordsPage(String pName_camera,String pNumber_camera,DefaultTableModel tableModel,List<String> commonFields){
        this.pName = pName_camera;
        this.pNumber = pNumber_camera;
        this.tableModel_camera = tableModel;
        this.commonFields = commonFields;
        //TODO: this page will include two main panels, one is M2 container another one is M5 container
        //this panel the outermost panel container, no need to set location and size
        JPanel outermostContainerPanel = new JPanel(null);
//        outermostContainerPanel.setBorder(BorderFactory.createTitledBorder("中继配置记录："));

        //camera records container
        JPanel cameraContainerPanel = new JPanel(null);
        cameraContainerPanel.setLocation(10,60);
        cameraContainerPanel.setSize(580,550);
        cameraContainerPanel.setBorder(BorderFactory.createTitledBorder(null,"摄像头 配置记录：", TitledBorder.LEFT,TitledBorder.TOP,new Font(null,Font.BOLD,15)));
        cameraContainerPanel.setLayout(null);

        //show common fields on the left
        final JPanel commonFieldsPanel_camera = new JPanel(null);
        commonFieldsPanel_camera.setLocation(650, 60);
        commonFieldsPanel_camera.setSize(350, 240);
        commonFieldsPanel_camera.setBorder(BorderFactory.createTitledBorder(null, "摄像头 其他参数：", TitledBorder.LEFT, TitledBorder.TOP, new Font(null, BOLD, 18)));
        commonFieldsPanel_camera.setLayout(null);

        final String[] commonFieldsLabels_camera = {"摄像头服务器IP : ","摄像头网关         : ","摄像头子网掩码 : "};
        //'i' started from 1, in order to setup the first label offset in vertical direction
        for (int i = 1; i <= commonFieldsLabels_camera.length; i++) {
            JLabel commonFiledsLabel_right = new JLabel(commonFieldsLabels_camera[i-1]);
            JLabel commonFiledsLabel_right_value = new JLabel(commonFieldsLabels_camera[i-1]);
            commonFiledsLabel_right.setFont(new Font(null, 1, 16));
            commonFiledsLabel_right.setLocation(20,40*i);
            commonFiledsLabel_right.setSize(135,40);
            commonFiledsLabel_right_value.setText(commonFields.get(i-1));
            commonFiledsLabel_right_value.setFont(new Font(null, 1, 16));
            commonFiledsLabel_right_value.setLocation(160,40*i);
            commonFiledsLabel_right_value.setSize(135,40);
            commonFieldsPanel_camera.add(commonFiledsLabel_right);
            commonFieldsPanel_camera.add(commonFiledsLabel_right_value);
        }



        //create a JTable to record the configuration info
        final JTable jTable_camera = new JTable(tableModel_camera);
        jTable_camera.setLocation(20,10);
        jTable_camera.setSize(540,450);
        jTable_camera.setRowHeight(25);

        //setup column width
        jTable_camera.getColumn("编号").setMaxWidth(45);
        jTable_camera.getColumn("位置").setMaxWidth(80);
        jTable_camera.getColumn("线路").setMaxWidth(45);
        jTable_camera.getColumn("摄像头 IP").setMaxWidth(115);
        log.info("there are "+jTable_camera.getColumnCount()+" columns");
        jTable_camera.getColumn("设备 ID").setMaxWidth(110);
        jTable_camera.getColumn("设备型号").setMaxWidth(145);
        jTable_camera.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        jTable_camera.setFont(new Font(null, Font.PLAIN, 15));

        //setup cell context align center
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
        jTable_camera.getColumn("编号").setCellRenderer(centerRenderer);
        jTable_camera.getColumn("位置").setCellRenderer(centerRenderer);
        jTable_camera.getColumn("线路").setCellRenderer(centerRenderer);
        jTable_camera.getColumn("摄像头 IP").setCellRenderer(centerRenderer);
        jTable_camera.getColumn("设备 ID").setCellRenderer(centerRenderer);
        jTable_camera.getColumn("设备型号").setCellRenderer(centerRenderer);

        //setup camera hanging table header
        JTableHeader jTableHeader_camera = jTable_camera.getTableHeader();
        jTableHeader_camera.setLocation(20,10);
        jTableHeader_camera.setSize(540,30);
        jTableHeader_camera.setFont(new Font(null, Font.BOLD, 16));
        jTableHeader_camera.setResizingAllowed(true);
        jTableHeader_camera.setReorderingAllowed(true);

        //setup M2 table context scrollable
        final JScrollPane tablePanel_camera = new JScrollPane(jTable_camera);
        tablePanel_camera.setLocation(15,40);
        tablePanel_camera.setSize(540,450);
        cameraContainerPanel.add(tablePanel_camera);

        //setup new create button
        JButton createcamera = new JButton("新建");
        createcamera.setFont(new Font(null,Font.BOLD,16));
        createcamera.setLocation(475,500);
        createcamera.setSize(80,40);
        cameraContainerPanel.add(createcamera);

        //setup remove button
        JButton removecamera = new JButton("删除");
        removecamera.setLocation(345,500);
        removecamera.setSize(80,40);
        removecamera.setFont(new Font(null,Font.BOLD,16));
        cameraContainerPanel.add(removecamera);

        //setup export camera hanging records button
        JButton exportcameraRecords = new JButton("导出");
        exportcameraRecords.setFont(new Font(null,Font.BOLD,16));
        exportcameraRecords.setLocation(15,500);
        exportcameraRecords.setSize(80,40);
        cameraContainerPanel.add(exportcameraRecords);

        //add export all camera hanging config records
        exportcameraRecords.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToExcel(tableModel_camera,"D:\\ConfigFile\\camera\\"+pName+".xlsx",4);
//                exportToExcel(tableModel_M2,System.getProperty("user.dir")+ "\\ConfigFile\\M2\\"+pName+".xlsx",4);
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "导出数据完毕 !",
                        "配置结果",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        //add action listener to create button
        createcamera.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                createCameraTabbedDialog(tableModel_camera);
                createcameraDialog(tableModel_camera);
            }
        });

        removecamera.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int remove  = JOptionPane.showConfirmDialog(mainFrame,"确定删除吗？");
                log.info("return int value is " + remove);
                if (remove == 0){

                    //TODO: need to change the row number, but how ? --> done
                    int selectedRowNumber = jTable_camera.getSelectedRow();
                    int allRowsCount = tableModel_camera.getRowCount();
                    int remainingRows = allRowsCount - selectedRowNumber;
                    log.info("the remaining rows are : " + remainingRows + "; all rows : " + allRowsCount + " ;" + selectedRowNumber + " was selected!");
                    log.info(jTable_camera.getSelectedRow() +" row was deleting");
                    for (int i = 1; i < remainingRows; i++) {
                        tableModel_camera.setValueAt(selectedRowNumber + i,selectedRowNumber+i,0);
                    }
                    if (jTable_camera.getSelectedRow() >= 0){
                        tableModel_camera.removeRow(jTable_camera.getSelectedRow());
                    }else {
                        JOptionPane.showMessageDialog(mainFrame,"目前没有可以被删除的记录 ！");
                    }
                }
            }
        });

        jTable_camera.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){

                    //the whole cell of a specific row are stored in this list.
                    List<String> cellValuesOfSpecificRow = new ArrayList<String>();
                    int rowNum = jTable_camera.getSelectedRow();
                    //i started from 1, cause no to edit row number.
                    log.info("the " + rowNum + "th row was selected !");
                    for (int i = 1; i <= 5; i++) {
                        int a = cellValuesOfSpecificRow.size();
                        log.info("row has " +a +"columns");
                        if (tableModel_camera.getValueAt(rowNum,i) != null){
                            cellValuesOfSpecificRow.add(tableModel_camera.getValueAt(rowNum,i).toString());
                        }else {
                            cellValuesOfSpecificRow.add("null");
                        }
                    }

                    //TODO: get the camera hanging olderIP,
                    if (tableModel_camera.getValueAt(rowNum,3) != null){
                        olderIP_camera = tableModel_camera.getValueAt(rowNum,3).toString();
                    }
                    updatecameraDialog(cellValuesOfSpecificRow,tableModel_camera);
                }
            }
        });

        outermostContainerPanel.add(cameraContainerPanel);
        outermostContainerPanel.add(commonFieldsPanel_camera);
        return outermostContainerPanel;
    }

    /**
     * this method is to udpate row data
     * @param rowData the original row data
     */
    public void updatecameraDialog(List<String> rowData, final DefaultTableModel tableModel){

        final JDialog jDialog_updateRow = new JDialog(mainFrame,"更新 摄像头",true);
        jDialog_updateRow.setSize(450,460);
        jDialog_updateRow.setLocationRelativeTo(mainFrame);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
//        Image icon = kit.getImage(System.getProperty("user.dir")+ "\\icon\\logo.png");
        jDialog_updateRow.setIconImage(icon);

        //select camera model first, then to config the camera
        final JPanel modelPanel = new JPanel(null);
        modelPanel.setBorder((BorderFactory.createTitledBorder("摄像头 配置")));

        JLabel selectModelLabel = new JLabel("请先选择摄像头型号 :");
        selectModelLabel.setLocation(10,30);
        selectModelLabel.setSize(300,40);
        selectModelLabel.setFont(new Font(null, Font.BOLD, 19));
        modelPanel.add(selectModelLabel);

        final JButton model01Button = new JButton("DS-2CD2T25FD-I8");
        model01Button.setLocation(130,100);
        model01Button.setSize(180,50);
        model01Button.setFont(new Font(null, Font.BOLD, 17));
        modelPanel.add(model01Button);

        final JButton model02Button = new JButton("DS-2CD2T10-I5");
        model02Button.setLocation(130,170);
        model02Button.setSize(180,50);
        model02Button.setFont(new Font(null, Font.BOLD, 17));
        modelPanel.add(model02Button);

        JButton model03Button = new JButton("型号3");
        model03Button.setLocation(130,240);
        model03Button.setSize(180,50);
        model03Button.setFont(new Font(null, Font.BOLD, 17));
        modelPanel.add(model03Button);

        final JPanel cameraOverlay_update = new JPanel(null);
        cameraOverlay_update.setBorder((BorderFactory.createTitledBorder("摄像头 更新")));

        final JLabel currentModel = new JLabel("当前设备型号 : ");
        currentModel.setLocation(20,30);
        currentModel.setSize(135,30);
        currentModel.setFont(new Font(null,Font.BOLD,16));
        cameraOverlay_update.add(currentModel);

        final JLabel currentModelValue = new JLabel();
        currentModelValue.setLocation(160,30);
        currentModelValue.setSize(200,30);
        currentModelValue.setFont(new Font(null,Font.BOLD,16));
        cameraOverlay_update.add(currentModelValue);

        if (rowData != null){
            if (rowData.get(4).trim().equals("null")){
                currentModelValue.setText(null);
            }else {
                String Model = rowData.get(4);
                currentModelValue.setText(Model);
                log.info("the current model is " + Model);
            }
        }

        //setup way label
        final String[] ways = new String[]{"左线","右线"};
        JLabel wayLabel = new JLabel("设定线路 :",SwingConstants.LEFT);
        wayLabel.setFont(new Font(null, 1, 16));
        wayLabel.setLocation(20,80);
        wayLabel.setSize(135,30);
        cameraOverlay_update.add(wayLabel);

        //setup way combo box
        final JComboBox<String> wayComboBox = new JComboBox<String>(ways);
        wayComboBox.setLocation(160,80);
        wayComboBox.setSize(200,30);
        wayComboBox.setFont(new Font(null, 1, 16));
        wayComboBox.setSelectedIndex(0);
        cameraOverlay_update.add(wayComboBox);

        if (rowData != null && rowData.get(0).trim().equals("右线")){
            wayComboBox.setSelectedIndex(1);
        }else {
            wayComboBox.setSelectedIndex(0);
        }

        //setup the specific position label
        JLabel specificPosition = new JLabel("具体位置 :",SwingConstants.LEFT);
        specificPosition.setFont(new Font(null, 1, 16));
        specificPosition.setLocation(20,130);
        specificPosition.setSize(135,30);
        cameraOverlay_update.add(specificPosition);

        //setup the specific position TextField
        final JTextField DKText = new JTextField(SwingConstants.RIGHT);
        DKText.setLocation(160,130);
        DKText.setSize(200,30);
        DKText.setFont(new Font(null,1,16));
        cameraOverlay_update.add(DKText);

        if (rowData != null){
            if (rowData.get(1).trim().equals("null")){
                DKText.setText(null);
            }else {
                String dkMiles = rowData.get(1);
                DKText.setText(dkMiles);
                log.info("the DK mile is " + dkMiles);
            }
        }

        //setup camera hanging IP label:
        JLabel cameraIPLabel = new JLabel("摄像头 IP 地址 :",SwingConstants.LEFT);
        cameraIPLabel.setFont(new Font(null, 1, 16));
        cameraIPLabel.setLocation(20,180);
        cameraIPLabel.setSize(135,30);
        cameraOverlay_update.add(cameraIPLabel);

        //setup camera hanging IP text field
        final JMIPV4AddressField IP_camera = new JMIPV4AddressField();
        IP_camera.setIpAddress("192.168.1.64");
        IP_camera.setFont(new Font(null, Font.PLAIN, 14));
        IP_camera.setLocation(160,180);
        IP_camera.setSize(200,30);
        cameraOverlay_update.add(IP_camera);

        if (rowData.get(2).trim().equals("null")){
            IP_camera.setText(null);
        }else {
            IP_camera.setText(rowData.get(2));
        }

        //setup camera ID label:
        JLabel cameraIDLabel = new JLabel("设备 ID :",SwingConstants.LEFT);
        cameraIDLabel.setFont(new Font(null, 1, 16));
        cameraIDLabel.setLocation(20,230);
        cameraIDLabel.setSize(135,30);
        cameraOverlay_update.add(cameraIDLabel);

        //setup camera ID text field
        final JTextField cameraIDInputBox = new JTextField(SwingConstants.RIGHT);
        cameraIDInputBox.setFont(new Font(null, Font.PLAIN, 14));
        cameraIDInputBox.setLocation(160,230);
        cameraIDInputBox.setSize(200,30);
        cameraOverlay_update.add(cameraIDInputBox);

        if (rowData.get(3).trim().equals("null")){
            cameraIDInputBox.setText(null);
        }else {
            cameraIDInputBox.setText(rowData.get(3));
        }

        //setup config button
        final JButton cameraUpdateButton = new JButton("更新摄像头");
        cameraUpdateButton.setFont(new Font(null,Font.BOLD,16));
        cameraUpdateButton.setLocation(145,320);
        cameraUpdateButton.setSize(135,40);
        cameraOverlay_update.add(cameraUpdateButton);

        cameraUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int progress = 0;
                String way = wayComboBox.getSelectedItem().toString();
                String DK = DKText.getText();
                String camera_IP = IP_camera.getText();
                String cameraModel = currentModelValue.getText();

                String path = "D:\\ConfigFile\\camera\\"+pName +".xlsx";
                importFromExcel(null,path);
                for (int i = 0; i < ipList.size(); i++) {
                    if (camera_IP.trim().equals(ipList.get(i)) && !camera_IP.trim().equals(olderIP_camera)){
                        JOptionPane.showMessageDialog(
                                mainFrame,
                                "IP 地址重复，请填写正确的 IP !",
                                "提示",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        return;
                    }

                }


                log.info("the older camera ip is "+olderIP_camera);
                log.info("the new camera ip is "+camera_IP);
                log.info("the camera net mask is "+commonFields.get(2));
                log.info("the camera gateway ip is "+commonFields.get(1));
                log.info("the camera server ip is "+commonFields.get(0));
                log.info("the camera id is "+ cameraIDInputBox.getText());
                log.info("the camera model is "+cameraModel);
                //String cameraIP,String cameraNetMask,String cameraGatewayIP,String serverIP,String deviceID
//                progress = new CameraConfig().config(camera_IP,commonFields.get(2),commonFields.get(1),commonFields.get(0),cameraIDInputBox.getText(),olderIP_camera,cameraModel);

                if (progress == 0){
                    JOptionPane.showMessageDialog(
                            mainFrame,
                            "更新成功 !",
                            "配置结果",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    int targetRow = tableModel.getRowCount() -1;

                    tableModel.setValueAt(way,targetRow,1);
                    tableModel.setValueAt(DK,targetRow,2);
                    tableModel.setValueAt(camera_IP,targetRow,3);
                    tableModel.setValueAt(cameraIDInputBox.getText(),targetRow,4);

                }else {
                    JOptionPane.showMessageDialog(
                            mainFrame,
                            "更新失败，请重新配置 !",
                            "配置结果",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
                jDialog_updateRow.dispose();
            }
        });
        //DS-2CD2T25FD-I8
        model01Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelPanel.setVisible(false);
                cameraOverlay_update.setVisible(true);
                currentModelValue.setText(model01Button.getText());
                jDialog_updateRow.setContentPane(cameraOverlay_update);

            }
        });

        //DS-2CD2T10-I5
        model02Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelPanel.setVisible(false);
                cameraOverlay_update.setVisible(true);
                currentModelValue.setText(model02Button.getText());
                jDialog_updateRow.setContentPane(cameraOverlay_update);

            }
        });
        jDialog_updateRow.setContentPane(cameraOverlay_update);
        jDialog_updateRow.setVisible(true);

    }

    /**
     * create camera dialog
     * @return the camera configuration dialog
     */
    public JDialog createcameraDialog(final DefaultTableModel tableModel){

        //prepare the camera create dialog
        final JDialog camerajDialog_create = new JDialog(mainFrame,"摄像头 配置页面",true);
        camerajDialog_create.setSize(450,460);
        camerajDialog_create.setLocationRelativeTo(mainFrame);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
//        Image icon = kit.getImage(System.getProperty("user.dir")+ "\\icon\\logo.png");
        camerajDialog_create.setIconImage(icon);

        //select camera model first, then to config the camera
        final JPanel modelPanel = new JPanel(null);
        modelPanel.setBorder((BorderFactory.createTitledBorder("摄像头 配置")));

        JLabel selectModelLabel = new JLabel("请先选择摄像头型号 :");
        selectModelLabel.setLocation(10,30);
        selectModelLabel.setSize(300,40);
        selectModelLabel.setFont(new Font(null, Font.BOLD, 19));
        modelPanel.add(selectModelLabel);

        final JButton model01Button = new JButton("DS-2CD2T25FD-I8");
        model01Button.setLocation(130,100);
        model01Button.setSize(180,50);
        model01Button.setFont(new Font(null, Font.BOLD, 17));
        modelPanel.add(model01Button);

        final JButton model02Button = new JButton("DS-2CD2T10-I5");
        model02Button.setLocation(130,170);
        model02Button.setSize(180,50);
        model02Button.setFont(new Font(null, Font.BOLD, 17));
        modelPanel.add(model02Button);

        JButton model03Button = new JButton("型号3");
        model03Button.setLocation(130,240);
        model03Button.setSize(180,50);
        model03Button.setFont(new Font(null, Font.BOLD, 17));
        modelPanel.add(model03Button);



        //prepare the context panel
        final JPanel cameraOverlay_create = new JPanel(null);
        cameraOverlay_create.setBorder((BorderFactory.createTitledBorder("摄像头 配置")));

        final JLabel currentModel = new JLabel("当前设备型号 : ");
        currentModel.setLocation(20,30);
        currentModel.setSize(135,30);
        currentModel.setFont(new Font(null,Font.BOLD,16));
        cameraOverlay_create.add(currentModel);

        final JLabel currentModelValue = new JLabel();
        currentModelValue.setLocation(160,30);
        currentModelValue.setSize(200,30);
        currentModelValue.setFont(new Font(null,Font.BOLD,16));
        cameraOverlay_create.add(currentModelValue);

        //setup way label
        final String[] ways = new String[]{"左线","右线"};
        JLabel wayLabel = new JLabel("设定线路 :",SwingConstants.LEFT);
        wayLabel.setFont(new Font(null, 1, 16));
        wayLabel.setLocation(20,80);
        wayLabel.setSize(135,30);
        cameraOverlay_create.add(wayLabel);

        //setup way combo box
        final JComboBox<String> wayComboBox = new JComboBox<String>(ways);
        wayComboBox.setLocation(160,80);
        wayComboBox.setSize(200,30);
        wayComboBox.setFont(new Font(null, 1, 16));
        wayComboBox.setSelectedIndex(0);
        cameraOverlay_create.add(wayComboBox);

        //setup the specific position label
        JLabel specificPosition = new JLabel("具体位置 :",SwingConstants.LEFT);
        specificPosition.setFont(new Font(null, 1, 16));
        specificPosition.setLocation(20,130);
        specificPosition.setSize(135,30);
        cameraOverlay_create.add(specificPosition);

        //setup the specific position TextField
        final JTextField DKText = new JTextField(SwingConstants.RIGHT);
        DKText.setLocation(160,130);
        DKText.setSize(200,30);
        DKText.setFont(new Font(null,1,16));
        cameraOverlay_create.add(DKText);

        //setup the max length of DK input box
        LimitedDocument ld = new LimitedDocument(5);
        ld.setAllowChar("0123456789");
        DKText.setDocument(ld);

        //setup camera hanging IP label:
        JLabel cameraIPLabel = new JLabel("摄像头 IP 地址 :",SwingConstants.LEFT);
        cameraIPLabel.setFont(new Font(null, 1, 16));
        cameraIPLabel.setLocation(20,180);
        cameraIPLabel.setSize(135,30);
        cameraOverlay_create.add(cameraIPLabel);

        //setup camera hanging IP text field
        final JMIPV4AddressField IP = new JMIPV4AddressField();
        IP.setIpAddress("192.168.1.64");
        IP.setFont(new Font(null, Font.PLAIN, 14));
        IP.setLocation(160,180);
        IP.setSize(200,30);
        cameraOverlay_create.add(IP);

        //setup camera ID label:
        JLabel cameraIDLabel = new JLabel("设备 ID :",SwingConstants.LEFT);
        cameraIDLabel.setFont(new Font(null, 1, 16));
        cameraIDLabel.setLocation(20,230);
        cameraIDLabel.setSize(135,30);
        cameraOverlay_create.add(cameraIDLabel);

        //setup camera ID text field
        final JTextField cameraIDInputBox = new JTextField(SwingConstants.RIGHT);
        cameraIDInputBox.setFont(new Font(null, Font.PLAIN, 14));
        cameraIDInputBox.setLocation(160,230);
        cameraIDInputBox.setSize(200,30);
        cameraOverlay_create.add(cameraIDInputBox);

        //setup config button
        final JButton cameraConfigButton = new JButton("配置摄像头");
        cameraConfigButton.setFont(new Font(null,Font.BOLD,16));
        cameraConfigButton.setLocation(50,320);
        cameraConfigButton.setSize(125,40);
        cameraOverlay_create.add(cameraConfigButton);

        final JButton backward_camera = new JButton("返回");
        backward_camera.setFont(new Font(null,Font.BOLD,16));
        backward_camera.setLocation(270,320);
        backward_camera.setSize(125,40);
        cameraOverlay_create.add(backward_camera);

        backward_camera.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelPanel.setVisible(true);
                cameraOverlay_create.setVisible(false);
                camerajDialog_create.setContentPane(modelPanel);

            }
        });

        //add event listener to config button
        cameraConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int progress = 0;
                String way = wayComboBox.getSelectedItem().toString();
                String DK = DKText.getText();
                String camera_IP = IP.getText();
                String cameraModel = currentModelValue.getText();

                //TODO: validate camera ip is duplicate or not
                //TODO: 1.read config records from excel; 2.list out camera ip and iterate the ip

                String path = "D:\\ConfigFile\\camera\\"+pName +".xlsx";
                importFromExcel(null,path);
                for (int i = 0; i < ipList.size(); i++) {
                    if (camera_IP.trim().equals(ipList.get(i))){
                        JOptionPane.showMessageDialog(
                                mainFrame,
                                "IP 地址重复，请填写正确的 IP !",
                                "提示",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        return;
                    }

                }

                log.info("camera ip is "+camera_IP);
                log.info("camera net mask is "+commonFields.get(2));
                log.info("camera gateway ip is "+commonFields.get(1));
                log.info("camera server ip is "+commonFields.get(0));
                log.info("camera id is "+cameraIDInputBox.getText());
                log.info("camera model is "+cameraModel);

                //String cameraIP,String cameraNetMask,String cameraGatewayIP,String serverIP,String deviceID
                //create
                progress = new CameraConfig().config(camera_IP,commonFields.get(2),commonFields.get(1),commonFields.get(0),cameraIDInputBox.getText(),null,cameraModel);
                if (progress == 1){
                    JOptionPane.showMessageDialog(
                            mainFrame,
                            "配置成功 !",
                            "配置结果",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    Vector emptyRow = new Vector();
                    for (int i = 0; i < 10; i++) {
                        emptyRow.add(null);
                    }
                    tableModel_camera.addRow(emptyRow);
                    int targetRow = tableModel.getRowCount() -1;
                    tableModel.setValueAt(way,targetRow,1);
                    tableModel.setValueAt(DK,targetRow,2);
                    tableModel.setValueAt(camera_IP,targetRow,3);
                    tableModel.setValueAt(cameraIDInputBox.getText(),targetRow,4);
                    tableModel.setValueAt(currentModelValue.getText(),targetRow,5);

                    if (recordIndex == 0){
                        tableModel_camera.setValueAt(recordIndex++,tableModel_camera.getRowCount()-1,0);
                    }else {
                        recordIndex = tableModel_camera.getRowCount();
                        tableModel_camera.setValueAt(recordIndex++,tableModel_camera.getRowCount()-1,0);
                    }
                    //todo: write the version nubmer to the record table
                    //todo: get the version number from the page --> cannot do that
                }else {
                    JOptionPane.showMessageDialog(
                            mainFrame,
                            "配置失败，请重新配置 !",
                            "配置结果",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
                camerajDialog_create.dispose();
            }
        });

        //DS-2CD2T25FD-I8
        model01Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelPanel.setVisible(false);
                cameraOverlay_create.setVisible(true);
                currentModelValue.setText(model01Button.getText());
                camerajDialog_create.setContentPane(cameraOverlay_create);

            }
        });

        //DS-2CD2T10-I5
        model02Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelPanel.setVisible(false);
                cameraOverlay_create.setVisible(true);
                currentModelValue.setText(model02Button.getText());
                camerajDialog_create.setContentPane(cameraOverlay_create);

            }
        });
        camerajDialog_create.setContentPane(modelPanel);
        camerajDialog_create.setVisible(true);
        return camerajDialog_create;
    }

    /**
     * Export data to excel.
     * @param tableModel the default table model
     * @param path the excel path which write to
     * @param columnLength the column length
     */
    public void exportToExcel(DefaultTableModel tableModel,String path,int columnLength){

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        XSSFRow row = sheet.createRow(0);

        //export column name first.
        //TODO: need concern the talbe header exist or not, if the table already exist then no need to export again.
        for (int k = 0; k < columnLength; k++) {
            XSSFCell cell = row.createCell(k);
            if (tableModel != null){
                cell.setCellValue(tableModel.getColumnName(k));
            }
            try {
                FileOutputStream fileOut = new FileOutputStream(path);
                wb.write(fileOut);
                fileOut.flush();
                fileOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //then export the table content row from the second row.
        if (tableModel != null){
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                row = sheet.createRow(i+1);

                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    XSSFCell cell = row.createCell(j);
                    try {
                        if (tableModel.getValueAt(i,j) != null){
                            cell.setCellValue(tableModel.getValueAt(i,j).toString());
                            FileOutputStream fileOut = new FileOutputStream(path);
                            wb.write(fileOut);
                            fileOut.flush();
                            fileOut.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }else {
            for (int i = 0; i < 1; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < commonFields.size(); j++) {
                    XSSFCell cell = row.createCell(j);
                    cell.setCellValue(commonFields.get(j));
                    FileOutputStream fileOut = null;
                    try {
                        fileOut = new FileOutputStream(path);
                        wb.write(fileOut);
                        fileOut.flush();
                        fileOut.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    /**
     * import excel data to JTable
     * @param tableModel  the target table model
     * @return all data vector
     */
    public Vector<String> importFromExcel(DefaultTableModel tableModel,String path){
        Vector<String> readFromExcel = null;
        ipList = new ArrayList<String>();
        try {
            FileInputStream excelFile = new FileInputStream(path);
            if(excelFile != null){
                XSSFWorkbook wb = new XSSFWorkbook(excelFile);
                XSSFSheet sheet = wb.getSheet("Sheet0");
                int lastRowIndex = sheet.getLastRowNum();
                for (int i = (tableModel == null ? 0 : 1); i <= lastRowIndex ; i++) {
                    readFromExcel = new Vector<String>();
                    XSSFRow row  = sheet.getRow(i);
                    if (row == null) { break; }
                    short lastCellNum = row.getLastCellNum();
                    for (int j = 0; j < lastCellNum; j++) {
                        String cellValue = row.getCell(j).getStringCellValue();
                        readFromExcel.add(cellValue);
                        if (j == 3){
                            ipList.add(cellValue);
                        }
                    }
                    if (tableModel != null){
                        tableModel.addRow(readFromExcel);
                    }else {
                        commonFields.clear();
                        for (String commonField : readFromExcel) {
                            commonFields.add(commonField);
                        }
                    }
                }
            }
            excelFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  readFromExcel;

    }
}
