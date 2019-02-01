package simRunner;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import models.LorentzGas;

import utils.MyDataFilter;
import utils.SmallTools;

public class LorentzGasRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int maxsize = 100000;
		int runtimeinms = 6000;
		if (args.length > 0) {
			maxsize = SmallTools.giveInteger(args[0]);
			runtimeinms = SmallTools.giveInteger(args[1]);
			System.out.println(maxsize + " " + runtimeinms);
		}

		JFileChooser fc = new JFileChooser();
		// -------------------------
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setCurrentDirectory(new File("."));

		FileFilter filter = new MyDataFilter("txt");
		fc.setFileFilter(filter);
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		BufferedWriter out;
		int retVal = fc.showOpenDialog(new JFrame());
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File datei = fc.getSelectedFile();
			try {
				out = new BufferedWriter(new FileWriter(datei, true));
				long begintime = System.currentTimeMillis();
				LorentzGas m;
				int run = 0;
				int fail = 0;
				while (System.currentTimeMillis() - begintime < runtimeinms) {
					m = new LorentzGas(1, 0.5, maxsize);
					if (!m.path.get(m.path.size() - 1).equals(
							new Dimension(0, 0))) {
						out.write("-1");
						fail++;
					} else
						out.write("" + (m.path.size() - 1));
					out.newLine();
					run++;
				}
				out.close();
				System.out.println("Generated " + run + " walks. In total "
						+ fail
						+ " did not return within  the first 100000 steps:"
						+ (fail * 100.0 / run) + "% failure.");
			} catch (IOException exp) {
				System.out.println(exp.getMessage());
			}
		}
	}
}
