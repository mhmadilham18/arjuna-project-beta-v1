package view.components;

import presenter.GamePresenter;
import model.entities.GameCharacter;
import util.Constants; // Butuh constants untuk tahu lebar layar

import javax.swing.*;
import java.awt.*;

public class HUDPanel extends JPanel {
    private GamePresenter presenter;

    public HUDPanel(GamePresenter presenter) {
        this.presenter = presenter;
        setBackground(new Color(20, 20, 20));
        setPreferredSize(new Dimension(1280, 80));
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton s1 = new JButton("Skill 1 (Key: 1)");
        JButton s2 = new JButton("Skill 2 (Key: 2)");
        JButton s3 = new JButton("Skill 3 (Key: 3)");
        s1.addActionListener(e -> presenter.onSkill(0));
        s2.addActionListener(e -> presenter.onSkill(1));
        s3.addActionListener(e -> presenter.onSkill(2));
        add(s1); add(s2); add(s3);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        GameCharacter p = presenter.getGameState().getPlayer();
        GameCharacter e = presenter.getGameState().getEnemy();

        g.setFont(new Font("Monospaced", Font.BOLD, 20));

        // --- GAMBAR STATUS PLAYER (KIRI) ---
        if (p != null) {
            String hpText = "PLAYER HP: " + p.getHp();
            String sukmaText = "SUKMA: " + p.getSukma();

            // Shadow
            g.setColor(Color.BLACK);
            g.drawString(hpText, 22, 32);
            g.drawString(sukmaText, 22, 57);

            // Warna Asli
            g.setColor(new Color(255, 215, 0)); // Gold
            g.drawString(hpText, 20, 30);
            g.setColor(new Color(135, 206, 250)); // Cyan
            g.drawString(sukmaText, 20, 55);
        }

        // --- GAMBAR STATUS MUSUH (KANAN) ---
        if (e != null) {
            String enemyHpText = "ENEMY HP: " + e.getHp();
            String enemySukmaText = "SUKMA: " + e.getSukma();

            // Hitung posisi kanan berdasarkan lebar panel
            int width = getWidth();
            int hpX = width - 200; // Geser ke kiri dari pojok kanan

            // Shadow
            g.setColor(Color.BLACK);
            g.drawString(enemyHpText, hpX + 2, 32);
            g.drawString(enemySukmaText, hpX + 2, 57);

            // Warna (Merah muda untuk musuh biar beda)
            g.setColor(new Color(255, 100, 100));
            g.drawString(enemyHpText, hpX, 30);
            g.setColor(new Color(200, 200, 200));
            g.drawString(enemySukmaText, hpX, 55);
        }
    }
}