package view.components;

import util.AssetLoader;   // kalau nanti mau load background khusus
import javax.swing.*;
import java.awt.*;

public class WaitingPanel extends JPanel {

    private final JLabel animatedLabel;
    private String baseText;
    private int dotCount = 0;

    public WaitingPanel(String message) {
        setLayout(new GridBagLayout()); // biar center banget
        setOpaque(false); // karena background kita gambar manual

        baseText = message;

        animatedLabel = new JLabel(message, SwingConstants.CENTER);
        animatedLabel.setFont(new Font("Cinzel", Font.BOLD, 32));
        animatedLabel.setForeground(new Color(255, 215, 0)); // gold
        animatedLabel.setOpaque(false);

        add(animatedLabel);

        startAnimation();
    }

    private void startAnimation() {
        // Timer buat animasi titik “...”
        Timer t = new Timer(500, e -> {
            dotCount = (dotCount + 1) % 4;
            String dots = ".".repeat(dotCount);
            animatedLabel.setText(baseText + dots);
        });
        t.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // === BACKGROUND IMAGE ===
        // dummy dulu → "background_dummy.jpg"
        Image bg = AssetLoader.getInstance().getImage("src\\assets\\images\\bg_waiting.png");

        if (bg != null) {
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
        } else {
            // fallback warna misty ritual merah gelap
            g.setColor(new Color(40, 0, 0));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // === OVERLAY GELAP SUPAYA TEKS LEBIH KONTRAS ===
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
