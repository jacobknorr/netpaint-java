/**
 * Draws rectangles based on information passed to it from the superclass
 * @author Shaion Moghimi 
 **/
package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;

public class Rectangle extends PaintObject implements Serializable {

	private Point point1;
	private Point point2;
	private Color color;

	/*
	 * Constructs a rectangle from a superclass of paintObject
	 */
	public Rectangle(Color c, Point p, Point p2) {
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
		g.fillRect(Math.min(point1.x, point2.x), Math.min(point1.y, point2.y), Math.abs(point2.x - point1.x),
				Math.abs(point2.y - point1.y));
	}

}
