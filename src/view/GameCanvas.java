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
        requestFocusInWindow();

        // register key listener
        addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                presenter.onMoveUp();
                break;
            case KeyEvent.VK_DOWN:
                presenter.onMoveDown();
                break;
            case KeyEvent.VK_SPACE:
                presenter.onShoot();
                break;
            case KeyEvent.VK_1:
                presenter.onSkill(0);
                break;
            case KeyEvent.VK_2:
                presenter.onSkill(1);
                break;
            case KeyEvent.VK_3:
                presenter.onSkill(2);
                break;
        }
    }

    // -------------------------------
    // PAINT GAME
    // -------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        GameState s = presenter.getGameState();
        if (s == null) return;

        GameCharacter p = s.getPlayer();
        GameCharacter e = s.getEnemy();

        if (p != null) {
            g.drawImage(p.getCurrentImage(), p.getX(), p.getY(), 120, 120, null);
        }

        if (e != null) {
            g.drawImage(e.getCurrentImage(), e.getX(), e.getY(), 120, 120, null);
        }

        for (Projectile pr : presenter.getProjectiles()) {
            g.setColor(pr.isFromPlayer() ? Color.RED : Color.YELLOW);
            g.fillRect(pr.getX(), pr.getY(), 20, 8);
        }
    }
}
