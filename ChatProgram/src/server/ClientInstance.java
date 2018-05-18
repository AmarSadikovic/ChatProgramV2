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

/**
 * En tråd som håller reda på en klient. Tar emot klientens skickade data
 * och behandlar den.
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

    public ClientInstance(String clientName, Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
        this.socket = socket;
        this.clientName = clientName;
        try {
            this.ois = ois;
            this.oos = oos;
//            oos = new ObjectOutputStream(socket.getOutputStream());
//            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
        }
        start();
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
//        ArrayList<String> tempToAll = new ArrayList<String>(1);
//        tempToAll.add("all");
//        server.broadcastMessage(new Message(userName + " disconnected.\n", tempToAll));
//        updateClientList(userName);
//        broadcastMessage(new Message(clientNameList, tempToAll));
//        interrupt();
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
//            System.out.println("Connected msssssessage skickat: " + msg.getMsg());
            oos.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getClientName() {
        return clientName;
    }
}