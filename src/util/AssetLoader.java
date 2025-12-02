package util;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AssetLoader {
    private static AssetLoader instance;
    private Map<String, Image> imageCache;
    private String basePath = "src/assets/images/";

    private AssetLoader() {
        imageCache = new HashMap<>();
    }

    public static AssetLoader getInstance() {
        if (instance == null) {
            instance = new AssetLoader();
        }
        return instance;
    }

    public void loadAllAssets() {
        try {
            // Load Cakil images
            loadImage("cakil_normal", "cakil_normal.png");
            loadImage("cakil_buffed", "cakil_buffed.png");
            loadImage("cakil_damaged", "cakil_damaged.png");
            loadImage("cakil_projectile", "cakil_projectile.png");

            // Load Patih Sabrang images
            loadImage("sabrang_normal", "sabrang_normal.png");
            loadImage("sabrang_buffed", "sabrang_buffed.png");
            loadImage("sabrang_damaged", "sabrang_damaged.png");
            loadImage("sabrang_projectile", "sabrang_projectile.png");

            // Load quiz background
            loadImage("quiz_background", "quiz_background.png");

            System.out.println("All assets loaded successfully!");
        } catch (Exception e) {
            System.err.println("Error loading assets: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadImage(String key, String filename) {
        try {
            File file = new File(basePath + filename);
            if (file.exists()) {
                Image img = ImageIO.read(file);
                imageCache.put(key, img);
                System.out.println("Loaded: " + filename);
            } else {
                System.err.println("File not found: " + basePath + filename);
                // Create placeholder image
                imageCache.put(key, createPlaceholder());
            }
        } catch (IOException e) {
            System.err.println("Error loading " + filename + ": " + e.getMessage());
            imageCache.put(key, createPlaceholder());
        }
    }

    private Image createPlaceholder() {
        java.awt.image.BufferedImage placeholder =
                new java.awt.image.BufferedImage(64, 64, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = placeholder.createGraphics();
        g.setColor(java.awt.Color.GRAY);
        g.fillRect(0, 0, 64, 64);
        g.setColor(java.awt.Color.BLACK);
        g.drawRect(0, 0, 63, 63);
        g.dispose();
        return placeholder;
    }

    public Image getImage(String key) {
        return imageCache.getOrDefault(key, createPlaceholder());
    }

    public Image getCakilNormal() { return getImage("cakil_normal"); }
    public Image getCakilBuffed() { return getImage("cakil_buffed"); }
    public Image getCakilDamaged() { return getImage("cakil_damaged"); }
    public Image getCakilProjectile() { return getImage("cakil_projectile"); }

    public Image getSabrangNormal() { return getImage("sabrang_normal"); }
    public Image getSabrangBuffed() { return getImage("sabrang_buffed"); }
    public Image getSabrangDamaged() { return getImage("sabrang_damaged"); }
    public Image getSabrangProjectile() { return getImage("sabrang_projectile"); }

    public Image getQuizBackground() { return getImage("quiz_background"); }
}