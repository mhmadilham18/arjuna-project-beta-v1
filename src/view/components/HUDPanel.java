package view.components;

import presenter.GamePresenter;
import model.entities.GameCharacter;

import javax.swing.*;
import java.awt.*;

public class HUDPanel extends JPanel {

    private GamePresenter presenter;

    public HUDPanel(GamePresenter presenter) {
        this.presenter = presenter;
        // Buat background panel sedikit transparan atau gelap
        setBackground(new Color(20, 20, 20));
        setPreferredSize(new Dimension(1280, 80));
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton s1 = new JButton("Skill 1 (Key: 1)");
        JButton s2 = new JButton("Skill 2 (Key: 2)");
        JButton s3 = new JButton("Skill 3 (Key: 3)");

        // Styling tombol sedikit
        Font btnFont = new Font("Dialog", Font.BOLD, 12);
        s1.setFont(btnFont); s2.setFont(btnFont); s3.setFont(btnFont);

        s1.addActionListener(e -> presenter.onSkill(0));
        s2.addActionListener(e -> presenter.onSkill(1));
        s3.addActionListener(e -> presenter.onSkill(2));

        add(s1);
        add(s2);
        add(s3);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        GameCharacter p = presenter.getGameState().getPlayer();

        // Ganti Font dan Warna
        g.setFont(new Font("Monospaced", Font.BOLD, 20));

        if (p != null) {
            // Efek Shadow Hitam agar tulisan jelas
            g.setColor(Color.BLACK);
            g.drawString("HP: " + p.getHp(), 22, 32);
            g.drawString("Sukma: " + p.getSukma(), 22, 57);

            // Tulisan Utama (KUNING EMAS)
            g.setColor(new Color(255, 215, 0)); // Gold Color
            g.drawString("HP: " + p.getHp(), 20, 30);

            // Sukma warna Biru Muda (Cyan) atau tetap Emas
            g.setColor(new Color(135, 206, 250)); // Light Sky Blue
            g.drawString("Sukma: " + p.getSukma(), 20, 55);
        }
    }
}