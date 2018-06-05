package server;

import message.Message;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
        File dir = new File ("tmp/test");
        dir.mkdirs();
        File tmp = new File (dir, "MyLogFile.log");
        try {
            tmp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public void addToLog(String event){
        Logger logger = Logger.getLogger("MyLog");
        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
//            fh = new FileHandler("C:/tempLOGCHAT/MyLogFile.log", true);
            fh = new FileHandler("tmp/test//MyLogFile.log", true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            // the following statement is used to log any messages
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
    public synchronized void broadcastMessage(Message msg) { //synchronized?

//        ArrayList<String> recipients = msg.getRecipients();
//        String sender = recipients.get(recipients.size()-1);
        if (msg.getType()==1){

        addToLog(msg.getMsg());
        } else if (msg.getType()==2){
//            System.out.println(msg.getImage().getDescription());
            addToLog("User sent an image file: " + msg.getImage().getDescription());
        }
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
        addToLog(clientName + " disconnected.");
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
