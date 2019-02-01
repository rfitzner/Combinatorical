package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 */
public class DateiIO {
	/**
	 * 
	 * @param filename
	 * @return ArrayList <String>, file content, where each entry is a line in
	 *         the file
	 */
	public static ArrayList<String> readFileLineWise(String filename) {

		BufferedReader in;

		ArrayList<String> lines = new ArrayList<String>();

		String zeile;

		try {
			in = new BufferedReader(new FileReader(new File(filename)));
			while ((zeile = in.readLine()) != null) {
				lines.add(zeile);
			}
			in.close();
		} catch (IOException exp) {
			System.out.println(exp.getMessage());
		}
		return lines;
	}

	/**
	 * 
	 * @param file
	 * @param new content, each entry will be written as a separate line in the
	 *        file
	 */
	public static void writeFileLineWise(String filename, String[] arg) {
		int len = arg.length;
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(filename, false)); // pb ->
																		// true
																		// hinzugefügt
																		// um
																		// anzufügen
			for (int i = 0; i < len; i++) {
				out.write(arg[i]);
				out.newLine();
			}
			out.close();
		} catch (IOException exp) {
			System.out.println(exp.getMessage());
		}
	}

	/**
	 * @param file
	 * @param obj
	 *            - object, to be written, the class involved need to implement
	 *            java.io.Serializable
	 */
	public static void writefileObj(File file, Object obj) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(file)));
			out.writeObject(obj);
			out.flush();
			out.close();
		} catch (IOException exp) {
			System.out.println(exp.getMessage());
		}
	}

	/**
	 * 
	 * @param filename
	 * @return object that was saved in the file, the class involved need to
	 *         implement java.io.Serializable
	 */
	public static Object readFileObj(File datei) {
		Object obj = null;
		try {
			ObjectInputStream in = new ObjectInputStream(
					new BufferedInputStream(new FileInputStream(datei)));
			obj = in.readObject();
			in.close();
		} catch (IOException exp) {
			System.out.println(exp.getMessage());
		} catch (ClassNotFoundException exp) {
			System.out.println(exp.getMessage());
		}
		return obj;
	}
}// end of class