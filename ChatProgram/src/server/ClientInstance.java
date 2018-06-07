package server;

/**
 * Created by Koffe on 2017-10-06.
 */

import message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

/**
 * En tråd som håller reda på en klient. Tar emot klientens skickade data och
 * behandlar den.
 *
 * @author Kristofer Svensson
 */
class ClientInstance extends Thread {

	private Socket socket;
	private String clientName;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Message message;
	private Server server;

	public ClientInstance(String clientName, Socket socket, ObjectInputStream ois, ObjectOutputStream oos,
			Server server) {
		this.socket = socket;
		this.clientName = clientName;
		this.server = server;
		try {
			this.ois = ois;
			this.oos = oos;
			// oos = new ObjectOutputStream(socket.getOutputStream());
			// ois = new ObjectInputStream(socket.getInputStream());
		} catch (Exception e) {
		}
		checkQueuedMessages();
		start();
	}

	public void checkQueuedMessages() {
		HashMap<String, ArrayList<Message>> map = server.getQueuedMessages();
		if (map.containsKey(clientName)) {
			ArrayList<Message> temp = map.get(clientName);
			for(Message m : temp) {
				writeMessage(m);
			}
		}
	}

	public void run() {

		while (!socket.isClosed()) {
			try {
				message = (Message) ois.readObject();
				switch (message.getType()) {
				// case 1 är standard textmeddelande.
				case 1:
					System.out.println("ClientInstance tog emot msg typ 1");
					server.broadcastMessage(message);
					break;
				// case 2 är bildmeddelande.
				case 2:
					server.broadcastMessage(message);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		System.out.println(clientName + " DISCONNECTED");
		server.broadcastDisconnectedClient(clientName);
	}

	/**
	 * Skickar ett meddelande till den klient som är bunden till tråden.
	 *
	 * @param msg
	 *            Meddelandet som ska skickas.
	 */
	public void writeMessage(Message msg) {
		try {
			oos.writeObject(msg);
			oos.flush();
			oos.reset();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getClientName() {
		return clientName;
	}
}
