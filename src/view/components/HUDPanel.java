package view.components;

import presenter.GamePresenter;
import model.entities.GameCharacter;

import javax.swing.*;
import java.awt.*;

public class HUDPanel extends JPanel {

    private GamePresenter presenter;

    public HUDPanel(GamePresenter presenter) {
        this.presenter = presenter;

        // === PANEL THEME ===
        setOpaque(false); // Transparan (akan diwarnai manual di paintComponent)
        setPreferredSize(new Dimension(1280, 90));
        setLayout(new FlowLayout(FlowLayout.CENTER, 25, 20));

        // === BUTTON STYLE (GOLD THEME) ===
        Color gold = new Color(255, 215, 0);
        Font btnFont = new Font("Dialog", Font.BOLD, 14);

        JButton s1 = createSkillButton("Skill 1 (Key: 1)", gold, btnFont);
        JButton s2 = createSkillButton("Skill 2 (Key: 2)", gold, btnFont);
        JButton s3 = createSkillButton("Skill 3 (Key: 3)", gold, btnFont);

        s1.addActionListener(e -> presenter.onSkill(0));
        s2.addActionListener(e -> presenter.onSkill(1));
        s3.addActionListener(e -> presenter.onSkill(2));

        add(s1);
        add(s2);
        add(s3);
    }

    private JButton createSkillButton(String text, Color gold, Font font) {
        JButton btn = new JButton(text);

        btn.setFont(font);
        btn.setForeground(Color.BLACK);
        btn.setBackground(gold);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(175, 145, 20), 2));
        btn.setPreferredSize(new Dimension(150, 35));

        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        // === BACKGROUND TRANSPARAN + GOLD OUTLINE ===
        Graphics2D g2 = (Graphics2D) g;

        // Panel hitam semi-transparan
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        // Border gold elegan
        g2.setColor(new Color(255, 215, 0));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        super.paintComponent(g);

        // === STATUS TEXT ===
        GameCharacter p = presenter.getGameState().getPlayer();
        GameCharacter e = presenter.getGameState().getEnemy();

        g2.setFont(new Font("Serif", Font.BOLD, 20));

        // === PLAYER INFO — KIRI ===
        if (p != null) {
            String hp = "PLAYER HP: " + p.getHp();
            String sukma = "SUKMA: " + p.getSukma();

            // Shadow
            g2.setColor(Color.BLACK);
            g2.drawString(hp, 22, 32);
            g2.drawString(sukma, 22, 58);

            // Gold text
            g2.setColor(new Color(255, 215, 0));
            g2.drawString(hp, 20, 30);

            // Sukma biru muda biar beda
            g2.setColor(new Color(150, 205, 255));
            g2.drawString(sukma, 20, 55);
        }

        // === ENEMY INFO — KANAN ===
        if (e != null) {
            int width = getWidth();
            int x = width - 230;

            String hp = "ENEMY HP: " + e.getHp();
            String sukma = "SUKMA: " + e.getSukma();

            // Shadow
            g2.setColor(Color.BLACK);
            g2.drawString(hp, x + 2, 32);
            g2.drawString(sukma, x + 2, 58);

            // Enemy HP merah
            g2.setColor(new Color(255, 100, 100));
            g2.drawString(hp, x, 30);

            // Enemy Sukma putih keabu
            g2.setColor(new Color(220, 220, 220));
            g2.drawString(sukma, x, 55);
        }
    }
}
