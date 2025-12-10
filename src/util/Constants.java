package util;

public class Constants {

    // Game states
    public static final int STATE_LOADING   = 0;
    public static final int STATE_PLAYING   = 1;
    public static final int STATE_GAME_OVER = 2;

    // Lanes & layar
    public static final int LANE_COUNT   = 8;
    public static final int LANE_HEIGHT  = 80;
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
    public static final int STATE_PAUSED    = 3;

    // Game Config
    public static final int PROJECTILE_SPEED = 14;
    public static final int DEFAULT_PORT = 5000;

    // Network
    public static final String MESSAGE_DELIMITER = "|";
    public static final String MSG_PLAYER_JOINED  = "PLAYER_JOINED";
    public static final String MSG_GAME_START     = "START";
    public static final String MSG_MOVE           = "MOVE";
    public static final String MSG_SHOOT          = "SHOOT";
    public static final String MSG_SKILL_ATTACK   = "SKILL_ATK";
    public static final String MSG_DAMAGE         = "DMG";
    public static final String MSG_SKILL_ACTIVATE = "SKILL";
    public static final String MSG_GAME_OVER      = "GAME_OVER";
    public static final String MSG_PAUSE          = "PAUSE";
    public static final String MSG_RESUME         = "RESUME";

}
