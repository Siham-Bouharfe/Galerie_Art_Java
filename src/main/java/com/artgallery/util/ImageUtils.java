package com.artgallery.util;

import javafx.scene.image.Image;
import java.io.File;
import java.io.InputStream;

public class ImageUtils {

    private static final String DEFAULT_IMAGE = "/images/default.png";
    private static final String IMAGES_DIR = "src/main/resources/";

    public static Image loadImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return loadDefaultImage();
        }

        try {
            // 1. Essayer de charger depuis le classpath (ressources packagées)
            InputStream is = ImageUtils.class.getResourceAsStream("/" + imagePath);
            if (is != null) {
                return new Image(is);
            }

            // 2. Essayer de charger depuis le système de fichiers (nouveaux uploads)
            File file = new File(IMAGES_DIR + imagePath);
            if (file.exists()) {
                return new Image(file.toURI().toString());
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + imagePath);
            e.printStackTrace();
        }

        return loadDefaultImage();
    }

    private static Image loadDefaultImage() {
        try {
            InputStream is = ImageUtils.class.getResourceAsStream(DEFAULT_IMAGE);
            if (is != null) {
                return new Image(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
