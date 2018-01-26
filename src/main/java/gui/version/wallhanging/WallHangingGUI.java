package gui.version.wallhanging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
public class WallHangingGUI {

    private final static Log log = LogFactory.getLog(WallHangingGUI.class);

    private JFrame mainFrame = new JFrame("配置页面");

    //create for store the common fields
    private List<String> commonFields = new ArrayList<String>();

    //init record index
    private int recordIndex = 1;

    //initialize wall hanging table header
    final String[] columns_wall = {"编号","线路","位置","壁挂 IP"};

    //define wall hanging table model
    private DefaultTableModel tableModel_wall = new DefaultTableModel(null,columns_wall){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    //define the global fields
    private String pName;
    private String pNumber;

    //the older IP which waiting for update, need this older ip to login
    private String olderIP_wall;

    /**
     * To render homepage, include project table and create project dialog
     */
    public void showHomepage(){
        //setup container's size and location
        mainFrame.setSize(800, 700);
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
        JLabel titleLabel = new JLabel("壁挂自动化配置",SwingConstants.CENTER);
        titleLabel.setSize(800,100);
        titleLabel.setFont(new Font(null,Font.BOLD, 60));
        homepagePanel.add(titleLabel);

        //setup project table title
        JLabel projectListLabel = new JLabel("项目列表：");
        projectListLabel.setLocation(90,155);
        projectListLabel.setSize(200,40);
        projectListLabel.setFont(new Font(null,BOLD,20));

        //store table records
        final JTable projectTable = new JTable(projectTableModel);
        projectTable.setLocation(100,210);
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
        projectjTableHeader.setLocation(100,150);
        projectjTableHeader.setPreferredSize(new Dimension(300,50));
        projectjTableHeader.setFont(new Font(null, Font.BOLD, 16));
        projectjTableHeader.setResizingAllowed(true);
        projectjTableHeader.setReorderingAllowed(true);

        //project container
        JScrollPane projectTableContainer = new JScrollPane(projectTable);
        projectTableContainer.setLocation(90,200);
        projectTableContainer.setSize(400,300);

        //import projects from project_list excel file, and render projects to projectTable
        importFromExcel(projectTableModel, Constant.Path_TestData_ProjectList);

        //setup project button
        JButton createPojectButton = new JButton("+ 新建项目");
        createPojectButton.setLocation(500,440);
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
                final JPanel outermostPanel = showConfigRecordsPage(pName,pNumber,tableModel_wall,commonFields);

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
                outermostHeaderPanel.setSize(760,13);
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
                backwardButton.setLocation(640,15);
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
                        tableModel_wall.getDataVector().clear();
                        outermostPanel.setVisible(false);
                        homepagePanel.setVisible(true);
                        mainFrame.setContentPane(homepagePanel);
                    }

                });

                //double click
                if (e.getClickCount() == 2){
                    homepagePanel.setVisible(false);
                    showConfigRecordsPage(pName,pNumber,tableModel_wall,commonFields).setVisible(true);
                    mainFrame.setContentPane(outermostPanel);
                    int targetRow = projectTable.getSelectedRow();
                    //set the value to these two global fields
                    pNumber = projectTableModel.getValueAt(targetRow,0).toString();
                    pName = projectTableModel.getValueAt(targetRow,1).toString();
                    String specificExcel_wall = "D:\\ConfigFile\\wall\\"+pName +".xlsx";
//                        String specificExcel_wall = System.getProperty("user.dir")+ "\\ConfigFile\\M2\\"+pName +".xlsx";
                    String SpecificProjectCommonField = "D:\\ConfigFile\\"+pName +"CommonFields.xlsx";
//                        String SpecificProjectCommonField = System.getProperty("user.dir")+ "\\ConfigFile\\"+pName +"CommonFields.xlsx";
                    File projectCorresspondingConfigFile_wall = new File(specificExcel_wall);
                    File projectCommonFieldFile = new File(SpecificProjectCommonField);
                    if (!projectCorresspondingConfigFile_wall.exists()){
                        exportToExcel(tableModel_wall,"D:\\ConfigFile\\wall\\"+pName+".xlsx",4);
//                            exportToExcel(tableModel_M2,System.getProperty("user.dir")+ "\\ConfigFile\\M2\\"+pName+".xlsx",4);
                    }
                    if (projectCorresspondingConfigFile_wall.exists() && projectCommonFieldFile.exists()){
                        //import table rows on main page
                        importFromExcel(tableModel_wall ,specificExcel_wall);
//                            jTable.setModel(defaultTableModel);
                        //import specific project common fields
                        //TODO: this time not to render table but to override commonfields.
                        importFromExcel(null,SpecificProjectCommonField);
                        //import project list excel, in order to show the project name and number on the main page
                    }

                    outermostPanel.add(currentPName);
                    outermostPanel.add(currentPNumber);
                    currentPName.setText(pName);
                    currentPNumber.setText(pNumber);

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
    public JPanel showConfigRecordsPage(String pName_wall,String pNumber_wall,DefaultTableModel tableModel,List<String> commonFields){
        this.pName = pName_wall;
        this.pNumber = pNumber_wall;
        this.tableModel_wall = tableModel;
        this.commonFields = commonFields;
        //TODO: this page will include two main panels, one is M2 container another one is M5 container
        //this panel the outermost panel container, no need to set location and size
        JPanel outermostContainerPanel = new JPanel(null);
//        outermostContainerPanel.setBorder(BorderFactory.createTitledBorder("中继配置记录："));

        //M2 records container
        JPanel WallContainerPanel = new JPanel(null);
        WallContainerPanel.setLocation(10,60);
        WallContainerPanel.setSize(480,550);
        WallContainerPanel.setBorder(BorderFactory.createTitledBorder(null,"壁挂 配置记录：", TitledBorder.LEFT,TitledBorder.TOP,new Font(null,Font.BOLD,15)));
        WallContainerPanel.setLayout(null);

        //create a JTable to record the configuration info
        final JTable jTable_wall = new JTable(tableModel_wall);
        jTable_wall.setLocation(20,10);
        jTable_wall.setSize(440,450);
        jTable_wall.setRowHeight(25);

        //setup column width
        jTable_wall.getColumn("编号").setMaxWidth(45);
        jTable_wall.getColumn("位置").setMaxWidth(80);
        jTable_wall.getColumn("线路").setMaxWidth(45);
        jTable_wall.getColumn("壁挂 IP").setPreferredWidth(30);
        jTable_wall.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        jTable_wall.setFont(new Font(null, Font.PLAIN, 15));

        //setup cell context align center
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
        jTable_wall.getColumn("编号").setCellRenderer(centerRenderer);
        jTable_wall.getColumn("位置").setCellRenderer(centerRenderer);
        jTable_wall.getColumn("线路").setCellRenderer(centerRenderer);
        jTable_wall.getColumn("壁挂 IP").setCellRenderer(centerRenderer);


        //setup wall hanging table header
        JTableHeader jTableHeader_wall = jTable_wall.getTableHeader();
        jTableHeader_wall.setLocation(20,10);
        jTableHeader_wall.setSize(440,30);
        jTableHeader_wall.setFont(new Font(null, Font.BOLD, 16));
        jTableHeader_wall.setResizingAllowed(true);
        jTableHeader_wall.setReorderingAllowed(true);

        //setup M2 table context scrollable
        final JScrollPane tablePanel_wall = new JScrollPane(jTable_wall);
        tablePanel_wall.setLocation(15,40);
        tablePanel_wall.setSize(440,450);
        WallContainerPanel.add(tablePanel_wall);

        //setup new create button
        JButton createWall = new JButton("新建");
        createWall.setFont(new Font(null,Font.BOLD,14));
        createWall.setLocation(355,500);
        createWall.setSize(80,30);
        WallContainerPanel.add(createWall);

        //setup remove button
        JButton removeWall = new JButton("删除");
        removeWall.setLocation(245,500);
        removeWall.setSize(80,30);
        removeWall.setFont(new Font(null,Font.BOLD,14));
        WallContainerPanel.add(removeWall);

        //setup export wall hanging records button
        JButton exportWallRecords = new JButton("导出");
        exportWallRecords.setFont(new Font(null,Font.BOLD,14));
        exportWallRecords.setLocation(15,500);
        exportWallRecords.setSize(80,30);
        WallContainerPanel.add(exportWallRecords);

        //add export all wall hanging config records
        exportWallRecords.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToExcel(tableModel_wall,"D:\\ConfigFile\\wall\\"+pName+".xlsx",4);
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
        createWall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                createWallDialog(tableModel_wall);
            }
        });

        removeWall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int remove  = JOptionPane.showConfirmDialog(mainFrame,"确定删除吗？");
                log.info("return int value is " + remove);
                if (remove == 0){

                    //TODO: need to change the row number, but how ? --> done
                    int selectedRowNumber = jTable_wall.getSelectedRow();
                    int allRowsCount = tableModel_wall.getRowCount();
                    int remainingRows = allRowsCount - selectedRowNumber;
                    log.info("the remaining rows are : " + remainingRows + "; all rows : " + allRowsCount + " ;" + selectedRowNumber + " was selected!");
                    log.info(jTable_wall.getSelectedRow() +" row was deleting");
                    for (int i = 1; i < remainingRows; i++) {
                        tableModel_wall.setValueAt(selectedRowNumber + i,selectedRowNumber+i,0);
                    }
                    if (jTable_wall.getSelectedRow() >= 0){
                        tableModel_wall.removeRow(jTable_wall.getSelectedRow());
                    }else {
                        JOptionPane.showMessageDialog(mainFrame,"目前没有可以被删除的记录 ！");
                    }
                }
            }
        });

        jTable_wall.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){

                    //the whole cell of a specific row are stored in this list.
                    List<String> cellValuesOfSpecificRow = new ArrayList<String>();
                    int rowNum = jTable_wall.getSelectedRow();
                    //i started from 1, cause no to edit row number.
                    log.info("the " + rowNum + "th row was selected !");
                    for (int i = 1; i <= 3; i++) {
                        int a = cellValuesOfSpecificRow.size();
                        log.info("row has " +a +"columns");
                        if (tableModel_wall.getValueAt(rowNum,i) != null){
                            cellValuesOfSpecificRow.add(tableModel_wall.getValueAt(rowNum,i).toString());
                        }else {
                            cellValuesOfSpecificRow.add("null");
                        }
                    }

                    //TODO: get the wall hanging olderIP,
                    if (tableModel_wall.getValueAt(rowNum,3) != null){
                        olderIP_wall = tableModel_wall.getValueAt(rowNum,3).toString();
                    }
                    updateWallDialog(cellValuesOfSpecificRow,tableModel_wall);
                }
            }
        });

        outermostContainerPanel.add(WallContainerPanel);
        return outermostContainerPanel;
    }

    /**
     * this method is to udpate row data
     * @param rowData the original row data
     */
    public void updateWallDialog(List<String> rowData, final DefaultTableModel tableModel){

        final JDialog jDialog_updateRow = new JDialog(mainFrame,"更新 M2",true);
        jDialog_updateRow.setSize(450,460);
        jDialog_updateRow.setLocationRelativeTo(mainFrame);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
//        Image icon = kit.getImage(System.getProperty("user.dir")+ "\\icon\\logo.png");
        jDialog_updateRow.setIconImage(icon);

        JPanel wallOverlay_update = new JPanel(null);
        wallOverlay_update.setBorder((BorderFactory.createTitledBorder("壁挂 更新")));

        //setup way label
        final String[] ways = new String[]{"左线","右线"};
        JLabel wayLabel = new JLabel("设定线路 :",SwingConstants.LEFT);
        wayLabel.setFont(new Font(null, 1, 16));
        wayLabel.setLocation(20,40);
        wayLabel.setSize(135,30);
        wallOverlay_update.add(wayLabel);

        //setup way combo box
        final JComboBox<String> wayComboBox = new JComboBox<String>(ways);
        wayComboBox.setLocation(160,40);
        wayComboBox.setSize(200,30);
        wayComboBox.setFont(new Font(null, 1, 16));
        wayComboBox.setSelectedIndex(0);
        wallOverlay_update.add(wayComboBox);

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
        wallOverlay_update.add(specificPosition);

        //setup the specific position TextField
        final JTextField DKText = new JTextField(SwingConstants.RIGHT);
        DKText.setLocation(160,90);
        DKText.setSize(200,30);
        DKText.setFont(new Font(null,1,16));
        wallOverlay_update.add(DKText);

        if (rowData != null){
            if (rowData.get(1).trim().equals("null")){
                DKText.setText(null);
            }else {
                String dkMiles = rowData.get(1);
                DKText.setText(dkMiles);
                log.info("the DK mile is " + dkMiles);
            }
        }

        //setup wall hanging IP label:
        JLabel wallIPLabel = new JLabel("壁挂 IP 地址 :",SwingConstants.LEFT);
        wallIPLabel.setFont(new Font(null, 1, 16));
        wallIPLabel.setLocation(20,140);
        wallIPLabel.setSize(135,30);
        wallOverlay_update.add(wallIPLabel);

        //setup wall hanging IP text field
        final JMIPV4AddressField IP_wall = new JMIPV4AddressField();
        IP_wall.setIpAddress("10.1.2.1");
        IP_wall.setFont(new Font(null, Font.PLAIN, 14));
        IP_wall.setLocation(160,140);
        IP_wall.setSize(200,30);
        wallOverlay_update.add(IP_wall);

        if (rowData.get(2).trim().equals("null")){
            IP_wall.setText(null);
        }else {
            IP_wall.setText(rowData.get(2));
        }

//        //setup wall hanging server IP label:
//        JLabel wallIPLabel_server = new JLabel("服务器 IP 地址 :",SwingConstants.LEFT);
//        wallIPLabel_server.setFont(new Font(null, 1, 16));
//        wallIPLabel_server.setLocation(20,190);
//        wallIPLabel_server.setSize(135,30);
//        wallOverlay_update.add(wallIPLabel_server);
//
//        //setup wall hanging server IP text field
//        final JMIPV4AddressField IP_wall_server = new JMIPV4AddressField();
//        IP_wall_server.setIpAddress("10.1.2.1");
//        IP_wall_server.setFont(new Font(null, Font.PLAIN, 14));
//        IP_wall_server.setLocation(160,190);
//        IP_wall_server.setSize(200,30);
//        wallOverlay_update.add(IP_wall_server);
//
//        if (rowData.get(3).trim().equals("null")){
//            IP_wall_server.setText(null);
//        }else {
//            IP_wall_server.setText(rowData.get(3));
//        }

        //setup config button
        final JButton wallUpdateButton = new JButton("壁挂");
        wallUpdateButton.setFont(new Font(null,Font.BOLD,14));
        wallUpdateButton.setLocation(145,310);
        wallUpdateButton.setSize(105,30);
        wallOverlay_update.add(wallUpdateButton);

        wallUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int progress = 0;
                String way = wayComboBox.getSelectedItem().toString();
                String DK = DKText.getText();
                String wall_IP = IP_wall.getText();
                int targetRow = tableModel.getRowCount() -1;

                tableModel.setValueAt(way,targetRow,1);
                tableModel.setValueAt(DK,targetRow,2);
                log.info("wall hanging ssid is "+commonFields.get(0));
                log.info("wall hanging IP is "+wall_IP);
                log.info("wall hanging server ip is "+commonFields.get(1));
                log.info("wall hanging gateway ip is "+commonFields.get(2));
                log.info("wall hanging net mask is "+commonFields.get(3));
//                progress = new WallHangingConfig().config(commonFields.get(0),olderIP_wall,commonFields.get(3),commonFields.get(2),commonFields.get(1));
                tableModel.setValueAt(wall_IP,targetRow,3);

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
        jDialog_updateRow.setContentPane(wallOverlay_update);
        jDialog_updateRow.setVisible(true);

    }

    /**
     * create M2 dialog
     * @return the M2 configuration dialog
     */
    public JDialog createWallDialog(final DefaultTableModel tableModel){

        //prepare the Wall create dialog
        final JDialog walljDialog_create = new JDialog(mainFrame,"壁挂 配置页面",true);
        walljDialog_create.setSize(450,460);
        walljDialog_create.setLocationRelativeTo(mainFrame);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
//        Image icon = kit.getImage(System.getProperty("user.dir")+ "\\icon\\logo.png");
        walljDialog_create.setIconImage(icon);

        //prepare the context panel
        JPanel wallOverlay_create = new JPanel(null);
        wallOverlay_create.setBorder((BorderFactory.createTitledBorder("壁挂 配置")));

        //setup way label
        final String[] ways = new String[]{"左线","右线"};
        JLabel wayLabel = new JLabel("设定线路 :",SwingConstants.LEFT);
        wayLabel.setFont(new Font(null, 1, 16));
        wayLabel.setLocation(20,40);
        wayLabel.setSize(135,30);
        wallOverlay_create.add(wayLabel);

        //setup way combo box
        final JComboBox<String> wayComboBox = new JComboBox<String>(ways);
        wayComboBox.setLocation(160,40);
        wayComboBox.setSize(200,30);
        wayComboBox.setFont(new Font(null, 1, 16));
        wayComboBox.setSelectedIndex(0);
        wallOverlay_create.add(wayComboBox);

        //setup the specific position label
        JLabel specificPosition = new JLabel("具体位置 :",SwingConstants.LEFT);
        specificPosition.setFont(new Font(null, 1, 16));
        specificPosition.setLocation(20,90);
        specificPosition.setSize(135,30);
        wallOverlay_create.add(specificPosition);

        //setup the specific position TextField
        final JTextField DKText = new JTextField(SwingConstants.RIGHT);
        DKText.setLocation(160,90);
        DKText.setSize(200,30);
        DKText.setFont(new Font(null,1,16));
        wallOverlay_create.add(DKText);

        //setup the max length of DK input box
        LimitedDocument ld = new LimitedDocument(5);
        ld.setAllowChar("0123456789");
        DKText.setDocument(ld);

        //setup wall hanging IP label:
        JLabel wallIPLabel = new JLabel("壁挂 IP 地址 :",SwingConstants.LEFT);
        wallIPLabel.setFont(new Font(null, 1, 16));
        wallIPLabel.setLocation(20,140);
        wallIPLabel.setSize(135,30);
        wallOverlay_create.add(wallIPLabel);

        //setup wall hanging IP text field
        final JMIPV4AddressField IP = new JMIPV4AddressField();
        IP.setIpAddress("10.1.2.1");
        IP.setFont(new Font(null, Font.PLAIN, 14));
        IP.setLocation(160,140);
        IP.setSize(200,30);
        wallOverlay_create.add(IP);

//        //setup wall hanging server IP label:
//        JLabel wallIPLabel_server = new JLabel("服务器 IP 地址 :",SwingConstants.LEFT);
//        wallIPLabel_server.setFont(new Font(null, 1, 16));
//        wallIPLabel_server.setLocation(20,190);
//        wallIPLabel_server.setSize(135,30);
//        wallOverlay_create.add(wallIPLabel_server);
//
//        //setup wall hanging server IP text field
//        final JMIPV4AddressField IP_server = new JMIPV4AddressField();
//        IP_server.setIpAddress("10.1.2.0");
//        IP_server.setFont(new Font(null, Font.PLAIN, 14));
//        IP_server.setLocation(160,190);
//        IP_server.setSize(200,30);
//        wallOverlay_create.add(IP_server);

        //setup config button
        final JButton wallConfigButton = new JButton("壁挂");
        wallConfigButton.setFont(new Font(null,Font.BOLD,14));
        wallConfigButton.setLocation(145,310);
        wallConfigButton.setSize(105,30);
        wallOverlay_create.add(wallConfigButton);

        //add event listener to config button
        wallConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Vector emptyRow = new Vector();
                for (int i = 0; i < 10; i++) {
                    emptyRow.add(null);
                }
                tableModel_wall.addRow(emptyRow);
                if (recordIndex == 0){
                    tableModel_wall.setValueAt(recordIndex++,tableModel_wall.getRowCount()-1,0);
                }else {
                    recordIndex = tableModel_wall.getRowCount();
                    tableModel_wall.setValueAt(recordIndex++,tableModel_wall.getRowCount()-1,0);
                }
                int progress = 0;
                String way = wayComboBox.getSelectedItem().toString();
                String DK = DKText.getText();
                String wall_IP = IP.getText();
                int targetRow = tableModel.getRowCount() -1;

                tableModel.setValueAt(way,targetRow,1);
                tableModel.setValueAt(DK,targetRow,2);
                tableModel.setValueAt(wall_IP,targetRow,3);

                log.info("wall hanging ssid is "+commonFields.get(0));
                log.info("wall hanging IP is "+wall_IP);
                log.info("wall hanging server ip is "+commonFields.get(1));
                log.info("wall hanging gateway ip is "+commonFields.get(2));
                log.info("wall hanging net mask is "+commonFields.get(3));
                //String ssidName, String wallHangingIP, String wallHangingNetmask, String wallHangingGatewayIP, String serverIP
//                progress = new WallHangingConfig().config(commonFields.get(0),wall_IP,commonFields.get(3),commonFields.get(2),commonFields.get(1));

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
                walljDialog_create.dispose();
            }
        });

        walljDialog_create.setContentPane(wallOverlay_create);
        walljDialog_create.setVisible(true);
        return walljDialog_create;
    }

    /**
     * Create a new project dialog when "create_project" button clicked
     * @param projectTableModel
     */
    public JDialog generateNewProjectDilog(final DefaultTableModel projectTableModel){

        //initialize new project dialog, the third parameter value is true ,means current dialog focused on the homepage,and
        //homepage only can ge clicked only if the project dialog was closed
        final JDialog newProjectDialog = new JDialog(mainFrame,"新建项目",true);
        newProjectDialog.setSize(470,500);
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
        ssidInputBox.setText("wallhanging");
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


        //ok button
        JButton createProjectButton = new JButton("创建");
        createProjectButton.setFont(new Font(null,Font.BOLD,16));
        createProjectButton.setLocation(100,380);
        createProjectButton.setSize(85,40);

        //cancel button
        JButton cancelButton = new JButton("取消");
        cancelButton.setFont(new Font(null,Font.BOLD,16));
        cancelButton.setLocation(265,380);
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
                String projectExcelPath = Constant.Path_TestData_ProjectList;
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
