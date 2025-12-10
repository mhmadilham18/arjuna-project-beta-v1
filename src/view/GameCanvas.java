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
    private String notificationText = "";
    private long notificationEndTime = 0;

    public GameCanvas(GamePresenter presenter) {
        this.presenter = presenter;
        setPreferredSize(new Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
    }

    public void setNotification(String text) {
        this.notificationText = text;
        this.notificationEndTime = System.currentTimeMillis() + 2000; // 2 detik
        repaint();
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

        if (p != null) g.drawImage(p.getCurrentImage(), p.getX(), p.getY(), 120, 120, null);

        // FLIP ENEMY IMAGE
        if (e != null) {
            g.drawImage(e.getCurrentImage(), e.getX() + 120, e.getY(), -120, 120, null);
        }

        for (Projectile pr : presenter.getProjectiles()) {
            Image img = pr.getImage();
            if (img != null) {
                if (!pr.isFromPlayer()) g.drawImage(img, pr.getX() + 60, pr.getY(), -60, 20, null);
                else g.drawImage(img, pr.getX(), pr.getY(), 60, 20, null);
            } else {
                g.setColor(pr.isFromPlayer() ? Color.RED : Color.YELLOW);
                g.fillRect(pr.getX(), pr.getY(), 20, 8);
            }
        }

        // NOTIFIKASI
        if (System.currentTimeMillis() < notificationEndTime && !notificationText.isEmpty()) {
            g.setFont(new Font("Dialog", Font.BOLD, 30));
            FontMetrics fm = g.getFontMetrics();
            int w = fm.stringWidth(notificationText);
            int h = fm.getHeight();
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect((getWidth() - w)/2 - 20, (getHeight() - h)/2 - 10, w + 40, h + 20);
            g.setColor(Color.WHITE);
            g.drawString(notificationText, (getWidth() - w)/2, (getHeight()/2) + 10);
        }
    }
}