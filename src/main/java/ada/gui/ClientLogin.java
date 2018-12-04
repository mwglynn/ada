package ada.gui;

import javax.swing.*;

public class ClientLogin {
    private JPanel panel1;
    private JButton buttonLogin;
    private JButton buttonRegister;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Client Login");
        frame.setContentPane(new ClientLogin().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(300, 200);
        frame.setVisible(true);
    }

}
