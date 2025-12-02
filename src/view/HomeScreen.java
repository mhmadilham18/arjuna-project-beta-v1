package view;

import presenter.GamePresenter;
import view.components.WaitingPanel;

import javax.swing.*;
import java.awt.*;

public class HomeScreen extends JFrame {

    public HomeScreen() {
        setTitle("ARJUNA BATTLE - HOME");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(10, 1));

        JLabel title = new JLabel("ARJUNA BATTLE", SwingConstants.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 22));

        JTextField nameField = new JTextField();
        nameField.setHorizontalAlignment(SwingConstants.CENTER);
        nameField.setBorder(BorderFactory.createTitledBorder("Nama Pemain"));

        JTextField ipField = new JTextField("127.0.0.1");
        ipField.setHorizontalAlignment(SwingConstants.CENTER);
        ipField.setBorder(BorderFactory.createTitledBorder("IP Server"));

        JButton hostBtn = new JButton("Host Game");
        JButton joinBtn = new JButton("Join Game");

        add(title);
        add(nameField);
        add(ipField);
        add(hostBtn);
        add(joinBtn);

        hostBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nama wajib diisi!");
                return;
            }
            GameWindow gw = new GameWindow(name, true, "127.0.0.1");
            gw.showWaiting("Menunggu pemain bergabung...");
            dispose();
        });

        joinBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String ip = ipField.getText().trim();
            if (name.isEmpty() || ip.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nama & IP wajib diisi!");
                return;
            }
            GameWindow gw = new GameWindow(name, false, ip);
            gw.showWaiting("Menghubungkan ke server...");
            dispose();
        });

        setVisible(true);
    }
}
