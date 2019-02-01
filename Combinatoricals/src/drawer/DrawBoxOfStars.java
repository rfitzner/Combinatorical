package drawer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utils.SmallTools;
import main.MainFrame;
import models.BoxOfStars;
import models.LorentzGas;

/**
 * This class generates let a random walk explore a percolation cluster. So we
 * generate a simple random walks that only walks along occupied edges. Every
 * time the walk encounters a new edge we check whether the edge is occupied or
 * not then the walk proceeds.
 * 
 * @author Robert Fitzner
 */
public class DrawBoxOfStars extends Outputapplet {
	private static final long serialVersionUID = 1L;
	boolean animate;
	BufferedImage bi;
	int delay;

	public DrawBoxOfStars(MainFrame fr) {
		this(fr, new BoxOfStars(100,10, 1,500), 1500, true);
	}

	public DrawBoxOfStars(MainFrame fr, BoxOfStars m, int d, boolean initparam) {
		model = m;
		frame = fr;
		delay=d;
		animate = true;
		if (initparam)
			initaliseParameterPanel();
	}

	@Override
	public void initaliseParameterPanel() {
		frame.setTitle("Create stars in a box.");
		// Reset upper part, where parameters are entered and the bottoms are
		frame.remove(frame.parameterentry);
		frame.parameterentry = new JPanel(new GridLayout(3, 4));

		
		frame.inputs = new JTextField[4];
		frame.inputs[0] = new JTextField("" + ((BoxOfStars) model).n);
		frame.inputs[1] = new JTextField("" + ((BoxOfStars) model).singletonweight);
		frame.inputs[2] = new JTextField("" + ((BoxOfStars) model).drawsteps);
		frame.inputs[3] = new JTextField("" + ((BoxOfStars) model).delay);
		for (int i = 4; i < frame.inputs.length; i++) {
			frame.inputs[i].setFont(new Font("Arial", Font.ROMAN_BASELINE
					| Font.PLAIN, 22));
		}
			
		frame.parameterentry.add(new JLabel("Nr. of particles"));
		frame.parameterentry.add(frame.inputs[0]);

		frame.parameterentry.add(new JLabel("Prefence for singleton"));
		frame.parameterentry.add(frame.inputs[1]);

		
		frame.parameterentry.add(new JLabel("Moves per frame"));
		frame.parameterentry.add(frame.inputs[2]);
		
		frame.parameterentry.add(new JLabel("Delay per frames"));
		frame.parameterentry.add(frame.inputs[3]);
		
		
		frame.parameterentry.add(new JLabel());
		
		JButton redrawButton = new JButton(MainFrame.redraw);
		frame.parameterentry.add(redrawButton);
		redrawButton.addActionListener(frame.listener);
		redrawButton.setActionCommand(MainFrame.redraw);

		frame.helpbutton = new JButton(MainFrame.askforhelp);
		frame.parameterentry.add(frame.helpbutton);
		frame.helpbutton.addActionListener(frame.listener);
		frame.helpbutton.setActionCommand(MainFrame.askforhelp);


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
		BoxOfStars m = (BoxOfStars) model;
		g.setColor(bg);
		g2.fillRect(0, 0, d.width, d.height);

		Font f = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
		FontMetrics fm = g.getFontMetrics();
		g2.setFont(f);
		g2.setColor(new Color(30, 30, 30));
		g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
		// add my name to the lower right corner
		g.setColor(fg);
		double centralboxsize = 0.9*Math.min(d.width, d.height);
		double xoffset= (d.width-centralboxsize)*1.0/2;
		double yoffset= (d.height-centralboxsize)*1.0/2;
		SmallTools.drawThickLine(g2, xoffset, yoffset , xoffset+centralboxsize, yoffset , 2, Color.black);
		SmallTools.drawThickLine(g2, xoffset, yoffset , xoffset, yoffset +centralboxsize , 2, Color.black);
		SmallTools.drawThickLine(g2, xoffset+centralboxsize, yoffset , xoffset+centralboxsize, yoffset+centralboxsize , 2, Color.black);
		SmallTools.drawThickLine(g2, xoffset, yoffset +centralboxsize, xoffset+centralboxsize, yoffset+centralboxsize , 2, Color.black);
		int t=1;
		
		if (!this.animate)	t=m.sites.size()-1;
		
		while (t<m.sites.size()) {
			g.setColor(bg);
			g2.fillRect(0, 0, d.width, d.height);
			SmallTools.drawThickLine(g2, xoffset, yoffset , xoffset+centralboxsize, yoffset , 3, Color.black);
			SmallTools.drawThickLine(g2, xoffset, yoffset , xoffset, yoffset +centralboxsize , 3, Color.black);
			SmallTools.drawThickLine(g2, xoffset+centralboxsize, yoffset , xoffset+centralboxsize, yoffset+centralboxsize , 3, Color.black);
			SmallTools.drawThickLine(g2, xoffset, yoffset +centralboxsize, xoffset+centralboxsize, yoffset+centralboxsize , 3, Color.black);
			g2.setColor(new Color(30, 30, 30));
			g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
			
			 
			g.setColor(fg);
			Iterator<double[]> itsides =m.sites.get(t).iterator();
			Iterator<double[]> itcraters =m.craters.get(t).iterator();
	

		    
			while (itsides.hasNext()) {
				double[] point = itsides.next();
				String out =intoutput(point[2]);
				Rectangle2D rect = fm.getStringBounds(out, g2);
				g2.drawString(out, Math.round(xoffset+centralboxsize*point[0]- rect.getWidth()), Math.round(yoffset+centralboxsize*point[1] + rect.getHeight()/2));
			}

			while (itcraters.hasNext()) {
				double[] point = itcraters.next();
				SmallTools.drawThickOval(g2, xoffset+centralboxsize*point[0], 
						yoffset+centralboxsize*point[1], 1,1,new Color(30, 30, 30));
			}

			
			if (this.animate){
				if (t % m.drawsteps==1)
					try {
						Thread.sleep(m.delay);
					} catch (InterruptedException e) {	}
				
			}
			t++;
		}
		g2.setColor(new Color(30, 30, 30));
		g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
		animate = false;
	}

	private static String numberoutput(double d){
		return ""+(new Double(Math.round(d*1000)*1.0/100)).toString();
	}
	
	private static String intoutput(double d){
		return ""+((int) d);
	}
	@Override
	public String getType() {
		return MainFrame.clusterExpTwoD;
	}

	private Object[] readInput() {
		int in1 = utils.SmallTools.giveInteger(frame.inputs[0].getText());
		double in2 = utils.SmallTools.giveDouble(frame.inputs[1].getText());
		int in3 = utils.SmallTools.giveInteger(frame.inputs[2].getText());
		int in4 = utils.SmallTools.giveInteger(frame.inputs[3].getText());
		
		boolean correctinput = true;
		String newLine = System.getProperty("line.separator");
		StringBuffer output = new StringBuffer();
		output.append("Input Error" + newLine);

		if (in1 == Integer.MAX_VALUE) {
			output.append("Number of particles is not a number: "
					+ frame.inputs[0].getText() + newLine);
			correctinput = false;
		} else {
			if ((in2 > Math.pow(10, 10)) || (in2 < 0)) {
				correctinput = false;
				output.append("Input for particles does not work ." + newLine);
			}
		}
		if (in2 == Double.MAX_VALUE) {
			output.append("The input for singleton is not a number: "
					+ frame.inputs[1].getText() + newLine);
			correctinput = false;
		} else {
			if ((in2 > 1000000000) || (in2 < 0)) {
				correctinput = false;
				output.append("Input for the weight does not make sense." + newLine);
			}
		}

		if (in3 == Integer.MAX_VALUE) {
			output.append("Number of frames is not a number: "
					+ frame.inputs[2].getText() + newLine);
			correctinput = false;
		} else {
			if ((in3 > Math.pow(10, 10)) || (in3 < 1)) {
				correctinput = false;
				output.append("Input of frames does nore make sense." + newLine);
			}
		}
		if (in4 == Integer.MAX_VALUE) {
			output.append("Input for the delay is not a number: "
					+ frame.inputs[3].getText() + newLine);
			correctinput = false;
		} else {
			if ((in4 > Math.pow(10, 10)) || (in4 < 0)) {
				correctinput = false;
				output.append("Input for delay does not work ." + newLine);
			}
		}
		
		if (correctinput) {
			Object[] values = new Object[4];
			values[0] = new Double(in1);
			values[1] = new Double(in2);
			values[2] = new Double(in3);
			values[3] = new Double(in4);
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
			int in1 = (int) ((Double) input[0]).doubleValue();
			double in2 = ((Double) input[1]).doubleValue();
			int in3 = (int) ((Double) input[2]).doubleValue();
			int in4 = (int)((Double) input[3]).doubleValue();
			frame.remove(frame.center);
				this.model = null;
				DrawBoxOfStars newSim = new DrawBoxOfStars(frame,
						new BoxOfStars(in1, in3,in2, in4), 1000, false);
				frame.center = newSim;
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
		Object[] input = readInput();
		if (input.length != 1) {
			try {
				int in1 = (int) ((Double) input[0]).doubleValue();
				double in2 = ((Double) input[1]).doubleValue();
				int in3 = (int) ((Double) input[2]).doubleValue();
				int in4 = (int)((Double) input[3]).doubleValue();
				
				DrawBoxOfStars newSim = new DrawBoxOfStars(frame,
						new BoxOfStars(in1, in3,in2, in4), 1000,
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
		return "A model Pieter Trapman came up with"
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