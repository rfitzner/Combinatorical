package utils;

import java.awt.Color;
import java.awt.Graphics2D;

public class SmallTools {
	public static int giveInteger(String input) {
		int number = 0;
		try {
			number = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			return Integer.MAX_VALUE;
		}
		return number;
	}

	public static double giveDouble(String input) {
		double number = 0;
		try {
			number = Double.parseDouble(input);
		} catch (NumberFormatException e) {
			number = Double.MAX_VALUE;
		}
		return number;
	}

	public static void drawThickBall(Graphics2D g, double x, double y,
			int thickness, Color c) {
		g.setColor(c);
		g.fillOval((int) x, (int) y, thickness, thickness);
	}

	public static void drawThickOval(Graphics2D g, double x, double y,
			double dx, double dy, Color c) {
		g.setColor(c);
		g.fillOval((int) Math.round(x - dx), (int) Math.round(y - dy),
				(int) Math.round(dx * 2), (int) Math.round(dy * 2));
	}

	/**
	 * Drawing a line with a given size
	 * 
	 * @param g
	 *            the graphic element that we draw into
	 * @param x1
	 *            -coordinates
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param thickness
	 *            -thickness of the line
	 * @param c
	 *            color that it will be drawn
	 */
	public static void drawThickLine(Graphics2D g, double x1, double y1,
			double x2, double y2, int thickness, Color c) {
		// The thick line is in fact a filled polygon
		g.setColor(c);
		int dX = (int) (x2 - x1);
		int dY = (int) (y2 - y1);
		// line length
		double lineLength = Math.sqrt(dX * dX + dY * dY);

		double scale = (double) (thickness) / (2 * lineLength);

		// The x,y increments from an endpoint needed to create a rectangle...
		double ddx = -scale * (double) dY;
		double ddy = scale * (double) dX;
		ddx += (ddx > 0) ? 0.5 : -0.5;
		ddy += (ddy > 0) ? 0.5 : -0.5;
		int dx = (int) ddx;
		int dy = (int) ddy;

		// Now we can compute the corner points...
		int xPoints[] = new int[4];
		int yPoints[] = new int[4];

		xPoints[0] = (int) x1 + dx;
		yPoints[0] = (int) y1 + dy;
		xPoints[1] = (int) x1 - dx;
		yPoints[1] = (int) y1 - dy;
		xPoints[2] = (int) x2 - dx;
		yPoints[2] = (int) y2 - dy;
		xPoints[3] = (int) x2 + dx;
		yPoints[3] = (int) y2 + dy;

		g.fillPolygon(xPoints, yPoints, 4);
	}

}
