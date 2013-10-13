package project.outputImage;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import project.datastructure.Map;

/**
 * 
 * @author philip
 * 
 */
public class MapToBmp {
	private static int IMAGE_WIDTH;
	private static int IMAGE_HEIGHT;
	private static Map map;
	private static BufferedImage image;
	private final static int temp = 2;

	/**
	 * 
	 * @param map
	 * @param file
	 */
	public MapToBmp(Map map, File file) {

	}

	/**
	 * 
	 * @param map
	 * @param absolutePath
	 * @param name
	 */
	public MapToBmp(Map map, String absolutePath, String name) {
		MapToBmp.map = map;
		initializeImage();
		drawImage();
		File temp = new File(absolutePath + "//" + name);
		try {
			ImageIO.write(image, "BMP", temp);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(new JLabel(),
					"Error printing out the map", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void initializeImage() {
		IMAGE_HEIGHT = map.getHeight() * temp;
		IMAGE_WIDTH = map.getWidth() * temp;
		image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT,
				BufferedImage.TYPE_INT_RGB);

	}

	private static void drawImage() {

		Graphics g = image.getGraphics();
		CellColourChooser chooser = new CellColourChooser();

		for (int i = 0; i < map.getWidth(); i++) {
			for (int j = 0; j < map.getHeight(); j++) {
				g.setColor(chooser.chooseColour(map.getValue(j, i)));
				g.fillRect(i * temp, j * temp, 1 * temp, 1 * temp);

			}
		}

	}

}