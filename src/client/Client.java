package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import model.Line;
import model.Oval;
import model.PaintImage;
import model.PaintObject;
import model.Rectangle;

/**
 * A JPanel GUI for Netpaint that has all paint objects drawn on it. A JPanel
 * exists in this JFrame that will draw this list of paint objects.
 * 
 * 
 * @author Rick Mercer
 * @author Shaion Moghimi
 * @author Jacob Knorr
 */

public class Client extends JFrame {

	private static final String ADDRESS = "localhost";
	private static final int PORT = 4000;

	private Point point1;
	private Point point2;
	private String currentPaintOb;
	private JRadioButton rect, oval, line, pict;
	private Color currentColor = Color.black;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
	private boolean drawing;

	public static void main(String[] args) {
		new Client();
	}

	private DrawingPanel drawingPanel;
	private static Vector<PaintObject> allPaintObjects;
	private static Vector<PaintObject> ghost = new Vector<PaintObject>();

	/*
	 * Constructor for the GUI calls a method which sets up the main components
	 * and then tries to connect to the server. Something here needs to be
	 * changed because it doesnt load the existing objects into the drawing
	 * panel.
	 */
	public Client() {
		setupModelAndView();
		try {
			socket = new Socket(ADDRESS, PORT);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			out.writeObject(allPaintObjects);
			allPaintObjects = (Vector<PaintObject>) in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			this.cleanUpAndQuit("Couldn't connect to the server");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		ServerListener serverListener = new ServerListener();
		serverListener.start();

	}

	/*
	 * Sets up the important parts of the frame such as the drawing panel and
	 * decorates it with a JScrollPane. Also includes the button cluster and the
	 * color chooser.
	 */
	public void setupModelAndView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		drawing = false;
		setLocation(20, 20);
		setSize(1120, 800); // to be changed
		setLayout(new BorderLayout());
		this.setBackground(new Color(143, 229, 230));

		drawingPanel = new DrawingPanel();
		drawingPanel.setPreferredSize(new Dimension(2000, 1500));
		drawingPanel.setLocation(10, 10);
		drawingPanel.addMouseListener(new mListener());
		drawingPanel.addMouseMotionListener(new mListener());

		JScrollPane jsc = new JScrollPane(drawingPanel);
		add(jsc, BorderLayout.CENTER);
		JPanel bottom = new JPanel();
		bottom.setSize(1100, 60); 
		bottom.setLocation(10, 640); 
		rect = new JRadioButton("Rectangle");
		oval = new JRadioButton("Oval");
		line = new JRadioButton("Line");
		pict = new JRadioButton("Image");
		ButtonGroup group = new ButtonGroup();

		rect.setActionCommand("rect");
		rect.addActionListener(new RadioListener());

		oval.setActionCommand("oval");
		oval.addActionListener(new RadioListener());

		line.setActionCommand("line");
		line.addActionListener(new RadioListener());

		pict.setActionCommand("pict");
		pict.addActionListener(new RadioListener());

		group.add(pict);
		group.add(line);
		group.add(rect);
		group.add(oval);

		JPanel buttons = new JPanel();
		buttons.setLocation(10, 10);
		buttons.add(pict);
		buttons.add(line);
		buttons.add(rect);
		buttons.add(oval);

		bottom.add(buttons);

		JColorChooser colorChooser = new JColorChooser();
		colorChooser.setColor(Color.black);
		JButton colorChoose = new JButton("Choose a color.");
		colorChoose.setLocation(10, 620);
		colorChoose.addActionListener(new colorListener());
		bottom.add(colorChoose);

		add(bottom, BorderLayout.SOUTH);

		allPaintObjects = new Vector<>();

		this.setVisible(true);
	}

	/*
	 * ServerListener waits for the server to write the a vector of PaintObjects
	 * so that it can paint them. Whenever the server writes a paint object it
	 * will call repaint so that it can add it to the panel.
	 */
	private class ServerListener extends Thread {
		@Override
		public void run() {
			try {
				while (true) {
					allPaintObjects = (Vector<PaintObject>) in.readObject();
					repaint();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	private void cleanUpAndQuit(String message) {
		try {
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is where all the drawing goes .
	 */
	class DrawingPanel extends JPanel {

		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			super.paintComponent(g2);
			g2.setColor(Color.white);
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());

			for (PaintObject ob : allPaintObjects) {
				ob.draw(g);
			}
			for (PaintObject ob : ghost) {
				ob.draw(g);
			}
			ghost.removeAllElements();

		}
	}

	/*
	 * This class is used for listening to the color chooser, when a color is
	 * chosen it sets the current color to that color so that objects drawn are
	 * the right color.
	 */
	private class colorListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Color c = JColorChooser.showDialog(null, "Choose a Color", currentColor);
			if (c != null)
				currentColor = c;
		}

	}

	/*
	 * This class listens to the radio buttons and sets a string for whenever a
	 * radio button is pressed so that the mouseListener can know what type of
	 * shape to draw.
	 */
	private class RadioListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getActionCommand().equals("rect"))
				currentPaintOb = "rect";
			else if (arg0.getActionCommand().equals("oval"))
				currentPaintOb = "oval";
			else if (arg0.getActionCommand().equals("line"))
				currentPaintOb = "line";
			else if (arg0.getActionCommand().equals("pict"))
				currentPaintOb = "pict";
		}

	}

	/*
	 * this class listens to the mouse and determines when it should be drawing.
	 * If the mouse is clicked it toggles the drawing boolean and starts to draw
	 * a ghost image. When the mouse is clicked again it draws the final picture
	 * and writes it to the server.
	 */
	private class mListener implements MouseListener, MouseMotionListener {

		private boolean flag = false;

		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (!drawing)
				point1 = arg0.getPoint();
			else {
				if (flag) {
					point2 = arg0.getPoint();
					if (point1.x > point2.x) {
						Point temp = point1;
						point1 = point2;
						point2 = temp;
					}
					if (currentPaintOb.equals("rect"))
						allPaintObjects.add(new Rectangle(currentColor, point1, point2));
					if (currentPaintOb.equals("oval"))
						allPaintObjects.add(new Oval(currentColor, point1, point2));
					if (currentPaintOb.equals("line"))
						allPaintObjects.add(new Line(currentColor, point1, point2));
					if (currentPaintOb.equals("pict"))
						allPaintObjects.add(new PaintImage(currentColor, point1, point2));
					point1 = null;
					point2 = null;

					try {
						out.writeObject(allPaintObjects);
						out.reset();
					} catch (IOException e) {
						e.printStackTrace();
					}
					repaint();
				}
			}
			drawing = !drawing;

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			flag = false;
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (drawing) {
				if (currentPaintOb.equals("rect"))
					ghost.add(new Rectangle(currentColor, point1, e.getPoint()));
				if (currentPaintOb.equals("oval"))
					ghost.add(new Oval(currentColor, point1, e.getPoint()));
				if (currentPaintOb.equals("line"))
					ghost.add(new Line(currentColor, point1, e.getPoint()));
				if (currentPaintOb.equals("pict"))
					ghost.add(new PaintImage(currentColor, point1, e.getPoint()));
				repaint();
			}

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			flag = true;
		}

	}
}
