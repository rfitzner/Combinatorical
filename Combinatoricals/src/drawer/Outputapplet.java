package drawer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;

import javax.swing.JApplet;

import utils.UserMessages;

import main.MainFrame;
import models.AbstractMathModel;

/**
 * Abstract Superclass for all frame that are displayed in the central part of
 * the applet It saved the default colors.
 * 
 * @author Robert Fitzner
 */
@SuppressWarnings("serial")
public abstract class Outputapplet extends JApplet {
	public final static Color bg = Color.white;
	public final static Color fg = Color.black;
	public final static Color red = Color.red;
	public final static Color white = Color.white;
	public MainFrame frame;
	public AbstractMathModel model;
	// number of times we draw this applet
	public int drawtimes;
	// time at which we draw the model the last time
	protected long drawnlasttime;
	//new Color(214, 0, 74)
	public Color[] colors = { Color.red , Color.blue, Color.green,
			Color.yellow, Color.cyan, Color.orange, Color.pink, Color.magenta,
			Color.black, Color.gray, Color.blue, Color.green, Color.yellow,
			Color.cyan, Color.orange, Color.pink, Color.magenta, Color.black,
			Color.gray, Color.blue, Color.green, Color.yellow, Color.cyan,
			Color.orange, Color.pink, Color.magenta, Color.black, Color.gray,
			new Color(220, 220, 220) };


	
	public void setDrawtimes(int in) {
		drawtimes = in;
	}

	public abstract void generateNew();

	public abstract void generateNewForFile(File file, String type);

	public abstract void saveCurrentlyShownToFile(File file, String type);

	public abstract void initaliseParameterPanel();

	public abstract void reDraw();

	public abstract void continueSim();

	public abstract String getType();

	public void showHelpForThisModel() {
		UserMessages help = new UserMessages(getHelpText(), this);
		frame.remove(this);
		frame.center = help;
		frame.add(frame.center, BorderLayout.CENTER);
		frame.helpbutton.setText("Back");
		frame.validate();

	}

	public abstract String getHelpText();

}
