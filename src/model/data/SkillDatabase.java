package model.data;

import model.CharacterType;
import model.SkillType;

import java.util.*;

public class SkillDatabase {

    private static SkillDatabase instance;
    private final Map<CharacterType, List<Skill>> skillsByCharacter = new HashMap<>();

    private SkillDatabase() {
        init();
    }

    public static SkillDatabase getInstance() {
        if (instance == null) instance = new SkillDatabase();
        return instance;
    }

    private void init() {
        List<Skill> cakilSkills = new ArrayList<>();
        // Tarian Darah – Immune Damage, 3 sec, CD 8, cost 3
        cakilSkills.add(new Skill(
                1,
                "Tarian Darah",
                "Cakil menari liar, sulit dibidik.",
                SkillType.DEFENCE,
                0,
                3,
                8000,
                true,
                1.0,
                1.0,
                true,
                3000
        ));
        // Tebasan Gigi Maut – ATK SPD +30%, 5 sec, CD 10, cost 4
        cakilSkills.add(new Skill(
                2,
                "Tebasan Gigi Maut",
                "Serangan cepat meninggalkan luka.",
                SkillType.BUFF,
                0,
                4,
                10000,
                true,
                1.0,
                1.3,
                false,
                5000
        ));
        // Kengerian Kegelapan – ATK +30%, 4 sec, CD 9, cost 4
        cakilSkills.add(new Skill(
                3,
                "Kengerian Kegelapan",
                "Tawa menyeramkan memberi kekuatan.",
                SkillType.BUFF,
                0,
                4,
                9000,
                true,
                1.3,
                1.0,
                false,
                4000
        ));
        skillsByCharacter.put(CharacterType.CAKIL, cakilSkills);

        List<Skill> sabrangSkills = new ArrayList<>();
        // Lemparan Gada Seberang – Damage 50 + 10% atk, CD 10, cost 3
        sabrangSkills.add(new Skill(
                4,
                "Lemparan Gada Seberang",
                "Lemparan gada lurus dengan amarah.",
                SkillType.ATTACK,
                50, // bonus 10% ATK dihitung saat eksekusi
                3,
                10000,
                false,
                1.0,
                1.0,
                false,
                0
        ));
        // Teriakan Neraka – Immune 3s, CD 8, cost 4
        sabrangSkills.add(new Skill(
                5,
                "Teriakan Neraka",
                "Raungan membuatnya tak terkalahkan.",
                SkillType.DEFENCE,
                0,
                4,
                8000,
                true,
                1.0,
                1.0,
                true,
                3000
        ));
        // Raksha – ATK +50% 5s, CD 12, cost 5
        sabrangSkills.add(new Skill(
                6,
                "Raksha",
                "Fokus penuh meningkatkan kekuatan.",
                SkillType.BUFF,
                0,
                5,
                12000,
                true,
                1.5,
                1.0,
                false,
                5000
        ));
        skillsByCharacter.put(CharacterType.PATIH_SABRENG, sabrangSkills);
    }

    public List<Skill> getSkills(CharacterType type) {
        return skillsByCharacter.get(type);
    }
}
