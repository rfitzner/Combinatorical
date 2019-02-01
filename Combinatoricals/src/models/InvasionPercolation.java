package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import main.MainFrame;
import drawer.DrawInvasionPercolation;
import drawer.Outputapplet;

@SuppressWarnings("serial")
public class InvasionPercolation extends AbstractMathModel {

	// size of the grid
	public int numberOfFinalEdges, delaypond, minx, miny, maxx, maxy;
	// The set of edges that we have already seen/constructed
	public HashMap<Integer, HashMap<Integer, boolean[]>> seenEdges;
	// The edges in the order that they where added to the invasion cluster
	public ArrayList<Edge> algorithmicOrder;
	// the set of all neighboring edges that could be added in the next step,
	// this list is ordered and
	// whenever we add a new edges we imminently put this in the correct
	// position in the list(insertion sort)
	// to increase runtime by inserting etc I save only the 3000 edges with the
	// smallest weights.
	// So the algorithm could thereby add edges that would otherwise not be
	// added.
	// Happens if we explore a deep bond of at least 3000 edges
	// But these big pond are so rare that they do not justify the otherwise
	// long runtime
	OrderedEdges neighbors;
	// the final invasion cluster can be separated in natural way at a number of
	// pivotal edges.
	// We save these edges
	public ArrayList<Edge> outlets;
	// and also the number of edges that we added between two pivotal bonds.
	public ArrayList<Integer> pondsize;

	public InvasionPercolation(int size, int delay1) {
		// INITIALISING
		this.numberOfFinalEdges = size;
		this.delaypond = delay1;

		// and initials the tools
		algorithmicOrder = new ArrayList<Edge>();
		neighbors = new OrderedEdges();
		outlets = new ArrayList<Edge>();
		pondsize = new ArrayList<Integer>();
		// to be able to draw it in the end we make the maximal displacements in
		// all directions
		maxx = 0;
		maxy = 0;
		minx = 0;
		miny = 0;
		// add the neighbors of the center as possible next edges
		seenEdges = new HashMap<Integer, HashMap<Integer, boolean[]>>();
		neighbors.add(new Edge(0, 0, 1, 0));
		neighbors.add(new Edge(0, 0, -1, 0));
		neighbors.add(new Edge(0, 0, 0, 1));
		neighbors.add(new Edge(0, 0, 0, -1));
		// DO THE INVASION
		int i = 0;
		Edge step;
		boolean tocontinue = true;

		do {
			// the first element in neighbors is the one with the lower weight
			step = neighbors.getFirst();
			// we save it as a taken step
			algorithmicOrder.add(step);
			// and add all neighboring of the end point of the edge to the as
			// new neighbor of the cluster
			addNeighbors(step);
			i++;
			if (neighbors.size() < 1) {
				tocontinue = false;
			}
			if (numberOfFinalEdges < i) {
				tocontinue = false;
			}
		} while (tocontinue);
		// we do not need it anymore so we delete it
		seenEdges = new HashMap<Integer, HashMap<Integer, boolean[]>>();
		// and compute the pots of the invasion
		computePotHights();
	}

	/**
	 * Constructor
	 * 
	 * In this constructor we also directly compute the invasion and ask other
	 * methods to color the ponds.
	 * 
	 * @param size
	 *            number of sets that we want to generate.
	 * @param delay1
	 *            delay we make after finishing a pond (ms)
	 * @param delay2
	 *            delay after the drawing of each edge (ms)
	 */

	/**
	 * I do not only compute the invasion but also color the different ponds. In
	 * invasion percolation we can identify different pivotal edges that
	 * separates the cluster in different parts, they can be identified by there
	 * weight with are high in comparison to the other edge-weights in the
	 * cluster
	 */
	private void computePotHights() {
		// I first look for the biggest weight/height, t
		// then for the biggest that was added after this, and so on
		pondsize.add(new Integer(0));
		int searchbegin = 0;
		double max = 0;
		int pos = 0;
		// now go through the list algorithmicOrder and look for the edge with
		// the biggest weight
		do {
			// System.out.print("Ponds : ");
			for (int i = searchbegin; i < algorithmicOrder.size() - 6; i++) {
				if (algorithmicOrder.get(i).bondp > max) {
					max = algorithmicOrder.get(i).bondp;
					pos = i;
				}
			}
			// System.out.print("("+pos+","+max +") ");
			// save the special edge and the point where we meet it
			outlets.add(algorithmicOrder.get(pos));
			pondsize.add(new Integer(pos));
			searchbegin = pos + 1;
			max = 0;
			// The last edges that we added are in this algorithm not of
			// interest...
		} while (searchbegin < algorithmicOrder.size() - 12);
		pondsize.add(new Integer(algorithmicOrder.size() - 6));
		// System.out.println();
	}

	/**
	 * We add all three neighboring edges at the end-point of the given edge
	 * 
	 * @param e
	 */
	private void addNeighbors(Edge e) {
		if (e.dy == 0) {
			neighbors.add(new Edge(e.x + e.dx, e.y, 0, 1));
			neighbors.add(new Edge(e.x + e.dx, e.y, 0, -1));
			neighbors.add(new Edge(e.x + e.dx, e.y, e.dx, 0));
			maxx = Math.max(e.x + 2 * e.dx, maxx);
			minx = Math.min(e.x + 2 * e.dx, minx);
		} else {
			neighbors.add(new Edge(e.x, e.y + e.dy, 1, 0));
			neighbors.add(new Edge(e.x, e.y + e.dy, -1, 0));
			neighbors.add(new Edge(e.x, e.y + e.dy, 0, e.dy));
			maxy = Math.max(e.y + 2 * e.dy, maxy);
			miny = Math.min(e.y + 2 * e.dy, miny);
		}
	}

	/**
	 * Simple record to save a weighted edge.
	 * 
	 * @author Robert Fitzner
	 */
	public class Edge implements java.io.Serializable {
		public double bondp;
		public int x, y;
		public byte dx, dy; // Position and orientation;

		public Edge(int x, int y, int i, int j) {
			this.bondp = Math.random();
			this.x = x;
			this.y = y;
			this.dx = (byte) i;
			this.dy = (byte) j;
		}

		public boolean equals(Edge other) {
			if ((this.x == other.x) && (this.y == other.y)
					&& (this.dx == other.dx) && (this.dy == other.dy))
				return true;
			if ((this.x + this.dx == other.x) && (this.y + this.dy == other.y)
					&& (this.x == other.x + other.dx)
					&& (this.y == other.y + other.dy))
				return true;
			return false;
		}

		public String toString() {
			return "Edge from (" + x + "," + y + ") to (" + (x + dx) + ","
					+ (y + dy) + ") with p=" + bondp;
		}
	}

	/**
	 * In following we save a list of weighted edges ordered by there weight.
	 * The ordering is establish by the -add- method, where a new element is
	 * always placed at the correct position (Insertion sort). Moreover we
	 * automatically add new edges into seenEdges.
	 * 
	 * @author Robert Fitzner
	 */
	public class OrderedEdges extends LinkedList<Edge> implements
			java.io.Serializable {
		/**
		 * give the first edge of the set which is also the edge with the
		 * smallest weight.
		 */
		public Edge getFirst() {
			return this.remove(0);
		}

		/**
		 * We add the given edge into the neighborhood. Thereby we first check
		 * whether we have already seen the edge, and if not insert it into the
		 * correct order into the ordered set.
		 */
		public boolean add(Edge e) {
			// We only add edges that we have not considered jet.
			if (newToSytemSeen(e)) {
				// So this is a new edge,
				// first the special case of a small neighborhood
				if (this.size() < 2) {
					if (this.size() == 0)
						super.add(e); // the only element
					else {// this.size==1
						if (this.get(0).bondp < e.bondp)
							this.add(e);// we add it after the one existing
										// element
						else
							this.add(0, e); // or place it before the existing
											// element
					}
				} else if (this.size() > 3000) {
					// if we saved more then 3000 edges, then we will only add
					// edges with small edge weight.
					if (e.bondp > this.get(this.size() - 1).bondp) {
						return true;// the weight is bigger then the ones we
									// already have, so we simply quit.
					} else {
						this.remove(this.size() - 1); // otherwise we remove the
														// old 100th element and
														// proceed to the adding
														// of the element
					}
				}
				// now we actually add the edge
				// we begin with two exceptional cases, that are quite likely
				if (e.bondp > this.get(this.size() - 1).bondp) // the new edge
																// is very heavy
					super.add(e);
				else if (e.bondp < this.get(0).bondp)// the new edge has a small
														// weight
					this.add(0, e);
				else {
					// otherwise we look for the right spot and insert
					this.add(findposition(e), e);
				}
			}
			return true;
		}

		/**
		 * we check whether we have already seen the edge, if not we mark it as
		 * seen.
		 * 
		 * @param e
		 * @return
		 */
		public boolean newToSytemSeen(Edge e) {
			// we identify a edge by the left/top end point
			Integer anchorx, anchory;
			// see the orientation 0 horizontal, 1 vertical
			int orientation;
			if (Math.abs(e.dx) == 1)
				orientation = 0;
			else
				orientation = 1;
			if (e.dx + e.dy == -1) {
				anchorx = new Integer(e.x + e.dx);
				anchory = new Integer(e.y + e.dy);
			} else {
				anchorx = new Integer(e.x);
				anchory = new Integer(e.y);
			}
			// if not there jet initialize the entries
			if (!seenEdges.containsKey(anchorx)) {
				seenEdges.put(anchorx, new HashMap<Integer, boolean[]>());
			}
			if (!seenEdges.get(anchorx).containsKey(anchory)) {
				boolean[] tmp = new boolean[2];
				tmp[0] = false;
				tmp[1] = false;
				seenEdges.get(anchorx).put(anchory, tmp);

			}
			// and give back the result
			boolean result = seenEdges.get(anchorx).get(anchory)[orientation];

			seenEdges.get(anchorx).get(anchory)[orientation] = true;
			return !result;
		}

		/**
		 * We want to find where to add the edge e, thereby we know that the
		 * list is already ordered. So we do a binomial search.
		 */
		public int findposition(Edge e) {
			int left = 0;
			int right = this.size() - 1;
			if (e.bondp > this.get(right).bondp)
				return this.size();
			while ((right != left)
					&& (this.get(right).bondp - this.get(left).bondp > 0)) {
				if (this.get((right - left) / 2 + left).bondp < e.bondp)
					left = Math.max((right - left) / 2 + left, left + 1);
				else
					right = (right - left) / 2 + left;
			}
			// I will return left such that
			// neighbor(right).bondp<= e.bondp
			return right;
		}

		public String toString() {
			String out = "";
			for (int i = 1; i < this.size(); i++)
				out += this.get(i).bondp + ",";
			return out;
		}
	}

	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return new DrawInvasionPercolation(fr, this, 100, true);
	}

}
