package models;

import main.MainFrame;
import drawer.DrawMineSweeper;
import drawer.Outputapplet;
import java.awt.Dimension;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class MineSweeper extends AbstractMathModel {
	public boolean[][] cleared, mine, marked;
	public byte[][] neighbormines;
	public double mineprob;
	public int nrmarked, nrcleared;
	public ArrayList<Dimension> inCleaning;

	public MineSweeper(int w, int h, double prob) {
		cleared = new boolean[w][h];
		mine = new boolean[w][h];
		marked = new boolean[w][h];
		neighbormines = new byte[w][h];
		mineprob = prob;
		nrmarked = 0;
		nrcleared = 0;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				mine[i][j] = (Math.random() < prob);
				cleared[i][j] = false;
				marked[i][j] = false;
			}
		}
		// prepare the cleaning, if we have a mine next to the startpoint
		// we can not continue, so we exclude the case
		mine[w / 2][h / 2] = false;
		mine[w / 2 + 1][h / 2 - 1] = false;
		mine[w / 2 + 1][h / 2] = false;
		mine[w / 2 + 1][h / 2 + 1] = false;
		mine[w / 2][h / 2 + 1] = false;
		mine[w / 2][h / 2 - 1] = false;
		mine[w / 2 - 1][h / 2 + 1] = false;
		mine[w / 2 - 1][h / 2 - 1] = false;
		mine[w / 2 - 1][h / 2] = false;
		// cleared[cleared.length / 2][cleared[0].length / 2] = true;
		// nrcleared++;
		inCleaning = new ArrayList<Dimension>();
		inCleaning
				.add(new Dimension(cleared.length / 2, cleared[0].length / 2));
		// remember nr of neighboring mines (it is faster doing it only once
		// here.
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				neighbormines[i][j] = getNrInNeighborhood(i, j, mine);
			}
		}
	}

	public void checkNextPoint() {
		Dimension d = inCleaning.remove(0);
		if (!cleared[d.width][d.height]) {
			nrcleared++;
			cleared[d.width][d.height] = true;
			addAllCleantoReClean(getNeighbors(d.width, d.height));
			if (mine[d.width][d.height])
				System.out.println("BOOOM");
		}
		if (nrOfmissingMines(d.width, d.height) == 0) {
			ArrayList<Dimension> n = this.getNeighbors(d.width, d.height);
			for (int l = 0; l < n.size(); l++) {
				if ((!marked[n.get(l).width][n.get(l).height])
						&& (!cleared[n.get(l).width][n.get(l).height])) {
					addNewToCleaning(n.get(l));
				}
			}
		} else if (nrOfUnknownPoints(d.width, d.height) == 0) {
			// if this is true then everything that is not cleared around this
			// point is a bomb.
			ArrayList<Dimension> n = this.getNeighbors(d.width, d.height);
			for (int l = 0; l < n.size(); l++) {
				if ((!marked[n.get(l).width][n.get(l).height])
						&& (!cleared[n.get(l).width][n.get(l).height])) {
					marked[n.get(l).width][n.get(l).height] = true;
					nrmarked++;
					addAllCleantoReClean(getNeighbors(n.get(l).width,
							n.get(l).height));
				}
			}
		}
	}

	public void addNewToCleaning(Dimension l) {

		if (!inCleaning.contains(l)) {
			if (marked[l.width][l.height]) {
				System.out.println("tried to add a marked mine");
			} else if (mine[l.width][l.height]) {
				System.out.println("tried to add a unmarked mine");
			} else if (!(cleared[l.width][l.height] && (nrOfmissingMines(
					l.width, l.height) == 0)))
				inCleaning.add(l);
		}
	}

	public void addAllCleantoReClean(ArrayList<Dimension> l) {
		for (int i = 0; i < l.size(); i++) {
			if (cleared[l.get(i).width][l.get(i).height]) {
				if (nrOfNotStatedPositions(l.get(i).width, l.get(i).height) != 0) {
					if (!inCleaning.contains(l.get(i))) {
						inCleaning.add(l.get(i));
					}
				}
			}
		}

	}

	public byte getNrInNeighborhood(int i, int j, boolean[][] a) {
		byte c = 0;
		ArrayList<Dimension> n = getNeighbors(i, j);
		for (int l = 0; l < n.size(); l++) {
			if (a[n.get(l).width][n.get(l).height])
				c++;
		}
		return c;
	}

	public byte nrOfmissingMines(int i, int j) {
		// return (byte) (getNrInNeighborhood(i, j, mine)-
		// getNrInNeighborhood(i, j, marked));
		return (byte) (neighbormines[i][j] - getNrInNeighborhood(i, j, marked));
	}

	public byte nrOfUnknownPoints(int i, int j) {
		return (byte) (getNeighbors(i, j).size()
				- getNrInNeighborhood(i, j, cleared)
		// - getNrInNeighborhood(i, j, mine));
		- neighbormines[i][j]);
	}

	public byte nrOfNotStatedPositions(int i, int j) {
		return (byte) (getNeighbors(i, j).size()
				- getNrInNeighborhood(i, j, cleared) - getNrInNeighborhood(i,
				j, marked));
	}

	public ArrayList<Dimension> getNeighbors(int i, int j) {
		ArrayList<Dimension> n = new ArrayList<Dimension>();
		if (i > 0) {
			n.add(new Dimension(i - 1, j));
			if (j > 0)
				n.add(new Dimension(i - 1, j - 1));
			if (j < cleared[i].length - 1)
				n.add(new Dimension(i - 1, j + 1));
		}
		if (j > 0)
			n.add(new Dimension(i, j - 1));
		if (j < cleared[0].length - 1)
			n.add(new Dimension(i, j + 1));
		if (i < cleared.length - 1) {
			n.add(new Dimension(i + 1, j));
			if (j > 0)
				n.add(new Dimension(i + 1, j - 1));
			if (j < cleared[i].length - 1)
				n.add(new Dimension(i + 1, j + 1));
		}
		return n;
	}

	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return new DrawMineSweeper(fr, this, true);
	}

}
