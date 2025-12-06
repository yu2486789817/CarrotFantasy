package carrotfantasy;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageReader {
	private static String projectRoot = null;
	
	// Find project root directory by looking for Images folder
	private static String findProjectRoot() {
		if (projectRoot != null) {
			return projectRoot;
		}
		
		// Try current working directory
		File currentDir = new File(System.getProperty("user.dir"));
		if (new File(currentDir, "Images").exists()) {
			projectRoot = currentDir.getAbsolutePath();
			return projectRoot;
		}
		
		// Try parent directory
		File parentDir = currentDir.getParentFile();
		if (parentDir != null && new File(parentDir, "Images").exists()) {
			projectRoot = parentDir.getAbsolutePath();
			return projectRoot;
		}
		
		// Try looking up from class location
		try {
			java.net.URL classUrl = ImageReader.class.getProtectionDomain()
					.getCodeSource().getLocation();
			File classDir;
			if (classUrl.getProtocol().equals("file")) {
				classDir = new File(classUrl.getPath());
			} else {
				// Handle jar files or other protocols
				String classPath = classUrl.getPath();
				// Decode URL encoding
				try {
					classPath = java.net.URLDecoder.decode(classPath, "UTF-8");
				} catch (java.io.UnsupportedEncodingException e) {
					// Ignore
				}
				classDir = new File(classPath);
			}
			
			if (classDir.isFile()) {
				classDir = classDir.getParentFile();
			}
			
			// Look for Images directory going up
			File searchDir = classDir;
			for (int i = 0; i < 5 && searchDir != null; i++) {
				if (new File(searchDir, "Images").exists()) {
					projectRoot = searchDir.getAbsolutePath();
					return projectRoot;
				}
				searchDir = searchDir.getParentFile();
			}
		} catch (Exception e) {
			// Ignore
		}
		
		// Default to current directory
		projectRoot = currentDir.getAbsolutePath();
		return projectRoot;
	}
	
	// Normalize path separators to forward slash
	private static String normalizePath(String path) {
		return path.replace('\\', '/');
	}
	
	// Get full file path - Refactored with Flyweight Pattern (made package-private for flyweight access)
	static File getImageFile(String file) {
		String normalizedPath = normalizePath(file);
		File imageFile = new File(normalizedPath);
		
		// If absolute path or file exists, return as is
		if (imageFile.isAbsolute() || imageFile.exists()) {
			return imageFile;
		}
		
		// Try relative to project root
		String root = findProjectRoot();
		File rootFile = new File(root, normalizedPath);
		if (rootFile.exists()) {
			return rootFile;
		}
		
		// Return original path (will fail with proper error)
		return imageFile;
	}
	
	ImageIcon getImageIcon(String file, int x, int y, int width, int height, double ratio, boolean rotate){
		File imageFile = getImageFile(file);
		BufferedImage img;
		ImageIcon imageicon = new ImageIcon();
		try {
			img = ImageIO.read(imageFile);
			if (img == null) {
				System.err.println("Failed to read image: " + imageFile.getAbsolutePath());
				return imageicon;
			}
			BufferedImage outImg=img.getSubimage(x, y, width, height);
			if(rotate) outImg = rotate(outImg, -90.0);
			imageicon = new ImageIcon(outImg);
			imageicon = new ImageIcon(imageicon.getImage().getScaledInstance((int)(imageicon.getIconWidth() * ratio), (int)(imageicon.getIconHeight() * ratio), Image.SCALE_FAST));
		} catch (IOException e) {
			System.err.println("Error reading image file: " + imageFile.getAbsolutePath());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error processing image: " + imageFile.getAbsolutePath());
			e.printStackTrace();
		}
		return imageicon;
	}
	
	ImageIcon getImageIcon(String file, int x, int y, int width, int height, double ratio, double degrees){
		File imageFile = getImageFile(file);
		BufferedImage img;
		ImageIcon imageicon = new ImageIcon();
		try {
			img = ImageIO.read(imageFile);
			if (img == null) {
				System.err.println("Failed to read image: " + imageFile.getAbsolutePath());
				return imageicon;
			}
			BufferedImage outImg=img.getSubimage(x, y, width, height);
			outImg = rotate(outImg, degrees);
			imageicon = new ImageIcon(outImg);
			imageicon = new ImageIcon(imageicon.getImage().getScaledInstance((int)(imageicon.getIconWidth() * ratio), (int)(imageicon.getIconHeight() * ratio), Image.SCALE_FAST));
		} catch (IOException e) {
			System.err.println("Error reading image file: " + imageFile.getAbsolutePath());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error processing image: " + imageFile.getAbsolutePath());
			e.printStackTrace();
		}
		return imageicon;
	}
	
	public BufferedImage rotate(BufferedImage image, Double degrees) {
		double radians = Math.toRadians(degrees);
		double sin = Math.abs(Math.sin(radians));
		double cos = Math.abs(Math.cos(radians));
	    int newWidth = (int) Math.round(image.getWidth() * cos + image.getHeight() * sin);
	    int newHeight = (int) Math.round(image.getWidth() * sin + image.getHeight() * cos);
		
		BufferedImage rotate = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = rotate.createGraphics();
		int x = (newWidth - image.getWidth()) / 2;
		int y = (newHeight - image.getHeight()) / 2;
		AffineTransform at = new AffineTransform();
		at.setToRotation(radians, x + (image.getWidth() / 2), y + (image.getHeight() / 2));
		at.translate(x, y);
		g2d.setTransform(at);
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
		return rotate;
	}

	// Refactored with Flyweight Pattern - helper method to create ImageIcon from BufferedImage
	ImageIcon getImageIconFromBufferedImage(BufferedImage baseImage, int x, int y, int width, int height, double ratio, boolean rotate) {
		ImageIcon imageicon = new ImageIcon();
		try {
			if (baseImage == null || x + width > baseImage.getWidth() || y + height > baseImage.getHeight()) {
				System.err.println("Invalid subimage parameters");
				return imageicon;
			}
			BufferedImage outImg = baseImage.getSubimage(x, y, width, height);
			if(rotate) outImg = rotate(outImg, -90.0);
			imageicon = new ImageIcon(outImg);
			imageicon = new ImageIcon(imageicon.getImage().getScaledInstance((int)(imageicon.getIconWidth() * ratio), (int)(imageicon.getIconHeight() * ratio), Image.SCALE_FAST));
		} catch (Exception e) {
			System.err.println("Error processing subimage");
			e.printStackTrace();
		}
		return imageicon;
	}

	// Refactored with Flyweight Pattern - helper method to create ImageIcon from BufferedImage with degrees
	ImageIcon getImageIconFromBufferedImage(BufferedImage baseImage, int x, int y, int width, int height, double ratio, double degrees) {
		ImageIcon imageicon = new ImageIcon();
		try {
			if (baseImage == null || x + width > baseImage.getWidth() || y + height > baseImage.getHeight()) {
				System.err.println("Invalid subimage parameters");
				return imageicon;
			}
			BufferedImage outImg = baseImage.getSubimage(x, y, width, height);
			outImg = rotate(outImg, degrees);
			imageicon = new ImageIcon(outImg);
			imageicon = new ImageIcon(imageicon.getImage().getScaledInstance((int)(imageicon.getIconWidth() * ratio), (int)(imageicon.getIconHeight() * ratio), Image.SCALE_FAST));
		} catch (Exception e) {
			System.err.println("Error processing subimage");
			e.printStackTrace();
		}
		return imageicon;
	}

}