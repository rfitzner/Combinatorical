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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utils.SAWgens;
import utils.SmallTools;

import main.MainFrame;
import models.RandomWalks;

/**
 * With this class we draw memory walks in 2-D in the central frame.
 * 
 * @author Robert Fitzner
 * 
 */
@SuppressWarnings("serial")
public class DrawRandomWalks extends Outputapplet {
	boolean animate;
	BufferedImage bi;
	int delay;

	public DrawRandomWalks(MainFrame fr) {
		this(fr, new RandomWalks(1, 100, 0), true);
	}

	/**
	 * 
	 * Draw a number of walks (w) with same length(s) and memory(m)
	 */
	public DrawRandomWalks(MainFrame fr, RandomWalks m, boolean initparam) {
		frame = fr;
		model = m;
		delay = 1200;
		if (initparam)
			this.initaliseParameterPanel();
	}

	public void paint(Graphics g) {
		if (animate) {
			paint(g, getSize());
			return;
		} else if ((bi != null)) {
			double difference = Math.max(Math.abs(((getSize().height - bi
					.getHeight()) * 1.0 / Math.min(bi.getHeight(),
					getSize().height))), Math.abs(((getSize().width - bi
					.getWidth()) * 1.0 / Math.min(getSize().width,
					bi.getWidth()))));
			if ((difference < 0.4)) {
				g.drawImage(bi.getScaledInstance(getSize().width,
						getSize().height, Image.SCALE_DEFAULT), 0, 0, null);
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
				RenderingHints.VALUE_ANTIALIAS_ON);
		// we ask for the size of the frame and then prepare the background
		g.setColor(bg);
		g2.fillRect(0, 0, d.width, d.height);
		// Then I add my name to the lower right corner
		g2.setColor(new Color(200, 200, 200));
		Font myFont = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
		g2.setFont(myFont);
		g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
		g.setColor(fg);
		int[][] steps = ((RandomWalks) model).sequenceOfWalks;
		// get the maximal displacement that we need to scale the drawing.
		double maxdisplacement = 0;
		int maxlength = 0;
		for (int w = 0; w < steps.length; w++) {
			// scaling values
			double tmp = SAWgens.getMaximalMovement(steps[w]) * 2;
			if (tmp > maxdisplacement)
				maxdisplacement = tmp;
			if (maxlength < steps[w].length)
				maxlength = steps[w].length;
		}
		// so we draw on a grid The central position/origin will be at
		int[] shifting = { 0, d.width / 2, d.height / 2 };
		// the horizontal and vertical distance between the nodes are
		double gridcellVertsize = (d.width - 20) / maxdisplacement;
		double gridCellHightsize = (d.height - 20) / maxdisplacement;
		// scale to the central cycle and place it
		double diameterhor = 2 * Math.sqrt(maxlength) * gridcellVertsize;
		double diametervert = 2 * Math.sqrt(maxlength) * gridCellHightsize;
		// Draw a circle around the center with a radius of square root(of
		// number of steps of first walk)
		for (int i = 0; i < 4; i++) {
			g2.draw(new Ellipse2D.Double(shifting[1] - diameterhor / 2 - i / 2,
					shifting[2] - diametervert / 2 - i / 2, diameterhor + i,
					diametervert + i));
		}
		// draw Grid
		g2.setColor(Color.black);
		if (maxdisplacement < 31) {
			for (int i = 0; i < 2 * maxdisplacement; i++) {
				g2.draw(new Line2D.Double(10, 10 + i * gridCellHightsize,
						d.width - 10, 10 + i * gridCellHightsize));
				g2.draw(new Line2D.Double(10 + i * gridcellVertsize, 10, 10 + i
						* gridcellVertsize, 10 + d.height));
			}
		}
		for (int w = 0; w < steps.length; w++) {
			int[] position = { 0, 0, 0 };
			for (int i = 0; i < steps[w].length; i++) {
				// we translate this the walk as the sequence of direction into
				// points
				int[] movement = { 0, 0, 0 };
				switch (steps[w][i]) {
				case 1:
					movement[1] = -1;
					break;
				case -1:
					movement[1] = 1;
					break;
				case 2:
					movement[2] = 1;
					break;
				case -2:
					movement[2] = -1;
					break;
				case 0:// should not happen
					movement[0] = 1;
					break;
				default:
				}
				// delay only at the first time.
				if (animate) {
					try {
						Thread.sleep(delay / steps[w].length);
					} catch (InterruptedException e) {
					}
				}

				// I use a displacement of +w so that walks are not draw
				// atop of
				// each other
				SmallTools.drawThickLine(g2, shifting[1] + position[1]
						* gridcellVertsize + w, shifting[2] + position[2]
						* gridCellHightsize + w, shifting[1]
						+ (position[1] + movement[1]) * gridcellVertsize + w,
						shifting[2] + (position[2] + movement[2])
								* gridCellHightsize + w, 3, colors[w
								% colors.length]);
				position[2] += movement[2];
				position[1] += movement[1];
			}
		}
		animate = false;
	}

	@Override
	public String getType() {
		return MainFrame.walkDrawing;
	}

	private Object[] readInput(int max1, int max2, int max3) {
		boolean correctinput = true;
		String newLine = System.getProperty("line.separator");
		StringBuffer output = new StringBuffer();
		output.append("Input Error" + newLine);

		int in1 = utils.SmallTools.giveInteger(frame.inputs[0].getText());
		int in2 = utils.SmallTools.giveInteger(frame.inputs[1].getText());
		int in3 = utils.SmallTools.giveInteger(frame.inputs[2].getText());

		if (in1 == Integer.MAX_VALUE) {
			output.append("The input for the number of walks is not an integer: "
					+ frame.inputs[2].getText() + newLine);
			correctinput = false;
		} else {
			if ((in1 > max1) || (in1 < 0)) {
				output.append(" The number of walks to draw is to big."
						+ newLine);
				correctinput = false;
			}
		}
		if (in2 == Integer.MAX_VALUE) {
			output.append("The input for the walk length is not an integer: "
					+ frame.inputs[1].getText() + newLine);
			correctinput = false;
		} else {
			if ((in2 > max2) || (in2 < 0)) {
				output.append("The number of steps is to big." + newLine);
				correctinput = false;
			}
		}

		if (in3 == Integer.MAX_VALUE) {
			output.append("The input for the walk memory is not an integer: "
					+ frame.inputs[1].getText() + newLine);
			correctinput = false;
		} else {
			if ((in3 > max3) || (in3 < 0)) {
				output.append("The size of the memory is to big." + newLine);
				correctinput = false;
			}
		}
		if (correctinput) {
			Object[] values = new Object[3];
			values[0] = new Integer(in1);
			values[1] = new Integer(in2);
			values[2] = new Integer(in3);
			return values;
		}
		Object[] error = new Object[1];
		error[0] = output.toString();
		return error;
	}

	@Override
	public void generateNew() {
		Object[] input = this.readInput(40, 3000, 400);
		if (input.length != 1) {
			frame.remove(frame.center);
			int in1 = ((Integer) input[0]).intValue();
			int in2 = ((Integer) input[1]).intValue();
			int in3 = ((Integer) input[2]).intValue();
			frame.center = new DrawRandomWalks(frame, new RandomWalks(in1, in2,
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
		Object[] input = this.readInput(40, 3000, 400);
		if (input.length != 1) {
			try {
				int in1 = ((Integer) input[0]).intValue();
				int in2 = ((Integer) input[1]).intValue();
				int in3 = ((Integer) input[2]).intValue();
				DrawRandomWalks newSim = new DrawRandomWalks(frame,
						new RandomWalks(in1, in2, in3), false);
				BufferedImage locbi = new BufferedImage(1200, 1200,
						BufferedImage.TYPE_BYTE_INDEXED);
				Graphics2D ig2 = locbi.createGraphics();
				newSim.paint(ig2, new Dimension(1200, 1200));

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
			BufferedImage locbi = new BufferedImage(1200, 1200,
					BufferedImage.TYPE_BYTE_INDEXED);
			Graphics2D ig2 = locbi.createGraphics();
			this.paint(ig2, new Dimension(1200, 1200));
			javax.imageio.ImageIO.write(locbi, type, file);
			locbi = new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_INDEXED);
			JOptionPane.showMessageDialog(frame,
					"Saving as picture is completed.");
		} catch (java.io.IOException ie) {
			JOptionPane.showMessageDialog(frame, ie.toString());
		}

	}

	@Override
	public void initaliseParameterPanel() {
		frame.setTitle("Memory walks and self-avoiding walks");
		// Reset upper part, where parameters are entered and the bottoms are
		frame.remove(frame.parameterentry);
		frame.parameterentry = new JPanel(new GridLayout(2, 6));

		frame.inputs = new JTextField[3];
		frame.inputs[0] = new JTextField(""
				+ ((RandomWalks) model).steps.length);
		frame.inputs[1] = new JTextField("" + ((RandomWalks) model).steps[0]);
		frame.inputs[2] = new JTextField("" + ((RandomWalks) model).memory[0]);
		for (int i = 0; i < frame.inputs.length; i++) {
			frame.inputs[i].setFont(new Font("Arial", Font.ROMAN_BASELINE
					| Font.PLAIN, 22));
		}

		frame.parameterentry.add(new JLabel("# of walks"));
		frame.parameterentry.add(new JLabel("# of steps"));
		frame.parameterentry.add(new JLabel("Memory"));

		// JButton advancedbutton = new JButton(MainFrame.advanced);
		// advancedbutton.setMnemonic(KeyEvent.VK_A);
		// advancedbutton.setActionCommand(MainFrame.advanced);
		// advancedbutton.addActionListener(frame.listener);
		// frame.parameterentry.add(advancedbutton);
		frame.parameterentry.add(new JLabel());
		frame.helpbutton = new JButton(MainFrame.askforhelp);
		frame.parameterentry.add(frame.helpbutton);
		frame.helpbutton.addActionListener(frame.listener);
		frame.helpbutton.setActionCommand(MainFrame.askforhelp);

		frame.parameterentry.add(frame.inputs[0]);
		frame.parameterentry.add(frame.inputs[1]);
		frame.parameterentry.add(frame.inputs[2]);

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
		frame.add(frame.parameterentry, BorderLayout.NORTH);
	}

	@Override
	public void reDraw() {
		animate = true;
		paint(getGraphics());

	}

	@Override
	public void continueSim() {
		// dummy
	}

	@Override
	public String getHelpText() {
		String newLine = System.getProperty("line.separator");
		String content = "This part of the applet can draw random walks with arbitrary memory"
				+ newLine
				+ " As special case this implies the following cases: "
				+ newLine
				+ newLine
				+ " -- simple random walks, (memory =0),"
				+ newLine
				+ " -- non-backtracking random walks, (memory =2),"
				+ newLine
				+ " -- and self-avoiding walks  (memory =walk length)."
				+ newLine
				+ newLine
				+ " The Model:"
				+ newLine
				+ " A walk starts at the origin (the center of the big circle). At each step"
				+ newLine
				+ " the walk can go left/right or up/down to one of the neighbouring positions."
				+ newLine
				+ " If the memory is zero then the steps are independent of each other and the "
				+ newLine
				+ " program draws a simple random walks. When the memory M is bigger then zero  "
				+ newLine
				+ "the walk rembers its last M positions and will not revisit these M-places"
				+ newLine
				+ " in the next steps. This is then a memory-M walk. "
				+ newLine
				+ " A memory 2 walk called non-backtracking walk."
				+ newLine
				+ " If the memory equals the number of steps then we have a strictly"
				+ newLine
				+ " self-avoiding walk."
				+ newLine
				+ newLine
				+ " The parameters:"
				+ newLine
				+ " You can draw multiple walks with the same memory in this part of the applet."
				+ newLine
				+ " They will be draw after each other. The drawing of walk will take about 1.2 second"
				// +"The standard setting is that the drawing"
				+ newLine
				// +
				// " of a walk takes one second, if you want to change the drawing speed or you "
				// + newLine
				// +
				// " want to draw walks with different memories in the same picture please go to "
				// + newLine
				// + " the advanced part."
				+ newLine
				+ newLine
				+ " The number of walks that are drawn is restricted to 30."
				+ newLine
				+ " The number of steps should be given as at most 3000."
				+ newLine
				+ " The memory should be a integer between 0 and 400."
				+ newLine
				+ " For a larger memory the computation of a walk can take extremely long. "
				+ newLine
				+ " So if you want to generate a 3000 step memory-400 be prepared to wait."
				+ newLine
				+ " some minutes. The used generator for the memory walks creates the path "
				+ newLine
				+ " step by step and if it runs into a trap, where he can not continue the path,"
				+ newLine
				+ " it traces back it steps until it finds way out of the trap."
				+ newLine
				+ " This produces by no means the average behavior of a n-step memory walk"
				+ newLine
				+ " as more intertwined paths are favoured."
				+ newLine
				+ newLine
				+ "To return to the program press the -Back- Button, that has replaced, "
				+ newLine
				+ "the help button or select a new part of the program";
		return content;
	}
}
