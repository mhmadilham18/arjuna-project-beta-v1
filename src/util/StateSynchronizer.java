package util;

import model.GameState;
import model.entities.Enemy;
import model.entities.GameCharacter;
import model.entities.Player;
import model.entities.Projectile;

public class StateSynchronizer {

    private static StateSynchronizer instance;
    private final Object lock = new Object();
    private GameState gameState;
    private final NetworkManager net = NetworkManager.getInstance();

    private StateSynchronizer() {}

    public static StateSynchronizer getInstance() {
        if (instance == null) instance = new StateSynchronizer();
        return instance;
    }

    public void setGameState(GameState gameState) {
        synchronized (lock) {
            this.gameState = gameState;
        }
    }

    // movement
    public void syncPlayerMove(int lane) {
        net.sendMessage(Constants.MSG_MOVE, String.valueOf(lane));
    }

    public void handleRemoteMove(String data) {
        synchronized (lock) {
            if (gameState == null) return;
            try {
                int lane = Integer.parseInt(data);
                Enemy enemy = gameState.getEnemy();
                if (enemy != null) {
                    enemy.setLane(lane);
                    enemy.updateYPosition();
                }
            } catch (Exception ignored) {}
        }
    }

    // shoot
    public void syncShoot(int lane, int damage) {
        net.sendMessage(Constants.MSG_SHOOT, lane + "," + damage);
    }

    public void handleRemoteShoot(String data) {
        synchronized (lock) {
            if (gameState == null) return;
            try {
                String[] parts = data.split(",");
                int lane = Integer.parseInt(parts[0]);
                int dmg = Integer.parseInt(parts[1]);

                Enemy enemy = gameState.getEnemy();
                if (enemy != null) {
                    Projectile p = new Projectile(
                            enemy.getX(),
                            enemy.getY(),
                            lane,
                            Constants.PROJECTILE_SPEED,
                            dmg,
                            false,
                            enemy.getProjectileImage()
                    );
                    gameState.addProjectile(p);
                }
            } catch (Exception ignored) {}
        }
    }

    // skill
    public void syncSkillActivate(int skillIndex, String effectKey, int durationSec) {
        net.sendMessage(Constants.MSG_SKILL_ACTIVATE, skillIndex + "," + effectKey + "," + durationSec);
    }

    public void handleRemoteSkillActivate(String data) {
        synchronized (lock) {
            if (gameState == null) return;
            try {
                String[] parts = data.split(",");
                String effect = parts[1];
                int duration = Integer.parseInt(parts[2]);
                Enemy enemy = gameState.getEnemy();
                if (enemy != null) applySkillEffect(enemy, effect, duration);
            } catch (Exception ignored) {}
        }
    }

    private void applySkillEffect(GameCharacter c, String effect, int durationSec) {
        if (effect.equals("Immune")) {
            c.setImmuneDamage(true);
            scheduleEnd(c, "immune", durationSec);
        } else if (effect.equals("ATKSPD30")) {
            c.setAttackSpeedMultiplier(1.3);
            scheduleEnd(c, "atkspd", durationSec);
        } else if (effect.equals("ATK30")) {
            c.setAttackMultiplier(1.3);
            scheduleEnd(c, "atk", durationSec);
        } else if (effect.equals("ATK50")) {
            c.setAttackMultiplier(1.5);
            scheduleEnd(c, "atk", durationSec);
        }
    }

    private void scheduleEnd(GameCharacter c, String type, int durationSec) {
        new Thread(() -> {
            try {
                Thread.sleep(durationSec * 1000L);
                synchronized (lock) {
                    switch (type) {
                        case "immune": c.setImmuneDamage(false); break;
                        case "atkspd": c.setAttackSpeedMultiplier(1.0); break;
                        case "atk":    c.setAttackMultiplier(1.0); break;
                    }
                }
            } catch (InterruptedException ignored) {}
        }).start();
    }

    // damage
    public void syncDamage(int damage) {
        net.sendMessage(Constants.MSG_DAMAGE, String.valueOf(damage));
    }

    public void handleRemoteDamage(String data) {
        synchronized (lock) {
            if (gameState == null) return;
            try {
                int dmg = Integer.parseInt(data);
                Player player = gameState.getPlayer();
                if (player != null) player.takeDamage(dmg);
            } catch (Exception ignored) {}
        }
    }

    // game over
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
