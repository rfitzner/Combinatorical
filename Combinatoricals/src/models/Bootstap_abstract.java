package models;

import java.awt.Dimension;
import java.util.LinkedList;

import drawer.DrawBootstap;
import drawer.Outputapplet;
import main.MainFrame;

/**
 * begin 7.3.2012 14:40 end of this 7.3.2012 16:50
 * 
 * @author rfitzner
 * 
 */
@SuppressWarnings("serial")
public abstract class Bootstap_abstract extends AbstractMathModel {

	public int width,height;
	public double initialprob;
	public LinkedList<Dimension> toCheck;
	public int[][] age;
	public int agecount;
	
	public void checkGeneration(){
		Dimension e= toCheck.poll();
		int generation= age[e.width][e.height];
		check(e);
		boolean cont=true;
		do{
			e=toCheck.peek();
			if (e==null) cont=false;
			else {
				if (generation== age[e.width][e.height]){
					check(e);
					toCheck.poll();
				}
				else cont=false;
			}
		} while(cont);
	}
	
	public void checkNext(){
		check(toCheck.poll());
	}
	
	public abstract boolean check(Dimension d);
	
	public abstract boolean isInfected(int i, int j);
	
		
}
