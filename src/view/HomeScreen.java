package view;

import javax.swing.*;
import java.awt.*;

public class HomeScreen extends JFrame {

    public HomeScreen() {
        setTitle("ARJUNA BATTLE - HOME");

        // UBAH DISINI: Default Maximize
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Hapus setSize, atau biarkan sbg fallback (opsional)
        // setSize(400, 350);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Gunakan GridBagLayout agar form tetap di tengah walau layar besar
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        formPanel.setPreferredSize(new Dimension(400, 300)); // Ukuran form tetap rapi

        JLabel title = new JLabel("ARJUNA BATTLE", SwingConstants.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 32));

        JTextField nameField = new JTextField();
        nameField.setHorizontalAlignment(SwingConstants.CENTER);
        nameField.setBorder(BorderFactory.createTitledBorder("Nama Pemain"));

        JTextField ipField = new JTextField("192.168.1.4"); // Ganti sesuai IP Server Anda
        ipField.setHorizontalAlignment(SwingConstants.CENTER);
        ipField.setBorder(BorderFactory.createTitledBorder("IP Server (Untuk Join)"));

        JButton hostBtn = new JButton("HOST GAME (Server)");
        hostBtn.setBackground(new Color(50, 200, 50));
        hostBtn.setFont(new Font("Dialog", Font.BOLD, 16));

        JButton joinBtn = new JButton("JOIN GAME (Client)");
        joinBtn.setBackground(new Color(50, 100, 200));
        joinBtn.setFont(new Font("Dialog", Font.BOLD, 16));
        joinBtn.setForeground(Color.WHITE);

        formPanel.add(title);
        formPanel.add(nameField);
        formPanel.add(ipField);
        formPanel.add(hostBtn);
        formPanel.add(joinBtn);

        add(formPanel);

        hostBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nama wajib diisi!");
                return;
            }
            // Kirim IP Localhost untuk server
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