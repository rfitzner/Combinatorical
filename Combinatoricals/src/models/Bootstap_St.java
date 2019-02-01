package models;

import java.awt.Dimension;
import java.util.LinkedList;

import drawer.DrawBootstap;
import drawer.Outputapplet;
import main.MainFrame;

/**
 * 
 * @author rfitzner
 * 
 */
@SuppressWarnings("serial")
public class Bootstap_St extends Bootstap_abstract {

	public boolean[][] hasneighbors;
	public boolean[][] occupied;

	public Bootstap_St(int w, int h, double p) {
		width=w;
		height=h;
		initialprob=p;
		agecount=0;
		hasneighbors= new boolean[w][h];
		occupied= new boolean[w][h];
		toCheck =new LinkedList<Dimension>();
		age = new int[w][h];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < w; j++) {
				hasneighbors[i][j] = false;
				if ( Math.random() < p ){
					occupied[i][j] = true;
					toCheck.add( new Dimension(i,j) );
					age[i][j]=0;
				}
				else occupied[i][j] = false;
			}
		}
	}
		
	public boolean check(Dimension d){
		LinkedList<Dimension> n = getNeighbors(d);
		boolean added=false;
		int a=0;
		while (!n.isEmpty()){
			Dimension e = n.poll();
			if (occupied[e.width][e.height] == false){
				if (hasneighbors[e.width][e.height] == false)
					hasneighbors[e.width][e.height] =  true;
				else {
					occupied[e.width][e.height]=true;
					a = age[d.width][d.height]+1;
					age[e.width][e.height]=a;
					agecount = Math.max(agecount,a);
					toCheck.add( e );
					added=true;
				}
			}
		}
		return added;
	}

	public LinkedList<Dimension> getNeighbors(Dimension d){
		LinkedList<Dimension> n= new LinkedList<Dimension>();
		if (d.width>0) n.add(new Dimension (d.width-1,d.height));
		else n.add(new Dimension (width-1,d.height));
		if (d.width<width-1) n.add(new Dimension (d.width+1,d.height));
		else n.add(new Dimension (0,d.height));
		if (d.height>0) n.add(new Dimension (d.width,d.height-1));
		else n.add(new Dimension (d.width,height-1));
		if (d.height < height -1 ) n.add(new Dimension (d.width,d.height+1));
		else n.add(new Dimension (d.width,0));
		return n;
	} 


	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return new DrawBootstap(fr, this, true);
	}

	@Override
	public boolean isInfected(int i, int j) {
		return this.occupied[i][j];
	}
	
	
}
