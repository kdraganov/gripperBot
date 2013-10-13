package project.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import project.datastructure.Map;
import project.gui.GuiMain;
import project.gui.MapDisplay;
import project.outputImage.MapToBmp;
import project.robot.GripperBot;
import project.outputImage.CellColourChooser;
/**
 * @author konstantin
 * 
 */
public class MainApp {

	/**
	 * @param args
	 */

	public static void main(String[] args) {
		if (args.length <= 0) {
			System.out.println("Missing arguments! Try -help!");
			System.out.println("Program will now terminate.");
			System.exit(1);
		} else {
			if (args[0].equals("-gui")) {
				Map map = new Map(0.1, 70, 70);
				GripperBot bot1 = new GripperBot(0, map);
				GuiMain gui = new GuiMain(map, bot1);
				while (true) {
					gui.updateMap(map);
					try {
						Thread.sleep(250);
					} catch (Exception e) {
					}
				}

			} else if (args[0].equals("-help")) {
				System.out
						.println("-explore - causes the robot to explore the environment.\n");
				System.out
						.println("-gui - starts a Graphical User Interface (GUI).\n");
				System.out
						.println("-solo - only one robot is used for completing the tast.\n");
				System.out
						.println("-multi - Currently only solo mode supported.\n Multi argument can be still used, but will not cause any change.\n");
				System.out
						.println("-collect x1 y1 x2 y2  - causes the robot to collect all garbage object found in the map.\n"
								+ " The command need to be followed by four valid number arguments denoting the area where the objects to be collected. Where:\n"
								+ "-> x1 y1 denotes the North-West corner (top-left)\n"
								+ "-> x1 y2 denotes the South-West corner (bottom-left)\n"
								+ "-> x2 y1 denotes the North-East corner (top-right)\n"
								+ "-> x2 y2 denotes the South-East corner (bottom-right).\n");
				System.out
						.println("-map [filename] - if no filename supplied a default value [output.bmp] would be used.\n");
				System.out
						.println("All right reserved!\nProperty of TEAM ENGELBART 2013");
			} else {
				for (int i = 0; i < args.length; i++) {
					if (!args[i].equals("-explore") && !args[i].equals("-solo")
							&& !args[i].equals("-multi")
							&& !args[i].equals("-map")
							&& !args[i].equals("-collect")
							&& !checkFileName(args[i]) && !checkArgs(args[i])) {
						System.out.println("Invalid arguments! Try -help!!");
						System.out.println("Program will now terminate.");
						System.exit(1);
					}
				}
				Map map = new Map(0.1, 70, 70);
				GripperBot bot1 = new GripperBot(0, map);
				try {
					Thread.sleep(3500);
				} catch (Exception e) {
				}
				bot1.startRobotSensors();
				try {
					Thread.sleep(3500);
				} catch (Exception e) {
				}
				for (int i = 0; i < args.length; i++) {
					MapDisplay gui;
					if (args[i].equals("-explore")) {
						bot1.explore();
						gui = new MapDisplay(map);
						while (!map.isExplored()) {
							gui.updateMap(map);
							try {
								Thread.sleep(250);
							} catch (Exception e) {
							}
						}
					} else if (args[i].equals("-collect")) {
						if (args.length > i+4) {
							for (int l=i; l<i+5; l++) {
								checkArgs(args[l]);
							}
						}else{
							System.out.println("Invalid collection area! Try -help!");
							System.out.println("Program will now terminate.");
							System.exit(1);
						}
						gui = new MapDisplay(map);
						if (!map.isExplored()) {
							bot1.explore();
							while (!map.isExplored()) {
								gui.updateMap(map);
								try {
									Thread.sleep(250);
								} catch (Exception e) {
								}
							}
						}
						bot1.collectgarbage(Double.parseDouble(args[i+1]), Double.parseDouble(args[i+2]), Double.parseDouble(args[i+3]), Double.parseDouble(args[i+4]));
						while (!map.isGarbageCollected()) {
							gui.updateMap(map);
							try {
								Thread.sleep(250);
							} catch (Exception e) {
							}
						}

					}

					else if (args[i].equals("-map")) {
						String name = "output.bmp";
						try{
							if (checkFileName(args[i + 1])) {
								name = args[i + 1] + ".bmp";
							}
						}catch(Exception e){}
						new MapToBmp(map, System.getProperty("user.dir"), name);
					}
				}

			}

		}

	}

	private static boolean checkFileName(String string) {
		Pattern patt = Pattern.compile("[_a-zA-Z0-9\\-\\.]*");
		Matcher matcher = patt.matcher(string);
		return matcher.matches();
	}

	private static boolean checkRange(double parseDouble) {

		if (parseDouble >= -30 && parseDouble <= 30) {

			return true;
		}

		return false;
	}

	private static boolean checkArgs(String arg) {

		try {
			checkRange(Double.parseDouble(arg));
		} catch (Exception e) {
			System.out.println("Invalid collection area! Try -help!");
			System.out.println("Program will now terminate.");
			System.exit(1);
		}
		return true;

	}
}