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

public class quickevRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		JFileChooser fc = new JFileChooser();
		// -------------------------
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setCurrentDirectory(new File("."));

		FileFilter filter = new MyDataFilter("txt");
		fc.setFileFilter(filter);

		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		BufferedReader in;
		int retVal = fc.showOpenDialog(new JFrame());
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File dateiIn = fc.getSelectedFile();
			File dateiOut = new File(dateiIn.getAbsolutePath().substring(0,
					dateiIn.getAbsolutePath().lastIndexOf("."))
					+ "ch"
					+ dateiIn.getAbsolutePath().substring(
							dateiIn.getAbsolutePath().lastIndexOf(".")));
			String zeile;
			try {
				dateiOut.createNewFile();
				in = new BufferedReader(new FileReader(dateiIn));
				int c = 0;
				int[] frequ = new int[10000001];
				for (int i = 0; i < frequ.length; i++) {
					frequ[i] = 0;
				}
				while ((zeile = in.readLine()) != null) {
					int i = SmallTools.giveInteger(zeile);
					if (i == -1)
						frequ[frequ.length - 1]++;
					else
						frequ[i]++;
					c++;
				}
				int[] tail = new int[10000001];
				double[] tailprob = new double[10000001];
				tail[frequ.length - 1] = frequ[frequ.length - 1];
				tailprob[frequ.length - 1] = tail[frequ.length - 1] * 1.0 / c;
				for (int i = frequ.length - 2; i > -1; i--) {
					tail[i] = tail[i + 1] + frequ[i];
					tailprob[i] = tail[i] * 1.0 / c;
				}
				int step = 1000000;
				for (int i = 0; i < frequ.length - 1; i = i + step) {
					double diff = Math.log(tailprob[i] * 1.0)
							- Math.log(tailprob[i + step] * 1.0);
					System.out.print(tailprob[i]);
					System.out.println("  " + diff / step);
				}

				in.close();
				System.out.println("done");
			} catch (IOException exp) {
				System.out.println(exp.getMessage());
			}
		}

	}
}
