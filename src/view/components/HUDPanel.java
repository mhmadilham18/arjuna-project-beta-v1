package view.components;

import presenter.GamePresenter;
import model.entities.GameCharacter;

import javax.swing.*;
import java.awt.*;

public class HUDPanel extends JPanel {

    private GamePresenter presenter;

    public HUDPanel(GamePresenter presenter) {
        this.presenter = presenter;

        setPreferredSize(new Dimension(1280, 80));
        setLayout(new FlowLayout());

        JButton s1 = new JButton("Skill 1");
        JButton s2 = new JButton("Skill 2");
        JButton s3 = new JButton("Skill 3");

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

        g.setColor(Color.WHITE);
        g.setFont(new Font("Dialog", Font.BOLD, 18));

        if (p != null) {
            g.drawString("HP: " + p.getHp(), 20, 25);
            g.drawString("Sukma: " + p.getSukma(), 20, 50);
        }
    }
}
