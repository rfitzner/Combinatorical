package models;

import java.awt.image.BufferedImage;

import main.MainFrame;
import drawer.DrawPerc;
import drawer.DrawPercDev;
import drawer.Outputapplet;

@SuppressWarnings("serial")
public class PercolationConfigurationSequence extends AbstractMathModel {

	// parameters for the development
	public int wsize, hsize, numberOfFrames;
	public double initialBondProb, finalBondProb;
	public PercolationConfiguration[] percConfigurations;

	/**
	 * The constructor that creates does the math.
	 * 
	 * @param hs
	 * @param ws
	 * @param prob1
	 * @param prob2
	 * @param nrframe
	 * @param delay
	 */
	public PercolationConfigurationSequence(int ws, int hs, double prob1,
			double prob2, int nrframe) {
		super();
		wsize = ws;
		hsize = hs;
		initialBondProb = prob1;
		finalBondProb = prob2;
		numberOfFrames = nrframe;

		construct();
	}

	/**
	 * this method constructs the sequence of percolation configurations
	 */
	public void construct() {
		percConfigurations = new PercolationConfiguration[numberOfFrames];

		// the initial configuration
		percConfigurations[0] = new PercolationConfiguration(hsize, wsize,
				initialBondProb);
		percConfigurations[0].labelTheCluster();
		// increase progress as first cluster is finished
		for (int i = 1; i < numberOfFrames; i++) {
			// now we generate all following configuration by adding edge to the
			// former one
			percConfigurations[i] = new PercolationConfiguration(
					percConfigurations[i - 1], hsize, wsize, initialBondProb
							+ (finalBondProb - initialBondProb) * (i - 1)
							/ (numberOfFrames - 1), initialBondProb
							+ (finalBondProb - initialBondProb) * i
							/ (numberOfFrames - 1));
			// and update the progress
		}
	}

	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return new DrawPercDev(fr, this, 500, true);
	}

}
