package view;

import model.data.Skill;
import model.entities.GameCharacter;
import presenter.GamePresenter;
import view.components.HUDPanel;
import view.components.QuizDialog;
import view.components.ResultDialog;
import view.components.WaitingPanel;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    private GamePresenter presenter;
    private GameCanvas canvas;
    private HUDPanel hud;
    private WaitingPanel waitingPanel;
    private JPanel mainContainer;
    private CardLayout cardLayout;

    // Background dummy (ganti nanti)
    private final Image gameBg = new ImageIcon("src\\assets\\images\\bg_battle.png").getImage();

    public GameWindow(String playerName, boolean isServer, String host) {
        setTitle("ARJUNA BATTLE - " + playerName);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();

        // === MAIN CONTAINER DGN CUSTOM BACKGROUND ===
        mainContainer = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(gameBg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainContainer.setOpaque(false);
        mainContainer.setLayout(cardLayout);

        presenter = new GamePresenter(new viewAdapter());

        // === WAITING PANEL (tema gelap + gold) ===
        waitingPanel = new WaitingPanel("Menunggu lawan...") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Layer hitam transparan biar text kebaca
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 160));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        waitingPanel.setForeground(new Color(255, 215, 0)); // gold
        waitingPanel.setFont(new Font("Serif", Font.BOLD, 40));
        mainContainer.add(waitingPanel, "WAITING");

        // === GAME PANEL ===
        JPanel gamePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Transparent supaya background tetap keliatan
                setOpaque(false);
            }
        };

        canvas = new GameCanvas(presenter) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Semi-transparent layer buat ngeblend dengan background batik
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 70));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // === HUD PANEL THEME WAYANG ===
        hud = new HUDPanel(presenter) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;

                // Hitam transparan + gold border
                g2.setColor(new Color(0, 0, 0, 160));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2.setColor(new Color(255, 215, 0));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                super.paintComponent(g);
            }
        };
        hud.setOpaque(false);

        gamePanel.add(canvas, BorderLayout.CENTER);
        gamePanel.add(hud, BorderLayout.SOUTH);
        mainContainer.add(gamePanel, "GAME");

        add(mainContainer);

        if (isServer) {
            showWaiting("Menunggu koneksi Player 2...");
            presenter.startGameAsServer(playerName, 5000);
        } else {
            showWaiting("Menghubungkan ke Server...");
            presenter.startGameAsClient(playerName, host, 5000);
        }

        setFocusable(true);
        setVisible(true);
    }

    public void showWaiting(String message) {
        waitingPanel.setToolTipText(message);
        cardLayout.show(mainContainer, "WAITING");
    }

    public void startGame() {
        cardLayout.show(mainContainer, "GAME");
        canvas.requestFocusInWindow();
    }

    private class viewAdapter implements presenter.GameContract.View {
        @Override
        public void onStateUpdated(model.GameState state) {}

        @Override
        public void showQuiz(GameCharacter self, Skill skill) {
            SwingUtilities.invokeLater(() -> new QuizDialog(GameWindow.this, presenter, self, skill));
        }

        @Override
        public void showResult(String winner, boolean isWinner) {
            SwingUtilities.invokeLater(() -> new ResultDialog(GameWindow.this, winner, isWinner));
        }

        @Override
        public void repaintGame() {
            canvas.repaint();
            hud.repaint();
        }

        @Override
        public void startGameDisplay() {
            GameWindow.this.startGame();
        }

        @Override
        public void showNotification(String text) {
            canvas.setNotification(text);
        }
    }

    
    
}


