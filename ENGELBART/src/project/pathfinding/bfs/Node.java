package project.pathfinding.bfs;

public class Node {

	private int row, column, value;
	private Node parent;

	public Node(int row, int column) {
		this.row = row;
		this.column = column;
		this.parent = null;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getParent() {
		return this.parent;
	}

	public int getRow() {
		return this.row;
	}

	public int getColumn() {
		return this.column;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	@Override
	public boolean equals(Object object) {
		boolean isEqual = false;
		if (object != null && object instanceof Node) {
			isEqual = (this.row == ((Node) object).getRow() && this.column == ((Node) object)
					.getColumn());
		}
		return isEqual;
	}

}
