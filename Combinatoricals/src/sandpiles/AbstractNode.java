package sandpiles;

import java.util.Vector;

public abstract class AbstractNode implements java.io.Serializable {
	public int label;
	static final int dummyID = 0;
	static final int simpleID = 1;
	public Vector<AbstractNode> neighbors;
	public int height;
	int type;
	boolean valueChanged;

	public int getType() {
		return type;
	}

	public int getHeight() {
		return height;
	}

	public Vector<AbstractNode> getNeighbors() {
		return neighbors;
	}

	public void addNeighbor(AbstractNode v) {
		neighbors.add(v);
	}

	public abstract Vector<AbstractNode> addGrain();

	public void setUnChanged() {
		valueChanged = false;
	}

	public void setChanged() {
		valueChanged = true;
	}

	public boolean isChanged() {
		return valueChanged;
	}
}
