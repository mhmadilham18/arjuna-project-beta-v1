package model.entities;

import model.CharacterState;
import model.CharacterType;
import model.data.Skill;
import util.Constants;

import java.awt.Image;
import java.util.List;

public class GameCharacter {

    public static final int MAX_HP = 100;
    public static final int MAX_SUKMA = 20;
    public static final int SUKMA_RECHARGE_RATE = 1;

    protected CharacterType type;
    protected String playerName;
    protected boolean isPlayer;

    protected int hp;
    protected int sukma;
    protected int baseAttack = 10;
//    protected int attackSpeedMillis = 333;
    protected int lane = 4;

    protected CharacterState state = CharacterState.NORMAL;
    protected long lastSukmaRecharge;

//    protected long lastAttackTime;

    protected List<Skill> skills;

    protected Image normalImage;
    protected Image buffedImage;
    protected Image damagedImage;
    protected Image projectileImage;

    protected int x, y;

    protected boolean immuneDamage = false;
    protected double attackMultiplier = 1.0;
    protected double attackSpeedMultiplier = 1.0;

    public GameCharacter(CharacterType type, String playerName, boolean isPlayer) {
        this.type = type;
        this.playerName = playerName;
        this.isPlayer = isPlayer;
        this.hp = MAX_HP;
        this.sukma = MAX_SUKMA;
        this.lastSukmaRecharge = System.currentTimeMillis();
    }

    public void update() {
        long now = System.currentTimeMillis();
        if (sukma < MAX_SUKMA && now - lastSukmaRecharge >= 3000) {
            sukma = Math.min(MAX_SUKMA, sukma + SUKMA_RECHARGE_RATE);
            lastSukmaRecharge = now;
        }
        updateVisualState();
        updateYPosition();
    }

    protected void updateVisualState() {
        if (hp <= 0) {
            state = CharacterState.DAMAGED;
        } else if (immuneDamage || attackMultiplier > 1.0 || attackSpeedMultiplier > 1.0) {
            state = CharacterState.BUFFED;
        } else {
            state = CharacterState.NORMAL;
        }
    }

    public void updateYPosition() {
        this.y = lane * Constants.LANE_HEIGHT + 20;
    }

    public void takeDamage(int damage) {
        if (immuneDamage) return;
        hp = Math.max(0, hp - damage);
        state = CharacterState.DAMAGED;
    }

    public int getCurrentDamage() {
        return (int) (baseAttack * attackMultiplier);
    }

    public int getBaseAttack() { return baseAttack; }

    public Image getCurrentImage() {
        switch (state) {
            case BUFFED: return buffedImage != null ? buffedImage : normalImage;
            case DAMAGED: return damagedImage != null ? damagedImage : normalImage;
            default: return normalImage;
        }
    }

    // --- Helpers ---
    public boolean consumeSukma(int amount) {
        if (sukma < amount) return false;
        sukma -= amount;
        return true;
    }

    public void addSukma(int amount) {
        this.sukma = Math.min(MAX_SUKMA, this.sukma + amount);
    }

    public void setHp(int hp) { this.hp = hp; }
    public void setSukma(int sukma) { this.sukma = sukma; }

    public boolean isDead() { return hp <= 0; }

    // --- Getters & Setters ---
    public CharacterType getType() { return type; }
    public boolean isPlayer() { return isPlayer; }
    public String getPlayerName() { return playerName; } // Added
    public int getHp() { return hp; }
    public int getSukma() { return sukma; }
    public int getLane() { return lane; }
    public int getX() { return x; }
    public int getY() { return y; }
    public List<Skill> getSkills() { return skills; }
    public Image getProjectileImage() { return projectileImage; }

    public void setLane(int lane) { this.lane = Math.max(0, Math.min(Constants.LANE_COUNT - 1, lane)); }
    public void moveUp() { setLane(lane - 1); }
    public void moveDown() { setLane(lane + 1); }

    public void setSkills(List<Skill> skills) { this.skills = skills; }
    public void setNormalImage(Image img) { this.normalImage = img; }
    public void setBuffedImage(Image img) { this.buffedImage = img; }
    public void setDamagedImage(Image img) { this.damagedImage = img; }
    public void setProjectileImage(Image img) { this.projectileImage = img; }

    public void setImmuneDamage(boolean immuneDamage) { this.immuneDamage = immuneDamage; }
    public void setAttackMultiplier(double attackMultiplier) { this.attackMultiplier = attackMultiplier; }
    public void setAttackSpeedMultiplier(double attackSpeedMultiplier) { this.attackSpeedMultiplier = attackSpeedMultiplier; }
}
