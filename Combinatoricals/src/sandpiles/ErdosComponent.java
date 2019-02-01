package sandpiles;

import java.util.Iterator;
import java.util.Vector;

import main.MainFrame;
import drawer.DrawErdosComponentSandPiles;
import drawer.Outputapplet;

@SuppressWarnings("serial")
public class ErdosComponent extends models.AbstractMathModel {

	public double edgeProbability;
	public AbstractNode[] component;
	Vector<AbstractNode> toGrain;
	public int initialNumber;

	@SuppressWarnings("unchecked")
	public ErdosComponent(int nrOfNode, double edgeProb, boolean initRandom) {
		edgeProbability = edgeProb;
		initialNumber = nrOfNode;
		// create the erdoes renyi graph
		toGrain = new Vector<AbstractNode>();
		SimpleToppler[] graph = new SimpleToppler[nrOfNode];
		for (int i = 0; i < nrOfNode; i++) {
			graph[i] = new SimpleToppler(i);
		}
		for (int i = 0; i < nrOfNode - 1; i++) {
			for (int j = i + 1; j < nrOfNode; j++) {
				if (Math.random() < edgeProb) {
					graph[i].addNeighbor(graph[j]);
					graph[j].addNeighbor(graph[i]);
				}
			}
		}
		// Identify the biggest component
		// initialize a array of label
		int[] names = new int[nrOfNode];
		int newname = 1;
		for (int i = 0; i < names.length; i++) {
			names[i] = 0;
		}// all points are unlabeled
			// the we go through the graph and perform a deep search algorithm
		for (int i = 0; i < names.length; i++) {
			if (names[i] == 0) {// we encountered a new cluster
				names[i] = newname;

				Vector<AbstractNode> toExplore = (Vector<AbstractNode>) graph[i].neighbors
						.clone();
				while (!toExplore.isEmpty()) {
					AbstractNode p = toExplore.remove(0);
					// if names[p.label]!=0 we have already labeled the point so
					// we do nothing,
					// otherwise we label it an add its neighborhood to be
					// checked
					if (names[p.label] == 0) {
						names[p.label] = newname;
						toExplore.addAll((Vector<AbstractNode>) p.neighbors
								.clone());
					}
				}
				newname++;
			}
		}
		// now we identify the biggest component
		int[] count = new int[newname];
		for (int i = 0; i < count.length; i++)
			count[i] = 0;
		for (int i = 0; i < names.length; i++)
			count[names[i]]++;
		int maxname = 1;
		for (int i = 0; i < count.length; i++) {
			if (count[i] > count[maxname])
				maxname = i;
		}
		component = new AbstractNode[count[maxname]];
		int j = 0;
		for (int i = 0; i < names.length; i++) {
			if (names[i] == maxname) {
				component[j] = graph[i];
				component[j].label = j;
				j++;
			}
		}
		// this completes the creation of the graph
		// finally we change the last node into a garbage node.
		GarbageNode dummy = new GarbageNode();
		dummy.neighbors = component[component.length - 1].neighbors;
		Iterator<AbstractNode> it = dummy.neighbors.iterator();
		while (it.hasNext()) {
			AbstractNode p = it.next();
			p.neighbors.remove(component[component.length - 1]);
			p.neighbors.add(dummy);
		}
		component[component.length - 1] = dummy;
		// change the labels to simplify the drawing
		for (int i = 0; i < component.length; i++) {
			component[i].label = i;
		}
		// and in case randomize the heights
		if (initRandom) {
			for (int i = 0; i < component.length; i++) {
				component[i].height = (int) (component[i].neighbors.size() * Math
						.random());
			}
		}
	}

	public void addGrainAndTopple(int px) {
		if (!toGrain.isEmpty())
			throw new IllegalArgumentException(" Programming mistake");
		if (px < 0 || px > component.length)
			throw new IndexOutOfBoundsException(
					" some wrong dimension in graining.");
		toGrain.add(component[px]);
		while (!toGrain.isEmpty()) {
			AbstractNode v = toGrain.remove(0);
			if (v != null) {
				toGrain.addAll((Vector<AbstractNode>) v.addGrain().clone());
			} else {
				System.out.println("found null in grainlist");
			}
		}
	}

	public void addGrain(boolean randomgraining) {
		if (randomgraining) {
			int i = (int) (Math.random() * component.length);
			addGrainAndTopple(i);
		} else {
			addGrainAndTopple(0);
		}
	}

	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return new DrawErdosComponentSandPiles(fr, this, true);
	}
}
