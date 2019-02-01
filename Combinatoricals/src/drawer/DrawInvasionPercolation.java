package drawer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.MainFrame;
import models.InvasionPercolation;
import models.InvasionPercolation.Edge;

/**
 * This class generates and displays invasion percolation on a finite 2-D grid.
 * Thereby each edge gets a random weight (uniform on (0,1] ). The we start
 * invasion percolation: let C(0) be the cluster connected to and N(0) be the
 * set of edges that are directly connected to points of C(0) but are not part
 * of C(0) In each step we add with edge in N(0) to C(0) that has the minimal
 * weight of all the edges in N(0) We stop this after a given number of steps.
 * Mathematically interesting is the behavior when we would be able to proceed
 * this for a infinite number of steps.
 * 
 * @author Robert Fitzner
 */
public class DrawInvasionPercolation extends Outputapplet {
	private static final long serialVersionUID = 1L;

	BufferedImage bi;
	int delay;
	boolean animate;

	public DrawInvasionPercolation(MainFrame fr) {
		this(fr, new InvasionPercolation(200, 0), 0, true);
	}

	public DrawInvasionPercolation(MainFrame fr, InvasionPercolation m,
			int delay, boolean initparam) {
		model = m;
		frame = fr;
		drawnlasttime = 0;
		this.delay = delay;
		animate = true;
		if (initparam)
			initaliseParameterPanel();
	}

	@Override
	public void initaliseParameterPanel() {
		frame.setTitle("Invasion percolation");
		// Reset upper part, where parameters are entered and the bottoms are
		frame.remove(frame.parameterentry);
		frame.parameterentry = new JPanel(new GridLayout(2, 4));

		frame.parameterentry.add(new JLabel("Number of edge to invade"));
		frame.parameterentry.add(new JLabel("Delay pond(ms)"));

		frame.helpbutton = new JButton(MainFrame.askforhelp);
		frame.parameterentry.add(frame.helpbutton);
		frame.helpbutton.addActionListener(frame.listener);
		frame.helpbutton.setActionCommand(MainFrame.askforhelp);

		frame.inputs = new JTextField[2];
		frame.inputs[0] = new JTextField(""
				+ ((InvasionPercolation) model).numberOfFinalEdges);
		frame.inputs[1] = new JTextField(""
				+ ((InvasionPercolation) model).delaypond);
		frame.parameterentry.add(frame.inputs[0]);
		frame.parameterentry.add(frame.inputs[1]);
		for (int i = 0; i < frame.inputs.length; i++) {
			frame.inputs[i].setFont(new Font("Arial", Font.ROMAN_BASELINE
					| Font.PLAIN, 22));
		}

		JButton generatebutton = new JButton("Generate new");
		generatebutton.setMnemonic(KeyEvent.VK_G);
		generatebutton.setActionCommand(MainFrame.gostring);
		generatebutton.addActionListener(frame.listener);
		frame.parameterentry.add(generatebutton);

		frame.add(frame.parameterentry, BorderLayout.NORTH);
	}

	public void paint(Graphics g) {
		if ((bi != null) && !animate) {
			// if we only have a small change in comparison to the
			// last image we just rescale the image
			double difference = Math.max(Math.abs(((getSize().height - bi
					.getHeight()) * 1.0 / Math.min(bi.getHeight(),
					getSize().height))), Math.abs(((getSize().width - bi
					.getWidth()) * 1.0 / Math.min(getSize().width,
					bi.getWidth()))));
			if ((difference < 0.4)) {
				g.drawImage(bi.getScaledInstance(getSize().width,
						getSize().height, Image.SCALE_DEFAULT), 0, 0, null);
				drawnlasttime = System.currentTimeMillis();
				return;
			}
		}
		if (animate) {
			paint(getGraphics(), this.getSize());
		} else {
			// otherwise we create new images.
			bi = new BufferedImage(getSize().width, getSize().height,
					BufferedImage.TYPE_BYTE_INDEXED);

			paint(bi.getGraphics(), this.getSize());
			g.drawImage(bi, 0, 0, null);
		}
	}

	public void paint(Graphics g, Dimension d) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// get the size of the frame to scale the displaying and initialize the
		// background
		InvasionPercolation m = (InvasionPercolation) model;
		g.setColor(bg);
		g2.fillRect(0, 0, d.width, d.height);
		// add my name to the lower right corner
		g2.setColor(new Color(50, 50, 50));
		g2.setFont(new Font("Arial", Font.ITALIC | Font.PLAIN, 22));
		g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
		g.setColor(fg);
		// prepare output for the outlet highs
		g2.setFont(new Font("Arial", Font.ITALIC | Font.PLAIN, 18));
		g2.setColor(new Color(0, 0, 0));
		g2.drawString(" Height of Outlets:", d.width - 180, 20);
		int activeDrawcolor = 0;
		// an edge will have the following length
		double gridsizeX = (d.width - 20) * 1.0 / (m.maxx - m.minx);
		double gridsizeY = (d.height - 20) * 1.0 / (m.maxy - m.miny);
		// position of the origin
		double centerX = gridsizeX * (-m.minx);
		double centerY = gridsizeY * (-m.miny);
		activeDrawcolor = -1;
		// Now we draw the invaded edges in the order as computed.
		// Thereby we color the different ponds in different colors
		for (int k = 0; k < m.pondsize.size() - 2; k++) {
			activeDrawcolor = (activeDrawcolor + 1) % colors.length;
			g2.setColor(colors[activeDrawcolor]);
			double outlethight = Math
					.round(m.outlets.get(activeDrawcolor).bondp * 1000) * 1.0 / 1000;
			g2.drawString((new Double(outlethight)).toString(), d.width - 80,
					50 + 30 * activeDrawcolor);
			for (int i = m.pondsize.get(k).intValue(); i < m.pondsize
					.get(k + 1).intValue(); i++) {
				Edge e = m.algorithmicOrder.get(i);
				g2.draw(new Line2D.Double(e.x * gridsizeX + centerX, e.y
						* gridsizeY + centerY, (e.x + e.dx) * gridsizeX
						+ centerX, (e.y + e.dy) * gridsizeY + centerY));
			}
			try {
				if (animate)
					Thread.sleep(delay);
			} catch (InterruptedException ed) {
			}
		}
		animate = false;
	}

	@Override
	public String getType() {
		return MainFrame.invasionperc;
	}

	private Object[] readInput(int max1, int max2) {
		int in1 = utils.SmallTools.giveInteger(frame.inputs[0].getText());
		int in2 = utils.SmallTools.giveInteger(frame.inputs[1].getText());

		boolean correctinput = true;
		String newLine = System.getProperty("line.separator");
		StringBuffer output = new StringBuffer();
		output.append("Input Error" + newLine);

		if (in1 == Integer.MAX_VALUE) {
			output.append("The input for the number of edges is not an integer: "
					+ frame.inputs[0].getText() + newLine);
			correctinput = false;
		} else {
			if ((in1 > max1) || (in1 < 0)) {
				correctinput = false;
				output.append("Number of edges is to big form memory."
						+ newLine);
			}
		}

		if (in2 == Integer.MAX_VALUE) {
			output.append("The input for delay is not an integer: "
					+ frame.inputs[1].getText() + newLine);
			correctinput = false;
		} else {
			if ((in2 > max2) || (in2 < 0)) {
				correctinput = false;
				output.append("The delay for pond is to long (1 minute?)."
						+ newLine);
			}
		}

		if (correctinput) {
			Object[] values = new Object[3];
			values[0] = new Integer(in1);
			values[1] = new Integer(in2);
			return values;
		}
		Object[] error = new Object[1];
		error[0] = output.toString();
		return error;

	}

	@Override
	public void generateNew() {
		// Object[] input = readInput((int) Math.pow(10, 7), 60000);
		Object[] input = readInput((int) (2 * Math.pow(10, 6)), 60000);
		if (input.length != 1) {
			frame.remove(frame.center);
			int in1 = ((Integer) input[0]).intValue();
			int in2 = ((Integer) input[1]).intValue();
			this.model = null;
			DrawInvasionPercolation newSim = new DrawInvasionPercolation(frame,
					new InvasionPercolation(in1, in2), 0, false);
			frame.center = newSim;
			frame.add(frame.center, BorderLayout.CENTER);
			newSim.animate = true;
			this.setVisible(false);
		} else {
			JOptionPane
					.showMessageDialog(frame, ((String) input[0]).toString());
		}
		frame.validate();
	}

	@Override
	public void reDraw() {
		animate = true;
		this.paint(getGraphics());
	}

	@Override
	public String getHelpText() {
		String newLine = System.getProperty("line.separator");
		String content = "In this part we see an simulation of first steps of invasion percolation."
				+ newLine
				+ newLine
				+ "The model:"
				+ newLine
				+ "We start with a finite grid of points where each edge between to neighboring"
				+ newLine
				+ "points gets a weight that is given by the realisation of a unifrom [0,1] random"
				+ newLine
				+ "variable. We begin by taking the edges with the lowest weight connected to"
				+ newLine
				+ "the origin and add this to our cluster. Then we look at the six edges that are "
				+ newLine
				+ "connected to this edge and add the one with the smallest weight to our cluster."
				+ newLine
				+ "Continuing in this way we add at each step the edge the with smallest weight that"
				+ newLine
				+ "is connected to our present cluster. We continue this until we add the given"
				+ newLine
				+ "number of edges."
				+ newLine
				+ newLine
				+ "The created cluster separate in a natural way at a small number of"
				+ newLine
				+ "pivotal edges(outlet). We mark the parts(ponds) of the cluster that are sperated"
				+ newLine
				+ "by these pivotal edges in different colors and give the weight of these"
				+ newLine
				+ "outlets-pivotal edges. "
				+ newLine
				+ newLine
				+ "To return to the program press the -Back- Button, that has replaced, "
				+ newLine
				+ "the help button or select a new part of the program";
		return content;
	}

	@Override
	public void generateNewForFile(File file, String type) {
		Object[] input = readInput((int) Math.pow(10, 9), 000);
		if (input.length != 1) {
			try {
				frame.remove(frame.center);
				int in1 = ((Integer) input[0]).intValue();
				int in2 = ((Integer) input[1]).intValue();
				DrawInvasionPercolation newSim = new DrawInvasionPercolation(
						frame, new InvasionPercolation(in1, in2), 0, false);
				InvasionPercolation m = ((InvasionPercolation) newSim.model);
				int w = (m.maxx - m.minx) * 3;
				int h = (m.maxy - m.miny) * 3;
				BufferedImage locbi = new BufferedImage(w, h,
						BufferedImage.TYPE_BYTE_INDEXED);
				Graphics2D ig2 = locbi.createGraphics();
				newSim.paint(ig2, new Dimension(w, h));

				newSim.destroy();
				javax.imageio.ImageIO.write(locbi, type, file);
				locbi = new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_INDEXED);
				frame.add(frame.center, BorderLayout.CENTER);
				JOptionPane.showMessageDialog(frame,
						"Saving of a new simulation as picture is completed.");

			} catch (java.io.IOException ie) {
				JOptionPane.showMessageDialog(frame, ie.toString());
			} catch (java.lang.OutOfMemoryError e) {
				JOptionPane.showMessageDialog(frame,
						"You have just blown your memory.");
			}
		} else {
			JOptionPane
					.showMessageDialog(frame, ((String) input[0]).toString());
		}
	}

	@Override
	public void saveCurrentlyShownToFile(File file, String type) {
		try {
			InvasionPercolation m = ((InvasionPercolation) model);

			int w = (m.maxx - m.minx) * 3;
			int h = (m.maxy - m.miny) * 3;
			BufferedImage locbi = new BufferedImage(w, h,
					BufferedImage.TYPE_BYTE_INDEXED);
			Graphics2D ig2 = locbi.createGraphics();
			this.paint(ig2, new Dimension(w, h));
			javax.imageio.ImageIO.write(locbi, type, file);
			locbi = new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_INDEXED);
			JOptionPane.showMessageDialog(frame,
					"Saving as picture is completed.");
		} catch (java.io.IOException ie) {
			JOptionPane.showMessageDialog(frame, ie.toString());
		}

	}

	@Override
	public void continueSim() {
		// Dummy
	}
}