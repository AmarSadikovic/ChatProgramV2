package message;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Klassen Message representerar ett meddelande i chattprogrammet. Den kan hålla strängar, ImageIcon-objekt, ArrayLists och integers.
 * Varje instans av klassen har en "msgType" som visar vilken typ av meddelande det rör sig om. Klienter och servern vet då vad som ska
 * göras med meddelandet.
 * @author Kristofer Svensson, Amar Sadikovic
 *
 */
public class Message implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private String message, userName;
	private ArrayList<String> recipients, clientList;
	public static final int TEXT=1, PICTURE=2, CLIENTNAMELIST=3;
	private int msgType;
	private ImageIcon image;
	

	/**
	 * Standard-konstruktorn för vanliga textmeddelanden.
	 * @param message Det textmeddelande som ska skickas.
	 */
	public Message (String userName, String message, ArrayList<String> recipients, int operation){
		this.userName = userName;
		this.message = message;
		this.recipients = recipients;
		this.msgType=operation;
	}

    /**
     * Denna konstruktorn används när ett bildmeddelande skickas.
     * @param image Bilden som ska skickas.
     */
    public Message (ImageIcon image, ArrayList<String> recipients){
        this.image=image;
        this.recipients=recipients;
        msgType=2;
    }
	
	/**
	 * Denna konstruktorn används endast för att skicka en lista av anslutna klienter, från servern till klienterna.
	 * @param clientList En XXXX av serveranslutna klienter.
	 */
	public Message (ArrayList<String> clientList){
		this.clientList=clientList;
		msgType=3;
	}

	public String getMsg(){
		return message;
	}
	
	public ImageIcon getImage(){
		return this.image;
	}

    public ArrayList<String> getRecipients(){
        return recipients;
    }
	
	public ArrayList<String> getClientList(){
		return clientList;
	}
	
	public int getType(){
		return msgType;
	}

	public String getUserName() {
		return userName;
	}
}
