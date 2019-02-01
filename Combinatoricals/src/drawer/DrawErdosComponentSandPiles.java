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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import sandpiles.AbstractNode;
import sandpiles.ErdosComponent;
import utils.SmallTools;

import main.MainFrame;
import models.BootstapBalls;

@SuppressWarnings("serial")
public class DrawErdosComponentSandPiles extends Outputapplet {
	public int grainsToAdd;
	boolean initDrawingDone;
	boolean randomGraining;
	JCheckBox box1, box2;
	BufferedImage bi;

	public DrawErdosComponentSandPiles(main.MainFrame fr) {
		this(fr, new ErdosComponent(10, 0.3, false), true);
	}

	public DrawErdosComponentSandPiles(main.MainFrame fenster,
			ErdosComponent m, boolean initparam) {
		this.frame = fenster;
		model = m;
		bi = null;
		initDrawingDone = false;
		if (initparam)
			this.initaliseParameterPanel();
	}

	@Override
	public void initaliseParameterPanel() {
		frame.setTitle("Sandpiles on ER- biggest component");
		frame.remove(frame.parameterentry);
		frame.parameterentry = new JPanel(new GridLayout(2, 6));

		box1 = new JCheckBox("Random Inital", false);
		box2 = new JCheckBox("Random Graining", false);

		frame.inputs = new JTextField[3];
		int tm = ((ErdosComponent) model).initialNumber;
		frame.inputs[0] = new JTextField("" + tm);
		frame.inputs[1] = new JTextField(""
				+ (((ErdosComponent) model).edgeProbability * tm));
		frame.inputs[2] = new JTextField("1");
		for (int i = 0; i < frame.inputs.length; i++) {
			frame.inputs[i].setFont(new Font("Arial", Font.ROMAN_BASELINE
					| Font.PLAIN, 22));
		}

		JButton redrawbuttom = new JButton(MainFrame.redraw);
		frame.parameterentry.add(redrawbuttom);
		redrawbuttom.addActionListener(frame.listener);
		redrawbuttom.setActionCommand(MainFrame.redraw);

		frame.helpbutton = new JButton(MainFrame.askforhelp);
		frame.parameterentry.add(frame.helpbutton);
		frame.helpbutton.addActionListener(frame.listener);
		frame.helpbutton.setActionCommand(MainFrame.askforhelp);

		JButton continuebutton = new JButton("Add Once More");
		continuebutton.setMnemonic(KeyEvent.VK_G);
		continuebutton.setActionCommand(MainFrame.continueString);
		continuebutton.addActionListener(frame.listener);
		frame.parameterentry.add(continuebutton);

		JButton generatebutton = new JButton("Generate new");
		generatebutton.setMnemonic(KeyEvent.VK_G);
		generatebutton.setActionCommand(MainFrame.gostring);
		generatebutton.addActionListener(frame.listener);
		frame.parameterentry.add(generatebutton);

		frame.parameterentry.add(new JLabel("Inital Nodes n"));
		frame.parameterentry.add(new JLabel("Edgeprob. X n"));
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

	public void paintAnimate(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// we ask for the size of the frame and then prepare the background
		ErdosComponent m = (ErdosComponent) model;
		int nrpoints = m.component.length;
		Dimension d = getSize();
		boolean redraw = true;
		int[] px = new int[nrpoints];
		int[] py = new int[nrpoints];
		for (int i = 0; i < nrpoints; i++) {
			px[i] = (int) Math.round(d.width / 2 + (d.width / 2 * 0.8)
					* Math.sin(2 * Math.PI * i / nrpoints));
			py[i] = (int) Math.round(d.height / 2 - (d.height / 2 * 0.8)
					* Math.cos(2 * Math.PI * i / nrpoints));
		}
		if (((grainsToAdd == 0) && redraw) || !initDrawingDone) {
			g.setColor(Color.white);
			g2.fillRect(0, 0, d.width, d.height);
			Font myFont = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
			g2.setFont(myFont);
			g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
			g2.setColor(Color.black);
			for (int i = 0; i < nrpoints; i++) {
				Iterator<AbstractNode> it = m.component[i].neighbors.iterator();
				while (it.hasNext()) {
					AbstractNode p = it.next();
					if (p.label > i) {
						g2.draw(new Line2D.Double(px[i], py[i], px[p.label],
								py[p.label]));
					}
				}
			}
			for (int i = 0; i < nrpoints - 1; i++) {
				paintNodeWithNrContent(g2, px[i], py[i], m.component[i].height,
						m.component[i].getNeighbors().size());
				m.component[i].setUnChanged();
			}
			paintDummyNode(g2, px[nrpoints - 1], py[nrpoints - 1]);
			initDrawingDone = true;
		} else {
			for (; 0 < grainsToAdd; grainsToAdd--) {
				m.addGrain(randomGraining);
				for (int i = 0; i < nrpoints - 1; i++) {
					if (m.component[i].isChanged()) {
						paintNodeWithNrContent(g2, px[i], py[i],
								m.component[i].height, m.component[i]
										.getNeighbors().size());
						m.component[i].setUnChanged();
					}
				}
			}
		}
	}

	public void paint(Graphics g, Dimension d) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// we ask for the size of the frame and then prepare the background
		ErdosComponent m = (ErdosComponent) model;
		int nrpoints = m.component.length;
		int[] px = new int[nrpoints];
		int[] py = new int[nrpoints];
		for (int i = 0; i < nrpoints; i++) {
			px[i] = (int) Math.round(d.width / 2 + (d.width / 2 * 0.8)
					* Math.sin(2 * Math.PI * i / nrpoints));
			py[i] = (int) Math.round(d.height / 2 - (d.height / 2 * 0.8)
					* Math.cos(2 * Math.PI * i / nrpoints));
		}
		// prepare the background
		g.setColor(Color.white);
		g2.fillRect(0, 0, d.width, d.height);
		g2.setColor(Color.black);
		Font myFont = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
		g2.setFont(myFont);
		g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
		// draw the initial graph first the edges
		for (int i = 0; i < nrpoints; i++) {
			Iterator<AbstractNode> it = m.component[i].neighbors.iterator();
			while (it.hasNext()) {
				AbstractNode p = it.next();
				if (p.label > i) {
					g2.draw(new Line2D.Double(px[i], py[i], px[p.label],
							py[p.label]));
				}
			}
		}
		// then the nodes
		for (int i = 0; i < nrpoints - 1; i++) {
			paintNodeWithNrContent(g2, px[i], py[i], m.component[i].height,
					m.component[i].getNeighbors().size());
			m.component[i].setUnChanged();
		}
		// and the dummy node
		paintDummyNode(g2, px[nrpoints - 1], py[nrpoints - 1]);
		initDrawingDone = true;
		for (; 0 < grainsToAdd; grainsToAdd--) {
			m.addGrain(randomGraining);
			for (int i = 0; i < nrpoints - 1; i++) {
				if (m.component[i].isChanged()) {
					paintNodeWithNrContent(g2, px[i], py[i],
							m.component[i].height, m.component[i]
									.getNeighbors().size());
					m.component[i].setUnChanged();
				}
			}
		}
	}

	@Override
	public String getType() {
		return MainFrame.sandPilesER;
	}

	public void paintNodeWithNrContent(Graphics2D g, int x, int y, int cont,
			int total) {
		if (((ErdosComponent) model).component.length < 20)
			paintNodeWithNrContent(g, x, y, cont, total, false);
		else if (((ErdosComponent) model).component.length < 50) {
			paintNodeWithColor(g, x, y, cont, total, 10);
		} else
			paintNodeWithColor(g, x, y, cont, total, 5);
	}

	public void paintNodeWithNrContent(Graphics2D g, int x, int y, int cont,
			int total, boolean markiert) {
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
		Ellipse2D elipsOut = new Ellipse2D.Double(x - (d + 4) / 2, y - (d + 4)
				/ 2, d + 4, d + 4);
		// g.draw(elipsOut);
		g.fill(elipsOut);
		Ellipse2D elips = new Ellipse2D.Double(x - d / 2, y - d / 2, d, d);
		// g.setColor(Color.white);
		g.setColor(numberToColor(cont, total));
		g.draw(elips);
		// g.setColor(numberToColor(cont));
		g.fill(elips);
		g.setColor(Color.black);
		g.setFont(myFont);
		g.drawString(inhalt, Math.round(x - l / 2), Math.round(y + h / 3));
	}

	public void paintNodeWithColor(Graphics2D g, int x, int y, int z, int t,
			int d) {

		Ellipse2D elips = new Ellipse2D.Double(x - d / 2, y - d / 2, d, d);
		g.setColor(numberToColor(z, t));
		g.draw(elips);
		g.fill(elips);
		g.setColor(Color.white);
	}

	public void drawThickLine(Graphics2D g, double x1, double y1, double x2,
			double y2, int thickness, Color c) {
		// The thick line is in fact a filled polygon
		g.setColor(c);
		int dX = (int) (x2 - x1);
		int dY = (int) (y2 - y1);
		// line length
		double lineLength = Math.sqrt(dX * dX + dY * dY);

		double scale = (double) (thickness) / (2 * lineLength);

		// The x,y increments from an endpoint needed to create a rectangle...
		double ddx = -scale * (double) dY;
		double ddy = scale * (double) dX;
		ddx += (ddx > 0) ? 0.5 : -0.5;
		ddy += (ddy > 0) ? 0.5 : -0.5;
		int dx = (int) ddx;
		int dy = (int) ddy;

		// Now we can compute the corner points...
		int xPoints[] = new int[4];
		int yPoints[] = new int[4];

		xPoints[0] = (int) x1 + dx;
		yPoints[0] = (int) y1 + dy;
		xPoints[1] = (int) x1 - dx;
		yPoints[1] = (int) y1 - dy;
		xPoints[2] = (int) x2 - dx;
		yPoints[2] = (int) y2 - dy;
		xPoints[3] = (int) x2 + dx;
		yPoints[3] = (int) y2 + dy;

		g.fillPolygon(xPoints, yPoints, 4);
	}

	private Color numberToColor(int z, int s) {
		return new Color(Math.min((int) (1.0 * z / s * 255), 255), Math.min(
				(int) ((1.0 - 1.0 * z / s) * 255), 255), 0);
	}

	public void paintDummyNode(Graphics2D g, int x, int y) {
		Font myFont = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
		String inhalt = "2";
		Rectangle2D recht = myFont.getStringBounds(inhalt,
				g.getFontRenderContext());
		double h = recht.getHeight();
		double l = recht.getWidth();
		double d = Math.sqrt(h * h + l * l);
		g.setColor(Color.gray);
		Ellipse2D elipsOut = new Ellipse2D.Double(x - d / 2 - 2, y - d / 2 + 6
				- 2, d + 4, d + 4);
		// g.draw(elipsOut);
		g.fill(elipsOut);
	}

	private Object[] readInput(int max1, int max2) {
		int in1 = utils.SmallTools.giveInteger(frame.inputs[0].getText());
		double in2 = utils.SmallTools.giveDouble(frame.inputs[1].getText());

		boolean correctinput = true;
		String newLine = System.getProperty("line.separator");
		StringBuffer output = new StringBuffer();
		output.append("Input Error" + newLine);

		if (in1 == Integer.MAX_VALUE) {
			output.append("The input for the number of initial nodes is not a number: "
					+ frame.inputs[0].getText() + newLine);
			correctinput = false;
		} else {
			if ((in1 > max1) || (in1 < 0)) {
				correctinput = false;
				output.append("The initial graph has to many points." + newLine);
			}
		}

		if (in2 == Integer.MAX_VALUE) {
			output.append("The input for the connectiveness does not make sense: "
					+ frame.inputs[1].getText() + newLine);
			correctinput = false;
		} else {
			if ((in2 > max2) || (in2 < 0)) {
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
		Object[] input = readInput(1000, 1000);
		// Object[] input = readInput(20000); applett
		if (input.length != 1) {
			frame.remove(frame.center);
			this.model = null;
			int in1 = ((Integer) input[0]).intValue();
			double in2 = ((Double) input[1]).doubleValue();
			DrawErdosComponentSandPiles newSim = new DrawErdosComponentSandPiles(
					frame,
					new ErdosComponent(in1, in2 / in1, box1.isSelected()),
					false);
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
		Object[] input = readInput(10000, 1000);
		// Object[] input = readInput(20000); applett
		int in3 = utils.SmallTools.giveInteger(frame.inputs[2].getText());
		if (input.length != 1) {
			try {
				int in1 = ((Integer) input[0]).intValue();
				double in2 = ((Double) input[1]).doubleValue();
				DrawErdosComponentSandPiles newSim = new DrawErdosComponentSandPiles(
						frame, new ErdosComponent(in1, in2 / in1,
								box1.isSelected()), false);
				newSim.box2 = box2;
				int w = 1200;
				int h = 1200;
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
							((ErdosComponent) newSim.model).addGrain(box2
									.isSelected());
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
	}

	@Override
	public void reDraw() {
		this.paint(getGraphics());
	}

	@Override
	public String getHelpText() {
		String newLine = System.getProperty("line.separator");
		return "In this part of the program we perform the sandpile model"
				+ newLine
				+ "on the biggest component of a Erdoes-Renyi graph."
				+ newLine
				+ newLine
				+ "To do that we first simulate a Erdoes-Renyi graph with n nodes"
				+ newLine
				+ "and edge probability p. When n*p>=1  there will exist one massive "
				+ newLine
				+ "component that will much bigger then all other comonents."
				+ newLine
				+ "The user can give as input the number of points of the ER graph and"
				+ newLine
				+ "the factor n*p. If you choose n*p<1 the resulting graph will be very thin."
				+ newLine
				+ "If on the other hand n*p>>1 then the graph fill be close to a complete graph."
				+ newLine
				+ newLine
				+ "Once the graph is created the user can add grains to the graph. If at one"
				+ newLine
				+ "point a node has the same number of grains on it as it has neighbors, it"
				+ newLine
				+ "will sent one grain to each of this neighbors. This proceeds until at each node"
				+ newLine
				+ "the number of grains is small then the number of neighbors."
				+ newLine
				+ "To be sure that this procedure ends at some time we mark one site as"
				+ newLine
				+ "dump site in which all grains get lost."
				+ newLine
				+ newLine
				+ "The user can choose wheather a newly generated graph is filled with random number of"
				+ newLine
				+ "grains or is just empty. At each time can be choosen whether a new grain"
				+ newLine
				+ "should be added to the top node or to random node."
				+ newLine
				+ newLine
				+ "To return to the program press the -Back- Button, that has replaced, "
				+ newLine
				+ "the help button or select a new part of the program";
	}

	@Override
	public void saveCurrentlyShownToFile(File file, String type) {
		try {
			int w = 1000;
			int h = 1000;
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
		int in3 = SmallTools.giveInteger(frame.inputs[2].getText());
		if (in3 == Integer.MAX_VALUE) {
			JOptionPane
					.showMessageDialog(
							frame,
							("The input for the number of grains to add is not a number: " + frame.inputs[2]
									.getText()));
		} else {
			grainsToAdd = in3;
			this.randomGraining = box2.isSelected();
			this.paint(getGraphics());
		}
	}
}
