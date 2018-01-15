package gui.version.version02;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ubnt.m2.M2_Configuration_Absolute;
import utility.Constant_Relative;
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
public class UBNT {

    private final static Log log = LogFactory.getLog(UBNT.class);

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

    //the older IP which waiting for update
    private String updatedIP_M2;

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
        JLabel titleLabel = new JLabel("中继自动化配置",SwingConstants.CENTER);
        titleLabel.setSize(1050,100);
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
        importFromExcel(projectTableModel, Constant_Relative.Path_TestData_ProjectList);

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

        //navigate to config details page, add double click project event listener
        projectTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final JPanel M2_outermostPanel = showConfigRecordsPage();

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
                M2_outermostPanel.add(outermostHeaderPanel);

                // create the header dynamically
                JLabel currentPNumberTitle = new JLabel("当前项目编号：");
                currentPNumberTitle.setLocation(20,10);
                currentPNumberTitle.setSize(160,40);
                currentPNumberTitle.setFont(new Font(null,Font.BOLD,18));
                M2_outermostPanel.add(currentPNumberTitle);

                JLabel currentPNameTitle = new JLabel("当前项目名称：");
                currentPNameTitle.setLocation(240,10);
                currentPNameTitle.setSize(160,40);
                currentPNameTitle.setFont(new Font(null,Font.BOLD,18));
                M2_outermostPanel.add(currentPNameTitle);

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
                M2_outermostPanel.add(backwardButton);

                //add backward button event listener
                backwardButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //need remove these two JLabel to make sure every time these two label are new added to outPanel
                        M2_outermostPanel.remove(currentPName);
                        M2_outermostPanel.remove(currentPNumber);
                        tableModel_M2.getDataVector().clear();
                        M2_outermostPanel.setVisible(false);
                        homepagePanel.setVisible(true);
                        mainFrame.setContentPane(homepagePanel);
                    }

                });

                //double click
                if (e.getClickCount() == 2){
                    homepagePanel.setVisible(false);
                    showConfigRecordsPage().setVisible(true);
                    mainFrame.setContentPane(M2_outermostPanel);
                    int targetRow = projectTable.getSelectedRow();
                    if (targetRow >= 0){
                        //set the value to these two global fields
                        pNumber = projectTableModel.getValueAt(targetRow,0).toString();
                        pName = projectTableModel.getValueAt(targetRow,1).toString();
                        String specificExcel = "D:\\ConfigFile\\M2\\"+pName +".xlsx";
//                        String specificExcel = System.getProperty("user.dir")+ "\\ConfigFile\\"+pName +".xlsx";
                        String SpecificProjectCommonField = "D:\\ConfigFile\\"+pName +"CommonFields.xlsx";
//                        String SpecificProjectCommonField = System.getProperty("user.dir")+ "\\ConfigFile\\"+pName +"CommonFields.xlsx";
                        File projectCorresspondingConfigFile = new File(specificExcel);
                        File projectCommonFieldFile = new File(SpecificProjectCommonField);
                        if (!projectCorresspondingConfigFile.exists()){
                            exportToExcel(tableModel_M2,"D:\\ConfigFile\\M2\\"+pName+".xlsx",4);
//                            exportToExcel(null,System.getProperty("user.dir")+ "\\ConfigFile\\"+pName+".xlsx",9);
                        }
                        if (projectCorresspondingConfigFile.exists() && projectCommonFieldFile.exists()){
                            //import table rows on main page
                            importFromExcel(tableModel_M2 ,specificExcel);
//                            jTable.setModel(defaultTableModel);
                            //import specific project common fields
                            //TODO: this time not to render table but to override commonfields.
                            importFromExcel(null,SpecificProjectCommonField);
                            //import project list excel, in order to show the project name and number on the main page
                        }

                        M2_outermostPanel.add(currentPName);
                        M2_outermostPanel.add(currentPNumber);
                        currentPName.setText(pName);
                        currentPNumber.setText(pNumber);

                    }else {
                        //TODO: may show waring message here.
                    }
                }
            }
        });


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
//                exportToExcel(defautTableModel,System.getProperty("user.dir")+ "\\ConfigFile\\"+pName+".xlsx",9);
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
        centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
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
















        outermostContainerPanel.add(M2ContainerPanel);
        outermostContainerPanel.add(M5ContainerPanel);
        return outermostContainerPanel;
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
        jTabbedPane_M5.add("位置",createM5Panel("位置",tableModel));
        jTabbedPane_M5.add("M5_AP",createM5Panel("M5_AP",tableModel));
        jTabbedPane_M5.add("M5_ST",createM5Panel("M5_ST",tableModel));

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
    public JPanel createM5Panel(String tabName, DefaultTableModel tableModel){
        JPanel jPanel = new JPanel(null);
        jPanel.setBorder((BorderFactory.createTitledBorder("UBNT( "+tabName+") 配置")));

        if (tabName.trim().equals("M5_AP")){





        }



        return null;
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

                if (!updatedIP_M2.trim().equals(M2_IP.trim())){
                    progress = new M2_Configuration_Absolute().configM2(commonFields.get(2),M2_IP,commonFields.get(4),commonFields.get(3),updatedIP_M2);
                    tableModel.setValueAt(M2_IP,targetRow,3);
                }else {
                    progress = 1;
                }

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

                progress = new M2_Configuration_Absolute().configM2(commonFields.get(2),M2_IP,commonFields.get(4),commonFields.get(3),null);

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
    public JDialog generateNewProjectDilog(final DefaultTableModel projectTableModel){

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
        projectContainerPanel.add(netMaskLabel_M5);
        projectContainerPanel.add(netMask_M5_InputBox);

        //ok button
        JButton createProjectButton = new JButton("创建");
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
                commonFields.add(projectNumInputBox.getText());
                commonFields.add(projectNameInputBox.getText());
                commonFields.add(ssidInputBox.getText());
                commonFields.add(gatewayIPInputBox.getText());
                commonFields.add(netMaskInputBox.getText());
                commonFields.add(gatewayIP_M5_InputBox.getText());
                commonFields.add(netMask_M5_InputBox.getText());

                //TODO: need to generate project row
                Vector emptyProjectRow = new Vector();
                for (int i = 0; i < 2; i++) {
                    emptyProjectRow.add(null);
                }
                projectTableModel.addRow(emptyProjectRow);

                //fill up the project table with project number and name
                projectTableModel.setValueAt(projectNumInputBox.getText(),projectTableModel.getRowCount()-1,0);
                projectTableModel.setValueAt(projectNameInputBox.getText(),projectTableModel.getRowCount()-1,1);

                //TODO: need to write the project info into the specific excel, in order to show these info on homepage once the app was running
                String projectExcelPath = Constant_Relative.Path_TestData_ProjectList;
//                String commonFieldsExcelPath = System.getProperty("user.dir")+"\\ConfigFile\\"+projectNameInputBox.getText() +"CommonFields.xlsx";
                String commonFieldsExcelPath = "D:\\ConfigFile\\"+projectNameInputBox.getText() +"CommonFields.xlsx";
                exportToExcel(projectTableModel,projectExcelPath,2);
                exportToExcel(null,commonFieldsExcelPath,7);

                newProjectDialog.dispose();
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
