package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdaServerForm {
    private JButton starButton;
    private JPanel serverPanel;

    public AdaServerForm() {
        starButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Server Started");
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ada Server");
        frame.setPreferredSize(new Dimension(200, 200));
        frame.setContentPane(new AdaServerForm().serverPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }
}
