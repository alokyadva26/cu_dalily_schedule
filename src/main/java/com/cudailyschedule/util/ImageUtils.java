package com.cudailyschedule.util;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * ImageUtils provides safe, cached loading and high-quality scaling of UI graphic assets.
 */
public class ImageUtils {
    private static final Map<String, BufferedImage> IMAGE_CACHE = new HashMap<>();

    public static BufferedImage loadImage(String path) {
        if (IMAGE_CACHE.containsKey(path)) {
            return IMAGE_CACHE.get(path);
        }

        try {
            // 1. Try class loader resource
            InputStream is = ImageUtils.class.getResourceAsStream(path);
            if (is != null) {
                BufferedImage img = ImageIO.read(is);
                if (img != null) {
                    IMAGE_CACHE.put(path, img);
                    return img;
                }
            }

            // 2. Try relative file system path
            File file = new File(path.startsWith("/") ? path.substring(1) : path);
            if (file.exists()) {
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    IMAGE_CACHE.put(path, img);
                    return img;
                }
            }

            // 3. Try checking in standard resource subdirectories
            File resourceFile = new File("src/main/resources" + (path.startsWith("/") ? path : "/" + path));
            if (resourceFile.exists()) {
                BufferedImage img = ImageIO.read(resourceFile);
                if (img != null) {
                    IMAGE_CACHE.put(path, img);
                    return img;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static ImageIcon loadScaledIcon(String path, int width, int height) {
        BufferedImage img = loadImage(path);
        if (img == null) return null;
        Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
