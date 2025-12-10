package util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {
    private static AudioPlayer instance;
    private String basePath = "src/assets/audio/";

    private AudioPlayer() {}

    public static AudioPlayer getInstance() {
        if (instance == null) instance = new AudioPlayer();
        return instance;
    }

    public void playBGM(String filename) {
        new Thread(() -> {
            try {
                File f = new File(basePath + filename);
                if (!f.exists()) return;
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void playSFX(String filename) {
        new Thread(() -> {
            try {
                File f = new File(basePath + filename);
                if (!f.exists()) return;
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}