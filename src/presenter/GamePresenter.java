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
    private boolean isPaused = false;

    public GamePresenter(GameContract.View view) {
        this.view = view;
        sync.setGameState(gameState);
        sync.setOnGameStartCallback(this::onGameStartConfirmed);

        // Load Assets di awal
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

        // Setup Images
        AssetLoader al = AssetLoader.getInstance();
        // Load image logic (Sama seperti sebelumnya)
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
        // Mencegah double start
        if (gameState.getState() == Constants.STATE_PLAYING) return;

        // Pastikan dijalankan di Thread UI agar aman
        SwingUtilities.invokeLater(() -> {
            gameState.setState(Constants.STATE_PLAYING);
            view.startGameDisplay();
            AudioPlayer.getInstance().playBGM("bgm.wav");
            startGameLoops();
        });
    }

    private void startGameLoops() {
        // 1. Main Game Loop (Movement & Update) - 60 FPS
        gameLoopTimer = new Timer(16, e -> {
            if (isPaused) return; // Cek pause

            if (gameState.getState() == Constants.STATE_GAME_OVER) {
                stopGame();
                return;
            }
            gameState.update();
            view.repaintGame();
            checkGameOver();
        });
        gameLoopTimer.start();

        // 2. Auto Shoot Timer (3x per detik = ~333ms)
        autoShootTimer = new Timer(333, e -> {
            if (isPaused) return; // Cek pause

            if (gameState.getState() == Constants.STATE_PLAYING) {
                performAutoShoot();
            }
        });
        autoShootTimer.start();
    }

    // --- PAUSE & RESUME SYSTEM ---
    public void pauseGame() {
        isPaused = true;
        if (gameLoopTimer != null) gameLoopTimer.stop();
        if (autoShootTimer != null) autoShootTimer.stop();
    }

    public void resumeGame() {
        isPaused = false;
        if (gameLoopTimer != null) gameLoopTimer.start();
        if (autoShootTimer != null) autoShootTimer.start();
        // Kembalikan fokus ke game canvas lewat view
        view.startGameDisplay();
    }

    private void stopGame() {
        if (gameLoopTimer != null) gameLoopTimer.stop();
        if (autoShootTimer != null) autoShootTimer.stop();

        String winner = gameState.getWinner();
        boolean isWinner = winner != null && player != null && winner.equals(player.getType().name());
        view.showResult(winner, isWinner);
    }

    private void checkGameOver() {
        if (gameState.getState() == Constants.STATE_GAME_OVER) {
            stopGame();
        }
    }

    // --- CONTROLS ---
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

        // FIX: Posisi Spawn (Y + 40) agar keluar dari tengah badan/tangan
        int spawnY = player.getY() + 40;

        Projectile p = new Projectile(
                player.getX(), spawnY, player.getLane(),
                Constants.PROJECTILE_SPEED, player.getCurrentDamage(),
                true, player.getProjectileImage()
        );
        gameState.addProjectile(p);
        AudioPlayer.getInstance().playSFX("shoot.wav");
        sync.syncShoot(player.getLane(), player.getCurrentDamage());
    }

    @Override
    public void onSkill(int index) {
        if (player == null || player.isDead() || isPaused) return;
        List<Skill> skills = player.getSkills();
        if (index < 0 || index >= skills.size()) return;

        Skill s = skills.get(index);
        if (!player.consumeSukma(s.getSukmaCost())) {
            System.out.println("Sukma tidak cukup!");
            return;
        }

        // PAUSE GAME SAAT QUIZ MUNCUL
        pauseGame();
        view.showQuiz(player, s);
    }

    public void applySkill(GameCharacter c, Skill s, boolean sendSync) {
        AudioPlayer.getInstance().playSFX("skill_ok.wav");

        switch (s.getType()) {
            case ATTACK:
                int dmg = s.getDamage() + (int)(c.getBaseAttack() * 0.1);
                // Skill projectile spawn di tengah juga
                Projectile skillProj = new Projectile(
                        c.getX(), c.getY() + 40, c.getLane(),
                        Constants.PROJECTILE_SPEED + 5,
                        dmg, true, c.getProjectileImage()
                );
                gameState.addProjectile(skillProj);
                if (sendSync) sync.syncSkillAttack(c.getLane(), dmg);
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
    public GameState getGameState() { return gameState; }
    @Override
    public List<Projectile> getProjectiles() { return gameState.getProjectiles(); }

    // --- NETWORK CALLBACKS ---
    @Override
    public void onMessageReceived(String type, String data) {
        switch (type) {
            case Constants.MSG_PLAYER_JOINED:
                if (net.isServer()) {
                    // FIX: Beri delay 1 detik agar Client siap menerima pesan START
                    new Thread(() -> {
                        try { Thread.sleep(1000); } catch (InterruptedException e) {}
                        sync.syncGameStart(); // Kirim ke client
                        onGameStartConfirmed(); // Start server sendiri
                    }).start();
                }
                break;
            case Constants.MSG_GAME_START:
                onGameStartConfirmed();
                break;
            case Constants.MSG_MOVE:           sync.handleRemoteMove(data); break;
            case Constants.MSG_SHOOT:          sync.handleRemoteShoot(data); break;
            case Constants.MSG_SKILL_ATTACK:   sync.handleRemoteSkillAttack(data); break;
            case Constants.MSG_DAMAGE:         sync.handleRemoteDamage(data); break;
            case Constants.MSG_SKILL_ACTIVATE: sync.handleRemoteSkillActivate(data); break;
            case Constants.MSG_GAME_OVER:      sync.handleGameOver(data); break;
        }
    }
}