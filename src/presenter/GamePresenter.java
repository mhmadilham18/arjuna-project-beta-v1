package presenter;

import model.*;
import model.data.QuizDatabase;
import model.data.Skill;
import model.data.SkillDatabase;
import model.entities.*;
import util.Constants;
import util.NetworkManager;
import util.StateSynchronizer;

import javax.swing.*;
import java.util.List;

public class GamePresenter implements GameContract.Presenter, NetworkManager.NetworkMessageListener {

    private final GameContract.View view;
    private final GameState gameState = new GameState();
    private final NetworkManager net = NetworkManager.getInstance();
    private final StateSynchronizer sync = StateSynchronizer.getInstance();

    private Player player;
    private Enemy enemy;
    private boolean running = false;

    public GamePresenter(GameContract.View view) {
        this.view = view;
        sync.setGameState(gameState);
        net.addListener(this);
    }

    @Override
    public void startGameAsServer(String playerName, int port) {
        try {
            net.startServer(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setupCharacters(playerName, true);
        startLoop();
    }

    @Override
    public void startGameAsClient(String playerName, String host, int port) {
        try {
            net.connectToServer(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setupCharacters(playerName, false);
        startLoop();
    }

    private void setupCharacters(String playerName, boolean isServer) {
        CharacterType myChar = isServer ? CharacterType.CAKIL : CharacterType.PATIH_SABRENG;
        CharacterType enemyChar = isServer ? CharacterType.PATIH_SABRENG : CharacterType.CAKIL;

        player = new Player(myChar, playerName);
        enemy  = new Enemy(enemyChar, "Lawan");

        List<Skill> mySkills = SkillDatabase.getInstance().getSkills(myChar);
        List<Skill> enemySkills = SkillDatabase.getInstance().getSkills(enemyChar);
        player.setSkills(mySkills);
        enemy.setSkills(enemySkills);

        gameState.setPlayer(player);
        gameState.setEnemy(enemy);
        gameState.setState(Constants.STATE_PLAYING);
    }

    private void startLoop() {
        if (running) return;
        running = true;
        Timer timer = new Timer(16, e -> {
            gameState.update();
            view.repaintGame();
            if (gameState.getState() == Constants.STATE_GAME_OVER) {
                running = false;
                String winner = gameState.getWinner();
                boolean isWinner = winner != null && player != null && winner.equals(player.getType().name());
                view.showResult(winner, isWinner);
            }
        });
        timer.start();
    }

    @Override
    public void onMoveUp() {
        if (player == null) return;
        player.moveUp();
        sync.syncPlayerMove(player.getLane());
    }

    @Override
    public void onMoveDown() {
        if (player == null) return;
        player.moveDown();
        sync.syncPlayerMove(player.getLane());
    }

    @Override
    public void onShoot() {
        if (player == null || !player.canAttack()) return;
        player.onAttack();
        Projectile p = new Projectile(
                player.getX(),
                player.getY(),
                player.getLane(),
                Constants.PROJECTILE_SPEED,
                player.getCurrentDamage(),
                true,
                player.getProjectileImage()
        );
        gameState.addProjectile(p);
        sync.syncShoot(player.getLane(), player.getCurrentDamage());
    }

    @Override
    public void onSkill(int index) {
        if (player == null) return;
        List<Skill> skills = player.getSkills();
        if (index < 0 || index >= skills.size()) return;

        Skill s = skills.get(index);
        if (!player.consumeSukma(s.getSukmaCost())) return;

        // tampilkan quiz
        view.showQuiz(player, s);
    }

    public void applySkill(GameCharacter c, Skill s, boolean sendSync) {
        switch (s.getType()) {
            case ATTACK:
                int dmg = s.getDamage() + (int)(c.getCurrentDamage() * 0.1);
                // di sini kamu bisa buat projectile khusus
                break;
            case DEFENCE:
            case BUFF:
                if (s.isImmuneDamage()) {
                    c.setImmuneDamage(true);
                    if (sendSync) sync.syncSkillActivate(s.getId(), "Immune", (int)(s.getBuffDurationMillis()/1000));
                }
                if (s.getAttackMultiplier() > 1.0) {
                    c.setAttackMultiplier(s.getAttackMultiplier());
                    if (sendSync) sync.syncSkillActivate(s.getId(), s.getAttackMultiplier() == 1.3 ? "ATK30" : "ATK50",
                            (int)(s.getBuffDurationMillis()/1000));
                }
                if (s.getAttackSpeedMultiplier() > 1.0) {
                    c.setAttackSpeedMultiplier(s.getAttackSpeedMultiplier());
                    if (sendSync) sync.syncSkillActivate(s.getId(), "ATKSPD30",
                            (int)(s.getBuffDurationMillis()/1000));
                }
                break;
        }
    }

    @Override
    public GameState getGameState() {
        return gameState;
    }

    @Override
    public java.util.List<Projectile> getProjectiles() {
        return gameState.getProjectiles();
    }

    // ===== Network callbacks =====
    @Override
    public void onMessageReceived(String type, String data) {
        switch (type) {
            case Constants.MSG_MOVE:           sync.handleRemoteMove(data); break;
            case Constants.MSG_SHOOT:          sync.handleRemoteShoot(data); break;
            case Constants.MSG_DAMAGE:         sync.handleRemoteDamage(data); break;
            case Constants.MSG_SKILL_ACTIVATE: sync.handleRemoteSkillActivate(data); break;
            case Constants.MSG_GAME_OVER:      sync.handleGameOver(data); break;
        }
    }
}
