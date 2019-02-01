package sandpiles;

import java.util.Vector;

public class RegularTree {
	int nrChildren, height;
	SimpleToppler[][] tree;
	GarbageNode dummy;
	Vector<AbstractNode> toGrain;

	public RegularTree(int h, int nrchild, boolean initRandom) {
		if (h < 2 || nrchild < 1)
			throw new IllegalArgumentException("Tree is to small");

		height = h;
		nrChildren = nrchild;
		dummy = new GarbageNode();
		toGrain = new Vector<AbstractNode>();
		initemptyTree();
		if (initRandom) {
			for (int i = 0; i < tree.length; i++)
				for (int j = 0; j < tree[i].length; j++) {
					tree[i][j].height = (int) (Math.random() * tree[i][j].neighbors
							.size());
				}
		}
	}

	private void initemptyTree() {
		tree = new SimpleToppler[height][];
		for (int i = 0; i < tree.length; i++) {
			tree[i] = new SimpleToppler[(int) Math.pow(nrChildren, i)];
			for (int j = 0; j < tree[i].length; j++) {
				tree[i][j] = new SimpleToppler();
			}
		}
		// first we fill the interior.

		for (int i = 0; i < tree.length - 1; i++) {
			for (int j = 0; j < tree[i].length; j++) {
				// add the connection between parents and children
				for (int k = j * nrChildren; k < (j + 1) * nrChildren; k++) {
					tree[i][j].addNeighbor(tree[i + 1][k]);
					tree[i + 1][k].addNeighbor(tree[i][j]);
				}
			}
		}
		// then we connect
		// tree[0][0].addNeighbor(dummy);
		// and finally we connect all leafs to the dummy
		for (int j = 0; j < tree[tree.length - 1].length; j++) {
			tree[tree.length - 1][j].addNeighbor(dummy);
		}
	}

	public void addGrainAndTopple(int px, int py) {
		if (!toGrain.isEmpty())
			throw new IllegalArgumentException(" Programming mistake");
		if (px < 0 || py < 0 || px > tree.length || py > tree[px].length)
			throw new IndexOutOfBoundsException(
					" some wrong dimension in graining.");
		toGrain.add(tree[px][py]);
		while (!toGrain.isEmpty()) {
			AbstractNode v = toGrain.remove(0);
			if (v != null) {
				toGrain.addAll(v.addGrain());
			} else {
				System.out.println("found null in grainlist");
			}
		}
	}

	public AbstractNode[][] getRealizationAsArray() {
		return tree;
	}

	public void addGrainAndTopple(boolean randomgraining) {
		if (randomgraining) {
			int point = (int) ((Math.pow(nrChildren, height) - 1) * Math
					.random());
			// int px=(int) Math.pow(point, 1.0/nrChildren);
			// int py=(int) (Math.pow(nrChildren,px)*Math.random());
			// System.out.println("take the point "+point+" which should be at "+px+" and "+py);
			int px = (int) (height * Math.random());
			int py = (int) (Math.pow(nrChildren, px) * Math.random());
			addGrainAndTopple(px, py);
		} else {
			addGrainAndTopple(0, 0);
		}
	}
}
