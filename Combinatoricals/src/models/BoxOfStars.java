package models;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import drawer.DrawBoxOfStars;
import drawer.DrawLorentzGas;
import drawer.Outputapplet;
import main.MainFrame;

/**
 * This class generates let a random walk explore a percolation cluster. So we
 * generate a simple random walks that only walks along occupied edges. Every
 * time the walk encounters a new edge we check whether the edge is occupied or
 * not then the walk proceeds.
 * 
 * @author Robert Fitzner
 */
public class BoxOfStars extends AbstractMathModel {
	private static final long serialVersionUID = 1L;
	// probability that a edge is occupied
	public double singletonweight;
	// total time in which the walk is drawn
	public int n,drawsteps,delay;
	int timesadded;
	public LinkedList<LinkedList<double[]>> sites;
	public LinkedList<LinkedList<double[]>> craters;

	/**
	 * 
	 * @param size
	 *            number of particles to be added
	 * @param weight
	 *            the bond probability of the percolation
	 * @param delay1
	 *            delay at drawing the edges
	 */
	public BoxOfStars(int size, int drawSteps, double weight,int delay1) {
		// INITIALISING
		n=size;
		this.drawsteps=Math.max(10, drawsteps);
		System.out.println(drawsteps);
		this.singletonweight=weight;
		this.delay=delay1;
		sites = new LinkedList<LinkedList<double[]>>();
		sites.add(new LinkedList<double[]>());
		
		craters = new LinkedList<LinkedList<double[]>>();
		craters.add(new LinkedList<double[]>());
		
		timesadded=0;
		for(int t=0; t<n;t++){
			double[] pos=new double[3];
			pos[0]=Math.random();
			pos[1]=Math.random();
			pos[2]=1;
			
			craters.get(t).add(pos);
			
			int index= putNewTo(pos,t);
			if( index ==-1 ) {
				sites.get(t).add(pos);
			} else {
				sites.get(t).get(index)[2]+=1;
			}
			
			LinkedList<double[]> nextlist=new LinkedList<double[]>();
			for (int i=0; i<sites.get(t).size();i++){
				double[] old=sites.get(t).get(i);
				double[] news= new double[3]; 
				for(int j=0; j<3;j++){
					news[j]=old[j];
				}
				nextlist.add(news);
			}
			sites.add(nextlist);

			LinkedList<double[]> nextlist2=new LinkedList<double[]>();
			for (int i=0; i<craters.get(t).size();i++){
				double[] old2=craters.get(t).get(i);
				double[] news2= new double[3]; 
				for(int j=0; j<3;j++){
					news2[j]=old2[j];
				}
				nextlist2.add(news2);
				
			}
			
			craters.add(nextlist2);
		}
	}

	private static double norm(double[] x,double[] y){
		if ((x.length!=3) || (y.length!=3)) return -1;
		return Math.sqrt( (x[0]-y[0])*(x[0]-y[0])+(x[1]-y[1])*(x[1]-y[1]) );
	}
	
	/**
	 * HERE WE COULD CHOOSE ANY FUNCTION WE LIKE
	 */
	private static double singleForce(double[] x, double[] y){
		if ((x.length!=3) || (y.length!=3)) return -1;
		return x[2]*y[2]/Math.pow(norm(x,y),2);
	}
	
	private int putNewTo(double[] newpoint,int time){
		timesadded++;
		double random=Math.random();
		int nrstars = sites.get( time ).size();
		double[] weights=new double[nrstars];
		double normaliser=this.singletonweight;
		for (int i=0; i<nrstars;i++){
			weights[i]=singleForce(newpoint,sites.get( time ).get(i));
			normaliser+=weights[i];
		}
		double step=this.singletonweight/normaliser;
		if (random<step) {
			System.out.println("Decided to create a new star at time "+ timesadded +", chances forthis was "+step);
			return -1;
		}
		for (int i=0; i<nrstars;i++){
			step+=weights[i]/normaliser;
			if (random<step) return i;
		}
		return -2;
	}
	
	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return new DrawBoxOfStars(fr, this, 1000, true);
	}

}