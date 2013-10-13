package project.datastructure;

import java.awt.Point;
import java.util.ArrayList;

/**
 * 
 * @author konstantin
 * 
 */
public class MapHelper {

	public static ArrayList<Point> getCellsOnLine(Point p1, Point p2) {
		ArrayList<Point> cells = new ArrayList<Point>();
		double m = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
		double b = p1.getY() - (m * p1.getX());
		double min, max, step;
		boolean isVertical = Math.round((p2.getX() - p1.getX()) * 100000000) == 0;
		if (isVertical) {
			step = 1;
			min = Math.min(p1.getY(), p2.getY());
			max = Math.max(p1.getY(), p2.getY());
		} else {
			step = 1 / (Math.abs(m) + 20);
			min = Math.min(p1.getX(), p2.getX());
			max = Math.max(p1.getX(), p2.getX());
		}
		for (double x = min; x < max; x = x + step) {
			Point temp = new Point();
			if (isVertical) {
				temp.y = (int) x;
				temp.x = (int) p1.getX();
			} else {
				double y = (m * x) + b;
				temp.y = (int) y;
				temp.x = (int) x;
			}
			if (!cells.contains(temp)) {
				cells.add(temp);
			}
		}
		return cells;
	}

}
