package drawer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import utils.SAWgens;

import main.MainFrame;
import models.PercolationConfiguration;
import models.RandomWalks;

/**
 * With this class we draw memory walks in 2-D in the central frame.
 * 
 * @author Robert Fitzner
 * 
 *         TODO Deactivated, is this part of the program of interest to anyone?
 * 
 */
@SuppressWarnings("serial")
public class DrawRandomWalksAdvanced extends Outputapplet {
	boolean animate;

	public DrawRandomWalksAdvanced(MainFrame fr) {
		this(fr, new RandomWalks(3, 100, 0), true);
	}

	/**
	 * 
	 * Draw a number of walks (w) with same length(s) and memory(m)
	 */
	public DrawRandomWalksAdvanced(MainFrame fr, RandomWalks m,
			boolean initparam) {
		frame = fr;
		model = m;
		animate = true;
		if (initparam)
			this.initaliseParameterPanel();
	}

	public void paint(Graphics g) {
		/*
		 * With the first lines I prepare for 2 things. 1. I count the number of
		 * times this method has be called, since I want to draw his walk
		 * stepwise only in the first run. I do this not by a boolean since it
		 * is also useful for debugging.
		 * 
		 * 2. This method is often call twice, because the main window sends an
		 * refresh and also the Outputapplet. To stop this I remember the last
		 * time I draw it and if was less then 200ms ago I do not redraw.
		 */
		/*
		 * drawtimes++; boolean redraw = true;
		 * 
		 * if (drawnlasttime != 0) { if ((System.currentTimeMillis() -
		 * drawnlasttime) < 100) redraw = false; } // There begin the actual
		 * drawing. if (redraw) { // initiate the drawing Graphics2D g2 =
		 * (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		 * RenderingHints.VALUE_ANTIALIAS_ON); //we ask for the size of the
		 * frame and then prepare the background Dimension d = getSize();
		 * g.setColor(bg); g2.fillRect(0, 0, d.width, d.height); // Then I add
		 * my name to the lower right corner g2.setColor( new Color(200, 200,
		 * 200)); Font myFont = new Font("Arial", Font.ITALIC | Font.PLAIN, 22);
		 * g2.setFont(myFont); g2.drawString(" Robert Fitzner", d.width-160,
		 * d.height-10); g.setColor(fg); // get the maximal displacement that we
		 * need to scale the drawing. double maxdisplacement = 0; for (int w =
		 * 0; w < sequenceOfWalks.length; w++) { // scaling values double tmp =
		 * SAWgens.getMaximalMovement(sequenceOfWalks[w]) * 2; if (tmp >
		 * maxdisplacement) maxdisplacement = tmp; }
		 * 
		 * // so we draw on a grid The central position/origin will be at int[]
		 * shifting = { 0, d.width/2, d.height/2 }; // the horizontal and
		 * vertical distance between the nodes are double gridcellVertsize =
		 * (d.width-20) / maxdisplacement; double gridCellHightsize =
		 * (d.height-20) / maxdisplacement; // scale to the central cycle and
		 * place it double diameterhor = 2*Math.sqrt(steps[0] ) *
		 * gridcellVertsize; double diametervert = 2*Math.sqrt(steps[0] ) *
		 * gridCellHightsize; // Draw a circle around the center with a radius
		 * of square root(of number of steps of first walk) for(int
		 * i=0;i<4;i++){ g2.draw(new Ellipse2D.Double(shifting[1] - diameterhor
		 * / 2-i/2, shifting[2] - diametervert / 2-i/2, diameterhor+i,
		 * diametervert+i)); } //draw Grid g2.setColor(Color.black);
		 * if(maxdisplacement<31){ for(int i=0;i<2*maxdisplacement;i++){
		 * g2.draw(new Line2D.Double(10, 10+i*gridCellHightsize, d.width-10,
		 * 10+i*gridCellHightsize)); g2.draw(new
		 * Line2D.Double(10+i*gridcellVertsize, 10, 10+i*gridcellVertsize,
		 * 10+d.height)); } } for (int w = 0; w < sequenceOfWalks.length; w++) {
		 * int[] position = { 0, 0, 0 }; for (int i = 0; i <
		 * sequenceOfWalks[w].length; i++) { // we translate this the walk as
		 * the sequence of direction into points int[] movement = { 0, 0, 0 };
		 * switch (sequenceOfWalks[w][i]) { case 1: movement[1] = -1; break;
		 * case -1: movement[1] = 1; break; case 2: movement[2] = 1; break; case
		 * -2: movement[2] = -1; break; case 0:// should not happen movement[0]
		 * = 1; break; default: }
		 * 
		 * // delay only at the first time. if (drawtimes == 1) { try {
		 * Thread.sleep(drawinmsperwalks / steps[w]); } catch
		 * (InterruptedException e) { } } // I use a displacement of +w so that
		 * walks are not draw atop of each other drawThickLine(g2, shifting[1] +
		 * position[1] * gridcellVertsize +w, shifting[2] + position[2] *
		 * gridCellHightsize + w, shifting[1] + (position[1] + movement[1]) *
		 * gridcellVertsize + w, shifting[2] + (position[2] + movement[2]) *
		 * gridCellHightsize+ w, 3, colors[w % colors.length ]); position[2] +=
		 * movement[2]; position[1] += movement[1]; } } }
		 * 
		 * drawnlasttime = System.currentTimeMillis();
		 */
	}

	@Override
	public String getType() {
		return MainFrame.walkDrawing;
	}

	@Override
	public void generateNew() {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateNewForFile(File file, String type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveCurrentlyShownToFile(File file, String type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initaliseParameterPanel() {

		// Reset upper part, where parameters are entered and the bottoms are
		frame.remove(frame.parameterentry);
		frame.inputs = new JTextField[8];
		frame.inputs[0] = new JTextField("" + 3);
		frame.inputs[1] = new JTextField("" + 1200);
		for (int i = 0; i < 3; i++) {
			frame.inputs[i + 2] = new JTextField("" + 100, 6);
			frame.inputs[i + 5] = new JTextField("" + 0, 6);

		}
		for (int i = 0; i < frame.inputs.length; i++) {
			frame.inputs[i].setFont(new Font("Arial", Font.ROMAN_BASELINE
					| Font.PLAIN, 22));
		}

		frame.parameterentry = new JPanel(new GridLayout(2, 2));
		JPanel NWpanel = new JPanel();
		JPanel NEpanel = new JPanel();
		JPanel SWpanel = new JPanel();
		JPanel SEpanel = new JPanel();
		frame.parameterentry.add(NWpanel);
		frame.parameterentry.add(NEpanel);
		JScrollPane SWbar = new JScrollPane(SWpanel);
		frame.parameterentry.add(SWbar);
		JScrollPane SEbar = new JScrollPane(SEpanel);
		frame.parameterentry.add(SEbar);

		NWpanel.add(new JLabel("Nr of walks "));
		frame.inputs[0].setActionCommand("resetnumberwalks");
		// frame.inputs[0].addActionListener(new SmallListener());
		NWpanel.add(frame.inputs[0]);

		frame.helpbutton = new JButton(MainFrame.askforhelp);
		frame.helpbutton.addActionListener(frame.listener);
		frame.helpbutton.setActionCommand(MainFrame.askforhelp);
		NWpanel.add(frame.helpbutton);

		JPanel timingpanel = new JPanel(new FlowLayout());
		timingpanel.add(new JLabel("ms per walk"));
		timingpanel.add(frame.inputs[1]);
		NEpanel.add(timingpanel);

		JPanel buttonpanel = new JPanel(new FlowLayout());
		JButton basicbutton = new JButton("Basic");
		basicbutton.setActionCommand(MainFrame.walkDrawing);
		basicbutton.addActionListener(frame.listener);
		buttonpanel.add(basicbutton);
		JButton generatebutton = new JButton("Generate new");
		generatebutton.setMnemonic(KeyEvent.VK_G);
		generatebutton.setActionCommand(MainFrame.gostring);
		generatebutton.addActionListener(frame.listener);
		buttonpanel.add(generatebutton);
		NEpanel.add(buttonpanel);

		SWpanel.add(new JLabel("Number of steps"));
		SWpanel.setLayout(new FlowLayout());
		SEpanel.add(new JLabel("Memory"));
		SEpanel.setLayout(new FlowLayout());

		for (int i = 0; i < 3; i++) {
			SWpanel.add(frame.inputs[i + 2]);
			SWpanel.add(frame.inputs[i + 5]);
		}

		frame.add(frame.parameterentry, BorderLayout.NORTH);

	}

	@Override
	public void reDraw() {
		// TODO Auto-generated method stub

	}

	@Override
	public void continueSim() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getHelpText() {
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
				+ " You can choose how many walks you want to see (maximal 30 )"
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
}
