package client;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import clientUI.UIController;
import log.Log;

public class ClientController {
	private String userName;
	private String host;
	private int port;
	private String uri;
	private Space clientSpace;
	private Space hostSpace;
	
	private Thread consumer;
	private Thread receiver;
	private Thread connecterDetector;
	
	private ClientController clientController = this;
	private UIController userInterface;
	
	public ClientController(int port, String host){
		this.port = port;
		this.host = host;
	}
	public void run() {
		
		clientSpace = new SequentialSpace();
		
		connecterDetector = new Thread(new ConnectionDetector(clientSpace, clientController));
		connecterDetector.start();
		
		userInterface = new UIController(port, host, clientController);
		
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
			userInterface.connectionError();
		} catch (IOException e) {
			Log.important("IOException");
			userInterface.connectionError();
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
			userInterface.newConnectionError();
			
		} catch (IOException e) {
			Log.important("IOException");
			userInterface.newConnectionError();
		}
	}
	
	
	

}
