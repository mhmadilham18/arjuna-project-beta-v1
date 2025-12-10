package util;

import model.GameState;
import model.entities.Enemy;
import model.entities.GameCharacter;
import model.entities.Projectile;

public class StateSynchronizer {

    private static StateSynchronizer instance;
    private final Object lock = new Object();
    private GameState gameState;
    private final NetworkManager net = NetworkManager.getInstance();

    // Callback untuk memberitahu presenter game mulai
    private Runnable onGameStartCallback;

    private StateSynchronizer() {}

    public static StateSynchronizer getInstance() {
        if (instance == null) instance = new StateSynchronizer();
        return instance;
    }

    public void setGameState(GameState gameState) {
        synchronized (lock) { this.gameState = gameState; }
    }

    public void setOnGameStartCallback(Runnable callback) {
        this.onGameStartCallback = callback;
    }

    // --- GAME FLOW ---
    public void syncGameStart() {
        net.sendMessage(Constants.MSG_GAME_START, "GO");
    }

    public void handleRemoteGameStart() {
        if (onGameStartCallback != null) {
            onGameStartCallback.run();
        }
    }

    // --- MOVEMENT ---
    public void syncPlayerMove(int lane) {
        net.sendMessage(Constants.MSG_MOVE, String.valueOf(lane));
    }

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

    // --- SHOOTING (Auto) ---
    public void syncShoot(int lane, int damage) {
        net.sendMessage(Constants.MSG_SHOOT, lane + "," + damage);
    }

    public void handleRemoteShoot(String data) {
        synchronized (lock) {
            if (gameState == null || gameState.getEnemy() == null) return;
            try {
                String[] parts = data.split(",");
                int lane = Integer.parseInt(parts[0]);
                int dmg = Integer.parseInt(parts[1]);
                spawnEnemyProjectile(lane, dmg, false); // Normal projectile
            } catch (Exception ignored) {}
        }
    }

    // --- SKILL ATTACK ---
    public void syncSkillAttack(int lane, int damage) {
        net.sendMessage(Constants.MSG_SKILL_ATTACK, lane + "," + damage);
    }

    public void handleRemoteSkillAttack(String data) {
        synchronized (lock) {
            if (gameState == null || gameState.getEnemy() == null) return;
            try {
                String[] parts = data.split(",");
                int lane = Integer.parseInt(parts[0]);
                int dmg = Integer.parseInt(parts[1]);
                spawnEnemyProjectile(lane, dmg, true); // Skill projectile (maybe different visual?)
            } catch (Exception ignored) {}
        }
    }

    private void spawnEnemyProjectile(int lane, int damage, boolean isSkill) {
        Enemy enemy = gameState.getEnemy();
        Projectile p = new Projectile(
                enemy.getX(), enemy.getY(), lane, Constants.PROJECTILE_SPEED,
                damage, false, enemy.getProjectileImage()
        );
        gameState.addProjectile(p);
    }

    // --- SKILL BUFF/DEFENCE ---
    public void syncSkillActivate(int skillIndex, String effectKey, int durationSec) {
        net.sendMessage(Constants.MSG_SKILL_ACTIVATE, skillIndex + "," + effectKey + "," + durationSec);
    }

    public void handleRemoteSkillActivate(String data) {
        synchronized (lock) {
            if (gameState == null || gameState.getEnemy() == null) return;
            try {
                String[] parts = data.split(",");
                String effect = parts[1];
                int duration = Integer.parseInt(parts[2]);
                applySkillEffect(gameState.getEnemy(), effect, duration);
            } catch (Exception ignored) {}
        }
    }

    private void applySkillEffect(GameCharacter c, String effect, int durationSec) {
        // Logika visual sederhana
        if (effect.equals("Immune")) c.setImmuneDamage(true);
        else if (effect.equals("ATKSPD30")) c.setAttackSpeedMultiplier(1.3);
        else if (effect.equals("ATK30")) c.setAttackMultiplier(1.3);
        else if (effect.equals("ATK50")) c.setAttackMultiplier(1.5);

        // Matikan efek setelah durasi (Thread terpisah)
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

    // --- DAMAGE & GAME OVER ---
    public void syncDamage(int damage) {
        net.sendMessage(Constants.MSG_DAMAGE, String.valueOf(damage));
    }

    public void handleRemoteDamage(String data) {
        synchronized (lock) {
            if (gameState == null || gameState.getPlayer() == null) return;
            try {
                gameState.getPlayer().takeDamage(Integer.parseInt(data));
            } catch (Exception ignored) {}
        }
    }

    public void syncGameOver(String winner) {
        net.sendMessage(Constants.MSG_GAME_OVER, winner);
    }

    public void handleGameOver(String winner) {
        synchronized (lock) {
            if (gameState != null) {
                gameState.setWinner(winner);
                gameState.setState(Constants.STATE_GAME_OVER);
            }
        }
    }
}