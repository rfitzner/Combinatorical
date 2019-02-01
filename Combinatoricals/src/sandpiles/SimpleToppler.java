package sandpiles;

import java.util.Vector;

public class SimpleToppler extends AbstractNode {

	public SimpleToppler() {
		height = 0;
		neighbors = new Vector<AbstractNode>();
		type = AbstractNode.simpleID;
		valueChanged = true;
	}

	public SimpleToppler(int l) {
		this();
		label = l;
	}

	public Vector<AbstractNode> addGrain() {
		height++;
		valueChanged = true;
		if (height < neighbors.size()) {
			return new Vector<AbstractNode>();
		} else {
			height -= neighbors.size();
			return (Vector<AbstractNode>) neighbors.clone();
		}
	}

}
