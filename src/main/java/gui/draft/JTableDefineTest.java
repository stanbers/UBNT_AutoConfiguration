package gui.draft;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

/**
 * @Author by XuLiang
 * @Date 2017/12/28 8:51
 * @Email stanxu526@gmail.com
 */
public class JTableDefineTest extends JFrame {
    private int currentPage=1;
    private  int pageSize=2;
    private int lastPage;
    JTable table=null;
    DefaultTableModel dtm=null;
    JScrollPane jsp=null;
    JTableDefineTest jTableDefineTest=null;
    List list,list1;
    JButton button1 =null;

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public JTableDefineTest(){

        list=FenYeTest.list;

        if(list.size()%pageSize==0){
            setLastPage(list.size()/getPageSize());
        }else{
            setLastPage(list.size()/getPageSize()+1);
        }


        String[] columnNames = {"用户名","密码"};
        dtm=new DefaultTableModel(columnNames, 0);

        table=new JTable(dtm);
        jsp = new JScrollPane();
        jsp.setViewportView(table);
        getContentPane().add(jsp);

        setTitle("表格");
        setBounds(100,100,500,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        showTable(currentPage);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.NORTH);

        JButton button = new JButton("首页");
        button.addActionListener(new MyTable());
        button.setActionCommand("首页");
        panel.add(button);
        button1 = new JButton("上一页");
        button1.addActionListener(new MyTable());
        panel.add(button1);
        JButton button2 = new JButton("下一页");
        button2.addActionListener(new MyTable());
        panel.add(button2);
        JButton button3 = new JButton("末页");
        button3.addActionListener(new MyTable());
        panel.add(button3);
        setVisible(true);

    }

    public void showTable(int currentPage){
        dtm.setRowCount(0);// 清除原有行
        FenYeTest f=new FenYeTest();
        setCurrentPage(currentPage);
        list1=f.findUsers(currentPage, pageSize);
        for(int row = 0;row<list1.size();row++)    //获得数据
        {
            Vector rowV = new Vector();
            User user= (User) list1.get(row);
            rowV.add(user.getName());  //数据
            rowV.add(user.getPass());
            dtm.addRow(rowV);
        }


        //  table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);  //关闭表格列的自动调整功能。
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);   //单选
        table.setSelectionBackground(Color.YELLOW);
        table.setSelectionForeground(Color.RED);
        table.setRowHeight(30);
    }

    public  void init(){

    }

    public static void main(String[] args) {
        new JTableDefineTest();
    }


    class MyTable  implements ActionListener
    {
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals("首页")){
                showTable(1);
            }

            if(e.getActionCommand().equals("上一页")){
                if(getCurrentPage()<=1){
                    setCurrentPage(2);
                }
                showTable(getCurrentPage()-1);
            }

            if(e.getActionCommand().equals("下一页")){
                if(getCurrentPage()<getLastPage()){
                    showTable(getCurrentPage()+1);
                }else{
                    showTable(getLastPage());
                }
            }

            if(e.getActionCommand().equals("末页")){
                showTable(getLastPage());
            }
        }
    }
}
