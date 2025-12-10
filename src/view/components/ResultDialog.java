package view.components;

import view.HomeScreen;
import javax.swing.*;
import java.awt.*;

public class ResultDialog extends JDialog {
    public ResultDialog(Frame owner, String winner, boolean isWinner) {
        super(owner, "Hasil Pertandingan", true);
        JPanel p = new JPanel(new GridLayout(3, 1));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel l1 = new JLabel(isWinner ? "MENANG!" : "KALAH!", SwingConstants.CENTER);
        l1.setFont(new Font("Dialog", Font.BOLD, 30));
        l1.setForeground(isWinner ? Color.GREEN : Color.RED);
        JLabel l2 = new JLabel("Pemenang: " + winner, SwingConstants.CENTER);
        l2.setFont(new Font("Dialog", Font.PLAIN, 18));
        JButton ok = new JButton("Kembali ke Menu Utama");
        ok.setFont(new Font("Dialog", Font.BOLD, 16));
        ok.addActionListener(e -> {
            dispose();
            owner.dispose();
            new HomeScreen();
        });
        p.add(l1); p.add(l2); p.add(ok);
        add(p); setSize(400, 250); setLocationRelativeTo(owner); setVisible(true);
    }
}