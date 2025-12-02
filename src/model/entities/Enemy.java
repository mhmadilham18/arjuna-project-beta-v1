package model.entities;

import model.CharacterType;
import util.Constants;

public class Enemy extends GameCharacter {

    public Enemy(CharacterType type, String name) {
        super(type, name, false);
        this.x = Constants.SCREEN_WIDTH - 220;
    }
}
