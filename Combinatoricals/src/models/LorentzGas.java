package models;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;

import drawer.DrawLorentzGas;
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
public class LorentzGas extends AbstractMathModel {
	private static final long serialVersionUID = 1L;
	// probability that a edge is occupied
	public double atomprob, dirprob;
	// total time in which the walk is drawn
	public int maxsites, sites, minx, miny, maxx, maxy, tries;
	// To save memory we do not save the full grid but only those points that
	// we actually seen
	public HashMap<Integer, HashMap<Integer, boolean[]>> seenSites;
	// path of the random walk
	public ArrayList<Dimension> path;

	/**
	 * 
	 * @param size
	 *            number of steps the walk should take
	 * @param bondprob
	 *            the bond probability of the percolation
	 * @param delay1
	 *            delay at drawing the edges
	 */
	public LorentzGas(double prob1, double prob2, int maxs) {
		// INITIALISING
		atomprob = prob1;
		dirprob = prob2;
		maxsites = maxs;
		sites = 0;
		// and initials the variable for the path
		// add the neighbors of the center as possible next edges

		seenSites = new HashMap<Integer, HashMap<Integer, boolean[]>>();
		maxx = 0;
		maxy = 0;
		minx = 0;
		miny = 0;
		path = new ArrayList<Dimension>();

		Dimension dir = new Dimension(1, 0);
		Dimension former = new Dimension(0, 0);
		getStatus(0, 0);
		seenSites.get(new Integer(0)).get(new Integer(0))[0] = true;
		path.add(new Dimension(0, 0));
		boolean notfoundOrigin = true;
		for (int i = 0; path.size() < maxsites && sites < maxsites
				&& notfoundOrigin; i++) {
			int w = former.width + dir.width;
			int h = former.height + dir.height;
			boolean[] s = getStatus(w, h);
			while (s[0] == false) {
				w += dir.width;
				h += dir.height;
				s = getStatus(w, h);
			}
			if (dir.width == 1) {
				if (s[1])
					dir = new Dimension(0, 1);
				else
					dir = new Dimension(0, -1);
			} else if (dir.width == -1) {
				if (s[1])
					dir = new Dimension(0, -1);
				else
					dir = new Dimension(0, 1);
			} else if (dir.height == 1) {
				if (s[1])
					dir = new Dimension(1, 0);
				else
					dir = new Dimension(-1, 0);
			} else {
				if (s[1])
					dir = new Dimension(-1, 0);
				else
					dir = new Dimension(1, 0);
			}
			maxx = Math.max(maxx, w);
			minx = Math.min(minx, w);
			maxy = Math.max(maxy, h);
			miny = Math.min(miny, h);
			former = new Dimension(w, h);
			path.add(new Dimension(w, h));
			if ((w == 0) && (h == 0))
				notfoundOrigin = false;
		}
	}

	/**
	 * @param point
	 */
	private boolean[] getStatus(int w, int h) {
		if (!seenSites.containsKey(new Integer(w))) {
			seenSites.put(new Integer(w), new HashMap<Integer, boolean[]>());
		}

		if (!seenSites.get(new Integer(w)).containsKey(new Integer(h))) {
			// System.out.println("put "+w+","+h);
			boolean[] entry = new boolean[2];
			sites++;
			if (Math.random() < atomprob) {
				entry[0] = true;
				entry[1] = (Math.random() < dirprob);
			} else {
				entry[0] = false;
				entry[1] = false;
			}
			seenSites.get(new Integer(w)).put(new Integer(h), entry);
		}
		return seenSites.get(new Integer(w)).get(new Integer(h));
	}

	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return new DrawLorentzGas(fr, this, 1000, true);
	}

}