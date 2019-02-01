package models;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;

import drawer.DrawClusterExplorationTwoD;
import drawer.Outputapplet;

import main.MainFrame;

/**
 * This class generates let a random walk explore a percolation cluster. So we
 * generate a simple random walks that only walks along occupied edges. Every
 * time the walk encounters a new edge we check whether the edge is occupied or
 * not then the walk proceeds.
 * 
 * @author Robert Fitzner
 */
public class ClusterExplorationTwoD extends AbstractMathModel {
	private static final long serialVersionUID = 1L;
	// probability that a edge is occupied
	public double bondp;
	// total time in which the walk is drawn
	public int steps, minx, miny, maxx, maxy, tries;
	// To save memory we do not save the full grid but only those points that
	// we actually seen
	public HashMap<Integer, HashMap<Integer, boolean[]>> seenEdges;
	// path of the random walk
	public Dimension[] path;

	/**
	 * 
	 * @param size
	 *            number of steps the walk should take
	 * @param bondprob
	 *            the bond probability of the percolation
	 * @param delay1
	 *            delay at drawing the edges
	 */
	public ClusterExplorationTwoD(int size, double bondprob, boolean retry) {
		// INITIALISING
		this.steps = size;
		this.bondp = bondprob;
		// and initials the variable for the path
		// add the neighbors of the center as possible next edges

		tries = 0;
		do {
			seenEdges = new HashMap<Integer, HashMap<Integer, boolean[]>>();
			maxx = 0;
			maxy = 0;
			minx = 0;
			miny = 0;
			path = new Dimension[size + 1];
			path[0] = new Dimension(0, 0);
			tries++;
			for (int i = 0; i < path.length - 1; i++) {
				path[i + 1] = setNeighbors(path[i]);
				// in this case i=0 and by chance we began at an isolated
				// vertex.
				if (path[i + 1] == null) {
					path = new Dimension[1];
					path[0] = new Dimension(0, 0);
					steps = 1;
				} else {
					maxx = Math.max(path[i + 1].width, maxx);
					minx = Math.min(path[i + 1].width, minx);
					maxy = Math.max(path[i + 1].height, maxy);
					miny = Math.min(path[i + 1].height, miny);
				}
			}
		} while (!(!retry || ((retry && tries == 100)) || (seenEdges.size() > 5)));
	} // (path.length==size+1)

	/**
	 * We just arrived at a site and check which of the neighboring edges is
	 * occupied. In the end we return one of the directly connected neighbors
	 * uniformly as new destination for the next step.
	 * 
	 * @param point
	 */
	private Dimension setNeighbors(Dimension point) {
		ArrayList<Dimension> connectedNeighbors = new ArrayList<Dimension>();
		if (isEdgeOccupied(point, 0))
			connectedNeighbors
					.add(new Dimension(point.width + 1, point.height));
		if (isEdgeOccupied(point, 1))
			connectedNeighbors
					.add(new Dimension(point.width, point.height + 1));
		Dimension np1 = new Dimension(point.width - 1, point.height);
		if (isEdgeOccupied(np1, 0))
			connectedNeighbors.add(np1);
		Dimension np2 = new Dimension(point.width, point.height - 1);
		if (isEdgeOccupied(np2, 1))
			connectedNeighbors.add(np2);
		if (connectedNeighbors.size() == 0)
			return null;
		return connectedNeighbors.get((int) (connectedNeighbors.size() * Math
				.random()));
	}

	/**
	 * This give back whether an edge is occupied or not.
	 * 
	 * @param point
	 *            the down of left end of the edge
	 * @param o
	 *            the orientation of the edge, 0 horizontal, 1 vertical
	 * @return status of the edge
	 */
	private boolean isEdgeOccupied(Dimension point, int o) {
		Integer x = point.width;
		Integer y = point.height;
		// check if we have seen the anchor pointedge before and if not then
		// initialize the
		// point
		if (!seenEdges.containsKey(x))
			seenEdges.put(x, new HashMap<Integer, boolean[]>());
		if (!seenEdges.get(x).containsKey(y)) {
			boolean[] tmp = new boolean[4];
			tmp[0] = false;// occupied
			tmp[1] = false;// occupied
			tmp[2] = false;// set before
			tmp[3] = false;// set before
			seenEdges.get(x).put(y, tmp);
		}
		// if we have not set the status before we do this now
		if (seenEdges.get(x).get(y)[o + 2] == false) {
			seenEdges.get(x).get(y)[o + 2] = true;
			seenEdges.get(x).get(y)[o] = (Math.random() < bondp);
		}
		// what we actually wanted
		return seenEdges.get(x).get(y)[o];
	}

	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return new DrawClusterExplorationTwoD(fr, this, true);
	}

}