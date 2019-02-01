package drawer;

import java.awt.Dimension;

import java.awt.BorderLayout;
import java.awt.Color;
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
import models.PercolationConfiguration;

/**
 * 
 * This class draws a simple percolation configuration in the middle frame. DONE
 * 
 * @author Robert Fitzner
 */
@SuppressWarnings("serial")
public class DrawPerc extends Outputapplet {

	BufferedImage bi;
	// this is inserted to cancel the computation and/or the drawing,
	// the used press cancel (the button is still missing)
	boolean canceled;

	/**
	 * the Constructor that is used the initializes the part of the program in
	 * the MainFrame
	 * 
	 * @param fr
	 */
	public DrawPerc(MainFrame fr) {
		this(fr, new PercolationConfiguration(100, 80, 0.5), true);
	}

	public DrawPerc(MainFrame fr, PercolationConfiguration co, boolean initparam) {
		super();
		model = co;
		frame = fr;
		if (initparam)
			initaliseParameterPanel();
		// Now we initialize a simple percolation that is shown in the
		// beginning.
	}

	@Override
	public void initaliseParameterPanel() {
		frame.setTitle("Percolation");

		frame.inputs = new JTextField[4];
		frame.inputs[0] = new JTextField(""
				+ ((PercolationConfiguration) model).horizontalSize);
		frame.inputs[1] = new JTextField(""
				+ ((PercolationConfiguration) model).verticalSize);
		frame.inputs[2] = new JTextField(""
				+ ((PercolationConfiguration) model).bondprob);
		frame.inputs[3] = new JTextField("0");
		for (int i = 0; i < frame.inputs.length; i++) {
			frame.inputs[i].setFont(new Font("Arial", Font.ROMAN_BASELINE
					| Font.PLAIN, 22));
		}
		// Reset upper part, where parameters are entered and the bottoms are
		frame.remove(frame.parameterentry);
		frame.parameterentry = new JPanel(new GridLayout(2, 5));
		frame.parameterentry.add(new JLabel("Width"));
		frame.parameterentry.add(new JLabel("Height"));
		frame.parameterentry.add(new JLabel("Edge probability"));

		JButton continuebutton = new JButton("Increase prob.");
		continuebutton.setMnemonic(KeyEvent.VK_G);
		continuebutton.setActionCommand(MainFrame.continueString);
		continuebutton.addActionListener(frame.listener);
		frame.parameterentry.add(continuebutton);

		frame.helpbutton = new JButton(MainFrame.askforhelp);
		frame.parameterentry.add(frame.helpbutton);
		frame.helpbutton.addActionListener(frame.listener);
		frame.helpbutton.setActionCommand(MainFrame.askforhelp);

		frame.parameterentry.add(frame.inputs[0]);
		frame.parameterentry.add(frame.inputs[1]);
		frame.parameterentry.add(frame.inputs[2]);
		frame.parameterentry.add(frame.inputs[3]);

		JButton generatebutton = new JButton("Generate new");
		generatebutton.setMnemonic(KeyEvent.VK_G);
		generatebutton.setActionCommand(MainFrame.gostring);
		generatebutton.addActionListener(frame.listener);
		frame.parameterentry.add(generatebutton);

		frame.add(frame.parameterentry, BorderLayout.NORTH);
	}

	public void paint(Graphics g) {
		if ((bi != null)) {
			// if we only have a small change in comparison to the
			// last image we just rescale the image
			double difference = Math.max(Math.abs(((getSize().height - bi
					.getHeight()) * 1.0 / Math.min(bi.getHeight(),
					getSize().height))), Math.abs(((getSize().width - bi
					.getWidth()) * 1.0 / Math.min(getSize().width,
					bi.getWidth()))));
			if ((difference < 0.3)) {
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

	public void paint(Graphics g, Dimension d) {

		// If we have drawn this configuration only 0.4 seconds ago we do not do
		// it again.
		// This happens when the used resizes the frame manually or multiple
		// redraws are called

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		// we asked for the size of the panel in which we draw the confi in
		// pixel times pixel.
		PercolationConfiguration m = ((PercolationConfiguration) model);

		// set the colors the standard for Outputpanels
		g.setColor(bg);
		g2.fillRect(0, 0, d.width, d.height);
		g2.setColor(new Color(200, 200, 200));
		int thickness = 0;
		// if we draw only a small configuration we draw a bit thicker lines
		if (Math.max(m.horizontalSize, m.verticalSize) < 151) {
			if (Math.min(m.horizontalSize, m.verticalSize) > 50) {
				thickness = 1;
			} else {
				thickness = 2;
			}
		}
		// writing my name in the lower right corner
		Font myFont = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
		g2.setFont(myFont);
		g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
		g.setColor(fg);

		// if we canceled the computation or an earlier drawing we draw this
		// not activated
		if (canceled) {
			g2.drawString(" Computation canceled", d.width / 2 - 180,
					d.height / 2 - 10);
		} else {
			int activeDrawcolor = 0;
			double gridsizeX = (d.width - 20) * 1.0 / m.horizontalSize;
			double gridsizeY = (d.height - 20) * 1.0 / m.verticalSize;
			// If we only have a few thousand bonds the drawing will be almost
			// instantaneous.
			// If we have more we need to smarter way to draw it.

			// To state the used mechanism in a simplified way:
			// We only draw every (width/100)th bond, to first create the rough
			// picture
			// then I fill in the missing bonds.
			// Thereby we randomize the order how the grid is filled.
			// distance in which we draw the items
			for (int i = 0; i < m.horizontalSize - 1; i++) {
				for (int j = 0; j < m.verticalSize - 1; j++) {
					// now we are at the point(i,j) and have to check,
					// whether we have to draw a bond to the left and down
					// from this point.
					if (m.configuration[0][i][j]) {
						// we choose the color according to in which cluster
						// size of the correspond cluster
						activeDrawcolor = PercolationConfiguration
								.givecolorvalue(m.orderOfClusters,
										m.markings[i][j], colors.length);
						if (thickness == 0) {
							g2.setColor(colors[activeDrawcolor]);
							g2.draw(new Line2D.Double(i * gridsizeX + 10, j
									* gridsizeY + 10, (i + 1) * gridsizeX + 10,
									j * gridsizeY + 10));
							g2.setColor(Color.black);
						} else {
							SmallTools.drawThickLine(g2, i * gridsizeX + 10, j
									* gridsizeY + 10, (i + 1) * gridsizeX + 10,
									j * gridsizeY + 10, thickness,
									colors[activeDrawcolor]);
						}
					}
					if (m.configuration[1][i][j]) {
						activeDrawcolor = PercolationConfiguration
								.givecolorvalue(m.orderOfClusters,
										m.markings[i][j], colors.length);
						if (thickness == 0) {
							g2.setColor(colors[activeDrawcolor]);
							g2.draw(new Line2D.Double(i * gridsizeX + 10, j
									* gridsizeY + 10, i * gridsizeX + 10,
									(j + 1) * gridsizeY + 10));
						} else {
							SmallTools.drawThickLine(g2, i * gridsizeX + 10, j
									* gridsizeY + 10, i * gridsizeX + 10,
									(j + 1) * gridsizeY + 10, thickness,
									colors[activeDrawcolor]);
						}
					}
				}
			}
			drawnlasttime = System.currentTimeMillis();
		}
		g2.setColor(Color.black);
		g2.drawString(
				new Double(
						Math.round(((PercolationConfiguration) model).bondprob * 10000) * 1.0 / 10000)
						.toString(), d.width - 80, d.height - 30);
		g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
	}

	/**
	 * With this we cancel the drawing (TOCOME the computation can also be
	 * canceled).
	 */
	public void setCancel() {
		canceled = true;
		if (model != null)
			((PercolationConfiguration) model).setCancel();
	}

	@Override
	public String getType() {
		return MainFrame.perc;
	}

	private Object[] readInput(int max1, int max2) {
		boolean correctinput = true;
		String newLine = System.getProperty("line.separator");
		StringBuffer output = new StringBuffer();
		output.append("Input Error" + newLine);

		int in1 = utils.SmallTools.giveInteger(frame.inputs[0].getText());
		int in2 = utils.SmallTools.giveInteger(frame.inputs[1].getText());
		double in3 = utils.SmallTools.giveDouble(frame.inputs[2].getText());

		if (in1 == Integer.MAX_VALUE) {
			output.append("The input for the vertical dimension is not an integer: "
					+ frame.inputs[2].getText() + newLine);
			correctinput = false;
		} else {
			if ((in1 > max1) || (in1 < 0)) {
				output.append(" Vertical grid size is to big." + newLine);
				correctinput = false;
			}

		}
		if (in2 == Integer.MAX_VALUE) {
			output.append("The input for the horizontal dimension is not an integer: "
					+ frame.inputs[1].getText() + newLine);
			correctinput = false;
		} else {
			if ((in2 > max2) || (in2 < 0)) {
				output.append("Horizontal grid size is to big." + newLine);
				correctinput = false;
			}
		}

		if (in3 == Double.MAX_VALUE) {
			output.append("The input for bound propability is not a number:"
					+ frame.inputs[0].getText() + newLine);
			correctinput = false;
		} else {
			if ((in3 > 1) || (in3 < 0)) {

				output.append("Edge Probability is not a propability."
						+ newLine);
				correctinput = false;
			}
		}
		if (correctinput) {
			Object[] values = new Object[3];
			values[0] = new Integer(in1);
			values[1] = new Integer(in2);
			values[2] = new Double(in3);
			return values;
		}
		Object[] error = new Object[1];
		error[0] = output.toString();
		return error;
	}

	@Override
	public void generateNew() {
		// destroy the displayed model and help message
		// Object[] input = this.readInput(1000, 1000); applett
		Object[] input = this.readInput(2000, 2000);
		if (input.length != 1) {
			frame.remove(frame.center);
			int in1 = ((Integer) input[0]).intValue();
			int in2 = ((Integer) input[1]).intValue();
			double in3 = ((Double) input[2]).doubleValue();
			frame.center = new DrawPerc(frame, new PercolationConfiguration(
					in1, in2, in3), false);
			frame.add(frame.center, BorderLayout.CENTER);
			this.setVisible(false);
			frame.remove(frame.parameterentry);
			frame.add(frame.parameterentry, BorderLayout.NORTH);
		} else {
			JOptionPane
					.showMessageDialog(frame, ((String) input[0]).toString());
		}
		frame.validate();
	}

	@Override
	public void reDraw() {
		paint(getGraphics());
	}

	@Override
	public String getHelpText() {
		String newLine = System.getProperty("line.separator");
		String content = "This part of the applet simulates bond percolation on a finite grid. "
				+ newLine
				+ newLine
				+ "The model:"
				+ newLine
				+ "We generate a grid of points. Then we connected for each pair of "
				+ newLine
				+ "neighboring points with a given probability p independently "
				+ newLine
				+ "of all other edges. The interesting feature is the development of the size "
				+ newLine
				+ "of the biggest clusters(red) and a phase transition of the model at p=1/2."
				+ newLine
				+ "We color the 20 biggest clusters. The biggest one is red. The other colors"
				+ newLine
				+ "are used multiple times."
				+ newLine
				+ newLine
				+ "The input:"
				+ newLine
				+ "The user can give the vertical and horizontal size of the grid."
				+ newLine
				+ "The dimension should be given as two integer between 1 and 1000."
				+ newLine
				+ "Further the user can give the bond probability that should be a  "
				+ newLine
				+ "number between 0 and 1. (Close to 0.5 is the most interesting part)."
				+ newLine
				+ newLine
				+ "As the user press the -Generate new- button a new bond percolation "
				+ newLine
				+ "with the given parameters will be created. "
				+ newLine
				+ "After the creation of this percolation configuration an algorithm start that "
				+ newLine
				+ "identifies connected components, so regions that are connected by bonds."
				+ newLine
				+ "Then the configuration is drawn and the biggest components are drawn in color."
				+ newLine
				+ "The biggest connected is in red, the second biggest is blue, then follows"
				+ newLine
				+ "green, yellow, cyan, orange, pink, magenta, black."
				+ newLine
				+ newLine
				+ "After generating a configuration you can increase the bond probability"
				+ newLine
				+ "and thereby add some new edges. To do this enter the probability that you want"
				+ newLine
				+ "to add in the right-most field."
				+ newLine
				+ newLine
				+ "It would be possible to generate bigger grids then 2000x2000,"
				+ newLine
				+ "but then the identifing of the clusters and painting takes long."
				+ newLine
				+ "As I would like to keep this program -real time- I deactived larger sizes."
				+ newLine
				+ newLine
				+ "In the downloadable version you can generate larger part of the lattice "
				+ newLine
				+ "that are then directly saved in a file. Here I restricted the size to 10000x10000."
				+ newLine
				+ "When simulating large picture you can expect to run out of memory is this case ."
				+ newLine
				+ "you would have to kill the program-process."
				+ newLine
				+ newLine
				+ "To return to the program press the -Back- Button, that has replaced, "
				+ newLine
				+ "the help button or select a new part of the program";
		return content;
	}

	@Override
	public void generateNewForFile(File file, String type) {
		Object[] input = this.readInput(20000, 20000);
		if (input.length == 1) {
			JOptionPane
					.showMessageDialog(frame, ((String) input[0]).toString());
		} else {
			try {
				int in1 = ((Integer) input[0]).intValue();
				int in2 = ((Integer) input[1]).intValue();
				double in3 = ((Double) input[2]).doubleValue();
				// destroy the displayed model and help message
				DrawPerc newSim = new DrawPerc(frame,
						new PercolationConfiguration(in1, in2, in3), false);
				int w = in1 * 3;
				int h = in2 * 3;
				BufferedImage locbi = new BufferedImage(w, h,
						BufferedImage.TYPE_BYTE_INDEXED);
				Graphics2D ig2 = locbi.createGraphics();
				newSim.paint(ig2, new Dimension(w, h));

				newSim.destroy();
				javax.imageio.ImageIO.write(locbi, type, file);
				locbi = new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_INDEXED);
				JOptionPane.showMessageDialog(frame,
						"Saving of a new simulation as picture is completed.");
			} catch (java.io.IOException ie) {
				JOptionPane.showMessageDialog(frame, ie.toString());
			} catch (java.lang.OutOfMemoryError e) {
				JOptionPane.showMessageDialog(frame,
						"You have just blown your memory.");
			}
		}
	}

	@Override
	public void saveCurrentlyShownToFile(File file, String type) {
		try {
			PercolationConfiguration m = ((PercolationConfiguration) model);
			int w = 0;
			int h = 0;
			if (Math.max(m.horizontalSize, m.verticalSize) > 1000) {
				w = m.horizontalSize * 3;
				h = m.verticalSize * 3;
			} else {
				w = m.horizontalSize * 3;
				h = m.verticalSize * 3;
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
		double add = SmallTools.giveDouble(frame.inputs[3].getText());
		if (add == Integer.MAX_VALUE) {
			JOptionPane
					.showMessageDialog(
							frame,
							("The input for the increase in probability is not a number: " + frame.inputs[2]
									.getText()));
		} else {
			if ((add + ((PercolationConfiguration) model).bondprob > 1)
					|| (add <= 0)) {
				JOptionPane
						.showMessageDialog(
								frame,
								(" We can not increase the bond probability by this number"));
			} else {
				PercolationConfiguration m = (PercolationConfiguration) model;
				model = new PercolationConfiguration(m, m.horizontalSize,
						m.verticalSize, m.bondprob, m.bondprob + add);
				bi = null;
				double d = Math.round((m.bondprob + add) * 1000) / 1000;
				frame.inputs[2].setText("" + (m.bondprob + d));
				frame.validate();
				this.reDraw();
			}
		}

	}
}
