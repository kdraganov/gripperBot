package project.gui.mapping;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import project.datastructure.Cell;
import project.datastructure.Map;
import project.outputImage.CellColourChooser;

/**
 * This class actually draws the map and is updated by the GuiMapDisplay class.
 * An instance of this class should not be created externally.
 * 
 * @author philip
 * 
 */
public class GuiMap extends JPanel implements MouseWheelListener {

	private boolean isReady;
	public static Map map;
	private static Dimension size;
	private int scale = 1;
	private int heightIncrement;
	private int widthIncrement;

	public GuiMap(Map map2) {
		size = new Dimension(map2.getHeight(), map2.getWidth());
		heightIncrement = map2.getHeight();
		widthIncrement = map2.getWidth();
		map = map2;
	}

	@Override
	// TODO Might have to make this public to work? Test it?
	protected void paintComponent(Graphics g) {
		if (isReady) {
			super.paintComponent(g);
			CellColourChooser chooser = new CellColourChooser();
			for (int j = 0; j < map.getHeight(); j++) {
				for (int i = 0; i < map.getWidth(); i++) {
					g.setColor(chooser.chooseColour(map.getValue(j, i)));
					g.fillRect(i * scale, j * scale, 1 * scale, 1 * scale);

				}
			}
			ArrayList<Point> garbages=map.getGarbageObjects();
			for(int i=0;i<garbages.size();i++){
				Point temp=garbages.get(i);
				g.setColor(chooser.chooseColour(Cell.GARBAGE));
				g.fillRect(map.toColumn(temp.getX()/5)*scale-1, map.toRow(temp.getY()/5)*scale-1, 1*scale*3, 1*scale*3);
			}
			ArrayList<Point> robots=map.getRobots();
			for(int i=0;i<robots.size();i++){
				Point temp=robots.get(i);
				g.setColor(chooser.chooseColour(Cell.ROBOT));
				g.fillOval((int)temp.getY()*scale-8,(int)temp.getX()*scale-8, 1*scale*8, 1*scale*8);
			}
		}

	}

	public void isReady(boolean isReady) {
		this.isReady = isReady;
	}

	public void updateMap(Map map) {
		this.map = map;
		this.repaint();
	}

	public void raiseScale() {

		if (scale + 1 > 32) {
		} else {
			scale++;
			size = new Dimension((int) size.getWidth() + widthIncrement,
					(int) size.getHeight() + heightIncrement);
			setPreferredSize(size);
		}
	}

	public void lowerScale() {

		if (scale - 1 < 1) {
		} else {
			scale--;
			size = new Dimension((int) size.getWidth() - widthIncrement,
					(int) size.getHeight() - heightIncrement);
			setPreferredSize(size);
		}

	}

	/**
	 * 
	 */
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		switch (arg0.getWheelRotation()) {
		case -1:

			raiseScale();
			revalidate();
			repaint();
			break;

		default:
			lowerScale();
			revalidate();
			repaint();
			break;
		}

	}
}
