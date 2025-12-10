package util;

import model.GameState;
import model.entities.Enemy;
import model.entities.GameCharacter;
import model.entities.Projectile;
import presenter.GamePresenter;

public class StateSynchronizer {

    private static StateSynchronizer instance;
    private final Object lock = new Object();
    private GameState gameState;
    private final NetworkManager net = NetworkManager.getInstance();
    private Runnable onGameStartCallback;
    private GamePresenter presenter;

    private StateSynchronizer() {}

    public static StateSynchronizer getInstance() {
        if (instance == null) instance = new StateSynchronizer();
        return instance;
    }

    public void setPresenter(GamePresenter presenter) {
        this.presenter = presenter;
    }

    public void setGameState(GameState gameState) {
        synchronized (lock) { this.gameState = gameState; }
    }

    public void setOnGameStartCallback(Runnable callback) {
        this.onGameStartCallback = callback;
    }

    public void syncPause() { net.sendMessage(Constants.MSG_PAUSE, "STOP"); }
    public void handleRemotePause() { if (presenter != null) presenter.pauseGame(true); }

    public void syncResume() { net.sendMessage(Constants.MSG_RESUME, "GO"); }
    public void handleRemoteResume() { if (presenter != null) presenter.resumeGame(); }

    public void syncGameStart() { net.sendMessage(Constants.MSG_GAME_START, "GO"); }
    public void syncPlayerMove(int lane) { net.sendMessage(Constants.MSG_MOVE, String.valueOf(lane)); }

    public void handleRemoteMove(String data) {
        synchronized (lock) {
            if (gameState == null || gameState.getEnemy() == null) return;
            try {
                int lane = Integer.parseInt(data);
                gameState.getEnemy().setLane(lane);
                gameState.getEnemy().updateYPosition();
            } catch (Exception ignored) {}
        }
    }

    public void syncShoot(int lane, int damage) { net.sendMessage(Constants.MSG_SHOOT, lane + "," + damage); }
    public void handleRemoteShoot(String data) { spawnRemoteProjectile(data, false); }

    public void syncSkillAttack(int lane, int damage) { net.sendMessage(Constants.MSG_SKILL_ATTACK, lane + "," + damage); }
    public void handleRemoteSkillAttack(String data) { spawnRemoteProjectile(data, true); }

    private void spawnRemoteProjectile(String data, boolean isSkill) {
        synchronized (lock) {
            if (gameState == null || gameState.getEnemy() == null) return;
            try {
                String[] parts = data.split(",");
                int lane = Integer.parseInt(parts[0]);
                int dmg = Integer.parseInt(parts[1]);
                Enemy enemy = gameState.getEnemy();
                Projectile p = new Projectile(enemy.getX(), enemy.getY() + 40, lane, Constants.PROJECTILE_SPEED, dmg, false, enemy.getProjectileImage());
                gameState.addProjectile(p);
            } catch (Exception ignored) {}
        }
    }

    // --- MODIFIKASI DISINI: SKILL NAME SYNC ---
    public void syncSkillActivate(int skillIndex, String skillName, String effectKey, int durationSec) {
        // Format: ID, Nama, Efek, Durasi
        net.sendMessage(Constants.MSG_SKILL_ACTIVATE, skillIndex + "," + skillName + "," + effectKey + "," + durationSec);
    }

    public void handleRemoteSkillActivate(String data) {
        synchronized (lock) {
            if (gameState == null || gameState.getEnemy() == null) return;
            try {
                String[] parts = data.split(",");
                // parts[0] = index (skip)
                String skillName = parts[1]; // Nama Skill
                String effect = parts[2];    // Key Effect
                int duration = Integer.parseInt(parts[3]);

                // Tampilkan nama skill musuh di layar kita
                if (presenter != null) {
                    presenter.showSkillNotification("Lawan: " + skillName + "!");
                }

                applySkillEffect(gameState.getEnemy(), effect, duration);
            } catch (Exception ignored) {}
        }
    }

    private void applySkillEffect(GameCharacter c, String effect, int durationSec) {
        if (effect.equals("Immune")) c.setImmuneDamage(true);
        else if (effect.equals("ATKSPD30")) c.setAttackSpeedMultiplier(1.3);
        else if (effect.equals("ATK30")) c.setAttackMultiplier(1.3);
        else if (effect.equals("ATK50")) c.setAttackMultiplier(1.5);

        new Thread(() -> {
            try {
                Thread.sleep(durationSec * 1000L);
                synchronized (lock) {
                    if (effect.equals("Immune")) c.setImmuneDamage(false);
                    else if (effect.equals("ATKSPD30")) c.setAttackSpeedMultiplier(1.0);
                    else if (effect.startsWith("ATK")) c.setAttackMultiplier(1.0);
                }
            } catch (InterruptedException ignored) {}
        }).start();
    }

    public void syncDamage(int damage) { net.sendMessage(Constants.MSG_DAMAGE, String.valueOf(damage)); }
    public void handleRemoteDamage(String data) {
        synchronized (lock) {
            if (gameState == null || gameState.getPlayer() == null) return;
            try { gameState.getPlayer().takeDamage(Integer.parseInt(data)); } catch (Exception ignored) {}
        }
    }
    public void syncGameOver(String winner) { net.sendMessage(Constants.MSG_GAME_OVER, winner); }
    public void handleGameOver(String winner) {
        synchronized (lock) {
            if (gameState != null) {
                gameState.setWinner(winner);
                gameState.setState(Constants.STATE_GAME_OVER);
            }
        }
    }
}