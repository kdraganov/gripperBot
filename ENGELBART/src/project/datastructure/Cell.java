package project.datastructure;

/**
 * Cell representing an occupancy value
 * 
 * @author konstantin
 * 
 */
public class Cell {

	public static final int UNEXPLORED = 0;
	public static final int FREE_SPACE = 1;
	public static final int OBSTACLE = 2;
	public static final int GARBAGE = 3;
	public static final int ROBOT = 4;
	private int cellValue;

	/**
	 * Default constructor
	 */
	public Cell() {
		this.cellValue = Cell.UNEXPLORED;
	}

	/**
	 * Constructor
	 * 
	 * @param value
	 *            occupancy value
	 */
	public Cell(int value) {
		this.cellValue = value;
	}

	/**
	 * 
	 * @return int the occupancy value
	 */
	public int getCellValue() {
		return this.cellValue;
	}

	/**
	 * Set the occupancy value of a cell
	 * 
	 * @param value
	 *            the occupancy value
	 */
	public void setCellValue(int value) {
		this.cellValue = value;
	}

}
