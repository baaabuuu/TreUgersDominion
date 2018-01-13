package client;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.RemoteSpace;
import org.jspace.Space;

import log.Log;

public class ConnectionHandler {
	private String userName;
	private String host;
	private int port;
	private String uri;
	private Space clientSpace;
	private Space hostSpace;
	
	private Thread consumer;
	private Thread receiver;
	private Thread connecterDetector;
	
	MainFrame mainFrame;
	
	public ConnectionHandler(int port, String host){
		this.port = port;
		this.host = host;
	}
	public void run() {
		
		connecterDetector = new Thread(new ConnectionDetector(clientSpace, this));
		connecterDetector.start();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Start new main and set size. 
					mainFrame = new MainFrame();
					mainFrame.setVisible(true);
					mainFrame.setSize(1280,650);
					// start game and add a reference to main.
					//game = new Cl_Game(mainFrame);
					// Start game background and set layout.
					//G_BG = new Game_Background();
					//G_BG.setLayout(new BorderLayout());
					// Start server selection and add a reference to main.
					//server = new Cl_Ser(mainFrame);
					// Add server selection to background pane
					//G_BG.add(server, BorderLayout.CENTER);
					// Add background pane to main.
					//mainFrame.setContentPane(G_BG);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	public void attemptConnection() {
		
	}
	
	public void newConnection(String newUri) {
		this.uri = newUri;
		consumer.interrupt();
		receiver.interrupt();
		
		try {
			hostSpace = new RemoteSpace(uri);
			
			receiver = new Thread(new Receiver(clientSpace, userName, hostSpace));
			consumer = new Thread(new Consumer(clientSpace, userName, hostSpace));
			consumer.start();
			receiver.start();
			
		} catch (UnknownHostException e) {
			Log.important("Host not found!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	

}
