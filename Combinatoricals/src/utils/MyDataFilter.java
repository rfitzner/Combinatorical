package utils;

/**
 * This filter is used when we save or load from a file. It 
 * garanties that the files have the wished file ending.
 */

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class MyDataFilter extends FileFilter {

	String wantedextension;

	boolean picture;

	/**
	 * Default constructor that should only be used when you want to initalize
	 * and Filter that will be overwritten.
	 */
	public MyDataFilter(String extension) {
		super();
		wantedextension = extension;
		picture = false;
	}

	public MyDataFilter(boolean b) {
		picture = true;
		wantedextension = null;
	}

	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = getExtension(f);
		if (extension != null) {
			if (wantedextension != null) {
				if (extension.equalsIgnoreCase(wantedextension)) {
					return true;
				} else {
					return false;
				}
			} else {
				if (extension.equalsIgnoreCase("bmp")
						|| extension.equalsIgnoreCase("jpg")
						|| extension.equalsIgnoreCase("gif")) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	public String getDescription() {
		if (picture)
			return "Desired file extension : jpg or bmp or gif";
		else
			return "Desired file extension :" + wantedextension + ".";
	}

	public String getWantedExtension() {
		if (picture)
			return null;
		else
			return this.wantedextension;
	}

	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

}
