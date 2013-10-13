package project.robot;
/**
 * Class that represents the constants that the gripper bot uses
 * @author k1188883
 *
 */
public class GripperBotConstants {

	public static final double[] SENSORS_HEADINGS = { Math.toRadians(0),
			Math.toRadians(7), Math.toRadians(15), Math.toRadians(22),
			Math.toRadians(30), Math.toRadians(60), Math.toRadians(90),
			Math.toRadians(135), Math.toRadians(180), Math.toRadians(225),
			Math.toRadians(270), Math.toRadians(300), Math.toRadians(330),
			Math.toRadians(338), Math.toRadians(345), Math.toRadians(353) };
	public static final int[] SENSORS = { 0, 1, 2, 3, 4, 12, 13, 14, 15 };
	public static final double RANGE_OFFSET =0.22;
	public static final double MOVE_SPEED = 0.45;
}
