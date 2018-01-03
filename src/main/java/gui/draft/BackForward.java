package gui.draft;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @Author by XuLiang
 * @Date 2018/01/03 17:14
 * @Email stanxu526@gmail.com
 */
public class BackForward extends JFrame implements ActionListener {
    private static Container frame;
    private static JPanel contentPrevious,contentCurrent,contentNext,upper,lower;
    private static JButton backButton, forwardButton;
    private static int whereAmI;
    public BackForward()
    {
        super("BackForward");
        setSize(300,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame = getContentPane();
        frame.setLayout(new BorderLayout());
        upper = new JPanel();
        contentPrevious = new JPanel(new FlowLayout());
        JLabel lblPrevious = new JLabel("contentPrevious JPanel");
        contentPrevious.add(lblPrevious);
        contentNext = new JPanel(new FlowLayout());
        JLabel lblNext = new JLabel("contentNext JPanel");
        contentNext.add(lblNext);
        contentCurrent = new JPanel(new FlowLayout());
        JLabel lblCurrent = new JLabel("contentCurrent JPanel");
        contentCurrent.add(lblCurrent);
        upper.add(contentCurrent);
        lower = new JPanel(new FlowLayout());
        backButton = new JButton("<<< Back");
        backButton.addActionListener(this);
        lower.add(backButton);
        forwardButton = new JButton("Forward >>>");
        forwardButton.addActionListener(this);
        lower.add(forwardButton);
        frame.add(upper, BorderLayout.NORTH);
        frame.add(lower,BorderLayout.SOUTH);
        setContentPane(frame);
        pack();
        setVisible(true);
        whereAmI = 0;
    }
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        if(ae.getSource() == backButton && whereAmI > -1)
        {
            upper.removeAll();
            if(whereAmI==0) upper.add(contentPrevious);
            else upper.add(contentCurrent);
            setContentPane(frame);
            whereAmI--;
        }
        else if(ae.getSource() == forwardButton && whereAmI < 1)
        {
            upper.removeAll();
            if(whereAmI==0) upper.add(contentNext);
            else upper.add(contentCurrent);
            setContentPane(frame);
            whereAmI++;
        }
    }
    public void setVisible(boolean visible)
    {
        if(visible)
        {
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            setLocation((dim.width - getWidth())/2, (dim.height - getHeight())/2);
        }
        super.setVisible(visible);
    }
    public static void main(String args[])
    {
        new BackForward();
    }

}
