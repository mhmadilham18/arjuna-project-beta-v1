package view.components;

import util.NetworkManager;
import view.HomeScreen;
import util.AssetLoader;

import javax.swing.*;
import java.awt.*;

public class ResultDialog extends JDialog {

    public ResultDialog(Window owner, String winner, boolean isWinner) {
        super(owner, "Hasil Pertarungan", ModalityType.APPLICATION_MODAL);

        setContentPane(new BackgroundPanel());
        setLayout(new GridBagLayout());
        setSize(550, 350);
        setLocationRelativeTo(owner);
        setResizable(false);

        // PENTING: Set default close operation agar X tidak bikin error
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        initUI(winner, isWinner);

        setVisible(true);
    }

    private void initUI(String winner, boolean isWinner) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- TITLE ---
        JLabel title = new JLabel(isWinner ? "KAMU MENANG!" : "KAMU KALAH!", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 36));
        title.setForeground(isWinner ? new Color(255, 215, 0) : new Color(255, 100, 100));
        gbc.gridy = 0;
        add(title, gbc);

        // --- WINNER INFO ---
        JLabel winnerLabel = new JLabel("Pemenang: " + winner, SwingConstants.CENTER);
        winnerLabel.setFont(new Font("Serif", Font.PLAIN, 22));
        winnerLabel.setForeground(new Color(230, 230, 230));
        gbc.gridy = 1;
        add(winnerLabel, gbc);

        // --- BUTTON ---
        JButton backBtn = new JButton("KEMBALI KE MENU UTAMA");
        styleButton(backBtn);

        backBtn.addActionListener(e -> {
            backBtn.setEnabled(false);
            backBtn.setText("Sedang Keluar...");

            // Jalankan Thread untuk bersih-bersih jaringan
            new Thread(() -> {
                // Panggil disconnect (sekarang sudah cepat karena kode di atas)
                NetworkManager.getInstance().disconnect();
            }).start();

            // LANGSUNG PINDAH LAYAR (Jangan menunggu thread di atas selesai)
            // Beri jeda sangat singkat (200ms) agar terasa natural, lalu tutup
            Timer delay = new Timer(200, evt -> {
                dispose();
                Window w = getOwner();
                if (w != null) w.dispose();
                new HomeScreen();
            });
            delay.setRepeats(false);
            delay.start();
        });

        gbc.gridy = 2;
        add(backBtn, gbc);
    }

    private void styleButton(JButton b) {
        b.setFont(new Font("Dialog", Font.BOLD, 18));
        b.setFocusPainted(false);
        b.setBackground(new Color(60, 20, 20));
        b.setForeground(new Color(255, 215, 0));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 60, 60), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
    }

    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Image bg = AssetLoader.getInstance().getQuizBackground();
            if (bg != null) {
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
            } else {
                g.setColor(new Color(40, 0, 0));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}