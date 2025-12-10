package util;

import javax.sound.sampled.*;
import java.io.File;

public class AudioPlayer {
    private static AudioPlayer instance;
    // Path ini relatif terhadap folder project (tempat file src berada)
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
                if (!f.exists()) {
                    System.err.println("[AUDIO ERROR] File not found: " + f.getAbsolutePath());
                    return;
                }

                System.out.println("[AUDIO] Playing BGM: " + filename);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);

                // Volume Control (Opsional: Mengecilkan suara BGM sedikit)
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-10.0f); // Kurangi 10 decibel agar tidak terlalu keras

                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            } catch (Exception e) {
                System.err.println("[AUDIO ERROR] " + e.getMessage());
            }
        }).start();
    }

    public void playSFX(String filename) {
        new Thread(() -> {
            try {
                File f = new File(basePath + filename);
                if (!f.exists()) {
                    // Jangan spam error jika file memang belum ada
                    // System.err.println("SFX not found: " + filename);
                    return;
                }

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