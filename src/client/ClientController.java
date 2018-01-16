package client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import clientUI.UIController;
import log.Log;
import objects.ClientCommands;
import objects.ServerCommands;
/**
 * The ClientController initiates the client and UI and handles opening and closing of
 * connections and threads.
 */
public class ClientController {
	private String userName;
	private String host;
	private int playerID;
	private int port;
	private String uri;
	private Space clientSpace;
	private Space hostSpace;
	private Space userSpace;
	
	/*
	private PrivateKey privKey = null;
    private PublicKey pubKey = null;
    private PublicKey serverKey = null;
	*/
	
	private Thread consumer;
	private Thread receiver;
	private Thread connecterDetector;
	
	private ClientController clientController = this;
	private UIController userInterface;
	/**
	 * The constructor of ClientController.
	 * @param port
	 * @param host
	 */
	public ClientController(int port, String host){
		this.port = port;
		this.host = host;
		
		/*
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");//Uses Digtial Signature Algorithm and thhe deafult SUN provider.
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(1024, random);

            KeyPair pair = keyGen.generateKeyPair();
            privKey = pair.getPrivate();
            pubKey = pair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */
        
	}/**
	 * Runs the ClientController
	 */
	public void run() {
		
		userSpace = new SequentialSpace();
		clientSpace = new SequentialSpace();
		
		connecterDetector = new Thread(new ConnectionDetector(clientSpace, clientController));
		connecterDetector.start();
		
		userInterface = new UIController(port, host, clientController, userSpace);
		
	}
	/**
	 * Attempts to establish a connection to a remote space,
	 * exchange public keys with the server and get an ID from server.
	 * @param uri
	 */
	public void attemptConnection(String uri) {
		try {
			hostSpace = new RemoteSpace(uri);
			initiateCommunication();
			//input = hostSpace.query(new ActualField(ServerCommands.serverKey), new FormalField(PublicKey.class));
			
		} catch (UnknownHostException e) {
			Log.important("UnknownHostException");
			userInterface.connectionError();
		} catch (IOException e) {
			Log.important("IOException");
			userInterface.connectionError();
		} catch (InterruptedException e) {
			Log.important("InterruptedException");
			userInterface.connectionError();
		}
	}
	/**
	 * Close the already established connection, then attempt to connect to a new one.
	 * @param newUri
	 */
	public void newConnection(String newUri) {
		this.uri = newUri;
		consumer.interrupt();
		receiver.interrupt();
		
		try {
			hostSpace = new RemoteSpace(uri);
			initiateCommunication();
		} catch (UnknownHostException e) {
			Log.important("UnknownHostException");
			userInterface.newConnectionError();
		} catch (IOException e) {
			Log.important("IOException");
			userInterface.newConnectionError();
		} catch (InterruptedException e) {
			Log.important("InterruptedException");
			userInterface.newConnectionError();
		}
	}
	/**
	 * Initiates the communication with the server.
	 * @throws InterruptedException 
	 */
	private void initiateCommunication() throws InterruptedException {
		Object[] input;
		
		hostSpace.put(ClientCommands.newPlayer, -1);
		input = hostSpace.get(new ActualField(ServerCommands.playerID),new FormalField(int.class));
		
		playerID = (int)input[1];
		input = new Object[2];
		input[0] = userName;
		input[1] = playerID;
		hostSpace.put(ClientCommands.playerName,input);
		
		receiver = new Thread(new Receiver(clientSpace, playerID, hostSpace));
		consumer = new Thread(new Consumer(clientSpace, playerID, hostSpace, userSpace, userInterface));
		consumer.start();
		receiver.start();
	}
}
