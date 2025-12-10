package util;

import javax.sound.sampled.*;
import java.io.File;
import java.util.Random;

public class AudioPlayer {
    private static AudioPlayer instance;
    private String basePath = "src/assets/audio/";
    private Random random = new Random(); // Untuk acak suara

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
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-10.0f);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    public void playSFX(String filename) {
        new Thread(() -> {
            try {
                File f = new File(basePath + filename);
                if (!f.exists()) {
                     System.err.println("File audio tidak ditemukan: " + filename);
                    return;
                }
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    // NEW: Metode untuk suara sakit acak
    public void playRandomHitSound() {
        if (random.nextBoolean()) {
            playSFX("aw.wav");
        } else {
            playSFX("aduh_sakitnya.wav");
        }
    }
}