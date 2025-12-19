package model;

import model.entities.Enemy;
import model.entities.Player;
import model.entities.Projectile;
import util.AudioPlayer;
import util.Constants;
import util.StateSynchronizer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameState {

    private Player player;
    private Enemy enemy;
    private final List<Projectile> projectiles = new CopyOnWriteArrayList<>();
    private String winner;
    private int state = Constants.STATE_LOADING;

    public Player getPlayer() { return player; }
    public Enemy getEnemy() { return enemy; }
    public List<Projectile> getProjectiles() { return projectiles; }

    public void setPlayer(Player player) { this.player = player; }
    public void setEnemy(Enemy enemy) { this.enemy = enemy; }

    public void addProjectile(Projectile p) { projectiles.add(p); }

    public void update() {
        if (state != Constants.STATE_PLAYING) return;

        if (player != null) player.update();
        if (enemy != null) enemy.update();

        List<Projectile> toRemove = new ArrayList<>();
        for (Projectile p : projectiles) {
            p.update();

            if (player != null && !p.isFromPlayer() && p.collidesWith(player)) {
                player.takeDamage(p.getDamage());
                toRemove.add(p);

                System.out.println("Player Hit! Playing sound...");
                AudioPlayer.getInstance().playRandomHitSound();
                StateSynchronizer.getInstance().syncMyHp(player.getHp());
            }

            else if (enemy != null && p.isFromPlayer() && p.collidesWith(enemy)) {
                toRemove.add(p);
            }
            else if (p.isExpired()) {
                toRemove.add(p);
            }
        }
        projectiles.removeAll(toRemove);

        if (player != null && player.isDead()) {
            winner = enemy != null ? enemy.getType().name(): "Enemy";
            state = Constants.STATE_GAME_OVER;
        } else if (enemy != null && enemy.isDead()) {
            winner = player != null ? player.getType().name() : "Player";
            state = Constants.STATE_GAME_OVER;
        }
    }

    public int getState() { return state; }
    public void setState(int state) { this.state = state; }

    public String getWinner() { return winner; }
    public void setWinner(String winner) { this.winner = winner; }
}