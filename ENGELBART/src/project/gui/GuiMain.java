package project.gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import project.datastructure.Map;
import project.gui.mapping.GuiMap;
import project.outputImage.MapToBmp;
import project.robot.GripperBot;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * @author Team ENGELBART
 */
@SuppressWarnings("serial")
public class GuiMain extends JFrame implements MouseWheelListener {
	private GuiMap myMap;
	private JButton savemapButton, collectButton, exploreButton;
	private static JTextField x1TestField, x2TestField, y1TestField,
			y2TestField;
	private JLabel x1Label, x2Label, y1Label, y2Label, statusbar;
	private JRadioButton solo, multi;
	private static boolean inProgress = false;
	private GripperBot bot1;
	private boolean exploring=false;
	private boolean collecting=false;
	private Map map;

	/**
	 * 
	 * @param map
	 * @param bot1
	 */
	public GuiMain(Map map, GripperBot bot1) {
		this.setTitle("Gripper BOT Control Software Tool");
		this.map=map;
		this.myMap = new GuiMap(map);
		this.myMap.isReady(true);
		this.bot1 = bot1;
		updateMap(map);
		createComponents();
		initializeFrame();
		operatingThread();
	}

	private void initializeFrame() {
		this.setSize(900, 600);
		this.setResizable(true);
		this.setMinimumSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		bot1.startRobotSensors();
	}

	private void createComponents() {
		this.savemapButton = new JButton("Save Map");
		this.savemapButton.setActionCommand("saveMap");
		this.collectButton = new JButton("Collect Garbage");
		this.collectButton.setActionCommand("collect");
		this.exploreButton = new JButton("Explore ");
		this.exploreButton.setActionCommand("explore");
		this.solo = new JRadioButton("Solo");
		this.solo.setSelected(true);
		this.solo.setActionCommand("solo");
		this.multi = new JRadioButton("Multi");
		this.multi.setActionCommand("multi");
		this.multi.setEnabled(false);
		GuiMain.x1TestField = new JTextField();
		GuiMain.x2TestField = new JTextField();
		GuiMain.y1TestField = new JTextField();
		GuiMain.y2TestField = new JTextField();
		this.statusbar = new JLabel();
		String[] listData = { "Comming soon." };
		JList list = new JList(listData);

		ButtonGroup bgroup = new ButtonGroup();
		bgroup.add(solo);
		bgroup.add(multi);

		this.x1Label = new JLabel("x1:");
		this.x2Label = new JLabel(" x2:");
		this.y1Label = new JLabel("y1:");
		this.y2Label = new JLabel(" y2: ");

		JPanel p = new JPanel(new BorderLayout());
		JPanel northPanel = new JPanel();
		JPanel westPanel = new JPanel();
		JPanel centerPanel = new JPanel(new GridLayout(1, 2));
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

		westPanel.setPreferredSize(new Dimension(200, 500));
		p.add(centerPanel, BorderLayout.CENTER);
		p.add(southPanel, BorderLayout.SOUTH);
		p.add(westPanel, BorderLayout.WEST);
		p.add(northPanel, BorderLayout.NORTH);

		JPanel westPane = new JPanel(new GridLayout(6, 1));
		JPanel centerPane = new JPanel(new BorderLayout());
		JPanel coordSetter = new JPanel(new GridLayout(2, 2));
		JPanel radioButton = new JPanel(new GridLayout(2, 1));
		JPanel taskList = new JPanel();
		JPanel logoPanel = new JPanel();
		JScrollPane scroller = new JScrollPane(this.myMap,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		centerPane.add(scroller, BorderLayout.CENTER);

		JLabel picLabel = new JLabel(new ImageIcon("logo.png"));
		logoPanel.add(picLabel);

		coordSetter.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), "Set Disposal Coordinates"));
		coordSetter.setPreferredSize(new Dimension(200, 80));
		radioButton.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), "Explore mode"));
		radioButton.setPreferredSize(new Dimension(150, 80));
		westPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), "Options"));
		centerPane.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), "Map"));
		taskList.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), "Task List"));
		taskList.setPreferredSize(new Dimension(180, 90));

		scroller.addMouseWheelListener(this);
		coordSetter.add(this.x1Label);
		coordSetter.add(GuiMain.x1TestField);
		coordSetter.add(this.x2Label);
		coordSetter.add(GuiMain.x2TestField);
		coordSetter.add(this.y1Label);
		coordSetter.add(GuiMain.y1TestField);
		coordSetter.add(this.y2Label);
		coordSetter.add(GuiMain.y2TestField);
		radioButton.add(this.solo);
		radioButton.add(this.multi);
		westPane.add(radioButton);
		westPane.add(coordSetter);
		westPane.add(taskList);
		westPane.add(logoPanel);
		taskList.add(list);
		westPanel.add(westPane);
		centerPanel.add(centerPane);
		southPanel.add(statusbar);
		southPanel.add(this.savemapButton);
		southPanel.add(this.collectButton);
		southPanel.add(this.exploreButton);
		this.add(p);

		this.solo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		this.multi.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		this.savemapButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter bmpFilter = new FileNameExtensionFilter(
						"bmp only", "bmp");
				chooser.setFileFilter(bmpFilter);
				int option;
				option = chooser.showSaveDialog(GuiMain.this);
				if (option == JFileChooser.APPROVE_OPTION) {
					statusbar.setText("You saved "
							+ ((chooser.getSelectedFile() != null) ? chooser
									.getSelectedFile().getName() : "nothing"));
					new MapToBmp(GuiMap.map, chooser.getCurrentDirectory()
							.getAbsolutePath(), chooser.getSelectedFile()
							.getName());
				} else {
					statusbar.setText("You canceled.");
				}
			}
		});

		this.exploreButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!GuiMain.inProgress) {
					GuiMain.inProgress = true;
					bot1.explore();
					exploring=true;
				} else {
					JOptionPane
							.showMessageDialog(
									new JFrame(),
									"An operation in Progress. Operation need to finish before another one can be started.");
				}

			}
		});
		this.collectButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!GuiMain.inProgress) {
					if (checkNumbers()) {
						GuiMain.inProgress = true;
					    collecting=true;
						bot1.collectgarbage(Double.parseDouble(x1TestField
								.getText()), Double.parseDouble(y1TestField
								.getText()), Double.parseDouble(x2TestField
								.getText()), Double.parseDouble(y2TestField
								.getText()));
					}
				} else {
					JOptionPane
							.showMessageDialog(
									new JFrame(),
									"An operation in Progress. Operation need to finish before another one can be started.");
				}
			}

			private boolean checkNumbers() {
				if (x1TestField.getText() != null
						&& x2TestField.getText() != null
						&& y1TestField.getText() != null
						&& y2TestField.getText() != null) {
					try {

						if (checkRange(Double
								.parseDouble(x1TestField.getText()))
								&& checkRange(Double.parseDouble(x2TestField
										.getText()))
								&& checkRange(Double.parseDouble(y1TestField
										.getText()))
								&& checkRange(Double.parseDouble(y2TestField
										.getText()))) {
							return true;

						}
					} catch (NumberFormatException e) {

						JOptionPane.showMessageDialog(new JFrame(),"Wrong coordinates!");
						return false;
					}
					JOptionPane.showMessageDialog(new JFrame(),
							"Wrong coordinates!");
				}
				return false;

			}

			private boolean checkRange(double parseDouble) {

				if (parseDouble >= -30 && parseDouble <= 30) {

					return true;
				}

				return false;
			}
		});
	}

	/**
	 * Updates the map with new data.
	 * 
	 * @param mapData
	 */
	public void updateMap(Map map) {
		myMap.updateMap(map);
	}

	public void mouseWheelMoved(MouseWheelEvent arg0) {
		switch (arg0.getWheelRotation()) {
		case -1:

			this.myMap.raiseScale();
			this.myMap.revalidate();
			this.myMap.repaint();
			break;

		default:
			this.myMap.lowerScale();
			this.myMap.revalidate();
			this.myMap.repaint();
			break;
		}

	}

	// TODO Delete if necessary
	public boolean isInProgress() {
		return inProgress;
	}

	public void operatingThread(){
		Thread temp =new Thread(){
			public void run(){
				while(true){
					if(exploring && map.isExplored() && !collecting){
						GuiMain.inProgress=false;
					}
					if(collecting && map.isGarbageCollected()){
						GuiMain.inProgress=false;
					}
					try {
						sleep(750);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		};
		temp.start();
	}
	// TODO Delete if necessary
	public void setInProgress(boolean inProgress) {
		GuiMain.inProgress = inProgress;
	}

}
