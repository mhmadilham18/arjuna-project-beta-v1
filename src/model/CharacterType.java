package model;

public enum CharacterType {
    CAKIL(1, "Cakil"),
    PATIH_SABRENG(2, "Patih Sabreng");

    private final int id;
    private final String name;

    CharacterType(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return  name;
    }

    public static CharacterType fromId(int id){
        for (CharacterType type : values()){
            if(type.id ==id) {
                return type;
            }
        }
        return CAKIL;
    }
}
