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

    public GameWindow(String playerName, boolean isServer, String host) {
        setTitle("Arjuna Battle - " + playerName);
        setSize(1280, 760);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        presenter = new GamePresenter(new viewAdapter());

        // Panel 1: Waiting
        waitingPanel = new WaitingPanel("Menunggu lawan...");
        mainContainer.add(waitingPanel, "WAITING");

        // Panel 2: Game Area
        JPanel gamePanel = new JPanel(new BorderLayout());
        canvas = new GameCanvas(presenter);
        hud = new HUDPanel(presenter);
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
        cardLayout.show(mainContainer, "WAITING");
    }

    public void startGame() {
        cardLayout.show(mainContainer, "GAME");
        // FIX: Request Focus agar keyboard langsung jalan
        canvas.requestFocusInWindow();
    }

    private class viewAdapter implements presenter.GameContract.View {
        @Override
        public void onStateUpdated(model.GameState state) {}

        @Override
        public void showQuiz(GameCharacter self, Skill skill) {
            SwingUtilities.invokeLater(() ->
                    new QuizDialog(GameWindow.this, presenter, self, skill)
            );
        }

        @Override
        public void showResult(String winner, boolean isWinner) {
            SwingUtilities.invokeLater(() ->
                    new ResultDialog(GameWindow.this, winner, isWinner)
            );
        }

        @Override
        public void repaintGame() {
            canvas.repaint();
            hud.repaint();
        }

        @Override
        public void startGameDisplay() {
            // Dipanggil saat game mulai ATAU resume dari pause
            GameWindow.this.startGame();
        }
    }
}