package icon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @Author by XuLiang
 * @Date 2017/12/26 17:17
 * @Email stanxu526@gmail.com
 */
public class SwingControlDemo {
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;

    public SwingControlDemo(){
        prepareGUI();
    }
    public static void main(String[] args){
        SwingControlDemo  swingControlDemo = new SwingControlDemo();
        swingControlDemo.showImageIconDemo();
    }
    private void prepareGUI(){
        mainFrame = new JFrame("Java Swing Examples");
        mainFrame.setSize(400,400);
        mainFrame.setLayout(new GridLayout(3, 1));

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });
        headerLabel = new JLabel("", JLabel.CENTER);
        statusLabel = new JLabel("",JLabel.CENTER);
        statusLabel.setSize(350,100);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.add(statusLabel);
        mainFrame.setVisible(true);
    }
    // Returns an ImageIcon, or null if the path was invalid.
    private static ImageIcon createImageIcon(String path,
                                             String description) {
        java.net.URL imgURL = SwingControlDemo.class.getResource(path);

        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    //System.getProperty("user.dir")+"\\src\\main\\java\\icon\\logo.png"
    //D:\RemoteConfigCentre\remoteConfigCentre\src\main\java\icon
    private void showImageIconDemo(){
        headerLabel.setText("Control in action: ImageIcon");
        ImageIcon icon = createImageIcon(".logo.png","Java");

        JLabel commentlabel = new JLabel("", icon,JLabel.CENTER);
        controlPanel.add(commentlabel);
        mainFrame.setVisible(true);
    }
}

