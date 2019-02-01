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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.MainFrame;

import sandpiles.ErdosComponent;
import sandpiles.LatticeGraph;
import utils.SmallTools;

public class DrawLatticeGraph extends Outputapplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1256857222578920492L;
	public int grainsToAdd;
	BufferedImage bi;
	JCheckBox box1, box2;

	public DrawLatticeGraph(main.MainFrame fr) {
		this(fr, new LatticeGraph(5, 5, false), true);
	}

	public DrawLatticeGraph(MainFrame fr, LatticeGraph m, boolean initparam) {
		frame = fr;
		model = m;
		grainsToAdd = 0;
		if (initparam)
			this.initaliseParameterPanel();
	}

	@Override
	public void initaliseParameterPanel() {
		frame.setTitle("Sandpiles on a grid");
		// Reset upper part, where parameters are entered and the bottoms are
		frame.remove(frame.parameterentry);
		frame.parameterentry = new JPanel(new GridLayout(2, 6));

		box1 = new JCheckBox("Random Inital", false);
		box2 = new JCheckBox("Random Graining", false);
		frame.inputs = new JTextField[3];
		frame.inputs[0] = new JTextField("" + ((LatticeGraph) model).width);
		frame.inputs[1] = new JTextField("" + ((LatticeGraph) model).height);
		frame.inputs[2] = new JTextField("0");
		for (int i = 0; i < frame.inputs.length; i++) {
			frame.inputs[i].setFont(new Font("Arial", Font.ROMAN_BASELINE
					| Font.PLAIN, 22));
		}

		JButton redrawbuttom = new JButton(MainFrame.redraw);
		redrawbuttom.addActionListener(frame.listener);
		redrawbuttom.setActionCommand(MainFrame.redraw);

		frame.helpbutton = new JButton(MainFrame.askforhelp);

		frame.helpbutton.addActionListener(frame.listener);
		frame.helpbutton.setActionCommand(MainFrame.askforhelp);

		JButton continuebutton = new JButton("Add Once More");
		continuebutton.setMnemonic(KeyEvent.VK_G);
		continuebutton.setActionCommand(MainFrame.continueString);
		continuebutton.addActionListener(frame.listener);

		JButton generatebutton = new JButton("Generate new");
		generatebutton.setMnemonic(KeyEvent.VK_G);
		generatebutton.setActionCommand(MainFrame.gostring);
		generatebutton.addActionListener(frame.listener);

		frame.parameterentry.add(new JLabel("Wight"));
		frame.parameterentry.add(new JLabel("Height"));
		frame.parameterentry.add(new JLabel("Nr of Grains"));
		frame.parameterentry.add(box1);
		frame.parameterentry.add(redrawbuttom);
		frame.parameterentry.add(frame.helpbutton);

		frame.parameterentry.add(frame.inputs[0]);
		frame.parameterentry.add(frame.inputs[1]);
		frame.parameterentry.add(frame.inputs[2]);
		frame.parameterentry.add(box2);
		frame.parameterentry.add(continuebutton);
		frame.parameterentry.add(generatebutton);

		frame.add(frame.parameterentry, BorderLayout.NORTH);
	}

	public void paint(Graphics g) {
		if (grainsToAdd >= 0) {
			paintAnimate(g);
		} else {
			if ((bi != null)) {
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
		// super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// we ask for the size of the frame and then prepare the background
		Font myFont = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
		g2.setFont(myFont);

		LatticeGraph m = (LatticeGraph) model;
		int grx = (int) Math.round((d.width - 20) * 1.0 / (m.grid.length + 1));
		int gry = (int) Math.round((d.height - 20) * 1.0
				/ (m.grid[0].length + 1));
		int gridmin = Math.min(grx, gry);
		if ((System.currentTimeMillis() - drawnlasttime) < 200)
			// if we are in this case then we
			g.setColor(Color.white);
		g2.fillRect(0, 0, d.width, d.height);
		g.setColor(Color.black);
		if (m.grid != null)
			for (int i = 0; i < m.grid.length; i++)
				for (int j = 0; j < m.grid[i].length; j++) {
					paintNode(g2, grx * (i + 1), gry * (j + 1),
							m.grid[i][j].getHeight(), gridmin);
					m.grid[i][j].setUnChanged();
				}
		drawnlasttime = System.currentTimeMillis();
		g.setColor(Color.black);
		g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
	}

	public void paintAnimate(Graphics g) {
		// super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// we ask for the size of the frame and then prepare the background
		Dimension d = getSize();
		LatticeGraph m = (LatticeGraph) model;
		Font myFont = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
		g2.setFont(myFont);
		int grx = (int) Math.round((d.width - 20) * 1.0 / (m.grid.length + 1));
		int gry = (int) Math.round((d.height - 20) * 1.0
				/ (m.grid[0].length + 1));
		int gridmin = Math.min(grx, gry);
		g.setColor(Color.white);
		g2.fillRect(0, 0, d.width, d.height);
		g.setColor(Color.black);
		if (m.grid != null)
			for (int i = 0; i < m.grid.length; i++)
				for (int j = 0; j < m.grid[i].length; j++) {
					paintNode(g2, grx * (i + 1), gry * (j + 1),
							m.grid[i][j].getHeight(), gridmin);
					m.grid[i][j].setUnChanged();
				}

		for (; 0 < grainsToAdd; grainsToAdd--) {
			if (box2.isSelected()) {
				m.addGrainAndTopple((int) (m.width * Math.random()),
						(int) (m.height * Math.random()));
			} else {
				m.addGrainAndTopple((m.width - 1) / 2, (m.height - 1) / 2);
			}
			for (int i = 0; i < m.grid.length; i++)
				for (int j = 0; j < m.grid[0].length; j++) {
					if (m.grid[i][j].isChanged()) {
						paintNode(g2, grx * (i + 1), gry * (j + 1),
								m.grid[i][j].getHeight(), gridmin);
						m.grid[i][j].setUnChanged();
					}
				}
			if (grainsToAdd < 20) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
			}
		}
		g2.setColor(Color.black);
		g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
	}

	public void paintGrid() {
		Graphics2D g2 = (Graphics2D) this.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// we ask for the size of the frame and then prepare the background

		Dimension d = getSize();
		g2.setColor(Color.white);
		g2.fillRect(0, 0, d.width, d.height);
		g2.setColor(Color.black);
		Font myFont = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
		g2.setFont(myFont);
		g2.drawString(" Robert Fitzner", d.width - 180, d.height - 10);
		g2.setColor(Color.white);
	}

	private void paintNode(Graphics2D g2, int x, int y, int content, int size) {
		if (size > 40) {
			paintNodeWithNrContent(g2, x, y, content, false);
		} else if (size > 20) {
			paintNodeWithColor(g2, x, y, content, 12);
		} else if (size > 10) {
			paintNodeWithColor(g2, x, y, content, 6);
		} else
			paintNodeWithColor(g2, x, y, content, size);

	}

	public void paintNodeWithNrContent(Graphics2D g, int x, int y, int cont,
			boolean markiert) {
		Font myFont = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
		String inhalt = cont + "";
		Rectangle2D recht = myFont.getStringBounds(inhalt,
				g.getFontRenderContext());
		double h = recht.getHeight();
		double l = recht.getWidth();
		double d = Math.sqrt(h * h + l * l);
		g.setColor(Color.black);
		if (markiert) {
			g.setColor(Color.red);
		}
		Ellipse2D elipsOut = new Ellipse2D.Double(x - d / 2 - 2, y - d / 2 + 6
				- 2, d + 4, d + 4);
		// g.draw(elipsOut);
		g.fill(elipsOut);
		Ellipse2D elips = new Ellipse2D.Double(x - d / 2, y - d / 2 + 6, d, d);
		// g.setColor(Color.white);
		g.setColor(numberToColor(cont));
		g.draw(elips);
		// g.setColor(numberToColor(cont));
		g.fill(elips);
		g.setColor(Color.black);
		g.setFont(myFont);
		g.drawString(inhalt, Math.round(x - l / 2), Math.round(y + h / 2));
	}

	public void paintNodeWithContent(Graphics2D g, int x, int y, String inhalt,
			boolean markiert) {
		Font myFont = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
		Rectangle2D recht = myFont.getStringBounds(inhalt,
				g.getFontRenderContext());
		double h = recht.getHeight();
		double l = recht.getWidth();
		double d = Math.sqrt(h * h + l * l);
		g.setColor(Color.black);
		if (markiert) {
			g.setColor(Color.red);
		}
		Ellipse2D elipsOut = new Ellipse2D.Double(x - d / 2 - 2, y - d / 2 + 6
				- 2, d + 4, d + 4);
		// g.draw(elipsOut);
		g.fill(elipsOut);
		Ellipse2D elips = new Ellipse2D.Double(x - d / 2, y - d / 2 + 6, d, d);
		g.setColor(Color.white);
		g.draw(elips);
		g.setColor(Color.white);
		g.fill(elips);
		g.setColor(Color.black);
		g.setFont(myFont);
		g.drawString(inhalt, Math.round(x - l / 2), Math.round(y + h / 2));
	}

	public void paintNodeWithColor(Graphics2D g, int x, int y, int z, int d) {
		// g.setColor(Color.black);
		// Ellipse2D elipsOut = new Ellipse2D.Double(x - d / 2 - 2, y - d / 2 +
		// 6
		// - 2, d + 2, d + 2);
		// g.draw(elipsOut);
		// g.fill(elipsOut);
		Ellipse2D elips = new Ellipse2D.Double(x - d / 2, y - d / 2 + 6, d, d);
		g.setColor(numberToColor(z));
		g.draw(elips);
		g.fill(elips);
		g.setColor(Color.white);
	}

	private Color numberToColor(int z) {
		switch (z) {
		case 0:
			return new Color(255, 255, 255);
		case 1:
			return new Color(0, 0, 255);
		case 2:
			return new Color(0, 255, 0);
		case 3:
			return new Color(255, 0, 0);
		default:
			return new Color(0, 0, 0);
		}
	}

	@Override
	public String getType() {
		return main.MainFrame.sandPileslattice;
	}

	private Object[] readInput(int max1, int max2) {
		boolean correctinput = true;
		String newLine = System.getProperty("line.separator");
		StringBuffer output = new StringBuffer();
		output.append("Input Error" + newLine);

		int in1 = utils.SmallTools.giveInteger(frame.inputs[0].getText());
		int in2 = utils.SmallTools.giveInteger(frame.inputs[1].getText());

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
		Object[] input = readInput(1000, 1000);
		if (input.length != 1) {
			frame.remove(frame.center);
			this.model = null;
			int in1 = ((Integer) input[0]).intValue();
			int in2 = ((Integer) input[1]).intValue();

			DrawLatticeGraph newSim = new DrawLatticeGraph(frame,
					new LatticeGraph(in1, in2, box1.isSelected()), false);
			frame.center = newSim;
			newSim.box1 = box1;
			newSim.box2 = box2;
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
		Object[] input = readInput(1000, 1000);
		int in3 = utils.SmallTools.giveInteger(frame.inputs[2].getText());
		if (input.length != 1) {
			try {
				int in1 = ((Integer) input[0]).intValue();
				int in2 = ((Integer) input[1]).intValue();
				DrawLatticeGraph newSim = new DrawLatticeGraph(frame,
						new LatticeGraph(in1, in2, box1.isSelected()), false);
				LatticeGraph m = (LatticeGraph) newSim.model;
				int w = 1000;
				int h = 1000;
				if (in1 > 250 || in2 > 250) {
					if (in1 > 1000 || in2 > 1000) {
						w = in1 * 2;
						h = in2 * 2;
					} else {
						w = in1 * 10;
						h = in2 * 10;
					}
				}
				int nrframes = 20;
				if (in3 > 1000 || in3 < 1)
					in3 = 1;
				String tmp1 = file.getAbsolutePath();
				for (int i = 0; i < nrframes; i++) {
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
					javax.imageio.ImageIO.write(locbi, type, tmpfile);
					// clear memory
					locbi = new BufferedImage(2, 2,
							BufferedImage.TYPE_BYTE_INDEXED);
					ig2 = locbi.createGraphics();
					// advance
					if (i != nrframes - 1) {
						for (int k = 0; k < in3; k++) {
							if (box2.isSelected()) {
								m.addGrainAndTopple(
										(int) (m.width * Math.random()),
										(int) (m.height * Math.random()));
							} else {
								m.addGrainAndTopple((m.width - 1) / 2,
										(m.height - 1) / 2);
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
		} else {
			JOptionPane.showMessageDialog(frame,
					((StringBuffer) input[0]).toString());
		}
		frame.validate();

	}

	@Override
	public void saveCurrentlyShownToFile(File file, String type) {
		try {

			int w = 1000;
			int h = 1000;
			if (((LatticeGraph) model).width > 250
					|| ((LatticeGraph) model).height > 250) {
				if (((LatticeGraph) model).width > 1000
						|| ((LatticeGraph) model).height > 1000) {
					w = ((LatticeGraph) model).width * 2;
					h = ((LatticeGraph) model).height * 2;
				} else {
					w = ((LatticeGraph) model).width * 10;
					h = ((LatticeGraph) model).height * 10;
				}

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
	public void reDraw() {
		this.paint(getGraphics());

	}

	@Override
	public void continueSim() {
		int in3 = SmallTools.giveInteger(frame.inputs[2].getText());
		if (in3 == Integer.MAX_VALUE) {
			JOptionPane
					.showMessageDialog(
							frame,
							("The input for the number of grains to add is not a number: " + frame.inputs[2]
									.getText()));
		} else {
			if (in3 < 0 || in3 > 10000)
				JOptionPane
						.showMessageDialog(
								frame,
								("The input grain to add is to big or negative: " + frame.inputs[2]
										.getText()));
			else {
				grainsToAdd = in3;
				this.paint(getGraphics());
			}
		}
	}

	@Override
	public String getHelpText() {
		return "Here we display the Abilian sandpile model in a finite box.";
	}

}
