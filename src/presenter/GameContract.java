package presenter;

import model.GameState;
import model.data.Skill;
import model.entities.GameCharacter;
import model.entities.Projectile;
import java.util.List;

public interface GameContract {
    interface View {
        void onStateUpdated(GameState state);
        void showQuiz(GameCharacter self, Skill skill);
        void showResult(String winnerName, boolean isWinner);
        void repaintGame();
        void startGameDisplay();
    }

    interface Presenter {
        void startGameAsServer(String playerName, int port);
        void startGameAsClient(String playerName, String host, int port);
        void onMoveUp();
        void onMoveDown();
        void onSkill(int index);
        GameState getGameState();
        List<Projectile> getProjectiles();
    }
}