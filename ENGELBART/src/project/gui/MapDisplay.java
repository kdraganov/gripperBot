package project.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import project.datastructure.Map;
import project.gui.mapping.GuiMap;

public class MapDisplay extends JFrame implements MouseWheelListener {
	private GuiMap myMap;

	public MapDisplay(Map map) {
		this.setTitle("Gripper BOT MAP");
		this.myMap = new GuiMap(map);
		this.myMap.isReady(true);
		updateMap(map);
		createComponents();
		initializeFrame();
	}

	private void initializeFrame() {
		this.setSize(900, 600);
		this.setResizable(true);
		this.setMinimumSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}

	private void createComponents() {
		JScrollPane scroller = new JScrollPane(this.myMap,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.addMouseWheelListener(this);
		JPanel p = new JPanel(new BorderLayout());
		p.add(scroller, BorderLayout.CENTER);
		this.add(p);

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

}
