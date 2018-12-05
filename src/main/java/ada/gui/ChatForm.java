package ada.gui;

import javax.swing.*;

public class ChatForm {
    private JPanel mainPanel;
    private JTable table1;
    private ChatForm c;
    public static void main(String[] args) {
        ChatForm c=new ChatForm();
        JFrame frame = new JFrame("ChatForm");
        frame.setSize(500,500);
        frame.setResizable(false);
        frame.setContentPane(c.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        c.mainPanel=new JPanel();
        c.mainPanel.add(new JTextArea());
        frame.setVisible(true);
    }
}
