package view;

import presenter.GamePresenter;
import model.GameState;
import model.entities.GameCharacter;
import model.entities.Projectile;
import util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameCanvas extends JPanel implements KeyListener {

    private GamePresenter presenter;

    public GameCanvas(GamePresenter presenter) {
        this.presenter = presenter;
        setPreferredSize(new Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:    presenter.onMoveUp(); break;
            case KeyEvent.VK_DOWN:  presenter.onMoveDown(); break;
            case KeyEvent.VK_1:     presenter.onSkill(0); break;
            case KeyEvent.VK_2:     presenter.onSkill(1); break;
            case KeyEvent.VK_3:     presenter.onSkill(2); break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        GameState s = presenter.getGameState();
        if (s == null) return;

        GameCharacter p = s.getPlayer();
        GameCharacter e = s.getEnemy();

        // --- GAMBAR PLAYER (KIRI, Hadap Kanan) ---
        if (p != null) {
            // Gambar normal
            g.drawImage(p.getCurrentImage(), p.getX(), p.getY(), 120, 120, null);
        }

        // --- GAMBAR ENEMY (KANAN, Hadap Kiri) ---
        if (e != null) {
            // Logika Flip: Kita gambar dari titik X+Lebar mundur ke kiri
            // Syntax: drawImage(img, x, y, width, height, observer)
            // Jika width negatif, gambar akan terbalik
            g.drawImage(e.getCurrentImage(),
                    e.getX() + 120, e.getY(), // Posisi X digeser sejauh lebar gambar
                    -120, 120,                // Lebar dibuat NEGATIF agar membalik
                    null);
        }

        // --- PROJECTILES ---
        for (Projectile pr : presenter.getProjectiles()) {
            Image projImg = pr.getImage();
            if (projImg != null) {
                // Jika projectile dari musuh, kita flip juga agar arah apinya sesuai
                if (!pr.isFromPlayer()) {
                    g.drawImage(projImg, pr.getX() + 60, pr.getY(), -60, 20, null);
                } else {
                    g.drawImage(projImg, pr.getX(), pr.getY(), 60, 20, null);
                }
            } else {
                g.setColor(pr.isFromPlayer() ? Color.RED : Color.YELLOW);
                g.fillRect(pr.getX(), pr.getY(), 20, 8);
            }
        }
    }
}