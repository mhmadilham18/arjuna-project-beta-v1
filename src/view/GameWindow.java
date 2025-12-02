package view;

import model.data.Skill;
import model.entities.GameCharacter;
import presenter.GamePresenter;
import view.GameCanvas;
import view.components.HUDPanel;
import view.components.QuizDialog;
import view.components.WaitingPanel;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    private GamePresenter presenter;
    private GameCanvas canvas;
    private HUDPanel hud;
    private WaitingPanel waitingPanel;

    public GameWindow(String playerName, boolean isServer, String host) {
        setTitle("Arjuna Battle");
        setSize(1280, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        presenter = new GamePresenter(new viewAdapter());
        canvas = new GameCanvas(presenter);
        hud = new HUDPanel(presenter);

        if (isServer) {
            presenter.startGameAsServer(playerName, 5000);
        } else {
            presenter.startGameAsClient(playerName, host, 5000);
        }

        addKeyListener(canvas);
        setFocusable(true);
    }

    public void showWaiting(String message) {
        waitingPanel = new WaitingPanel(message);
        setContentPane(waitingPanel);
        setVisible(true);
    }

    public void startGame() {
        JPanel container = new JPanel(new BorderLayout());
        container.add(canvas, BorderLayout.CENTER);
        container.add(hud, BorderLayout.SOUTH);
        setContentPane(container);
        revalidate();
        repaint();
    }

    private class viewAdapter implements presenter.GameContract.View {

        @Override
        public void onStateUpdated(model.GameState state) {}

        @Override
        public void showQuiz(GameCharacter self, Skill skill) {
            new QuizDialog(GameWindow.this, presenter, self, skill);
        }


        @Override
        public void showResult(String winner, boolean isWinner) {
            new view.components.ResultDialog(GameWindow.this, winner, isWinner);
        }

        @Override
        public void repaintGame() {
            canvas.repaint();
            hud.repaint();
        }
    }
}
