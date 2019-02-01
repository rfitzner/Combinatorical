package models;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.Vector;

import main.MainFrame;
import models.ClusterExploration.NNNode;

import drawer.DrawPerc;
import drawer.DrawPercFrozen;
import drawer.Outputapplet;

/**
 * In this class we generate and store a percolation configuration.
 * 
 * @author Robert Fitzner
 * 
 */
public class FrozenPercolationConfiguration extends AbstractMathModel {
	private static final long serialVersionUID = 1L;
	// the parameters for the percolation
	public int horizontalSize;
	public int verticalSize;
	public int maxsize;
	public boolean boundisRadius;
	public int[] orderOfOccupying;

	public Cluster[] configuration;

	public FrozenPercolationConfiguration(int vs, int hs, double prob) {
		this(hs, vs, prob, 10);
	}

	/**
	 * With this constructor we create an fresh percolation configuration,
	 * without labeling the clusters.
	 */
	FrozenPercolationConfiguration(int hs, int vs, double prob,
			int numberofColors) {
		horizontalSize = vs;
		verticalSize = hs;
		generateRandomizedlist();
		generateConfiguration();
	}

	/**
	 * Generates a list with all number from 1 to the given unitsize in a random
	 * order.
	 */
	public void generateRandomizedlist() {
		orderOfOccupying = new int[horizontalSize * verticalSize * 2];
		for (int i = 0; i < orderOfOccupying.length; i++) {
			orderOfOccupying[i] = i;
		}
		int tmp = 0;
		for (int t = orderOfOccupying.length - 1; t > 0; t--) {
			int randomindex = (int) (Math.random() * (t + 1));
			tmp = orderOfOccupying[t];
			orderOfOccupying[t] = orderOfOccupying[randomindex];
			orderOfOccupying[randomindex] = tmp;
		}
	}

	public void generateConfiguration() {
		configuration = new Cluster[horizontalSize * verticalSize];
		for (int i = 0; i < orderOfOccupying.length; i++) {
			boolean horizontal = (orderOfOccupying[i] % 2 == 0);
			int posBegin = orderOfOccupying[i] / 2;
			int posEnd;
			if (horizontal)
				posEnd = posBegin + 1;
			else
				posEnd = posBegin + horizontalSize;
			if (configuration[posBegin] != null) {
				if (configuration[posEnd] != null) {
					Cluster c = new Cluster(posBegin, posEnd,
							orderOfOccupying[i]);
					configuration[posEnd] = c;
					configuration[posBegin] = c;
				} else {

				}
			}
		}
	}

	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return new DrawPercFrozen(fr, this, true);
	}

	public class Cluster implements java.io.Serializable {
		public SortedIntList nodes, edges;
		public boolean frozen;
		int maxx, maxy, minx, miny;

		public Cluster(int posBegin, int posEnd, int edge) {
			nodes = new SortedIntList();
			edges = new SortedIntList();
			nodes.add(new Integer(posBegin));
			nodes.add(new Integer(posEnd));
			edges.add(new Integer(edge));
			minx = posBegin % horizontalSize;
			miny = posBegin / horizontalSize;
			maxx = posEnd % horizontalSize;
			maxy = posEnd / horizontalSize;
		}
	}

	public class SortedIntList implements java.io.Serializable {
		public LinkedList<Integer> content;

		public SortedIntList() {
			content = new LinkedList<Integer>();
		}

		public void add(int p) {
			content.add(new Integer(p), findPos(p));
		}

		/**
		 * return the position of the element in the list, or if not there the
		 * position where it should be found
		 * 
		 * @param p
		 * @return
		 */
		public int findPos(int p) {
			if (content.size() == 0)
				return 0;
			if (content.getFirst().intValue() > p)
				return 0;
			else if (content.getLast().intValue() > p)
				return content.size();
			else {
				int left = 0;
				int right = content.size() - 1;
				while (right - left > 0) {
					int middle = (right + left) / 2;
					if (getInt(middle) == p) {
						return middle; // found it
					}
					if (getInt(middle) > p)
						right = middle;
					else
						left = middle;
				}
				return left; // found position
			}
		}

		public int getInt(int p) {
			if (content.size() == 0)
				return -1;
			else {
				return content.get(p).intValue();
			}
		}

		public boolean contains(int p) {
			if (content.size() == 0)
				return false;
			else if (getInt(findPos(p)) == p)
				return true;
			else
				return false;
		}

		public void merge(SortedIntList other) {
			if (other.content.size() == 0)
				return;
			if (this.content.size() == 0) {
				this.content = other.content;
				return;
			}
			int i = 0;
			int j = 0;
			while ((i < this.content.size()) && (j < other.content.size())) {

			}
		}

	}

}
