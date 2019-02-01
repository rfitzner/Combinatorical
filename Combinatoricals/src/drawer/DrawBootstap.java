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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.MainFrame;
import models.Bootstap_Iso;
import models.Bootstap_Modified;
import models.Bootstap_St;
import models.Bootstap_abstract;

@SuppressWarnings("serial")
public class DrawBootstap extends Outputapplet {
	boolean animate;
	BufferedImage bi;
	
	JCheckBox iterateSites;
	JComboBox setting;
	
	public DrawBootstap(MainFrame fr) {
		this(fr, new Bootstap_St(500, 500, 0.05), true);
	}

	public DrawBootstap(MainFrame fr, Bootstap_abstract bootstap, boolean initparam) {
		frame = fr;
		model = bootstap;
		animate = true;
		if (initparam)
			this.initaliseParameterPanel();
	}

	public void paint(Graphics g) {
		if ((bi != null) && !animate) {
			// if we only have a small change in comparison to the
			// last image we just re-scale the image
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
				BufferedImage.TYPE_3BYTE_BGR);

		paint(bi.getGraphics(), this.getSize());
		g.drawImage(bi, 0, 0, null);
	}

	public void paint(Graphics g, Dimension d) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setColor(bg);
		g2.fillRect(0, 0, d.width, d.height);
		double whiteBorder = 2;
		int w=((Bootstap_abstract) model).width;
		int h=((Bootstap_abstract) model).height;
		double xFactor = (d.width- 2*whiteBorder)/w;
		double yFactor = (d.height- 2*whiteBorder)/h;
		if ( ((Bootstap_abstract) model).toCheck.size()>0){
			g2.setColor(new Color(120, 120, 120));
			for (int i=0;i<h;i++){
				for (int j=0;j<h;j++){
					if ( ((Bootstap_abstract) model).isInfected(i,j))
						g2.fill(new Rectangle2D.Double(whiteBorder+xFactor*i, whiteBorder+yFactor*j,
						xFactor,yFactor));
				}
			}
		} else {
			for (int i=0;i<h;i++){
				for (int j=0;j<h;j++){
					if ( ((Bootstap_abstract) model).isInfected(i,j)){
						double fac =  ((Bootstap_abstract) model).age[i][j]*1.0/(((Bootstap_abstract) model).agecount);
						g2.setColor(new Color (  
								(float)(0.0+ fac),
								(float)(0.0+ fac),
								(float)(1.0-fac) ));
						g2.fill(new Rectangle2D.Double(whiteBorder+xFactor*i, whiteBorder+yFactor*j,
							xFactor,yFactor));
					}
				}
			}	
		}
		g2.setColor(new Color(70, 70, 70));
		g2.setFont(new Font("Arial", Font.ITALIC | Font.PLAIN, 22));
		g2.drawString(" Robert Fitzner", d.width - 160, d.height - 10);
	}

	private Object[] readInput(int max1) {
		int in1 = utils.SmallTools.giveInteger(frame.inputs[0].getText());
		int in2 = utils.SmallTools.giveInteger(frame.inputs[1].getText());
		double in3 = utils.SmallTools.giveDouble(frame.inputs[2].getText());

		boolean correctinput = true;
		String newLine = System.getProperty("line.separator");
		StringBuffer output = new StringBuffer();
		output.append("Input Error" + newLine);

		if (in1 == Integer.MAX_VALUE) {
			output.append("The input for the width not an integer: "
					+ frame.inputs[0].getText() + newLine);
			correctinput = false;
		} else {
			if ((in1 > max1) || (in1 < 0)) {
				correctinput = false;
				output.append("Width does not make sense." + newLine);
			}
		}

		if (in2 == Integer.MAX_VALUE) {
			output.append("The input for the height is not an integer: "
					+ frame.inputs[1].getText() + newLine);
			correctinput = false;
		} else {
			if ((in2 > max1) || (in2 < 0)) {
				correctinput = false;
				output.append("Height does not make sense." + newLine);
			}
		}
		
		if (in3 == Double.MAX_VALUE) {
			output.append("The input for the probability is not a number: "
					+ frame.inputs[2].getText() + newLine);
			correctinput = false;
		} else {
			if ((in3 > 1) || (in3 < 0)) {
				correctinput = false;
				output.append("Input for probability ist not a probability." + newLine);
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
			int in2 = ((Integer) input[1]).intValue();
			double prob = ((Double) input[2]).doubleValue();
			Bootstap_abstract m;
			if (setting.getSelectedIndex()==0) m=new Bootstap_St(in1, in2, prob);
			else if (setting.getSelectedIndex()==1) m=new Bootstap_Iso(in1, in2, prob);
			else m=new Bootstap_Modified(in1, in2, prob);
			
			DrawBootstap newSim = new DrawBootstap(frame, m, false);
			
			newSim.iterateSites=this.iterateSites;
			newSim.setting=this.setting;
			
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
			//TODO 
		}
	}

	@Override
	public void initaliseParameterPanel() {
		frame.setTitle("Bootstrap percolation");

		frame.inputs = new JTextField[4];
		frame.inputs[0] = new JTextField("" + ((Bootstap_abstract) model).width);
		frame.inputs[1] = new JTextField(""	+ ((Bootstap_abstract) model).height);
		frame.inputs[2] = new JTextField("" + ((Bootstap_abstract) model).initialprob);
		frame.inputs[3] = new JTextField("5");

		iterateSites=new JCheckBox("Use sideNr");
		iterateSites.setSelected(false);
		String[] tmp={"Standard", "Anisotropic", "Modified"};
		setting= new JComboBox(tmp);
		setting.setSelectedIndex(0);
		for (int i = 0; i < frame.inputs.length; i++) {
			frame.inputs[i].setFont(new Font("Arial", Font.ROMAN_BASELINE
					| Font.PLAIN, 22));
		}
		// Reset upper part, where parameters are entered and the bottoms are
		frame.remove(frame.parameterentry);
		frame.parameterentry = new JPanel(new GridLayout(2, 7));
		frame.parameterentry.add(new JLabel("width"));
		frame.parameterentry.add(new JLabel("height"));
		frame.parameterentry.add(new JLabel("prob"));
		frame.parameterentry.add(new JLabel("# iteration"));
		frame.parameterentry.add(setting);
		frame.parameterentry.add(iterateSites);
		
		JButton continuebutton = new JButton(MainFrame.continueString);
		continuebutton.addActionListener(frame.listener);
		continuebutton.setActionCommand(MainFrame.continueString);
		frame.parameterentry.add(continuebutton);

		

		frame.parameterentry.add(frame.inputs[0]);
		frame.parameterentry.add(frame.inputs[1]);
		frame.parameterentry.add(frame.inputs[2]);
		frame.parameterentry.add(frame.inputs[3]);
		frame.parameterentry.add(new JLabel(""));
		frame.helpbutton = new JButton(MainFrame.askforhelp);
		frame.helpbutton.addActionListener(frame.listener);
		frame.helpbutton.setActionCommand(MainFrame.askforhelp);
		frame.parameterentry.add(frame.helpbutton);

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
		return MainFrame.bootstrap;
	}

	@Override
	public String getHelpText() {
		String newLine = System.getProperty("line.separator");
		return "The model:"
				+ newLine
				+ "Where between each frame the."
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
		int in4 = utils.SmallTools.giveInteger(frame.inputs[3].getText());
		if (in4 == Integer.MAX_VALUE || in4 < 0) {
			JOptionPane.showMessageDialog(frame,
					"The input for number of steps is not an integer.");
			return;
		} else {
			Bootstap_abstract m = ((Bootstap_abstract) model);
			int j = 0;
			for (int i = 0; (i < in4) && (m.toCheck.size()>0) ; i++) {
				if (!iterateSites.isSelected())
					m.checkGeneration();
				else
					m.checkNext();
			}
			this.reDraw();
		}
	}

}
