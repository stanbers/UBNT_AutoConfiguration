package gui.draft;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * @Author by XuLiang
 * @Date 2017/12/28 7:58
 * @Email stanxu526@gmail.com
 */
public class Test03 {
    public static void main(String[] args) {
        JFrame jf = new JFrame();
        jf.setSize(940, 600);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //setup the logo icon
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon = kit.getImage("D:\\icon\\logo.png");
        jf.setIconImage(icon);

        final JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.setFont(new Font(null, Font.PLAIN, 18));
        jTabbedPane.add("中继",createTextPanel("中继"));



        jTabbedPane.setSelectedIndex(0);
        jf.setContentPane(jTabbedPane);
        jf.setVisible(true);
    }

    public static JPanel createTextPanel(String tabName){
        TableModel myData = new MyTableModel();
        JTable jTable = new JTable(myData);

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


        return jPanel;
    }

    public static class MyTableModel extends AbstractTableModel {
        Object[] columnNames = {"编号","位置","M2 IP", "M5_Ap IP", "M5_AP 频率", "M5_AP mac地址", "M5_ST IP","M5_ST 锁定mac地址","操作"};  // 7 columns
        Object[][] rowData = {
                {1,"左线","10.1.2.100", "192.168.155.10", "5820", "44:D9:E7:48:94:CB", "192.168.155.50", "44:D9:E7:48:94:CB","更改"},
                {2,"左线","10.1.2.101", "192.168.155.11", "5840", "44:D9:E7:48:94:CD", "192.168.155.51", "44:D9:E7:48:94:CD","更改"},
                {3,"左线","10.1.2.102", "192.168.155.12", "5860", "44:D9:E7:48:94:CE", "192.168.155.52", "44:D9:E7:48:94:CE","更改"},
                {4,"右线","10.1.2.103", "192.168.155.13", "5880", "44:D9:E7:48:94:CF", "192.168.155.53", "44:D9:E7:48:94:CF","更改"},
                {5,"右线","10.1.2.104", "192.168.155.14", "5900", "44:D9:E7:48:94:CG", "192.168.155.54", "44:D9:E7:48:94:CG","更改"},
        };

        /**
         * 返回总行数
         */
        @Override
        public int getRowCount() {
            return rowData.length;
        }

        /**
         * 返回总列数
         */
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * 返回列名称（表头名称），AbstractTableModel 中对该方法的实现默认是以
         * 大写字母 A 开始作为列名显示，所以这里需要重写该方法返回我们需要的列名。
         */
        @Override
        public String getColumnName(int column) {
            return columnNames[column].toString();
        }

        /**
         * 返回指定单元格的显示的值
         */
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return rowData[rowIndex][columnIndex];
        }
    }

}
