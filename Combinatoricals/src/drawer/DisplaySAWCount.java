package drawer;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import main.MainFrame;
import models.SAWCounting;

/**
 * In this class I count all self-avoiding on the Zd grid for a given d up to a
 * given length n.
 * 
 * For a small step number I compute the number live and also return the
 * runtime. If the number of steps is to big I return the number as fare as
 * there are known an an estimation of the time it need to compute it on the
 * give computer.
 * 
 * Thereby I only count walk pattern and not actually walks. A pattern always
 * start in direction 1 and when we move in the next step in another direction
 * when it will be in direction 2, and then in direction 3 and so on DONE
 * 
 * @author Robert Fitzner
 */
@SuppressWarnings("serial")
public class DisplaySAWCount extends Outputapplet {

	/**
	 * list of result that will be displayed
	 */

	// parameter of the last computation
	JTextArea writingplate;

	public DisplaySAWCount(MainFrame fr, int d, int n) {
		this(fr, new SAWCounting(d, n), false);
	}

	public DisplaySAWCount(MainFrame fr) {
		this(fr, new SAWCounting(3, 8), true);
	}

	/**
	 * 
	 */
	public DisplaySAWCount(MainFrame fr, SAWCounting m, boolean initparam) {
		model = m;
		writingplate = new JTextArea(converteResultToString());
		writingplate.setEditable(false);
		Font myFont = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
		writingplate.setFont(myFont);

		this.getContentPane().setLayout(new BorderLayout(5, 5));
		JScrollPane bar = new JScrollPane(writingplate);
		add(bar); // first we do the path and produce that we want to put.
		this.frame = fr; // the we assign the frame in which this will be
							// displayed
		if (initparam)
			this.initaliseParameterPanel(); // and then change the frame so that
		// this can be displayed
	}

	// ======================Computational Methodes==========
	/**
	 * Creates the TextArea and the text that we be shown
	 * 
	 * @param dim
	 * @param n
	 */

	private String converteResultToString() {
		String newLine = System.getProperty("line.separator");
		String result = "";
		SAWCounting m = (SAWCounting) model;
		if (m.toBigToCompute) {
			if (m.dimension < 13)
				result = "For dimension " + m.dimension
						+ " my database knows the following results:" + newLine;
			else
				result = " The dimension is to high and I have no information in my database."
						+ newLine;
		} else {
			result = "For dimension " + m.dimension
					+ " I just calculated the following result:" + newLine;
		}

		if (!m.toBigToCompute || m.dimension < 13) {
			for (int i = 0; i < m.cnList.length - 1; i++) {
				result += " We have " + m.cnList[i] + "        " + (i + 1)
						+ "-step walks" + newLine;
			}
		}
		if (m.toBigToCompute) {
			String timetoreturn = timeToString(m.timeToCalculate);
			if (!timetoreturn.equals(m.tolong)) {
				result += newLine
						+ " On this computer the calculation would took about "
						+ timeToString(m.timeToCalculate) + "." + newLine
						+ newLine;
			} else
				result += newLine + m.tolong + newLine;
		} else {
			result += newLine + " The calculation took "
					+ Math.round(m.timeToCalculate) + " ms." + newLine
					+ newLine;
		}

		if (m.toBigToCompute) {
			result += newLine
					+ "Precomputed results are taken from http://www.math.ubc.ca/~slade/"
					+ newLine
					+ "They are calculated using an improved algorithm developed by "
					+ newLine
					+ "Nathan Clisby, Richard Liang, and Gordon Slade.";
		} else {
			result += "by Robert Fitzner";
		}

		return result;
	}

	public String timeToString(double timeToCalculate) {
		double rawtime = timeToCalculate;
		if (rawtime < 1000) {
			return Math.round(rawtime) + " ms";
		}
		rawtime = rawtime / 1000;
		if (rawtime < 60) {
			return rawtime / 1000 + " seconds";
		}
		rawtime = rawtime / 60;
		if (rawtime < 60) {
			return Math.round(rawtime * 100) * 1.0 / 100 + " minutes";
		}
		rawtime = rawtime / 60;
		if (rawtime < 24) {
			return Math.round(rawtime * 100) * 1.0 / 100 + " hours";
		}
		rawtime = rawtime / 24;
		if (rawtime < 365) {
			return Math.round(rawtime * 100) * 1.0 / 100 + " days";
		}
		rawtime = rawtime / 365.25;
		if (rawtime < 1000000) {
			return Math.round(rawtime * 100) * 1.0 / 100 + " years";
		}
		rawtime = rawtime / 1000000;
		if (rawtime < 13700) {
			return Math.round(rawtime * 100) * 1.0 / 100 + " million years";
		}
		rawtime = rawtime / 13700;
		if (rawtime < Long.MAX_VALUE)
			return ((long) rawtime) + " times the age of the universe";
		return ((SAWCounting) model).tolong;
	}

	@Override
	public void initaliseParameterPanel() {
		frame.setTitle("Self-avoiding walk counter");
		// Reset upper part, where parameters are entered and the bottoms are
		frame.remove(frame.parameterentry);
		frame.parameterentry = new JPanel(new GridLayout(2, 3));
		frame.inputs = new JTextField[2];
		frame.inputs[0] = new JTextField("" + ((SAWCounting) model).dimension);
		frame.inputs[1] = new JTextField("" + ((SAWCounting) model).steps);
		for (int i = 0; i < frame.inputs.length; i++) {
			frame.inputs[i].setFont(new Font("Arial", Font.ROMAN_BASELINE
					| Font.PLAIN, 22));
		}

		JButton generatebutton = new JButton("Count walks");
		generatebutton.setMnemonic(KeyEvent.VK_G);
		generatebutton.setActionCommand(MainFrame.gostring);
		generatebutton.addActionListener(frame.listener);

		frame.helpbutton = new JButton(MainFrame.askforhelp);
		frame.parameterentry.add(frame.helpbutton);
		frame.helpbutton.addActionListener(frame.listener);
		frame.helpbutton.setActionCommand(MainFrame.askforhelp);

		frame.parameterentry.add(new JLabel(" Dimension"));
		frame.parameterentry.add(new JLabel(" Up to number of steps :"));
		frame.parameterentry.add(frame.helpbutton);
		frame.parameterentry.add(frame.inputs[0]);
		frame.parameterentry.add(frame.inputs[1]);
		frame.parameterentry.add(generatebutton);

		frame.add(frame.parameterentry, BorderLayout.NORTH);
	}

	@Override
	public void generateNew() {
		boolean correctinput = true;
		String newLine = System.getProperty("line.separator");
		StringBuffer output = new StringBuffer();
		output.append("Input Error" + newLine);

		int in1 = utils.SmallTools.giveInteger(frame.inputs[0].getText());
		int in2 = utils.SmallTools.giveInteger(frame.inputs[1].getText());

		// read all inputs and check them
		if (in1 == Integer.MAX_VALUE) {
			output.append("There must be a typing error in the dimension field, it is not a number: "
					+ frame.inputs[0].getText() + newLine);
			correctinput = false;
		} else {
			if ((in1 > 20) || (in1 < 0)) {
				output.append(" You have given an invalide dimension, please give a dimension smaller than 20."
						+ newLine);
				correctinput = false;
			}
		}
		if (in2 == Integer.MAX_VALUE) {
			output.append("The entry in the field -Up to number of steps- is not an integer: "
					+ frame.inputs[1].getText() + newLine);
			correctinput = false;
		} else {
			if ((in2 < 0)) {
				correctinput = false;
				output.append(" The number of steps should be positive."
						+ newLine);
			}
		}
		if (correctinput) {
			frame.remove(frame.center);
			frame.center = new DisplaySAWCount(frame, in1, in2);
			frame.center.frame = this.frame;
			frame.add(frame.center, BorderLayout.CENTER);
			this.setVisible(false);
		} else {
			JOptionPane.showMessageDialog(frame, output.toString());
		}
		frame.validate();
	}

	@Override
	public String getType() {
		return MainFrame.sawCounting;
	}

	@Override
	public void reDraw() {
		paint(getGraphics());
	}

	public String getHelpText() {
		String newLine = System.getProperty("line.separator");
		String content = "This part of the applet counts the number of n-step self-avoiding walks"
				+ newLine
				+ "for given dimensions and number of steps."
				+ newLine
				+ "To be exact it counts the number of all possible nearest-neighbour"
				+ newLine
				+ "strictly self-avoiding walks on the d dimensional lattice."
				+ newLine
				+ newLine
				+ "This task can be extremely runtime consuming. For this reason we only compute the"
				+ newLine
				+ "number of n-steps walks that we can actually calculated live."
				+ newLine
				+ "If the number of steps is to big the applet estimates the time that"
				+ newLine
				+ "it would take to compute the result on your computer"
				+ newLine
				+ "and returns a pre-computed result."
				+ newLine
				+ newLine
				+ "To return to the program press the -Back- Button, that has replaced, "
				+ newLine
				+ "the help button or select a new part of the program";
		return content;
	}

	@Override
	public void generateNewForFile(File file, String type) {
		JOptionPane
				.showMessageDialog(
						frame,
						"Feature not available for this model. See http://www.math.ubc.ca/~slade/ for a complete list.");

	}

	@Override
	public void saveCurrentlyShownToFile(File file, String type) {
		JOptionPane
				.showMessageDialog(
						frame,
						"Feature not available for this model. See http://www.math.ubc.ca/~slade/ for a complete list.");
	}

	@Override
	public void continueSim() {
		JOptionPane.showMessageDialog(frame,
				"Feature not available for this model.");

	}

}