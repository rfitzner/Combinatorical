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
public class Bootstap_Modified extends Bootstap_abstract {

	/**
	 * 0 = empty
	 * 1 = vertical neighbor
	 * 2 = horizontal neighbor
	 * 3 = infected
	 */
	byte state[][];

	public Bootstap_Modified(int w, int h, double p) {
		width=w;
		height=h;
		initialprob=p;
		agecount=0;
		state= new byte[w][h];
		toCheck =new LinkedList<Dimension>();
		age = new int[w][h];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < w; j++) {
				if ( Math.random() < p ){
					state[i][j] = 3;
					toCheck.add( new Dimension(i,j) );
					age[i][j]=0;
				}
				else state[i][j] = 0;
			}
		}
	}


	@Override
	public boolean check(Dimension d) {
		LinkedList<Dimension> n = getNeighbors(d);
		boolean added=false;
		int a=0;
		int direction=-1; //0,1 = left or right and 2,3 = top or bottom
		while (!n.isEmpty()){
			Dimension e = n.poll();
			direction++;
			if (state[e.width][e.height] < 3){
				if (state[e.width][e.height]==0){
					if ( direction < 2 )
						state[e.width][e.height]=1;
					else state[e.width][e.height]=2;
				} else if (state[e.width][e.height]==1){ // have (old) horizontal n					
					if ( direction > 1 ){// as well as have vertical n
						state[e.width][e.height]=3;
						a = age[d.width][d.height]+1;
						age[e.width][e.height]=a;
						agecount = Math.max(agecount,a);
						toCheck.add( e );
						added=true;
					}
				} else { // state==2, (old) vertical neighbor
					if ( direction < 2 ){// and a new horizontal n
						state[e.width][e.height]=3;
						a = age[d.width][d.height]+1;
						age[e.width][e.height]=a;
						agecount = Math.max(agecount,a);
						toCheck.add( e );
						added=true;
					}
				}
			}
		}
		return added;
	}
	
	
	public LinkedList<Dimension> getNeighbors(Dimension d){
		LinkedList<Dimension> n= new LinkedList<Dimension>();
		if (d.width>0) n.add(new Dimension (d.width-1,d.height)); // left
		else n.add(new Dimension (width-1,d.height));
		if (d.width<width-1) n.add(new Dimension (d.width+1,d.height)); // right
		else n.add(new Dimension (0,d.height));
		if (d.height>0) n.add(new Dimension (d.width,d.height-1));//top
		else n.add(new Dimension (d.width,height-1));
		if (d.height < height -1 ) n.add(new Dimension (d.width,d.height+1));//down
		else n.add(new Dimension (d.width,0));
		return n;
	}
	
	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return new DrawBootstap(fr, this, true);
	}

	@Override
	public boolean isInfected(int i, int j) {
		return (state[i][j]>2);
	}

}
