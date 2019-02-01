package utils;

/**
 * In this class generate Memory random walks on the Z-2 lattice. The length and
 * memory should be given as parameter
 * 
 * @author Robert Fitzner
 */
public class SAWgens {

	// We want to generate a memory walk in dimension 2.
	// The problem we can get trapped in a dead end. and that is VERY likely to
	// happen, so I need to find a method to revise and get out of the trap
	// again
	public static int[] generateaMSAW(int m, int pathlength) {
		// generate the steps
		int[] path = new int[pathlength];
		// the corresponding positions
		int[][] pospath = new int[pathlength + 1][2];
		for (int i = 0; i < pospath.length; i++) {
			pospath[i][0] = 0;
			pospath[i][1] = 0;
		}
		// and to get out of a trap the list of useful directions the we could
		// used after the n step
		int[][] allowedSet = new int[pathlength][4];
		// in the beginning everything is useful
		for (int i = 0; i < allowedSet.length; i++) {
			allowedSet[i][0] = -2;
			allowedSet[i][1] = -1;
			allowedSet[i][2] = 1;
			allowedSet[i][3] = 2;
		}

		// generate a step
		int firststep = getnextStep(allowedSet[0]);
		// and save it in both formats
		path[0] = firststep;
		pospath[1][Math.abs(firststep) - 1] += firststep / Math.abs(firststep);
		// now we add the other steps
		for (int s = 1; s < pathlength; s++) {
			// the step s-1 was saved now take the next one
			boolean notsuccesful = true;
			while (notsuccesful) {
				// generate possible step, into an direction we have not used
				// befor
				int step = getnextStep(allowedSet[s]);
				// end write it into the two representations
				path[s] = step;
				pospath[s + 1][0] = pospath[s][0];
				pospath[s + 1][1] = pospath[s][1];
				pospath[s + 1][Math.abs(step) - 1] = pospath[s + 1][Math
						.abs(step) - 1] + step / Math.abs(step);

				// check for mistakes within the memory
				boolean mistake = false;
				for (int i = 1; s - i > -1 && i < m && (!mistake); i++) {
					if ((pospath[s + 1][0] == pospath[s - i][0])
							&& (pospath[s + 1][1] == pospath[s - i][1])) {
						mistake = true;
					}
				} //
				if ((!mistake) || (m < 2)) {
					notsuccesful = false;
				} else {
					// there was a mistake so removed the s-step that we already
					// saved
					for (int i = 0; i < m; i++) {
						pospath[s + 1][0] = 0;
						pospath[s + 1][1] = 0;
					}
					// then we want to remove this possible direction form
					// the possible directions/steps, so that we do not repeat
					// the same mistake
					if (allowedSet[s].length != 1)
						allowedSet[s] = removeFromArray(allowedSet[s], step);
					else {
						// If we land here then this was the last possibility
						// from this point.
						// SO we ended in a dead end. Thereby we need to remove
						// the last step
						// Moreover it is possible that we are trapped in a long
						// tube-like trap/dead end, so we might have to take of
						// multiple steps at once
						// in case of a long dead-end all steps in the dead-end
						// have also only one possible direction.
						int stepstoredoNOW = 1;
						while (allowedSet[s - stepstoredoNOW].length == 1)
							stepstoredoNOW++;

						// we found the entrance to the trap/dead-end so we
						// remember that we do not enter it again
						allowedSet[s - stepstoredoNOW] = removeFromArray(
								allowedSet[s - stepstoredoNOW], path[s
										- stepstoredoNOW]);
						// now we do not enter the trap again we reset the
						// history for all coming steps
						for (int i = s - stepstoredoNOW + 1; i <= s; i++) {
							allowedSet[i] = new int[4];
							allowedSet[i][0] = -2;
							allowedSet[i][1] = -1;
							allowedSet[i][2] = 1;
							allowedSet[i][3] = 2;
							pospath[i + 1][0] = 0;
							pospath[i + 1][0] = 0;
						}
						// move s to the point where we restart.
						// We subtract 1 so that we reenter the while loop the
						// the correct point.
						s = s - stepstoredoNOW - 1;
					}
				}// end of done mistake ?
			}// end of while succesful adding a step
		}// end of loop over s -step of the walks
		return path;
	}

	/**
	 * gives back a uniform-random element of the given array
	 * 
	 * @param allowedset
	 * @return
	 */
	private static int getnextStep(int[] allowedset) {
		double x = Math.random();
		return allowedset[(int) (x * allowedset.length)];
	}

	/*
	 * I remove the elem from the array, thereby we assume that elem is at most
	 * once in the array. If it is not in the list we return a copy of this
	 * array, otherwise we return the array where without this one elem.
	 */
	private static int[] removeFromArray(int[] array, int elem) {
		// look for it
		boolean notfound = true;
		for (int i = 0; (i < array.length) && notfound; i++) {
			notfound = (array[i] == elem);
		}
		int[] rarray;
		if (notfound) {
			// not in list so return of copy of the original list
			rarray = new int[array.length];
			for (int i = 0; (i < array.length); i++) {
				rarray[i] = array[i];
			}
		} else {
			rarray = new int[array.length - 1];
			try {
				int j = 0;
				for (int i = 0; (i < array.length); i++) {
					if (array[i] != elem) {
						rarray[j] = array[i];
						j++;
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {// if this occurs then
														// some part of the
														// algorithm failed,
														// So I need to know
														// this
				System.out.println("input error at  " + array.length + "with");
				listausgabe(array);
			}
		}
		return rarray;
	}

	/**
	 * For Debugging: Write out the path of the walk where we write out the
	 * direction of the last step and the coordinates of all used vertices.
	 * 
	 * @param path
	 */
	public static void listausgabe(int[] path) {
		System.out.print("{ ");
		for (int i = 0; i < path.length; i++)
			System.out.print(" Direction of last move" + path[i] + ",  ");
		System.out.println();
		System.out.print(" position of walker: ");
		int px = 0;
		int py = 0;
		System.out.print("( " + px + "," + py + "),");
		for (int i = 0; i < path.length; i++) {
			if (Math.abs(path[i]) == 1)
				px += path[i];
			if (Math.abs(path[i]) == 2)
				py += path[i] / 2;
			System.out.print("( " + px + "," + py + "),");
			System.out.println();
		}
	}

	/**
	 * We use the following to scale the walk that we draw. If the walk sticked
	 * all the time close to the origin we can zoom in, if the walk wandered at
	 * same time very fare away we have to draw each step as a small line... *
	 * 
	 * @param path
	 *            the sequence of movements in which the walk moved.
	 * @return the maximum difference distance of the walk from the origin (in
	 *         sup norm)
	 */
	public static int getMaximalMovement(int[] path) {
		int tmpmax = 0;
		int[] tmppositon = { 0, 0 };
		for (int i = 0; i < path.length; i++) {
			tmppositon[Math.abs(path[i]) - 1] += path[i] / Math.abs(path[i]);

			if (Math.abs(tmppositon[0]) > tmpmax)
				tmpmax = Math.abs(tmppositon[0]);
			if (Math.abs(tmppositon[1]) > tmpmax)
				tmpmax = Math.abs(tmppositon[1]);
		}
		return tmpmax;
	}
}
