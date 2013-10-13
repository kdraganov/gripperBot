package project.datastructure;

import java.awt.Point;
import java.util.ArrayList;

/**
 * Class that represents the map model
 * @author Team Engelbart
 * 
 */
public class Map {

	private Cell[][] map;
	private int centreRow;
	private int centreColumn;
	private final double CELL_SIZE; // Meters squared per cell of this map.
	private final int WIDTH;
	private final int HEIGHT;
	private final int ROBOTACTUALWIDTH;
	private boolean isExplored;
	private ArrayList<Point> garbageObjects;
	private ArrayList<Point> robots;

	/**
	 * 
	 * @param double cellSize
	 * @param int width
	 * @param int height
	 */
	public Map(double cellSize, int width, int height) {
		this.CELL_SIZE = cellSize;
		int tempWidth = (int) (width / this.CELL_SIZE);

		if (tempWidth % 2 == 0) {
			this.WIDTH = tempWidth + 1;
		} else {
			this.WIDTH = tempWidth;
		}

		int tempHeight = (int) (height / this.CELL_SIZE);
		if (tempHeight % 2 == 0) {
			this.HEIGHT = tempHeight + 1;
		} else {
			this.HEIGHT = tempHeight;
		}

		this.map = new Cell[this.HEIGHT][this.WIDTH];
		for (int y = 0; y < this.WIDTH; y++) {
			for (int x = 0; x < this.HEIGHT; x++) {
				map[x][y] = new Cell(Cell.UNEXPLORED);
			}

		}
		this.centreRow = (int) Math.floor(this.HEIGHT / 2);
		this.centreColumn = (int) Math.floor(this.WIDTH / 2);
		this.ROBOTACTUALWIDTH = (int) Math.ceil(0.5 / this.CELL_SIZE);
		this.isExplored = false;
		this.garbageObjects = new ArrayList<Point>();
		this.robots = new ArrayList<Point>(3);
	}

	/**
	 * 
	 * @param row
	 * @param column
	 * @param value
	 */
	public void setValue(int row, int column, int value) {
		this.map[row][column].setCellValue(value);
	}

	/**
	 * 
	 * @return int
	 */
	public int getCentreColumn() {
		return this.centreColumn;
	}

	/**
	 * 
	 * @return int
	 */
	public int getCentreRow() {
		return this.centreRow;
	}

	/**
	 * 
	 * @param x
	 *            int row
	 * @param y
	 *            int coulumn
	 * @return int occupancy value of a cell
	 */
	public int getValue(int row, int column) {
		if (row < 0 || row >= this.HEIGHT) {
			return -1;
		} else if (column < 0 || column >= this.WIDTH) {
			return -1;
		} else {
			return this.map[row][column].getCellValue();
		}
	}

	/**
	 * 
	 * @return double the size of the cell
	 */
	public double getCellSize() {
		return this.CELL_SIZE;
	}

	/**
	 * 
	 * @return int the number of columns
	 */
	public int getWidth() {
		return this.WIDTH;
	}

	/**
	 * 
	 * @return int the number of rows
	 */
	public int getHeight() {
		return this.HEIGHT;
	}

	/**
	 * 
	 * @return Cell[] []
	 */
	public Cell[][] getMap() {
		return this.map;
	}

	public ArrayList<Point> getGarbageObjects() {
		return this.garbageObjects;
	}

	public ArrayList<Point> getRobots() {
		return this.robots;
	}

	public void addGarbageObject(double xPos, double yPos) {
		Point object = new Point();
		xPos = Math.round(xPos * 5);
		yPos = Math.round(yPos * 5);
		object.setLocation(xPos, yPos);
		if (!this.garbageObjects.contains(object)) {
			this.garbageObjects.add(object);
		}
	}

	/**
	 * Fill all cells intersected by the line specified by the points
	 * 
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @param value
	 */
	public void fillCells(double startX, double startY, double endX,
			double endY, int value) {
		Point p1 = new Point((int) toColumn(startX), (int) toRow(startY));
		Point p2 = new Point((int) toColumn(endX), (int) toRow(endY));
		ArrayList<Point> cells = MapHelper.getCellsOnLine(p1, p2);
		for (int i = 0; i < cells.size(); i++) {
			if (this.map[cells.get(i).y][cells.get(i).x].getCellValue() < Cell.GARBAGE) {
				try {
					this.map[cells.get(i).y][cells.get(i).x]
							.setCellValue(value);
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("Robot has gone missing!!!");
					System.exit(0);
				}
			}
		}
	}

	public int toRow(double y) {
		double rowCell = y / this.CELL_SIZE;
		return (int) (this.centreRow + (rowCell * -1));

	}

	public int toColumn(double x) {
		double columnCell = x / this.CELL_SIZE;
		return (int) (this.centreColumn + columnCell);

	}

	public double toY(int y) {
		double row;
		if (y > this.centreRow) {
			row = (y - this.centreRow) * -1;
		} else {
			row = (this.centreRow - y);
		}
		return row * this.CELL_SIZE;
	}

	public double toX(int x) {
		double column = (x - this.centreColumn) * this.CELL_SIZE;
		return column;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean canRobotPass(int x, int y) {
		for (int column = x - ROBOTACTUALWIDTH; column <= x + ROBOTACTUALWIDTH; column++) {
			for (int row = y - ROBOTACTUALWIDTH; row <= y + ROBOTACTUALWIDTH; row++) {
				if (this.getValue(row, column) == Cell.OBSTACLE) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param botId
	 * @param xCurrent
	 * @param yCurrent
	 */
	public void updateRobotPosition(int botId, double xCurrent, double yCurrent) {
		try {
			this.robots.get(botId).setLocation(toRow(yCurrent),
					toColumn(xCurrent));
		} catch (IndexOutOfBoundsException e) {
			this.robots.add(botId, new Point(toRow(yCurrent),
					toColumn(xCurrent)));
		}
	}

	public boolean isCloseToWall(int row, int column, double bufferZone) {
		int wallBound = (int) Math.ceil(bufferZone / this.CELL_SIZE);
		for (int r = row - wallBound; r < row + wallBound; r++) {
			for (int col = column - wallBound; col < column
					+ wallBound; col++) {
				if (this.getValue(r, col) == Cell.OBSTACLE) {
					return true;
				}
			}
		}
		return false;
	}

	public void setExplored() {
		this.isExplored = true;
	}

	public boolean isExplored() {
		return this.isExplored;
	}

	public boolean isGarbageCollected(){
		return this.garbageObjects.isEmpty();
	}
	public Point getNextgarbage(){
		return this.garbageObjects.remove(0);
	}
}
