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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.MainFrame;
import models.LorentzGas;

/**
 * This class generates let a random walk explore a percolation cluster. So we
 * generate a simple random walks that only walks along occupied edges. Every
 * time the walk encounters a new edge we check whether the edge is occupied or
 * not then the walk proceeds.
 * 
 * @author Robert Fitzner
 */
public class DrawLorentzGas extends Outputapplet {
	private static final long serialVersionUID = 1L;
	boolean animate;
	BufferedImage bi;
	int delay;

	public DrawLorentzGas(MainFrame fr) {
		this(fr, new LorentzGas(1, 0.5, 10), 1500, true);
	}

	public DrawLorentzGas(MainFrame fr, LorentzGas m, int d, boolean initparam) {
		model = m;
		frame = fr;
		d = delay;
		animate = true;
		if (initparam)
			initaliseParameterPanel();
	}

	@Override
	public void initaliseParameterPanel() {
		frame.setTitle("Lorentz Gas Model in d=2");
		// Reset upper part, where parameters are entered and the bottoms are
		frame.remove(frame.parameterentry);
		frame.parameterentry = new JPanel(new GridLayout(2, 4));

		frame.parameterentry.add(new JLabel("Prob. of a molecule"));
		frame.parameterentry.add(new JLabel("Prob. of a NW-SE bounce"));

		JButton redrawButton = new JButton(MainFrame.redraw);
		frame.parameterentry.add(redrawButton);
		redrawButton.addActionListener(frame.listener);
		redrawButton.setActionCommand(MainFrame.redraw);

		frame.helpbutton = new JButton(MainFrame.askforhelp);
		frame.parameterentry.add(frame.helpbutton);
		frame.helpbutton.addActionListener(frame.listener);
		frame.helpbutton.setActionCommand(MainFrame.askforhelp);
		frame.inputs = new JTextField[2];
		frame.inputs[0] = new JTextField("" + ((LorentzGas) model).atomprob);
		frame.inputs[1] = new JTextField("" + ((LorentzGas) model).dirprob);
		for (int i = 0; i < frame.inputs.length; i++) {
			frame.inputs[i].setFont(new Font("Arial", Font.ROMAN_BASELINE
					| Font.PLAIN, 22));
		}
		frame.parameterentry.add(frame.inputs[0]);
		frame.parameterentry.add(frame.inputs[1]);
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
			paint(g, this.getSize());
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

	public void paint(Graphics g, Dimension d) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// get the size of the frame to scale the displaying and initialize
		// the background
		LorentzGas m = (LorentzGas) model;
		g.setColor(bg);
		g2.fillRect(0, 0, d.width, d.height);
		// add my name to the lower right corner
		g.setColor(fg);
		double l = 0.3;
		double gridsizeX = (d.width - 20) * 1.0 / (m.maxx - m.minx + 2);
		double gridsizeY = (d.height - 20) * 1.0 / (m.maxy - m.miny + 2);
		double centerX = gridsizeX * (-m.minx + 1);
		double centerY = gridsizeY * (-m.miny + 1);
		g2.setColor(new Color(100, 100, 100));
		if (Math.max(m.maxx - m.minx, m.maxy - m.miny) < 100) {
			Iterator<Entry<Integer, HashMap<Integer, boolean[]>>> it1 = m.seenSites
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
						if (tmp[1])
							g2.draw(new Line2D.Double((x - l) * gridsizeX
									+ centerX, (y - l) * gridsizeY + centerY,
									((x + l)) * gridsizeX + centerX, (y + l)
											* gridsizeY + centerY));
						else
							g2.draw(new Line2D.Double((x - l) * gridsizeX
									+ centerX, (y + l) * gridsizeY + centerY,
									((x + l)) * gridsizeX + centerX, (y - l)
											* gridsizeY + centerY));
					}
				}
			}
		}
		// draw the path
		int end = m.path.size();

		for (int i = 0; i < end - 1; i++) {
			g2.setColor(Color.red);
			g2.draw(new Line2D.Double(
					m.path.get(i).width * gridsizeX + centerX,
					m.path.get(i).height * gridsizeY + centerY, m.path
							.get(i + 1).width * gridsizeX + centerX, m.path
							.get(i + 1).height * gridsizeY + centerY));
			try {
				if (animate) {
					Thread.sleep(Math.min(10000 / m.path.size(), 200));
					if (animate)
						utils.SmallTools.drawThickBall(g2, i * 1.0 / end
								* d.width, 0, 3, new Color(0, 255, 0));
				}
			} catch (InterruptedException ed) {
			}

		}
		g2.setColor(new Color(30, 30, 30));
		Font f = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
		g2.setFont(f);
		g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
		String a = " Return was";
		if (!m.path.get(m.path.size() - 1).equals(new Dimension(0, 0)))
			a += " not ";
		a += "succesful while doing " + (m.path.size() - 1) + " steps.";
		g2.drawString(a,
				d.width
						- (int) f.getStringBounds(a, g2.getFontRenderContext())
								.getWidth() - 20, d.height - 35);
		animate = false;
	}

	@Override
	public String getType() {
		return MainFrame.clusterExpTwoD;
	}

	private Object[] readInput() {

		double in1 = utils.SmallTools.giveDouble(frame.inputs[0].getText());
		double in2 = utils.SmallTools.giveDouble(frame.inputs[1].getText());
		// int in3 = utils.SmallTools.giveInteger(frame.inputs[2].getText());

		boolean correctinput = true;
		String newLine = System.getProperty("line.separator");
		StringBuffer output = new StringBuffer();
		output.append("Input Error" + newLine);

		if (in1 == Integer.MAX_VALUE) {
			output.append("The input for the molecule probability is not a numnber: "
					+ frame.inputs[0].getText() + newLine);
			correctinput = false;
		} else {
			if ((in1 > 1) || (in1 < 0)) {
				correctinput = false;
				output.append("Input is not a probability." + newLine);
			}
		}

		if (in2 == Integer.MAX_VALUE) {
			output.append("The input for direction probability: "
					+ frame.inputs[1].getText() + newLine);
			correctinput = false;
		} else {
			if ((in2 > 1) || (in2 < 0)) {
				correctinput = false;
				output.append("Input is not a probability." + newLine);
			}
		}

		if (correctinput) {
			Object[] values = new Object[2];
			values[0] = new Double(in1);
			values[1] = new Double(in2);
			return values;
		}
		Object[] error = new Object[1];
		error[0] = output.toString();
		return error;

	}

	@Override
	public void generateNew() {
		Object[] input = readInput();
		if (input.length != 1) {
			double in1 = ((Double) input[0]).doubleValue();
			double in2 = ((Double) input[1]).doubleValue();
			try {
				frame.remove(frame.center);
				this.model = null;
				DrawLorentzGas newSim = new DrawLorentzGas(frame,
						new LorentzGas(in1, in2, (int) Math.pow(10, 7)), 1000,
						false);
				frame.center = newSim;
				frame.add(frame.center, BorderLayout.CENTER);
				newSim.animate = true;
				this.setVisible(false);
			} catch (java.lang.OutOfMemoryError e) {
				JOptionPane
						.showMessageDialog(frame,
								"You have just blown your memory. Initial new small version.");
				DrawLorentzGas newSim = new DrawLorentzGas(frame,
						new LorentzGas(in1, in2, (int) Math.pow(10, 3)), 1000,
						false);
				frame.center = newSim;
				frame.add(frame.center, BorderLayout.CENTER);
				newSim.animate = false;
			}
		} else {
			JOptionPane
					.showMessageDialog(frame, ((String) input[0]).toString());
		}
		frame.validate();
	}

	@Override
	public void generateNewForFile(File file, String type) {
		Object[] input = readInput();
		if (input.length != 1) {
			try {
				double in1 = ((Double) input[0]).doubleValue();
				double in2 = ((Double) input[1]).doubleValue();
				DrawLorentzGas newSim = new DrawLorentzGas(frame,
						new LorentzGas(in1, in2, (int) Math.pow(10, 8)), 1000,
						false);
				LorentzGas m = (LorentzGas) newSim.model;
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
		this.paint(getGraphics());

	}

	@Override
	public String getHelpText() {
		String newLine = System.getProperty("line.separator");
		return "In this model we place at each point of the Z^2 lattice a particle"
				+ newLine
				+ "with probabilty p. Further we give each particle either an orientation."
				+ newLine
				+ "It will orient NW-SE with probability q and NE-SW with probability (1-q)."
				+ newLine
				+ "Then we start a ray of light that will be reflected by each"
				+ newLine
				+ "particle following the orientation of the particles."
				+ newLine
				+ "For p=1 and q=1/2 the ray will always return to the point of origin."
				+ newLine
				+ "For other p and q this is not clear."
				+ newLine
				+ newLine
				+ "To return to the program press the -Back- Button, that has replaced, "
				+ newLine
				+ "the help button or select a new part of the program";
	}

	@Override
	public void saveCurrentlyShownToFile(File file, String type) {
		try {
			LorentzGas m = ((LorentzGas) model);
			int w = Math.max((m.maxx - m.minx) * 5, 800);
			int h = Math.max((m.maxy - m.miny) * 5, 800);
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
		// Dummy to easy to blow the memory
	}

}