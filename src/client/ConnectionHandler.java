package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
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
	
	private MainFrame mainFrame;
	private GamePanel gamePanel;
	private GameBackground gameBG;
	private ServerPanel serverPanel;
	
	private ConnectionHandler connectionHandler = this;
	
	public ConnectionHandler(int port, String host){
		this.port = port;
		this.host = host;
	}
	public void run() {
		
		clientSpace = new SequentialSpace();
		
		connecterDetector = new Thread(new ConnectionDetector(clientSpace, connectionHandler));
		connecterDetector.start();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Create new mainFrame and set its size. 
					mainFrame = new MainFrame();
					mainFrame.setVisible(true);
					mainFrame.setSize(1280,650);
					// Create new gamePanel.
					gamePanel = new GamePanel(mainFrame);
					// Start game background and set layout.
					gameBG = new GameBackground();
					gameBG.setLayout(new BorderLayout());
					// Start server selection and add a reference to main.
					serverPanel = new ServerPanel(mainFrame, port, host, connectionHandler);
					// Add server selection to background pane
					gameBG.add(serverPanel, BorderLayout.CENTER);
					// Add background pane to main.
					mainFrame.setContentPane(gameBG);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	public void attemptConnection(String newUri) {
		
		try {
			hostSpace = new RemoteSpace(newUri);
			
			receiver = new Thread(new Receiver(clientSpace, userName, hostSpace));
			consumer = new Thread(new Consumer(clientSpace, userName, hostSpace));
			consumer.start();
			receiver.start();
			
		} catch (UnknownHostException e) {
			Log.important("UnknownHostException");
			serverPanel.setError("Host was not found.");
		} catch (IOException e) {
			Log.important("IOException");
			serverPanel.setError("Host was not found.");
		}
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
			Log.important("UnknownHostException");
			gameBG.remove(gamePanel);
			gameBG.add(serverPanel, BorderLayout.CENTER);
			serverPanel.setError("Host was not found.");
			
		} catch (IOException e) {
			Log.important("IOException");
			gameBG.remove(gamePanel);
			gameBG.add(serverPanel, BorderLayout.CENTER);
			serverPanel.setError("Host was not found.");
		}
	}
	
	
	

}
