package server;

import message.Message;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Klassen Server har metoder och inre klasser som tillåter flera anslutna
 * klienter och mottagande av deras skickade data. Servern kan även skicka data
 * till samtliga anslutna klienter. Servern lagrar trådar för samtliga anslutna
 * klienter i en ArrayList, och klienternas användarnamn i en annan ArrayList.
 *
 * @author Kristofer Svensson, Amar Sadikovic
 */
public class Server {

    private int port;
    private ServerGUI serverGUI;
    private Clients clients;
    //	private ArrayList<ClientInstance> clientList;
//	private ArrayList<String> clientNameList;
    private Message message;

    public Server(int port) {
        serverGUI = new ServerGUI(this);
        clients = new Clients();
        showServerGUI();
        this.port = port;
        System.out.println("Server is now online.");
        ConnectionListener cl = new ConnectionListener();
//		clientList = new ArrayList<ClientInstance>();
//		clientNameList = new ArrayList<String>();
        cl.start();
    }

    public void showServerGUI() {
        JFrame frame = new JFrame("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(serverGUI);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Skickar argumentet Message till alla anslutna klienter.
     *
     * @param msg Meddelandet som ska sändas till alla klienter.
     */
    public synchronized void broadcastMessage(Message msg) { //synchronized?

        if (msg.getRecipients().get(0).equals("all")){
            for (String key : clients.getKeys()){
                System.out.println("Skrev msg till user:" + key);
                clients.get(key).writeMessage(msg);
            }
        }
        else {
            for (String key : clients.getKeys()) {
                for (String rec : msg.getRecipients()) {
                    if (key.equals(rec)) {
                        clients.get(key).writeMessage(msg);
                    }
                }
            }
        }
    }

    public synchronized void broadcastDisconnectedClient(String clientName){
        clients.removeClient(clientName);
        ArrayList<String> remainingClients = new ArrayList<>();
        remainingClients.addAll(clients.getKeys());
        for (String key : clients.getKeys()){
            clients.get(key).writeMessage(new Message(clientName + " Disconnected.\n", remainingClients, 1));
            clients.get(key).writeMessage(new Message(remainingClients));
        }
    }


/**
 * Kollar igenom listan som håller klient-trådar och ser till att listan med
 * användarnamn stämmer överrens.
 *
 * @param userNameToEdit
 * Användarnamnet som ska läggas in eller tas bort från listan.
 */
    /*private void updateClientList(String userNameToEdit) {
        boolean foundClient = false;
		for (int i = 0; i < clientList.size(); i++) {
			if (clientList.get(i).userName.equals(userNameToEdit)) {
				clientNameList.add(userNameToEdit);
				foundClient = true;
			}
		}
		// Om namnet inte fanns i listan för klienttrådar så ska den tas bort
		// från klientnamnlistan.
		if (!foundClient) {
			for (int i = 0; i < clientNameList.size(); i++) {
				if (clientNameList.get(i).equals(userNameToEdit)) {
					clientNameList.remove(i);
					i--;
				}
			}
		}

	}*/

    /**
     * Lyssnar efter nya anslutningar. När en klient ansluter binds den till en
     * socket och en instans av klienten skapas och läggs in i en ArrayList.
     * Klientnamnet läggs in i motsvarande ArrayList som skickas ut till alla
     * anslutna klienter.
     *
     * @author Kristofer Svensson
     */
    private class ConnectionListener extends Thread {
        public void run() {
//            System.out.println("Server is now ready to accept connections.");
            ArrayList<String> connectedClients = new ArrayList<>();
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                    String clientName = ois.readUTF();
//                    System.out.println("Namn mottaget: " + clientName);
                    clients.addClient(new ClientInstance(clientName, clientSocket, ois, oos, Server.this));
                    connectedClients.clear();
                    connectedClients.addAll(clients.getKeys());
                    for (String key: clients.getKeys()){
                        clients.get(key).writeMessage(new Message(clientName + " connected \n", connectedClients, 1));
                        clients.get(key).writeMessage(new Message(connectedClients));
                        System.out.println("From client list: " + key);
                    }
//					System.out.println(cl.userName+" connected");
//                    ArrayList<String> tempToAll = new ArrayList<String>(1);
//                    tempToAll.add("all");
//					broadcastMessage(new Message(cl.userName + " connected.\n", tempToAll));
//					updateClientList(cl.userName);
//					broadcastMessage(new Message(clientNameList, tempToAll));
                }
            } catch (Exception e) {
            }
        }

    }

    public static void main(String[] args) {
        Server server = new Server(1337);
    }
}
