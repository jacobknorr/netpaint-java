package model;

/**
 * Draws images of a doge based on information passed to it from the superclass
 * 
 * @author Shaion Moghimi 
 **/
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

public class PaintImage extends PaintObject implements Serializable {

	private Point point1;
	private Point point2;
	private Color color;

	/*
	 * Constructs images based on the superclass
	 */
	public PaintImage(Color c, Point p, Point p2) {
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
		Graphics2D g2 = (Graphics2D) g;
		Image doge = null;
		try {
			doge = ImageIO.read(new File("images/doge.jpeg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		g2.drawImage(doge, Math.min(point1.x, point2.x), Math.min(point1.y, point2.y), Math.abs(point2.x - point1.x),
				Math.abs(point2.y - point1.y), null);
	}

}