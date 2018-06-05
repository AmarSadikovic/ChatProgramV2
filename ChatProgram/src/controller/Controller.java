package controller;

import client.Client;
import client.ClientGUI;
import message.Message;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Set;

public class Controller {

	private Client client;
	private ClientGUI clientGUI;
	private JFrame frame;
	private Calendar cal;
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    private Message message;

	public Controller(Client client) {
		this.client = client;
		clientGUI = new ClientGUI(this);
	}

    /**
     * Målar upp GUI:t för klienten vid programstart. Används även senare
     * för att uppdatera titeln på fönstret.
     *
     */
	public void showClientGUI() {
		frame = new JFrame("Chat - Not connected.");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(clientGUI);
		frame.pack();
		frame.setVisible(true);
//		frame.setTitle("TEST");
	}

    public void showMessageInClientGUI (String msg){
        clientGUI.showMessage(msg);
    }


    /**
     * Refreshar listan av användare i Client-GUI:t. Denna metod körs när en
     * klient connectar eller disconnectar.
     *
     * @param clientListFromServer
     *            En arraylist som håller strängar med anslutna klienters
     *            användarnamn.
     */
    public void updateClientList(ArrayList<String> clientListFromServer) {
		System.out.println("Client:" + client.getUserName() + " User list following:");
		for (String client: clientListFromServer) {
			System.out.println("Client: " + client);
		}
		clientGUI.updateClientList(clientListFromServer);
//        clientGUI.clearClientList();

//        for (int i = 0; i < clientListFromServer.size(); i++) {
//            clientGUI.addToClientList(clientListFromServer.get(i));
//        }
    }

    /**
     * Skapar och skickar ett ImageIcon-objekt.
     *
     * filepath
     *            Sökvägen till den fil som valdes i klientens GUI.
     */
    public void makeImage(String filepath, String recipients) {
		File file = new File (filepath);
		String fileName = file.getName();
		ImageIcon image = new ImageIcon(filepath);
		Image imagefile = image.getImage();
		imagefile = imagefile.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
		image = new ImageIcon(imagefile, fileName);
        ArrayList<String> recipientsList = new ArrayList<>();
        if (recipients.equals("")){
            recipientsList.add("all");
        } else {
            String[] parts = recipients.split(";");
            Collections.addAll(recipientsList, parts);
            recipientsList.add(client.getUserName());
        }
        client.sendMessage(new Message(image, recipientsList));
    }

    public void showImageInClientGUI (ImageIcon image){
        clientGUI.showImage(image);
    }

    /**
     * Lägger in namnen som skrevs in i mottagarboxen i en arraylist och exekverar constructMessage.
     *
     * @param msg Textmeddelandet som skrevs in.
     * @param recipients Namnen på de mottagare som skrevs in. String-format.
     */
	public void defineRecipients(String msg, String recipients) {

        ArrayList<String> recipientsList = new ArrayList<>();
        if (recipients.equals("")){
            recipientsList.add("all");
			/*Vad händer om någon heter "all"*/
        } else {
            String[] parts = recipients.split(";");
            Collections.addAll(recipientsList, parts);
            recipientsList.add(client.getUserName());
        }
        constructMessage(msg, recipientsList);
	}

    /*
     * Skapar ett korrekt formaterat Message-objekt, komplett med tidsstämpel, namn på personen som skrev meddelandet,
     * och en lista över de klienter som ska ta emot meddelandet.
     *
     * @param msg Textmeddelandet som skrevs in.
     * @param recipients Namnen på de mottagare som skrevs in. ArrayList-format.
     */
	public void constructMessage(String msg, ArrayList<String> recipients) {
		String res;
		cal = Calendar.getInstance();
		res = "[" + sdf.format(cal.getTime()) + "] " + "<"+client.getUserName()+"> "+ msg+"\n";
		client.sendMessage(new Message(res, recipients, 1));
	}

    /**
     * Ansluter klienten till servern.
     * @param userName Klientens valda användarnamn.
     */
	public void connect(String userName) {
		client.setUserName(userName);
		frame.setTitle("Chat - Connected as " + userName);
        client.connect((String) JOptionPane.showInputDialog(frame,
                "Enter server IP",
                "Server IP\n", JOptionPane.OK_CANCEL_OPTION, null,
                null, "127.0.0.1"));
	}

    /**
     * Disconnectar klienten och ser till att sockets och strömmar stängs.
     */
	public void disconnect() {
		client.disconnect();
		frame.setTitle("Chat - Not connected.");
	}

}
