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

import utils.SmallTools;

import main.MainFrame;
import models.BootstapBalls;
import models.BootstapBalls.Ball;

@SuppressWarnings("serial")
public class DrawBootstapBalls extends Outputapplet {
	boolean animate;
	BufferedImage bi;

	public DrawBootstapBalls(MainFrame fr) {
		this(fr, new BootstapBalls(100, 0.01), true);
	}

	public DrawBootstapBalls(MainFrame fr, BootstapBalls m, boolean initparam) {
		frame = fr;
		model = m;
		animate = true;
		if (initparam)
			this.initaliseParameterPanel();
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
		g.setColor(bg);
		g2.fillRect(0, 0, d.width, d.height);
		BootstapBalls m = (BootstapBalls) model;
		double whiteBorder = 2;
		double cycleBorder = 3;
		double xFactor = d.width / 2 - whiteBorder - cycleBorder;
		double yFactor = d.height / 2 - whiteBorder - cycleBorder;
		double cx = d.width * 1.0 / 2;
		double cy = d.height * 1.0 / 2;
		SmallTools.drawThickOval(g2, cx, cy, d.width * 1.0 / 2 - whiteBorder,
				d.height * 1.0 / 2 - whiteBorder, Color.black);
		SmallTools.drawThickOval(g2, cx, cy, d.width * 1.0 / 2 - whiteBorder
				- cycleBorder, d.height * 1.0 / 2 - whiteBorder - cycleBorder,
				Color.white);
		for (int i = 0; i < m.balls.size(); i++) {
			Ball ball = m.balls.get(i);
			SmallTools.drawThickOval(g2, cx + ball.x * xFactor, cy + ball.y
					* yFactor, ball.r * xFactor, ball.r * yFactor, Color.black);
		}
		g2.setColor(new Color(70, 70, 70));
		g2.setFont(new Font("Arial", Font.ITALIC | Font.PLAIN, 22));
		g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
		g2.drawString(" Number of balls in picture= " + m.balls.size(),
				d.width - 340, d.height - 35);
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
				output.append("To new points." + newLine);
			}
		}

		if (in2 == Integer.MAX_VALUE) {
			output.append("The input for the radi is not an integer: "
					+ frame.inputs[1].getText() + newLine);
			correctinput = false;
		} else {
			if ((in2 > 2) || (in2 < 0)) {
				correctinput = false;
				output.append("Input for radi does not make send." + newLine);
			}
		}

		if (correctinput) {
			Object[] values = new Object[3];
			values[0] = new Integer(in1);
			values[1] = new Double(in2);
			return values;
		}
		Object[] error = new Object[1];
		error[0] = output;
		return error;

	}

	@Override
	public void generateNew() {
		Object[] input = readInput(100000);
		// Object[] input = readInput(20000); applett
		if (input.length != 1) {
			frame.remove(frame.center);
			this.model = null;
			int in1 = ((Integer) input[0]).intValue();
			double in2 = ((Double) input[1]).doubleValue();
			DrawBootstapBalls newSim = new DrawBootstapBalls(frame,
					new BootstapBalls(in1, in2), false);
			frame.center = newSim;
			frame.add(frame.center, BorderLayout.CENTER);
			this.setVisible(false);
		} else {
			JOptionPane.showMessageDialog(frame,
					((StringBuffer) input[0]).toString());
		}
		frame.validate();
	}

	@Override
	public void generateNewForFile(File file, String type) {

		Object[] input = readInput(1000000);
		int in3 = utils.SmallTools.giveInteger(frame.inputs[2].getText());
		if (input.length == 1) {
			JOptionPane
					.showMessageDialog(frame, ((String) input[0]).toString());
		} else {
			try {
				int in1 = ((Integer) input[0]).intValue();
				double in2 = ((Double) input[1]).doubleValue();
				DrawBootstapBalls newSim = new DrawBootstapBalls(frame,
						new BootstapBalls(in1, in2), false);
				int w = 800;
				int h = 800;
				int nrframes = 100;
				if (in2 < 0.0125) {
					w = (int) Math.round(10 / in2);
					h = (int) Math.round(10 / in2);
				}
				if (in3 == 0) {
					in3 = (int) Math.ceil(in1 * 1.0 / 20) + 1;
				}
				System.out.println(in3 + "" + nrframes);
				boolean cont = true;
				String tmp1 = file.getAbsolutePath();
				for (int i = 0; i < nrframes && cont; i++) {
					// create the image
					BufferedImage locbi = new BufferedImage(w, h,
							BufferedImage.TYPE_BYTE_INDEXED);
					Graphics2D ig2 = locbi.createGraphics();
					newSim.paint(ig2, new Dimension(w, h));
					// write in file
					File tmpfile = new File(tmp1.substring(0,
							tmp1.lastIndexOf("."))
							+ (i + 1) + tmp1.substring(tmp1.lastIndexOf(".")));
					tmpfile.createNewFile();
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
					}
					javax.imageio.ImageIO.write(locbi, type, tmpfile);
					// clear memory
					locbi = new BufferedImage(2, 2,
							BufferedImage.TYPE_BYTE_INDEXED);
					ig2 = locbi.createGraphics();
					// advance
					if (i != nrframes - 1) {
						int j = 0;
						for (int k = 0; k < in3 && cont; k++) {
							j = ((BootstapBalls) newSim.model).unitOne(j);
							if (j == -1) {
								cont = false;// could not merge anymore
								System.out.println("");
							}
						}
					}
				}
				JOptionPane.showMessageDialog(frame,
						"Saving the sequence of pictures is completed.");
			} catch (java.io.IOException ie) {
				JOptionPane.showMessageDialog(frame, ie.toString());
			} catch (java.lang.OutOfMemoryError e) {
				JOptionPane.showMessageDialog(frame,
						"You have just blown your memory.");
			}

		}

	}

	@Override
	public void initaliseParameterPanel() {
		frame.setTitle("Bootstrap percolation on the unit ball");

		frame.inputs = new JTextField[3];
		frame.inputs[0] = new JTextField(""
				+ ((BootstapBalls) model).nrOfInitialballs);
		frame.inputs[1] = new JTextField(""
				+ ((BootstapBalls) model).initialradius);
		frame.inputs[2] = new JTextField("1");

		for (int i = 0; i < frame.inputs.length; i++) {
			frame.inputs[i].setFont(new Font("Arial", Font.ROMAN_BASELINE
					| Font.PLAIN, 22));
		}
		// Reset upper part, where parameters are entered and the bottoms are
		frame.remove(frame.parameterentry);
		frame.parameterentry = new JPanel(new GridLayout(2, 4));
		frame.parameterentry.add(new JLabel("# of inital points"));
		frame.parameterentry.add(new JLabel("Radius of points "));

		JButton continuebutton = new JButton(MainFrame.continueString);

		continuebutton.addActionListener(frame.listener);
		continuebutton.setActionCommand(MainFrame.continueString);
		frame.parameterentry.add(continuebutton);

		frame.helpbutton = new JButton(MainFrame.askforhelp);
		frame.helpbutton.addActionListener(frame.listener);
		frame.helpbutton.setActionCommand(MainFrame.askforhelp);
		frame.parameterentry.add(frame.helpbutton);

		frame.parameterentry.add(frame.inputs[0]);
		frame.parameterentry.add(frame.inputs[1]);
		frame.parameterentry.add(frame.inputs[2]);

		JButton generatebutton = new JButton("Generate new");
		generatebutton.setMnemonic(KeyEvent.VK_G);
		generatebutton.setActionCommand(MainFrame.gostring);
		generatebutton.addActionListener(frame.listener);
		frame.parameterentry.add(generatebutton);
		frame.add(frame.parameterentry, BorderLayout.NORTH);

	}

	@Override
	public void reDraw() {
		this.paint(getGraphics());

	}

	@Override
	public String getType() {
		return MainFrame.bootstrapBalls;
	}

	@Override
	public String getHelpText() {
		String newLine = System.getProperty("line.separator");
		return "The model:"
				+ newLine
				+ "We place a number n of point with given radius on the"
				+ newLine
				+ "unit disc. When two ball intersect then we replace these two balls"
				+ newLine
				+ "with the smallest balls complete covering both balls."
				+ newLine
				+ "We continue this until there are no more overlapping balls."
				+ newLine
				+ "The question is then whether this procedure creates a ball covering"
				+ newLine
				+ "the hole unit disc."
				+ newLine
				+ "Whether there exists a covering ball in the end depends the number of points(n)"
				+ newLine
				+ "and the radi(r) of the these initial balls. For each radi there exist a critical"
				+ newLine
				+ "number of initial balls nc(r), such that if n>nc(r) in the finite stage of the"
				+ newLine
				+ "developmenthas there exists a covering ball with high probability. Moreover if "
				+ newLine
				+ "we begin with n<nc(r) then there is next to no chance to have a covering ball in"
				+ newLine
				+ "the end."
				+ newLine
				+ newLine
				+ "At each step you can choose how many ball you want to merge in the next steps."
				+ newLine
				+ "When selecting drawing the current picture, the current stage of the simulation"
				+ newLine
				+ "is drawn into a file with a resolution that shows all details of the stage."
				+ newLine
				+ "When drawing a new simulation into a file a bootstrap with the given parametes is draw"
				+ newLine
				+ "into a number of file, where between each frame the given number of balls are merged."
				+ newLine
				+ newLine
				+ "To return to the program press the -Back- Button, that has replaced, "
				+ newLine
				+ "the help button or select a new part of the program";
	}

	@Override
	public void saveCurrentlyShownToFile(File file, String type) {
		try {
			int w = 10000;
			int h = 10000;
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
		int in = utils.SmallTools.giveInteger(frame.inputs[2].getText());
		if (in == Integer.MAX_VALUE || in < 0) {
			JOptionPane.showMessageDialog(frame,
					"The input for the number of mergings is not an integer: ");
			return;
		} else {
			long time = System.currentTimeMillis();
			int j = 0;
			for (int i = 0; i < in; i++) {
				j = ((BootstapBalls) model).unitOne(j);
				if (j != -1) {
					if (System.currentTimeMillis() - time > 3000) {
						this.reDraw();
						time = System.currentTimeMillis();
					}
				} else {
					this.reDraw();
					JOptionPane.showMessageDialog(frame,
							" Found nothing to merge anymore.");
					return;
				}
			}
			this.reDraw();
		}
	}

}
