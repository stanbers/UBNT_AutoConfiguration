package gui.version.integration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ubnt.m2.M2_Configuration;
import ubnt.m5.M5_Configuration;
import utility.Constant;
import utility.JMIPV4AddressField;
import utility.LimitedDocument;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static java.awt.Font.BOLD;

/**
 * @Author by XuLiang
 * @Date 2018/01/12 10:09
 * @Email stanxu526@gmail.com
 */
public class ConfigSet {

    private final static Log log = LogFactory.getLog(ConfigSet.class);

    private JFrame mainFrame = new JFrame("配置页面");

    //create for store the common fields
    private List<String> commonFields = new ArrayList<String>();

    //init record index
    private int recordIndex = 1;

    //initialize M2/M5 table header
    final String[] columns_M2 = {"编号","线路","位置","M2 IP"};
    final String[] columns_M5 = {"编号","线路","位置","M5_AP IP","M5_AP 频率","M5_AP mac地址","M5_ST IP","M5_ST锁定的AP mac地址"};

    //define M2 table model
    private final DefaultTableModel tableModel_M2 = new DefaultTableModel(null,columns_M2){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    //define M5 table model
    private final DefaultTableModel tableModel_M5 = new DefaultTableModel(null,columns_M5){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    //define the global fields
    private String pName;
    private String pNumber;

    //the older IP which waiting for update, need this older ip to login
    private String updatedIP_M2,originalIP_M5AP,originalIP_M5ST;

    final String[] commonFieldsLabels = {"SSID: ","M2 无线网关：","M2 子网掩码：","M5 网桥网段：","M5 子网掩码："};

    /**
     * To render homepage, include project table and create project dialog
     */
    public void showHomepage(){
        //setup container's size and location
        mainFrame.setSize(1300, 700);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
//        Image icon = kit.getImage(System.getProperty("user.dir")+"\\icon\\logo.png");
        Image icon = kit.getImage("D:\\icon\\logo.png");
        mainFrame.setIconImage(icon);

        //create homepage panel
        final JPanel homepagePanel = new JPanel(null);

        //initialize project table header
        final String[] projectHeader = {"项目编号","项目名称"};

        //setup project table cell is not editable
        final DefaultTableModel projectTableModel = new DefaultTableModel(null,projectHeader){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        //setup homepage top title
        JLabel titleLabel = new JLabel("自动化配置",SwingConstants.CENTER);
        titleLabel.setSize(1300,100);
        titleLabel.setFont(new Font(null,Font.BOLD, 60));
        homepagePanel.add(titleLabel);

        //setup project table title
        JLabel projectListLabel = new JLabel("项目列表：");
        projectListLabel.setLocation(90,105);
        projectListLabel.setSize(200,40);
        projectListLabel.setFont(new Font(null,BOLD,20));

        //store table records
        final JTable projectTable = new JTable(projectTableModel);
        projectTable.setLocation(100,160);
        projectTable.setSize(300,350);
        projectTable.setFont(new Font(null,Font.PLAIN,16));

        //setup cell context align center
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );

        //set up project table header's layout
        projectTable.getColumn("项目编号").setPreferredWidth(130);
        projectTable.getColumn("项目名称").setPreferredWidth(220);
        projectTable.getColumn("项目编号").setCellRenderer(centerRenderer);
        projectTable.getColumn("项目名称").setCellRenderer(centerRenderer);
        projectTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        projectTable.setFont(new Font(null, Font.PLAIN, 18));
        projectTable.setRowHeight(40);

        //setup project table header
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
        importFromExcel(projectTableModel, Constant.Path_TestData_ProjectList);

        //setup project button
        JButton createPojectButton = new JButton("+ 新建项目");
        createPojectButton.setLocation(340,100);
        createPojectButton.setSize(150,40);
        createPojectButton.setFont(new Font(null, BOLD, 18));

        //show common fields on homepage
        final JPanel commonFieldsPanel = new JPanel(null);
        commonFieldsPanel.setLocation(600, 150);
        commonFieldsPanel.setSize(600, 300);
        commonFieldsPanel.setBorder(BorderFactory.createTitledBorder(null, "项目其他参数：", TitledBorder.LEFT, TitledBorder.TOP, new Font(null, BOLD, 18)));
        homepagePanel.add(commonFieldsPanel);

        //setup update common field button
        JButton updateCommonField = new JButton("修改参数");
        updateCommonField.setLocation(450,252);
        updateCommonField.setSize(135,40);
        updateCommonField.setFont(new Font(null, BOLD, 18));
        commonFieldsPanel.add(updateCommonField);

        final JPanel fieldValuePanel = new JPanel(null);

        //add action listener to update_fields button
        updateCommonField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateNewProjectDilog(projectTableModel,commonFieldsPanel,true,fieldValuePanel);
            }
        });


        //'i' started from 1, in order to setup the first label offset in vertical direction
        for (int i = 1; i <= commonFieldsLabels.length; i++) {
            JLabel commonFiledsLabel = new JLabel(commonFieldsLabels[i-1]);
            commonFiledsLabel.setFont(new Font(null, 1, 16));
            commonFiledsLabel.setLocation(10,40*i);
            commonFiledsLabel.setSize(125,30);
            commonFieldsPanel.add(commonFiledsLabel);
        }


        //add button event listener
        createPojectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //to invoke new project dialog
                generateNewProjectDilog(projectTableModel,commonFieldsPanel,false,fieldValuePanel);
            }
        });

        //setup ubnt button
        JButton ubnt_homepage = new JButton("配置中继");
        ubnt_homepage.setLocation(100,530);
        ubnt_homepage.setSize(150,50);
        ubnt_homepage.setFont(new Font(null, BOLD, 20));
        homepagePanel.add(ubnt_homepage);

        //setup wall hanging button
        JButton wallHanging_homepage = new JButton("配置壁挂");
        wallHanging_homepage.setLocation(400,530);
        wallHanging_homepage.setSize(150,50);
        wallHanging_homepage.setFont(new Font(null, BOLD, 20));
        homepagePanel.add(wallHanging_homepage);

        //setup camera button
        JButton camera_homepage = new JButton("配置摄像头");
        camera_homepage.setLocation(700,530);
        camera_homepage.setSize(150,50);
        camera_homepage.setFont(new Font(null, BOLD, 20));
        homepagePanel.add(camera_homepage);

        //setup router button
        JButton router_homepage = new JButton("配置路由器");
        router_homepage.setLocation(1000,530);
        router_homepage.setSize(150,50);
        router_homepage.setFont(new Font(null, BOLD, 20));
        homepagePanel.add(router_homepage);

        //navigate to config details page, add double click project event listener
        projectTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                //initialize the field value panel, need to clear this panel each time

                fieldValuePanel.setLocation(135,40);
                fieldValuePanel.setSize(150,230);
                commonFieldsPanel.add(fieldValuePanel);
                commonFieldsPanel.remove(fieldValuePanel);

                int targetRow = projectTable.getSelectedRow();
                if (e.getClickCount() == 1){

                    log.info(commonFields.size());
                    //set the value to these two global fields
                    pNumber = projectTableModel.getValueAt(targetRow,0).toString();
                    pName = projectTableModel.getValueAt(targetRow,1).toString();

                    String SpecificProjectCommonField = "D:\\ConfigFile\\"+pName +"CommonFields.xlsx";
                    File projectCommonFieldFile = new File(SpecificProjectCommonField);
                    if (projectCommonFieldFile.exists()){
                        importFromExcel(null,SpecificProjectCommonField);
                    }

                    // render the common fields value
                    for (int i = 0; i < commonFieldsLabels.length; i++) {
                        if (commonFields.size() > 0){
                            log.info(commonFields.get(i+1));
                            JLabel commonFieldsValue  = new JLabel(commonFields.get(i+2));
                            commonFieldsValue.setLocation(0,40*i);
                            commonFieldsValue.setSize(140,30);
                            commonFieldsValue.setFont(new Font(null,Font.BOLD,16));
                            fieldValuePanel.add(commonFieldsValue);
                        }

                    }
                    commonFieldsPanel.add(fieldValuePanel);
                    fieldValuePanel.repaint();
                }

                final JPanel outermostPanel = showConfigRecordsPage();

                JPanel outermostHeaderPanel = new JPanel(){
                    @Override
                    public void paintComponent(Graphics graphics){
                        super.paintComponent(graphics);
                        Graphics2D g2 = (Graphics2D) graphics;
                        Shape line01 = new Line2D.Double(0,3,1135,3);
                        Shape line02 = new Line2D.Double(0,5,1135,5);
                        g2.draw(line01);
                        g2.draw(line02);
                    }
                };
                outermostHeaderPanel.setLocation(10,45);
                outermostHeaderPanel.setSize(1135,13);
                outermostPanel.add(outermostHeaderPanel);

                // create the header dynamically
                JLabel currentPNumberTitle = new JLabel("当前项目编号：");
                currentPNumberTitle.setLocation(20,10);
                currentPNumberTitle.setSize(160,40);
                currentPNumberTitle.setFont(new Font(null,Font.BOLD,18));
                outermostPanel.add(currentPNumberTitle);

                JLabel currentPNameTitle = new JLabel("当前项目名称：");
                currentPNameTitle.setLocation(240,10);
                currentPNameTitle.setSize(160,40);
                currentPNameTitle.setFont(new Font(null,Font.BOLD,18));
                outermostPanel.add(currentPNameTitle);

                final JLabel currentPName = new JLabel(pName);
                currentPName.setLocation(370,10);
                currentPName.setSize(180,40);
                currentPName.setFont(new Font(null,Font.BOLD,18));

                final JLabel currentPNumber = new JLabel(pNumber);
                currentPNumber.setLocation(150,10);
                currentPNumber.setSize(80,40);
                currentPNumber.setFont(new Font(null,Font.BOLD,18));

                //add backward button, go back to homepage
                JButton  backwardButton = new JButton("返回首页");
                backwardButton.setLocation(1020,15);
                backwardButton.setSize(120,30);
                backwardButton.setFont(new Font(null,Font.BOLD,18));
                outermostPanel.add(backwardButton);

                //add backward button event listener
                backwardButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //need remove these two JLabel to make sure every time these two label are new added to outPanel
                        outermostPanel.remove(currentPName);
                        outermostPanel.remove(currentPNumber);
                        tableModel_M2.getDataVector().clear();
                        tableModel_M5.getDataVector().clear();
                        outermostPanel.setVisible(false);
                        homepagePanel.setVisible(true);
                        mainFrame.setContentPane(homepagePanel);
                    }

                });

                //double click
                if (e.getClickCount() == 2){
                    homepagePanel.setVisible(false);
                    showConfigRecordsPage().setVisible(true);
                    mainFrame.setContentPane(outermostPanel);
                    if (targetRow >= 0){

                        String specificExcel_M2 = "D:\\ConfigFile\\M2\\"+pName +".xlsx";
//                        String specificExcel_M2 = System.getProperty("user.dir")+ "\\ConfigFile\\M2\\"+pName +".xlsx";
                        String specificExcel_M5 = "D:\\ConfigFile\\M5\\"+pName +".xlsx";
//                        String specificExcel_M5 = System.getProperty("user.dir")+ "\\ConfigFile\\M5\\"+pName +".xlsx";
//                        String SpecificProjectCommonField = "D:\\ConfigFile\\"+pName +"CommonFields.xlsx";
//                        String SpecificProjectCommonField = System.getProperty("user.dir")+ "\\ConfigFile\\"+pName +"CommonFields.xlsx";
                        File projectCorresspondingConfigFile_M2 = new File(specificExcel_M2);
                        File projectCorresspondingConfigFile_M5 = new File(specificExcel_M5);

                        if (!projectCorresspondingConfigFile_M2.exists()){
                            exportToExcel(tableModel_M2,"D:\\ConfigFile\\M2\\"+pName+".xlsx",4);
//                            exportToExcel(tableModel_M2,System.getProperty("user.dir")+ "\\ConfigFile\\M2\\"+pName+".xlsx",4);
                        }
                        if (!projectCorresspondingConfigFile_M5.exists()){
                            exportToExcel(tableModel_M5,"D:\\ConfigFile\\M5\\"+pName+".xlsx",8);
//                            exportToExcel(tableModel_M5,System.getProperty("user.dir")+ "\\ConfigFile\\M5\\"+pName+".xlsx",8);
                        }
                        if (projectCorresspondingConfigFile_M2.exists()){
                            //import table rows on main page
                            importFromExcel(tableModel_M2 ,specificExcel_M2);
//                            jTable.setModel(defaultTableModel);
                            //import specific project common fields
                            //TODO: this time not to render table but to override commonfields.
//                            importFromExcel(null,SpecificProjectCommonField);
                            //import project list excel, in order to show the project name and number on the main page
                        }
                        if (projectCorresspondingConfigFile_M5.exists()){
                            //import table rows on main page
                            importFromExcel(tableModel_M5 ,specificExcel_M5);
//                            jTable.setModel(defaultTableModel);
                            //import specific project common fields
                            //TODO: this time not to render table but to override commonfields.
//                            importFromExcel(null,SpecificProjectCommonField);
                            //import project list excel, in order to show the project name and number on the main page
                        }

                        outermostPanel.add(currentPName);
                        outermostPanel.add(currentPNumber);
                        currentPName.setText(pName);
                        currentPNumber.setText(pNumber);

                    }else {
                        //TODO: may show waring message here.
                    }
                }
            }
        });



//        //ssid field
//        JLabel ssidLabel = new JLabel("SSID: ",SwingConstants.LEFT);
//        ssidLabel.setFont(new Font(null, 1, 16));
//        ssidLabel.setLocation(10,10);
//        ssidLabel.setSize(140,30);
//        ssidLabel.setOpaque(true);
//        ssidLabel.setBackground(Color.red);
//        commonFieldsPanel.add(ssidLabel);


        homepagePanel.add(projectListLabel);
        homepagePanel.add(projectTableContainer);
        homepagePanel.add(createPojectButton);

//        mainFrame.setContentPane(showConfigRecordsPage());
        mainFrame.setContentPane(homepagePanel);
        mainFrame.setVisible(true);
    }

    /**
     * To generate the outermost container panel records page
     */
    public JPanel showConfigRecordsPage(){
        //TODO: this page will include two main panels, one is M2 container another one is M5 container
        //this panel the outermost panel container, no need to set location and size
        JPanel outermostContainerPanel = new JPanel(null);
//        outermostContainerPanel.setBorder(BorderFactory.createTitledBorder("中继配置记录："));

        //M2 records container
        JPanel M2ContainerPanel = new JPanel(null);
        M2ContainerPanel.setLocation(10,60);
        M2ContainerPanel.setSize(380,550);
        M2ContainerPanel.setBorder(BorderFactory.createTitledBorder(null,"M2 配置记录：", TitledBorder.LEFT,TitledBorder.TOP,new Font(null,Font.BOLD,15)));
        M2ContainerPanel.setLayout(null);

        //create a JTable to record the configuration info
        final JTable jTable_M2 = new JTable(tableModel_M2);
        jTable_M2.setLocation(20,10);
        jTable_M2.setSize(340,450);
        jTable_M2.setRowHeight(25);

        //setup column width
        jTable_M2.getColumn("编号").setMaxWidth(45);
        jTable_M2.getColumn("位置").setMaxWidth(80);
        jTable_M2.getColumn("线路").setMaxWidth(45);
        jTable_M2.getColumn("M2 IP").setPreferredWidth(30);
        jTable_M2.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        jTable_M2.setFont(new Font(null, Font.PLAIN, 15));

        //setup cell context align center
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
        jTable_M2.getColumn("编号").setCellRenderer(centerRenderer);
        jTable_M2.getColumn("位置").setCellRenderer(centerRenderer);
        jTable_M2.getColumn("线路").setCellRenderer(centerRenderer);
        jTable_M2.getColumn("M2 IP").setCellRenderer(centerRenderer);


        //setup M2 table header
        JTableHeader jTableHeader_M2 = jTable_M2.getTableHeader();
        jTableHeader_M2.setLocation(20,10);
        jTableHeader_M2.setSize(340,30);
        jTableHeader_M2.setFont(new Font(null, Font.BOLD, 16));
        jTableHeader_M2.setResizingAllowed(true);
        jTableHeader_M2.setReorderingAllowed(true);

        //setup M2 table context scrollable
        final JScrollPane tablePanel_M2 = new JScrollPane(jTable_M2);
        tablePanel_M2.setLocation(15,40);
        tablePanel_M2.setSize(340,450);
        M2ContainerPanel.add(tablePanel_M2);

        //setup new create button
        JButton createM2 = new JButton("新建");
        createM2.setFont(new Font(null,Font.BOLD,14));
        createM2.setLocation(275,500);
        createM2.setSize(80,30);
        M2ContainerPanel.add(createM2);

        //setup remove button
        JButton removeM2 = new JButton("删除");
        removeM2.setLocation(165,500);
        removeM2.setSize(80,30);
        removeM2.setFont(new Font(null,Font.BOLD,14));
        M2ContainerPanel.add(removeM2);

        //setup export M2 records button
        JButton exportM2Records = new JButton("导出");
        exportM2Records.setFont(new Font(null,Font.BOLD,14));
        exportM2Records.setLocation(15,500);
        exportM2Records.setSize(80,30);
        M2ContainerPanel.add(exportM2Records);

        //add export all M2 config records
        exportM2Records.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToExcel(tableModel_M2,"D:\\ConfigFile\\M2\\"+pName+".xlsx",4);
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
        createM2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vector emptyRow = new Vector();
                for (int i = 0; i < 10; i++) {
                    emptyRow.add(null);
                }
                tableModel_M2.addRow(emptyRow);
                if (recordIndex == 0){
                    tableModel_M2.setValueAt(recordIndex++,tableModel_M2.getRowCount()-1,0);
                }else {
                    recordIndex = tableModel_M2.getRowCount();
                    tableModel_M2.setValueAt(recordIndex++,tableModel_M2.getRowCount()-1,0);
                }
                createM2Dialog(tableModel_M2);
            }
        });

        removeM2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int remove  = JOptionPane.showConfirmDialog(mainFrame,"确定删除吗？");
                log.info("return int value is " + remove);
                if (remove == 0){

                    //TODO: need to change the row number, but how ? --> done
                    int selectedRowNumber = jTable_M2.getSelectedRow();
                    int allRowsCount = tableModel_M2.getRowCount();
                    int remainingRows = allRowsCount - selectedRowNumber;
                    log.info("the remaining rows are : " + remainingRows + "; all rows : " + allRowsCount + " ;" + selectedRowNumber + " was selected!");
                    log.info(jTable_M2.getSelectedRow() +" row was deleting");
                    for (int i = 1; i < remainingRows; i++) {
                        tableModel_M2.setValueAt(selectedRowNumber + i,selectedRowNumber+i,0);
                    }
                    if (jTable_M2.getSelectedRow() >= 0){
                        tableModel_M2.removeRow(jTable_M2.getSelectedRow());
                    }else {
                        JOptionPane.showMessageDialog(mainFrame,"目前没有可以被删除的记录 ！");
                    }
                }
            }
        });

        jTable_M2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){

                    //the whole cell of a specific row are stored in this list.
                    List<String> cellValuesOfSpecificRow = new ArrayList<String>();
                    int rowNum = jTable_M2.getSelectedRow();
                    //i started from 1, cause no to edit row number.
                    log.info("the " + rowNum + "th row was selected !");
                    for (int i = 1; i <= 3; i++) {
                        int a = cellValuesOfSpecificRow.size();
                        log.info("row has " +a +"columns");
                        if (tableModel_M2.getValueAt(rowNum,i) != null){
                            cellValuesOfSpecificRow.add(tableModel_M2.getValueAt(rowNum,i).toString());
                        }else {
                            cellValuesOfSpecificRow.add("null");
                        }
                    }

                    //TODO: get the M2 olderIP,
                    if (tableModel_M2.getValueAt(rowNum,3) != null){
                        updatedIP_M2 = tableModel_M2.getValueAt(rowNum,3).toString();
                    }
                    updateM2Dialog(cellValuesOfSpecificRow,tableModel_M2);
                }
            }
        });

        //M5 records container
        JPanel M5ContainerPanel = new JPanel(null);
        M5ContainerPanel.setLocation(425,60);
        M5ContainerPanel.setSize(850,550);
        M5ContainerPanel.setBorder(BorderFactory.createTitledBorder(null,"M5 配置记录：", TitledBorder.LEFT,TitledBorder.TOP,new Font(null,Font.BOLD,15)));
        M5ContainerPanel.setLayout(null);

        //create a JTable to record the configuration info
        final JTable jTable_M5 = new JTable(tableModel_M5);
        jTable_M5.setLocation(20,10);
        jTable_M5.setSize(820,450);
        jTable_M5.setRowHeight(25);

        //setup column width
        jTable_M5.getColumn("编号").setMaxWidth(45);
        jTable_M5.getColumn("位置").setMaxWidth(45);
        jTable_M5.getColumn("线路").setMaxWidth(45);
        jTable_M5.getColumn("M5_AP IP").setMaxWidth(100);
        jTable_M5.getColumn("M5_AP 频率").setMaxWidth(120);
        jTable_M5.getColumn("M5_AP mac地址").setMaxWidth(140);
        jTable_M5.getColumn("M5_ST IP").setMaxWidth(100);
        jTable_M5.getColumn("M5_ST锁定的AP mac地址").setMaxWidth(230);
        jTable_M5.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        jTable_M5.setFont(new Font(null, Font.PLAIN, 15));

        //setup cell context align center
        DefaultTableCellRenderer centerRenderer_M5 = new DefaultTableCellRenderer();
        centerRenderer_M5.setHorizontalAlignment( SwingConstants.CENTER );
        jTable_M5.getColumn("编号").setCellRenderer(centerRenderer_M5);
        jTable_M5.getColumn("位置").setCellRenderer(centerRenderer_M5);
        jTable_M5.getColumn("线路").setCellRenderer(centerRenderer_M5);
        jTable_M5.getColumn("M5_AP IP").setCellRenderer(centerRenderer_M5);
        jTable_M5.getColumn("M5_AP 频率").setCellRenderer(centerRenderer_M5);
        jTable_M5.getColumn("M5_ST IP").setCellRenderer(centerRenderer_M5);
        jTable_M5.getColumn("M5_ST锁定的AP mac地址").setCellRenderer(centerRenderer_M5);


        //setup M5 table header
        JTableHeader jTableHeader_M5 = jTable_M5.getTableHeader();
        jTableHeader_M5.setLocation(20,10);
        jTableHeader_M5.setSize(820,30);
        jTableHeader_M5.setFont(new Font(null, Font.BOLD, 16));
        jTableHeader_M5.setResizingAllowed(true);
        jTableHeader_M5.setReorderingAllowed(true);

        //setup M5 table context scrollable
        final JScrollPane tablePanel_M5 = new JScrollPane(jTable_M5);
        tablePanel_M5.setLocation(15,40);
        tablePanel_M5.setSize(820,450);
        M5ContainerPanel.add(tablePanel_M5);

        //setup new create button
        JButton createM5 = new JButton("新建");
        createM5.setFont(new Font(null,Font.BOLD,14));
        createM5.setLocation(735,500);
        createM5.setSize(80,30);
        M5ContainerPanel.add(createM5);

        //setup remove button
        JButton removeM5 = new JButton("删除");
        removeM5.setLocation(625,500);
        removeM5.setSize(80,30);
        removeM5.setFont(new Font(null,Font.BOLD,14));
        M5ContainerPanel.add(removeM5);

        //setup export M2 records button
        JButton exportM5Records = new JButton("导出");
        exportM5Records.setFont(new Font(null,Font.BOLD,14));
        exportM5Records.setLocation(415,500);
        exportM5Records.setSize(80,30);
        M5ContainerPanel.add(exportM5Records);

        //add event listener to create button
        createM5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vector emptyRow = new Vector();
                for (int i = 0; i < 10; i++) {
                    emptyRow.add(null);
                }
                tableModel_M5.addRow(emptyRow);
                if (recordIndex == 0){
                    tableModel_M5.setValueAt(recordIndex++,tableModel_M5.getRowCount()-1,0);
                }else {
                    recordIndex = tableModel_M5.getRowCount();
                    tableModel_M5.setValueAt(recordIndex++,tableModel_M5.getRowCount()-1,0);
                }
                createM5Overlay(tableModel_M5);
            }
        });

        //add export all M5 config records
        exportM5Records.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToExcel(tableModel_M5,"D:\\ConfigFile\\M5\\"+pName+".xlsx",8);
//                exportToExcel(tableModel_M5,System.getProperty("user.dir")+ "\\ConfigFile\\M5\\"+pName+".xlsx",8);
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "导出数据完毕 !",
                        "配置结果",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        //add remove record event listener to M5
        removeM5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int remove  = JOptionPane.showConfirmDialog(mainFrame,"确定删除吗？");
                log.info("return int value is " + remove);
                if (remove == 0){

                    int selectedRowNumber = jTable_M5.getSelectedRow();
                    int allRowsCount = tableModel_M5.getRowCount();
                    int remainingRows = allRowsCount - selectedRowNumber;
                    log.info("the remaining rows are : " + remainingRows + "; all rows : " + allRowsCount + " ;" + selectedRowNumber + " was selected!");
                    log.info(jTable_M5.getSelectedRow() +" row was deleting");
                    for (int i = 1; i < remainingRows; i++) {
                        tableModel_M5.setValueAt(selectedRowNumber + i,selectedRowNumber+i,0);
                    }
                    if (jTable_M5.getSelectedRow() >= 0){
                        tableModel_M5.removeRow(jTable_M5.getSelectedRow());
                    }else {
                        JOptionPane.showMessageDialog(mainFrame,"目前没有可以被删除的记录 ！");
                    }
                }
            }
        });

        //update M5 row
        jTable_M5.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){

                    //the whole cell of a specific row are stored in this list.
                    List<String> cellValuesOfSpecificRow = new ArrayList<String>();
                    int rowNum = jTable_M5.getSelectedRow();
                    //i started from 1, cause no need to edit row number.
                    log.info("the " + rowNum + "th row was selected !");
                    for (int i = 1; i <= 7; i++) {
                        int a = cellValuesOfSpecificRow.size();
                        log.info("row has " +a +"columns");
                        if (tableModel_M5.getValueAt(rowNum,i) != null){
                            cellValuesOfSpecificRow.add(tableModel_M5.getValueAt(rowNum,i).toString());
                        }else {
                            cellValuesOfSpecificRow.add("null");
                        }
                    }

                    //TODO: get the M5 AP older IP/fruq/MAC, ST IP
                    if (tableModel_M5.getValueAt(rowNum,3) != null){
                        originalIP_M5AP = tableModel_M5.getValueAt(rowNum,3).toString();
                    }
                    if (tableModel_M5.getValueAt(rowNum,6) != null){
                        originalIP_M5ST = tableModel_M5.getValueAt(rowNum,6).toString();
                    }

                    updateM5Overlay(cellValuesOfSpecificRow,tableModel_M5);
                }
            }
        });

        outermostContainerPanel.add(M2ContainerPanel);
        outermostContainerPanel.add(M5ContainerPanel);
        return outermostContainerPanel;
    }

    /**
     * to update M5 row
     * @param rowData  the target row data
     * @param tableModel  the table model
     */
    public void updateM5Overlay(List<String> rowData,DefaultTableModel tableModel){
        final JDialog M5jDialog_update = new JDialog(mainFrame,"M5 修改页面",true);
        M5jDialog_update.setSize(450,500);
        M5jDialog_update.setLocationRelativeTo(mainFrame);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
//        Image icon = kit.getImage(System.getProperty("user.dir")+ "\\icon\\logo.png");
        M5jDialog_update.setIconImage(icon);

        //setup JTabbedPane
        final JTabbedPane jTabbedPane_M5 = new JTabbedPane();
        jTabbedPane_M5.setFont(new Font("ITALIC", 1, 16));
        jTabbedPane_M5.add("位置",updateM5Panel("位置",tableModel,M5jDialog_update,rowData));
        jTabbedPane_M5.add("M5_AP",updateM5Panel("M5_AP",tableModel,M5jDialog_update,rowData));
        jTabbedPane_M5.add("M5_ST",updateM5Panel("M5_ST",tableModel,M5jDialog_update,rowData));

        jTabbedPane_M5.setSelectedIndex(0);
//        createTextPanelOverlay("M2");
//        createTextPanelOverlay("位置",defautTableModel);
        M5jDialog_update.setContentPane(jTabbedPane_M5);
        M5jDialog_update.setVisible(true);
    }

    /**
     * to update M5
     * @param tabName  the tab name
     * @param tableModel the table model
     * @param dialog the target overlay
     * @param rowData the selected row data
     * @return  the updated M5 panel
     */
    public JPanel updateM5Panel(String tabName, final DefaultTableModel tableModel, final JDialog dialog, List<String> rowData){
        JPanel jPanel = new JPanel(null);
        jPanel.setBorder((BorderFactory.createTitledBorder("UBNT( "+tabName+") 修改")));

        JButton update = new JButton(tabName);
        update.setFont(new Font(null,Font.BOLD,14));
        update.setLocation(145,310);
        update.setSize(105,30);
        jPanel.add(update);

        final String[] ways = new String[]{"左线","右线"};
        final JComboBox<String> jComboBoxWay = new JComboBox<String>(ways);
        final JTextField DKText = new JTextField(SwingConstants.RIGHT);
        // the list have to be add JTextField, can not be String, otherwise can not get the TextField text
        final List<JTextField> jTextFields = new ArrayList<JTextField>();
        final List<String> fruqComboBoxList = new ArrayList<String>();

        if (tabName != null && tabName.trim().equals("位置")){
            //setup way title label
            JLabel way = new JLabel("设定线路 :",SwingConstants.LEFT);
            way.setFont(new Font(null, 1, 16));
            way.setLocation(10,40);
            way.setSize(115,30);

            //setup way combo box
            jComboBoxWay.setLocation(120,40);
            jComboBoxWay.setSize(200,30);
            jComboBoxWay.setFont(new Font(null, 1, 16));
            jComboBoxWay.setSelectedIndex(0);

            if (rowData != null && rowData.get(0).trim().equals("右线")){
                jComboBoxWay.setSelectedIndex(1);
            }else {
                jComboBoxWay.setSelectedIndex(0);
            }

            //setup position title label
            JLabel specificPosition = new JLabel("具体位置 :",SwingConstants.LEFT);
            specificPosition.setFont(new Font(null, 1, 16));
            specificPosition.setLocation(10,90);
            specificPosition.setSize(115,30);

            //setup specific position
            DKText.setLocation(120,90);
            DKText.setSize(200,30);
            DKText.setFont(new Font(null,1,16));

            //setup the max length of DK input box
            LimitedDocument ld = new LimitedDocument(5);
            ld.setAllowChar("0123456789");
            DKText.setDocument(ld);

            if (rowData != null){
                if (rowData.get(1).trim().equals("null")){
                    DKText.setText(null);
                }else {
                    String dkMiles = rowData.get(1);
                    DKText.setText(dkMiles);
                    log.info("the DK mile is " + dkMiles);
                }
            }

            jPanel.add(way);
            jPanel.add(jComboBoxWay);
            jPanel.add(specificPosition);
            jPanel.add(DKText);

            //add event listener when 'position' button clicked
            update.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String way = jComboBoxWay.getSelectedItem().toString();
                    String DK = DKText.getText();
                    tableModel.setValueAt(way,tableModel.getRowCount()-1,1);
                    tableModel.setValueAt(DK,tableModel.getRowCount()-1,2);
                }
            });
        }

        //M5_AP configuration
        if (tabName != null && tabName.trim().equals("M5_AP")){

            //setup M5_AP ip address title label
            JLabel IPAddress_AP = new JLabel("IP 地址 :",SwingConstants.LEFT);
            IPAddress_AP.setFont(new Font(null, 1, 16));
            IPAddress_AP.setLocation(10,40);
            IPAddress_AP.setSize(115,30);
            jPanel.add(IPAddress_AP);

            //setup M5_AP input box with default value
            final JMIPV4AddressField IP_AP = new JMIPV4AddressField();

            if (rowData !=null){
                if (rowData.get(2).trim().equals("null")){
                    IP_AP.setText(null);
                }else {
                    IP_AP.setText(rowData.get(2));
                }
            }

//            IP_AP.setIpAddress("192.168.155.1");
            IP_AP.setFont(new Font(null, Font.PLAIN, 14));
            IP_AP.setLocation(120,40);
            IP_AP.setSize(200,30);
            jPanel.add(IP_AP);

            //setup M5_AP frequency title label
            JLabel fruq_AP = new JLabel("频率(MHz) :",SwingConstants.LEFT);
            fruq_AP.setFont(new Font(null, 1, 16));
            fruq_AP.setLocation(10,80);
            fruq_AP.setSize(115,30);
            jPanel.add(fruq_AP);

            //setup the frequency combo box
            final String[] fruqs = new String[]{"5820","5840","5860"};
            final JComboBox<String> fruq = new JComboBox<String>(fruqs);
            fruq.setLocation(120,80);
            fruq.setSize(200,30);
            fruq.setFont(new Font(null, Font.PLAIN, 14));
            jPanel.add(fruq);

            if (rowData.get(3).trim().equals("null")){
                fruq.setSelectedItem("5820");
            }else {
                fruq.setSelectedItem(rowData.get(3));
            }

            //add event listener when 'M5_AP' button clicked
            update.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int progress = 0;
                    String IPAddress_AP =  IP_AP.getText();
                    String fruq_AP = fruq.getSelectedItem().toString();
                    tableModel.setValueAt(IPAddress_AP,tableModel.getRowCount()-1,3);
                    tableModel.setValueAt(fruq_AP,tableModel.getRowCount()-1,4);
                    progress = new M5_Configuration().configM5("AP",commonFields.get(2),IPAddress_AP,commonFields.get(6),commonFields.get(5),fruq_AP,null,originalIP_M5AP);
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
                    dialog.dispose();
                }
            });
        }

        if (tabName != null && tabName.trim().equals("M5_ST")){

            //setup M5_ST ip address title label
            JLabel IPAddress_ST = new JLabel("IP 地址 :",SwingConstants.LEFT);
            IPAddress_ST.setFont(new Font(null, 1, 16));
            IPAddress_ST.setLocation(10,40);
            IPAddress_ST.setSize(115,30);
            jPanel.add(IPAddress_ST);

            //setup M5_ST  input box with default value
            final JMIPV4AddressField IP_ST = new JMIPV4AddressField();
            if (rowData.get(5).trim().equals("null")){
                IP_ST.setText(null);
            }else {
                IP_ST.setText(rowData.get(5));
            }
            IP_ST.setFont(new Font(null, Font.PLAIN, 14));
            IP_ST.setLocation(120,40);
            IP_ST.setSize(200,30);
            jPanel.add(IP_ST);

            //setup M5_AP MAC address title label
            JLabel mac_AP = new JLabel("AP mac 地址 :",SwingConstants.LEFT);
            mac_AP.setFont(new Font(null, 1, 16));
            mac_AP.setLocation(10,90);
            mac_AP.setSize(115,30);
            jPanel.add(mac_AP);

            //setup the frequency combo box
            final JTextField macBox = new JTextField(SwingConstants.RIGHT);
            macBox.setLocation(120,90);
            macBox.setSize(200,30);
            macBox.setFont(new Font(null,1,16));
            if (rowData.get(5).trim().equals("null")){
                macBox.setText(null);
            }else {
                macBox.setText(rowData.get(4));
            }
            jPanel.add(macBox);

            //add event listener when 'M5_AP' button clicked
            update.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int progress = 0;
                    String IPAddress_ST = IP_ST.getText();
                    String macAddress = macBox.getText();
                    tableModel.setValueAt(macAddress,tableModel.getRowCount()-1,5);
                    tableModel.setValueAt(IPAddress_ST,tableModel.getRowCount()-1,6);
                    tableModel.setValueAt(macAddress,tableModel.getRowCount()-1,7);

                    progress = new M5_Configuration().configM5("ST",commonFields.get(2),IPAddress_ST,commonFields.get(6),commonFields.get(5),null,macAddress,originalIP_M5ST);
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
                    dialog.dispose();
                }
            });
        }
        return jPanel;
    }

    /**
     * create M5 dialog popup
     * @param tableModel  the table model
     */
    public void createM5Overlay(final DefaultTableModel tableModel){
        //prepare the M5 create dialog
        final JDialog M5jDialog_create = new JDialog(mainFrame,"M5 配置页面",true);
        M5jDialog_create.setSize(450,500);
        M5jDialog_create.setLocationRelativeTo(mainFrame);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
//        Image icon = kit.getImage(System.getProperty("user.dir")+ "\\icon\\logo.png");
        M5jDialog_create.setIconImage(icon);

        //setup JTabbedPane
        final JTabbedPane jTabbedPane_M5 = new JTabbedPane();
        jTabbedPane_M5.setFont(new Font("ITALIC", 1, 16));
        jTabbedPane_M5.add("位置",createM5Panel("位置",tableModel,M5jDialog_create));
        jTabbedPane_M5.add("M5_AP",createM5Panel("M5_AP",tableModel,M5jDialog_create));
        jTabbedPane_M5.add("M5_ST",createM5Panel("M5_ST",tableModel,M5jDialog_create));

        jTabbedPane_M5.setSelectedIndex(0);
//        createTextPanelOverlay("M2");
//        createTextPanelOverlay("位置",defautTableModel);
        M5jDialog_create.setContentPane(jTabbedPane_M5);
        M5jDialog_create.setVisible(true);
    }

    /**
     * M5 panel container
     * @param tabName  the tab name :M5_AP or M5_ST
     * @param tableModel the M5 config records table model
     * @return the panel container
     */
    public JPanel createM5Panel(String tabName, final DefaultTableModel tableModel, final JDialog dialog){
        JPanel jPanel = new JPanel(null);
        jPanel.setBorder((BorderFactory.createTitledBorder("UBNT( "+tabName+") 配置")));

        JButton create = new JButton(tabName);
        create.setFont(new Font(null,Font.BOLD,14));
        create.setLocation(145,310);
        create.setSize(105,30);
        jPanel.add(create);

        final String[] ways = new String[]{"左线","右线"};
        final JComboBox<String> jComboBoxWay = new JComboBox<String>(ways);
        final JTextField DKText = new JTextField(SwingConstants.RIGHT);
        // the list have to be add JTextField, can not be String, otherwise can not get the TextField text
        final List<JTextField> jTextFields = new ArrayList<JTextField>();
        final List<String> fruqComboBoxList = new ArrayList<String>();

        if (tabName != null && tabName.trim().equals("位置")){
            //setup way title label
            JLabel way = new JLabel("设定线路 :",SwingConstants.LEFT);
            way.setFont(new Font(null, 1, 16));
            way.setLocation(10,40);
            way.setSize(115,30);

            //setup way combo box
            jComboBoxWay.setLocation(120,40);
            jComboBoxWay.setSize(200,30);
            jComboBoxWay.setFont(new Font(null, 1, 16));
            jComboBoxWay.setSelectedIndex(0);

            //setup position title label
            JLabel specificPosition = new JLabel("具体位置 :",SwingConstants.LEFT);
            specificPosition.setFont(new Font(null, 1, 16));
            specificPosition.setLocation(10,90);
            specificPosition.setSize(115,30);

            //setup specific position
            DKText.setLocation(120,90);
            DKText.setSize(200,30);
            DKText.setFont(new Font(null,1,16));

            //setup the max length of DK input box
            LimitedDocument ld = new LimitedDocument(5);
            ld.setAllowChar("0123456789");
            DKText.setDocument(ld);

            jPanel.add(way);
            jPanel.add(jComboBoxWay);
            jPanel.add(specificPosition);
            jPanel.add(DKText);

            //add event listener when 'position' button clicked
            create.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String way = jComboBoxWay.getSelectedItem().toString();
                    String DK = DKText.getText();
                    tableModel.setValueAt(way,tableModel.getRowCount()-1,1);
                    tableModel.setValueAt(DK,tableModel.getRowCount()-1,2);
                }
            });
        }

        //M5_AP configuration
        if (tabName != null && tabName.trim().equals("M5_AP")){

            //setup M5_AP ip address title label
            JLabel IPAddress_AP = new JLabel("IP 地址 :",SwingConstants.LEFT);
            IPAddress_AP.setFont(new Font(null, 1, 16));
            IPAddress_AP.setLocation(10,40);
            IPAddress_AP.setSize(115,30);
            jPanel.add(IPAddress_AP);

            //setup M5_AP input box with default value
            final JMIPV4AddressField IP_AP = new JMIPV4AddressField();
            IP_AP.setIpAddress("192.168.155.1");
            IP_AP.setFont(new Font(null, Font.PLAIN, 14));
            IP_AP.setLocation(120,40);
            IP_AP.setSize(200,30);
            jPanel.add(IP_AP);

            //setup M5_AP frequency title label
            JLabel fruq_AP = new JLabel("频率(MHz) :",SwingConstants.LEFT);
            fruq_AP.setFont(new Font(null, 1, 16));
            fruq_AP.setLocation(10,80);
            fruq_AP.setSize(115,30);
            jPanel.add(fruq_AP);

            //setup the frequency combo box
            final String[] fruqs = new String[]{"5820","5840","5860"};
            final JComboBox<String> fruq = new JComboBox<String>(fruqs);
            fruq.setLocation(120,80);
            fruq.setSize(200,30);
            fruq.setFont(new Font(null, Font.PLAIN, 14));
            jPanel.add(fruq);

            //add the selected fruq to the list, just like the IP
            if (fruq.getSelectedIndex() == 0){
                fruqComboBoxList.add(fruq.getItemAt(fruq.getSelectedIndex()));
            }
            fruq.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    fruqComboBoxList.set(0,fruq.getItemAt(fruq.getSelectedIndex()));
                }
            });

            //add event listener when 'M5_AP' button clicked
            create.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int progress = 0;
                    String IPAddress_AP =  IP_AP.getText();
                    String fruq_AP = fruqComboBoxList.get(0);
                    tableModel.setValueAt(IPAddress_AP,tableModel.getRowCount()-1,3);
                    tableModel.setValueAt(fruq_AP,tableModel.getRowCount()-1,4);

                    progress = new M5_Configuration().configM5("AP",commonFields.get(2),IPAddress_AP,commonFields.get(6),commonFields.get(5),fruq_AP,null,null);
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
                    dialog.dispose();
                }
            });
        }

        if (tabName != null && tabName.trim().equals("M5_ST")){

            //setup M5_ST ip address title label
            JLabel IPAddress_ST = new JLabel("IP 地址 :",SwingConstants.LEFT);
            IPAddress_ST.setFont(new Font(null, 1, 16));
            IPAddress_ST.setLocation(10,40);
            IPAddress_ST.setSize(115,30);
            jPanel.add(IPAddress_ST);

            //setup M5_ST  input box with default value
            final JMIPV4AddressField IP_ST = new JMIPV4AddressField();
            IP_ST.setIpAddress("192.168.155.1");
            IP_ST.setFont(new Font(null, Font.PLAIN, 14));
            IP_ST.setLocation(120,40);
            IP_ST.setSize(200,30);
            jPanel.add(IP_ST);

            //setup M5_AP MAC address title label
            JLabel mac_AP = new JLabel("AP mac 地址 :",SwingConstants.LEFT);
            mac_AP.setFont(new Font(null, 1, 16));
            mac_AP.setLocation(10,90);
            mac_AP.setSize(115,30);
            jPanel.add(mac_AP);

            //setup the frequency combo box
            final JTextField macBox = new JTextField(SwingConstants.RIGHT);
            macBox.setLocation(120,90);
            macBox.setSize(200,30);
            macBox.setFont(new Font(null,1,16));
            jPanel.add(macBox);

            //add event listener when 'M5_AP' button clicked
            create.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int progress = 0;
                    String IPAddress_ST = IP_ST.getText();
                    String macAddress = macBox.getText();
                    tableModel.setValueAt(macAddress,tableModel.getRowCount()-1,5);
                    tableModel.setValueAt(IPAddress_ST,tableModel.getRowCount()-1,6);
                    tableModel.setValueAt(macAddress,tableModel.getRowCount()-1,7);

                    progress = new M5_Configuration().configM5("ST",commonFields.get(2),IPAddress_ST,commonFields.get(6),commonFields.get(5),null,macAddress,null);
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
                    dialog.dispose();
                }
            });
        }
        return jPanel;
    }


    /**
     * this method is to udpate row data
     * @param rowData the original row data
     */
    public void updateM2Dialog(List<String> rowData, final DefaultTableModel tableModel){

        final JDialog jDialog_updateRow = new JDialog(mainFrame,"更新 M2",true);
        jDialog_updateRow.setSize(500,500);
        jDialog_updateRow.setLocationRelativeTo(mainFrame);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
//        Image icon = kit.getImage(System.getProperty("user.dir")+ "\\icon\\logo.png");
        jDialog_updateRow.setIconImage(icon);

        JPanel M2Overlay_update = new JPanel(null);
        M2Overlay_update.setBorder((BorderFactory.createTitledBorder("M2 更新")));

        //setup way label
        final String[] ways = new String[]{"左线","右线"};
        JLabel wayLabel = new JLabel("设定线路 :",SwingConstants.LEFT);
        wayLabel.setFont(new Font(null, 1, 16));
        wayLabel.setLocation(20,40);
        wayLabel.setSize(115,30);
        M2Overlay_update.add(wayLabel);

        //setup way combo box
        final JComboBox<String> wayComboBox = new JComboBox<String>(ways);
        wayComboBox.setLocation(120,40);
        wayComboBox.setSize(200,30);
        wayComboBox.setFont(new Font(null, 1, 16));
        wayComboBox.setSelectedIndex(0);
        M2Overlay_update.add(wayComboBox);

        if (rowData != null && rowData.get(0).trim().equals("右线")){
            wayComboBox.setSelectedIndex(1);
        }else {
            wayComboBox.setSelectedIndex(0);
        }

        //setup the specific position label
        JLabel specificPosition = new JLabel("具体位置 :",SwingConstants.LEFT);
        specificPosition.setFont(new Font(null, 1, 16));
        specificPosition.setLocation(20,90);
        specificPosition.setSize(115,30);
        M2Overlay_update.add(specificPosition);

        //setup the specific position TextField
        final JTextField DKText = new JTextField(SwingConstants.RIGHT);
        DKText.setLocation(120,90);
        DKText.setSize(200,30);
        DKText.setFont(new Font(null,1,16));
        M2Overlay_update.add(DKText);

        if (rowData != null){
            if (rowData.get(1).trim().equals("null")){
                DKText.setText(null);
            }else {
                String dkMiles = rowData.get(1);
                DKText.setText(dkMiles);
                log.info("the DK mile is " + dkMiles);
            }
        }

        //setup M2 IP label:
        JLabel M2IPLabel = new JLabel("IP 地址 :",SwingConstants.LEFT);
        M2IPLabel.setFont(new Font(null, 1, 16));
        M2IPLabel.setLocation(20,140);
        M2IPLabel.setSize(115,30);
        M2Overlay_update.add(M2IPLabel);

        //setup M2 IP text field
        final JMIPV4AddressField IP = new JMIPV4AddressField();
        IP.setIpAddress("10.1.2.1");
        IP.setFont(new Font(null, Font.PLAIN, 14));
        IP.setLocation(120,140);
        IP.setSize(200,30);
        M2Overlay_update.add(IP);

        if (rowData.get(2).trim().equals("null")){
            IP.setText(null);
        }else {
            IP.setText(rowData.get(2));
        }

        //setup config button
        final JButton M2UpdateButton = new JButton("M2");
        M2UpdateButton.setFont(new Font(null,Font.BOLD,14));
        M2UpdateButton.setLocation(145,310);
        M2UpdateButton.setSize(105,30);
        M2Overlay_update.add(M2UpdateButton);

        M2UpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int progress = 0;
                String way = wayComboBox.getSelectedItem().toString();
                String DK = DKText.getText();
                String M2_IP = IP.getText();
                int targetRow = tableModel.getRowCount() -1;

                tableModel.setValueAt(way,targetRow,1);
                tableModel.setValueAt(DK,targetRow,2);

                progress = new M2_Configuration().configM2(commonFields.get(2),M2_IP,commonFields.get(4),commonFields.get(3),updatedIP_M2);
                tableModel.setValueAt(M2_IP,targetRow,3);

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
        jDialog_updateRow.setContentPane(M2Overlay_update);
        jDialog_updateRow.setVisible(true);

    }

    /**
     * create M2 dialog
     * @return the M2 configuration dialog
     */
    public JDialog createM2Dialog(final DefaultTableModel tableModel){

        //prepare the M2 create dialog
        final JDialog M2jDialog_create = new JDialog(mainFrame,"M2 配置页面",true);
        M2jDialog_create.setSize(450,500);
        M2jDialog_create.setLocationRelativeTo(mainFrame);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
//        Image icon = kit.getImage(System.getProperty("user.dir")+ "\\icon\\logo.png");
        M2jDialog_create.setIconImage(icon);

        //prepare the context panel
        JPanel M2Overlay_create = new JPanel(null);
        M2Overlay_create.setBorder((BorderFactory.createTitledBorder("M2 配置")));

        //setup way label
        final String[] ways = new String[]{"左线","右线"};
        JLabel wayLabel = new JLabel("设定线路 :",SwingConstants.LEFT);
        wayLabel.setFont(new Font(null, 1, 16));
        wayLabel.setLocation(20,40);
        wayLabel.setSize(115,30);
        M2Overlay_create.add(wayLabel);

        //setup way combo box
        final JComboBox<String> wayComboBox = new JComboBox<String>(ways);
        wayComboBox.setLocation(120,40);
        wayComboBox.setSize(200,30);
        wayComboBox.setFont(new Font(null, 1, 16));
        wayComboBox.setSelectedIndex(0);
        M2Overlay_create.add(wayComboBox);

        //setup the specific position label
        JLabel specificPosition = new JLabel("具体位置 :",SwingConstants.LEFT);
        specificPosition.setFont(new Font(null, 1, 16));
        specificPosition.setLocation(20,90);
        specificPosition.setSize(115,30);
        M2Overlay_create.add(specificPosition);

        //setup the specific position TextField
        final JTextField DKText = new JTextField(SwingConstants.RIGHT);
        DKText.setLocation(120,90);
        DKText.setSize(200,30);
        DKText.setFont(new Font(null,1,16));
        M2Overlay_create.add(DKText);

        //setup the max length of DK input box
        LimitedDocument ld = new LimitedDocument(5);
        ld.setAllowChar("0123456789");
        DKText.setDocument(ld);

        //setup M2 IP label:
        JLabel M2IPLabel = new JLabel("IP 地址 :",SwingConstants.LEFT);
        M2IPLabel.setFont(new Font(null, 1, 16));
        M2IPLabel.setLocation(20,140);
        M2IPLabel.setSize(115,30);
        M2Overlay_create.add(M2IPLabel);

        //setup M2 IP text field
        final JMIPV4AddressField IP = new JMIPV4AddressField();
        IP.setIpAddress("10.1.2.1");
        IP.setFont(new Font(null, Font.PLAIN, 14));
        IP.setLocation(120,140);
        IP.setSize(200,30);
        M2Overlay_create.add(IP);

        //setup config button
        final JButton M2ConfigButton = new JButton("M2");
        M2ConfigButton.setFont(new Font(null,Font.BOLD,14));
        M2ConfigButton.setLocation(145,310);
        M2ConfigButton.setSize(105,30);
        M2Overlay_create.add(M2ConfigButton);

        //add event listener to config button
        M2ConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int progress = 0;
                String way = wayComboBox.getSelectedItem().toString();
                String DK = DKText.getText();
                String M2_IP = IP.getText();
                int targetRow = tableModel.getRowCount() -1;

                tableModel.setValueAt(way,targetRow,1);
                tableModel.setValueAt(DK,targetRow,2);
                tableModel.setValueAt(M2_IP,targetRow,3);

                progress = new M2_Configuration().configM2(commonFields.get(2),M2_IP,commonFields.get(4),commonFields.get(3),null);

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
                M2jDialog_create.dispose();
            }
        });

        M2jDialog_create.setContentPane(M2Overlay_create);
        M2jDialog_create.setVisible(true);
        return M2jDialog_create;
    }

    /**
     * Create a new project dialog when "create_project" button clicked
     * @param projectTableModel
     */
    public JDialog generateNewProjectDilog(final DefaultTableModel projectTableModel, final JPanel commonFieldsPanel, final boolean isUpdate,final JPanel fieldValuePanel){

        //initialize new project dialog, the third parameter value is true ,means current dialog focused on the homepage,and
        //homepage only can ge clicked only if the project dialog was closed
        final JDialog newProjectDialog = new JDialog(mainFrame,"新建项目",true);
        newProjectDialog.setSize(470,630);
        newProjectDialog.setLocationRelativeTo(mainFrame);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
//        Image icon = kit.getImage(System.getProperty("user.dir")+"\\icon\\logo.png");
        Image icon = kit.getImage("D:\\icon\\logo.png");
        newProjectDialog.setIconImage(icon);

        //this JPanel was created for store all these components which will shown on the project dialog
        JPanel projectContainerPanel = new JPanel(null);
        projectContainerPanel.setBorder(BorderFactory.createTitledBorder("新建项目："));

        //project id label and corresponding text field
        JLabel projectNumLabel = new JLabel("项目编号 :");
        final JTextField projectNumInputBox = new JTextField();  // I need the input text, it will be used later
        projectNumLabel.setLocation(50,40);
        projectNumLabel.setSize(120,40);
        projectNumLabel.setFont(new Font(null, 1, 18));
        projectNumInputBox.setLocation(190,40);
        projectNumInputBox.setSize(200,40);
        projectNumInputBox.setFont(new Font(null, Font.PLAIN, 18));
        if (isUpdate){
            log.info(commonFields.get(0));
            projectNumInputBox.setText(commonFields.get(0));
        }
        projectContainerPanel.add(projectNumLabel);
        projectContainerPanel.add(projectNumInputBox);

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
        if (isUpdate){
            projectNameInputBox.setText(commonFields.get(1));
        }
        projectContainerPanel.add(projectNameLabel);
        projectContainerPanel.add(projectNameInputBox);
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
        if (isUpdate){
            ssidInputBox.setText(commonFields.get(2));
        }
        projectContainerPanel.add(ssidLabel);
        projectContainerPanel.add(ssidInputBox);

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
        if (isUpdate){
            gatewayIPInputBox.setText(commonFields.get(3));
        }
        projectContainerPanel.add(gatewayIP);
        projectContainerPanel.add(gatewayIPInputBox);

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
                                newProjectDialog,
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
        if (isUpdate){
            netMaskInputBox.setText(commonFields.get(4));
        }
        projectContainerPanel.add(netMaskLabel);
        projectContainerPanel.add(netMaskInputBox);

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
        if (isUpdate){
            gatewayIP_M5.setText(commonFields.get(5));
        }
        projectContainerPanel.add(gatewayIP_M5);
        projectContainerPanel.add(gatewayIP_M5_InputBox);

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
        if (isUpdate){
            netMask_M5_InputBox.setText(commonFields.get(6));
        }
        projectContainerPanel.add(netMaskLabel_M5);
        projectContainerPanel.add(netMask_M5_InputBox);

        //ok button
        JButton createProjectButton = new JButton(isUpdate?"修改":"创建");
        createProjectButton.setFont(new Font(null,Font.BOLD,16));
        createProjectButton.setLocation(100,530);
        createProjectButton.setSize(85,40);

        //cancel button
        JButton cancelButton = new JButton("取消");
        cancelButton.setFont(new Font(null,Font.BOLD,16));
        cancelButton.setLocation(265,530);
        cancelButton.setSize(85,40);

        //create project button event listener
        createProjectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    //save those all fields.
                    log.info("the project number is "+projectNumInputBox.getText());
                    commonFields.clear();
                    commonFields.add(projectNumInputBox.getText());
                    commonFields.add(projectNameInputBox.getText());
                    commonFields.add(ssidInputBox.getText());
                    commonFields.add(gatewayIPInputBox.getText());
                    commonFields.add(netMaskInputBox.getText());
                    commonFields.add(gatewayIP_M5_InputBox.getText());
                    commonFields.add(netMask_M5_InputBox.getText());
                if (!isUpdate){
                    //TODO: need to generate project row
                    Vector emptyProjectRow = new Vector();
                    for (int i = 0; i < 2; i++) {
                        emptyProjectRow.add(null);
                    }
                    projectTableModel.addRow(emptyProjectRow);
                    //fill up the project table with project number and name
                    projectTableModel.setValueAt(projectNumInputBox.getText(),projectTableModel.getRowCount()-1,0);
                    projectTableModel.setValueAt(projectNameInputBox.getText(),projectTableModel.getRowCount()-1,1);
                }


                //TODO: need to write the project info into the specific excel, in order to show these info on homepage once the app was running
                String projectExcelPath = Constant.Path_TestData_ProjectList;
//                String commonFieldsExcelPath = System.getProperty("user.dir")+"\\ConfigFile\\"+projectNameInputBox.getText() +"CommonFields.xlsx";
                String commonFieldsExcelPath = "D:\\ConfigFile\\"+projectNameInputBox.getText() +"CommonFields.xlsx";
                exportToExcel(projectTableModel,projectExcelPath,2);
                exportToExcel(null,commonFieldsExcelPath,7);

//                //initialize the field value panel, need to clear this panel each time
//                JPanel fieldValuePanel = new JPanel(null);
                fieldValuePanel.setLocation(135,40);
                fieldValuePanel.setSize(150,230);
                commonFieldsPanel.add(fieldValuePanel);
                commonFieldsPanel.remove(fieldValuePanel);

                // render the common fields value
                for (int i = 0; i < commonFieldsLabels.length; i++) {
                    if (commonFields.size() > 0){
                        log.info(commonFields.get(i+2));
                        JLabel commonFieldsValue  = new JLabel(commonFields.get(i+2));
                        commonFieldsValue.setLocation(0,40*i);
                        commonFieldsValue.setSize(140,30);
                        commonFieldsValue.setFont(new Font(null,Font.BOLD,16));
                        fieldValuePanel.add(commonFieldsValue);
                    }

                }
                newProjectDialog.dispose();
                commonFieldsPanel.add(fieldValuePanel);
                fieldValuePanel.repaint();
                SwingUtilities.updateComponentTreeUI(mainFrame);
            }
        });

        //cancel button event listener
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newProjectDialog.dispose();
            }
        });

        projectContainerPanel.add(createProjectButton);
        projectContainerPanel.add(cancelButton);

        projectContainerPanel.add(innerJPanelWifi);
        projectContainerPanel.add(innerJPanel_M5);

        newProjectDialog.setContentPane(projectContainerPanel);
        newProjectDialog.setVisible(true);
        return newProjectDialog;

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
