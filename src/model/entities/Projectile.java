package model.entities;

import util.Constants;

import java.awt.Image;

public class Projectile {

    private int x, y;
    private final int lane;
    private final int speed;
    private final int damage;
    private final boolean fromPlayer;
    private final Image image;
    private boolean expired = false;

    public Projectile(int x, int y, int lane, int speed,
                      int damage, boolean fromPlayer, Image image) {
        this.x = x;
        this.y = y;
        this.lane = lane;
        this.speed = speed;
        this.damage = damage;
        this.fromPlayer = fromPlayer;
        this.image = image;
    }

    public void update() {
        x += fromPlayer ? speed : -speed;
        if (x < -50 || x > Constants.SCREEN_WIDTH + 50) {
            expired = true;
        }
    }

    public boolean collidesWith(GameCharacter c) {
        if (c.getLane() != lane) return false;
        int cx = c.getX();
        return Math.abs(cx - x) < 40;
    }

    public boolean isExpired() { return expired; }
    public int getDamage() { return damage; }
    public boolean isFromPlayer() { return fromPlayer; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Image getImage() { return image; }
}
