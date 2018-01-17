package client;

import java.io.IOException;
import java.net.UnknownHostException;

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
	private RemoteSpace hostSpace;
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
		userInterface = new UIController(port, host, clientController, userSpace);
		
	}
	/**
	 * Attempts to establish a connection to a remote space,
	 * exchange public keys with the server and get an ID from server.
	 * @param uri
	 */
	public void attemptConnection(String uri) {
		this.uri = uri;
		try {
			this.hostSpace = new RemoteSpace(uri);
			
			initiateCommunication();
			userInterface.startGame();
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
		Log.log("Connecting to: " + uri);
		try {
			hostSpace.close();
		} catch (IOException e1) {
			
		}
		consumer.interrupt();
		connecterDetector.interrupt();
		
		Log.log("" + consumer.isInterrupted());
		Log.log("" + connecterDetector.isInterrupted());
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			Log.important("" + e1.getLocalizedMessage() + "\n" + e1.getMessage());
			Log.important("Sleep was interrupted");
		}
		//receiver.interrupt();
		
		try {
			hostSpace = new RemoteSpace(uri);
			Log.log("connected to new RemoteSpace");
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
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	private void initiateCommunication() throws InterruptedException, UnknownHostException, IOException {
		Object[] input;
		Log.log("initiateCommunication");
		hostSpace.put(-1, ClientCommands.newPlayer);
		Log.log("Before get");
		input = hostSpace.get(new ActualField(ServerCommands.playerID),new FormalField(Integer.class));
		
		playerID = (int)input[1];
		Log.log("Gained ID: " + playerID);
		
		hostSpace.put(playerID, ClientCommands.playerName);
		hostSpace.put(playerID,userName);
		
		//receiver = new Thread(new Receiver(clientSpace, playerID, hostSpace));
		consumer = new Thread(new Consumer(clientSpace, playerID, hostSpace, userSpace, userInterface, clientController));
		connecterDetector = new Thread(new ConnectionDetector(new RemoteSpace(this.uri), clientController));
		
		consumer.start();
		connecterDetector.start();
		
		//receiver.start();
		Log.log("Threads initiated.");
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * Abuses a method to kill jSpace servers.
	 */
	public void killServer() {
		try {
			hostSpace = new RemoteSpace("tcp://" + host + ":" + port + "/ripServer?conn");
			Log.log("Connected to a non-exsistant space!");
			try {
				Log.log((String)(hostSpace.get(new ActualField("Hello from the non-existant space!"))[0]));
			}catch(NullPointerException e) {
				Log.log("I just skipped a get!");
			}
			Log.log("Killed the server!");
			System.exit(0);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
