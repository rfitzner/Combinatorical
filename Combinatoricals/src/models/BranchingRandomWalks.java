package models;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.LinkedList;
import java.util.ListIterator;

import drawer.DrawBranchingRandomWalks;
import drawer.Outputapplet;

import utils.SAWgens;

import main.MainFrame;

/**
 * Implementation of a Branching random walk on ZxZ with binomial offspring
 * distribution.
 * 
 * @author Robert Fitzner
 * 
 */
@SuppressWarnings("serial")
public class BranchingRandomWalks extends AbstractMathModel {
	// sequence of walks, each walk is saved as a sequence of directions
	public Particle root;;
	public double intensity;
	public int maxx, maxy, minx, miny, steps, nrGenerations, minNumber, progeny;

	private BranchingRandomWalks(double intensitv, int nrsteps, int n) {
		super();
		intensity = intensitv;
		steps = nrsteps;
		minNumber = n;
	}

	public static BranchingRandomWalks createBranchingRandomWalks(double intensitv, int nrsteps, int min) {
		BranchingRandomWalks m = new BranchingRandomWalks(intensitv, nrsteps, min);
		m.createNewWalk();
		return m;
	}

	private void createNewWalk() {
		int c = 0;
		do {
			boolean cont = true;
			nrGenerations = 0;
			progeny = 1;
			maxx = 1;
			maxy = 1;
			minx = -1;
			miny = -1;
			root = new Particle(null, new Dimension(0, 0), this);
			LinkedList<Particle> currentGeneration = new LinkedList<Particle>();
			currentGeneration.add(root);
			while (cont) {
				LinkedList<Particle> nextGeneration = new LinkedList<Particle>();
				ListIterator<Particle> it = currentGeneration.listIterator();
				while (it.hasNext()) {
					nextGeneration.addAll(it.next().createOffspring(intensity));
				}
				progeny += nextGeneration.size();
				nrGenerations++;
				if ((nrGenerations > steps) || (nextGeneration.isEmpty())) {
					cont = false;
				} else {
					currentGeneration = nextGeneration;
				}

			}
		} while ((c < 100) && (progeny < this.minNumber));

	}

	protected void resetMax(Dimension d) {
		maxx = Math.max(maxx, d.width);
		maxy = Math.max(maxy, d.height);
		minx = Math.min(minx, d.width);
		miny = Math.min(miny, d.height);
	}

	public static int getBinomial(int n, double p) {
		int x = 0;
		double logq = Math.log(1.0 - p);
		double sum = 0;
		while (true) {
			sum += Math.log(Math.random()) / (n - x);
			if (sum < logq) {
				System.out.println("got "+n+" and "+p+" returned "+x);
				return x;
			}
			x++;
		}
	}

	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return new DrawBranchingRandomWalks(fr, this, true);
	}

	public class Particle {
		BranchingRandomWalks envoirment;
		public Particle parent;
		public Dimension location;
		public LinkedList<Particle> children;

		public Particle(Particle p, Dimension l, BranchingRandomWalks e) {
			this.parent = p;
			this.location = l;
			this.envoirment = e;
			children = new LinkedList<Particle>();
			BranchingRandomWalks envoirment = e;
			envoirment.resetMax(location);
		}

		public LinkedList<Particle> createOffspring(double intensity) {
			int nrOfChildren = getBinomial(4, intensity);
			if (nrOfChildren > 0) {
				for (int i = 0; i < nrOfChildren; i++) {
					children.add(new Particle(this, this.getRandomNeighbor(), this.envoirment));
				}
			}
			return children;
		}

		private Dimension getRandomNeighbor() {
			double seed = Math.random();
			if (seed < 0.25)
				return new Dimension(this.location.width + 1, this.location.height);
			if (seed < 0.5)
				return new Dimension(this.location.width - 1, this.location.height);
			if (seed < 0.75)
				return new Dimension(this.location.width, this.location.height + 1);
			else
				return new Dimension(this.location.width, this.location.height - 1);
		}
	}
}
