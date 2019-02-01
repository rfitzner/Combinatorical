package drawer;

import java.awt.Dimension;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;

import main.MainFrame;
import models.PercolationConfiguration;
import models.PercolationConfigurationSequence;

/**
 * This class controls the drawing a percolation configuration as we increase
 * the percolation probability. As also want to be able to redraw the
 * development we save all generated configurations in an array. DONE
 * 
 * @author Robert Fitzner
 */
@SuppressWarnings("serial")
public class DrawPercDev extends Outputapplet {
	// the sequence of configurations
	BufferedImage bi[];
	int nrframes;
	// The user can choose how long a drawn configuration is displayed before it
	// is overdrawn by the following.
	// The following is this time in ms.
	int pauseafterdrawing;
	// the user can also choose that a specific configuration of the sequence is
	// drawn
	int clusterToDraw;
	JScrollBar percframer;
	Scrollevent percframerlistener;

	public DrawPercDev(MainFrame fr) {
		this(fr, new PercolationConfigurationSequence(1000, 1000, 0.45, 0.55, 5),
				200, true);
	}

	public DrawPercDev(MainFrame fr, PercolationConfigurationSequence m,
			int delay, boolean addaptPanel) {
		model = m;
		pauseafterdrawing = delay;
		frame = fr;
		drawtimes = 0;
		clusterToDraw = -1;
		nrframes = ((PercolationConfigurationSequence) model).numberOfFrames;
		bi = new BufferedImage[nrframes];
		if (addaptPanel)
			initaliseParameterPanel();
	}

	@Override
	public void initaliseParameterPanel() {
		PercolationConfigurationSequence m = (PercolationConfigurationSequence) model;
		frame.setTitle("Development of a percolation configuration");
		// Reset upper part, where parameters are entered and the bottoms are
		frame.remove(frame.parameterentry);
		frame.parameterentry = new JPanel(new GridLayout(2, 7));
		// Labels of input
		frame.parameterentry.add(new JLabel("Init. prob."));
		frame.parameterentry.add(new JLabel("Final  prob."));
		frame.parameterentry.add(new JLabel("Number of frames"));
		frame.parameterentry.add(new JLabel("Delay(ms)"));
		frame.parameterentry.add(new JLabel("Gridsize"));
		// parameterentry.add(new JLabel("vertical size"));

		JButton redrawbutton = new JButton(MainFrame.redraw);
		redrawbutton.setMnemonic(KeyEvent.VK_G);
		redrawbutton.setActionCommand(MainFrame.redraw);
		redrawbutton.addActionListener(frame.listener);
		frame.parameterentry.add(redrawbutton);

		JButton generatebutton = new JButton("Generate new");
		generatebutton.setMnemonic(KeyEvent.VK_G);
		generatebutton.setActionCommand(MainFrame.gostring);
		generatebutton.addActionListener(frame.listener);
		frame.parameterentry.add(generatebutton);

		// Field for input
		frame.inputs = new JTextField[6];
		frame.inputs[0] = new JTextField("" + m.initialBondProb);
		frame.inputs[1] = new JTextField("" + m.finalBondProb);
		frame.inputs[2] = new JTextField("" + m.numberOfFrames);
		frame.inputs[3] = new JTextField("" + this.pauseafterdrawing);
		frame.inputs[4] = new JTextField("" + m.wsize,5);
		frame.inputs[5] = new JTextField("" + m.hsize,5);
		for (int i = 0; i < frame.inputs.length; i++) {
			frame.inputs[i].setFont(new Font("Arial", Font.ROMAN_BASELINE
					| Font.PLAIN, 22));
		}

		frame.parameterentry.add(frame.inputs[0]);
		frame.parameterentry.add(frame.inputs[1]);
		frame.parameterentry.add(frame.inputs[2]);
		frame.parameterentry.add(frame.inputs[3]);
		JPanel dims = new JPanel();
		dims.add(frame.inputs[4]);
		dims.add(frame.inputs[5]);
		frame.parameterentry.add(dims);

		percframer = new JScrollBar(JScrollBar.HORIZONTAL,
				m.numberOfFrames - 1, 0, 0, m.numberOfFrames - 1);
		percframerlistener = new Scrollevent(this);
		percframer.addAdjustmentListener(percframerlistener);
		frame.parameterentry.add(percframer);

		frame.helpbutton = new JButton(MainFrame.askforhelp);
		frame.parameterentry.add(frame.helpbutton);
		frame.helpbutton.addActionListener(frame.listener);
		frame.helpbutton.setActionCommand(MainFrame.askforhelp);
		frame.add(frame.parameterentry, BorderLayout.NORTH);
		frame.validate();
	}

	public void paint(Graphics g) {
		PercolationConfigurationSequence m = (PercolationConfigurationSequence) model;
		if (m.percConfigurations != null) {
			boolean recompute = false;
			if (bi[0] == null) {
				recompute = true;
			} else {
				double difference = Math
						.max(Math.abs(((getSize().height - bi[0].getHeight()) * 1.0 / Math
								.min(bi[0].getHeight(), getSize().height))),
								Math.abs(((getSize().width - bi[0].getWidth()) * 1.0 / Math
										.min(getSize().width, bi[0].getWidth()))));
				if (difference > 0.3)
					recompute = true;
			}
			if (recompute)
				for (int i = 0; i < m.percConfigurations.length; i++) {
					bi[i] = new BufferedImage(getSize().width,
							getSize().height, BufferedImage.TYPE_BYTE_INDEXED);
					drawOneConfig((Graphics2D) bi[i].getGraphics(),
							m.percConfigurations[i], getSize());
				}
			if (clusterToDraw == -1) {
				for (int i = 0; i < m.percConfigurations.length; i++) {
					g.drawImage(bi[i].getScaledInstance(getSize().width,
							getSize().height, Image.SCALE_DEFAULT), 0, 0, null);
					try {
						Thread.sleep(pauseafterdrawing);
					} catch (InterruptedException e) {
					}
				}
				clusterToDraw = m.percConfigurations.length - 1;
			} else {
				g.drawImage(
						bi[clusterToDraw].getScaledInstance(getSize().width,
								getSize().height, Image.SCALE_DEFAULT), 0, 0,
						null);
			}
		}
	}

	/**
	 * To avoid doubling of the code I source out this code documentation see
	 * DrawPercDev.paint()
	 */
	void drawOneConfig(Graphics2D g2, PercolationConfiguration conf, Dimension d) {
		// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		// we the size of the panel in pixel time pixel
		g2.setColor(bg);
		g2.fillRect(0, 0, d.width, d.height);

		double gridsizeX = (d.width - 20) * 1.0 / conf.horizontalSize;
		double gridsizeY = (d.height - 20) * 1.0 / conf.verticalSize;

		for (int i = 0; i < conf.horizontalSize - 1; i++) {
			for (int j = 0; j < conf.verticalSize - 1; j++) {
				if (conf.configuration[0][i][j]) {
					int activeDrawcolor = PercolationConfiguration
							.givecolorvalue(conf.orderOfClusters,
									conf.markings[i][j], colors.length);
					g2.setColor(colors[activeDrawcolor]);
					g2.draw(new Line2D.Double(i * gridsizeX + 10, j * gridsizeY
							+ 10, (i + 1) * gridsizeX + 10, j * gridsizeY + 10));
					g2.setColor(Color.black);
				}
				if (conf.configuration[1][i][j]) {
					int activeDrawcolor = PercolationConfiguration
							.givecolorvalue(conf.orderOfClusters,
									conf.markings[i][j], colors.length);
					g2.setColor(colors[activeDrawcolor]);
					g2.draw(new Line2D.Double(i * gridsizeX + 10, j * gridsizeY
							+ 10, i * gridsizeX + 10, (j + 1) * gridsizeY + 10));
					g2.setColor(Color.black);
				}
			}
		}
		g2.setColor(new Color(30, 30, 30));
		Font myFont = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
		g2.setFont(myFont);
		g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
		g2.drawString(new Double(
				Math.round(conf.bondprob * 10000) * 1.0 / 10000).toString(),
				d.width - 80, d.height - 30);
	}

	/**
	 * With this we can select which cluster should be drawn
	 * 
	 * @param input
	 */
	public void setclusterToDraw(int input) {
		if (input < 0)
			clusterToDraw = -1;
		else if (input > nrframes - 1)
			clusterToDraw = nrframes - 1;
		else
			clusterToDraw = input;
	}

	@Override
	public String getType() {
		return MainFrame.percdev;
	}

	public void setPauseafterdrawing(int in) {
		pauseafterdrawing = in;
	}

	private Object[] readInput(int max1, int max2, int max5, int max6) {
		boolean correctinput = true;
		String newLine = System.getProperty("line.separator");
		StringBuffer output = new StringBuffer();
		output.append("Input Error" + newLine);
		// read all inputs and check them
		int in1 = utils.SmallTools.giveInteger(frame.inputs[4].getText());
		int in2 = utils.SmallTools.giveInteger(frame.inputs[5].getText());
		double in3 = utils.SmallTools.giveDouble(frame.inputs[0].getText());
		double in4 = utils.SmallTools.giveDouble(frame.inputs[1].getText());
		int in5 = utils.SmallTools.giveInteger(frame.inputs[2].getText());
		int in6 = utils.SmallTools.giveInteger(frame.inputs[3].getText());

		if (in1 == Integer.MAX_VALUE) {
			output.append("The input for the vertical dimension is not an integer: "
					+ frame.inputs[4].getText() + newLine);
			correctinput = false;
		} else {
			if ((in1 > max1) || (in1 < 0)) {
				correctinput = false;
				output.append("Horizontal grid size is to big (please between 1 and "
						+ max1 + ")." + newLine);
			}
		}

		if (in2 == Integer.MAX_VALUE) {
			output.append("The input for the horizontal dimension is not an integer: "
					+ frame.inputs[5].getText() + newLine);
			correctinput = false;
		} else {
			if ((in2 > max2) || (in2 < 0)) {
				correctinput = false;
				output.append("Vertical grid size is to big (please between 1 and "
						+ max2 + ")." + newLine);
			}
		}

		if (in3 == Double.MAX_VALUE) {
			output.append("The input for the initial bound propability is not a number:"
					+ frame.inputs[0].getText() + newLine);
			correctinput = false;
		} else {
			if ((in3 > 1) || (in3 < 0)) {
				correctinput = false;
				output.append("Given value for initial probability is not a propability [0,1]."
						+ newLine);
			}
		}
		if (in4 == Double.MAX_VALUE) {
			output.append("The input for the final bound propability is not a number:"
					+ frame.inputs[1].getText() + newLine);
			correctinput = false;
		} else {
			if ((in4 > 1) || (in4 < 0)) {
				correctinput = false;
				output.append("Given value for final probability is not a propability [0,1]."
						+ newLine);
			} else {
				if (in4 < in3 && correctinput) {
					correctinput = false;
					output.append("The initial bond probability can not be smaller then then the final."
							+ newLine);
				}
			}
		}

		if (in5 == Integer.MAX_VALUE) {
			output.append("The input for number of frames is not a number: "
					+ frame.inputs[2].getText() + newLine);
			correctinput = false;
		} else {
			if ((in5 > max5) || (in5 < 0)) {
				correctinput = false;
				output.append("The number of frames should be less then "
						+ max5 + ", otherwise you memory will fail." + newLine);
			}
		}

		if (in6 == Integer.MAX_VALUE) {
			output.append("The input for the delay should be a positive integer: "
					+ frame.inputs[3].getText() + newLine);
			correctinput = false;
		} else {
			if ((in6 > max6) || (in6 < 0)) {
				correctinput = false;
				output.append("I do not really think that you want to wait for more then 10 minutes."
						+ newLine);
			}
		}
		if (correctinput) {
			Object[] values = new Object[6];
			values[0] = new Integer(in1);
			values[1] = new Integer(in2);
			values[2] = new Double(in3);
			values[3] = new Double(in4);
			values[4] = new Integer(in5);
			values[5] = new Integer(in6);
			return values;
		}
		Object[] error = new Object[1];
		error[0] = output.toString();
		return error;

	}

	@Override
	public void generateNew() {
		// Object[] input = this.readInput(2500, 2500, 100, 60000);
		Object[] input = this.readInput(1500, 1000, 101, 60000);
		if (input.length == 1) {
			JOptionPane
					.showMessageDialog(frame, ((String) input[0]).toString());
		} else { // destroy the displayed model and help message
			int in1 = ((Integer) input[0]).intValue();
			int in2 = ((Integer) input[1]).intValue();
			double in3 = ((Double) input[2]).doubleValue();
			double in4 = ((Double) input[3]).doubleValue();
			int in5 = ((Integer) input[4]).intValue();
			int in6 = ((Integer) input[5]).intValue();

			frame.remove(frame.center);
			DrawPercDev newSim = new DrawPercDev(frame,
					new PercolationConfigurationSequence(in1, in2, in3, in4,
							in5), in6, false);
			frame.center = newSim;
			newSim.frame = this.frame;
			newSim.percframer = this.percframer;
			newSim.percframer.setMaximum(in5 - 1);
			newSim.percframer.setValue(in5 - 1);
			newSim.percframerlistener = this.percframerlistener;
			newSim.percframerlistener.mother = newSim;

			frame.add(newSim, BorderLayout.CENTER);
		}
		frame.validate();
	}

	public void redrawPercDevelopment(int frametoDraw) {
		try {
			int in6 = Integer.parseInt(frame.inputs[3].getText());
			setPauseafterdrawing(in6);
			setclusterToDraw(frametoDraw);
			setDrawtimes(0);
			if (this.getGraphics() != null)
				paint(getGraphics());
			if (frametoDraw < 0) {
				percframer.setValue(percframer.getMaximum());
				setclusterToDraw(this.nrframes - 1);
			}
		} catch (NumberFormatException e) {
			System.out.println("Input is not a number.");
		}
	}

	public class Scrollevent implements AdjustmentListener {
		DrawPercDev mother;

		public Scrollevent(DrawPercDev m) {
			mother = m;
		}

		public void adjustmentValueChanged(AdjustmentEvent ae) {
			int value = ae.getValue();
			mother.redrawPercDevelopment(value);
		}
	}

	@Override
	public void reDraw() {
		redrawPercDevelopment(-1);

	}

	@Override
	public String getHelpText() {
		String newLine = System.getProperty("line.separator");
		String content = "This part shows how a configuration develops when the bond probability"
				+ newLine
				+ " increases. The initial and final bond probability can be given by "
				+ newLine
				+ " the user. The development of the configuration is shown using a number of "
				+ newLine
				+ " intermediate configurations. The total number of configurations shown "
				+ newLine
				+ " is given in the field -number of frames-."
				+ newLine
				+ newLine
				+ " Interesting hereby is to watch the development over the critical "
				+ newLine
				+ " probability 0.5, when the configuration develops from the subcritical "
				+ newLine
				+ " phase to the supercritical phase."
				+ newLine
				+ newLine
				+ " After computing the hole sequence of configurations will be shown."
				+ newLine
				+ " Thereby the user can give a duration for which one configuration will be "
				+ newLine
				+ " displayed before it is overpainted."
				+ newLine
				+ " Alternatively one can scroll through all configurations."
				+ newLine
				+ newLine
				+ " By choosing the grid size and the number of frames the user should keep"
				+ newLine
				+ " in mind that the algorithm might need some minutes to generate the "
				+ newLine
				+ " configuration, to color the clusters and draw them. "
				+ newLine
				+ newLine
				+ "To return to the program press the -Back- Button, that has replaced, "
				+ newLine
				+ "the help button or select a new part of the program";
		return content;
	}

	@Override
	public void generateNewForFile(File file, String type) {
		Object[] input = this.readInput(4000, 4000, 100, Integer.MAX_VALUE - 2);
		if (input.length == 1) {
			JOptionPane
					.showMessageDialog(frame, ((String) input[0]).toString());
		} else {
			try {
				int in1 = ((Integer) input[0]).intValue();
				int in2 = ((Integer) input[1]).intValue();
				double in3 = ((Double) input[2]).doubleValue();
				double in4 = ((Double) input[3]).doubleValue();
				int in5 = ((Integer) input[4]).intValue();
				DrawPercDev newSim = new DrawPercDev(frame,
						new PercolationConfigurationSequence(in1, in2, in3,
								in4, in5), 0, false);
				int w = in1 * 3;
				int h = in2 * 3;
				File[] files = new File[newSim.nrframes];

				for (int i = 0; i < newSim.nrframes; i++) {
					String tmp1 = file.getAbsolutePath();
					files[i] = new File(
							tmp1.substring(0, tmp1.lastIndexOf(".")) + (i + 1)
									+ tmp1.substring(tmp1.lastIndexOf(".")));
					files[i].createNewFile();
				}
				for (int i = 0; i < newSim.nrframes; i++) {
					BufferedImage locbi = new BufferedImage(w, h,
							BufferedImage.TYPE_BYTE_INDEXED);
					Graphics2D ig2 = locbi.createGraphics();
					newSim.drawOneConfig(
							ig2,
							((PercolationConfigurationSequence) newSim.model).percConfigurations[i],
							new Dimension(w, h));
					// System.out.println(tmp1.substring(0,tmp1.lastIndexOf("."))+i+tmp1.substring(tmp1.lastIndexOf(".")));
					javax.imageio.ImageIO.write(locbi, type, files[i]);
					locbi = new BufferedImage(2, 2,
							BufferedImage.TYPE_BYTE_INDEXED);
				}
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
			PercolationConfigurationSequence ma = ((PercolationConfigurationSequence) model);
			PercolationConfiguration m = ma.percConfigurations[this.clusterToDraw];
			int w = 0;
			int h = 0;
			if (Math.max(m.horizontalSize, m.verticalSize) > 1000) {
				w = m.horizontalSize * 3;
				h = m.verticalSize * 3;
			} else {
				w = m.horizontalSize * 5;
				h = m.verticalSize * 5;
			}
			BufferedImage locbi = new BufferedImage(w, h,
					BufferedImage.TYPE_BYTE_INDEXED);
			Graphics2D ig2 = locbi.createGraphics();
			this.drawOneConfig(ig2, m, new Dimension(w, h));
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
