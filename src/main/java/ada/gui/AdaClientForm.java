package ada.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ada.AdaClient;

public class AdaClientForm {
    private JPanel clientPanel;
    private JButton buttonStart;
    private AdaClient client;

    public AdaClientForm() {
        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (client == null) {
                    JOptionPane.showMessageDialog(null, "Client Started");
                    try {
                        client = new AdaClient();
                        client.main(null);
                    } catch (Exception excp) {
                        JOptionPane.showMessageDialog(null, "Error ins starting client");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Client Already Started");
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ada Client");
        frame.setPreferredSize(new Dimension(200, 200));
        frame.setContentPane(new AdaClientForm().clientPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }
}