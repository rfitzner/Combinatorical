package models;

import java.util.LinkedList;
import main.MainFrame;
import drawer.Outputapplet;

@SuppressWarnings("serial")
public class DistancePrefGraph extends models.AbstractMathModel {

	public LinkedList<Node> graph;
	public int nrOfNode;
	public double alpha,c;
	int type;
	@SuppressWarnings("unchecked")
	public DistancePrefGraph(int n, double c1, double alpha1,int t) {
		alpha=alpha1;
		c=c1;
		nrOfNode=n;
		type=t;

		// create the graph
		double[] seed=new double[n];
		seed[0]=Math.random();
		for (int i = 1; i < nrOfNode; i++) {
			double newseed=Math.random();
			if (seed[i-1]<newseed){
				seed[i]=newseed;
			} else {
				int j=0;
				while (seed[j]<newseed){ j++;}
				for (int k = i; k > j; k--) {
					seed[k]=seed[k-1];
				}
				seed[j]=newseed;
			}
		}
		//System.out.println();
		//System.out.println();
		//System.out.println(listPrint(seed,nrOfNode));
		
		graph=new LinkedList<Node>();
		for (int i = 0; i < nrOfNode; i++) {
			graph.add(new Node(i,seed[i]));
		}
		
		
		for (int i = 1; i < nrOfNode ; i++) {
			Node node1=graph.get(i);
			double x1=node1.opinion;
			for (int j = 0; j < i; j++) {
				double x2=graph.get(j).opinion;
				double factor;
				if (type==0) factor=1/Math.pow(x1-x2,alpha)*1/nrOfNode*c;
				else if (type==1) factor=Math.pow(1-Math.abs(x1-x2),alpha)/nrOfNode*c;
				else factor=Math.exp(c/Math.pow(Math.abs(x1-x2),alpha))/nrOfNode;
				
				if ( Math.random()<factor ) {
					Node node2=graph.get(j);
					node1.addNeighbor(node2);
					node2.addNeighbor(node1);
					//System.out.println("Set edge "+ x1 +" to "+ x2 + " with factor "+factor);
				} //else System.out.println("No  edge "+ x1 +" to "+ x2 + " with factor "+factor);
			}
		}
		/*
		// Identify the biggest component
		// initialize a array of label
		int[] names = new int[nrOfNode];
		int newname = 1;
		for (int i = 0; i < names.length; i++) {
			names[i] = 0;
		}// all points are unlabeled
			// the we go through the graph and perform a deep search algorithm
		for (int i = 0; i < names.length; i++) {
			if (names[i] == 0) {// we encountered a new cluster
				names[i] = newname;

				Vector<AbstractNode> toExplore = (Vector<AbstractNode>) graph[i].neighbors
						.clone();
				while (!toExplore.isEmpty()) {
					AbstractNode p = toExplore.remove(0);
					// if names[p.label]!=0 we have already labeled the point so
					// we do nothing,
					// otherwise we label it an add its neighborhood to be
					// checked
					if (names[p.label] == 0) {
						names[p.label] = newname;
						toExplore.addAll((Vector<AbstractNode>) p.neighbors
								.clone());
					}
				}
				newname++;
			}
		}
		// now we identify the biggest component
		int[] count = new int[newname];
		for (int i = 0; i < count.length; i++)
			count[i] = 0;
		for (int i = 0; i < names.length; i++)
			count[names[i]]++;
		int maxname = 1;
		for (int i = 0; i < count.length; i++) {
			if (count[i] > count[maxname])
				maxname = i;
		}
		component = new AbstractNode[count[maxname]];
		int j = 0;
		for (int i = 0; i < names.length; i++) {
			if (names[i] == maxname) {
				component[j] = graph[i];
				component[j].label = j;
				j++;
			}
		}
		// this completes the creation of the graph
		// finally we change the last node into a garbage node.
		GarbageNode dummy = new GarbageNode();
		dummy.neighbors = component[component.length - 1].neighbors;
		Iterator<AbstractNode> it = dummy.neighbors.iterator();
		while (it.hasNext()) {
			AbstractNode p = it.next();
			p.neighbors.remove(component[component.length - 1]);
			p.neighbors.add(dummy);
		}
		component[component.length - 1] = dummy;
		// change the labels to simplify the drawing
		for (int i = 0; i < component.length; i++) {
			component[i].label = i;
		}
		// and in case randomize the heights
		if (initRandom) {
			for (int i = 0; i < component.length; i++) {
				component[i].height = (int) (component[i].neighbors.size() * Math
						.random());
			}
		}
		*/
	}


	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return null;//new DrawErdosComponentSandPiles(fr, this, true);
	}
	
	public static String listPrint(double[] list,int n){
		String s="{";
		for (int i = 0; i < n-1; i++) {
			s+=list[i]+",";
		}
		s+=list[n-1]+"}";
		return s;
	}

/*	public String printForJson(){
		String newLine = System.getProperty("line.separator");
		String s="{"+newLine+  "    \"nodes\":["+newLine;
		for (int i = 0; i < nrOfNode-1; i++) {
			s+="        {\"name\":\""+(graph.get(i).label+1)+"\",\"group\":"+(i+1)+"},"+newLine;
		}
		s+="        {\"name\":\" "+(graph.get(nrOfNode-1).label+1)+"\",\"group\":"+(nrOfNode)+"}"+newLine+"],"+newLine;
		s+="    \"links\":["+newLine;
		for (int i = 0; i < nrOfNode; i++) {
			Node node1=graph.get(i);
			int currsize=node1.getNeighbors().size();
			for (int j = 0; j < currsize; j++) {
				Node node2=node1.getNeighbors().get(j);
				if(node1.label>node2.label)
					s+="        {\"source\": "+(node1.label+1)+",\"target\":"+(node2.label+1)+",\"value\": 1 },"+newLine;
			}
		}
		s+="    ]"+newLine +"}";
		return s;
	}*/
	
	
	public String printForJson(){
		String newLine = System.getProperty("line.separator");
		
		int[] px = new int[nrOfNode];
		int[] py = new int[nrOfNode];
		for (int i = 0; i < nrOfNode; i++) {
			px[i] = (int) Math.round(300 + (300 *0.8)
					* Math.sin(2 * Math.PI * i / nrOfNode));
			py[i] = (int) Math.round(300 - (300 *0.8)
					* Math.cos(2 * Math.PI * i / nrOfNode));
		}

		
		String s="{"+newLine+  "    \"nodes\":["+newLine;
		for (int i = 0; i < nrOfNode-1; i++) {
			s+="        {\"x\":"+px[i]+",\"y\":"+py[i]+"},"+newLine;
		}
		s+="        {\"x\": "+px[nrOfNode-1]+",\"y\":"+py[nrOfNode-1]+"}"+newLine+"],"+newLine;
		s+="    \"links\":["+newLine;
		for (int i = 0; i < nrOfNode; i++) {
			Node node1=graph.get(i);
			int currsize=node1.getNeighbors().size();
			for (int j = 0; j < currsize; j++) {
				Node node2=node1.getNeighbors().get(j);
				if(node1.label>node2.label)
					s+="        {\"source\": "+node1.label+",\"target\":"+node2.label+" },"+newLine;
			}
		}
		s+="    ]"+newLine +"}";
		return s;
	}


	
	
	public class Node implements java.io.Serializable {
		public int label,clusterLabel;
		public double opinion;
		public LinkedList<Node> neighbors;

		public Node(int l,double d){
			neighbors = new LinkedList<Node>();
			label=l;
			opinion=d;
		}
		
		public LinkedList<Node> getNeighbors() {
			return neighbors;
		}

		public void addNeighbor(Node v) {
			neighbors.add(v);
		}

	}
	
}
