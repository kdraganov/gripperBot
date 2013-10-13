package project.pathfinding.bfs;

import java.util.ArrayList;
import java.util.LinkedList;
import project.pathfinding.bfs.Node;
import project.datastructure.Cell;
import project.datastructure.Map;

/**
 * class used while map is being explored to find closest unexplored cell
 * 
 * @author konstantin
 * 
 */
public class BreadthFirstSearch {

	private Map map;
	private ArrayList<Node> closedSet;
	private ArrayList<Node> openSet;
	private boolean atGoal = false;
	private Node goalNode;

	public BreadthFirstSearch(Map map) {
		this.map = map;
		this.closedSet = new ArrayList<Node>();
		this.openSet = new ArrayList<Node>();
	}

	/**
	 * 
	 * @param startCell the current robot location
	 * @return boolean if map is explored
	 */
	public Boolean isExplored(Node startCell) {
		openSet.add(startCell);
		while (!openSet.isEmpty() && !atGoal) {
			Node temp = openSet.remove(0);
			findAdjacent(temp);
			closedSet.add(temp);
		}
		return atGoal;
	}

	/**
	 * 
	 * @return LinkedList<node> the path to the target
	 */
	public LinkedList<Node> getPath() {
		LinkedList<Node> path = new LinkedList<Node>();
		path.addFirst(this.goalNode);
		Node next = this.goalNode.getParent();
		while (next != null) {
			path.addFirst(next);
			next = next.getParent();
		}
		return path; 
	}

	/**
	 * 
	 * @param cell
	 */
	private void findAdjacent(Node cell) {
		expandNode(new Node(cell.getRow(), cell.getColumn() + 1), cell);
		expandNode(new Node(cell.getRow() + 1, cell.getColumn()), cell);
		expandNode(new Node(cell.getRow(), cell.getColumn() - 1), cell);
		expandNode(new Node(cell.getRow() - 1, cell.getColumn()), cell);
		expandNode(new Node(cell.getRow() + 1, cell.getColumn() + 1), cell);
		expandNode(new Node(cell.getRow() - 1, cell.getColumn() - 1), cell);
		expandNode(new Node(cell.getRow() - 1, cell.getColumn() + 1), cell);
		expandNode(new Node(cell.getRow() + 1, cell.getColumn() - 1), cell);
	}

	private void expandNode(Node temp, Node cell) {
		if (isFree(temp) && !wasVisited(temp) && !this.atGoal
				&& !openSet.contains(temp)
				&& !map.isCloseToWall(temp.getRow(), temp.getColumn(), 0.25)) {
			isGoal(temp);
			openSet.add(temp);
			temp.setParent(cell);
		}
	}

	/**
	 * 
	 * @param temp
	 * @return
	 */
	private boolean wasVisited(Node node) {
		return this.closedSet.contains(node);
	}

	/**
	 * 
	 * @param point
	 * @return
	 */
	private boolean isFree(Node node) {
		return (map.getValue(node.getRow(), node.getColumn()) != Cell.OBSTACLE && map
				.getValue(node.getRow(), node.getColumn()) >= 0);
	}

	/**
	 * 
	 * @param point
	 */
	private void isGoal(Node node) {
		if (map.getValue(node.getRow(), node.getColumn()) == Cell.UNEXPLORED) {
			this.atGoal = true;
			this.goalNode = node;
		}
	}
}
