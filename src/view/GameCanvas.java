package view;

import presenter.GamePresenter;
import model.GameState;
import model.entities.GameCharacter;
import model.entities.Projectile;
import util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform; // Import ini
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameCanvas extends JPanel implements KeyListener {

    private GamePresenter presenter;
    private String notificationText = "";
    private long notificationEndTime = 0;

    public GameCanvas(GamePresenter presenter) {
        this.presenter = presenter;
        // PreferredSize tetap, tapi nanti akan di-stretch
        setPreferredSize(new Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
    }

    public void setNotification(String text) {
        this.notificationText = text;
        this.notificationEndTime = System.currentTimeMillis() + 2000;
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

        // --- LOGIKA ZOOM/SCALING AGAR FULL SCREEN ---
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform oldTransform = g2.getTransform(); // Simpan settingan lama

        double scaleX = (double) getWidth() / Constants.SCREEN_WIDTH;
        double scaleY = (double) getHeight() / Constants.SCREEN_HEIGHT;
        g2.scale(scaleX, scaleY); // Stretch gambar
        // ---------------------------------------------

        GameState s = presenter.getGameState();
        if (s == null) {
            g2.setTransform(oldTransform); // Kembalikan settingan
            return;
        }

        GameCharacter p = s.getPlayer();
        GameCharacter e = s.getEnemy();

        if (p != null) g.drawImage(p.getCurrentImage(), p.getX(), p.getY(), 120, 120, null);

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

        if (System.currentTimeMillis() < notificationEndTime && !notificationText.isEmpty()) {
            // Gunakan font dari Constants atau statis, karena di-scale, font juga ikut membesar
            g.setFont(new Font("Dialog", Font.BOLD, 30));
            FontMetrics fm = g.getFontMetrics();
            int w = fm.stringWidth(notificationText);
            int h = fm.getHeight();

            // Koordinat tengah berdasarkan resolusi ASLI (1280x720)
            int centerX = Constants.SCREEN_WIDTH / 2;
            int centerY = Constants.SCREEN_HEIGHT / 2;

            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(centerX - w/2 - 20, centerY - h/2 - 10, w + 40, h + 20);
            g.setColor(Color.WHITE);
            g.drawString(notificationText, centerX - w/2, centerY + 10);
        }

        // Kembalikan settingan agar tidak merusak komponen lain (opsional)
        g2.setTransform(oldTransform);
    }
}