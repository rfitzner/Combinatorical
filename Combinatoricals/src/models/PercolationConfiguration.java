package models;

import java.awt.Dimension;
import java.util.Vector;

import main.MainFrame;

import drawer.DrawPerc;
import drawer.Outputapplet;

/**
 * In this class we generate and store a percolation configuration.
 * 
 * @author Robert Fitzner
 * 
 */
public class PercolationConfiguration extends AbstractMathModel {
	private static final long serialVersionUID = 1L;
	// the parameters for the percolation
	public int horizontalSize;
	public int verticalSize;
	public double bondprob;
	// Moreover, I save how many clusters should be colored
	int nrofcolors;
	// the following two booleans are used if we want to cancel the the
	// computation
	// of a new configuration ( function not jet implemented )
	boolean canceled, completed;
	// the number of points that we already labeled
	int labeled;

	// The basic configuration: we have a {0,1}x{0,...vsize-1}x{0,..hsize-1} big
	// array
	// if a boolean configuration[0][i][j] is true then the bond (i,j),(i+1,j)
	// is occuppied
	// if a boolean configuration[1][i][j] is true then the bond (i,j),(i,j+1)
	// is occuppied
	public boolean[][][] configuration;
	// Additionally we label all the clusters. We do this by saying telling all
	// points (i,j)
	// what the label(number) of his cluster is marking[i,j].
	public int[][] markings;

	// We want to color the 10(or n) biggest clusters, the biggest in red,
	// second biggest in blue,....
	// The following two arrays are used for the correct coloring
	int[] numberofvertinC; // we count the number of points in a cluster
	public int[] orderOfClusters; // and in the end we sort the cluster by there
									// number of points

	public PercolationConfiguration(int vs, int hs, double prob) {
		this(hs, vs, prob, 10);
	}

	/**
	 * With this constructor we create an fresh percolation configuration,
	 * without labeling the clusters.
	 */
	PercolationConfiguration(int hs, int vs, double prob, int numberofColors) {
		horizontalSize = vs;
		verticalSize = hs;
		bondprob = prob;
		nrofcolors = numberofColors;
		canceled = false;
		completed = false;
		configuration = generateConfiguration(verticalSize, horizontalSize,
				bondprob);
		labelTheCluster();
	}

	/*
	 * We create a configuration C1 with bond probability p1, starting from
	 * another configuration C2 with bond probability p2<p1. Therefore we begin
	 * with C2 and let all occupied edge occuipied. Then we set all edges that
	 * are vacant in C2 occupied with probability: (p1-p2) / (1 -p2). This
	 * results in configuration that corresponds to a conf. with bond
	 * probability p1. We do not labeling the clusters of this configuration.
	 */
	public PercolationConfiguration(PercolationConfiguration oldconfig, int hs,
			int vs, double oldbondprob, double newbondprob) {
		horizontalSize = hs;
		verticalSize = vs;
		bondprob = newbondprob;
		canceled = false;
		completed = false;

		// first we copy the old edges
		configuration = new boolean[2][horizontalSize - 1][verticalSize - 1];

		boolean[][][] edgesToAdd = addsomeEdgesConfiguration(oldbondprob,
				newbondprob, oldconfig.configuration);

		// then initials the new configuration with the old + the new edges
		for (int i = 0; i < horizontalSize - 1; i++) {
			for (int j = 0; j < verticalSize - 1; j++) {
				configuration[0][i][j] = oldconfig.configuration[0][i][j]
						|| edgesToAdd[0][i][j];
				configuration[1][i][j] = oldconfig.configuration[1][i][j]
						|| edgesToAdd[1][i][j];
			}
		}
		labelTheCluster();
	}

	/**
	 * With this method we initialize the labeling all clusters of the
	 * percolation configurations There we assume a priori that nothing on the
	 * configuration is labeled.
	 * 
	 * After labeling we generate the list orderOfClusters which orders the
	 * cluster by there size.
	 */
	public void labelTheCluster() {
		// we have not labeled up to now
		labeled = 0;
		// we begin by labeling all clusters
		labelClusters();
		// and at last initialise the array orderOfClusters
		sortClustersizes();
		completed = true;
	}

	/**
	 * Generate a configuration of given size for given bond probability p.
	 */
	public static boolean[][][] generateConfiguration(int hsize, int vsize,
			double bondprob) {
		boolean[][][] percConfiguration = new boolean[2][vsize - 1][hsize - 1];
		for (int i = 0; i < vsize - 1; i++) {
			for (int j = 0; j < hsize - 1; j++) {
				percConfiguration[0][i][j] = (Math.random() < bondprob);
				percConfiguration[1][i][j] = (Math.random() < bondprob);
			}
		}
		return percConfiguration;
	}

	/**
	 * In the following we start from a percolation configuration that is
	 * assumed to be generated with edge probability oldbondprob and then
	 * generate a configration that corresponds to a percolation configuration
	 * with bond probability newbondprod>oldbondprob. I assume that the input
	 * satisfies oldbondprob<newbondprob, otherwise we return the original
	 * configuration.
	 * 
	 * To generate the new configuration we only add the edges that are missing
	 * for the configuration configuration to be a representative for
	 * configurations with edge probability newbondprob.
	 * 
	 * @param oldbondprob
	 * @param newbondprob
	 * @param config
	 * @return
	 */
	public static boolean[][][] addsomeEdgesConfiguration(double oldbondprob,
			double newbondprob, boolean[][][] config) {
		double gapprobability = (newbondprob - oldbondprob) / (1 - oldbondprob);
		boolean[][][] missingedges = new boolean[2][config[0].length][config[0][0].length];

		for (int i = 0; i < config[0].length; i++) {
			for (int j = 0; j < config[0][0].length; j++) {
				if (!config[0][i][j])
					missingedges[0][i][j] = (Math.random() < gapprobability);
				if (!config[1][i][j])
					missingedges[1][i][j] = (Math.random() < gapprobability);
			}
		}
		return missingedges;
	}

	/**
	 * With this method we labeling all clusters of the percolation
	 * configurations There we assume a priori that nothing on the configuration
	 * is labeled. Here I implement the depth-first algorithm. Whenever I find a
	 * point in a new cluster, I follow all the occupied bonds and label all
	 * connected points and thereby the hole cluster.
	 * 
	 * 
	 */
	public void labelClusters() {
		// initialize
		int usedcluster = 1;
		markings = new int[horizontalSize][verticalSize];
		for (int x = 0; x < horizontalSize; x++)
			for (int y = 0; y < verticalSize; y++)
				markings[x][y] = 0;
		/*
		 * I see the field-index as usually in the matrix characterization
		 * Positions xy so a field 11 21 31 41 .. 21 22 32 42 23 23 33 43
		 * 
		 * My loop go like reading a text so first form left to right and after
		 * finishing a line I increase y to read the next line.
		 * 
		 * When I find a new cluster, I explore this cluster before proceeding.
		 */

		// go through the field.
		for (int y = 0; y < verticalSize - 1; y++) {
			for (int x = 0; x < horizontalSize - 1; x++) {
				// if the label is not 0 then we have seen the cluster before
				// and can proceed
				if (markings[x][y] == 0) {
					// if the next line were true then the point would be
					// isolated and we could simply proceed
					if (!((configuration[0][x][y]) && (configuration[1][x][y]))) {
						// so we found a unlabeled cluster, we set the number
						// for the new cluster
						usedcluster++;
						/*
						 * Now we label this point and then explore the cluster
						 * that is connected to this point. This could easily
						 * been done by recursion but a stack overflow is very
						 * likely. So we use a method that works like recursion.
						 * I save all the points that I still have to label in a
						 * vector.
						 */
						Vector<Dimension> vector = new Vector<Dimension>();
						vector.add(new Dimension(x, y));
						// do the labeling
						markings[x][y] = usedcluster;
						labeled++;
						while (!vector.isEmpty()) {
							// we take the off the first element of the vector
							Dimension point = vector.remove(0);
							/*
							 * Now we check in all four surrounding vertices. We
							 * check in the following order - are we at the
							 * lower border, left border etc - is the neighbor
							 * connected to the point - have we not labeled that
							 * neighbor before. If all this is the case then -
							 * we label the new point - and then add it to the
							 * list of point for which we still have to check
							 * the neighborhood
							 */

							if (point.height < verticalSize - 1) {
								if (point.width < horizontalSize - 1)
									if (configuration[0][point.width][point.height])
										if (markings[point.width + 1][point.height] == 0) {
											markings[point.width + 1][point.height] = usedcluster;
											labeled++;
											vector.add(new Dimension(
													point.width + 1,
													point.height));
										}
								// left
								if (point.width > 0)
									if (configuration[0][point.width - 1][point.height])
										if (markings[point.width - 1][point.height] == 0) {
											markings[point.width - 1][point.height] = usedcluster;
											labeled++;
											vector.add(new Dimension(
													point.width - 1,
													point.height));
										}
							}
							if (point.width < configuration[0].length) {
								// up
								if (point.height > 0)
									if (configuration[1][point.width][point.height - 1])
										if (markings[point.width][point.height - 1] == 0) {
											markings[point.width][point.height - 1] = usedcluster;
											labeled++;
											vector.add(new Dimension(
													point.width,
													point.height - 1));
										}

								// down
								if (point.height < configuration[0][0].length)
									if (configuration[1][point.width][point.height])
										if (markings[point.width][point.height + 1] == 0) {
											markings[point.width][point.height + 1] = usedcluster;
											labeled++;
											vector.add(new Dimension(
													point.width,
													point.height + 1));
										}
							}

						}
					} else {
						labeled++;
					}
				}
				// was a new cluster
			}// end sum over x (going horizontal)
		}// end sum over y (going downwards)

		// In the end I go through the configuration once more and count the
		// number of vertices that are connected to
		// a cluster.
		numberofvertinC = new int[usedcluster + 1];

		for (int x = 0; x < markings.length; x++)
			for (int y = 0; y < markings[0].length; y++)
				numberofvertinC[markings[x][y]]++;
	}

	/**
	 * In this method we generate a list where that contain the cluster order
	 * there size(increasing).
	 */
	private void sortClustersizes() {
		int length = numberofvertinC.length - 1;
		if (length < 2)
			orderOfClusters = numberofvertinC;
		else {
			int[] sorted = new int[length];
			for (int i = 0; i < length; i++) {
				sorted[i] = i + 1;
			}
			orderOfClusters = mergeSort(sorted, 1, sorted.length - 1);
		}
	}

	/**
	 * A typical mergeSort, where we do order by the number itself but by the
	 * number of vertices in the cluster with this label.
	 */
	private int[] mergeSort(int[] tosort, int left, int right) {
		if (left == right) {
			int[] oneelem = new int[1];
			oneelem[0] = tosort[left];
			return oneelem;
		} else {
			int middle = left + (right - left) / 2;
			int[] lefthalf = mergeSort(tosort, left, middle);
			int[] righthalf = mergeSort(tosort, middle + 1, right);
			int[] result = new int[right - left + 1];
			int iT = 0;
			int iTleft = 0;
			int iTright = 0;
			while ((iTleft < lefthalf.length) && (iTright < righthalf.length)) {
				if (numberofvertinC[lefthalf[iTleft]] > numberofvertinC[righthalf[iTright]]) {
					result[iT] = lefthalf[iTleft];
					iTleft++;
					iT++;
				} else {
					result[iT] = righthalf[iTright];
					iTright++;
					iT++;
				}
			}
			if (iTleft < lefthalf.length) {
				while (iTleft < lefthalf.length) {
					result[iT] = lefthalf[iTleft];
					iTleft++;
					iT++;
				}
			} else
				while ((iTright < righthalf.length)) {
					result[iT] = righthalf[iTright];
					iTright++;
					iT++;
				}
			return result;
		}

	}

	/**
	 * 
	 * I return the color in which a edge should be drawn. labelOfCluster is the
	 * name of the cluster in which the edge is. If the vertex is not in one of
	 * the biggest clusters (up to nrOfcolors cluster) -1 is return and the edge
	 * will be gray.
	 */
	public static int givecolorvalue(int[] sorted, int labelOfCluster,
			int nrOfColors) {

		for (int i = 0; i < sorted.length && i < nrOfColors; i++)
			if (labelOfCluster == sorted[i]) {
				return i;
			}
		return nrOfColors - 1;
	}

	/**
	 * Generates a list with all number from 1 to the given unitsize in a random
	 * order.
	 */
	public static int[] generateRandomizedlist(int unitsize) {
		int[] result = new int[unitsize * unitsize];
		Vector<Integer> tmp = new Vector<Integer>();
		for (int i = 0; i < result.length; i++) {
			tmp.add(new Integer(i));
		}

		for (int i = 0; i < result.length; i++) {
			int randomindex = (int) (Math.random() * (result.length - i));
			result[i] = tmp.remove(randomindex);
		}

		return result;
	}

	/**
	 * Interface for canceling the computation (TOCOME)
	 */
	public void setCancel() {
		canceled = true;
	}

	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return new DrawPerc(fr, this, true);
	}

}
