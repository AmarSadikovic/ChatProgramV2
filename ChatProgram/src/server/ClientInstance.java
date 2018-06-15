package server;

import message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * En tråd som håller reda på en klient. Tar emot klientens skickade data och
 * behandlar den.
 *
 * @author Kristofer Svensson, Amar Sadikovic
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
        } catch (Exception e) {
        }
        checkQueuedMessages();
        start();
    }

    public void checkQueuedMessages() {
        HashMap<String, ArrayList<Message>> queueMap = server.getQueuedMessages();
        if (queueMap.containsKey(clientName)) {
            ArrayList<String> client = new ArrayList<>();
            client.add(clientName);
            writeMessage(new Message(clientName, "Welcome back. You received one or more direct messages while you were offline:\n", client, 1));
            ArrayList<Message> tempQueue = queueMap.get(clientName);
            for (Message m : tempQueue) {
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
        server.broadcastDisconnectedClient(clientName);
    }

    /**
     * Skickar ett meddelande till den klient som är bunden till tråden.
     *
     * @param msg Meddelandet som ska skickas.
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
