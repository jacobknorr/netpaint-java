
package model;

/**
 * Abstract class used by the 4 subclasses, oval, rectangle, line, and paintImage
 * 
 * @author Shaion Moghimi
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;

public abstract class PaintObject implements Serializable {

	private Point pointOne;
	private Point pointTwo;
	private Color color;

	/*
	 * Constructor takes a color, and two points and stores them in instance
	 * variables
	 */
	public PaintObject(Color c, Point p, Point p2) {
		color = c;
		pointOne = p;
		pointTwo = p2;
	}

	/*
	 * Abstract method used by each sub class to draw
	 */
	public abstract void draw(Graphics g);

	/*
	 * returns the first point
	 */
	public Point getPointOne() {
		return pointOne;
	}

	/*
	 * returns the second point
	 */
	public Point getPointTwo() {
		return pointTwo;
	}

	/*
	 * returns the color
	 */
	public Color getColor() {
		return color;
	}
}
