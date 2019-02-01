package models;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import drawer.Outputapplet;

import utils.SAWgens;

import main.MainFrame;

/**
 * With this class we draw memory walks in 2-D in the central frame.
 * 
 * @author Robert Fitzner
 * 
 */
@SuppressWarnings("serial")
public class RandomWalks extends AbstractMathModel {
	// sequence of walks, each walk is saved as a sequence of directions
	public int[][] sequenceOfWalks;
	// we draw a number of different walks at once, the following saves
	// the number of steps of the i-th walk
	public int[] steps;
	// the memory the i-th walk
	public int[] memory;

	public RandomWalks(int w, int s, int m) {
		super();
		steps = new int[w];
		memory = new int[w];

		sequenceOfWalks = new int[w][];
		for (int i = 0; i < w; i++) {
			steps[i] = s;
			memory[i] = m;
			sequenceOfWalks[i] = SAWgens.generateaMSAW(m, s);
		}
	}

	/**
	 * This constructor is used when we draw multiple walks with different sizes
	 * and/or memory.
	 * 
	 * @param s
	 *            array with the different number of steps
	 * @param m
	 *            different memories
	 * @param w
	 *            and the number of walks we want to draw
	 */
	public RandomWalks(int w, int[] s, int[] m) {
		super();
		steps = new int[w];
		memory = new int[w];

		sequenceOfWalks = new int[w][];
		for (int i = 0; i < w; i++) {
			steps[i] = s[i];
			memory[i] = m[i];
			sequenceOfWalks[i] = SAWgens.generateaMSAW(m[i], s[i]);
		}
	}

	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		// TODO Auto-generated method stub
		return null;
	}
}
