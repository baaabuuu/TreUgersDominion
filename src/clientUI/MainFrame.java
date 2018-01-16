package clientUI;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {
	
	static MainFrame mainFrame;
	
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		// Add title, icon, set resize and exit on close.
		setTitle("Dominion: The blatant ripoff");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/clientUI/IconImage.png")));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void attemptConnect(){
		
        //consumer = new Cl_Consumer(mainFrame, transmit);
        //consumer.start();
        
        //login = new Cl_Login(mainFrame, transmit);
        
		//G_BG.remove(server);
		//G_BG.add(login, BorderLayout.CENTER);
		//mainFrame.setContentPane(G_BG);
		// Set focus to userField.
		//login.userField.requestFocus();
	}

}

