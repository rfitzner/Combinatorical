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

import utils.SmallTools;

import main.MainFrame;
import models.PercolationClusterWithWalkOnIt;

/**
 * We first generate a 2-dimensional percolation cluster with of at most at most
 * maxSizeCluster edges. Thereby we built is so that the underlying system has
 * bond probability p. Then we start on this already explored Cluster a simple
 * random walk with a given number of steps. DONE
 * 
 * @author Robert Fitzner
 */
public class DrawWalkOnPerc extends Outputapplet {
	private static final long serialVersionUID = 1L;

	BufferedImage bi;
	int delay;
	boolean animate;

	public DrawWalkOnPerc(MainFrame fr) {
		this(fr, new PercolationClusterWithWalkOnIt(0.6, 1000, 3000), 0, true);
	}

	public DrawWalkOnPerc(MainFrame fr, PercolationClusterWithWalkOnIt m,
			int delay, boolean initpanel) {
		model = m;
		frame = fr;
		delay = 0;
		animate = true;
		if (initpanel)
			initaliseParameterPanel();
	}

	@Override
	public void initaliseParameterPanel() {
		frame.setTitle("Walk on a percolation cluster");
		// Reset upper part, where parameters are entered and the bottoms are
		frame.remove(frame.parameterentry);
		frame.parameterentry = new JPanel(new GridLayout(2, 5));

		frame.parameterentry.add(new JLabel("Edge probability"));
		frame.parameterentry.add(new JLabel("Max cluster size"));
		frame.parameterentry.add(new JLabel("Number of steps"));

		JButton redrawbottom = new JButton(MainFrame.redraw);
		frame.parameterentry.add(redrawbottom);
		redrawbottom.addActionListener(frame.listener);
		redrawbottom.setActionCommand(MainFrame.redraw);

		frame.helpbutton = new JButton(MainFrame.askforhelp);
		frame.parameterentry.add(frame.helpbutton);
		frame.helpbutton.addActionListener(frame.listener);
		frame.helpbutton.setActionCommand(MainFrame.askforhelp);

		frame.inputs = new JTextField[3];
		frame.inputs[0] = new JTextField(""
				+ ((PercolationClusterWithWalkOnIt) model).bprob);
		frame.inputs[1] = new JTextField(""
				+ ((PercolationClusterWithWalkOnIt) model).maxSizeCluster);
		frame.inputs[2] = new JTextField(""
				+ ((PercolationClusterWithWalkOnIt) model).steps);
		for (int i = 0; i < frame.inputs.length; i++) {
			frame.inputs[i].setFont(new Font("Arial", Font.ROMAN_BASELINE
					| Font.PLAIN, 22));
		}

		frame.parameterentry.add(frame.inputs[0]);
		frame.parameterentry.add(frame.inputs[1]);
		frame.parameterentry.add(frame.inputs[2]);

		frame.parameterentry.add(new JLabel());
		JButton generatebutton = new JButton("Generate new");
		generatebutton.setMnemonic(KeyEvent.VK_G);
		generatebutton.setActionCommand(MainFrame.gostring);
		generatebutton.addActionListener(frame.listener);
		frame.parameterentry.add(generatebutton);

		frame.add(frame.parameterentry, BorderLayout.NORTH);
	}

	public void paint(Graphics g) {
		if (animate)
			this.paintAnimation(g);
		else {
			if (bi != null) {
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
		// get the size of the frame to scale the displaying and initialize
		// the background
		PercolationClusterWithWalkOnIt m = (PercolationClusterWithWalkOnIt) model;
		g.setColor(bg);
		g2.fillRect(0, 0, d.width, d.height);
		// add my name to the lower right corner
		g2.setColor(new Color(30, 30, 30));
		g2.setFont(new Font("Arial", Font.ITALIC | Font.PLAIN, 22));
		g2.drawString(" Robert Fitzner", d.width - 180, d.height - 10);
		g.setColor(fg);
		g2.setFont(new Font("Arial", Font.ITALIC | Font.PLAIN, 18));
		g2.setColor(new Color(30, 30, 30));
		// an edge will have the following lenght
		double gridsizeX = (d.width - 20) * 1.0 / (m.edges.length);
		double gridsizeY = (d.height - 20) * 1.0 / (m.edges[0].length);
		if ((m.edges.length == 1) && (m.edges.length == 1)) {
			g2.setColor(new Color(0, 0, 0));
			g2.drawString(
					" The inital percolation cluster is just an isolated vertex.",
					d.width / 3, d.height / 3);
		} else {
			for (int i = 0; i < m.edges.length; i++) {
				for (int j = 0; j < m.edges[i].length; j++) {
					if (m.edges[i][j][0]) {
						g2.draw(new Line2D.Double(i * gridsizeX + 10, j
								* gridsizeY + 10, (i + 1) * gridsizeX + 10, j
								* gridsizeY + 10));
					}
					if (m.edges[i][j][1]) {
						g2.draw(new Line2D.Double(i * gridsizeX + 10, j
								* gridsizeY + 10, i * gridsizeX + 10, (j + 1)
								* gridsizeY + 10));
					}
				}
			}
			SmallTools.drawThickBall(g2, m.path[0].width * gridsizeX + 10,
					m.path[0].height * gridsizeY + 10, 8, red);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			g2.setColor(new Color(0, 0, 255));
			for (int i = 0; i < m.path.length - 1; i++) {
				g2.draw(new Line2D.Double(m.path[i].width * gridsizeX + 10,
						m.path[i].height * gridsizeY + 10, m.path[i + 1].width
								* gridsizeX + 10, m.path[i + 1].height
								* gridsizeY + 10));
			}
		}
	}

	public void paintAnimation(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// get the size of the frame to scale the displaying and initialize
		// the background
		PercolationClusterWithWalkOnIt m = (PercolationClusterWithWalkOnIt) model;
		Dimension d = getSize();
		g.setColor(bg);
		g2.fillRect(0, 0, d.width, d.height);
		// add my name to the lower right corner
		g2.setColor(new Color(200, 200, 200));
		g2.setFont(new Font("Arial", Font.ITALIC | Font.PLAIN, 22));
		g2.drawString(" Robert Fitzner", d.width - 180, d.height - 10);
		g.setColor(fg);
		g2.setFont(new Font("Arial", Font.ITALIC | Font.PLAIN, 18));
		g2.setColor(new Color(100, 100, 100));
		// an edge will have the following lenght
		double gridsizeX = (d.width - 20) * 1.0 / (m.edges.length);
		double gridsizeY = (d.height - 20) * 1.0 / (m.edges[0].length);
		if ((m.edges.length == 1) && (m.edges.length == 1)) {
			g2.setColor(new Color(0, 0, 0));
			g2.drawString(
					" The inital percolation cluster is just an isolated vertex.",
					d.width / 3, d.height / 3);
		} else {
			for (int i = 0; i < m.edges.length; i++) {
				for (int j = 0; j < m.edges[i].length; j++) {
					if (m.edges[i][j][0]) {
						g2.draw(new Line2D.Double(i * gridsizeX + 10, j
								* gridsizeY + 10, (i + 1) * gridsizeX + 10, j
								* gridsizeY + 10));
					}
					if (m.edges[i][j][1]) {
						g2.draw(new Line2D.Double(i * gridsizeX + 10, j
								* gridsizeY + 10, i * gridsizeX + 10, (j + 1)
								* gridsizeY + 10));
					}
				}
			}
			SmallTools.drawThickBall(g2, m.path[0].width * gridsizeX + 10,
					m.path[0].height * gridsizeY + 10, 8, red);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			int headstart = (int) Math.sqrt(m.path.length) - 1;
			if (headstart > 0) {
				g2.setColor(red);
				for (int i = 0; i < headstart; i++) {
					g2.draw(new Line2D.Double(m.path[i].width * gridsizeX + 10,
							m.path[i].height * gridsizeY + 10,
							m.path[i + 1].width * gridsizeX + 10,
							m.path[i + 1].height * gridsizeY + 10));
				}
				for (int i = headstart; i < m.path.length - 1; i++) {
					g2.setColor(red);
					g2.draw(new Line2D.Double(m.path[i].width * gridsizeX + 10,
							m.path[i].height * gridsizeY + 10,
							m.path[i + 1].width * gridsizeX + 10,
							m.path[i + 1].height * gridsizeY + 10));
					g2.setColor(new Color(0, 0, 255));
					g2.draw(new Line2D.Double(m.path[i - headstart].width
							* gridsizeX + 10, m.path[i - headstart].height
							* gridsizeY + 10, m.path[i - headstart + 1].width
							* gridsizeX + 10, m.path[i - headstart + 1].height
							* gridsizeY + 10));

					try {
						Thread.sleep(delay / m.path.length);
					} catch (InterruptedException e) {
					}
					SmallTools.drawThickBall(g2, (i - headstart) * 1.0
							/ (m.path.length - headstart) * d.width, 0, 3,
							new Color(0, 255, 0));
				}
			}
		}
		animate = false;
	}

	@Override
	public String getType() {
		return MainFrame.walkOnPerc;
	}

	private Object[] readInput(int max2, int max3) {

		double in1 = SmallTools.giveDouble(frame.inputs[0].getText());
		int in2 = SmallTools.giveInteger(frame.inputs[1].getText());
		int in3 = SmallTools.giveInteger(frame.inputs[2].getText());

		boolean inputinrange = true;
		String errorstring = "";
		if ((in1 > 1) || (in1 < 0)) {
			errorstring += "Side Probability is not a propability."
					+ System.getProperty("line.separator");
			inputinrange = false;
		}
		if ((in2 > max2) || (in2 < 0)) {
			inputinrange = false;
			errorstring += "Cluster can be to big, you will blow up your memory. \n"
					+ System.getProperty("line.separator");
		}
		if ((in3 > max3) || (in3 < 0)) {
			inputinrange = false;
			errorstring += "Too many steps for the walker. \n";
		}
		if (inputinrange) {
			Object[] values = new Object[3];
			values[0] = new Double(in1);
			values[1] = new Integer(in2);
			values[2] = new Integer(in3);
			return values;
		} else {
			Object[] error = new Object[1];
			error[0] = errorstring.toString();
			return error;
		}

	}

	@Override
	public void generateNew() {
		Object[] input = this.readInput(500000, 2000000);

		if (input.length != 1) {
			double in1 = ((Double) input[0]).doubleValue();
			int in2 = ((Integer) input[1]).intValue();
			int in3 = ((Integer) input[2]).intValue();
			frame.remove(frame.center);
			this.makeSpace();
			DrawWalkOnPerc newSim = new DrawWalkOnPerc(frame,
					new PercolationClusterWithWalkOnIt(in1, in2, in3), 0, false);
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
	public void generateNewForFile(File file, String type) {
		Object[] input = this.readInput(5000000, 100000000);
		if (input.length == 1) {
			JOptionPane
					.showMessageDialog(frame, ((String) input[0]).toString());
		} else {
			try {
				double in1 = ((Double) input[0]).doubleValue();
				int in2 = ((Integer) input[1]).intValue();
				int in3 = ((Integer) input[2]).intValue();
				frame.remove(frame.center);
				this.makeSpace();
				DrawWalkOnPerc newSim = new DrawWalkOnPerc(frame,
						new PercolationClusterWithWalkOnIt(in1, in2, in3), 0,
						false);
				newSim.animate = false;
				int w = ((PercolationClusterWithWalkOnIt) newSim.model).edges.length * 3;
				int h = ((PercolationClusterWithWalkOnIt) newSim.model).edges[0].length * 3;
				BufferedImage bi = new BufferedImage(w, h,
						BufferedImage.TYPE_INT_RGB);
				Graphics2D ig2 = bi.createGraphics();
				newSim.paint(ig2, new Dimension(w, h));
				javax.imageio.ImageIO.write(bi, type, file);
				bi = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
				frame.add(frame.center, BorderLayout.CENTER);
				JOptionPane.showMessageDialog(frame,
						"Saving of a new simulation as picture is completed.");
				newSim.destroy();
			} catch (java.io.IOException ie) {
				JOptionPane.showMessageDialog(frame, ie.toString());
			} catch (java.lang.OutOfMemoryError e) {
				JOptionPane.showMessageDialog(frame,
						"You have just blown your memory.");
			}
		}
	}

	@Override
	public void reDraw() {
		animate = true;
		paint(getGraphics());
	}

	private void makeSpace() {
		model = null;
	}

	@Override
	public String getHelpText() {
		String newLine = System.getProperty("line.separator");
		String content = "In this part we see how a random walk moves on a two "
				+ newLine
				+ "dimensional percolation cluster."
				+ newLine
				+ newLine
				+ "The model"
				+ newLine
				+ "We consider the percolation cluster that is connected to the origin."
				+ newLine
				+ "We first simulate this cluster and then start a random walk at the"
				+ newLine
				+ "origin. At each step the walker chooses uniformly on of the connected"
				+ newLine
				+ "occupied edges for this next step. For the definition"
				+ newLine
				+ "of percolation see help at the corresponding part."
				+ newLine
				+ newLine
				+ "Parameters:"
				+ newLine
				+ "The user can give the edge probability of the percolation, the maximal "
				+ newLine
				+ " number of edges that we explore of the cluster and the"
				+ newLine
				+ "number of steps of the random walk. As a percolation cluster can"
				+ newLine
				+ "be infinite we need to give the program a maximal size at which"
				+ newLine
				+ "the exploration is stopped."
				+ newLine
				+ newLine
				+ "A note to the implementation:"
				+ newLine
				+ "We save the cluster using an two dimensional dynamic vector."
				+ newLine
				+ "So we only save edges that are actually explored."
				+ newLine
				+ "We explore the cluster by checking first which points are"
				+ newLine
				+ "connected to the origin and save them into an list."
				+ newLine
				+ "Then we take randomly one of the point in list and explore"
				+ newLine
				+ "its neighborhood and add all connected point to the list."
				+ newLine
				+ "We repeat this until we either explore the full cluster or "
				+ newLine
				+ "explored a given maximal number of points."
				+ newLine
				+ "After this is done we start the random walk."
				+ newLine
				+ "If the cluster is smaller then 100 egdes we think of it as to small "
				+ newLine
				+ "and will not start the walk."
				+ newLine
				+ newLine
				+ "To return to the program press the -Back- Button, that has replaced, "
				+ newLine
				+ "the help button or select a new part of the program";
		return content;
	}

	@Override
	public void saveCurrentlyShownToFile(File file, String type) {
		try {
			PercolationClusterWithWalkOnIt m = ((PercolationClusterWithWalkOnIt) model);
			int w = m.edges.length * 3;
			int h = m.edges[1].length * 3;

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
		// dummy
	}

}