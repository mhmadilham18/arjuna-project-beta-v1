package presenter;

import model.*;
import model.data.Skill;
import model.data.SkillDatabase;
import model.entities.*;
import util.*;

import javax.swing.*;
import java.util.List;

public class GamePresenter implements GameContract.Presenter, NetworkManager.NetworkMessageListener {

    private final GameContract.View view;
    private final GameState gameState = new GameState();
    private final NetworkManager net = NetworkManager.getInstance();
    private final StateSynchronizer sync = StateSynchronizer.getInstance();

    private Player player;
    private Enemy enemy;

    private Timer gameLoopTimer;
    private Timer autoShootTimer;
    private Timer voiceOverTimer; // NEW

    private boolean isPaused = false;
    private boolean isRemotePause = false;

    public GamePresenter(GameContract.View view) {
        this.view = view;
        sync.setGameState(gameState);
        sync.setPresenter(this);
        sync.setOnGameStartCallback(this::onGameStartConfirmed);
        AssetLoader.getInstance().loadAllAssets();
        net.addListener(this);
    }

    @Override
    public void startGameAsServer(String playerName, int port) {
        try {
            net.startServer(port);
            setupCharacters(playerName, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void startGameAsClient(String playerName, String host, int port) {
        try {
            net.connectToServer(host, port);
            setupCharacters(playerName, false);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setupCharacters(String playerName, boolean isServer) {
        CharacterType myChar = isServer ? CharacterType.CAKIL : CharacterType.PATIH_SABRENG;
        CharacterType enemyChar = isServer ? CharacterType.PATIH_SABRENG : CharacterType.CAKIL;

        player = new Player(myChar, playerName);
        enemy  = new Enemy(enemyChar, "Lawan");

        AssetLoader al = AssetLoader.getInstance();
        player.setNormalImage(myChar == CharacterType.CAKIL ? al.getCakilNormal() : al.getSabrangNormal());
        player.setBuffedImage(myChar == CharacterType.CAKIL ? al.getCakilBuffed() : al.getSabrangBuffed());
        player.setDamagedImage(myChar == CharacterType.CAKIL ? al.getCakilDamaged() : al.getSabrangDamaged());
        player.setProjectileImage(myChar == CharacterType.CAKIL ? al.getCakilProjectile() : al.getSabrangProjectile());

        enemy.setNormalImage(enemyChar == CharacterType.CAKIL ? al.getCakilNormal() : al.getSabrangNormal());
        enemy.setBuffedImage(enemyChar == CharacterType.CAKIL ? al.getCakilBuffed() : al.getSabrangBuffed());
        enemy.setDamagedImage(enemyChar == CharacterType.CAKIL ? al.getCakilDamaged() : al.getSabrangDamaged());
        enemy.setProjectileImage(enemyChar == CharacterType.CAKIL ? al.getCakilProjectile() : al.getSabrangProjectile());

        player.setSkills(SkillDatabase.getInstance().getSkills(myChar));
        enemy.setSkills(SkillDatabase.getInstance().getSkills(enemyChar));

        gameState.setPlayer(player);
        gameState.setEnemy(enemy);
        gameState.setState(Constants.STATE_LOADING);
    }

    private void onGameStartConfirmed() {
        if (gameState.getState() == Constants.STATE_PLAYING) return;
        SwingUtilities.invokeLater(() -> {
            gameState.setState(Constants.STATE_PLAYING);
            view.startGameDisplay();
            AudioPlayer.getInstance().playBGM("bgm.wav");
            startGameLoops();
        });
    }

    private void startGameLoops() {
        gameLoopTimer = new Timer(16, e -> {
            if (isPaused) return;
            if (gameState.getState() == Constants.STATE_GAME_OVER) { stopGame(); return; }
            gameState.update();
            view.repaintGame();
            checkGameOver();
        });
        gameLoopTimer.start();

        autoShootTimer = new Timer(333, e -> {
            if (isPaused) return;
            if (gameState.getState() == Constants.STATE_PLAYING) performAutoShoot();
        });
        autoShootTimer.start();

        // Voice Over Timer (5 detik)
        voiceOverTimer = new Timer(5000, e -> {
            if (isPaused || player == null || player.isDead()) return;
            String sound = (player.getType() == CharacterType.CAKIL) ? "cakil_sound.wav" : "gareng_sound.wav";
            AudioPlayer.getInstance().playSFX(sound);
        });
        voiceOverTimer.start();
    }

    // --- PAUSE SYSTEM ---
    public void startLocalPause() {
        pauseGame(false);
        sync.syncPause();
    }

    public void endLocalPause() {
        resumeGame();
        sync.syncResume();
    }

    public void pauseGame(boolean fromRemote) {
        isPaused = true;
        isRemotePause = fromRemote;
        if (gameLoopTimer != null) gameLoopTimer.stop();
        if (autoShootTimer != null) autoShootTimer.stop();
        if (voiceOverTimer != null) voiceOverTimer.stop();

        if (fromRemote) showSkillNotification("Lawan sedang merapal mantra...");
    }

    public void resumeGame() {
        isPaused = false;
        isRemotePause = false;
        if (gameLoopTimer != null) gameLoopTimer.start();
        if (autoShootTimer != null) autoShootTimer.start();
        if (voiceOverTimer != null) voiceOverTimer.start();
        view.startGameDisplay();
    }

    private void stopGame() {
        if (gameLoopTimer != null) gameLoopTimer.stop();
        if (autoShootTimer != null) autoShootTimer.stop();
        if (voiceOverTimer != null) voiceOverTimer.stop();
        String winner = gameState.getWinner();
        boolean isWinner = winner != null && player != null && winner.equals(player.getType().name());
        view.showResult(winner, isWinner);
    }

    private void checkGameOver() {
        if (gameState.getState() == Constants.STATE_GAME_OVER) stopGame();
    }

    @Override
    public void onSkill(int index) {
        if (player == null || player.isDead() || isPaused) return;
        List<Skill> skills = player.getSkills();
        if (index < 0 || index >= skills.size()) return;

        Skill s = skills.get(index);
        if (!player.consumeSukma(s.getSukmaCost())) {
            showSkillNotification("Sukma Tidak Cukup!");
            return;
        }

        startLocalPause(); // Freeze Game
        view.showQuiz(player, s);
    }

    public void applySkill(GameCharacter c, Skill s, boolean sendSync) {
        AudioPlayer.getInstance().playSFX("skill_ok.wav");
        String notifText = "";

        switch (s.getType()) {
            case ATTACK:
                int dmg = s.getDamage() + (int)(c.getBaseAttack() * 0.1);
                Projectile p = new Projectile(c.getX(), c.getY() + 40, c.getLane(), Constants.PROJECTILE_SPEED + 5, dmg, true, c.getProjectileImage());
                gameState.addProjectile(p);
                if (sendSync) sync.syncSkillAttack(c.getLane(), dmg);
                notifText = "Skill Serangan! +" + dmg + " DMG";
                break;
            case DEFENCE:
            case BUFF:
                int dur = (int)(s.getBuffDurationMillis()/1000);
                if (s.isImmuneDamage()) {
                    c.setImmuneDamage(true);
                    if (sendSync) sync.syncSkillActivate(s.getId(), "Immune", dur);
                    notifText = "KEBAL " + dur + " Detik!";
                }
                if (s.getAttackMultiplier() > 1.0) {
                    c.setAttackMultiplier(s.getAttackMultiplier());
                    String key = s.getAttackMultiplier() == 1.3 ? "ATK30" : "ATK50";
                    if (sendSync) sync.syncSkillActivate(s.getId(), key, dur);
                    notifText = "ATK +" + (s.getAttackMultiplier()==1.3?"30%":"50%");
                }
                if (s.getAttackSpeedMultiplier() > 1.0) {
                    c.setAttackSpeedMultiplier(s.getAttackSpeedMultiplier());
                    if (sendSync) sync.syncSkillActivate(s.getId(), "ATKSPD30", dur);
                    notifText = "Speed +30%";
                }
                break;
        }
        showSkillNotification(notifText);
    }

    public void showSkillNotification(String text) {
        if (view != null) view.showNotification(text);
    }

    @Override
    public void onMoveUp() {
        if (player == null || isPaused) return;
        player.moveUp();
        sync.syncPlayerMove(player.getLane());
    }

    @Override
    public void onMoveDown() {
        if (player == null || isPaused) return;
        player.moveDown();
        sync.syncPlayerMove(player.getLane());
    }

    private void performAutoShoot() {
        if (player == null || player.isDead()) return;
        Projectile p = new Projectile(player.getX(), player.getY() + 40, player.getLane(), Constants.PROJECTILE_SPEED, player.getCurrentDamage(), true, player.getProjectileImage());
        gameState.addProjectile(p);
        AudioPlayer.getInstance().playSFX("shoot.wav");
        sync.syncShoot(player.getLane(), player.getCurrentDamage());
    }

    @Override
    public GameState getGameState() { return gameState; }
    @Override
    public List<Projectile> getProjectiles() { return gameState.getProjectiles(); }

    @Override
    public void onMessageReceived(String type, String data) {
        switch (type) {
            case Constants.MSG_PLAYER_JOINED:
                if (net.isServer()) {
                    new Thread(() -> {
                        try { Thread.sleep(1000); } catch (InterruptedException e) {}
                        sync.syncGameStart();
                        onGameStartConfirmed();
                    }).start();
                }
                break;
            case Constants.MSG_GAME_START: onGameStartConfirmed(); break;
            case Constants.MSG_MOVE: sync.handleRemoteMove(data); break;
            case Constants.MSG_SHOOT: sync.handleRemoteShoot(data); break;
            case Constants.MSG_SKILL_ATTACK: sync.handleRemoteSkillAttack(data); break;
            case Constants.MSG_DAMAGE: sync.handleRemoteDamage(data); break;
            case Constants.MSG_SKILL_ACTIVATE: sync.handleRemoteSkillActivate(data); break;
            case Constants.MSG_GAME_OVER: sync.handleGameOver(data); break;
            case Constants.MSG_PAUSE: sync.handleRemotePause(); break;
            case Constants.MSG_RESUME: sync.handleRemoteResume(); break;
        }
    }
}