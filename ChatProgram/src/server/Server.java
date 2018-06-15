package server;

import message.Message;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
    private Message message;
    private HashMap<String, ArrayList<Message>> queuedMessages;

    public Server(int port) {
        serverGUI = new ServerGUI(this);
        clients = new Clients();
        showServerGUI();
        this.port = port;
        System.out.println("Server is now online.");
        queuedMessages = new HashMap<>();
        ConnectionListener cl = new ConnectionListener();
        File dir = new File("tmp/test");
        dir.mkdirs();
        File tmp = new File(dir, "MyLogFile.log");
        try {
            tmp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cl.start();
    }

    public void showServerGUI() {
        JFrame frame = new JFrame("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(serverGUI);
        frame.pack();
        frame.setVisible(true);
    }

    public void addToLog(String event) {
        Logger logger = Logger.getLogger("MyLog");
        FileHandler fh;
        try {
            fh = new FileHandler("tmp/test//MyLogFile.log", true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.info(event);
            fh.close();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Skickar argumentet Message till alla anslutna klienter.
     *
     * @param msg Meddelandet som ska sändas till alla klienter.
     */
    public synchronized void broadcastMessage(Message msg) { // synchronized?

        if (msg.getType() == 1) {
            addToLog(msg.getMsg());
        } else if (msg.getType() == 2) {
            addToLog(msg.getUserName() + " sent an image file: " + msg.getImage().getDescription());
        }
        if (msg.getRecipients().get(0).equals("all")) {
            for (String key : clients.getKeys()) {
                clients.get(key).writeMessage(msg);
            }
        } else {
            for (String recipient : msg.getRecipients()) {
                if (clients.getKeys().contains(recipient)) {
                    clients.get(recipient).writeMessage(msg);
                } else {
                    queueMessage(msg, recipient);
                    ArrayList<String> sender = new ArrayList<>();
                    sender.add(msg.getUserName());
                    clients.get(msg.getUserName()).writeMessage(new Message(msg.getUserName(), "That user is not online. Your message will be delivered to them as soon as they log in\n", sender, 1));
                }
            }
        }
    }

    public synchronized void broadcastDisconnectedClient(String clientName) {
        clients.removeClient(clientName);
        ArrayList<String> remainingClients = new ArrayList<>();
        remainingClients.addAll(clients.getKeys());
        for (String key : clients.getKeys()) {
            clients.get(key)
                    .writeMessage(new Message(clientName, clientName + " Disconnected.\n", remainingClients, 1));
            clients.get(key).writeMessage(new Message(remainingClients));
        }
        addToLog(clientName + " disconnected.");
    }

    public void queueMessage(Message msg, String client) {
        ArrayList<Message> messageQueue;
        if (queuedMessages.containsKey(client)) {
            messageQueue = queuedMessages.remove(client);
            messageQueue.add(msg);
            queuedMessages.put(client, messageQueue);
        } else {
            messageQueue = new ArrayList<>();
            messageQueue.add(msg);
            queuedMessages.put(client, messageQueue);
        }
    }

    public HashMap<String, ArrayList<Message>> getQueuedMessages() {
        return queuedMessages;
    }

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
            ArrayList<String> connectedClients = new ArrayList<>();
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                    String clientName = ois.readUTF();
                    clients.addClient(new ClientInstance(clientName, clientSocket, ois, oos, Server.this));
                    connectedClients.clear();
                    connectedClients.addAll(clients.getKeys());
                    for (String key : clients.getKeys()) {
                        clients.get(key).writeMessage(
                                new Message(clientName, clientName + " connected. \n", connectedClients, 1));
                        clients.get(key).writeMessage(new Message(connectedClients));
                    }
                    addToLog(clientName + " connected.");
                }
            } catch (Exception e) {
            }
        }

    }

    public static void main(String[] args) {
        Server server = new Server(1337);
    }
}
