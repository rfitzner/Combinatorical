package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import main.MainFrame;
import drawer.DrawClusterExploration;
import drawer.DrawPerc;
import drawer.Outputapplet;

public class ClusterExploration extends AbstractMathModel {
	// size of the grid
	public double bprob, drawnlasttime, maxEuclidean;
	// number of step that the walk should take, the time in which the walk
	// should be drawn. Further we have the dimension in which we draw it.
	// And we save the number of runs that we tried to generate the
	// configuration.
	public int steps, dim, numberOfRuns;
	// We save all explored edges in a system of stacked HaspMaps to improve
	// runtime and minimize the necessary memory to save the explored space.
	public HashMap<Integer, Serializable> exploredGrid;
	// the maximal displacements in the different dimensions
	public int[] maxDis, minDis;
	// the path of the dimension.
	public LinkedList<NNNode> path;
	public int addedEdge;

	public ClusterExploration(double percprob, int dimension, int numberOfSteps) {
		// INITIALISING
		dim = dimension;
		bprob = percprob;
		steps = numberOfSteps;
		drawnlasttime = 0;
		numberOfRuns = 0;

		// we will try to generate a walk
		// if we start at a isolated point/ a small cluster (euclidean <5)
		// we give it a new try.

		boolean failed;
		do {
			addedEdge = 0;
			failed = false;
			// initializing of the walk and environment
			exploredGrid = new HashMap<Integer, Serializable>();
			maxEuclidean = 0;
			maxDis = new int[dim];
			minDis = new int[dim];
			for (int i = 0; i < dim; i++) {
				maxDis[i] = 0;
				minDis[i] = 0;
			}
			path = new LinkedList<NNNode>();
			int[] orr = new int[dim];
			for (int i = 0; i < dim; i++)
				orr[i] = 0;
			path.add(giveNodeToHashRecurison(orr, exploredGrid, 0));
			NNNode oldpoint = path.get(0);
			int stepstaken = 0;
			try {
				do {
					exploreNeighborhood(oldpoint);
					NNNode nextpoint = giveNextStep(oldpoint);
					path.add(nextpoint);
					if (nextpoint == null)// mistake abort
					{
						failed = true;
						// stepstaken = steps + 1;
						// System.out.println("failed dute to no neighbots "
						// + numberOfRuns);
					}
					stepstaken++;
					oldpoint = nextpoint;

				} while (stepstaken < steps);
			} catch (NullPointerException e) {
				failed = true;
			}
			numberOfRuns++;
			if (!failed) {
				prepareDrawing();
				if (maxEuclidean < 5) {
					failed = true;
					// System.out.println("failed dute to small move "
					// + numberOfRuns);
				}
			}
		} while (failed && (numberOfRuns < 100));
		// before proceeding we free a bit of memory
		//exploredGrid.clear();
	}

	/**
	 * We want to have create a node at the position described at the
	 * coordinates point, if it does not exist we first create the point, add it
	 * to the map and then return it.
	 * 
	 * @param point
	 *            the coordinate of the requested point
	 * @param map
	 *            the set of saved points
	 * @param level
	 *            the method is called recursively, level give the level of
	 *            recursion
	 * @return the desire point
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private NNNode giveNodeToHashRecurison(int[] point,
			HashMap<Integer, Serializable> map, int level) {
		if (level < point.length - 1) {
			if (!map.containsKey(new Integer(point[level]))) {
				map.put(new Integer(point[level]), new HashMap());
			}
			return giveNodeToHashRecurison(point,
					(HashMap<Integer, Serializable>) map.get(new Integer(
							point[level])), level + 1);
		} else {
			if (map.containsKey(new Integer(point[level]))) {
				return (NNNode) map.get(new Integer(point[level]));
			} else {
				addedEdge++;
				NNNode newNode = new NNNode(bprob, point);
				// System.out.println("Put " + newNode.toString());
				map.put(new Integer(point[level]), newNode);
				return newNode;
			}
		}
	}

	/**
	 * Here we want to be sure that we explore the nighborhoof of the poinat
	 * Here we first check whether we added it already before A point knows
	 * whether it has edges to the right or above, so we only check the left and
	 * down.
	 * 
	 * @param point
	 *            the that
	 */
	private void exploreNeighborhood(NNNode point) {
		if (point.fullyExplored == false) {
			for (int d = 0; d < point.pos.length; d++) {// we have to look at d
														// different neighbors,
														// (the other d are
														// already verified
				int[] tmp = new int[point.pos.length];// first we copy the
														// position of this
														// point
				for (int i = 0; i < tmp.length; i++)
					tmp[i] = point.pos[i];
				tmp[d]--;// then change it to the coordinate that we want to
							// check next
				giveNodeToHashRecurison(tmp, exploredGrid, 0);
				// and check the node.
			}
			point.fullyExplored = true;
		}
	}

	/**
	 * We give back the coordinates of the four neighboring points, in the
	 * format of the dynamic array first top, right, down, left
	 * 
	 * @param point
	 * @return the next neighbor
	 */
	private NNNode giveNextStep(NNNode point) {
		ArrayList<NNNode> directNeighbors = new ArrayList<NNNode>();
		// Find Neighbors
		for (int i = 0; i < point.edges.length; i++) {
			if (point.edges[i]) {
				int[] tmp = new int[point.pos.length];
				for (int j = 0; j < tmp.length; j++)
					tmp[j] = point.pos[j];
				tmp[i]++;
				directNeighbors.add(giveNodeToHashRecurison(tmp, exploredGrid,
						0));
			}
			int[] tmp2 = new int[point.pos.length];
			for (int j = 0; j < tmp2.length; j++)
				tmp2[j] = point.pos[j];
			tmp2[i]--;
			NNNode node = giveNodeToHashRecurison(tmp2, exploredGrid, 0);
			if (node.edges[i]) {
				directNeighbors.add(node);
			}
		}
		/*
		 * System.out.print(" Neighbors of "+point.toString()+ " are :");
		 * for(int i=0; i< directNe ighbors.size();i++){
		 * System.out.print(directNeighbors.get(i).toString()+ "; "); }
		 * System.out.println();
		 */
		if (directNeighbors.size() == 0) {
			return null;
		} else {
			return directNeighbors.get((int) (Math.random() * directNeighbors
					.size()));
		}
	}

	/*
	 * As a preparation of the drawing we compute the maximal displacement and
	 * compute the maximal euclidean norm.
	 */
	private void prepareDrawing() {
		Iterator<NNNode> it = path.iterator();
		while (it.hasNext()) {
			NNNode n = it.next();
			int eucl = 0;
			for (int d = 0; d < dim; d++) {
				minDis[d] = Math.min(n.pos[d], minDis[d]);
				maxDis[d] = Math.max(n.pos[d], maxDis[d]);
				eucl += n.pos[d] * n.pos[d];
			}
			maxEuclidean = Math.max(maxEuclidean, Math.sqrt(eucl));
		}
	}

	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return new DrawClusterExploration(fr, this, 0, true);
	}

	/**
	 * Represents a note of the Zd graph with all connected edges that are
	 * connected Up to now only for the NN setting
	 * 
	 * @author Robert Fitzner
	 * 
	 */
	@SuppressWarnings("serial")
	public class NNNode implements java.io.Serializable {
		// probability of a edge
		public double bondp;
		// the position of the node in the lattice /half-plane
		public int[] pos;
		// information whether the connected edges
		// are:open:open:explored:explored
		public boolean[] edges;
		// information whether we explore all neighboring points of this node
		public boolean fullyExplored;

		public NNNode(double probability, int[] p) {
			this.bondp = probability;
			this.pos = p;
			edges = new boolean[p.length];
			// at each node we save d neighbors and at this point we set them
			// open or closed
			for (int i = 0; i < edges.length; i++)
				edges[i] = (Math.random() < bondp);
			fullyExplored = false;
		}

		/**
		 * Typical function to see whether two node represent the same point on
		 * the half-plane
		 * 
		 * @param other
		 *            the node that we want to compare it to
		 * @return whether the two nodes represent the same node
		 */
		public boolean equals(NNNode other) {
			if (pos.length != other.pos.length)
				return false;
			for (int i = 0; i < pos.length; i++)
				if (pos[i] != other.pos[i])
					return false;
			return true;
		}

		/**
		 * @return Euclidean norm of this point
		 */

		public double euclideanNorm() {
			double tmp = 0;
			for (int i = 0; i < pos.length; i++)
				tmp += pos[i] * pos[i];
			return Math.sqrt(tmp);
		}

		/**
		 * 
		 */
		public String toString() {
			String re = "Node (";
			for (int i = 0; i < pos.length - 1; i++)
				re = re + "" + pos[i] + ",";
			re = re + "" + pos[pos.length - 1] + ")";
			return re;
		}
	}

}
