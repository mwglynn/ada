package ada.gui;

import ada.AdaServerMain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdaServerForm {
    private JButton starButton;
    private JPanel serverPanel;
    private AdaServerMain server;

    public AdaServerForm() {
        starButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (server == null) {
                    server = new AdaServerMain();
                    server.main(null);
                } else {
                    JOptionPane.showMessageDialog(null, "Server Already " +
                            "Started");
                }
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
