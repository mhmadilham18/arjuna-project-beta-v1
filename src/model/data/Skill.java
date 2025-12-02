package model.data;

import model.SkillType;

public class Skill {

    private final int id;
    private final String name;
    private final String description;
    private final SkillType type;

    private final int damage;           // jika ATTACK
    private final int sukmaCost;
    private final long cooldownMillis;

    private final boolean givesBuff;
    private final double attackMultiplier;
    private final double attackSpeedMultiplier;
    private final boolean immuneDamage;
    private final long buffDurationMillis;

    public Skill(int id, String name, String description,
                 SkillType type,
                 int damage,
                 int sukmaCost,
                 long cooldownMillis,
                 boolean givesBuff,
                 double attackMultiplier,
                 double attackSpeedMultiplier,
                 boolean immuneDamage,
                 long buffDurationMillis) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.damage = damage;
        this.sukmaCost = sukmaCost;
        this.cooldownMillis = cooldownMillis;
        this.givesBuff = givesBuff;
        this.attackMultiplier = attackMultiplier;
        this.attackSpeedMultiplier = attackSpeedMultiplier;
        this.immuneDamage = immuneDamage;
        this.buffDurationMillis = buffDurationMillis;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public SkillType getType() { return type; }
    public int getDamage() { return damage; }
    public int getSukmaCost() { return sukmaCost; }
    public long getCooldownMillis() { return cooldownMillis; }
    public boolean isGivesBuff() { return givesBuff; }
    public double getAttackMultiplier() { return attackMultiplier; }
    public double getAttackSpeedMultiplier() { return attackSpeedMultiplier; }
    public boolean isImmuneDamage() { return immuneDamage; }
    public long getBuffDurationMillis() { return buffDurationMillis; }
}
