package carrotfantasy;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

// Refactored with Flyweight Pattern
// Flyweight interface for image resources
interface ImageFlyweight {
    ImageIcon getImageIcon(int x, int y, int width, int height, double ratio, boolean rotate);
    ImageIcon getImageIcon(int x, int y, int width, int height, double ratio, double degrees);
    BufferedImage getBaseImage();
    String getImageKey();
}

// Concrete Flyweight - represents shared image data
class ConcreteImageFlyweight implements ImageFlyweight {
    private final String imagePath;
    private final BufferedImage baseImage;
    private final String imageKey;
    private static ImageReader imgReader = new ImageReader();

    public ConcreteImageFlyweight(String imagePath) {
        this.imagePath = imagePath;
        this.imageKey = imagePath;
        this.baseImage = loadBaseImage(imagePath);
    }

    private BufferedImage loadBaseImage(String path) {
        try {
            java.io.File imageFile = ImageReader.getImageFile(path);
            return javax.imageio.ImageIO.read(imageFile);
        } catch (Exception e) {
            System.err.println("Error loading base image: " + path);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ImageIcon getImageIcon(int x, int y, int width, int height, double ratio, boolean rotate) {
        if (baseImage == null) {
            return new ImageIcon();
        }
        return imgReader.getImageIconFromBufferedImage(baseImage, x, y, width, height, ratio, rotate);
    }

    @Override
    public ImageIcon getImageIcon(int x, int y, int width, int height, double ratio, double degrees) {
        if (baseImage == null) {
            return new ImageIcon();
        }
        return imgReader.getImageIconFromBufferedImage(baseImage, x, y, width, height, ratio, degrees);
    }

    @Override
    public BufferedImage getBaseImage() {
        return baseImage;
    }

    @Override
    public String getImageKey() {
        return imageKey;
    }
}

// Flyweight Factory - manages flyweight instances
class ImageFlyweightFactory {
    private static final Map<String, ImageFlyweight> flyweightPool = new HashMap<>();
    private static final Object lock = new Object();

    // Get or create flyweight instance
    public static ImageFlyweight getFlyweight(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }

        // Normalize path
        String normalizedPath = normalizePath(imagePath);
        
        synchronized (lock) {
            // Check if flyweight already exists
            if (flyweightPool.containsKey(normalizedPath)) {
                return flyweightPool.get(normalizedPath);
            }

            // Create new flyweight
            ImageFlyweight flyweight = new ConcreteImageFlyweight(normalizedPath);
            flyweightPool.put(normalizedPath, flyweight);
            return flyweight;
        }
    }

    // Get flyweight with subimage key (for sprite sheets)
    public static ImageFlyweight getFlyweight(String imagePath, int x, int y, int width, int height) {
        String key = imagePath + ":" + x + "," + y + "," + width + "," + height;
        return getFlyweight(key);
    }

    // Clear flyweight pool (for memory management)
    public static void clearPool() {
        synchronized (lock) {
            flyweightPool.clear();
        }
    }

    // Get pool size (for debugging)
    public static int getPoolSize() {
        synchronized (lock) {
            return flyweightPool.size();
        }
    }

    private static String normalizePath(String path) {
        return path.replace('\\', '/');
    }
}

