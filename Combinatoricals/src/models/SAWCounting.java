package models;

import main.MainFrame;

import drawer.DisplaySAWCount;
import drawer.Outputapplet;

@SuppressWarnings("serial")
public class SAWCounting extends AbstractMathModel {
	public int dimension, steps;
	public double timeToCalculate;
	public String[] cnList;
	public boolean toBigToCompute;
	public String tolong;
	public String result;

	public SAWCounting(int dim, int n) {
		dimension = dim;
		steps = n;
		produceCentralOutput(dim, n);
		tolong = "At the time that our comuter has finished the computations"
				+ System.getProperty("line.separator")
				+ "the atom your are build have long stopped existing.";
	}

	/**
	 * Computes or load the number of walk for the given parameters
	 * 
	 * @param dim
	 * @param pathl
	 */
	public void produceCentralOutput(int dim, int pathl) {
		String[] listofCns = usedcalculatedCnUpToN(dim);
		cnList = new String[Math.min(pathl + 1, listofCns.length + 1)];

		// The first thing I do is to check if I calculate it myself
		// I do not want computations of more a minutes, the used bound for come
		// from experience.
		//
		if (((dim == 2) && (pathl > 15)) ||((dim == 3) && (pathl > 12)) || ((dim > 2) && (pathl > 11))) {
			// I check and it would take to long to do it live
			toBigToCompute = true;
			// so I take the result from the one I know
			for (int i = 0; i < Math.min(pathl, listofCns.length); i++) {
				cnList[i] = listofCns[i];
			}
			// and estimate the time I need to compute the desired result
			long basictime;
			if (dim == 2) {
				long[] numberlist = calculateAllCnUpToN(dim, 15);
				basictime = numberlist[15];
				timeToCalculate = Math.round(basictime
						* Math.exp((pathl - 15) * Math.log(2.8)));
			} else if (dim == 3) {
				long[] numberlist = calculateAllCnUpToN(dim, 11);
				basictime = numberlist[11];
				timeToCalculate = Math.round(basictime
						* Math.exp((pathl - 11) * Math.log(4.9)));
			} else {
				long[] numberlist = calculateAllCnUpToN(dim, 10);
				basictime = numberlist[10];
				timeToCalculate = basictime
						* Math.exp((pathl - 10) * Math.log(2 * dim - 1.1));
			}
		} else {
			long[] numberlist;

			// if (dim==2)
			// numberlist = CalculateAllCnUpToNDim2(pathl);
			// else
			numberlist = calculateAllCnUpToN(dim, pathl);
			toBigToCompute = false;
			cnList = new String[pathl + 1];
			for (int i = 0; i < pathl; i++) {
				cnList[i] = (new Long(numberlist[i])).toString();
			}
			timeToCalculate = numberlist[pathl];
		}
	}

	/**
	 * 
	 * We compute the number of all self-avoiding walks of length -pathl-, as a
	 * byproduct we also compute the number of all walks with less steps. We
	 * return the number of walk in the list, where I should high-light here
	 * that I use the last element to return the timeit took to compute the
	 * result.
	 */
	public static long[] calculateAllCnUpToN(int dim, int pathl) {
		if (pathl < 2) {
			long[] Cns = new long[2];
			Cns[0] = 1;
			Cns[1] = 2 * dim;
			return Cns;
		}
		int i; // counter for loop of the steps
		int d; // counter to loop over the dimension
		int l; // l for possible loop-length (back-vision-range/memory) and

		long[] Cns = new long[pathl + 1];
		Cns[0] = 2 * dim;
		for (i = 1; i < pathl; i++) {
			Cns[i] = 0;
		}
		// I use the last entry to save the need time
		Cns[pathl] = System.currentTimeMillis();

		// computes the number of walks that the pattern will represent, this
		// depends on the total number of dimension and the number of dimensions
		// the pattern uses.
		long[] numberofaquevalences = new long[dim + 1];
		numberofaquevalences[0] = 0;
		for (i = 0; i < dim; i++) {
			int tmp = 1;
			for (d = 0; d <= i; d++) {
				tmp = tmp * (dim - d) * 2;
			}
			numberofaquevalences[i + 1] = tmp;
		}
		// initialising the path-variable in steps and in verticies.
		int[] currentsteps = new int[pathl];
		int[][] currentvertices = new int[pathl + 1][dim + 1];
		for (i = 0; i < pathl; i++) {
			currentsteps[i] = 0;
			for (d = 1; d < dim + 1; d++)
				currentvertices[i][d] = 0;
		}
		currentsteps[0] = 1;
		currentvertices[1][1] = 1;
		// we will keep tract of the dimensions we use up to a step
		int[] useddimensions = new int[pathl];
		for (i = 0; i < pathl; i++) {
			useddimensions[i] = 1;
		}

		// Here we actually begin with the Computation, it uses a simple
		// recursion
		// that goes though all possible combinations. Thereby we descent in the
		// recursive structur at most to the level -pathl-.
		int level = 1;

		while (level > 0) // we keep creating new path until we try to change
		// the first step
		{
			// iteration the current walk in terms of the last step

			// we made last time a negative step or we are here for the first
			// time
			if (currentsteps[level] < 1) {
				// The last pattern we observer made a movement in direction
				// 0,-1 -2
				// so have have to go in the next dimension with positive sign
				// so
				// e.g. we count walks with -2 and now change this to 3
				currentsteps[level] = 1 - currentsteps[level];
			} else {
				// we looked at i,2,3 now we look at the opposite direction.
				currentsteps[level] = -currentsteps[level];
			}

			// explained after the if::
			if ((currentsteps[level] != -(useddimensions[level - 1] + 1))
					&& currentsteps[level] <= dim)
			// the first condition: we want to start each new used dimension
			// in the positive direction, so we count 112(-1)23 but not
			// 112(-1)2(-3)
			// Thereby we also ensure what we do not overleap a dimension. So
			// that 124 does not occur.
			// the second condition checkes whether we do want to label that is
			// bigger then
			// the number of dimension the method should use
			{
				// we copy want to descent a level to modify the next position,
				// wherefore we first copy some information form the last level
				for (d = 1; d < dim + 1; d++)
					currentvertices[level + 1][d] = currentvertices[level][d];
				// and update the information where we are in vertex form
				if (currentsteps[level] > 0)
					currentvertices[level + 1][currentsteps[level]]++;
				else
					currentvertices[level + 1][Math.abs(currentsteps[level])]--;

				// and use the vertex form to check for in intersection with
				// this new step
				boolean noloop = true;
				for (l = 2; l <= level + 1 && noloop; l += 2) {
					// we check if we reached this position already at steps l
					// Thereby we use that on the Zd lattice we can only make
					// even-step-loops
					boolean equalUpToNow = true;
					for (d = 1; d <= dim && equalUpToNow; d++)
						if (currentvertices[level + 1 - l][d] != currentvertices[level + 1][d])
							equalUpToNow = false;
					if (equalUpToNow)
						noloop = false;
				}
				// If this path is ok we save the numbers and descent,
				// otherwise we skip it.
				if (noloop) {
					if (level < pathl - 1) // if we are not at the the end, we
					// descent once more.
					{
						// before descending we update useddimension if
						// necessary
						// and add the number of paths that this valid pattern
						// represents
						if (currentsteps[level] == useddimensions[level - 1] + 1) {
							useddimensions[level] = useddimensions[level - 1] + 1;
						} else {
							useddimensions[level] = useddimensions[level - 1];
						}
						Cns[level] += numberofaquevalences[useddimensions[level]];
						level++;// descent
					} else
					// We are at the end, so we do not want to ascent.
					// But we need to save the number of SAW
					{
						if (currentsteps[level] == useddimensions[level - 1] + 1) {
							Cns[level] += numberofaquevalences[useddimensions[level - 1] + 1];
						} else {
							Cns[level] += numberofaquevalences[useddimensions[level - 1]];
						}
					}
				} // end the if when this path of length level was a mistake
			} else { // we have no more variants to check for this current
				// path, so we delete the last step and ascent once

				currentsteps[level] = 0;// before ascending we delete the traces
										// we left in the recursion
				level--;
			}// end of looking at this path tail
		}// end of terminating while over level
		Cns[pathl] = System.currentTimeMillis() - Cns[pathl];

		return Cns;
	}

	/**
	 * This method return the saved number of walks, established by Clisby,
	 * Liang and Slade
	 * 
	 * @param dim
	 * @return
	 */
	public static String[] usedcalculatedCnUpToN(int dim) {
		String[] list;
		switch (dim) {
		case 2: {
			String[] tmp = { "4", "12", "36", "100", "284", "780", "2172",
					"5916", "16268", "44100", "120292", "324932", "881500",
					"2374444", "6416596", "17245332", "46466676", "124658732",
					"335116620", "897697164", "2408806028", "6444560484",
					"17266613812", "46146397316", "123481354908",
					"329712786220", "881317491628", "2351378582244",
					"6279396229332", "16741957935348", "44673816630956",
					"119034997913020", "317406598267076", "845279074648708",
					"2252534077759844", "5995740499124412",
					"15968852281708724", "42486750758210044",
					"113101676587853932", "300798249248474268",
					"800381032599158340", "2127870238872271828",
					"5659667057165209612", "15041631638016155884",
					"39992704986620915140", "106255762193816523332",
					"282417882500511560972", "750139547395987948108",
					"1993185460468062845836", "5292794668724837206644",
					"14059415980606050644844", "37325046962536847970116",
					"99121668912462180162908", "263090298246050489804708",
					"698501700277581954674604", "1853589151789474253830500",
					"4920146075313000860596140", "13053884641516572778155044",
					"34642792634590824499672196", "91895836025056214634047716",
					"243828023293849420839513468",
					"646684752476890688940276172",
					"1715538780705298093042635884",
					"4549252727304405545665901684",
					"12066271136346725726547810652",
					"31992427160420423715150496804",
					"84841788997462209800131419244",
					"224916973773967421352838735684",
					"596373847126147985434982575724",
					"1580784678250571882017480243636",
					"4190893020903935054619120005916" };
			list = tmp.clone();
			break;
		}
		case 3: {
			String[] tmp = { "6", "30", "150", "726", "3534", "16926", "81390",
					"387966", "1853886", "8809878", "41934150", "198842742",
					"943974510", "4468911678", "21175146054", "100121875974",
					"473730252102", "2237723684094", "10576033219614",
					"49917327838734", "235710090502158", "1111781983442406",
					"5245988215191414", "24730180885580790",
					"116618841700433358", "549493796867100942",
					"2589874864863200574", "12198184788179866902",
					"57466913094951837030", "270569905525454674614" };
			list = tmp.clone();
			break;
		}
		case 4: {
			String[] tmp = { "8", "56", "392", "2696", "18584", "127160",
					"871256", "5946200", "40613816", "276750536", "1886784200",
					"12843449288", "87456597656", "594876193016",
					"4047352264616", "27514497698984", "187083712725224",
					"1271271096363128", "8639846411760440",
					"58689235680164600", "398715967140863864",
					"2707661592937721288", "18389434921635285800",
					"124852857467211187784" };
			list = tmp.clone();
			break;
		}
		case 5: {
			String[] tmp = { "10", "90", "810", "7210", "64250", "570330",
					"5065530", "44906970", "398227610", "3527691690",
					"31255491850", "276741169130", "2450591960890",
					"21690684337690", "192003889675210", "1699056192681930",
					"15035937610909770", "133030135015071770",
					"1177032340670878170", "10412322608416261050",
					"92113105222899934010", "814766179787983302090",
					"7207026563685440727850", "63742525570299581210090" };
			list = tmp.clone();
			break;
		}
		case 6: {
			String[] tmp = { "12", "132", "1452", "15852", "173172", "1887492",
					"20578452", "224138292", "2441606532", "26583605772",
					"289455960492", "3150796704012", "34298615880372",
					"373292253262692", "4062873240668412", "44214072776280252",
					"481167126859845852", "5235893033922430692",
					"56975931806991140292", "619957835069070600132",
					"6745858105534183489092", "73398893398168440782892",
					"798629075137768054499292", "8689265092167904101731532" };
			list = tmp.clone();
			break;
		}
		case 7: {
			String[] tmp = { "14", "182", "2366", "30590", "395654", "5110070",
					"66009062", "852194966", "11002765718", "142019952830",
					"1833202179662", "23659632189662", "305360673698150",
					"3940760013826454", "50857078231126286",
					"656293571739976142", "8469305943784113806",
					"109290078485661202262", "1410313416278288850230",
					"18198630021961664962694", "234835176481489026589958",
					"3030253517601098034318254", "39101713771945255704676382",
					"504552243465714026682387806" };
			list = tmp.clone();
			break;
		}
		case 8: {
			String[] tmp = { "16", "240", "3600", "53776", "803504",
					"11994096", "179054640", "2672126256", "39878886896",
					"595065468048", "8879592484240", "132491660323472",
					"1976912303612080", "29496313445323888",
					"440098575225868624", "6566302628140689744",
					"97969968518462054352", "1461698348385616122224",
					"21808373396863539892464", "325374770476507076809584",
					"4854505122280192400621168", "72427352538238368039098256",
					"1080589134633584722212134224",
					"16121895426864166076568532880" };
			list = tmp.clone();
			break;
		}
		case 9: {
			String[] tmp = { "18", "306", "5202", "88146", "1493874",
					"25300530", "428518386", "7256300850", "122876680626",
					"2080586127186", "35229409431570", "596495353475538",
					"10099744526658546", "171003188767881906",
					"2895335387107970706", "49021668492861718674",
					"829999403731225961874", "14052840969325999278258",
					"237930859323785632760370", "4028424846336006393611250",
					"68205585749645874319685874",
					"1154790442297055887026756498",
					"19551790737608267993216369490",
					"331031180656504515248478230994" };
			list = tmp.clone();
			break;
		}
		case 10: {
			String[] tmp = { "20", "380", "7220", "136820", "2593100",
					"49121660", "930556460", "17625825740", "333857601020",
					"6323384122580", "119767717450100", "2268399952520660",
					"42963566150826380", "813721674662589980",
					"15411746407417290020", "291893918240586194660",
					"5528387235193561980740", "104705682990258791936540",
					"1983088865824542959434940", "37558921405099250412242300",
					"711351287961645290529969020",
					"13472695702762071088671214580",
					"255167246010321746624740564100",
					"4832756707052478587170415132180" };
			list = tmp.clone();
			break;
		}
		case 11: {
			String[] tmp = { "22", "462", "9702", "203302", "4260542",
					"89253582", "1869809502", "39167457582", "820458452462",
					"17185914925542", "359989506212182", "7540511273930822",
					"157947298263243742", "3308420553034902382",
					"69299392385043268822", "1451565583054963249302",
					"30404929596858248780502", "636869613282097404699182",
					"13340039327784366568293582",
					"279423686104297325939502462",
					"5852876455869447505259219582",
					"122595691140615738285574648182",
					"2567917546820289870716780157382",
					"53788168727245424866692455320262" };
			list = tmp.clone();
			break;
		}
		case 12: {
			String[] tmp = { "24", "552", "12696", "291480", "6692424",
					"153614760", "3526063752", "80931227016", "1857565708968",
					"42634594787160", "978544945823832", "22459264078075992",
					"515478463349872200", "11831064537706447464",
					"271542137952854806776", "6232321082672399260152",
					"143041632747658763159736", "3283028620369535296924392",
					"75350633086861362709510440",
					"1729413736555694364257240424",
					"39692725190511323920669792488",
					"911009259819999998697513672024",
					"20909067930293907689071201132152",
					"479895271559400621984606896091096" };
			list = tmp.clone();
			break;
		}
		default:
			list = new String[1];
			list[0] = "I have no Data for this dimension.";
		}

		return list;
	}

	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return new DisplaySAWCount(fr, this, true);
	}

}
