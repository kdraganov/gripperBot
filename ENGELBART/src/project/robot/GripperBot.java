package project.robot;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import javaclient3.FiducialInterface;
import javaclient3.GripperInterface;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.fiducial.PlayerFiducialItem;
import project.datastructure.Cell;
import project.datastructure.Map;
import project.pathfinding.a_star.AreaMap;
import project.pathfinding.bfs.BreadthFirstSearch;
import project.pathfinding.bfs.Node;

/**
 * Class responsible for controlling the robot
 * @author konstantin
 * 
 */
public class GripperBot {

	/**
	 * The Player proxy object.
	 */
	private PlayerClient robot = null;

	/**
	 * The position2d interface.
	 */
	private Position2DInterface pos2D = null;

	/**
	 * The rangerinterface.
	 */
	private RangerInterface sonar = null;
	/**
	 * The FiducialInterface.
	 */
	private FiducialInterface fiducial = null;
	/**
	 * THe Gripper Interface
	 */
	private GripperInterface gripper = null;
	private double speed = 0;
	private double turnRate = 0;
	private Map map = null;
	private double yaw;
	private double x_current_position;
	private double y_current_position;
	private double[] sensorValues;
	private PlayerFiducialItem[] fiducialObjects;
	private boolean frontObstacle = false;
	private boolean leftObstacle = false;
	private boolean rightObstacle = false;
	private final double OBSTACLE_BOUNCE = 1.2;
	private boolean isStuck = false;
	private int botId;
	private double maxFrontRight, maxFrontLeft;

	public GripperBot(int botId, Map map) {
		try {
			this.robot = new PlayerClient("localhost", 6665);
			this.pos2D = robot.requestInterfacePosition2D(botId,
					PlayerConstants.PLAYER_OPEN_MODE);
			this.sonar = robot.requestInterfaceRanger(botId,
					PlayerConstants.PLAYER_OPEN_MODE);
			this.fiducial = robot.requestInterfaceFiducial(botId,
					PlayerConstants.PLAYER_OPEN_MODE);
			this.gripper = robot.requestInterfaceGripper(botId,
					PlayerConstants.PLAYER_OPEN_MODE);
		} catch (PlayerException e) {
			System.err.println("BaseRobot: Error connecting to Player!\n>>>"
					+ e.toString());
			System.exit(1);
		}
		this.map = map;
		robot.runThreaded(-1, -1);
		this.botId = botId;
	}

	public void startRobotSensors() {
		this.getReadings();
		this.setRobotMovement();
		this.obstacleDetection();
		this.stallTread();
		this.stuckThread();
	}

	/**
	 * Thread that gets the robot coordinates and heading
	 */
	private void getReadings() {
		Thread collectData = new Thread() {
			public void run() {
				while (true) {
					while (!sonar.isDataReady() && !pos2D.isDataReady()
							&& !fiducial.isDataReady())
						;
					sensorValues = sonar.getData().getRanges();
					try {
						yaw = pos2D.getYaw();
						x_current_position = pos2D.getX();
						y_current_position = pos2D.getY();
					} catch (NullPointerException e) {
						System.out.println("Player returned null value");
					}
					fiducialObjects = fiducial.getData().getFiducials();
					map.updateRobotPosition(botId, x_current_position,
							y_current_position);
					try {
						sleep(55);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		collectData.start();
	}

	public void stallTread() {
		Thread stall = new Thread() {
			public void run() {
				while (true) {
					// TODO: handle exception
					while (!pos2D.isDataReady())
						;
					if (pos2D.getData().getStall() == 1) {
						isStuck = true;
					} else {
						isStuck = false;
					}
					try {
						sleep(300);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		stall.start();
	}

	public void stuckThread() {
		Thread stall = new Thread() {
			public void run() {
				while (true) {
					if (isStuck) {
						try {
							sleep(15000);
						} catch (InterruptedException e) {
						}
					}
					if (isStuck) {
						if (sensorValues[0] > 2.5 && sensorValues[1] > 2.5
								&& sensorValues[15] > 2.5) {
							speed = GripperBotConstants.MOVE_SPEED;
						} else if (sensorValues[8] > 2.5) {
							speed = -1 * GripperBotConstants.MOVE_SPEED;
						}
						try {
							sleep(2000);
						} catch (Exception e) {
						}
					}
					try {
						sleep(300);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		stall.start();
	}

	/**
	 * Method for picking up a given garbage object
	 * 
	 * @param garbage
	 */
	public void collectgarbage(final double x1, final double y1,
			final double x2, final double y2) {
		Thread collect = new Thread() {
			public void run() {
				ArrayList<Point> path;
				while (!map.isGarbageCollected()) {
					Point temp = map.getNextgarbage();
					try{
					path = GripperBotHelper.getPath(map.toRow(y_current_position),map.toColumn(x_current_position),map.toRow(temp.getY()/5), map.toColumn(temp.getX()/5),  new AreaMap(map));
					followPath(path);
					turnTo(GripperBotHelper.getYawToPoint(x_current_position, y_current_position, temp.getX()/5, temp.getY()/5));
					pickGarbage();
					path = GripperBotHelper.getPath(map.toRow(y_current_position),map.toColumn(x_current_position),map.toRow((int) (y1 + y2) / 2),map.toColumn((int) (x1 + x2) / 2),  new AreaMap(map));
					followPath(path);
					}catch(Exception e){}
					gripper.open();
				}
			}
		};
		collect.start();
	}

	private void pickGarbage() {
		if (fiducialObjects.length > 0) {
			int i = 0;
			boolean isGarbage = false;
			while (i < fiducialObjects.length && !isGarbage) {
				if (fiducialObjects[i].getId() > 1) {
					isGarbage = true;
				} else {
					i++;
				}
			}
			double pX = fiducialObjects[i].getPose().getPx();
			double pY = fiducialObjects[i].getPose().getPy();
			double hypotenuse = Math.sqrt(Math.pow(pX, 2)
					+ Math.pow(Math.abs(pY), 2));
			double angle = Math.asin(Math.abs(pY) / hypotenuse)
					* (180 / Math.PI);
			double heading;
			if (pY < 0) {
				heading = yaw - Math.toRadians(angle);
			} else {
				heading = yaw + Math.toRadians(angle);
			}
			double cosOffset = Math.cos(heading);
			double sinOffset = Math.sin(heading);
			double xPos = x_current_position + (cosOffset * (hypotenuse - 0.2));
			double yPos = y_current_position + (sinOffset * (hypotenuse - 0.2));
			moveTo(xPos, yPos);
			gripper.close();
		}
	}

	private void followPath(ArrayList<Point> pathPoints) {
		for (int i = 0; i < pathPoints.size() - 15; i++) {
			map.setValue(pathPoints.get(i).y, pathPoints.get(i).x, 5);
		}
		while (pathPoints.size() > 15 && !isStuck) {
			Point temp = pathPoints.remove(0);
			double targetX = map.toX(temp.x);
			double targetY = map.toY(temp.y);
			moveTo(targetX, targetY);
			map.setValue(temp.y, temp.x, Cell.FREE_SPACE);
		}
	}

	/**
	 * Thread that sets the robot speed and turnRate
	 */
	private void setRobotMovement() {
		Thread speedSet = new Thread() {
			public void run() {
				while (true) {
					pos2D.setSpeed(speed, turnRate);
					try {
						sleep(5);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		speedSet.start();
	}

	private void obstacleDetection() {
		Thread obstacleAvoidance = new Thread() {
			public void run() {
				while (true) {
					while (!sonar.isDataReady())
						;
					double minFront = OBSTACLE_BOUNCE;
					try {
						minFront = Math.min(sensorValues[0], Math.min(
								sensorValues[1], sensorValues[14]));
					} catch (Exception e) {
					}

					if (minFront < OBSTACLE_BOUNCE) {
						frontObstacle = true;
						speed = 0;
					} else {
						frontObstacle = false;
					}

					maxFrontRight = Math.max(sensorValues[2], Math.max(
							sensorValues[3], sensorValues[4]));
					maxFrontLeft = Math.max(sensorValues[14], Math.max(
							sensorValues[13], sensorValues[12]));

					double minFrontSide = Math.min(sensorValues[2], Math.min(
							sensorValues[3], Math.min(sensorValues[4],
									Math
											.min(sensorValues[12], Math.min(
													sensorValues[13],
													sensorValues[14])))));
					if ((sensorValues[10] < OBSTACLE_BOUNCE || sensorValues[11] < OBSTACLE_BOUNCE)) {
						leftObstacle = true;
					} else {
						leftObstacle = false;
					}

					if ((sensorValues[5] < OBSTACLE_BOUNCE || sensorValues[6] < OBSTACLE_BOUNCE)) {
						rightObstacle = true;
					} else {
						rightObstacle = false;
					}
					try {
						sleep(25);
					} catch (InterruptedException e) {
					}
				}
			}

		};
		obstacleAvoidance.start();
	}

	public void locateGarbage() {
		Thread temp = new Thread() {
			public void run() {
				while (!map.isExplored()) {
					if (fiducialObjects.length > 0) {
						for (int i = 0; i < fiducialObjects.length; i++) {
							try {
								if (fiducialObjects[i].getId() != 1) {
									addGarbage(calculateObjectPosition(
											fiducialObjects[i].getPose()
													.getPx(),
											fiducialObjects[i].getPose()
													.getPy(),
											x_current_position,
											y_current_position));
								}
							} catch (ArrayIndexOutOfBoundsException e) {
							}
						}
					}
					try {
						sleep(250);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		temp.start();
	}

	private void addGarbage(Point point) {
		map.addGarbageObject(point.getX(), point.getY());
	}

	private Point calculateObjectPosition(double pX, double pY, double curX,
			double curY) {
		double hypotenuse = Math.sqrt(Math.pow(pX, 2)
				+ Math.pow(Math.abs(pY), 2));
		double angle = Math.asin(Math.abs(pY) / hypotenuse) * (180 / Math.PI);
		double heading;
		if (pY < 0) {
			heading = yaw - Math.toRadians(angle);
		} else {
			heading = yaw + Math.toRadians(angle);
		}
		double cosOffset = Math.cos(heading);
		double sinOffset = Math.sin(heading);
		double xPos = curX + (cosOffset * (hypotenuse));
		double yPos = curY + (sinOffset * (hypotenuse));
		Point temp = new Point();
		temp.setLocation(xPos, yPos);
		return temp;
	}

	/**
	 * Method for controlling the robot to explorer the complete accessible area
	 */
	public void explore() {
		Thread temp = new Thread() {
			public void run() {
				while (!map.isExplored()) {
					for (int i = 0; i < GripperBotConstants.SENSORS.length; i++) {
						sensorDataTranslation(
								x_current_position,
								y_current_position,
								sensorValues[GripperBotConstants.SENSORS[i]],
								GripperBotConstants.SENSORS_HEADINGS[GripperBotConstants.SENSORS[i]]);
					}
					try {
						sleep(300);
					} catch (Exception e) {
					}
				}
				map.setExplored();
			}
		};
		temp.start();
		this.locateGarbage();
		navigate();

	}

	public void navigate() {
		Thread nav = new Thread() {
			public void run() {
				speed = GripperBotConstants.MOVE_SPEED;
				while (!frontObstacle) {
					try {
						sleep(5);
					} catch (Exception e) {
					}
				}
				speed = 0;
				while (!map.isExplored()) {
					if (!isStuck) {
						BreadthFirstSearch bfs = new BreadthFirstSearch(map);
						Node node = new Node(map.toRow(y_current_position), map
								.toColumn(x_current_position));
						if (map.isCloseToWall(map.toRow(y_current_position),
								map.toColumn(x_current_position), 0.25)) {
							if (sensorValues[0] > 2.5 && sensorValues[1] > 2.5
									&& sensorValues[15] > 2.5) {
								speed = GripperBotConstants.MOVE_SPEED;
							} else if (sensorValues[8] > 2.5) {
								speed = -1 * GripperBotConstants.MOVE_SPEED;
							}
							try {
								sleep(2000);
							} catch (Exception e) {
							}
						}
						if (bfs.isExplored(node)) {
							followPath(bfs.getPath());
						} else {
							map.setExplored();
						}
					} else {
						double minLeft = Math.min(sensorValues[12], Math.min(
								sensorValues[11], sensorValues[10]));
						double minRight = Math.min(sensorValues[4], Math.min(
								sensorValues[5], sensorValues[6]));
						int direction = -1;
						if (minLeft < minRight) {
							direction = 1;
						}
						turnRate = direction * 0.2;
						try {
							sleep(55);
						} catch (Exception e) {
						}
						speed = 0.35;
						try {
							sleep(4000);
						} catch (Exception e) {
						}
						speed = 0;
						turnRate = 0;
					}
				}

			}
		};
		nav.start();
	}

	private void followPath(LinkedList<Node> pathPoints) {
		for (int i = 0; i < pathPoints.size() - 1; i++) {
			map.setValue(pathPoints.get(i).getRow(), pathPoints.get(i)
					.getColumn(), 5);
		}
		boolean seen = false;
		while (!pathPoints.isEmpty() && !seen && !isStuck) {
			Node temp = pathPoints.getLast();
			if (map.getValue(temp.getRow(), temp.getColumn()) != Cell.UNEXPLORED
					&& GripperBotHelper.distanceTo(x_current_position,
							y_current_position, map.toX(temp.getColumn()), map
									.toY(temp.getRow())) < 2.5) {
				seen = true;
			}
			temp = pathPoints.remove(0);
			double targetX = map.toX(temp.getColumn());
			double targetY = map.toY(temp.getRow());
			if (GripperBotHelper.distanceTo(x_current_position,
					y_current_position, targetX, targetY) > 0.5
					|| pathPoints.isEmpty()) {
				moveTo(targetX, targetY);
			}
			map.setValue(temp.getRow(), temp.getColumn(), Cell.FREE_SPACE);
		}
		if (!pathPoints.isEmpty()) {
			if (map.getValue(pathPoints.getLast().getRow(), pathPoints
						.getLast().getColumn()) == Cell.UNEXPLORED) {
					map.setValue(pathPoints.getLast().getRow(), pathPoints
							.getLast().getColumn(), Cell.OBSTACLE);
				}
		}
		for (int i = 0; i < pathPoints.size() - 1; i++) {
			map.setValue(pathPoints.get(i).getRow(), pathPoints.get(i)
					.getColumn(), Cell.FREE_SPACE);
		}
	}

	private void moveTo(double x, double y) {
		turnTo(GripperBotHelper.getYawToPoint(x_current_position,
				y_current_position, x, y));
		speed = GripperBotConstants.MOVE_SPEED;
		double prevDistance = Math.floor(GripperBotHelper.distanceTo(
				x_current_position, y_current_position, x, y) * 10);
		double curDistance = Math.floor(GripperBotHelper.distanceTo(
				x_current_position, y_current_position, x, y) * 10);
		while ((curDistance != 0 || prevDistance < curDistance)
				&& (!frontObstacle && !isStuck)) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
			}
			prevDistance = curDistance;
			curDistance = Math.floor(GripperBotHelper.distanceTo(
					x_current_position, y_current_position, x, y) * 10);
		}
		speed = 0;
	}

	/**
	 * Method that cause the robot to turn to the given direction
	 * 
	 * @param direction
	 */
	public void turnTo(final double direction) {
		long targetYaw = GripperBotHelper.roundedYaw(direction);
		if (targetYaw == GripperBotHelper.roundedYaw(yaw)) {
			return;
		}
		double turnDirection = 1;
		double yawDegrees = Math.toDegrees(yaw);
		double directionDegrees = Math.toDegrees(direction);
		if (directionDegrees < yawDegrees
				&& directionDegrees > Math.toDegrees(yaw - Math.toRadians(180))
				|| (directionDegrees > yawDegrees && directionDegrees > Math
						.toDegrees(yaw + Math.toRadians(180)))) {
			turnDirection = -1;
		}
		long difference = GripperBotHelper.roundedYaw(yaw) - targetYaw;
		difference = Math.abs(difference);
		while (Math.floor(difference) > 0 && !isStuck) {
			if (pos2D.isDataReady()) {
				difference = Math.abs(GripperBotHelper.roundedYaw(pos2D
						.getYaw())
						- targetYaw);
				speed = 0;
				if (difference < 15) {
					turnRate = turnDirection * difference / 150;
				} else {
					turnRate = turnDirection * 0.15;
				}
			}
		}
		turnRate = 0;

	}

	/*
	 * Method that takes the coordinates of a sensor and the free range in front
	 * and translates this data to the map representation model
	 */
	private void sensorDataTranslation(double x, double y, double range,
			double offset) {
		double cosOffset = Math.cos(yaw + offset);
		double sinOffset = Math.sin(yaw + offset);
		double endFrontX = x
				+ (cosOffset * (range - GripperBotConstants.RANGE_OFFSET));
		double endFrontY = y
				+ (sinOffset * (range - GripperBotConstants.RANGE_OFFSET));
		map.fillCells(x, y, endFrontX, endFrontY, Cell.FREE_SPACE);
		if (range < 5) {
			double endX = endFrontX + (cosOffset * 0.3);
			double endY = endFrontY + (sinOffset * 0.3);
			map.fillCells(endFrontX, endFrontY, endX, endY, Cell.OBSTACLE);
		}

	}

}