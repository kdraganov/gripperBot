 package project.outputImage;

import java.awt.Color;

public class CellColourChooser {

	public Color chooseColour(int value) {

		switch (value) {
		case 1:
			return Color.WHITE;
		case 2:
			return Color.BLACK;
		case 3:
			return Color.ORANGE;
		case 4:
			return Color.BLUE;
		case 5:
			return Color.GREEN;
		case 6:
			return Color.red;
		default:
			return Color.GRAY;
		}
	}
}
