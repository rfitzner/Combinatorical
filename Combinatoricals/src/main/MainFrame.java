package main;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import models.AbstractMathModel;

import drawer.Outputapplet;
import drawer.DrawPerc;

import utils.DateiIO;
import utils.MyDataFilter;
import utils.UserMessages;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * This is the central part of the program.
 * 
 * @author Robert Fitzner
 * 
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	// Name of the different parts
	public static final String sawCounting = "SAW counter";
	public static final String walkDrawing = "Drawing of walks";
	public static final String addwancedwalkDrawing = "Advanced Draw SAW";
	public static final String walkInLatticeGasTwoD = "Walk on lattice Gas";
	public static final String branchingWalkDrawing = "Branching walks";

	public static final String perc = "Percolation";
	public static final String percdev = "Development of a percolation conf.";
	public static final String invasionperc = "Invasion Percolation";
	public static final String bootstrapBalls = "Bootstrap perc in cont. space";
	public static final String bootstrap = "Bootstrap percolation";
	public static final String frozenperc = "Frozen Percolation";
	public static final String walkOnPerc = "Walk On 2D Percolation cluster";
	public static final String clusterExpTwoD = "Exploration in dimension 2";
	public static final String clusterExpArbD = "Exploration of a highD cluster";

	public static final String minesweeper = "Minesweeper";
	public static final String boxOfStars = "Stars in a box";
	public static final String inhomgeniousER = "Inhom. Dist Graph";

	public static final String sandPileslattice = "On the lattice";
	public static final String sandPilesER = "ER-Component";
	public static final String sandPilesRegularTree = "Regular tree";
	

	public JTextField[] inputs;

	// public static final Class[]
	// models={SAWCounting.class,DrawWalks.class,DrawPerc.class,DrawPercDev.class,Invasionpercolation.class,WalkOnPerc.class,ClusterExploration.class,ClusterExplHighD.class,LatticeGraphDrawer.class,RegularTreeDrawer.class,ErdosComponentDrawer.class};
	// name for action from the menubar
	static String quit = "Quit";
	static String open = "Open a saved sim";
	static String save = "Save this sim";

	// name for the different kinds of action
	public static final String gostring = "Generate";
	public static final String continueString = "Continue";
	public static final String redraw = "Redraw";
	public static final String filedraw = "Draw new in File";
	public static final String filecurrentdraw = "Draw current in File";
	public static final String advanced = "Adv.opt.";
	public static final String askforhelp = "Help?";
	static String askforGeneralhelp = "Help on the applet";
	static String inputerrorMessage = "An input is not a number,";
	static String infoAuthor = "authorinfo";

	// Status saves the name of the part that is active right now,
	// sidestatus is used when the central display is busy showing
	// the help or other user information in main output frame
	// (center)

	// the three frames of the Applet + a second version for the
	// centerpart that is used when we want to show user information.
	JPanel menupanel;
	public JPanel parameterentry;
	public Outputapplet center, centerforHelp;
	public ActionListener listener;
	public JButton helpbutton;

	/**
	 * The only constructor In this we set the size and some properties about
	 * the general behavior of this frame and insert dummy values that will be
	 * replace when init is called.
	 */
	public MainFrame() {
		// set layout and size.
		this.getContentPane().setLayout(new BorderLayout());
		this.setSize(800, 600);
		// add the menupanel
		listener = new MainAction(this);

		this.setJMenuBar(new MenueLeiste(this));
		parameterentry = new JPanel();
		center = new UserMessages("init");

		add(center, BorderLayout.CENTER);
		add(parameterentry, BorderLayout.NORTH);
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	}

	/**
	 * Fills the empty frame with its first data.
	 * 
	 */
	public void init() {
		DrawPerc newSim = new DrawPerc(this);
		//DisplaySAWCount newSim = new DisplaySAWCount(this);
		center = newSim;
		add(center, BorderLayout.CENTER);
		setVisible(true);
	}

	/**
	 * Saves a set of walks/configurations to a file. In this way you can save
	 * and load especially pretty configurations.
	 */
	public void saveSimulaton() {
		JFileChooser fc = new JFileChooser();

		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setCurrentDirectory(new File("."));
		FileFilter filter = new MyDataFilter("rss");
		fc.setFileFilter(filter);
		int retVal = fc.showSaveDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File datei = fc.getSelectedFile();
			if (MyDataFilter.getExtension(datei) == null) {
				String nameWithEnd = datei.getAbsolutePath() + ".rss";
				datei = new File(nameWithEnd);
			}
			if (filter.accept(datei)) {
				DateiIO.writefileObj(datei, center.model);
			}
		}
	}

	/**
	 * Loads a saved set of walks/configurations from a file. In this way you
	 * can save and load especially pretty configurations.
	 */
	public void loadSimulaton() {
		JFileChooser fc = new JFileChooser();
		// -------------------------
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setCurrentDirectory(new File("."));

		FileFilter filter = new MyDataFilter("rss");
		fc.setFileFilter(filter);
		fc.setDialogType(JFileChooser.OPEN_DIALOG);

		int retVal = fc.showOpenDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File datei = fc.getSelectedFile();
			AbstractMathModel newContent = (AbstractMathModel) DateiIO
					.readFileObj(datei);
			Outputapplet newcenter = newContent.createDrawer(this);
			this.remove(center);
			center = newcenter;
			add(center, BorderLayout.CENTER);
			this.validate();
		}
	}

	void drawNewIntoFile() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setCurrentDirectory(new File("."));
		MyDataFilter filter = new MyDataFilter(true);
		fc.setFileFilter(filter);
		int retVal = fc.showSaveDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (filter.accept(file)) {
				if (MyDataFilter.getExtension(file).equalsIgnoreCase("bmp"))
					center.generateNewForFile(file, "BMP");
				else if (MyDataFilter.getExtension(file)
						.equalsIgnoreCase("jpg"))
					center.generateNewForFile(file, "JPEG");
				else if (MyDataFilter.getExtension(file)
						.equalsIgnoreCase("gif"))
					center.generateNewForFile(file, "GIF");
			} else {
				JOptionPane.showMessageDialog(this, "Invalide file extension.");
			}
		}
	}

	void drawCurrentIntoFile() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setCurrentDirectory(new File("."));
		MyDataFilter filter = new MyDataFilter(true);
		fc.setFileFilter(filter);
		int retVal = fc.showSaveDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (filter.accept(file)) {
				if (MyDataFilter.getExtension(file).equalsIgnoreCase("bmp"))
					center.saveCurrentlyShownToFile(file, "BMP");
				else if (MyDataFilter.getExtension(file)
						.equalsIgnoreCase("jpg"))
					center.saveCurrentlyShownToFile(file, "JPEG");
				else if (MyDataFilter.getExtension(file)
						.equalsIgnoreCase("gif"))
					center.saveCurrentlyShownToFile(file, "GIF");
			} else {
				JOptionPane.showMessageDialog(this, "Invalide file extension.");
			}
		}
	}

	public static void main(String args[]) {
		MainFrame m = new MainFrame();
		m.init();
	}

}
