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
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.MainFrame;
import models.MineSweeper;

@SuppressWarnings("serial")
public class DrawMineSweeper extends Outputapplet {
	BufferedImage bi;
	boolean animate;

	Color[] p1 = { Color.yellow, Color.black, new Color(235, 0, 0) };
	Color[] p2;// = { new Color(255, 255, 255), new Color(245, 245, 245),
				// new Color(235, 235, 235), new Color(225, 225, 225),

	// new Color(215, 215, 215), new Color(205, 205, 205),
	// new Color(195, 195, 195), new Color(185, 185, 185),
	// new Color(175, 175, 175) };

	public DrawMineSweeper(MainFrame fr) {
		this(fr, new MineSweeper(10, 10, 0.1), true);
	}

	public DrawMineSweeper(MainFrame fr, MineSweeper m, boolean initparam) {
		frame = fr;
		model = m;

		p2 = new Color[9];
		for (int i = 0; i < p2.length; i++) {
			p2[i] = new Color(255 - 15 * i, 255 - 15 * i, 255 - 15 * i);
		}

		if (initparam)
			this.initaliseParameterPanel();
	}

	@Override
	public void paint(Graphics g) {
		if ((bi != null) && !animate) {
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
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		// we asked for the size of the panel in which we draw the confi in
		// pixel times pixel.
		MineSweeper m = ((MineSweeper) model);

		// set the colors the standard for Outputpanels
		g.setColor(bg);
		g2.fillRect(0, 0, d.width, d.height);
		g2.setColor(new Color(200, 200, 200));
		double gridsizeX = (d.width - 20) * 1.0 / m.cleared.length;
		double gridsizeY = (d.height - 20) * 1.0 / m.cleared[0].length;
		g2.setColor(Color.black);
		for (int i = 0; i < m.cleared.length; i++) {
			for (int j = 0; j < m.cleared[i].length; j++) {
				if (m.inCleaning.contains(new Dimension(i, j))) {
					if (m.inCleaning.get(0).equals(new Dimension(i, j)))
						g2.setColor(Color.blue);
					else {
						if (m.marked[i][j])
							g2.setColor(new Color(111, 0, 255));
						else if (m.mine[i][j])
							g2.setColor(new Color(111, 0, 255));
						else if (!m.cleared[i][j])
							g2.setColor(new Color(0, 255, 0));
						else
							g2.setColor(new Color(100, 255, 100));
					}
				} else {
					if (m.marked[i][j])
						g2.setColor(p1[1]);
					else if (m.mine[i][j])
						g2.setColor(p1[2]);
					else if (!m.cleared[i][j])
						g2.setColor(p1[0]);
					else
						g2.setColor(p2[m.getNrInNeighborhood(i, j, m.mine)]);
				}
				g2.fillRect((int) Math.round(10 + i * gridsizeX),
						(int) Math.round(10 + j * gridsizeY),
						Math.max((int) Math.round(gridsizeX), 1),
						Math.max((int) Math.round(gridsizeY), 1));
			}
		}
		g2.setColor(Color.black);
		Font myFont = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
		g2.setFont(myFont);
		g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
		animate = false;
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
			output.append("The input for mie propability is not a number:"
					+ frame.inputs[0].getText() + newLine);
			correctinput = false;
		} else {
			if ((in3 > 1) || (in3 < 0)) {

				output.append("Mine Probability is not a propability."
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
		Object[] input = this.readInput(2000, 2000);
		if (input.length != 1) {
			frame.remove(frame.center);
			int in1 = ((Integer) input[0]).intValue();
			int in2 = ((Integer) input[1]).intValue();
			double in3 = ((Double) input[2]).doubleValue();
			frame.center = new DrawMineSweeper(frame, new MineSweeper(in1, in2,
					in3), false);
			frame.add(frame.center, BorderLayout.CENTER);
			this.setVisible(false);
		} else {
			JOptionPane
					.showMessageDialog(frame, ((String) input[0]).toString());
		}
		frame.validate();

	}

	@Override
	public void generateNewForFile(File file, String type) {
		JOptionPane.showMessageDialog(frame,
				"Feature not available for this model. Why should it?");
	}

	@Override
	public void initaliseParameterPanel() {
		frame.setTitle("MineSweeper");

		frame.inputs = new JTextField[4];
		frame.inputs[0] = new JTextField(""
				+ ((MineSweeper) model).cleared.length);
		frame.inputs[1] = new JTextField(""
				+ ((MineSweeper) model).cleared[0].length);
		frame.inputs[2] = new JTextField("" + ((MineSweeper) model).mineprob);
		frame.inputs[3] = new JTextField("1");
		for (int i = 0; i < frame.inputs.length; i++) {
			frame.inputs[i].setFont(new Font("Arial", Font.ROMAN_BASELINE
					| Font.PLAIN, 22));
		}
		// Reset upper part, where parameters are entered and the bottoms are
		frame.remove(frame.parameterentry);
		frame.parameterentry = new JPanel(new GridLayout(2, 5));
		frame.parameterentry.add(new JLabel("Width"));
		frame.parameterentry.add(new JLabel("Height"));
		frame.parameterentry.add(new JLabel("Mine Probability"));

		JButton continuebutton = new JButton("Continue");
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

	@Override
	public void reDraw() {
		animate = true;
		paint(getGraphics());

	}

	@Override
	public String getType() {
		return MainFrame.minesweeper;
	}

	@Override
	public String getHelpText() {
		String newLine = System.getProperty("line.separator");
		String content = "Here we simlate an automatised version of the famous game Minesweeper. "
				+ newLine
				+ "Thereby mines are places randomly on the plane and the player should find these mines."
				+ newLine
				+ newLine
				+ "To the original game:"
				+ newLine
				+ "At each step the player can see for all points he has already cleared how many mines are "
				+ newLine
				+ "on the eight neighboring positions, then he has to choose a position where he suspects no mines."
				+ newLine
				+ "If here was no mine the number of neighboring mines is displayed on the, then cleared, square."
				+ newLine
				+ "This proceed until all mines are found or the user tries to clear a field where a mine is present,"
				+ newLine
				+ "which lets the mine explode and thereby makes the player lose."
				+ newLine
				+ newLine
				+ "In this program the user gives the chance p that a mine is present on an arbitrary field."
				+ newLine
				+ "Then an algorith tries to find all mines."
				+ newLine
				+ newLine
				+ "The problem of interest:"
				+ newLine
				+ "It has been shown that finding all mines in a finite box of increasing size is NP-hard problem"
				+ newLine
				+ "and it is known that there are configuration which can not be solved, without taking the chance to "
				+ newLine
				+ "ignite a bomb."
				+ "To the author a different question was posed. Image playing minesweeper on an infinite grid."
				+ newLine
				+ "What is the probability that you can keep on playing forever. If the mineprobability p is positive "
				+ newLine
				+ "this probability is always smaller then one. Reason being that our first field to clear could  "
				+ newLine
				+ "be a mine. And even if the first field is not a mine, just one mine in the neighborhood would give the risk"
				+ newLine
				+ "of igniting a mine when clearing the second position."
				+ newLine
				+ "From percolation arguments we know that there is always a positive probability that we can"
				+ newLine
				+ "continue forever if p<(1/2)^9 and that if p>1/2 the probability is zero."
				+ newLine
				+ "to:p<(1/2)^9, we take 3x3 blocks of the lattice, then the chance that a block is free of mines is 1/2."
				+ newLine
				+ "Thus there is a chance of a path of free block leading to infinity. "
				+ newLine
				+ "to :p>1/2 following from the idea of the proof of Kersten for bond percolation that there is no infinite "
				+ newLine
				+ "cluster at p=1/2. (We know that there exists a finite ring of mines around the origin.)"
				+ newLine
				+ "So there must be a critical mine probability pc from which on here does not even exists the chance to play"
				+ newLine
				+ "on forever."
				+ newLine
				+ "This simulations suggestest that this critical mine probabilty is close to 20 percent."
				+ newLine
				+ newLine
				+ "To the implementation:"
				+ newLine
				+ "To avoid the most-likely reason for an early stop of the clearing we assume that there are no mines at the first"
				+ newLine
				+ "point and the neighborhood of this point. We color not-identified mines red and marked mines black."
				+ newLine
				+ "Positions we have not considere jet that have no mines are yellow."
				+ newLine
				+ "The field that we are known to be  free of mines but we still have to evaluate are green (lightgreen if we considered "
				+ newLine
				+ "them before). Fields that we have cleared and do not need to reconsider draw in gray-shades. "
				+ newLine
				+ "The more mines are around them the darker they are."
				+ newLine
				+ "You give the number of analytic steps that you want to have carried out in the next turn."
				+ newLine
				+ newLine
				+ "To return to the program press the -Back- Button, that has replaced, "
				+ newLine
				+ "the help button or select a new part of the program";
		return content;
	}

	public void continueSim() {
		int in = utils.SmallTools.giveInteger(frame.inputs[3].getText());

		if (in == Integer.MAX_VALUE || in < 0) {
			JOptionPane.showMessageDialog(frame,
					"The input for the number of unions is not an integer: ");
			return;
		} else {
			MineSweeper m = ((MineSweeper) model);
			for (int t = 0; t < in && (m.inCleaning.size() > 0); t++) {
				m.checkNextPoint();
			}
			this.reDraw();
		}
	}

	@Override
	public void saveCurrentlyShownToFile(File file, String type) {
		try {
			MineSweeper m = ((MineSweeper) model);
			int w = 0;
			int h = 0;
			if (Math.max(m.cleared.length, m.cleared[0].length) > 400) {
				w = m.cleared.length;
				h = m.cleared[0].length;
			} else {
				w = m.cleared.length * 3;
				h = m.cleared[0].length * 3;
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

}
