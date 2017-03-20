package server;

/**
 * Allows multiple clients to read and write a Vector of
 * PaintObjects every time a client writes a PaintObject to this server.
 *
 * CS 335
 * Fall 2016
 * 
 * @author Jacob Knorr
 * @author Shaion Moghimi
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import model.PaintObject;

public class Server {

	public static int PORT = 4000;

	static Vector<PaintObject> vec = null;
	private static ServerSocket server;
	private static List<ObjectOutputStream> clients = Collections.synchronizedList(new ArrayList<>());

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		server = new ServerSocket(PORT);

		while (true) {
			Socket client = server.accept();

			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			clients.add(out); 
			ClientHandler handler = new ClientHandler(in, clients);
			handler.start();

		}
	}
}

/*
 * This writes all of the objects to the clients
 */
class ClientHandler extends Thread {

	private ObjectInputStream input;
	private List<ObjectOutputStream> clients;

	public ClientHandler(ObjectInputStream input, List<ObjectOutputStream> clients) {
		this.input = input;
		this.clients = clients;
		if (Server.vec != null)
			writeDrawingToClients(Server.vec);
	}

	@Override
	public void run() {
		while (true) {

			Vector<PaintObject> dr = null;
			try {
				dr = (Vector<PaintObject>) input.readObject();
			} catch (IOException e) {
				this.cleanUp();
				return;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				this.cleanUp();
				return;
			}

			if (Server.vec == null || Server.vec.size() < dr.size()) {
				Server.vec = dr;
			}
			this.writeDrawingToClients(Server.vec);
		}
	}

	private void writeDrawingToClients(Vector<PaintObject> drawing) {
		synchronized (clients) {
			ObjectOutputStream to_be_removed = null;
			for (ObjectOutputStream client : clients) {
				try {
					client.writeObject(drawing);
					client.reset();
					
				} catch (IOException e) {
					to_be_removed = client;
				}
			}
			clients.remove(to_be_removed);
		}
	}

	private void cleanUp() {
		try {
			this.input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

