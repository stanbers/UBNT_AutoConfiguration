package gui.draft;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Vector;

/**
 * @Author by XuLiang
 * @Date 2018/01/04 10:27
 * @Email stanxu526@gmail.com
 */
public class ExcelTojTable extends JFrame {
    static JTable table;
    static JScrollPane scroll;
    // header is Vector contains table Column
    static Vector headers = new Vector();
    // Model is used to construct JTable
    static DefaultTableModel model = null;
    // data is Vector contains Data from Excel File
    static Vector data = new Vector();
    static JButton jbClick;
    static JFileChooser jChooser;
    static int tableWidth = 0; // set the tableWidth
    static int tableHeight = 0; // set the tableHeight

    public ExcelTojTable() {
        super("Import Excel To JTable");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.white);
        jChooser = new JFileChooser();
        jbClick = new JButton("Select Excel File");
        buttonPanel.add(jbClick, BorderLayout.CENTER);
        // Show Button Click Event
        jbClick.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jChooser.showOpenDialog(null);

                File file = jChooser.getSelectedFile();
                if (!file.getName().endsWith("xlsx")) {
                    JOptionPane.showMessageDialog(null,
                            "Please select only Excel file.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    fillData(file);
                    model = new DefaultTableModel(data,
                            headers);
                    tableWidth = model.getColumnCount()
                            * 150;
                    tableHeight = model.getRowCount()
                            * 25;
                    table.setPreferredSize(new Dimension(
                            tableWidth, tableHeight));

                    table.setModel(model);
                }
            }
        });

        table = new JTable();
        table.setAutoCreateRowSorter(true);
        model = new DefaultTableModel(data, headers);

        table.setModel(model);
        table.setBackground(Color.pink);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setEnabled(false);
        table.setRowHeight(25);
        table.setRowMargin(4);

        tableWidth = model.getColumnCount() * 150;
        tableHeight = model.getRowCount() * 25;
        table.setPreferredSize(new Dimension(
                tableWidth, tableHeight));

        scroll = new JScrollPane(table);
        scroll.setBackground(Color.pink);
        scroll.setPreferredSize(new Dimension(300, 300));
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        getContentPane().add(buttonPanel,
                BorderLayout.NORTH);
        getContentPane().add(scroll,
                BorderLayout.CENTER);
        setSize(600, 600);
        setResizable(true);
        setVisible(true);
    }

    /**
     * Fill JTable with Excel file data.
     *
     * @param file file :contains xls file to display in jTable
     */
    void fillData(File file) {
        try {
            InputStream is = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(is);
//                Logger.getLogger(
//                        ExcelTojTable.class.getName()).log(Level.SEVERE,null, ex);
            XSSFSheet sheet = workbook.getSheetAt(0);
            headers.clear();
//            for (int i = 0; i < sheet.getLastRowNum(); i++) {
//                XSSFRow row = sheet.getRow(i);
////                XSSFCell cell1 = sheet.getCell(i, 0);
//                XSSFCell cell1 = row.getCell()
//                headers.add(cell1.getContents());
//            }

            data.clear();
            for (int j = 1; j < sheet.getLastRowNum(); j++) {
                Vector d = new Vector();
                XSSFRow row = sheet.getRow(j);
                for (int i = 0; i < row.getLastCellNum(); i++) {

                    XSSFCell cell = row.getCell(i);

                    d.add(cell.getStringCellValue());

                }
                d.add("\n");
                data.add(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        new ExcelTojTable();
    }
}
