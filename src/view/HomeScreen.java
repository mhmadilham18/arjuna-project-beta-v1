package view;

import javax.swing.*;
import java.awt.*;

public class HomeScreen extends JFrame {

    public HomeScreen() {

        setTitle("ARJUNA BATTLE - HOME");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // LOAD BACKGROUND IMAGE
        Image bg = new ImageIcon("arjuna-project-beta-v1\\src\\assets\\images\\bg_home.png").getImage();

        // Custom panel buat background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // === FORM PANEL (transparan semi gelap) ===
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 12, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                // Panel gelap semi-transparan biar teks kebaca
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }
        };
        formPanel.setOpaque(false);
        formPanel.setPreferredSize(new Dimension(420, 350));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // === TITLE ===
        JLabel title = new JLabel("ARJUNA BATTLE", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 38));
        title.setForeground(new Color(255, 215, 0)); // gold

        // === TEXTFIELD STYLE ===
        Color borderGold = new Color(230, 198, 92);
        Color textColor = Color.WHITE;

        JTextField nameField = new JTextField();
        nameField.setHorizontalAlignment(SwingConstants.CENTER);
        nameField.setForeground(textColor);
        nameField.setBackground(new Color(20, 20, 20, 180));
        nameField.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(borderGold),
                "Nama Pemain",
                0, 0,
                new Font("Dialog", Font.PLAIN, 14),
                borderGold
        ));

        JTextField ipField = new JTextField("192.168.1.4");
        ipField.setHorizontalAlignment(SwingConstants.CENTER);
        ipField.setForeground(textColor);
        ipField.setBackground(new Color(20, 20, 20, 180));
        ipField.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(borderGold),
                "IP Server (Untuk Join)",
                0, 0,
                new Font("Dialog", Font.PLAIN, 14),
                borderGold
        ));

        // === BUTTON STYLE ===
        JButton hostBtn = new JButton("HOST GAME (Server)");
        hostBtn.setFont(new Font("Dialog", Font.BOLD, 16));
        hostBtn.setBackground(new Color(180, 140, 20)); // gold gelap
        hostBtn.setForeground(Color.BLACK);
        hostBtn.setFocusPainted(false);

        JButton joinBtn = new JButton("JOIN GAME (Client)");
        joinBtn.setFont(new Font("Dialog", Font.BOLD, 16));
        joinBtn.setBackground(new Color(255, 215, 0)); // gold terang
        joinBtn.setForeground(Color.BLACK);
        joinBtn.setFocusPainted(false);

        // === ADD COMPONENTS ===
        formPanel.add(title);
        formPanel.add(nameField);
        formPanel.add(ipField);
        formPanel.add(hostBtn);
        formPanel.add(joinBtn);

        backgroundPanel.add(formPanel, gbc);
        setContentPane(backgroundPanel);

        // === ACTION ===
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
