package main;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * The menu bar to select a model and open the help message.
 */
class MenueLeiste extends JMenuBar {
	private static final long serialVersionUID = 1L;

	// the menu part
	private JMenu mnuFile = new JMenu("File"),
			mnuModelWalk = new JMenu("Walks"), mnuModelPerc = new JMenu(
					"Percolation"), mnuModelWalkOnPerc = new JMenu(
					"SRWalk on Percolation"), mnuModelsandPiles = new JMenu(
					"Sandpiles"), mnuModelOther = new JMenu("Other"),
			mnuInfo = new JMenu("Info");
	// and there items
	private JMenuItem mniFileSave = new JMenuItem(MainFrame.save),
			mniFileOpen = new JMenuItem(MainFrame.open),
			mniFileDraw = new JMenuItem(MainFrame.filedraw),
			mniFileCurrentDraw = new JMenuItem(MainFrame.filecurrentdraw),
			mniEnde = new JMenuItem(MainFrame.quit);

	// the choice of model
	private JMenuItem mniDSAW = new JMenuItem(MainFrame.walkDrawing),
			mniBranching = new JMenuItem(MainFrame.branchingWalkDrawing),
			mniCSAW = new JMenuItem(MainFrame.sawCounting),
			mniperc = new JMenuItem(MainFrame.perc),
			mnipercdev = new JMenuItem(MainFrame.percdev),
			mnipercinvasion = new JMenuItem(MainFrame.invasionperc),
			mnipercfrozen = new JMenuItem(MainFrame.frozenperc),
			mnibootpercBall = new JMenuItem(MainFrame.bootstrapBalls),
			mnibootperc = new JMenuItem(MainFrame.bootstrap),
			mnipercwalk = new JMenuItem(MainFrame.walkOnPerc),
			mniwalkingas = new JMenuItem(MainFrame.walkInLatticeGasTwoD),
			mnipercwalkHighD = new JMenuItem(MainFrame.clusterExpArbD),
			mniclusterExp = new JMenuItem(MainFrame.clusterExpTwoD),
			mniminesweeper = new JMenuItem(MainFrame.minesweeper),
			mniInhomDist = new JMenuItem(MainFrame.inhomgeniousER),
			mnistarbox = new JMenuItem(MainFrame.boxOfStars),
			mnisandPilesLattice = new JMenuItem(MainFrame.sandPileslattice),
			mnisandPilesER = new JMenuItem(MainFrame.sandPilesER),
			mnisandPilesRegularTree = new JMenuItem(
					MainFrame.sandPilesRegularTree);

	private JMenuItem mniInfoAuthor = new JMenuItem("Author"),
			mniInfoModel = new JMenuItem("Help for the applet");
	// the main frame to which this menu belongs
	private MainFrame mainFrame = null;

	public MenueLeiste(MainFrame fenster) {
		this.mainFrame = fenster;

		// ----FILE Part---
		mnuFile.add(mniFileOpen);
		mnuFile.add(mniFileSave);
		mnuFile.addSeparator();
		mnuFile.add(mniFileDraw);
		mnuFile.add(mniFileCurrentDraw);
		mnuFile.addSeparator();

		mnuFile.add(mniEnde);
		mniFileOpen.setActionCommand(MainFrame.open);
		mniFileSave.setActionCommand(MainFrame.save);
		mniFileDraw.setActionCommand(MainFrame.filedraw);
		mniFileCurrentDraw.setActionCommand(MainFrame.filecurrentdraw);
		mniFileOpen.addActionListener(mainFrame.listener);
		mniFileSave.addActionListener(mainFrame.listener);
		mniFileDraw.addActionListener(mainFrame.listener);
		mniFileCurrentDraw.addActionListener(mainFrame.listener);
		mniEnde.setActionCommand(MainFrame.quit);
		mniEnde.addActionListener(mainFrame.listener);

		// ----Model choice---
		mnuModelWalk.add(mniDSAW);
		mnuModelWalk.add(mniCSAW);
		mnuModelWalk.add(mniwalkingas);
		mnuModelWalk.add(mniBranching);
		mnuModelPerc.add(mniperc);
		mnuModelPerc.add(mnipercdev);
		mnuModelPerc.add(mnipercinvasion);
		// mnuModelPerc.add(mnipercfrozen);

		mnuModelPerc.add(mnibootpercBall);
		mnuModelPerc.add(mnibootperc);
		mnuModelWalkOnPerc.add(mnipercwalk);
		mnuModelWalkOnPerc.add(mnipercwalkHighD);
		mnuModelWalkOnPerc.add(mniclusterExp);

		mniDSAW.setActionCommand(MainFrame.walkDrawing);
		mniBranching.setActionCommand(MainFrame.branchingWalkDrawing);
		mniCSAW.setActionCommand(MainFrame.sawCounting);
		mniperc.setActionCommand(MainFrame.perc);
		mnipercdev.setActionCommand(MainFrame.percdev);
		mnipercinvasion.setActionCommand(MainFrame.invasionperc);
		mnibootpercBall.setActionCommand(MainFrame.bootstrapBalls);
		mnibootperc.setActionCommand(MainFrame.bootstrap);
		mnipercfrozen.setActionCommand(MainFrame.frozenperc);
		mnipercwalk.setActionCommand(MainFrame.walkOnPerc);
		mniwalkingas.setActionCommand(MainFrame.walkInLatticeGasTwoD);
		mnipercwalkHighD.setActionCommand(MainFrame.clusterExpArbD);
		mniclusterExp.setActionCommand(MainFrame.clusterExpTwoD);

		mniDSAW.addActionListener(mainFrame.listener);
		mniBranching.addActionListener(mainFrame.listener);
		mniCSAW.addActionListener(mainFrame.listener);
		mniperc.addActionListener(mainFrame.listener);
		mnipercdev.addActionListener(mainFrame.listener);
		mnipercinvasion.addActionListener(mainFrame.listener);
		mnipercfrozen.addActionListener(mainFrame.listener);
		mnibootpercBall.addActionListener(mainFrame.listener);
		mnibootperc.addActionListener(mainFrame.listener);
		mnipercwalk.addActionListener(mainFrame.listener);
		mnipercwalkHighD.addActionListener(mainFrame.listener);
		mniclusterExp.addActionListener(mainFrame.listener);
		mniwalkingas.addActionListener(mainFrame.listener);

		mniminesweeper.setActionCommand(MainFrame.minesweeper);
		mniminesweeper.addActionListener(mainFrame.listener);

		mnistarbox.setActionCommand(MainFrame.boxOfStars);
		mnistarbox.addActionListener(mainFrame.listener);
		
		mniInhomDist.setActionCommand(MainFrame.inhomgeniousER);
		mniInhomDist.addActionListener(mainFrame.listener);
		
		mnuModelOther.add(mniminesweeper);
		mnuModelOther.add(mnistarbox);
		mnuModelOther.add(mniInhomDist);

		// ----Model choice---
		// -- SandPiles
		mnuModelsandPiles.add(mnisandPilesLattice);
		mnuModelsandPiles.add(mnisandPilesER);
		// mnuModelsandPiles.add(mnisandPilesRegularTree);
		mnisandPilesLattice.setActionCommand(MainFrame.sandPileslattice);
		mnisandPilesER.setActionCommand(MainFrame.sandPilesER);
		mnisandPilesRegularTree
				.setActionCommand(MainFrame.sandPilesRegularTree);
		mnisandPilesLattice.addActionListener(mainFrame.listener);
		mnisandPilesER.addActionListener(mainFrame.listener);
		mnisandPilesRegularTree.addActionListener(mainFrame.listener);

		// ----Info Part---
		mnuInfo.add(mniInfoModel);
		mnuInfo.add(mniInfoAuthor);
		mniInfoModel.setActionCommand(MainFrame.askforGeneralhelp);
		mniInfoAuthor.setActionCommand(MainFrame.infoAuthor);
		mniInfoAuthor.addActionListener(mainFrame.listener);
		mniInfoModel.addActionListener(mainFrame.listener);

		// -- add the main items
		this.add(mnuFile);
		this.add(mnuModelWalk);
		this.add(mnuModelPerc);
		this.add(mnuModelWalkOnPerc);
		this.add(mnuModelsandPiles);
		this.add(mnuModelOther);
		this.add(mnuInfo);
	}// end of constructor

}