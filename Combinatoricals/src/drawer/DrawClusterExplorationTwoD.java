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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.MainFrame;
import models.ClusterExplorationTwoD;

/**
 * This class generates let a random walk explore a percolation cluster. So we
 * generate a simple random walks that only walks along occupied edges. Every
 * time the walk encounters a new edge we check whether the edge is occupied or
 * not then the walk proceeds.
 * 
 * @author Robert Fitzner
 */
public class DrawClusterExplorationTwoD extends Outputapplet {
	private static final long serialVersionUID = 1L;
	boolean showEverythingInitally, animate;
	JCheckBox box1, box2;
	BufferedImage bi;
	private boolean showOnlyExplored;

	public DrawClusterExplorationTwoD(MainFrame fr) {
		this(fr, new ClusterExplorationTwoD(2000, 0.6, false), true);
	}

	public DrawClusterExplorationTwoD(MainFrame fr, ClusterExplorationTwoD m,
			boolean initparam) {
		model = m;
		frame = fr;
		showEverythingInitally = true;
		animate = true;
		showOnlyExplored = false;
		if (initparam)
			initaliseParameterPanel();
	}

	@Override
	public void initaliseParameterPanel() {
		frame.setTitle("Exploration of a cluster in d=2");
		// Reset upper part, where parameters are entered and the bottoms are
		frame.remove(frame.parameterentry);
		frame.parameterentry = new JPanel(new GridLayout(2, 5));

		frame.parameterentry.add(new JLabel("Number of steps"));
		frame.parameterentry.add(new JLabel("Edge probability"));
		box1 = new JCheckBox("Retry trivial", true);
		frame.parameterentry.add(box1);

		frame.helpbutton = new JButton(MainFrame.askforhelp);
		frame.parameterentry.add(frame.helpbutton);
		frame.helpbutton.addActionListener(frame.listener);
		frame.helpbutton.setActionCommand(MainFrame.askforhelp);
		frame.inputs = new JTextField[2];
		frame.inputs[0] = new JTextField(""
				+ ((ClusterExplorationTwoD) model).steps);
		frame.inputs[1] = new JTextField(""
				+ ((ClusterExplorationTwoD) model).bondp);
		for (int i = 0; i < frame.inputs.length; i++) {
			frame.inputs[i].setFont(new Font("Arial", Font.ROMAN_BASELINE
					| Font.PLAIN, 22));
		}
		box2 = new JCheckBox("Show Everything", true);
		frame.parameterentry.add(frame.inputs[0]);
		frame.parameterentry.add(frame.inputs[1]);
		frame.parameterentry.add(box2);

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
		ClusterExplorationTwoD m = (ClusterExplorationTwoD) model;
		g.setColor(bg);
		g2.fillRect(0, 0, d.width, d.height);
		// add my name to the lower right corner
		g.setColor(fg);
		if (m.path.length == 1) {
			g2.drawString(" We began our walk at an isolatet node.",
					d.width / 3, d.height / 3);
		} else {
			double gridsizeX = (d.width - 20) * 1.0 / (m.maxx - m.minx + 2);
			double gridsizeY = (d.height - 20) * 1.0 / (m.maxy - m.miny + 2);
			double centerX = gridsizeX * (-m.minx + 1);
			double centerY = gridsizeY * (-m.miny + 1);
			g2.setColor(new Color(100, 100, 100));
			int exploredEdges = 0;
			// draw the explore Grid
			Iterator<Entry<Integer, HashMap<Integer, boolean[]>>> it1 = m.seenEdges
					.entrySet().iterator();
			while (it1.hasNext()) {
				Entry<Integer, HashMap<Integer, boolean[]>> entry = it1.next();
				int x = entry.getKey().intValue();
				Iterator<Entry<Integer, boolean[]>> it2 = entry.getValue()
						.entrySet().iterator();
				while (it2.hasNext()) {
					Entry<Integer, boolean[]> entry2 = it2.next();
					int y = entry2.getKey().intValue();
					boolean[] tmp = entry2.getValue();
					if (tmp[0]) {
						if (showEverythingInitally) {
							g2.draw(new Line2D.Double(x * gridsizeX + centerX,
									y * gridsizeY + centerY, (x + 1)
											* gridsizeX + centerX, y
											* gridsizeY + centerY));
						}
						exploredEdges++;
					}
					if (tmp[1]) {
						if (showEverythingInitally) {
							g2.draw(new Line2D.Double(x * gridsizeX + centerX,
									y * gridsizeY + centerY, x * gridsizeX
											+ centerX, (y + 1) * gridsizeY
											+ centerY));
						}
						exploredEdges++;
					}
				}

			}
			if (!showOnlyExplored) {
				int end = Math.min(m.path.length - 1, exploredEdges
						* exploredEdges);
				for (int i = 0; i < end; i++) {
					g2.setColor(red);
					g2.draw(new Line2D.Double(m.path[i].width * gridsizeX
							+ centerX, m.path[i].height * gridsizeY + centerY,
							m.path[i + 1].width * gridsizeX + centerX,
							m.path[i + 1].height * gridsizeY + centerY));
					g2.setColor(new Color(0, 0, 255));
					g2.draw(new Line2D.Double(m.path[i].width * gridsizeX
							+ centerX, m.path[i].height * gridsizeY + centerY,
							m.path[i + 1].width * gridsizeX + centerX,
							m.path[i + 1].height * gridsizeY + centerY));
					utils.SmallTools.drawThickBall(g2, i * 1.0 / m.path.length
							* d.width, 3, 3, new Color(0, 255, 0));
				}
			}
			g2.setColor(new Color(70, 70, 70));
			g2.setFont(new Font("Arial", Font.ITALIC | Font.PLAIN, 22));
			g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
			g2.drawString(" Number of explored edges = " + exploredEdges,
					d.width - 340, d.height - 35);
			if (box1.isSelected())
				g2.drawString(" Number of retries = " + m.tries, d.width - 240,
						d.height - 60);
		}
	}

	public void paintAnimation(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// get the size of the frame to scale the displaying and initialize
		// the background
		ClusterExplorationTwoD m = (ClusterExplorationTwoD) model;
		g.setColor(bg);
		Dimension d = getSize();
		g2.fillRect(0, 0, d.width, d.height);
		// add my name to the lower right corner
		g2.setColor(new Color(100, 100, 100));
		g2.setFont(new Font("Arial", Font.ITALIC | Font.PLAIN, 22));
		g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
		g.setColor(fg);
		if (m.path.length == 1) {
			g2.drawString(" We began our walk at an isolatet node.",
					d.width / 3, d.height / 3);
			if (box1.isSelected())
				g2.drawString(" Number of retries = " + m.tries, d.width - 300,
						d.height - 35);
		} else {
			double gridsizeX = (d.width - 20) * 1.0 / (m.maxx - m.minx + 2);
			double gridsizeY = (d.height - 20) * 1.0 / (m.maxy - m.miny + 2);
			double centerX = gridsizeX * (-m.minx + 1);
			double centerY = gridsizeY * (-m.miny + 1);
			g2.setColor(new Color(100, 100, 100));
			int exploredEdges = 0;
			// draw the explore Grid

			Iterator<Entry<Integer, HashMap<Integer, boolean[]>>> it1 = m.seenEdges
					.entrySet().iterator();
			while (it1.hasNext()) {
				Entry<Integer, HashMap<Integer, boolean[]>> entry = it1.next();
				int x = entry.getKey().intValue();
				Iterator<Entry<Integer, boolean[]>> it2 = entry.getValue()
						.entrySet().iterator();
				while (it2.hasNext()) {
					Entry<Integer, boolean[]> entry2 = it2.next();
					int y = entry2.getKey().intValue();
					boolean[] tmp = entry2.getValue();
					if (tmp[0]) {
						if (showEverythingInitally) {
							g2.draw(new Line2D.Double(x * gridsizeX + centerX,
									y * gridsizeY + centerY, (x + 1)
											* gridsizeX + centerX, y
											* gridsizeY + centerY));
						}
						exploredEdges++;
					}
					if (tmp[1]) {
						if (showEverythingInitally) {
							g2.draw(new Line2D.Double(x * gridsizeX + centerX,
									y * gridsizeY + centerY, x * gridsizeX
											+ centerX, (y + 1) * gridsizeY
											+ centerY));
						}
						exploredEdges++;
					}
				}
			}

			int headstart = (int) Math.sqrt(m.path.length) - 1;
			if (headstart > 0) {
				g2.setColor(red);
				for (int i = 0; i < headstart; i++) {
					g2.draw(new Line2D.Double(m.path[i].width * gridsizeX
							+ centerX, m.path[i].height * gridsizeY + centerY,
							m.path[i + 1].width * gridsizeX + centerX,
							m.path[i + 1].height * gridsizeY + centerY));
					utils.SmallTools.drawThickBall(g2, i * 1.0 / m.path.length
							* d.width, 3, 3, new Color(0, 255, 0));
				}
				int end = Math.min(m.path.length - 1, exploredEdges
						* exploredEdges);
				for (int i = headstart; i < end; i++) {
					g2.setColor(red);
					g2.draw(new Line2D.Double(m.path[i].width * gridsizeX
							+ centerX, m.path[i].height * gridsizeY + centerY,
							m.path[i + 1].width * gridsizeX + centerX,
							m.path[i + 1].height * gridsizeY + centerY));
					g2.setColor(new Color(0, 0, 255));
					g2.draw(new Line2D.Double(m.path[i - headstart].width
							* gridsizeX + centerX, m.path[i - headstart].height
							* gridsizeY + centerY,
							m.path[i + 1 - headstart].width * gridsizeX
									+ centerX, m.path[i + 1 - headstart].height
									* gridsizeY + centerY));
					// try {
					// Thread.sleep(m.totalDelay / steps);

					// } catch (InterruptedException ed) {
					// }
					utils.SmallTools.drawThickBall(g2, i * 1.0 / m.path.length
							* d.width, 0, 3, new Color(0, 255, 0));
				}
			}
			g2.setColor(new Color(70, 70, 70));
			g2.setFont(new Font("Arial", Font.ITALIC | Font.PLAIN, 22));
			g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
			g2.drawString(" Number of explored edges = " + exploredEdges,
					d.width - 340, d.height - 35);
			if (box1.isSelected())
				g2.drawString(" Number of retries = " + m.tries, d.width - 240,
						d.height - 60);
		}
		animate = false;
	}

	@Override
	public String getType() {
		return MainFrame.clusterExpTwoD;
	}

	private Object[] readInput(int max1) {
		int in1 = utils.SmallTools.giveInteger(frame.inputs[0].getText());
		double in2 = utils.SmallTools.giveDouble(frame.inputs[1].getText());

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
				output.append("To many steps for the walker." + newLine);
			}
		}

		if (in2 == Integer.MAX_VALUE) {
			output.append("The input for delay is not an integer: "
					+ frame.inputs[1].getText() + newLine);
			correctinput = false;
		} else {
			if ((in2 > 1) || (in2 < 0)) {
				correctinput = false;
				output.append("Input is not a probability." + newLine);
			}
		}

		if (correctinput) {
			Object[] values = new Object[3];
			values[0] = new Integer(in1);
			values[1] = new Double(in2);
			return values;
		}
		Object[] error = new Object[1];
		error[0] = output.toString();
		return error;

	}

	@Override
	public void generateNew() {
		Object[] input = readInput((int) Math.pow(10, 8));
		if (input.length != 1) {
			frame.remove(frame.center);
			this.model = null;
			int in1 = ((Integer) input[0]).intValue();
			double in2 = ((Double) input[1]).doubleValue();
			DrawClusterExplorationTwoD newSim = new DrawClusterExplorationTwoD(
					frame, new ClusterExplorationTwoD(in1, in2,
							box1.isSelected()), false);
			newSim.showEverythingInitally = box2.isSelected();
			frame.center = newSim;
			newSim.box1 = this.box1;
			newSim.box2 = this.box2;
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
		Object[] input = readInput((int) Math.pow(10, 12));
		if (input.length != 1) {
			try {
				int in1 = ((Integer) input[0]).intValue();
				double in2 = ((Double) input[1]).doubleValue();
				DrawClusterExplorationTwoD newSim = new DrawClusterExplorationTwoD(
						frame, new ClusterExplorationTwoD(in1, in2,
								box1.isSelected()), false);
				newSim.showOnlyExplored = true;
				ClusterExplorationTwoD m = (ClusterExplorationTwoD) newSim.model;
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
	public void reDraw() {
		animate = true;
		this.showEverythingInitally = box2.isSelected();
		this.paint(getGraphics());

	}

	@Override
	public String getHelpText() {
		String newLine = System.getProperty("line.separator");
		String content = "In this part of the program we see how a random walk explores a two "
				+ newLine
				+ "dimensional percolation cluster."
				+ newLine
				+ newLine
				+ "The model"
				+ newLine
				+ "We start a random walk at the origin on a percolation cluster."
				+ newLine
				+ "At each step the walk chooses one of the open and neighboring edges, for his"
				+ newLine
				+ "next step uniformly at random. Doing this the walker explores the cluster."
				+ newLine
				+ "For the definition of percolation see the describtion at the correspond part."
				+ newLine
				+ newLine
				+ "Parameters:"
				+ newLine
				+ "A input the user gives the edge probability for the percolation and the"
				+ newLine
				+ "number of steps of the random walk. Further the user can give a delay"
				+ newLine
				+ "which regulates the speed at which the walk is draw and he can decide"
				+ newLine
				+ "whether all edges that are going to be explored will be initially shown."
				+ newLine
				+ newLine
				+ "A note to the implementation:"
				+ newLine
				+ "The percolation configuration is generated while the walker takes its steps."
				+ newLine
				+ "So each time the walker reaches a previously unseen node we check which "
				+ newLine
				+ "of the four connected edges are occupied."
				+ newLine
				+ "Then we take one of the connected occupied edges as the direction of"
				+ newLine
				+ "the next step."
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
			ClusterExplorationTwoD m = ((ClusterExplorationTwoD) model);
			int w = 0;
			int h = 0;
			if (Math.max(m.maxx - m.minx, m.maxy - m.miny) > 2000) {
				w = (m.maxx - m.minx) * 3;
				h = (m.maxy - m.miny) * 3;
			} else {
				w = (m.maxx - m.minx) * 5;
				h = (m.maxy - m.miny) * 5;
			}
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