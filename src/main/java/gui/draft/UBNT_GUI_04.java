package gui.draft;

import javax.swing.*;
import java.awt.*;

/**
 * @Author by XuLiang
 * @Date 2017/12/26 9:24
 * @Email stanxu526@gmail.com
 */
public class UBNT_GUI_04 {

    private static String[] labelName = {"SSID name :","IP Address :","Netmask :","Gateway IP :","Frequecy :","Mac address: "};
    public static void main(String[] args) {

        JFrame jf = new JFrame("测试窗口");
        jf.setSize(400, 500);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setLocationRelativeTo(null);

        final JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.add("M2",createTextPanel("M2"));
        jTabbedPane.add("M5_AP",createTextPanel("M5_AP"));
        jTabbedPane.add("M5_ST",createTextPanel("M5_ST"));


        jTabbedPane.setSelectedIndex(0);
        jf.setContentPane(jTabbedPane);
        jf.setVisible(true);

    }
    private static JComponent createTextPanel(String tabName){
        JPanel jPanel = new JPanel(null);

        JTextField inputBoxes = null;
        int realLength = 0;
        if (tabName.trim().equals("M2")){
            realLength = labelName.length-2;
        }
        else if (tabName.trim().equals("M5_AP")){
            realLength = labelName.length-1;
        }
        else if (tabName.trim().equals("M5_ST")){
            realLength = labelName.length;
        }
        for (int i = 1; i <= realLength; i++) {

            //setup labels
            JLabel labels = new JLabel(labelName[i-1],SwingConstants.LEFT);
            labels.setFont(new Font("ITALIC", 1, 16));
            labels.setLocation(10,40*i);
            labels.setSize(115,30);
            jPanel.add(labels);

            //setup input boxes
            inputBoxes = new JTextField(SwingConstants.RIGHT);
            inputBoxes.setFont(new Font(null, Font.PLAIN, 14));
            inputBoxes.setLocation(120,40*i);
            inputBoxes.setSize(200,30);
            jPanel.add(inputBoxes);
            // jTextFields.add(inputBoxes);
        }

        return jPanel;
    }


}
