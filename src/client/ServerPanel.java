package client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.Color;

// JPanel containing needed code for an address/socket selection screen.
public class ServerPanel extends JPanel implements ActionListener{
	// JPanel contains following items:
	private JTextField serverField, socketField;
	private JLabel lblServer, lblSocket, lblError;
	private JButton btnConnect, btnExit;
	// The JPanel contains a reference to the main Frame.
	private MainFrame main;
	
	public ServerPanel(MainFrame main) {
		this.main = main;
		
		// Contains no layout and has no background.
		setLayout(null);
		setOpaque(false);
		
		// Buttons.
		btnConnect = new JButton("Connect");				// Connect button.
		btnConnect.setBounds(540, 370, 90, 25);				// Placement and size.
		btnConnect.setForeground(Color.white);				// Text is white.
        btnConnect.setFocusPainted(false);					// Removes the painted box when in focus.
        btnConnect.setBackground(new Color(219, 142, 27));	// Set background color to an orange.
        btnConnect.setFont(new Font("Tahoma", Font.BOLD, 12)); // Sets font.
        btnConnect.addActionListener(new ActionListener() {	// Adds actionListener containing the following...
            public void actionPerformed(ActionEvent e) {
            	
            }
        });
		add(btnConnect); // Add to JPanel.
		
		btnExit = new JButton("Exit");
		btnExit.setBounds(650, 370, 90, 25);
		btnExit.setForeground(Color.white);
        btnExit.setFocusPainted(false);
        btnExit.setBackground(new Color(219, 142, 27));
        btnExit.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	// Close game when pressed.
                System.exit(0);
            }
        });
		add(btnExit);
		
		// Labels.
		lblServer = new JLabel("IP-Address");
		lblServer.setForeground(Color.white);
		lblServer.setBounds(540, 300, 65, 14);
		add(lblServer);
		
		lblSocket = new JLabel("Socket");
		lblSocket.setForeground(Color.white);
		lblSocket.setBounds(540, 335, 65, 14);
		add(lblSocket);
		
		lblError = new JLabel(" ");
		lblError.setForeground(Color.RED);
		lblError.setBounds(540, 275, 200, 14);
		add(lblError);
		
		// Text fields.
		serverField = new JTextField(20);
		serverField.setBounds(610, 300, 130, 20);
		serverField.setText("127.0.0.1");
		// On enter press, sets focus to socket field.
		serverField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	socketField.requestFocus();
            }
        });
		add(serverField);
		
		socketField = new JTextField(20);
		socketField.setBounds(610, 335, 130, 20);
		socketField.setText("8181");
		// On enter press, do the same as connect button.
		socketField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            }
        });
		add(socketField);
	}
	// Required to be there by ActionListener implement.
	public void actionPerformed(ActionEvent e) {}
	private void attemptConnection(){
		if(serverField.getText().matches("[0-9.]+") && serverField.getText().length() < 17
    			&& socketField.getText().matches("[0-9]+") && socketField.getText().length() < 5){
    		
    		main.host = serverField.getText();
    		main.port = Integer.parseInt(socketField.getText());
    		
    		main.attemptConnect();
    		
    		
    		
    	// Error for wrong characters.
    	}else if(serverField.getText().length() < 17 && socketField.getText().length() < 5){
    		lblError.setText("Numbers and dots only!");
    	// Error for too many characters.
    	}else{
    		lblError.setText("A field is too long!");
    	}
	}
}
