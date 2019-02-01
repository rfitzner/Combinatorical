package utils;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import drawer.Outputapplet;

import main.MainFrame;

/**
 * Here is an interface to show messages to the user. Like the help-text or
 * error messages. I plan to show this messages in middle frame the central
 * part, for this reason this is a subclass of Outputapplet. So this creates a
 * simple textArea, add scollbars and then display a text message that is saved
 * in this code.
 * 
 * This rest of this code should be self-explaining.
 * 
 * @author Robert Fitzner
 * 
 */
@SuppressWarnings("serial")
public class UserMessages extends Outputapplet {
	Outputapplet mother;

	/**
	 * The text of the help-messages is save in the code, namely in private
	 * methods in this class. the parameter is give which text you want
	 * 
	 * @param topic
	 */
	public UserMessages(String content) {
		super();
		mother = null;
		this.getContentPane().setLayout(new BorderLayout(5, 5));
		JTextArea writingplate;
		writingplate = new JTextArea(content);
		writingplate.setEditable(false);
		Font myFont = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
		writingplate.setFont(myFont);
		JScrollPane bar = new JScrollPane(writingplate);
		add(bar);
	}

	public UserMessages(String content, Outputapplet other) {
		super();
		mother = other;
		this.getContentPane().setLayout(new BorderLayout(5, 5));

		JTextArea writingplate;

		writingplate = new JTextArea(content);
		writingplate.setEditable(false);
		Font myFont = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
		writingplate.setFont(myFont);
		JScrollPane bar = new JScrollPane(writingplate);
		add(bar);
	}

	private static String getsawdExplText() {
		String newLine = System.getProperty("line.separator");
		String content = "This part of the applet can draw different random walks"
				+ newLine
				+ " -- simple random walks, "
				+ newLine
				+ " -- memory walk with arbitrary memory "
				+ newLine
				+ " -- and self-avoiding walks."
				+ newLine
				+ newLine
				+ " The Model:"
				+ newLine
				+ " A walk starts at the origin (the center of the big circle) and after each step"
				+ newLine
				+ " the walk can go left/right or up/down to one of the neighbouring positions."
				+ newLine
				+ " If the memory is zero then the steps are independent of each other and the "
				+ newLine
				+ " program draws a simple random walks. When the memory M, given by the  "
				+ newLine
				+ " user, is bigger then 0m then the walk rembers its last M positions and will"
				+ newLine
				+ " not revisit these M-places in the next step. This is then a memory-M walk. "
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
				+ " They will be draw after each other. The standard setting is that the drawing"
				+ newLine
				+ " of a walk takes one second, if you want to change the drawing speed or you "
				+ newLine
				+ " want to draw walks with different memories in the same picture please go to "
				+ newLine
				+ " the advanced part."
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

	private static String getsawdAdvExplText() {
		String newLine = System.getProperty("line.separator");
		String content = "For the explanation of the random walk, steps and memory please look "
				+ newLine
				+ " at the help while being in the basic Draw SAW part."
				+ newLine
				+ newLine
				+ " In the advanced part you can choose how long it takes for a walk to be draw. "
				+ newLine
				+ " By taking long time we will be able to see each step of the walker. "
				+ newLine
				+ " If you choose time 0 the if you just want to the path."
				+ newLine
				+ "You can choose how many walks you want to see (maximal 30 )"
				+ newLine
				+ " When you change the number of walk you should always confirm the change"
				+ newLine
				+ " with pressing enter. Only then the number of field for the number of "
				+ newLine
				+ " steps and memories are adjusted."
				+ newLine
				+ " Each walk can have a different memory and a different number of steps. "
				+ newLine
				+ " When changing the number of walks the fields will be initialised"
				+ newLine
				+ " with the former first entry. "
				+ newLine
				+ newLine
				+ "To return to the program press the -Back- Button, that has replaced, "
				+ newLine
				+ "the help button or select a new part of the program";

		return content;
	}

	public static String getAuthorText() {
		String newLine = System.getProperty("line.separator");
		String content = "This applet was developed  by Robert Fitzner in 2010-2012."
				+ newLine
				+ newLine
				+ "During this time he was employed as PhD. student at the University of Technology Eindhoven.";
		return content;
	}

	@Override
	public String getType() {
		return main.MainFrame.askforhelp;
	}

	public static String askforGeneralhelp() {
		String newLine = System.getProperty("line.separator");
		String content = "This applet simulates different models for random spatial media."
				+ newLine
				+ newLine
				+ "The different model can be selected via the menu bar. When selecting a new model "
				+ newLine
				+ "a small example is generated. The upper part give the user the possibilty "
				+ newLine
				+ "to change the parameter of the model. One of the necessary inputs is the "
				+ newLine
				+ "(maximal) size of the system that the user want to simulate. Thereby the author made"
				+ newLine
				+ "restrictions to the maximal size, to garanty that each simulation can be"
				+ newLine
				+ "generated with a couple of seconds. The author sees this applet as a way "
				+ newLine
				+ "to produce picture and animations of these models in real time and not as a way to simulate them."
				+ newLine
				+ newLine
				+ "Generate simulation can be saved/loaded in the downloadable version. A currently shown image can "
				+ newLine
				+ "can be written into a file, that will be generated with a resolution big enough to see all details."
				+ newLine
				+ newLine
				+ "Moreover new simulation can be written directly into images. For this feature the author allows bigger."
				+ newLine
				+ "systems to be generated. Thereby it shoud be pointed out that the simulation could take several minutes"
				+ newLine
				+ "and that the program could also run out of memory."
				+ newLine
				+ newLine
				+ "The program is intended to be used within academia for the introduction of these model."
				+ newLine
				+ "The author encourages everyone to use it in lectures and presentations.";
		return content;
	}

	private static String getsandPileslattice() {
		String newLine = System.getProperty("line.separator");
		String content = "This applet simulates the abilean sandpile model on a finite grid."
				+ newLine
				+ newLine
				+ "The model:"
				+ newLine
				+ "At each point of the grid a number of grains can be allocated is the number "
				+ newLine
				+ "of grains is as big or bigger then the number of neighbors (4) the stack of"
				+ newLine
				+ "will topple, meaning that the one grain is given to each of the four neighbors."
				+ newLine
				+ "If a point at the boundary topples one(resp. two grains) will be given to a point"
				+ newLine
				+ "outside of the grid and this grain is then lost for the system."
				+ newLine
				+ "In this way the system will always stabilies at a state where at each site are"
				+ newLine
				+ "at most three grains. The user can then add a number of grains and "
				+ newLine
				+ "and observe how the system stabilises."
				+ newLine
				+ newLine
				+ "The parameters:"
				+ newLine
				+ "The user can give the gridsize (maximal 150x100) and an initial number of"
				+ newLine
				+ "grains. After generating the inital configuration the user can addititionally"
				+ newLine
				+ "add points (up to 1000000). Further the user can choose whether the grains"
				+ newLine
				+ "are added uniformly at random or just always at the middle point of the grid."
				+ newLine
				+ "Further it can be choosen whether in initial configuration is empty or filled "
				+ newLine
				+ "at random."
				+ newLine
				+ newLine
				+ "To return to the program press the -Back- Button, that has replaced, "
				+ newLine
				+ "the help button or select a new part of the program";

		return content;
	}

	@Override
	public void generateNew() {
		if (mother != null) {
			mother.generateNew();
		}
	}

	@Override
	public void showHelpForThisModel() {
		if (mother != null) {
			// System.out.println("want to go back from help"+
			// mother.toString());
			mother.frame.remove(this);
			mother.frame.center = mother;
			mother.frame.add(mother.frame.center, BorderLayout.CENTER);
			mother.frame.helpbutton.setText(MainFrame.askforhelp);
			mother.frame.center.paint(mother.frame.center.getGraphics());
		}
	}

	@Override
	public void reDraw() {
		// this is just a dummy
	}

	@Override
	public void initaliseParameterPanel() {
		// this is just a dummy

	}

	public String getHelpText() {
		return "This is a display for text.";
	}

	@Override
	public void generateNewForFile(File file, String type) {
		mother.generateNewForFile(file, type);

	}

	@Override
	public void continueSim() {
		if (mother != null) {
			// System.out.println("want to go back from help"+
			// mother.toString());
			mother.frame.remove(this);
			mother.frame.center = mother;
			mother.frame.add(mother.frame.center, BorderLayout.CENTER);
			mother.frame.helpbutton.setText(MainFrame.askforhelp);
			mother.frame.center.paint(mother.frame.center.getGraphics());
			mother.continueSim();
		}

	}

	@Override
	public void saveCurrentlyShownToFile(File file, String type) {
		mother.saveCurrentlyShownToFile(file, type);

	}
}
