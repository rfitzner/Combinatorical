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
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utils.SmallTools;
import main.MainFrame;
import models.ClusterExploration;
import models.ClusterExploration.NNNode;

/**
 * In this class we will let a random walk explore a percolation cluster as in
 * ClusterExploration , but this time in arbitrary, given dimension.
 * 
 * @author Robert Fitzner
 */
public class DrawClusterExploration extends Outputapplet {
	private static final long serialVersionUID = 1L;
	int delay;
	boolean animate;
	BufferedImage bi;

	public DrawClusterExploration(MainFrame fr) {
		this(fr, new ClusterExploration(0.4, 3, 2000), 0, true);
	}

	public DrawClusterExploration(MainFrame fr, ClusterExploration m, int del,
			boolean initpanel) {
		model = m;
		frame = fr;
		delay = del;
		animate = true;
		if (initpanel)
			initaliseParameterPanel();
	}

	@Override
	public void initaliseParameterPanel() {
		frame.remove(frame.parameterentry);
		frame.parameterentry = new JPanel(new GridLayout(2, 6));

		frame.parameterentry.add(new JLabel("Edge prob"));
		frame.parameterentry.add(new JLabel("Dimensions"));
		frame.parameterentry.add(new JLabel("Number of steps"));
		frame.parameterentry.add(new JLabel("Time to draw path(ms)"));

		JButton redrawbutton = new JButton(MainFrame.redraw);
		redrawbutton.setMnemonic(KeyEvent.VK_G);
		redrawbutton.setActionCommand(MainFrame.redraw);
		redrawbutton.addActionListener(frame.listener);
		frame.parameterentry.add(redrawbutton);

		frame.helpbutton = new JButton(MainFrame.askforhelp);
		frame.parameterentry.add(frame.helpbutton);
		frame.helpbutton.addActionListener(frame.listener);
		frame.helpbutton.setActionCommand(MainFrame.askforhelp);

		frame.inputs = new JTextField[4];
		frame.inputs[0] = new JTextField(""
				+ ((ClusterExploration) model).bprob);
		frame.inputs[1] = new JTextField("" + ((ClusterExploration) model).dim);
		frame.inputs[2] = new JTextField(""
				+ ((ClusterExploration) model).steps);
		frame.inputs[3] = new JTextField("" + delay);

		for (int i = 0; i < frame.inputs.length; i++) {
			frame.inputs[i].setFont(new Font("Arial", Font.ROMAN_BASELINE
					| Font.PLAIN, 22));
			frame.parameterentry.add(frame.inputs[i]);
		}
		frame.parameterentry.add(new JLabel());
		JButton generatebutton = new JButton("Generate new");
		generatebutton.setMnemonic(KeyEvent.VK_G);
		generatebutton.setActionCommand(MainFrame.gostring);
		generatebutton.addActionListener(frame.listener);
		frame.parameterentry.add(generatebutton);

		frame.add(frame.parameterentry, BorderLayout.NORTH);
	}

	public void paint(Graphics g) {
		if ((bi != null) && !animate) {
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
		if (animate) {
			paint(getGraphics(), this.getSize());
		} else {
			// otherwise we create new images.
			bi = new BufferedImage(getSize().width, getSize().height,
					BufferedImage.TYPE_BYTE_INDEXED);

			paint(bi.getGraphics(), this.getSize());
			g.drawImage(bi, 0, 0, null);
		}
	}

	/**
	 * Here we drawing of the expansion done
	 */
	public void paint(Graphics g, Dimension d) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// get the size of the frame to scale the displaying and initialize
		// the background
		ClusterExploration m = (ClusterExploration) model;
		g.setColor(Outputapplet.bg);
		g2.fillRect(0, 0, d.width, d.height);
		// add my name to the lower right corner
		g2.setColor(new Color(150, 150, 150));
		g2.setFont(new Font("Arial", Font.ITALIC | Font.PLAIN, 22));
		g2.drawString(" Robert Fitzner", d.width - 180, d.height - 10);
		g.setColor(Outputapplet.fg);
		g2.setFont(new Font("Arial", Font.ITALIC | Font.PLAIN, 18));
		g2.setColor(new Color(100, 100, 100));
		// give the error messages
		if (m.path.size() == 1) {
			g2.setColor(new Color(0, 0, 0));
			g2.drawString(
					" The inital percolation cluster is just an isolated vertex.",
					d.width / 8, d.height / 3);
		} else if (m.numberOfRuns == 100) {
			g2.setColor(new Color(0, 0, 0));
			g2.drawString(
					" Failed to find a sufficiently large connected cluster in the first 100 runs.",
					d.width / 8, d.height / 3);
		} else {// or finally draw the cluster.
			// set parameters to draw it
			double gridsizeX = (d.width - 20) * 1.0
					/ (m.maxDis[0] - m.minDis[0]);
			double gridsizeY = (d.height - 140) * 1.0
					/ (m.maxDis[1] - m.minDis[1]);
			double gridmiddleX = gridsizeX * (-m.minDis[0]);
			double gridmiddleY = gridsizeY * (-m.minDis[1]);
			// Draw the axis for the graphic there the norms is shown
			g2.setColor(new Color(20, 20, 20));
			g2.fillRect(20, d.height - 140, 2, 110);
			g2.fillRect(20, d.height - 30, (d.width - 40), 2);
			for (int j = 1; j < 11; j++) {
				g2.fillRect(20, (int) (d.height - 30 - 11 * j), (d.width - 40),
						1);
			}
			// and now draw the graph
			Iterator<NNNode> it = m.path.iterator();
			NNNode former = it.next();
			int i = 1;
			// the we draw the first two coordinate of the walk
			while (it.hasNext()) {
				NNNode curr = it.next();
				g2.setColor(red);
				// draw step
				g2.draw(new Line2D.Double(former.pos[0] * gridsizeX + 10
						+ gridmiddleX, former.pos[1] * gridsizeY + 10
						+ gridmiddleY, curr.pos[0] * gridsizeX + 10
						+ gridmiddleX, curr.pos[1] * gridsizeY + 10
						+ gridmiddleY));
				// and mark the norm of this new position in the diagram
				utils.SmallTools.drawThickOval(g2, 20 + (d.width - 30) * i
						* 1.0 / m.path.size(),
						d.height - 30 - 120 * curr.euclideanNorm()
								/ m.maxEuclidean, 3, 3, new Color(0, 100, 255));
				i++;
				former = curr;
				try {
					if (animate)
						Thread.sleep(delay / m.path.size());
				} catch (InterruptedException ed) {
				}
			}

			g2.drawString("Max Norm: " + Math.round(m.maxEuclidean * 100) * 1.0
					/ 100 + " explored points: " + m.addedEdge
					+ " needed times to simulate: " + (m.numberOfRuns) + ".",
					20, d.height - 10);
			g2.setColor(new Color(100, 100, 100));
			g2.setFont(new Font("Arial", Font.ITALIC | Font.PLAIN, 22));
			g2.drawString(" Robert Fitzner", d.width - 180, d.height - 10);

		}
		animate = false;
	}

	@Override
	public String getType() {
		return MainFrame.clusterExpArbD;
	}

	private Object[] readInput(int max2, int max3, int max4) {
		double in1 = SmallTools.giveDouble(frame.inputs[0].getText());
		int in2 = SmallTools.giveInteger(frame.inputs[1].getText());
		int in3 = SmallTools.giveInteger(frame.inputs[2].getText());
		int in4 = SmallTools.giveInteger(frame.inputs[3].getText());
		boolean inputinrange = true;
		String errorstring = "";
		if ((in1 > 1) || (in1 < 0)) {
			errorstring += "Side Probability is not a propability.";
			inputinrange = false;
		}
		if ((in2 > max2) || (in2 < 0)) {
			errorstring += "We do not want to take so high dimensions.";
			inputinrange = false;
		}
		if ((in3 > max3) || (in3 < 0)) {
			inputinrange = false;
			errorstring = "Too many steps for of the walker. \n";
		}

		if ((in4 > max4) || (in4 < 0)) {
			inputinrange = false;
			errorstring = "The delay for Edge is to long (" + in4 / 1000
					+ " seconds?). \n";
		}
		if (inputinrange) {
			Object[] values = new Object[4];
			values[0] = new Double(in1);
			values[1] = new Integer(in2);
			values[2] = new Integer(in3);
			values[3] = new Integer(in4);
			return values;
		} else {
			Object[] error = new Object[1];
			error[0] = errorstring;
			return error;
		}
	}

	@Override
	public void generateNew() {
		// Object[] input = this.readInput(20, 200000, 60000); applet
		Object[] input = this.readInput(20, 500000, 60000);
		if (input.length != 1) {
			double in1 = ((Double) input[0]).doubleValue();
			int in2 = ((Integer) input[1]).intValue();
			int in3 = ((Integer) input[2]).intValue();
			int in4 = ((Integer) input[3]).intValue();
			frame.remove(frame.center);
			this.model = null;
			DrawClusterExploration newSim = new DrawClusterExploration(frame,
					new ClusterExploration(in1, in2, in3), in4, false);
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
		Object[] input = this.readInput(20, 2000000, Integer.MAX_VALUE);
		if (input.length == 1) {
			JOptionPane.showMessageDialog(frame,
					((StringBuffer) input[0]).toString());
		} else {
			try {
				double in1 = ((Double) input[0]).doubleValue();
				int in2 = ((Integer) input[1]).intValue();
				int in3 = ((Integer) input[2]).intValue();

				DrawClusterExploration newSim = new DrawClusterExploration(
						frame, new ClusterExploration(in1, in2, in3), 0, false);
				newSim.animate = false;
				ClusterExploration m = ((ClusterExploration) newSim.model);
				int w = (m.maxDis[0] - m.minDis[0]) * 3;
				int h = (m.maxDis[1] - m.minDis[1]) * 3;
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
		}
	}

	@Override
	public void reDraw() {
		animate = true;
		int i = SmallTools.giveInteger(frame.inputs[3].getText());
		if (i != Integer.MAX_VALUE)
			delay = i;
		this.paint(getGraphics());
	}

	@Override
	public String getHelpText() {
		String newLine = System.getProperty("line.separator");
		String content = "In this part we see how an random walk explores a percolation"
				+ newLine
				+ "cluster in a given dimension>1."
				+ newLine
				+ newLine
				+ "The Model"
				+ newLine
				+ "We consider the percolation cluster that is connected to the origin."
				+ newLine
				+ "We start a random walk at the origin and then the walk we choose"
				+ newLine
				+ "at each step on of the edges that is connected, uniformly, for this next"
				+ newLine
				+ "step. Doing this the walker explores the cluster."
				+ newLine
				+ "For the definition of percolation see the help at the correspond part."
				+ newLine
				+ "We only draw the first two coordinates of the walk and the Euclidean"
				+ newLine
				+ "norm of the position of the walker over time."
				+ newLine
				+ newLine
				+ "Parameters:"
				+ newLine
				+ "The user can give the edge probability of the percolation, the number of"
				+ newLine
				+ "steps of the random walk and the dimension. The number of steps"
				+ newLine
				+ "for the applet version is limit to 200000 and to 500000 for the downloadable"
				+ "version."
				+ newLine
				+ "Further we user can decide how long the drawing of the path should take.(max 1 min)"
				+ newLine
				+ newLine
				+ "A note to the implementation:"
				+ newLine
				+ "The percolation configuration is computed while the walker takes its steps"
				+ newLine
				+ "So each time the walker reaches a previously unseen node we check  "
				+ newLine
				+ "which of the connected edges are occupied."
				+ newLine
				+ "After this we take one of the connected occupied edges as the direction "
				+ newLine
				+ "of the next step. To save the explored edges we use a recurive "
				+ newLine
				+ "combination of hashmaps. For this reason we restrict the maximal"
				+ newLine
				+ "dimension to 20. We advise not to use to long walks in higher dimension"
				+ newLine
				+ "as the program will use up all memory."
				+ newLine
				+ "To avoid triviality of the inital cluster we will repeat the simulation up to 100 times,"
				+ newLine
				+ "until we obtain a non-trivial cluster."
				+ newLine
				+ newLine
				+ "To return to the program press the -Back- Button, that has replaced, "
				+ newLine
				+ "the help button or select a new part of the program";
		return content;
	}

	@Override
	public void saveCurrentlyShownToFile(File file, String type) {
		try {
			ClusterExploration m = ((ClusterExploration) model);
			int w = 0;
			int h = 0;
			if (Math.max(m.maxDis[0] - m.minDis[0], m.maxDis[1] - m.minDis[1]) > 1000) {
				w = (m.maxDis[0] - m.minDis[0]) * 3;
				h = (m.maxDis[1] - m.minDis[1]) * 3;
			} else {
				w = (m.maxDis[0] - m.minDis[0]) * 5;
				h = (m.maxDis[1] - m.minDis[1]) * 5;
			}
			BufferedImage locbi = new BufferedImage(w, h,
					BufferedImage.TYPE_BYTE_INDEXED);
			Graphics2D ig2 = locbi.createGraphics();
			this.paint(ig2, new Dimension(w, h));
			javax.imageio.ImageIO.write(locbi, type, file);
			locbi = new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_INDEXED);
			JOptionPane.showMessageDialog(frame,
					"Saving as picture is completed.");
			
			if (m.dim==3){
				extrasave(m,file);
			}
		} catch (java.io.IOException ie) {
			JOptionPane.showMessageDialog(frame, ie.toString());
		}
	
	}

	private void extrasave(ClusterExploration m, File fold) {
		String fname=fold.getAbsolutePath();
		System.out.println("Begin");
		File f = new File(fname.substring(0, fname.lastIndexOf("."))+ ".inc");
		try {	
			BufferedWriter out= new BufferedWriter(new FileWriter(f, false));
			Iterator< Serializable> it1 =m.exploredGrid.values().iterator();
			while(it1.hasNext()){
				HashMap<Integer, Serializable> l2=(HashMap<Integer, Serializable>) it1.next();
				Iterator< Serializable> it2=l2.values().iterator();
				while(it2.hasNext()){
					HashMap<Integer, Serializable> l3=(HashMap<Integer, Serializable>) it2.next();
					Iterator< Serializable> it3=l2.values().iterator();
					while(it3.hasNext()){
						HashMap<Integer, Serializable> l4=(HashMap<Integer, Serializable>) it3.next();
						Iterator< Serializable> it4=l3.values().iterator();
						while(it4.hasNext()){
							NNNode node= (NNNode) it4.next();
							
							out.write("sphere{ <"+node.pos[0]+","+node.pos[1]+","+node.pos[2]+">, 0.3  pigment{ color Black } } ");
							out.newLine();
							if (node.edges[0]){
								out.write("cylinder{ <"+node.pos[0]+","+node.pos[1]+","+node.pos[2]+"> , <"+(node.pos[0]-1)+","+node.pos[1]+","+node.pos[2]+">, 0.1  pigment{ color Orange } } ");
								//out.write("cylinder{ <"+node.pos[0]+","+node.pos[1]+","+node.pos[2]+"> , <"+(node.pos[0]+1)+","+node.pos[1]+","+node.pos[2]+">, 0.1  pigment{ color Blue } } ");
								out.newLine();
							}
							if (node.edges[1]){
								out.write("cylinder{ <"+node.pos[0]+","+node.pos[1]+","+node.pos[2]+"> , <"+node.pos[0]+","+(node.pos[1]-1)+","+node.pos[2]+">, 0.1  pigment{ color Orange } } ");
								//out.write("cylinder{ <"+node.pos[0]+","+node.pos[1]+","+node.pos[2]+"> , <"+node.pos[0]+","+(node.pos[1]+1)+","+node.pos[2]+">, 0.1  pigment{ color Orange  } } ");
								out.newLine();
							}
							if (node.edges[2]){
								out.write("cylinder{ <"+node.pos[0]+","+node.pos[1]+","+node.pos[2]+"> , <"+node.pos[0]+","+node.pos[1]+","+(node.pos[2]-1)+">, 0.1  pigment{ color Orange } } ");
								//out.write("cylinder{ <"+node.pos[0]+","+node.pos[1]+","+node.pos[2]+"> , <"+node.pos[0]+","+node.pos[1]+","+(node.pos[2]+1)+">, 0.1  pigment{ color Black } } ");
								out.newLine();
							}
						}	
					}
				}
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("End");
	}

	@Override
	public void continueSim() {
		// Dummy
	}
}