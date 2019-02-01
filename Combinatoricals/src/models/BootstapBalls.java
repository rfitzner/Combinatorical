package models;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

import drawer.DrawBootstapBalls;
import drawer.Outputapplet;
import main.MainFrame;

/**
 * begin 7.3.2012 14:40 end of this 7.3.2012 16:50
 * 
 * @author rfitzner
 * 
 */
@SuppressWarnings("serial")
public class BootstapBalls extends AbstractMathModel {

	public ArrayList<Ball> balls;
	public int nrOfInitialballs;
	public double initialradius;

	public BootstapBalls(int nr, double rad) {
		balls = new ArrayList<Ball>();
		initialradius = rad;
		nrOfInitialballs = nr;
		for (int i = 0; i < nr; i++) {
			double x;
			double y;
			do {
				x = 2 * Math.random() - 1;
				y = 2 * Math.random() - 1;
			} while (x * x + y * y > 1);
			balls.add(new Ball(x, y, rad));
		}
	}

	@Override
	public Outputapplet createDrawer(MainFrame fr) {
		return new DrawBootstapBalls(fr, this, true);
	}

	public class Ball implements java.io.Serializable {
		public double r, x, y;

		public Ball(double x, double y, double radius) {
			this.x = x;
			this.y = y;
			this.r = radius;
		}

		public boolean equals(Ball other) {
			if ((x == other.x) && (y == other.y))
				return true;
			return false;
		}

		public boolean overlaps(Ball other) {
			if ((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y) < (r + other.r)
					* (r + other.r))
				return true;
			return false;
		}

		/**
		 * creates the smallest ball that completely covers both balls
		 * 
		 * @param other
		 */
		public Ball createCommonBall(Ball other) {
			double centerDistance = Math.sqrt((x - other.x) * (x - other.x)
					+ (y - other.y) * (y - other.y));
			if (centerDistance < Math.abs(r - other.r)) {
				// on the completely contained in the other
				if (r > other.r)
					return this.clone();
				else
					return other.clone();
			} else {
				double newRadius = (r + other.r + centerDistance) / 2;
				double newX = other.x * other.r / (r + other.r) + x * r
						/ (r + other.r);
				double newY = other.y * other.r / (r + other.r) + y * r
						/ (r + other.r);
				return new Ball(newX, newY, newRadius);
			}
		}

		public Ball clone() {
			return new Ball(x, y, r);
		}
	}

	public static void drawThickOval(Graphics2D g, double x, double y,
			double dx, double dy, Color c) {
		g.setColor(c);
		System.out.println("draw ball at (" + (int) Math.round(x - dx) + ","
				+ (int) Math.round(y - dy) + " with radi "
				+ (int) Math.round(dx / 2) + " " + (int) Math.round(dy / 2));
		g.fillOval((int) Math.round(x - dx), (int) Math.round(y - dy),
				(int) Math.round(dx * 2), (int) Math.round(dy * 2));
	}

	public boolean unitOne() {
		for (int i = 0; i < balls.size() - 1; i++) {
			Ball b = balls.get(i);
			for (int j = i + 1; j < balls.size(); j++) {
				if (b.overlaps(balls.get(j))) {
					Ball o = balls.get(j);
					Ball n = b.createCommonBall(o);
					balls.remove(j);
					balls.remove(i);
					balls.add(n);
					return true;
				}
			}
		}
		return false;
	}

	public int unitOne(int begin) {
		if (begin > balls.size() - 1)
			begin = 0;
		for (int i = 0; i < balls.size() - 1; i++) {
			int p = (i + begin) % balls.size();
			Ball b = balls.get(p);
			for (int j = i + 1; j < balls.size(); j++) {
				int q = (j + begin) % balls.size();
				if (b.overlaps(balls.get(q))) {
					Ball o = balls.get(q);
					Ball n = b.createCommonBall(o);
					if (p < q) {
						balls.remove(q);
						balls.remove(p);
					} else {
						balls.remove(p);
						balls.remove(q);
					}
					balls.add(n);
					return p;
				}
			}
		}
		return -1;
	}
}
