package client;

import controller.Controller;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Set;

/**
 * En klass för att måla upp och redigera klientens användargränssnitt.
 * 
 * @author Kristofer Svensson, Amar Sadikovic, Alessandro Crnomat
 *
 */
public class ClientGUI extends JPanel implements ActionListener, ListSelectionListener {

	private JPanel pnlCenter = new JPanel(new BorderLayout());
	private JPanel pnlCenterEast = new JPanel(new BorderLayout());
	private JPanel pnlSouth = new JPanel(new BorderLayout());
	private JPanel pnlSouthEast = new JPanel(new GridLayout(2, 2));
	private JList clientList;
	private DefaultListModel clientListElements;
	private JButton btnSkicka = new JButton("Send message");
	private JButton btnBild = new JButton("Send image");
	private JButton btnDisconnect = new JButton("Disconnect");
	private JButton btnConnect = new JButton("Connect");
	private JFrame frame;
	private JLabel lblRight = new JLabel("Right");
	private JFileChooser fc = new JFileChooser();
	private FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF & PNG Images", "jpg", "gif", "png");
	private JTextPane tpOutput = new JTextPane();
	private StyledDocument doc = tpOutput.getStyledDocument();
	private JScrollPane spOutput = new JScrollPane(tpOutput);
	private JTextArea taInput = new JTextArea("Input");
	private JTextArea taNameInput = new JTextArea("Pm");
	private JScrollPane spUsers;
	private JScrollPane spInput = new JScrollPane(taInput);
	private JScrollPane spNameInput = new JScrollPane(taNameInput);
	private Controller controller;

	public ClientGUI(Controller controller) {
		fc.setFileFilter(filter);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fc.setCurrentDirectory(new File(System.getProperty("user.home")));
            }
        });
		tpOutput.setText("Chatprogram - skrivet av Kristofer Svensson, Amar Sadikovic och Alessandro Crnomat");
		SimpleAttributeSet keyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWord, Color.RED);
		StyleConstants.setBackground(keyWord, Color.YELLOW);
		StyleConstants.setBold(keyWord, true);
		this.controller = controller;
		/*
		 * Klientlista
		 */
		clientListElements = new DefaultListModel();
		clientList = new JList(clientListElements);
		clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		clientList.setSelectedIndex(0);
		clientList.addListSelectionListener(this);
		clientList.setVisibleRowCount(20);
		spUsers = new JScrollPane(clientList);
		/*
		 * Storleksinställningar osv.
		 */
		spUsers.setPreferredSize(new Dimension(120, 200));
		setPreferredSize(new Dimension(600, 600));
		setLayout(new BorderLayout());
		spOutput.setPreferredSize(new Dimension(600, 50));
		spOutput.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		spInput.setPreferredSize(new Dimension(600, 20));
		spInput.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		spNameInput.setPreferredSize(new Dimension(600, 20));
		spNameInput.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		taInput.setLineWrap(true);
		taInput.setWrapStyleWord(true);
		/*
		 * Alla grafiska element läggs in i respektive paneler.
		 */
		add(pnlCenter, BorderLayout.CENTER);
		add(pnlSouth, BorderLayout.SOUTH);
		pnlSouth.add(pnlSouthEast, BorderLayout.EAST);
		pnlCenter.add(pnlCenterEast, BorderLayout.EAST);
		pnlSouth.add(spInput, BorderLayout.CENTER);
		pnlSouth.add(spNameInput, BorderLayout.SOUTH);
		pnlSouthEast.add(btnSkicka, BorderLayout.EAST);
		pnlSouthEast.add(btnBild, BorderLayout.EAST);
		pnlSouthEast.add(btnConnect, BorderLayout.EAST);
		pnlSouthEast.add(btnDisconnect, BorderLayout.EAST);
		pnlCenter.add(spOutput, BorderLayout.CENTER);
		lblRight.setPreferredSize(new Dimension(185, 600));
		pnlCenterEast.add(spUsers, BorderLayout.EAST);
		/*
		 * Knappar som är inaktiverade från början.
		 */
		btnSkicka.setEnabled(false);
		btnBild.setEnabled(false);
		btnDisconnect.setEnabled(false);
		tpOutput.setEditable(false);
		/*
		 * Listeners.
		 */
		btnSkicka.addActionListener(this);
		btnBild.addActionListener(this);
		btnConnect.addActionListener(this);
		btnDisconnect.addActionListener(this);
		taInput.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					btnSkicka.doClick();
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});
	}

	/**
	 * Målar upp en bild i chattfönstret.
	 * 
	 * @param imageIcon
	 *            ImageIcon-objektet som skall visas.
	 */
	public void showImage(ImageIcon imageIcon) {
		tpOutput.setCaretPosition(tpOutput.getText().length());
		tpOutput.insertIcon(imageIcon);
	}

	public void showMessage(String msg) {
		try {
			doc.insertString(doc.getEndPosition().getOffset(), msg, null);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnSkicka) {
			if (taInput.getText().length() != 0) {
				controller.defineRecipients(taInput.getText(), taNameInput.getText());
			}
			taInput.setText(null);
			//TODO: Flytta caret till första pos, första raden här på något sätt?
		} else if (e.getSource() == btnConnect) {
            controller.connect((String) JOptionPane.showInputDialog(frame,
                    "Enter desired username.",
                    "Name\n", JOptionPane.OK_CANCEL_OPTION, null,
                    null, ""));
            btnSkicka.setEnabled(true);
			btnBild.setEnabled(true);
			btnDisconnect.setEnabled(true);
			btnConnect.setEnabled(false);
		} else if (e.getSource() == btnDisconnect) {
			controller.disconnect();
			btnDisconnect.setEnabled(false);
			btnConnect.setEnabled(true);
		} else if (e.getSource() == btnBild) {
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				//controller.makeImage(file.getPath(), taNameInput.getText());
			}
		}
	}

	/**
	 * Tar bort samtliga klientnamn från listan i klientens GUI.
	 */
	public void clearClientList() {
		clientListElements.clear();
	}

	/**
	 * Tar emot ett användarnamn och lägger till det i listan i klientens GUI.
	 * 
	 * @param //clientName
	 *            Användarnamnet som ska läggas in i listan.
	 */
	public void updateClientList (ArrayList<String> clientList){
		clearClientList();
		for (int i = 0; i<clientList.size(); i++){
			clientListElements.addElement(clientList.get(i));
			System.out.println("Trying to add " + clientList.get(i) + " to name list");
		}
		//do something Jlist
	}
	/*public void addToClientList(String clientName) {
		clientListElements.addElement(clientName);
	}*/

	/*
	 * Nån metod som krävs för listmodel.(non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.
	 * ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
	}
}