package models;

import main.MainFrame;
import drawer.Outputapplet;

public abstract class AbstractMathModel implements java.io.Serializable {
	public abstract Outputapplet createDrawer(MainFrame fr);
}
