package project.robot;

import java.awt.Point;
import java.util.ArrayList;
import project.datastructure.Map;
import project.pathfinding.a_star.AStar;
import project.pathfinding.a_star.AreaMap;

/**
 * Class containing helper methods for GripperBotF
 * 
 * @author konstantin
 * 
 */
public class GripperBotHelper {
	protected final static int PRECISION = 100;

	/**
	 * Method which rounds given yaw to specified Precision
	 * 
	 * @param yaw
	 *            the yaw to be rounded
	 * @return the rounded yaw
	 */
	public static long roundedYaw(double yaw) {
		return (Math.round(yaw * PRECISION));
	}

	/**
	 * 
	 * @param target_x
	 * @param target_y
	 * @return The rounded yaw to a target position
	 */
	public static double getYawToPoint(double curX, double curY,
			double target_x, double target_y) {
		return Math.atan2(target_y - curY, target_x - curX);
	}

	/**
	 * 
	 * @param curX
	 * @param curY
	 * @param goalX
	 * @param goalY
	 * @param map
	 * @return
	 */
	public static ArrayList<Point> getPath(int curX, int curY, int goalX,
			int goalY, AreaMap map) {
		AStar aStar = new AStar(map);
		ArrayList<Point> shortestPath = aStar.calcShortestPath(curY, curX,
				goalY, goalX);
		return shortestPath;
	}

	/**
	 * Method which computes the distance to a given point from robots current
	 * location.
	 * 
	 * @param xCur
	 * @param yCur
	 * @param xEnd
	 * @param yEnd
	 * @return The distance to point with coordinates x,y from current
	 *         position.
	 */
	public static double distanceTo(double xCur, double yCur, double xEnd,
			double yEnd) {
		return Math.sqrt(Math.pow(xEnd - xCur, 2) + Math.pow(yEnd - yCur, 2));
	}
}
