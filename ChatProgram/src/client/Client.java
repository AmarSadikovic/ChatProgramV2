package client;

import controller.Controller;
import message.Message;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Klassen Client representerar en klient i chattprogrammet och innehåller
 * metoder för att connect och disconnect från en server, skapa
 * ImageIcon-objekt av en filsökväg, skicka information till klientens GUI
 * samt ta emot och skicka objekt via objektströmmar.
 * 
 * @author Kristofer Svensson, Amar Sadikovic, Alessandro Crnomat
 *
 */
public class Client {

	private String userName;
	private Message msg;
	private Controller controller;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Socket socket;
	private JFrame frame;
	private int port;

	public Client() {
		controller = new Controller(this);
//		this.controller = controller;
		controller.showClientGUI();
		this.port = 1337;

	}

	/**
	 * Ansluter till servern.
	 */
	public void connect(String ip) {
		try {
			socket = new Socket(ip, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
//			System.out.println("Strömmar clientside");
			oos.writeUTF(userName);
			System.out.println("Namn skickat till servern: " + userName);
//			oos.writeObject(userName);
            oos.flush();
            new messageListener();
		} catch (Exception e) {
		}
	}

	/**
	 * Stänger alla strömmar och socket.
	 */
	public void disconnect() {
		try {
			if (socket != null) {
				socket.close();
			}
			if (oos != null) {
				oos.close();
			}
			if (ois != null) {
				ois.close();
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Skickar ett Message-objekt till Servern.
	 * 
	 * @param msg
	 *            Meddelandet som ska skickas.
	 */
	public void sendMessage(Message msg) {
		try {
			oos.writeObject(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lyssnar efter objekt från servern. Varje Message har ett typvärde som
	 * sattes när själva meddelandet skapades. Denna Type avgör hur
	 * Message-objektet behandlas.
	 * 
	 * @author Kristofer Svensson
	 *
	 */
	private class messageListener extends Thread {

		public messageListener() {
			start();
		}

		public void run() {
			try {
				while (true) {
					Message message = (Message) ois.readObject();
					switch (message.getType()) {
						case 1: //vanligt textmeddelande
							System.out.println("Message mottaget från server: " + message.getMsg());
							controller.showMessageInClientGUI(message.getMsg());
							break;
						case 3: //Uppdatering av clientList
							System.out.println("Fick clientlist från server");
							//TODO: Jag tror att strömmen använder gamla clientlist trots att en ny kommer in. testa den där command
							controller.updateClientList(message.getClientList());
							break;
					default:
						System.out.println("should not happen");
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("KUNDE INTE LÄSA OBJEKT FRÅN SERVERN");
			}
		}
	}


	/**
	 * Returnerar klientens användarnamn.
	 * 
	 * @return username.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Ändrar klientens användarnamn.
	 * 
	 * @param userName
	 *            Det nya klientnamnet som ska användas.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public static void main(String[] args) {
		Client client = new Client();
	}

}
