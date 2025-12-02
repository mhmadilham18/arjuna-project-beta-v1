package model.entities;

import model.CharacterType;

public class Player extends GameCharacter {

    public Player(CharacterType type, String name) {
        super(type, name, true);
        this.x = 120;
    }
}
