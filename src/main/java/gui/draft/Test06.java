package gui.draft;

import gui.main.UBNT_GUI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utility.Constant;

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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static java.awt.Font.BOLD;

/**
 * @Author by XuLiang
 * @Date 2017/12/29 13:56
 * @Email stanxu526@gmail.com
 */
public class Test06 {
    private final static Log log = LogFactory.getLog(UBNT_GUI.class);
    //the container
    private JFrame jFrame = new JFrame("配置界面");
    private String projectId;
    private List<String> commonFields = new ArrayList<String>();
    //the project item should be added dynamically
    final Vector<String> projects = new Vector<String>();

    private String[] labelName = {"IP 地址 :","频率(MHz) :","Mac 地址 :"};
    private static String M2_IP,M5_AP_IP,M5_AP_Fruq,M5_AP_Mac,M5_ST_IP,position;
    //table page panel
    private JPanel outPanel = new JPanel(null);
    private JScrollPane tablePanel;

    private int realLength;
    private JButton jButton = new JButton("M2");
    private DefaultTableModel defautTableModelOut;
    private final JDialog jDialog = new JDialog();
    private int recordIndex = 1;
    private int rowNum;

    private XSSFSheet ExcelWSheet;
    private XSSFWorkbook ExcelWBook;
    private XSSFCell Cell;
    private XSSFRow Row;


    /**
     * to show the homepage
     */
    public  void show() {
        //setup container's size and location
        jFrame.setSize(1050, 600);
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

        String[] columns = {"编号","位置","DK","M2 IP", "M5_Ap IP", "M5_AP 频率", "M5_AP mac地址", "M5_ST IP","M5_ST 锁定mac地址"};

        final DefaultTableModel defautTableModel = new DefaultTableModel(null,columns){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        //add combo box item changed listener
        projectListComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String selectedItem = (String) e.getItem();
                //TODO: here need navigate to the main page to show the table
                homepagePanel.setVisible(false);
                SwingUtilities.updateComponentTreeUI(jFrame);
                jFrame.repaint();
                //TODO: render the row data to the outPanel, the problem is how to send the data to outPanel
                //TODO: the row data are read from excel, one excel server one project
                jFrame.setContentPane(outPanel);
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

        importFromExcel(defautTableModel);

        final JTable jTable = new JTable(defautTableModel);

        jTable.setLocation(20,60);
        jTable.setSize(950,450);
        jTable.setRowHeight(25);


        //setup column width
        jTable.getColumn("编号").setMaxWidth(45);
        jTable.getColumn("DK").setMaxWidth(80);
        jTable.getColumn("位置").setMaxWidth(45);
        jTable.getColumn("M5_AP 频率").setPreferredWidth(40);
        jTable.getColumn("M2 IP").setPreferredWidth(30);
        jTable.getColumn("M5_Ap IP").setPreferredWidth(40);
        jTable.getColumn("M5_ST IP").setPreferredWidth(50);
        jTable.getColumn("M5_ST 锁定mac地址").setPreferredWidth(100);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        jTable.setFont(new Font(null, Font.PLAIN, 15));

        JTableHeader jTableHeader = jTable.getTableHeader();
        jTableHeader.setLocation(20,30);
        jTableHeader.setSize(950,30);
        jTableHeader.setFont(new Font(null, Font.BOLD, 16));
        jTableHeader.setResizingAllowed(true);
        jTableHeader.setReorderingAllowed(true);

        tablePanel = new JScrollPane(jTable);
        tablePanel.setLocation(10,10);
        tablePanel.setSize(960,400);

        outPanel.add(tablePanel);
        JButton export = new JButton("导出数据");
        export.setFont(new Font(null,Font.BOLD,14));
        export.setLocation(40,450);
        export.setSize(100,40);
        outPanel.add(export);

        final JButton editRow = new JButton("修改一行");
        editRow.setLocation(470,450);
        editRow.setSize(100,40);
        editRow.setFont(new Font(null,Font.BOLD,14));
        outPanel.add(editRow);

        JButton removeRow = new JButton("删除一行");
        removeRow.setLocation(590,450);
        removeRow.setSize(100,40);
        removeRow.setFont(new Font(null,Font.BOLD,14));
        outPanel.add(removeRow);

        JButton add = new JButton("选择配置");
        add.setFont(new Font(null,Font.BOLD,14));
        add.setLocation(710,450);
        add.setSize(100,40);
        outPanel.add(add);

        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //ToDo: to generate a new row with all data, but how ?  ---> generate the data frist!
                log.info("there are already "+ defautTableModel.getRowCount() + " records");
                Vector emptyRow = new Vector();
                for (int i = 0; i < 10; i++) {
                    emptyRow.add(null);
                }
                defautTableModel.addRow(emptyRow);
                if (recordIndex == 0){
                    defautTableModel.setValueAt(recordIndex++,defautTableModel.getRowCount()-1,0);
                }else {
                    recordIndex = defautTableModel.getRowCount();
                    defautTableModel.setValueAt(recordIndex++,defautTableModel.getRowCount()-1,0);
                }
                rowGenerator(defautTableModel);
            }
        });

        //the whole cell of a specific row are stored in this list.
        editRow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> cellValuesOfSpecificRow = new ArrayList<String>();
                rowNum = jTable.getSelectedRow();
                //i started from 1, cause no to edit row number.
                log.info("the " + rowNum + "th row was selected !");
                for (int i = 1; i <= 8; i++) {
                        int a = cellValuesOfSpecificRow.size();
                        log.info(a);
                    if (defautTableModel.getValueAt(rowNum,i) != null){
                        cellValuesOfSpecificRow.add(defautTableModel.getValueAt(rowNum,i).toString());
                    }else {
                        cellValuesOfSpecificRow.add("null");
                    }
                }
                editRow(cellValuesOfSpecificRow,defautTableModel);
            }
        });

        removeRow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int remove  = JOptionPane.showConfirmDialog(jFrame,"确定删除吗？");
                log.info("return int value is " + remove);
                if (remove == 0){

                    //TODO: need to change the row number, but how ?
                    int selectedRowNumber = jTable.getSelectedRow();
                    int allRowsCount = defautTableModel.getRowCount();
                    int remainingRows = allRowsCount - selectedRowNumber;
                    log.info("the remaining rows are :" + remainingRows + "; all rows : " + allRowsCount + " ;" + selectedRowNumber + " was selected!");
                    log.info(jTable.getSelectedRow() +" row was deleting");
                    for (int i = 1; i < remainingRows; i++) {
                        defautTableModel.setValueAt(selectedRowNumber + i,selectedRowNumber+i,0);
                    }
                    defautTableModel.removeRow(jTable.getSelectedRow());
                }
            }
        });

        export.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToExcel(defautTableModel);
            }
        });

        jFrame.setContentPane(homepagePanel);
        jFrame.setVisible(true);
    }

    /**
     * import excel data to JTable
     * @param defautTableModel  the target table model
     * @return all data vector
     */
    public Vector<String> importFromExcel(DefaultTableModel defautTableModel){
        Vector readFromExcel = null;
        try {
            FileInputStream ExcelFile = new FileInputStream(Constant.Path_TestData_Output);
            if(ExcelFile != null){
                XSSFWorkbook wb = new XSSFWorkbook(ExcelFile);
                XSSFSheet sheet = wb.getSheet("Sheet0");
                int lastRowIndex = sheet.getLastRowNum();
                BreakAllForLooplabel:
                for (int i = 1; i <= lastRowIndex ; i++) {
                    readFromExcel = new Vector();
                    Row  = sheet.getRow(i);
                    if (Row == null) { break; }
                    short lastCellNum = Row.getLastCellNum();
                    for (int j = 0; j < lastCellNum; j++) {
                        String cellValue = Row.getCell(j).getStringCellValue();
                        readFromExcel.add(cellValue);
                    }
                    defautTableModel.addRow(readFromExcel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  readFromExcel;

    }

    /**
     * export row data to excel
     */
    public void exportToExcel(DefaultTableModel defautTableModel){

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        Row = sheet.createRow(0);

        //export column name first.
        //TODO: need concern the talbe header exist or not, if the table already exist then no need to export again.
        for (int k = 0; k < 9; k++) {
            Cell = Row.createCell(k);
            Cell.setCellValue(defautTableModel.getColumnName(k));
            try {
                FileOutputStream fileOut = new FileOutputStream(Constant.Path_TestData_Output);
                wb.write(fileOut);
                fileOut.flush();
                fileOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //then export the table content row from the second row.
        for (int i = 0; i < defautTableModel.getRowCount(); i++) {
            Row = sheet.createRow(i+1);

            for (int j = 0; j < defautTableModel.getColumnCount(); j++) {
                Cell = Row.createCell(j);
                try {
                    if (defautTableModel.getValueAt(i,j) != null){
                        Cell.setCellValue(defautTableModel.getValueAt(i,j).toString());
                        FileOutputStream fileOut = new FileOutputStream(Constant.Path_TestData_Output);
                        wb.write(fileOut);
                        fileOut.flush();
                        fileOut.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * this method is to udpate row data
     * @param rowData the original row data
     */
    public void editRow(List<String> rowData, DefaultTableModel defautTableModel){
        final JDialog jDialog = new JDialog();
        jDialog.setSize(500,500);
        jDialog.setLocationRelativeTo(jFrame);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
        jDialog.setIconImage(icon);

        //setup JTabbedPane
        final JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.setFont(new Font("ITALIC", 1, 16));
        jTabbedPane.add("位置",updateOverlayPanel("位置",rowData,defautTableModel));
        jTabbedPane.add("M2",updateOverlayPanel("M2",rowData,defautTableModel));
        jTabbedPane.add("M5_AP",updateOverlayPanel("M5_AP",rowData,defautTableModel));
        jTabbedPane.add("M5_ST",updateOverlayPanel("M5_ST",rowData,defautTableModel));

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
        updateOverlayPanel("位置",null,defautTableModel);
        jDialog.setContentPane(jTabbedPane);
        jDialog.setVisible(true);

    }



    /**
     * generate rows
     */
    public void rowGenerator(DefaultTableModel defautTableModel){
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
        jTabbedPane.add("位置",createTextPanelOverlay("位置",defautTableModel));
        jTabbedPane.add("M2",createTextPanelOverlay("M2",defautTableModel));
        jTabbedPane.add("M5_AP",createTextPanelOverlay("M5_AP",defautTableModel));
        jTabbedPane.add("M5_ST",createTextPanelOverlay("M5_ST",defautTableModel));
//        jTabbedPane.add("记录数据",createTextPanelOverlay("记录数据"));

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
//        createTextPanelOverlay("M2");
        createTextPanelOverlay("位置",defautTableModel);
        jDialog.setContentPane(jTabbedPane);
        jDialog.setVisible(true);

    }

    /**
     * update row overlay panel
     * @param tabName the tab name
     * @param rowData the row data
     * @param defautTableModel the table model
     * @return  the update row overlay panel
     */
    private JPanel updateOverlayPanel(String tabName,List<String> rowData,final DefaultTableModel defautTableModel){
        JPanel jPanel = new JPanel(null);
        jPanel.setBorder((BorderFactory.createTitledBorder("UBNT( "+tabName+") 配置")));
        jPanel.setLayout(null);

        JTextField inputBoxes = null;
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
            if (rowData != null && rowData.get(0).trim().equals("右线")){
                jComboBox.setSelectedIndex(1);
            }else {
                jComboBox.setSelectedIndex(0);
            }

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

            if (rowData != null && rowData.get(1).contains("+")){
                String originalKM = rowData.get(1).substring(0,rowData.get(1).indexOf("+")-1);
                String originalMeter = rowData.get(1).substring(rowData.get(1).indexOf("+")+1,rowData.get(1).length());
                KM.setText(originalKM.trim());
                meter.setText(originalMeter.trim());
                log.info("the original km is " + originalKM.trim() + ", meter is " + originalMeter.trim());
            }else {
                KM.setText(null);
                meter.setText(null);
            }

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

                    if (tabName.trim().equals("M5_AP")){
                        if (rowData.get(i+2).trim().equals("null")){
                            inputBoxes.setText(null);
                        }else {
                            inputBoxes.setText(rowData.get(i+2));
                        }
                    }
                    if (tabName.trim().equals("M5_ST")){
                        if (rowData.get(i+5).trim().equals("null")){
                            inputBoxes.setText(null);
                        }else {
                            inputBoxes.setText(rowData.get(i+5));
                        }
                    }

                    jPanel.add(inputBoxes);
                    jTextFields.add(inputBoxes);
                }else {
                    //TODO: implements IP input text field
                    JMIPV4AddressField IPTextField = new JMIPV4AddressField();

                    if (rowData !=null){
                        if (tabName.trim().equals("M5_AP")){
                            if (rowData.get(i+2).trim().equals("null")){
                                IPTextField.setText(null);
                            }else {
                                IPTextField.setText(rowData.get(i+2));
                            }
                        }
                        if (tabName.trim().equals("M5_ST")){
                            if (rowData.get(i+5).trim().equals("null")){
                                IPTextField.setText(null);
                            }else {
                                IPTextField.setText(rowData.get(i+5));
                            }
                        }
                        if (tabName.trim().equals("M2")){
                            if (rowData.get(i+4).trim().equals("null")){
                                IPTextField.setText(null);
                            }else {
                                IPTextField.setText(rowData.get(i+1));
                            }
                        }
                    }
                    IPTextField.setFont(new Font(null, Font.PLAIN, 14));
                    IPTextField.setLocation(120,40*i);
                    IPTextField.setSize(200,30);
                    jPanel.add(IPTextField);
                    jTextFields.add(IPTextField);
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
                position = jComboBox.getSelectedItem().toString();
                String DK = KM.getText() + " + " + meter.getText();

                int progress = 0;
                if (buttonText.trim().equals("M2")){
                    M2_IP = jTextFields.get(0).getText();
                    log.info("M2_IP is " + M2_IP);
//                    cellValue.add(M2_IP);
                    int targetRow = defautTableModel.getRowCount();
                    log.info("the real row count is :" + targetRow);
                    defautTableModel.setValueAt(M2_IP,rowNum,3);

                    //ssid = commonFields.get(1); netmask = commonFields.get(3); gatewayIP = commonFields.get(2);
//                    M2_Configuration.configM2(commonFields.get(1),M2_IP,commonFields.get(3),commonFields.get(2));
                }
                else if (buttonText.trim().equals("M5_AP")){
                    M5_AP_IP = jTextFields.get(0).getText();
                    M5_AP_Fruq = jTextFields.get(1).getText();
                    log.info("M5_AP_IP is " + M5_AP_IP);
                    log.info("M5_AP_Fruq is " + M5_AP_Fruq);
                    defautTableModel.setValueAt(M5_AP_IP,rowNum,4);
                    defautTableModel.setValueAt(M5_AP_Fruq,rowNum,5);
                    //M5_Configuration.configM5("AP",commonFields.get(1),M5_AP_IP,commonFields.get(3),commonFields.get(2),M5_AP_Fruq,null);
                }
                else if (buttonText.trim().equals("M5_ST")){
                    M5_ST_IP = jTextFields.get(0).getText();
                    M5_AP_Mac = jTextFields.get(1).getText();
                    log.info("M5_ST_IP is " + M5_ST_IP);
                    log.info("M5_AP_Mac is " + M5_AP_Mac);

                    defautTableModel.setValueAt(M5_AP_Mac,rowNum,6);
                    defautTableModel.setValueAt(M5_ST_IP,rowNum,7);
                    defautTableModel.setValueAt(M5_AP_Mac,rowNum,8);
                    //M5_Configuration.configM5("ST",commonFields.get(1),M5_ST_IP,commonFields.get(3),commonFields.get(2),null,M5_AP_Mac);
                }else if (buttonText.trim().equals("位置")){
                    defautTableModel.setValueAt(position,rowNum,1);
                    defautTableModel.setValueAt(DK,rowNum,2);
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


    /**
     * the final config overlay
     * @param tabName  the tab name
     * @return  the overlay panel content
     */
    private JPanel createTextPanelOverlay(String tabName, final DefaultTableModel defautTableModel){
        JPanel jPanel = new JPanel(null);
        jPanel.setBorder((BorderFactory.createTitledBorder("UBNT( "+tabName+") 配置")));
        jPanel.setLayout(null);

        JTextField inputBoxes = null;
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
                position = jComboBox.getSelectedItem().toString();
                String DK = KM.getText() + " + " + meter.getText();

                int progress = 0;
                if (buttonText.trim().equals("M2")){
                    M2_IP = jTextFields.get(0).getText();
                    log.info("M2_IP is " + M2_IP);
//                    cellValue.add(M2_IP);
                    int targetRow = defautTableModel.getRowCount();
                    log.info("the real row count is :" + targetRow);
                    defautTableModel.setValueAt(M2_IP,targetRow-1,3);

                    //ssid = commonFields.get(1); netmask = commonFields.get(3); gatewayIP = commonFields.get(2);
//                    M2_Configuration.configM2(commonFields.get(1),M2_IP,commonFields.get(3),commonFields.get(2));
                }
                else if (buttonText.trim().equals("M5_AP")){
                    M5_AP_IP = jTextFields.get(0).getText();
                    M5_AP_Fruq = jTextFields.get(1).getText();
                    log.info("M5_AP_IP is " + M5_AP_IP);
                    log.info("M5_AP_Fruq is " + M5_AP_Fruq);
                    defautTableModel.setValueAt(M5_AP_IP,defautTableModel.getRowCount()-1,4);
                    defautTableModel.setValueAt(M5_AP_Fruq,defautTableModel.getRowCount()-1,5);
                    //M5_Configuration.configM5("AP",commonFields.get(1),M5_AP_IP,commonFields.get(3),commonFields.get(2),M5_AP_Fruq,null);
                }
                else if (buttonText.trim().equals("M5_ST")){
                    M5_ST_IP = jTextFields.get(0).getText();
                    M5_AP_Mac = jTextFields.get(1).getText();
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
        ssidInputBox.setFont(new Font(null, Font.PLAIN, 18));
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
//        final JTextField gatewayIPInputBox = new JTextField();  // I need the input text, it will be used later
        final JTextField gatewayIPInputBox = new JMIPV4AddressField();  // I need the input text, it will be used later
        gatewayIP.setLocation(60,180);
        gatewayIP.setSize(120,40);
        gatewayIP.setFont(new Font(null, 1, 18));
        gatewayIPInputBox.setLocation(190,180);
        gatewayIPInputBox.setSize(180,40);
        gatewayIPInputBox.setFont(new Font(null, Font.PLAIN, 18));
        jPanel.add(gatewayIP);
        jPanel.add(gatewayIPInputBox);

        //net mask label and corresponding text field
        JLabel netMaskLabel = new JLabel("子网掩码 :");
        final JTextField netMaskInputBox = new JMIPV4AddressField();
        netMaskLabel.setLocation(60,230);
        netMaskLabel.setSize(120,40);
        netMaskLabel.setFont(new Font(null, BOLD,18));
        netMaskInputBox.setLocation(190,230);
        netMaskInputBox.setSize(180,40);
        netMaskInputBox.setFont(new Font(null, Font.PLAIN, 18));
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
        final JTextField gatewayIP_M5_InputBox = new JMIPV4AddressField();  // I need the input text, it will be used later
        gatewayIP_M5.setLocation(60,340);
        gatewayIP_M5.setSize(120,40);
        gatewayIP_M5.setFont(new Font(null, 1, 18));
        gatewayIP_M5_InputBox.setLocation(190,340);
        gatewayIP_M5_InputBox.setSize(180,40);
        gatewayIP_M5_InputBox.setFont(new Font(null, Font.PLAIN, 18));
        jPanel.add(gatewayIP_M5);
        jPanel.add(gatewayIP_M5_InputBox);

        //M5 bridge net mask label and corresponding text field
        JLabel netMaskLabel_M5 = new JLabel("子网掩码 :");
        final JTextField netMask_M5_InputBox = new JMIPV4AddressField();
        netMaskLabel_M5.setLocation(60,390);
        netMaskLabel_M5.setSize(120,40);
        netMaskLabel_M5.setFont(new Font(null, BOLD,18));
        netMask_M5_InputBox.setLocation(190,390);
        netMask_M5_InputBox.setSize(180,40);
        netMask_M5_InputBox.setFont(new Font(null, Font.PLAIN, 18));
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
                commonFields.add(gatewayIPInputBox.getText());
                commonFields.add(netMaskInputBox.getText());
                commonFields.add(gatewayIP_M5_InputBox.getText());
                commonFields.add(netMask_M5_InputBox.getText());
                log.info("the project id is "+netMask_M5_InputBox.getText());
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
