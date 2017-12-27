package gui;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * @Author by XuLiang
 * @Date 2017/12/27 15:03
 * @Email stanxu526@gmail.com
 */
public class AutoSprint02 {
    public static void main(String[] args) {
        JFrame jf = new JFrame();
        jf.setSize(940, 600);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
        jf.setIconImage(icon);

        //create tab bar
        final JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.setFont(new Font(null, Font.PLAIN, 18));
        jTabbedPane.add("中继",createTextPanel("中继"));
        jTabbedPane.add("壁挂",createTextPanel("壁挂"));
        jTabbedPane.add("路由器",createTextPanel("路由器"));


        jTabbedPane.setSelectedIndex(0);
        jf.setContentPane(jTabbedPane);
        jf.setVisible(true);


    }

    public static JPanel createTextPanel(String tabName){
        //create JPanel
        JPanel jPanel = new JPanel(null);
        jPanel.setBorder((BorderFactory.createTitledBorder(tabName+" 配置")));
        jPanel.setLayout(null);

        //create JTable
        Object[] columnNames = {"编号","位置","M2 IP", "M5_Ap IP", "M5_AP 频率", "M5_AP mac地址", "M5_ST IP","M5_ST 锁定mac地址","操作"};  // 7 columns
        Object[][] rowData = {
                {1,"左线","10.1.2.100", "192.168.155.10", "5820", "44:D9:E7:48:94:CB", "192.168.155.50", "44:D9:E7:48:94:CB","更改"},
                {2,"左线","10.1.2.101", "192.168.155.11", "5840", "44:D9:E7:48:94:CD", "192.168.155.51", "44:D9:E7:48:94:CD","更改"},
                {3,"左线","10.1.2.102", "192.168.155.12", "5860", "44:D9:E7:48:94:CE", "192.168.155.52", "44:D9:E7:48:94:CE","更改"},
                {4,"右线","10.1.2.103", "192.168.155.13", "5880", "44:D9:E7:48:94:CF", "192.168.155.53", "44:D9:E7:48:94:CF","更改"},
                {5,"右线","10.1.2.104", "192.168.155.14", "5900", "44:D9:E7:48:94:CG", "192.168.155.54", "44:D9:E7:48:94:CG","更改"},
        };
        JTable jTable = new JTable(rowData,columnNames);
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

        //create checkbox
        JCheckBox jCheckBox = new JCheckBox();// how to add this checkbox to the beginning of the line

        //not work !  ---> missing table header ,why ? ---> resolved , cause it has to setup the table header's size and location as well.
        JTableHeader jTableHeader = jTable.getTableHeader();
        jTableHeader.setLocation(20,30);
        jTableHeader.setSize(850,30);
        jTableHeader.setFont(new Font(null, Font.BOLD, 16));
        jTableHeader.setResizingAllowed(true);
        jTableHeader.setReorderingAllowed(true);

        jPanel.add(jTableHeader,BorderLayout.NORTH);
        jPanel.add(jTable,BorderLayout.CENTER);



        jPanel.add(jTable);

        return jPanel;
    }
}
