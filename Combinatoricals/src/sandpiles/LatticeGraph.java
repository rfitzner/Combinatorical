package sandpiles;

import java.util.Vector;

import main.MainFrame;
import drawer.DrawLatticeGraph;
import drawer.Outputapplet;

@SuppressWarnings("serial")
public class LatticeGraph extends models.AbstractMathModel {
	public int height, width;
	public SimpleToppler[][] grid;
	public GarbageNode dummy;
	Vector<AbstractNode> toGrain;

	public LatticeGraph(int h, int w, boolean random) {
		if (h < 3 || w < 3)
			throw new IllegalArgumentException("Gridsize to small");

		height = h;
		width = w;
		dummy = new GarbageNode();
		toGrain = new Vector<AbstractNode>();
		grid = new SimpleToppler[w][h];
		initemptyGrid();
		if (random) {
			// System.out.print("make random"+((int) (Math.random()*4)));
			for (int i = 0; i < w; i++)
				for (int j = 0; j < h; j++) {
					grid[i][j].height = (int) (Math.random() * 4);
				}
		}
	}

	private void initemptyGrid() {
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++) {
				grid[i][j] = new SimpleToppler();
			}

		// first we fill the interior.
		for (int i = 1; i < grid.length - 1; i++)
			for (int j = 1; j < grid[i].length - 1; j++) {
				grid[i][j].addNeighbor(grid[i - 1][j]);
				grid[i][j].addNeighbor(grid[i + 1][j]);
				grid[i][j].addNeighbor(grid[i][j - 1]);
				grid[i][j].addNeighbor(grid[i][j + 1]);
			}
		// then the border on the lines
		for (int j = 1; j < grid[0].length - 1; j++) {
			grid[0][j].addNeighbor(dummy);
			grid[0][j].addNeighbor(grid[0][j - 1]);
			grid[0][j].addNeighbor(grid[0][j + 1]);
			grid[0][j].addNeighbor(grid[1][j]);
			grid[grid.length - 1][j].addNeighbor(dummy);
			grid[grid.length - 1][j].addNeighbor(grid[grid.length - 1][j - 1]);
			grid[grid.length - 1][j].addNeighbor(grid[grid.length - 1][j + 1]);
			grid[grid.length - 1][j].addNeighbor(grid[grid.length - 2][j]);
		}
		for (int i = 1; i < grid.length - 1; i++) {
			grid[i][0].addNeighbor(dummy);
			grid[i][0].addNeighbor(grid[i - 1][0]);
			grid[i][0].addNeighbor(grid[i + 1][0]);
			grid[i][0].addNeighbor(grid[i][1]);
			grid[i][grid[i].length - 1].addNeighbor(dummy);
			grid[i][grid[i].length - 1].addNeighbor(grid[i - 1][0]);
			grid[i][grid[i].length - 1].addNeighbor(grid[i + 1][0]);
			grid[i][grid[i].length - 1]
					.addNeighbor(grid[i][grid[i].length - 2]);
		}
		// and now the four edges
		grid[0][0].addNeighbor(dummy);
		grid[0][0].addNeighbor(dummy);
		grid[0][0].addNeighbor(grid[1][0]);
		grid[0][0].addNeighbor(grid[0][1]);
		grid[grid.length - 1][0].addNeighbor(dummy);
		grid[grid.length - 1][0].addNeighbor(dummy);
		grid[grid.length - 1][0].addNeighbor(grid[grid.length - 2][0]);
		grid[grid.length - 1][0].addNeighbor(grid[grid.length - 1][1]);
		grid[0][grid[0].length - 1].addNeighbor(dummy);
		grid[0][grid[0].length - 1].addNeighbor(dummy);
		grid[0][grid[0].length - 1].addNeighbor(grid[1][grid[0].length - 1]);
		grid[0][grid[0].length - 1].addNeighbor(grid[0][grid[0].length - 2]);
		grid[grid.length - 1][grid[0].length - 1].addNeighbor(dummy);
		grid[grid.length - 1][grid[0].length - 1].addNeighbor(dummy);
		grid[grid.length - 1][grid[0].length - 1]
				.addNeighbor(grid[grid.length - 2][grid[0].length - 1]);
		grid[grid.length - 1][grid[0].length - 1]
				.addNeighbor(grid[grid.length - 1][grid[0].length - 2]);
	}

	@SuppressWarnings("unchecked")
	public void addGrainAndTopple(int px, int py) {
		if (!toGrain.isEmpty())
			throw new IllegalArgumentException(" Programming mistake");
		if (px < 0 || py < 0 || px > width || py > height)
			throw new IndexOutOfBoundsException(
					" some wrong dimension in graining.");
		toGrain.add(grid[px][py]);
		while (!toGrain.isEmpty()) {
			AbstractNode v = toGrain.remove(0);
			if (v != null) {
				toGrain.addAll((Vector<AbstractNode>) v.addGrain().clone());
			} else {
				System.out.println("found null in grainlist");
			}
		}
	}

	public AbstractNode[][] getRealizationAsArray() {
		return grid;
	}

	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return new DrawLatticeGraph(fr, this, true);
	}
}
