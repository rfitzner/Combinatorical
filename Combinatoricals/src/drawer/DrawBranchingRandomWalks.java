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
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utils.SAWgens;
import utils.SmallTools;

import main.MainFrame;
import models.BranchingRandomWalks;
import models.RandomWalks;
import models.BranchingRandomWalks.Particle;

/**
 * With this class we draw memory walks in 2-D in the central frame.
 * 
 * @author Robert Fitzner
 * 
 */
@SuppressWarnings("serial")
public class DrawBranchingRandomWalks extends Outputapplet {
	boolean animate;
	BufferedImage bi;
	int delay,until;

	public DrawBranchingRandomWalks(MainFrame fr) {
		this(fr, BranchingRandomWalks.createBranchingRandomWalks(0.3, 4,0), true);
	}

	/**
	 * 
	 * Draw a number of walks (w) with same length(s) and memory(m)
	 */
	public DrawBranchingRandomWalks(MainFrame fr, BranchingRandomWalks m,
			boolean initparam) {
		frame = fr;
		model = m;
		delay = 100;
		until=2200;
		animate = true;
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
		// get the maximal displacement that we need to scale the drawing.
		double maxdisplacement = 1 + Math.max(
				((BranchingRandomWalks) model).maxx
						- ((BranchingRandomWalks) model).minx,
				((BranchingRandomWalks) model).maxy
						- ((BranchingRandomWalks) model).miny);
		// the horizontal and vertical distance between the nodes are
		double gridcellVertsize = (d.width - 40) / maxdisplacement;
		double gridCellHightsize = (d.height - 40) / maxdisplacement;
		// so we draw on a grid The central position/origin will be at
		int[] shifting = { 0, d.width / 2 -(int)((((BranchingRandomWalks) model).maxx
						+ ((BranchingRandomWalks) model).minx)*gridcellVertsize/ 2), d.height / 2-(int)((((BranchingRandomWalks) model).maxy
								+ ((BranchingRandomWalks) model).miny)*gridCellHightsize / 2) };


		// draw Grid
		g2.setColor(Color.black);
		LinkedList<Particle> currentGeneration=new LinkedList<Particle>();
		currentGeneration.add( ((BranchingRandomWalks) model).root);
		boolean cont=true;
		int generation=0;
		while (cont){
			LinkedList<Particle> nextGeneration=new LinkedList<Particle>();
			ListIterator<Particle> it=currentGeneration.listIterator();
			while(it.hasNext()){
				Particle current=it.next();
				nextGeneration.addAll(current.children);
				if(generation>0){
					Dimension start=current.parent.location;
				Dimension end=current.location;
				SmallTools.drawThickLine(g2, 
						shifting[1]+ start.width * gridcellVertsize,
						shifting[2] + start.height* gridCellHightsize,
						shifting[1] + end.width* gridcellVertsize, 
						shifting[2]	+ end.height* gridCellHightsize,
						3, colors[generation % colors.length]);
				if(generation==until+1)g2.drawString(current.parent.children.size()+"",
						(int)(shifting[1]+ start.width * gridcellVertsize),
						(int)(shifting[2] + start.height* gridCellHightsize));

				}
			}
			
			if (animate) {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
				}
			}
			
			if ((nextGeneration.isEmpty())|| (generation>until)){
				cont=false;
			} else {
				currentGeneration=nextGeneration;
				generation++;
				
			}
		}
		animate = false;
	}

	@Override
	public String getType() {
		return MainFrame.walkDrawing;
	}

	private Object[] readInput(int max1, double max2) {
		boolean correctinput = true;
		String newLine = System.getProperty("line.separator");
		StringBuffer output = new StringBuffer();
		output.append("Input Error" + newLine);

		int in1 = utils.SmallTools.giveInteger(frame.inputs[0].getText());
		int in3 = utils.SmallTools.giveInteger(frame.inputs[2].getText());
		int in4 = utils.SmallTools.giveInteger(frame.inputs[3].getText());
		double in2 = utils.SmallTools.giveDouble(frame.inputs[1].getText());

		if (in1 == Integer.MAX_VALUE) {
			output.append("The input for the number of steps is not an integer: "
					+ frame.inputs[0].getText() + newLine);
			correctinput = false;
		} else {
			if ((in1 > max1) || (in1 < 0)) {
				output.append(" The number of steps is to big." + newLine);
				correctinput = false;
			}
		}
		if (in2 == Double.MAX_VALUE) {
			output.append("The input for intensity is not a number; "
					+ frame.inputs[1].getText() + newLine);
			correctinput = false;
		} else {
			if ((in2 > max2) || (in2 < 0)) {
				output.append("The intensity does not work." + newLine);
				correctinput = false;
			}
		}

		if (in3 == Integer.MAX_VALUE) {
			output.append("The input for the minimal number of steps is not an integer: "
					+ frame.inputs[0].getText() + newLine);
			correctinput = false;
		} else {
			if ((in1 > 10000) || (in1 < 0)) {
				output.append(" The minimal number of steps is to big." + newLine);
				correctinput = false;
			}
		}
		
		if (in4 == Integer.MAX_VALUE) {
			output.append("The input for the delay is not an integer: "
					+ frame.inputs[0].getText() + newLine);
			correctinput = false;
		} else {
			if ((in1 > max1) || (in1 < 0)) {
				output.append(" The delay does not make sense." + newLine);
				correctinput = false;
			}
		}
		
		if (correctinput) {
			Object[] values = new Object[4];
			values[0] = new Integer(in1);
			values[1] = new Double(in2);
			values[2] = new Integer(in3);
			values[3] = new Integer(in4);
			return values;
		}
		Object[] error = new Object[1];
		error[0] = output.toString();
		return error;
	}

	@Override
	public void generateNew() {
		Object[] input = this.readInput(300, 60);
		if (input.length != 1) {
			frame.remove(frame.center);
			int in1 = ((Integer) input[0]).intValue();
			int in3 = ((Integer) input[2]).intValue();
			double in2 = ((Double) input[1]).doubleValue();
			delay = ((Integer) input[3]).intValue();
			System.out.println("create new drawer with "+in2);
			frame.center = new DrawBranchingRandomWalks(frame,
					BranchingRandomWalks.createBranchingRandomWalks(in2, in1,in3), false);
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
		Object[] input = this.readInput(200, 4);
		if (input.length != 1) {
			try {
				int in1 = ((Integer) input[0]).intValue();
				int in2 = ((Integer) input[1]).intValue();
				int in3 = ((Integer) input[2]).intValue();
				DrawBranchingRandomWalks newSim = new DrawBranchingRandomWalks(
						frame, BranchingRandomWalks.createBranchingRandomWalks(in2, in1,in3), false);
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
		frame.setTitle("Branching random walks");
		// Reset upper part, where parameters are entered and the bottoms are
		frame.remove(frame.parameterentry);
		frame.parameterentry = new JPanel(new GridLayout(2, 4));

		frame.inputs = new JTextField[4];
		frame.inputs[0] = new JTextField(""
				+ ((BranchingRandomWalks) model).steps);
		frame.inputs[1] = new JTextField(
				""+ (Math.round(((BranchingRandomWalks) model).intensity * 1000) * 1.0 / 1000));
		frame.inputs[2] = new JTextField(""
				+ ((BranchingRandomWalks) model).minNumber);
		frame.inputs[3] = new JTextField(""
				+ this.delay);
		
		for (int i = 0; i < frame.inputs.length; i++) {
			frame.inputs[i].setFont(new Font("Arial", Font.ROMAN_BASELINE
					| Font.PLAIN, 22));
		}

		frame.parameterentry.add(new JLabel("MaxGenerations"));
		frame.parameterentry.add(new JLabel("Intensitv"));
		frame.parameterentry.add(new JLabel("Minimal size"));
		frame.parameterentry.add(new JLabel("Drawing Delay"));

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
		until=2000;
		paint(getGraphics());

	}

	@Override
	public void continueSim() {
		until++;
		if(until>2000) until=0;
		bi=null;
		paint(getGraphics());
	}

	@Override
	public String getHelpText() {
		String newLine = System.getProperty("line.separator");
		String content = "This part of the applet can draw branching random walks"
				+ newLine
				+ "To return to the program press the -Back- Button, that has replaced, "
				+ newLine
				+ "the help button or select a new part of the program";
		return content;
	}
}
