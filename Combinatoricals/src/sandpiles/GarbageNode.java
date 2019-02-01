package sandpiles;

import java.util.Vector;

public class GarbageNode extends AbstractNode {
	public GarbageNode() {
		height = Integer.MAX_VALUE;
		neighbors = new Vector<AbstractNode>();
		type = AbstractNode.simpleID;
	}

	public Vector<AbstractNode> addGrain() {
		return new Vector<AbstractNode>();
	}

}
