package server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * En klass för att måla upp serverns GUI.
 * 
 * @author Kristofer Svensson, Amar Sadikovic
 *
 */
public class ServerGUI extends JPanel implements ActionListener {


	private Server server;
	private JLabel lblServer = new JLabel("Server up and running");

	public ServerGUI(Server server) {

		this.server = server;
		// Storleksinställningar osv.
		setPreferredSize(new Dimension(300, 100));
		setLayout(new BorderLayout());
		add(lblServer, BorderLayout.CENTER);

	}

	public void actionPerformed(ActionEvent e) {
//		String res = sdf.format(cal.getTime()) + " " + taInput.getText();
//		if (e.getSource() == btnServerMessage) {
//			taInput.setText(null);
//		}
	}
}
