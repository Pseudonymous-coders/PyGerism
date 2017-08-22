package org.pseudonymous.plagiarism.gui;

import org.codehaus.plexus.util.DirectoryScanner;
import org.pseudonymous.plagiarism.config.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Resources {
    private static final String res_folder = "res";
    private static Resource[] resources;

    public static void init() {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{"**/*.*"});
        scanner.setBasedir(res_folder);
        scanner.scan();


        String[] file_names = scanner.getIncludedFiles();
        resources = new Resource[file_names.length];

        for (int ind = 0; ind < file_names.length; ind++) {
            String basename;
            try {
                basename = file_names[ind].split("\\.(?=[^\\.]+$)")[0];
            } catch (Exception err) {
                Logger.Log("Failed getting basename of file " + file_names[ind], true);
                basename = file_names[ind].substring(0, file_names[ind].length() - 4);
            }

            Logger.Log("Loading resource " + res_folder + File.separator + file_names[ind]);
            resources[ind] = new Resource(getImage((res_folder +
                    File.separator + file_names[ind])), basename);
        }
    }

    public static Image getResource(String resource) {
        for (Resource r : resources) {
            if (r.getName().equals(resource)) {
                return r.getIcon();
            }
        }
        Logger.Log("Failed to load resource " + resource + "! It doesn't exist!", true);
        return null;
    }

    public static BufferedImage getBufferedResource(String resource) {
        for (Resource r : resources) {
            if (r.getName().equals(resource)) {
                return r.getBuffered();
            }
        }
        Logger.Log("Failed to load buffered resource " + resource + "! It doesn't exist!", true);
        return null;
    }

    public static BufferedImage getImage(String path) {
        try {
            File imagePath = new File(path);
            if (!imagePath.isFile() || !imagePath.exists()) {
                Logger.Log("Couldn't find image " + path);
                return null;
            }
            return ImageIO.read(imagePath);
        } catch (IOException err) {
            err.printStackTrace();
            Logger.Log("Failed to load resource " + path, true);
        }

        return null;
    }

    public static BufferedImage ImageToBuffered(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        BufferedImage buffered_image = new BufferedImage(
                image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = buffered_image.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        return buffered_image;
    }
}