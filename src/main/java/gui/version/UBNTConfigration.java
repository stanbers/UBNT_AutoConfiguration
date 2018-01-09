package gui.version;

import utility.JMIPV4AddressField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ubnt.m2.M2_Configuration;
import ubnt.m5.M5_Configuration;
import utility.Constant;
import utility.LimitedDocument;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
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
public class UBNTConfigration {
    private final static Log log = LogFactory.getLog(UBNTConfigration.class);
    //the container
    private JFrame jFrame = new JFrame("配置界面");
    private List<String> commonFields = new ArrayList<String>();

    private String[] labelName = {"IP 地址 :","频率(MHz) :","Mac 地址 :"};
    private static String M2_IP,M5_AP_IP,M5_AP_Fruq,M5_AP_Mac,M5_ST_IP,position;
    //table page panel
    private JPanel outPanel = new JPanel(null);
    private JScrollPane tablePanel;

    private int realLength;
    private JButton jButton = new JButton("M2");
    private final JDialog jDialog = new JDialog(jFrame,"提示信息",true);
    private int recordIndex = 1;
    private int rowNum;
    private String pName;
    private String pNumber;

    private XSSFCell Cell;
    private XSSFRow Row;
    private final String[] fruqs = new String[]{"5820","5840","5860","5880","5900","5920"};


    /**
     * show the homepage
     */
    public void show() {
        //setup container's size and location
        jFrame.setSize(1050, 600);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
//        Image icon = kit.getImage(System.getProperty("user.dir")+"\\icon\\logo.png");
        Image icon = kit.getImage("D:\\icon\\logo.png");
        jFrame.setIconImage(icon);

        //create the homepage panel
        final JPanel homepagePanel = new JPanel(null);

        //initialize project table header
        String[] projectHeader = {"项目编号","项目名称"};

        final DefaultTableModel projectTableModel = new DefaultTableModel(null,projectHeader){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JLabel titleLabel = new JLabel("中继自动化配置",SwingConstants.CENTER);
        titleLabel.setSize(1050,100);
        titleLabel.setFont(new Font(null,Font.BOLD, 60));
        homepagePanel.add(titleLabel);


        JLabel projectListLabel = new JLabel("项目列表：");
        projectListLabel.setLocation(90,105);
        projectListLabel.setSize(200,40);
        projectListLabel.setFont(new Font(null,BOLD,20));

        //store table records
        final JTable projectTable = new JTable(projectTableModel);
        projectTable.setLocation(100,160);
        projectTable.setSize(300,350);
        projectTable.setFont(new Font(null,Font.PLAIN,16));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
        //set up project table header
        projectTable.getColumn("项目编号").setPreferredWidth(130);
        projectTable.getColumn("项目名称").setPreferredWidth(220);
        projectTable.getColumn("项目编号").setCellRenderer(centerRenderer);
        projectTable.getColumn("项目名称").setCellRenderer(centerRenderer);
        projectTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        projectTable.setFont(new Font(null, Font.PLAIN, 18));
        projectTable.setRowHeight(40);

        JTableHeader projectjTableHeader = projectTable.getTableHeader();
        projectjTableHeader.setLocation(100,100);
        projectjTableHeader.setPreferredSize(new Dimension(300,50));
        projectjTableHeader.setFont(new Font(null, Font.BOLD, 16));
        projectjTableHeader.setResizingAllowed(true);
        projectjTableHeader.setReorderingAllowed(true);


        //project container
        JScrollPane projectTableContainer = new JScrollPane(projectTable);
        projectTableContainer.setLocation(90,150);
        projectTableContainer.setSize(400,300);

        //import projects from project_list excel file, and render projects to projectTable
        importFromExcel(projectTableModel,Constant.Path_TestData_ProjectList);

        //initialize table header
        String[] columns = {"编号","线路","位置","M2 IP", "M5_Ap IP", "M5_AP 频率", "M5_AP mac地址", "M5_ST IP","M5_ST 锁定mac地址"};

        final DefaultTableModel defautTableModel = new DefaultTableModel(null,columns){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        final JLabel currentPNumber = new JLabel(pNumber);
        currentPNumber.setLocation(150,10);
        currentPNumber.setSize(80,40);
        currentPNumber.setFont(new Font(null,Font.BOLD,18));


        final JLabel currentPName = new JLabel(pName);
        currentPName.setLocation(370,10);
        currentPName.setSize(80,40);
        currentPName.setFont(new Font(null,Font.BOLD,18));


        //TODO: double click the project record, navigate to the main page
        projectTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){
                    homepagePanel.setVisible(false);
//                    SwingUtilities.updateComponentTreeUI(jFrame);
//                    jFrame.repaint();
                    //TODO: render the row data to the outPanel, the problem is how to send the data to outPanel
                    //TODO: the row data are read from excel, one excel server one project
                    jFrame.setContentPane(outPanel);
                    int targetRow = projectTable.getSelectedRow();
                    if (targetRow >= 0){
                        pNumber = projectTableModel.getValueAt(targetRow,0).toString();
                        pName = projectTableModel.getValueAt(targetRow,1).toString();
                        //TODO: to show the project config records based on the project name or number
                        //TODO: need import the config records excel under this project, but how ? --> done
                        //TODO: so it's better to rename the config excel with project name or number when it was generated.
                        //if (can find the specific excel which's name is same with project name or number')
                        //need to generate the project excel first, otherwise the following code could not find this specific excel
                        String specificExcel = "D:\\ConfigFile\\"+pName +".xlsx";
//                        String specificExcel = System.getProperty("user.dir")+ "\\ConfigFile\\"+pName +".xlsx";
                        String SpecificProjectCommonField = "D:\\ConfigFile\\"+pName +"CommonFields.xlsx";
//                        String SpecificProjectCommonField = System.getProperty("user.dir")+ "\\ConfigFile\\"+pName +"CommonFields.xlsx";
                        File projectCorresspondingConfigFile = new File(specificExcel);
                        File projectCommonFieldFile = new File(SpecificProjectCommonField);
                        if (!projectCorresspondingConfigFile.exists()){
                            exportToExcel(defautTableModel,"D:\\ConfigFile\\"+pName+".xlsx",9);
//                            exportToExcel(null,System.getProperty("user.dir")+ "\\ConfigFile\\"+pName+".xlsx",9);
                        }
                        if (projectCorresspondingConfigFile.exists() && projectCommonFieldFile.exists()){
                            //import table rows on main page
                            importFromExcel( defautTableModel,specificExcel);
                            //import specific project common fields
                            //TODO: this time not to render table but to override commonfields.
                            importFromExcel(null,SpecificProjectCommonField);
                            //import project list excel, in order to show the project name and number on the main page
                        }

                            outPanel.add(currentPName);
                            outPanel.add(currentPNumber);
                            currentPName.setText(pName);
                            currentPNumber.setText(pNumber);

                    }else {
                        //TODO: may show waring message here.
                    }
                }
            }
        });

        //setup project button
        JButton createPojectButton = new JButton("+ 新建项目");
        createPojectButton.setLocation(700,390);
        createPojectButton.setSize(200,60);
        createPojectButton.setFont(new Font(null, BOLD, 25));

        //add button event listener
        createPojectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //to invoke new project dialog
                generateNewProjectDilog(projectTableModel);

            }
        });

        homepagePanel.add(projectListLabel);
        homepagePanel.add(projectTableContainer);
        homepagePanel.add(createPojectButton);

//        importFromExcel(defautTableModel,Constant.Path_TestData_Output);

        JLabel currentPNumberTitle = new JLabel("当前项目编号：");
        currentPNumberTitle.setLocation(20,10);
        currentPNumberTitle.setSize(160,40);
        currentPNumberTitle.setFont(new Font(null,Font.BOLD,18));
        outPanel.add(currentPNumberTitle);

        JLabel currentPNameTitle = new JLabel("当前项目名称：");
        currentPNameTitle.setLocation(240,10);
        currentPNameTitle.setSize(160,40);
        currentPNameTitle.setFont(new Font(null,Font.BOLD,18));
        outPanel.add(currentPNameTitle);

        //add backward button, go back to homepage
        JButton  backwardButton = new JButton("返回首页");
        backwardButton.setLocation(850,15);
        backwardButton.setSize(120,30);
        backwardButton.setFont(new Font(null,Font.BOLD,18));
        outPanel.add(backwardButton);

        backwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //need remove these two JLabel to make sure every time these two label are new added to outPanel
                outPanel.remove(currentPName);
                outPanel.remove(currentPNumber);
                show();
            }
        });

        final JTable jTable = new JTable(defautTableModel);

        jTable.setLocation(20,100);
        jTable.setSize(950,450);
        jTable.setRowHeight(25);

        //setup column width
        jTable.getColumn("编号").setMaxWidth(45);
        jTable.getColumn("位置").setMaxWidth(80);
        jTable.getColumn("线路").setMaxWidth(45);
        jTable.getColumn("M5_AP 频率").setPreferredWidth(40);
        jTable.getColumn("M2 IP").setPreferredWidth(30);
        jTable.getColumn("M5_Ap IP").setPreferredWidth(40);
        jTable.getColumn("M5_ST IP").setPreferredWidth(50);
        jTable.getColumn("M5_ST 锁定mac地址").setPreferredWidth(100);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        jTable.setFont(new Font(null, Font.PLAIN, 15));

        JTableHeader jTableHeader = jTable.getTableHeader();
        jTableHeader.setLocation(20,70);
        jTableHeader.setSize(950,30);
        jTableHeader.setFont(new Font(null, Font.BOLD, 16));
        jTableHeader.setResizingAllowed(true);
        jTableHeader.setReorderingAllowed(true);

        tablePanel = new JScrollPane(jTable);
        tablePanel.setLocation(10,50);
        tablePanel.setSize(960,400);

        outPanel.add(tablePanel);
        JButton export = new JButton("导出");
        export.setFont(new Font(null,Font.BOLD,14));
        export.setLocation(40,500);
        export.setSize(100,40);
        outPanel.add(export);

        JButton removeRow = new JButton("删除");
        removeRow.setLocation(590,500);
        removeRow.setSize(100,40);
        removeRow.setFont(new Font(null,Font.BOLD,14));
        outPanel.add(removeRow);

        JButton add = new JButton("新建");
        add.setFont(new Font(null,Font.BOLD,14));
        add.setLocation(710,500);
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

        //TODO: use double click table row instead of edit row button
        jTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){

                    //the whole cell of a specific row are stored in this list.
                    List<String> cellValuesOfSpecificRow = new ArrayList<String>();
                    rowNum = jTable.getSelectedRow();
                    //i started from 1, cause no to edit row number.
                    log.info("the " + rowNum + "th row was selected !");
                    for (int i = 1; i <= 8; i++) {
                        int a = cellValuesOfSpecificRow.size();
                        log.info("row has " +a +"columns");
                        if (defautTableModel.getValueAt(rowNum,i) != null){
                            cellValuesOfSpecificRow.add(defautTableModel.getValueAt(rowNum,i).toString());
                        }else {
                            cellValuesOfSpecificRow.add("null");
                        }
                    }
                    editRow(cellValuesOfSpecificRow,defautTableModel);
                }
            }
        });

        removeRow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int remove  = JOptionPane.showConfirmDialog(jFrame,"确定删除吗？");
                log.info("return int value is " + remove);
                if (remove == 0){

                    //TODO: need to change the row number, but how ? --> done
                    int selectedRowNumber = jTable.getSelectedRow();
                    int allRowsCount = defautTableModel.getRowCount();
                    int remainingRows = allRowsCount - selectedRowNumber;
                    log.info("the remaining rows are :" + remainingRows + "; all rows : " + allRowsCount + " ;" + selectedRowNumber + " was selected!");
                    log.info(jTable.getSelectedRow() +" row was deleting");
                    for (int i = 1; i < remainingRows; i++) {
                        defautTableModel.setValueAt(selectedRowNumber + i,selectedRowNumber+i,0);
                    }
                    if (jTable.getSelectedRow() >= 0){
                        defautTableModel.removeRow(jTable.getSelectedRow());
                    }else {
                        JOptionPane.showMessageDialog(jFrame,"目前没有可以被删除的记录 ！");
                    }
                }
            }
        });

        export.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToExcel(defautTableModel,"D:\\ConfigFile\\"+pName+".xlsx",9);
//                exportToExcel(defautTableModel,System.getProperty("user.dir")+ "\\ConfigFile\\"+pName+".xlsx",9);
                JOptionPane.showMessageDialog(
                        jFrame,
                        "导出数据完毕 !",
                        "配置结果",
                        JOptionPane.INFORMATION_MESSAGE
                );
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
    public Vector<String> importFromExcel(DefaultTableModel defautTableModel,String path){
        Vector<String> readFromExcel = null;
        try {
            FileInputStream excelFile = new FileInputStream(path);
            if(excelFile != null){
                XSSFWorkbook wb = new XSSFWorkbook(excelFile);
                XSSFSheet sheet = wb.getSheet("Sheet0");
                int lastRowIndex = sheet.getLastRowNum();
                for (int i = (defautTableModel == null ? 0 : 1); i <= lastRowIndex ; i++) {
                    readFromExcel = new Vector<String>();
                    Row  = sheet.getRow(i);
                    if (Row == null) { break; }
                    short lastCellNum = Row.getLastCellNum();
                    for (int j = 0; j < lastCellNum; j++) {
                        String cellValue = Row.getCell(j).getStringCellValue();
                        readFromExcel.add(cellValue);
                    }
                    if (defautTableModel != null){
                        defautTableModel.addRow(readFromExcel);
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

    /**
     * export row data to excel
     */
    public void exportToExcel(DefaultTableModel defautTableModel,String path,int columnLength){

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        Row = sheet.createRow(0);

        //export column name first.
        //TODO: need concern the talbe header exist or not, if the table already exist then no need to export again.
        for (int k = 0; k < columnLength; k++) {
            Cell = Row.createCell(k);
            if (defautTableModel != null){
                Cell.setCellValue(defautTableModel.getColumnName(k));
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
        if (defautTableModel != null){
            for (int i = 0; i < defautTableModel.getRowCount(); i++) {
                Row = sheet.createRow(i+1);

                for (int j = 0; j < defautTableModel.getColumnCount(); j++) {
                    Cell = Row.createCell(j);
                    try {
                        if (defautTableModel.getValueAt(i,j) != null){
                            Cell.setCellValue(defautTableModel.getValueAt(i,j).toString());
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
                Row = sheet.createRow(i);
                for (int j = 0; j < commonFields.size(); j++) {
                    Cell = Row.createCell(j);
                    Cell.setCellValue(commonFields.get(j));
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
     * this method is to udpate row data
     * @param rowData the original row data
     */
    public void editRow(List<String> rowData, DefaultTableModel defautTableModel){
        final JDialog jDialog = new JDialog(jFrame,"配置页面",true);
        jDialog.setSize(500,500);
        jDialog.setLocationRelativeTo(jFrame);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
//        Image icon = kit.getImage(System.getProperty("user.dir")+ "\\icon\\logo.png");
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
        final JDialog jDialog = new JDialog(jFrame,"配置页面",true);
        jDialog.setSize(500,500);
        jDialog.setLocationRelativeTo(outPanel);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
//        Image icon = kit.getImage(System.getProperty("user.dir")+ "\\icon\\logo.png");
        jDialog.setIconImage(icon);

//        JPanel jPanel = new JPanel(null);

        //setup JTabbedPane
        final JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.setFont(new Font("ITALIC", 1, 16));
//        jTabbedPane.add("位置",new CreateConfigDialog().createDialog("位置",labelName,commonFields,defautTableModel));
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
        final JTextField DKText = new JTextField(SwingConstants.RIGHT);
        final List<JTextField> jTextFields = new ArrayList<JTextField>();
        final JComboBox<String> fruqComboBox = new JComboBox<String>(fruqs);
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

//            JLabel DK = new JLabel("DK ");
//            DK.setLocation(120,90);
//            DK.setSize(30,30);
//            DK.setFont(new Font(null,1,16));

            DKText.setLocation(120,90);
            DKText.setSize(200,30);
            DKText.setFont(new Font(null,1,16));

            if (rowData != null){
                if (rowData.get(1).trim().equals("null")){
                    DKText.setText(null);
                }else {
                    String dkMiles = rowData.get(1);
                    DKText.setText(dkMiles);
                    log.info("the DK mile is " + dkMiles);
                }
            }

            //validate number input only
            DKText.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char vchar = e.getKeyChar();
                    if (!(Character.isDigit(vchar)) && (vchar != KeyEvent.VK_BACK_SPACE) && (vchar != KeyEvent.VK_DELETE)){
                        JOptionPane.showMessageDialog(
                                jFrame,
                                "请输入数字 !",
                                "DK 里数",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        e.consume();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });

            jPanel.add(way);
            jPanel.add(jComboBox);
            jPanel.add(specificPosition);
//            jPanel.add(DK);
            jPanel.add(DKText);
        }else {
            if (tabName != null && tabName.trim().equals("M2")){
                realLength = labelName.length-2;
            }
            else if (tabName != null && tabName.trim().equals("M5_AP") || tabName.trim().equals("M5_ST")){
                realLength = labelName.length-1;
            }

            for (int i = 1; i <= realLength; i++) {


//                final JComboBox fruq_update = new JComboBox(fruqs);
                if (tabName != null && i == realLength && !tabName.trim().equals("M2")){
                    if (tabName.trim().equals("M5_AP")){
                        labelName[i-1] = "频率(MHz) :";
                        fruqComboBox.setLocation(120,40*i);
                        fruqComboBox.setSize(200,30);
                        fruqComboBox.setFont(new Font(null, Font.PLAIN, 14));
                        jPanel.add(fruqComboBox);
//                        jTextFields.add(fruq_update.getItemAt(fruq_update.getSelectedIndex()).toString());

                        if (rowData.get(i+2).trim().equals("null")){
                            fruqComboBox.setSelectedItem("5820");
                        }else {
                            fruqComboBox.setSelectedItem(rowData.get(i+2));
                        }
                    }else if (tabName.trim().equals("M5_ST")){
                        labelName[i-1] = "Mac 地址: ";
                        inputBoxes = new JTextField(SwingConstants.RIGHT);
                        inputBoxes.setFont(new Font(null, Font.PLAIN, 14));
                        inputBoxes.setLocation(120,40*i);
                        inputBoxes.setSize(200,30);
                        if (rowData.get(i+5).trim().equals("null")){
                            inputBoxes.setText(null);
                        }else {
                            inputBoxes.setText(rowData.get(i+5));
                        }
                        jPanel.add(inputBoxes);
                        jTextFields.add(inputBoxes);
                    }
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
                String DK = DKText.getText();

                int progress = 0;
                if (buttonText.trim().equals("M2")){
                    M2_IP = jTextFields.get(0).getText();
                    log.info("M2_IP is " + M2_IP);
//                    cellValue.add(M2_IP);
                    int targetRow = defautTableModel.getRowCount();
                    log.info("the real row count is :" + targetRow);
                    defautTableModel.setValueAt(M2_IP,rowNum,3);

                    //ssid = commonFields.get(1); netmask = commonFields.get(3); gatewayIP = commonFields.get(2);
                    M2_Configuration.configM2(commonFields.get(0),M2_IP,commonFields.get(2),commonFields.get(1));
                }
                else if (buttonText.trim().equals("M5_AP")){
                    M5_AP_IP = jTextFields.get(0).getText();
                    M5_AP_Fruq = fruqComboBox.getSelectedItem().toString();
                    log.info("M5_AP_IP is " + M5_AP_IP);
                    log.info("M5_AP_Fruq is " + M5_AP_Fruq);
                    defautTableModel.setValueAt(M5_AP_IP,rowNum,4);
                    defautTableModel.setValueAt(M5_AP_Fruq,rowNum,5);
                    M5_Configuration.configM5("AP",commonFields.get(0),M5_AP_IP,commonFields.get(4),commonFields.get(3),M5_AP_Fruq,null);
                }
                else if (buttonText.trim().equals("M5_ST")){
                    M5_ST_IP = jTextFields.get(0).getText();
                    M5_AP_Mac = jTextFields.get(1).getText();
                    log.info("M5_ST_IP is " + M5_ST_IP);
                    log.info("M5_AP_Mac is " + M5_AP_Mac);

                    defautTableModel.setValueAt(M5_AP_Mac,rowNum,6);
                    defautTableModel.setValueAt(M5_ST_IP,rowNum,7);
                    defautTableModel.setValueAt(M5_AP_Mac,rowNum,8);
                    M5_Configuration.configM5("ST",commonFields.get(0),M5_ST_IP,commonFields.get(4),commonFields.get(3),null,M5_AP_Mac);
                }else if (buttonText.trim().equals("位置")){
                    defautTableModel.setValueAt(position,rowNum,1);
                    defautTableModel.setValueAt(DK,rowNum,2);
                    if (commonFields != null){
                        log.info("ssid is " + commonFields.get(0));
                        log.info("M2 gatewayIP is " + commonFields.get(1));
                        log.info("M2 netmask is " + commonFields.get(2));
                        log.info("M5-AP gatewayIP is " + commonFields.get(3));
                        log.info("M5-AP netmask is " + commonFields.get(4));
                    }
                }

                if(!buttonText.trim().equals("位置")){

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
                }
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
        final JTextField DKText = new JTextField(SwingConstants.RIGHT);
        // the list have to be add JTextField, can not be String, otherwise can not get the TextField text
        final List<JTextField> jTextFields = new ArrayList<JTextField>();
        final List<String> fruqComboBox = new ArrayList<String>();
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

//            JLabel DK = new JLabel("DK ");
//            DK.setLocation(120,90);
//            DK.setSize(30,30);
//            DK.setFont(new Font(null,1,16));

            DKText.setLocation(120,90);
            DKText.setSize(200,30);
            DKText.setFont(new Font(null,1,16));

            //TODO: need to fill up the values automatically based on the previous record DK's value
            if (defautTableModel.getRowCount() > 2){
                //only for loop the previous two rows
                String previousDKValue = defautTableModel.getValueAt(defautTableModel.getRowCount()-2,2).toString().trim();
                String pre_previousDKValue = defautTableModel.getValueAt(defautTableModel.getRowCount()-3,2).toString().trim();
                    int previousDKValue_int = Integer.parseInt(previousDKValue);
                    int currentDKValue_int = 0;
                    if (previousDKValue.equals(pre_previousDKValue)){
                        currentDKValue_int = previousDKValue_int + 500;
                    }else {
                        currentDKValue_int = previousDKValue_int;
                    }
                DKText.setText(currentDKValue_int +"");

            }

            //validate number input only
            DKText.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char vchar = e.getKeyChar();
                    if (!(Character.isDigit(vchar)) && (vchar != KeyEvent.VK_BACK_SPACE) && (vchar != KeyEvent.VK_DELETE)){
                        JOptionPane.showMessageDialog(
                                jDialog,
                                "请输入数字 !",
                                "DK 里数",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        e.consume();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });

            jPanel.add(way);
            jPanel.add(jComboBox);
            jPanel.add(specificPosition);
//            jPanel.add(DK);
            jPanel.add(DKText);
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

                        final JComboBox<String> fruq = new JComboBox<String>(fruqs);
                        fruq.setLocation(120,40*i);
                        fruq.setSize(200,30);
                        fruq.setFont(new Font(null, Font.PLAIN, 14));
                        jPanel.add(fruq);

                        if (fruq.getSelectedIndex() == 0){
                            fruqComboBox.add(fruq.getItemAt(fruq.getSelectedIndex()));
                        }
                        fruq.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                fruqComboBox.set(0,fruq.getItemAt(fruq.getSelectedIndex()));
                            }
                        });
                    }else if (tabName.trim().equals("M5_ST")){
                        labelName[i-1] = "Mac 地址: ";
                        inputBoxes = new JTextField(SwingConstants.RIGHT);
                        inputBoxes.setFont(new Font(null, Font.PLAIN, 14));
                        inputBoxes.setLocation(120,40*i);
                        inputBoxes.setSize(200,30);
                        jPanel.add(inputBoxes);
                        jTextFields.add(inputBoxes);
                    }


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
                String DK = DKText.getText();

                //TODO:

                int progress = 0;
                if (buttonText.trim().equals("M2")){
                    M2_IP = jTextFields.get(0).getText();
                    log.info("M2_IP is " + M2_IP);
//                    cellValue.add(M2_IP);
                    int targetRow = defautTableModel.getRowCount();
                    log.info("the real row count is :" + targetRow);
                    defautTableModel.setValueAt(M2_IP,targetRow-1,3);

                    //ssid = commonFields.get(1); netmask = commonFields.get(3); gatewayIP = commonFields.get(2);
                    M2_Configuration.configM2(commonFields.get(0),M2_IP,commonFields.get(2),commonFields.get(1));
                }
                else if (buttonText.trim().equals("M5_AP")){
                    M5_AP_Fruq = fruqComboBox.get(0);
                    M5_AP_IP = jTextFields.get(0).getText();
                    log.info("M5_AP_IP is " + M5_AP_IP);
                    log.info("M5_AP_Fruq is " + M5_AP_Fruq);
                    defautTableModel.setValueAt(M5_AP_IP,defautTableModel.getRowCount()-1,4);
                    defautTableModel.setValueAt(M5_AP_Fruq,defautTableModel.getRowCount()-1,5);
                    M5_Configuration.configM5("AP",commonFields.get(0),M5_AP_IP,commonFields.get(4),commonFields.get(3),M5_AP_Fruq,null);
                }
                else if (buttonText.trim().equals("M5_ST")){
                    M5_ST_IP = jTextFields.get(0).getText();
                    M5_AP_Mac = jTextFields.get(1).getText();
                    log.info("M5_ST_IP is " + M5_ST_IP);
                    log.info("M5_AP_Mac is " + M5_AP_Mac);

                    defautTableModel.setValueAt(M5_AP_Mac,defautTableModel.getRowCount()-1,6);
                    defautTableModel.setValueAt(M5_ST_IP,defautTableModel.getRowCount()-1,7);
                    defautTableModel.setValueAt(M5_AP_Mac,defautTableModel.getRowCount()-1,8);
                    M5_Configuration.configM5("ST",commonFields.get(1),M5_ST_IP,commonFields.get(4),commonFields.get(3),null,M5_AP_Mac);
                }else if (buttonText.trim().equals("位置")){
                    defautTableModel.setValueAt(position,defautTableModel.getRowCount()-1,1);
                    defautTableModel.setValueAt(DK,defautTableModel.getRowCount()-1,2);

                    String previousDKValue = null;
                    String pre_previousDKValue = null;
                    if (defautTableModel.getRowCount() > 2){
                        //only for loop the previous two rows
                        previousDKValue = defautTableModel.getValueAt(defautTableModel.getRowCount()-2,2).toString().trim();
                        pre_previousDKValue = defautTableModel.getValueAt(defautTableModel.getRowCount()-3,2).toString().trim();
                        int previousDKValue_int = Integer.parseInt(previousDKValue);
                        int currentDKValue_int = 0;
                        if (previousDKValue.equals(pre_previousDKValue)){
                            currentDKValue_int = previousDKValue_int + 500;
                        }else {
                            currentDKValue_int = previousDKValue_int;
                        }
                        DKText.setText(currentDKValue_int +"");

                        if (defautTableModel.getValueAt(defautTableModel.getRowCount()-2,1).equals(jComboBox.getSelectedItem())
                                && previousDKValue.equals(DKText.getText())){
                            JOptionPane.showMessageDialog(jDialog,"位置重复了!  请修改");
                        }
                    }else if (defautTableModel.getRowCount() == 2){
                        previousDKValue = defautTableModel.getValueAt(defautTableModel.getRowCount()-2,2).toString().trim();
                        String previousPostionValue = defautTableModel.getValueAt(defautTableModel.getRowCount()-2,1).toString().trim();
                        if (previousPostionValue.equals(jComboBox.getSelectedItem())
                                && previousDKValue.equals(DKText.getText())){
                            JOptionPane.showMessageDialog(jDialog,"位置重复了!  请修改");
                        }
                    }
                    if (commonFields != null){
                        log.info("ssid is " + commonFields.get(0));
                        log.info("M2 gatewayIP is " + commonFields.get(1));
                        log.info("M2 netmask is " + commonFields.get(2));
                        log.info("M5-AP gatewayIP is " + commonFields.get(3));
                        log.info("M5-AP netmask is " + commonFields.get(4));
                    }
                }

                if (!buttonText.trim().equals("位置")){

                    //to make sure using the right flag
                    if (buttonText.trim().substring(0,2).equals("M2")){
                        progress = M2_Configuration.progress;
                    }
                    else if (buttonText.trim().substring(0,2).equals("M5")){
                        progress = M5_Configuration.progress;
                    }

//                    setup the popup window to let the user know the configuration is successful or not
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
                    jDialog.dispose();
                }
            }

        });


        return jPanel;
    }

    /**
     * Generate new project dialog
     * @return the project dialog
     */
    public JDialog generateNewProjectDilog(final DefaultTableModel projectTableModel){
        final JDialog jDialog = new JDialog(jFrame,"新建项目",true);
        jDialog.setSize(470,630);
        jDialog.setLocationRelativeTo(jFrame);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
//        Image icon = kit.getImage(System.getProperty("user.dir")+"\\icon\\logo.png");
        Image icon = kit.getImage("D:\\icon\\logo.png");
        jDialog.setIconImage(icon);

        JPanel jPanel = new JPanel(null);
        jPanel.setBorder(BorderFactory.createTitledBorder("新建项目："));

        //project id label and corresponding text field
        JLabel projectNumLabel = new JLabel("项目编号 :");
        final JTextField projectNumInputBox = new JTextField();  // I need the input text, it will be used later
        projectNumLabel.setLocation(50,40);
        projectNumLabel.setSize(120,40);
        projectNumLabel.setFont(new Font(null, 1, 18));
        projectNumInputBox.setLocation(190,40);
        projectNumInputBox.setSize(200,40);
        projectNumInputBox.setFont(new Font(null, Font.PLAIN, 18));
        jPanel.add(projectNumLabel);
        jPanel.add(projectNumInputBox);

        //setup the max length of projectNumber input box
        LimitedDocument ld = new LimitedDocument(3);
        ld.setAllowChar("0123456789");
        projectNumInputBox.setDocument(ld);

        //project id label and corresponding text field
        JLabel projectNameLabel = new JLabel("项目名称 :");
        final JTextField projectNameInputBox = new JTextField();  // I need the input text, it will be used later
        projectNameLabel.setLocation(50,90);
        projectNameLabel.setSize(120,40);
        projectNameLabel.setFont(new Font(null, 1, 18));
        projectNameInputBox.setLocation(190,90);
        projectNameInputBox.setSize(200,40);
        projectNameInputBox.setFont(new Font(null, Font.PLAIN, 18));
        jPanel.add(projectNameLabel);
        jPanel.add(projectNameInputBox);

        //ssid name label and corresponding text field
        JLabel ssidLabel = new JLabel("SSID :");
        final JTextField ssidInputBox = new JTextField();
        ssidInputBox.setText("ubnt");
        ssidLabel.setLocation(60,180);
        ssidLabel.setSize(120,40);
        ssidLabel.setFont(new Font(null, BOLD,18));
        ssidInputBox.setLocation(190,180);
        ssidInputBox.setSize(180,40);
        ssidInputBox.setFont(new Font(null, Font.PLAIN, 18));
        jPanel.add(ssidLabel);
        jPanel.add(ssidInputBox);

        //subway wifi panel include netmask and gateIP
        JPanel innerJPanelWifi = new JPanel();
        innerJPanelWifi.setLocation(50,140);
        innerJPanelWifi.setSize(340,200);
        innerJPanelWifi.setBorder(BorderFactory.createTitledBorder(null,"隧道无线网络", TitledBorder.LEFT,TitledBorder.TOP,new Font(null, BOLD,18)));
        innerJPanelWifi.setLayout(null);

        //gateway IP label and corresponding text field
        JLabel gatewayIP = new JLabel("无线网关 :");
//        final JTextField gatewayIPInputBox = new JTextField();
        final JMIPV4AddressField gatewayIPInputBox = new JMIPV4AddressField();
        String defaultIPValues = "10.23.0.1";
        gatewayIPInputBox.setIpAddress(defaultIPValues);
        gatewayIP.setLocation(60,230);
        gatewayIP.setSize(120,40);
        gatewayIP.setFont(new Font(null, 1, 18));
        gatewayIPInputBox.setLocation(190,230);
        gatewayIPInputBox.setSize(180,40);
        gatewayIPInputBox.setFont(new Font(null, Font.PLAIN, 18));
        jPanel.add(gatewayIP);
        jPanel.add(gatewayIPInputBox);

        //TODO: update gatewayIP when project number textField lose focus
        projectNumInputBox.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                JTextField jTextField = (JTextField) e.getSource();
                String pNumStr = jTextField.getText();
                if (pNumStr != null && !pNumStr.isEmpty()){

                    int pNum = Integer.parseInt(pNumStr);
                    if (pNum > 255){
                        JOptionPane.showMessageDialog(
                                jDialog,
                                "您输入的编号大于255，请输入小于255的数字 !",
                                "项目编号",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        projectNumInputBox.grabFocus();
                    }else {
                        String dynamicIP = "10."+pNumStr+".0.1";
                        gatewayIPInputBox.setText(dynamicIP);
                        projectNameInputBox.grabFocus();
                    }
                }
            }
        });

        //net mask label and corresponding text field
        JLabel netMaskLabel = new JLabel("子网掩码 :");
        final JMIPV4AddressField netMaskInputBox = new JMIPV4AddressField();
        netMaskInputBox.setIpAddress("255.255.248.0");
        netMaskLabel.setLocation(60,280);
        netMaskLabel.setSize(120,40);
        netMaskLabel.setFont(new Font(null, BOLD,18));
        netMaskInputBox.setLocation(190,280);
        netMaskInputBox.setSize(180,40);
        netMaskInputBox.setFont(new Font(null, Font.PLAIN, 18));
        jPanel.add(netMaskLabel);
        jPanel.add(netMaskInputBox);

        //subway M5 bridge panel include netmask and gateIP
        JPanel innerJPanel_M5 = new JPanel();
        innerJPanel_M5.setLocation(50,360);
        innerJPanel_M5.setSize(340,150);
        innerJPanel_M5.setBorder(BorderFactory.createTitledBorder(null,"M5 网桥", TitledBorder.LEFT,TitledBorder.TOP,new Font(null, BOLD,18)));
        innerJPanel_M5.setLayout(null);

        //M5 bridge gateway IP label and corresponding text field
        JLabel gatewayIP_M5 = new JLabel("网段 :");
        final JMIPV4AddressField gatewayIP_M5_InputBox = new JMIPV4AddressField();
        gatewayIP_M5_InputBox.setIpAddress("192.168.155.1");
        gatewayIP_M5.setLocation(60,400);
        gatewayIP_M5.setSize(120,40);
        gatewayIP_M5.setFont(new Font(null, 1, 18));
        gatewayIP_M5_InputBox.setLocation(190,400);
        gatewayIP_M5_InputBox.setSize(180,40);
        gatewayIP_M5_InputBox.setFont(new Font(null, Font.PLAIN, 18));
        jPanel.add(gatewayIP_M5);
        jPanel.add(gatewayIP_M5_InputBox);

        //M5 bridge net mask label and corresponding text field
        JLabel netMaskLabel_M5 = new JLabel("子网掩码 :");
        final JMIPV4AddressField netMask_M5_InputBox = new JMIPV4AddressField();
        netMask_M5_InputBox.setIpAddress("255.255.255.0");
        netMaskLabel_M5.setLocation(60,450);
        netMaskLabel_M5.setSize(120,40);
        netMaskLabel_M5.setFont(new Font(null, BOLD,18));
        netMask_M5_InputBox.setLocation(190,450);
        netMask_M5_InputBox.setSize(180,40);
        netMask_M5_InputBox.setFont(new Font(null, Font.PLAIN, 18));
        jPanel.add(netMaskLabel_M5);
        jPanel.add(netMask_M5_InputBox);

        //ok button
        JButton okButton = new JButton("创建");
        okButton.setFont(new Font(null,Font.BOLD,16));
        okButton.setLocation(100,530);
        okButton.setSize(85,40);

        //cancel button
        JButton cancelButton = new JButton("取消");
        cancelButton.setFont(new Font(null,Font.BOLD,16));
        cancelButton.setLocation(265,530);
        cancelButton.setSize(85,40);

        //ok button event listener
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //save those all fields, those fields will be used to update the config file except project_id.
                pNumber = projectNumInputBox.getText();
                pName = projectNameInputBox.getText();
                log.info("the project number is "+projectNumInputBox.getText());
                commonFields.add(ssidInputBox.getText());
                commonFields.add(gatewayIPInputBox.getText());
                commonFields.add(netMaskInputBox.getText());
                commonFields.add(gatewayIP_M5_InputBox.getText());
                commonFields.add(netMask_M5_InputBox.getText());
                log.info("the M5 net mask is "+netMask_M5_InputBox.getText());
                //TODO: need to generate project row,but how? --> done
                Vector emptyProjectRow = new Vector();
                for (int i = 0; i < 2; i++) {
                    emptyProjectRow.add(null);
                }
                projectTableModel.addRow(emptyProjectRow);
                //fill up the project table with project number and name
                projectTableModel.setValueAt(pNumber,projectTableModel.getRowCount()-1,0);
                projectTableModel.setValueAt(pName,projectTableModel.getRowCount()-1,1);
                //TODO: need to write the project info into the specific excel, in order to show these info on homepage once the app was running
                String projectExcelPath = Constant.Path_TestData_ProjectList;
//                String commonFieldsExcelPath = System.getProperty("user.dir")+"\\ConfigFile\\"+pName +"CommonFields.xlsx";
                String commonFieldsExcelPath = "D:\\ConfigFile\\"+pName +"CommonFields.xlsx";
                exportToExcel(projectTableModel,projectExcelPath,2);
                exportToExcel(null,commonFieldsExcelPath,7);

                jDialog.dispose();
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
