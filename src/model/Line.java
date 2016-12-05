
package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;

/**
 * Draws lines based on information passed to it from the superclass
 * 
 * @author Shaion Moghimi
 **/
public class Line extends PaintObject implements Serializable {

	private Point point1;
	private Point point2;
	private Color color;

	/*
	 * Constructs a line based on the super class
	 */
	public Line(Color c, Point p, Point p2) {
		super(c, p, p2);
		point1 = super.getPointOne();
		point2 = super.getPointTwo();
		color = super.getColor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.PaintObject#draw(java.awt.Graphics)
	 */
	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.drawLine(point1.x, point1.y, point2.x, point2.y);
	}

}
