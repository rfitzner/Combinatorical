package models;

import java.awt.Dimension;
import java.util.ArrayList;

import main.MainFrame;
import drawer.Outputapplet;
import drawer.DrawWalkOnPerc;

@SuppressWarnings("serial")
public class PercolationClusterWithWalkOnIt extends AbstractMathModel {

	// bond probability and a stretching factor that modifies the way a cluster
	// is
	// explored in the super-critical regime.
	public double bprob;// strechfactor;
	// number of maximal number of edges in the cluster that will be explored
	// the number of steps that the random walk will take
	// and the time in which it should be drawn.
	public int maxSizeCluster, steps, delay;
	// we saw all vertices at which we look at an edge ( will be deposed after
	// construction )
	public ArrayList<ArrayList<boolean[]>> exploredEdges;
	// the set of edges for which we should explore the neighborhood if the
	// cluster continues.
	public ArrayList<Dimension> pointsToCheck;
	// the set of occupied edges in the final form.
	public boolean[][][] edges;
	// the relative position of the origin.
	public Dimension center;
	// the path of the random walk.
	public Dimension[] path;

	public PercolationClusterWithWalkOnIt(double percprob, int clusterS,
			int numberOfSteps) {
		// INITIALISING
		bprob = percprob;
		steps = numberOfSteps;

		maxSizeCluster = clusterS;
		// strechfactor = streching;
		// create the grid with weighted edges

		// and initials the tools
		pointsToCheck = new ArrayList<Dimension>();

		exploredEdges = new ArrayList<ArrayList<boolean[]>>();
		for (int i = 0; i < 10; i++) {
			ArrayList<boolean[]> tmp2 = new ArrayList<boolean[]>();
			for (int j = 0; j < 10; j++) {
				boolean[] tmp = new boolean[3];
				tmp[0] = false;
				tmp[1] = false;
				tmp[2] = false;
				tmp2.add(tmp);
			}
			exploredEdges.add(tmp2);
		}

		// DO THE INVASION to create the cluster
		int vertexesAdded = 0;
		pointsToCheck.add(new Dimension(0, 0));
		do {
			vertexesAdded++;
			// Dimension point = pointsToCheck.remove(pointsToCheck.size()-1);
			Dimension point = pointsToCheck.remove((int) Math.round(Math
					.random() * (pointsToCheck.size() - 1)));
			// we add the connected neighbors
			addNeighbors(point);
		} while (pointsToCheck.size() > 0 && vertexesAdded < maxSizeCluster);
		// before proceeding we free a bit of memory
		pointsToCheck.clear();
		// System.out.println("Number Of Vertecies: "+vertexesAdded);
		translateToGrid();
		// to clear the memory we delete the dynamic array after we translated
		// it with the method translate to Grid to the static array
		exploredEdges.clear();
		if ((vertexesAdded > Math.sqrt(maxSizeCluster))) {
			path = new Dimension[steps];
			createwalk();
		} else {
			// System.out.println(" cancels with "+vertexesAdded+" points.");
			path = new Dimension[1];
			path[0] = new Dimension(0, 0);
		}
	}

	/**
	 * We translate the dynamic array into a static one. We do this as it is
	 * easier to simulate the random walk on a static array
	 */
	private void translateToGrid() {
		// initialising
		// int minx,miny,maxx,maxy;
		// first we need to identify the size of the the
		// static array
		int minarrayx = 0, maxarrayx = 0, minarrayy = 0, maxarrayy = 0;
		// so we have look for the bonds of a retangel,
		// such that there is not occupied edge out side the max
		// for moving right
		for (int i = 0; i < exploredEdges.size(); i += 2) {
			// for moving right and up
			for (int j = 0; j < exploredEdges.get(i).size(); j += 2) {
				if ((exploredEdges.get(i).get(j)[1])
						|| exploredEdges.get(i).get(j)[2]) {
					maxarrayx = Math.max(i, maxarrayx);
					maxarrayy = Math.max(j, maxarrayy);
				}
			}
			// for moving right and down
			for (int j = 1; j < exploredEdges.get(i).size(); j += 2) {
				if ((exploredEdges.get(i).get(j)[1])
						|| exploredEdges.get(i).get(j)[2]) {
					maxarrayx = Math.max(i, maxarrayx);
					minarrayy = Math.max(j, minarrayy);
				}
			}
		}
		// move left
		for (int i = 1; i < exploredEdges.size(); i += 2) {
			// again first up
			for (int j = 0; j < exploredEdges.get(i).size(); j += 2) {
				if ((exploredEdges.get(i).get(j)[1])
						|| exploredEdges.get(i).get(j)[2]) {
					minarrayx = Math.max(i, minarrayx);
					maxarrayy = Math.max(j, maxarrayy);
				}
			}
			// then down
			for (int j = 1; j < exploredEdges.get(i).size(); j += 2) {
				if ((exploredEdges.get(i).get(j)[1])
						|| exploredEdges.get(i).get(j)[2]) {
					minarrayx = Math.max(i, minarrayx);
					minarrayy = Math.max(j, minarrayy);
				}
			}
		}
		// Translate to Grid bounds
		int maxx = maxarrayx / 2;
		int maxy = maxarrayy / 2;
		int minx = 0;
		int miny = 0;
		if (minarrayx > 0) {
			minx = -(minarrayx + 1) / 2;
		}
		if (minarrayy > 0) {
			miny = -(minarrayy + 1) / 2;
		}
		center = new Dimension(-minx, maxy);
		// now we know the size and can fill the static array
		edges = new boolean[maxx - minx + 1][maxy - miny + 1][2];
		// first we initialize everything as if there are no edges
		for (int i = 0; i < edges.length; i++)
			for (int j = 0; j < edges[0].length; j++)
				for (int k = 0; k < edges[0][0].length; k++)
					edges[i][j][k] = false;
		// and now we full it with the correct values
		for (int i = 0; i < exploredEdges.size(); i += 2) {
			int coordx = i / 2 - minx;
			for (int j = 0; j < exploredEdges.get(i).size(); j += 2) {
				int coordy = maxy - j / 2;
				if (exploredEdges.get(i).get(j)[1])
					edges[coordx][coordy][0] = true;
				if (exploredEdges.get(i).get(j)[2])
					edges[coordx][coordy][1] = true;
			}
			for (int j = 1; j < exploredEdges.get(i).size(); j += 2) {
				int coordy = maxy + (j + 1) / 2;
				if (exploredEdges.get(i).get(j)[1])
					edges[coordx][coordy][0] = true;
				if (exploredEdges.get(i).get(j)[2])
					edges[coordx][coordy][1] = true;
			}
		}
		for (int i = 1; i < exploredEdges.size(); i += 2) {
			int coordx = -minx - (i + 1) / 2;
			for (int j = 0; j < exploredEdges.get(i).size(); j += 2) {
				int coordy = maxy - j / 2;
				if (exploredEdges.get(i).get(j)[1])
					edges[coordx][coordy][0] = true;
				if (exploredEdges.get(i).get(j)[2])
					edges[coordx][coordy][1] = true;
			}
			for (int j = 1; j < exploredEdges.get(i).size(); j += 2) {
				int coordy = maxy + (j + 1) / 2;
				if (exploredEdges.get(i).get(j)[1])
					edges[coordx][coordy][0] = true;
				if (exploredEdges.get(i).get(j)[2])
					edges[coordx][coordy][1] = true;
			}
		}

	}

	/**
	 * @param e
	 */
	private void addNeighbors(Dimension point) {
		// check whether we finished this point before
		if (exploredEdges.get(point.width).get(point.height)[0] != true) {
			// the we get the coordinates of the four neighboring sides
			// in Grid coordinates and as coding for the dynamic array
			Dimension[] neighbor = giveNeighbors(point);
			// end make sure that the corresponding entries in the dynamic array
			// are initialized
			enlargeArray(neighbor);
			// Then we evaluate the 4 neighbors,
			// first we always check that
			// we have not evaluated this side before
			// if not we decide whether we want to draw the edge.
			// if so we add it by telling the left/Top end of the edge
			// that the edge is present (see it to true
			if (exploredEdges.get(neighbor[0].width).get(neighbor[0].height)[0] == false) {
				// we have not evaluated this side so also not this edge
				if (Math.random() < bprob) {
					exploredEdges.get(neighbor[0].width)
							.get(neighbor[0].height)[2] = true;
					pointsToCheck.add(neighbor[0]);
				}
			}
			// left
			if (exploredEdges.get(neighbor[3].width).get(neighbor[3].height)[0] == false) {
				// we have not evaluated this side so also not this edge
				if (Math.random() < bprob) {
					exploredEdges.get(neighbor[3].width)
							.get(neighbor[3].height)[1] = true;
					pointsToCheck.add(neighbor[3]);
				}
			}
			// right and down
			for (int j = 1; j < 3; j++) {
				if (exploredEdges.get(neighbor[j].width)
						.get(neighbor[j].height)[0] == false) {
					// we have not evaluated this side so also not this edge
					if (Math.random() < bprob) {
						exploredEdges.get(point.width).get(point.height)[j] = true;
						pointsToCheck.add(neighbor[j]);
					}
				}
			}
			// in the end we mark that we already evaluated this point
			exploredEdges.get(point.width).get(point.height)[0] = true;
		}
	}

	/**
	 * We give back the coordinates of the four neighboring points, in the
	 * format of the dynamic array first top, right, down, left
	 * 
	 * @param point
	 * @return
	 */
	private Dimension[] giveNeighbors(Dimension point) {
		Dimension[] result = new Dimension[4];
		if ((point.width % 2 == 0) && (point.width > 1)) {
			// we are have x>0
			result[1] = new Dimension(point.width + 2, point.height);
			result[3] = new Dimension(point.width - 2, point.height);
		} else if (point.width == 0) {
			// we are have x=0 so move to x=1 (arg=2)and x=-1(arg=1)
			result[1] = new Dimension(2, point.height);
			result[3] = new Dimension(1, point.height);
		} else if (point.width == 1) {
			// we are have x=-1 so move to x=0 (arg=0)and x=-2(arg=3)
			result[1] = new Dimension(0, point.height);
			result[3] = new Dimension(3, point.height);
		} else {
			result[1] = new Dimension(point.width - 2, point.height);
			result[3] = new Dimension(point.width + 2, point.height);
		}

		if ((point.height % 2 == 0) && (point.height > 1)) {
			// we are have x>0
			result[0] = new Dimension(point.width, point.height + 2);
			result[2] = new Dimension(point.width, point.height - 2);
		} else if (point.height == 0) {
			// we are have x=0 so move to x=1 (arg=2)and x=-1(arg=1)
			result[0] = new Dimension(point.width, 2);
			result[2] = new Dimension(point.width, 1);
		} else if (point.height == 1) {
			// we are have x=-1 so move to x=0 (arg=0)and x=-2(arg=3)
			result[0] = new Dimension(point.width, 0);
			result[2] = new Dimension(point.width, 3);
		} else {
			result[0] = new Dimension(point.width, point.height - 2);
			result[2] = new Dimension(point.width, point.height + 2);
		}
		return result;
	}

	private void enlargeArray(Dimension[] points) {
		try {
			for (int i = 0; i < points.length; i++) {
				for (int j = exploredEdges.size(); j < points[i].width + 4; j++)
					exploredEdges.add(new ArrayList<boolean[]>());
			}
			for (int i = 0; i < points.length; i++) {
				for (int j = exploredEdges.get(points[i].width).size(); j < points[i].height + 4; j++) {
					boolean[] tmp = new boolean[3];
					tmp[0] = false;
					tmp[1] = false;
					tmp[2] = false;
					exploredEdges.get(points[i].width).add(tmp);
				}
			}
		} catch (OutOfMemoryError e) {
			System.out.println("Dynamic array have blown the memory");
		}
	}

	private void createwalk() {
		path[0] = center;
		ArrayList<Dimension> neighbors = new ArrayList<Dimension>();
		for (int i = 0; i < path.length - 1; i++) {
			// collect the set of all point that we can reach in the next step
			try {

				if (path[i].width < edges.length - 1
						&& edges[path[i].width][path[i].height][0])
					neighbors.add(new Dimension(path[i].width + 1,
							path[i].height));
				if (path[i].height < edges[0].length - 1
						&& edges[path[i].width][path[i].height][1])
					neighbors.add(new Dimension(path[i].width,
							path[i].height + 1));
				if (path[i].width > 0) {
					if (edges[path[i].width - 1][path[i].height][0]) {
						neighbors.add(new Dimension(path[i].width - 1,
								path[i].height));
					}
				}
				if (path[i].height > 0) {
					if (edges[path[i].width][path[i].height - 1][1]) {
						neighbors.add(new Dimension(path[i].width,
								path[i].height - 1));
					}
				}
			} catch (Exception e) {
				// was need for debugging
				System.out.println(" Error as a step was missing :" + path[i]);
			}
			// then we select on of the neighbors uniformly as the next position
			// of the
			// walker
			double choice = Math.random();
			for (int j = 0; j < neighbors.size(); j++) {
				if (choice < (j + 1) * 1.0 / neighbors.size()) {
					path[i + 1] = neighbors.get(j);
					j = 6;
				}
			}
			// debugging
			if (path[i + 1] == null) {
				System.out.println("Step "
						+ i
						+ " Prob: "
						+ choice
						+ " and "
						+ neighbors.size()
						+ " with test result "
						+ (choice < (neighbors.size() - 1 + 1) * 1.0
								/ neighbors.size()));
				if (neighbors.size() == 0) {
					if (path[i].width < edges.length)
						System.out.println("Right:"
								+ edges[path[i].width][path[i].height][0]);
					if (path[i].height < edges[0].length)
						System.out.println("Lower:"
								+ edges[path[i].width][path[i].height][1]);
					if (path[i].width - 1 > 0)
						System.out.println("Left:"
								+ edges[path[i].width - 1][path[i].height][0]);
					if (path[i].height - 1 > 0)
						System.out.println("Upper:"
								+ edges[path[i].width][path[i].height - 1][1]);
				}
				i = 10000;
			}
			neighbors.clear();
		}
	}

	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return new DrawWalkOnPerc(fr, this, 0, true);
	}

}
