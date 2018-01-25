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
    final String[] columns_camera = {"编号","线路","位置","摄像头 IP"};

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

    /**
     * Show config records
     * @param pName_camera
     * @param pNumber_camera
     * @param tableModel
     * @param commonFields
     * @return
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
        cameraContainerPanel.setSize(480,550);
        cameraContainerPanel.setBorder(BorderFactory.createTitledBorder(null,"摄像头 配置记录：", TitledBorder.LEFT,TitledBorder.TOP,new Font(null,Font.BOLD,15)));
        cameraContainerPanel.setLayout(null);

        //create a JTable to record the configuration info
        final JTable jTable_camera = new JTable(tableModel_camera);
        jTable_camera.setLocation(20,10);
        jTable_camera.setSize(440,450);
        jTable_camera.setRowHeight(25);

        //setup column width
        jTable_camera.getColumn("编号").setMaxWidth(45);
        jTable_camera.getColumn("位置").setMaxWidth(80);
        jTable_camera.getColumn("线路").setMaxWidth(45);
        jTable_camera.getColumn("摄像头 IP").setPreferredWidth(30);
        jTable_camera.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        jTable_camera.setFont(new Font(null, Font.PLAIN, 15));

        //setup cell context align center
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
        jTable_camera.getColumn("编号").setCellRenderer(centerRenderer);
        jTable_camera.getColumn("位置").setCellRenderer(centerRenderer);
        jTable_camera.getColumn("线路").setCellRenderer(centerRenderer);
        jTable_camera.getColumn("摄像头 IP").setCellRenderer(centerRenderer);


        //setup camera hanging table header
        JTableHeader jTableHeader_camera = jTable_camera.getTableHeader();
        jTableHeader_camera.setLocation(20,10);
        jTableHeader_camera.setSize(440,30);
        jTableHeader_camera.setFont(new Font(null, Font.BOLD, 16));
        jTableHeader_camera.setResizingAllowed(true);
        jTableHeader_camera.setReorderingAllowed(true);

        //setup M2 table context scrollable
        final JScrollPane tablePanel_camera = new JScrollPane(jTable_camera);
        tablePanel_camera.setLocation(15,40);
        tablePanel_camera.setSize(440,450);
        cameraContainerPanel.add(tablePanel_camera);

        //setup new create button
        JButton createcamera = new JButton("新建");
        createcamera.setFont(new Font(null,Font.BOLD,14));
        createcamera.setLocation(355,500);
        createcamera.setSize(80,30);
        cameraContainerPanel.add(createcamera);

        //setup remove button
        JButton removecamera = new JButton("删除");
        removecamera.setLocation(245,500);
        removecamera.setSize(80,30);
        removecamera.setFont(new Font(null,Font.BOLD,14));
        cameraContainerPanel.add(removecamera);

        //setup export camera hanging records button
        JButton exportcameraRecords = new JButton("导出");
        exportcameraRecords.setFont(new Font(null,Font.BOLD,14));
        exportcameraRecords.setLocation(15,500);
        exportcameraRecords.setSize(80,30);
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
                Vector emptyRow = new Vector();
                for (int i = 0; i < 10; i++) {
                    emptyRow.add(null);
                }
                tableModel_camera.addRow(emptyRow);
                if (recordIndex == 0){
                    tableModel_camera.setValueAt(recordIndex++,tableModel_camera.getRowCount()-1,0);
                }else {
                    recordIndex = tableModel_camera.getRowCount();
                    tableModel_camera.setValueAt(recordIndex++,tableModel_camera.getRowCount()-1,0);
                }
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
                    for (int i = 1; i <= 3; i++) {
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
        return outermostContainerPanel;
    }

    /**
     * this method is to udpate row data
     * @param rowData the original row data
     */
    public void updatecameraDialog(List<String> rowData, final DefaultTableModel tableModel){

        final JDialog jDialog_updateRow = new JDialog(mainFrame,"更新 M2",true);
        jDialog_updateRow.setSize(450,460);
        jDialog_updateRow.setLocationRelativeTo(mainFrame);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
//        Image icon = kit.getImage(System.getProperty("user.dir")+ "\\icon\\logo.png");
        jDialog_updateRow.setIconImage(icon);

        JPanel cameraOverlay_update = new JPanel(null);
        cameraOverlay_update.setBorder((BorderFactory.createTitledBorder("摄像头 更新")));

        //setup way label
        final String[] ways = new String[]{"左线","右线"};
        JLabel wayLabel = new JLabel("设定线路 :",SwingConstants.LEFT);
        wayLabel.setFont(new Font(null, 1, 16));
        wayLabel.setLocation(20,40);
        wayLabel.setSize(135,30);
        cameraOverlay_update.add(wayLabel);

        //setup way combo box
        final JComboBox<String> wayComboBox = new JComboBox<String>(ways);
        wayComboBox.setLocation(160,40);
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
        specificPosition.setLocation(20,90);
        specificPosition.setSize(135,30);
        cameraOverlay_update.add(specificPosition);

        //setup the specific position TextField
        final JTextField DKText = new JTextField(SwingConstants.RIGHT);
        DKText.setLocation(160,90);
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
        cameraIPLabel.setLocation(20,140);
        cameraIPLabel.setSize(135,30);
        cameraOverlay_update.add(cameraIPLabel);

        //setup camera hanging IP text field
        final JMIPV4AddressField IP_camera = new JMIPV4AddressField();
        IP_camera.setIpAddress("10.1.2.1");
        IP_camera.setFont(new Font(null, Font.PLAIN, 14));
        IP_camera.setLocation(160,140);
        IP_camera.setSize(200,30);
        cameraOverlay_update.add(IP_camera);

        if (rowData.get(2).trim().equals("null")){
            IP_camera.setText(null);
        }else {
            IP_camera.setText(rowData.get(2));
        }

//        //setup camera hanging server IP label:
//        JLabel cameraIPLabel_server = new JLabel("服务器 IP 地址 :",SwingConstants.LEFT);
//        cameraIPLabel_server.setFont(new Font(null, 1, 16));
//        cameraIPLabel_server.setLocation(20,190);
//        cameraIPLabel_server.setSize(135,30);
//        cameraOverlay_update.add(cameraIPLabel_server);
//
//        //setup camera hanging server IP text field
//        final JMIPV4AddressField IP_camera_server = new JMIPV4AddressField();
//        IP_camera_server.setIpAddress("10.1.2.1");
//        IP_camera_server.setFont(new Font(null, Font.PLAIN, 14));
//        IP_camera_server.setLocation(160,190);
//        IP_camera_server.setSize(200,30);
//        cameraOverlay_update.add(IP_camera_server);
//
//        if (rowData.get(3).trim().equals("null")){
//            IP_camera_server.setText(null);
//        }else {
//            IP_camera_server.setText(rowData.get(3));
//        }

        //setup config button
        final JButton cameraUpdateButton = new JButton("摄像头");
        cameraUpdateButton.setFont(new Font(null,Font.BOLD,14));
        cameraUpdateButton.setLocation(145,310);
        cameraUpdateButton.setSize(105,30);
        cameraOverlay_update.add(cameraUpdateButton);

        cameraUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int progress = 0;
                String way = wayComboBox.getSelectedItem().toString();
                String DK = DKText.getText();
                String camera_IP = IP_camera.getText();
                int targetRow = tableModel.getRowCount() -1;

                tableModel.setValueAt(way,targetRow,1);
                tableModel.setValueAt(DK,targetRow,2);
                log.info(commonFields.get(2));
                log.info(olderIP_camera);
                log.info(commonFields.get(4));
                log.info(commonFields.get(3));
                log.info(commonFields.get(7));
                //String cameraIP,String cameraNetMask,String cameraGatewayIP,String serverIP,String deviceID
//                progress = new CameraConfig().config();
                tableModel.setValueAt(camera_IP,targetRow,3);

                if (progress == 1){
                    JOptionPane.showMessageDialog(
                            mainFrame,
                            "更新成功 !",
                            "配置结果",
                            JOptionPane.INFORMATION_MESSAGE
                    );
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
        jDialog_updateRow.setContentPane(cameraOverlay_update);
        jDialog_updateRow.setVisible(true);

    }

    /**
     * create M2 dialog
     * @return the M2 configuration dialog
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

        //prepare the context panel
        JPanel cameraOverlay_create = new JPanel(null);
        cameraOverlay_create.setBorder((BorderFactory.createTitledBorder("摄像头 配置")));

        //setup way label
        final String[] ways = new String[]{"左线","右线"};
        JLabel wayLabel = new JLabel("设定线路 :",SwingConstants.LEFT);
        wayLabel.setFont(new Font(null, 1, 16));
        wayLabel.setLocation(20,40);
        wayLabel.setSize(135,30);
        cameraOverlay_create.add(wayLabel);

        //setup way combo box
        final JComboBox<String> wayComboBox = new JComboBox<String>(ways);
        wayComboBox.setLocation(160,40);
        wayComboBox.setSize(200,30);
        wayComboBox.setFont(new Font(null, 1, 16));
        wayComboBox.setSelectedIndex(0);
        cameraOverlay_create.add(wayComboBox);

        //setup the specific position label
        JLabel specificPosition = new JLabel("具体位置 :",SwingConstants.LEFT);
        specificPosition.setFont(new Font(null, 1, 16));
        specificPosition.setLocation(20,90);
        specificPosition.setSize(135,30);
        cameraOverlay_create.add(specificPosition);

        //setup the specific position TextField
        final JTextField DKText = new JTextField(SwingConstants.RIGHT);
        DKText.setLocation(160,90);
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
        cameraIPLabel.setLocation(20,140);
        cameraIPLabel.setSize(135,30);
        cameraOverlay_create.add(cameraIPLabel);

        //setup camera hanging IP text field
        final JMIPV4AddressField IP = new JMIPV4AddressField();
        IP.setIpAddress("10.1.2.1");
        IP.setFont(new Font(null, Font.PLAIN, 14));
        IP.setLocation(160,140);
        IP.setSize(200,30);
        cameraOverlay_create.add(IP);

//        //setup camera hanging server IP label:
//        JLabel cameraIPLabel_server = new JLabel("服务器 IP 地址 :",SwingConstants.LEFT);
//        cameraIPLabel_server.setFont(new Font(null, 1, 16));
//        cameraIPLabel_server.setLocation(20,190);
//        cameraIPLabel_server.setSize(135,30);
//        cameraOverlay_create.add(cameraIPLabel_server);
//
//        //setup camera hanging server IP text field
//        final JMIPV4AddressField IP_server = new JMIPV4AddressField();
//        IP_server.setIpAddress("10.1.2.0");
//        IP_server.setFont(new Font(null, Font.PLAIN, 14));
//        IP_server.setLocation(160,190);
//        IP_server.setSize(200,30);
//        cameraOverlay_create.add(IP_server);

        //setup config button
        final JButton cameraConfigButton = new JButton("摄像头");
        cameraConfigButton.setFont(new Font(null,Font.BOLD,14));
        cameraConfigButton.setLocation(145,310);
        cameraConfigButton.setSize(105,30);
        cameraOverlay_create.add(cameraConfigButton);

        //add event listener to config button
        cameraConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int progress = 0;
                String way = wayComboBox.getSelectedItem().toString();
                String DK = DKText.getText();
                String camera_IP = IP.getText();
                int targetRow = tableModel.getRowCount() -1;

                tableModel.setValueAt(way,targetRow,1);
                tableModel.setValueAt(DK,targetRow,2);
                tableModel.setValueAt(camera_IP,targetRow,3);

                log.info(commonFields.get(2));
                log.info(camera_IP);
                log.info(commonFields.get(4));
                log.info(commonFields.get(3));
                log.info(commonFields.get(7));
                //String cameraIP,String cameraNetMask,String cameraGatewayIP,String serverIP,String deviceID
//                progress = new CameraConfig().config(camera_IP,);

                if (progress == 1){
                    JOptionPane.showMessageDialog(
                            mainFrame,
                            "配置成功 !",
                            "配置结果",
                            JOptionPane.INFORMATION_MESSAGE
                    );
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

        camerajDialog_create.setContentPane(cameraOverlay_create);
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
