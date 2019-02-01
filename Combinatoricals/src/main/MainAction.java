package main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import drawer.*;

import utils.UserMessages;

/**
 * This is the only ActionListener of the program. So whenever any bottom is
 * dressed the action will end up here. I decide then what to do with the
 * different kind of actions that I associated this the bottom. So what follows
 * is a massive if else and reaction to get to know that bottom was pressed in
 * what setting.
 */

public class MainAction implements ActionListener {
	MainFrame frame;

	MainAction(MainFrame fr) {
		frame = fr;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// ================at first the items that open a new part of the
		// program=====================
		Outputapplet a = null;
		if ((e.getActionCommand()).equals(MainFrame.perc))
			a = new DrawPerc(frame);
		if ((e.getActionCommand()).equals(MainFrame.frozenperc))
			a = new DrawPercFrozen(frame);
		if ((e.getActionCommand()).equals(MainFrame.percdev))
			a = new DrawPercDev(frame);
		if ((e.getActionCommand()).equals(MainFrame.sawCounting))
			a = new DisplaySAWCount(frame);
		if ((e.getActionCommand()).equals(MainFrame.walkDrawing))
			a = new DrawRandomWalks(frame);
		if ((e.getActionCommand()).equals(MainFrame.branchingWalkDrawing))
			a = new DrawBranchingRandomWalks(frame);
		if ((e.getActionCommand()).equals(MainFrame.invasionperc))
			a = new DrawInvasionPercolation(frame);
		if ((e.getActionCommand()).equals(MainFrame.walkOnPerc))
			a = new DrawWalkOnPerc(frame);
		if ((e.getActionCommand()).equals(MainFrame.bootstrapBalls))
			a = new DrawBootstapBalls(frame);
		if ((e.getActionCommand()).equals(MainFrame.bootstrap))
			a = new DrawBootstap(frame);
		if ((e.getActionCommand()).equals(MainFrame.clusterExpArbD))
			a = new DrawClusterExploration(frame);
		if ((e.getActionCommand()).equals(MainFrame.clusterExpTwoD))
			a = new DrawClusterExplorationTwoD(frame);
		if ((e.getActionCommand()).equals(MainFrame.walkInLatticeGasTwoD))
			a = new DrawLorentzGas(frame);
		if ((e.getActionCommand()).equals(MainFrame.minesweeper)){
			System.out.println("mines ");
			a = new DrawMineSweeper(frame);
		}
			
		if ((e.getActionCommand()).equals(MainFrame.boxOfStars))
			a = new DrawBoxOfStars(frame);
		if ((e.getActionCommand()).equals(MainFrame.sandPileslattice))
			a = new DrawLatticeGraph(frame);
		if ((e.getActionCommand()).equals(MainFrame.sandPilesER))
			a = new DrawErdosComponentSandPiles(frame);
		if ((e.getActionCommand()).equals(MainFrame.inhomgeniousER))
			a = new DrawDistPrefGraph(frame);
		
			
		/*
		 * if ((e.getActionCommand()).equals(MainFrame.sandPilesRegularTree))
		 * frame.initialSandPilesRegularTree();
		 */

		if (a != null) {
			// System.out.println("adding "+a.toString());
			frame.remove(frame.center);
			frame.center = a;
			frame.add(frame.center, BorderLayout.CENTER);
			frame.validate();
		}
		// ==== now the go command, a simulation is already shown and we are
		// asked to generate a new one of the same kind=========
		if ((e.getActionCommand()).equals(MainFrame.gostring))
			frame.center.generateNew();
		if ((e.getActionCommand()).equals(MainFrame.redraw))
			frame.center.reDraw();
		if ((e.getActionCommand()).equals(MainFrame.continueString))
			frame.center.continueSim();

		if (e.getActionCommand().equals(MainFrame.filedraw))
			frame.drawNewIntoFile();

		if (e.getActionCommand().equals(MainFrame.filecurrentdraw))
			frame.drawCurrentIntoFile();

		// show the help in the central frame, change the help bottom to a back
		// bottom and if called for a second time redisplays the original frame
		// with
		// the shown walk/percolation..
		if ((e.getActionCommand()).equals(MainFrame.askforhelp))
			frame.center.showHelpForThisModel();
		if ((e.getActionCommand()).equals(MainFrame.askforGeneralhelp)) // the
																		// help...
			JOptionPane.showMessageDialog(frame,
					UserMessages.askforGeneralhelp());
		// shows the name of the Author of this program in the middle frame and
		// change the help bottom to a back bottom
		if ((e.getActionCommand()).equals(MainFrame.infoAuthor)) {
			// showMessage(UserMessages.getAuthorText());
			JOptionPane.showMessageDialog(frame, UserMessages.getAuthorText());
		}
		// Special event in the advanced Draw SAW part:
		// there we can draw an arbitrary number of walks with different length
		// and memory.
		// The different length and memory can be entered in different
		// textfields.
		// if we change the number of walks we need to change the number of
		// those textfields, with the following
		// if ((e.getActionCommand()).equals("resetnumberwalks"))
		// frame.initialSAWDrawAdvanced();

		// =================== Basic commands================
		if (e.getActionCommand().equals(MainFrame.open)) {
			frame.loadSimulaton();
		}
		if (e.getActionCommand().equals(MainFrame.save)) {
			frame.saveSimulaton();
		}
		if (e.getActionCommand().equals(MainFrame.quit)) {
			frame.setVisible(false);
			frame.dispose();
		}
	}

}
