package network;


import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.HashMap;

import org.jspace.ActualField;
import org.jspace.FIFOSpace;
import org.jspace.FormalField;
import org.jspace.QueueSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;
import org.jspace.Tuple;

import cards.CardReader;
import log.Log;
import objects.ClientCommands;
import objects.LoungeObject;
import objects.ServerCommands;


public class Lounge {
	//Setup
	private int noOfGamesAllowed = 1000;
	private int noOfPlayersAllowed = 5*noOfGamesAllowed;
	private Lobby[] gamesRunning = new Lobby[noOfGamesAllowed];
	private HashMap<Integer, Integer> gamesMap = new HashMap<Integer, Integer>(); 
	private String[] playerNames = new String[noOfGamesAllowed];
	private int indexID;
	private LoungeObject object = new LoungeObject();
	
	
	
	//Setup the uri
	private int port = 8181;
	private String host = "localhost";
	private String uri = "tcp://"+ host + ":" + port + "/?keep";
	
	
	public static void main(String[] args) throws InterruptedException{
		new Lounge().Start();
	}
	
	public void Start() throws InterruptedException{
		Log.log("Starting server");
		
		//Setup the Repository
		SpaceRepository repository = new SpaceRepository();
		repository.addGate(uri);
		
		//Setup the lobby
		Space lounge	 = new QueueSpace();
		repository.add("lounge", lounge);
		
		//Setting up value holders
		int playerID;
		ClientCommands cmd;
		String tempURI;
		int gameID;
		
		//Setting up private/public key
//		PrivateKey privKey = null;
//		PublicKey pubKey = null;
//		try {
//			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");//Uses Digtial Signature Algorithm and thhe deafult SUN provider.
//			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
//			keyGen.initialize(1024, random);
//			
//			KeyPair pair = keyGen.generateKeyPair();
//			privKey = pair.getPrivate();
//			pubKey = pair.getPublic();
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchProviderException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//		
//		
//		//Puts the public key in the jSpace
//		lounge.put(ServerCommands.serverKey, pubKey);
//		
		//Set up cardReader
		CardReader cardReader = null;
		try {
			cardReader = new CardReader();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		//Wait for command
		while(true){
			Log.log("Waiting for new Message");
			//Reads command
			Object[] firstInput = lounge.get(new FormalField(Integer.class), new FormalField(ClientCommands.class));
			playerID = (int) firstInput[0];
			cmd = (ClientCommands) firstInput[1];
			
			Log.log("Message recived: \"" + cmd.toString() + "\", from: " + playerID);
			
			Object[] secondInput;
			//Obeys command
			switch(cmd){
			case enterLobby:
				Log.log("Finding uri for lobby");
				secondInput = lounge.get(new ActualField(playerID), new FormalField(Integer.class));
				gameID = (int) secondInput[1];
				
				//Check if the given game is valid
				if(noOfGamesAllowed > gameID && gamesRunning[gameID] != null && !gamesRunning[gameID].getGameRunning()){
					Log.log("Sending uri");
					lounge.put(playerID, ServerCommands.newConnection, gamesRunning[gameID].getURI()); //Special command to interrupt current connection
					playerNames[playerID] = null;
					sendHeader(lounge, playerID, ServerCommands.playerID);
				} else {
					Log.log("Failed to find game. Sending Exception");
					sendMessage(lounge, playerID, "Game Not Found or already started");
					lounge.put(playerID, ClientCommands.getLobbies);
				}
				break;
			case createLobby:
				Log.log("Creating lobby");
				//Finds a valid game space
				for(int i = 0; i<noOfGamesAllowed; i++)
				{
					if(gamesRunning[i] == null)
					{
						
						tempURI = "tcp://"+ host + ":" + port + "/" + i +"?keep";
						Log.log("URI: " + tempURI);
						//Setup game
						Space gameSpace = new SequentialSpace();
						repository.add(Integer.toString(i), gameSpace);
						Lobby tempInit = new Lobby(tempURI, cardReader, gameSpace);
						gamesRunning[i] = tempInit;
						tempInit.start();
						//Sending URI
						tempURI = "tcp://"+ host + ":" + port + "/" + i +"?conn";
						Log.log("Game created. Sending URI to: " + playerID);						
						lounge.put(playerID, ServerCommands.newConnection, tempURI); //Special command to interrupt current connection from user
						playerNames[playerID] = null;
						sendHeader(lounge, playerID, ServerCommands.newConnection);
						break;
					}
				}
				break;
			case getLobbies:
				Log.log("Finding lobbies");
				//Checks all games and puts them in a hashmap if they are active
				for(int i = 0; i< noOfGamesAllowed; i++){
					
					if (gamesRunning[i] != null && !gamesRunning[i].getGameRunning()){
						
						gamesMap.put(i, gamesRunning[i].getActivePlayers());
					} else if (gamesRunning[i] != null && gamesRunning[i].getGameRunning()) {
						gamesMap.remove(i);
					}
				}	
					object.setGames(gamesMap);
					Log.log("Sending lobies");
					sendHeader(lounge, playerID, ServerCommands.setLaunge);
					lounge.put(playerID, object);
				break;
			case newPlayer:	
				int ID;
				Log.log("Finding avaible ID");
				//Finds a avaible ID
				for(ID = 0; ID < noOfPlayersAllowed; ID++){
					if(playerNames[ID] == null){
						Log.log("Sending player ID: " + ID);
						
						sendHeader(lounge, ID, ServerCommands.playerID);
						
						playerNames[ID] = "";
						
						break;
					}
				}
				break;
			case playerName:
				secondInput = lounge.get(new ActualField(playerID), new FormalField(String.class));
				//If the name is avaible, then sets it
				if(playerNames[(int) secondInput[0]] == "" && (String) secondInput[1] != ""){
					Log.log("Saving ID: " + (int) secondInput[0] + " as name: " + (String) secondInput[1]);
					playerNames[(int) secondInput[0]] = (String) secondInput[1];	
				}
				sendMessage(lounge, playerID, "Welcome " + playerNames[playerID] + " you have ID: " + playerID);
				lounge.put(playerID, ClientCommands.getLobbies);
				
				break;
			default:
				Log.important("Uknown message recieved. Ignoring.");
				break;
			}
		}
	
	
	}

	
	/**
	 * Sends a message to the given player ID, on the given space.
	 *  
	 * @param jSpace
	 * @param playerID
	 * @param msg
	 * @throws InterruptedException
	 */
	private void sendMessage(Space jSpace, int playerID, String msg) throws InterruptedException {
		sendHeader(jSpace, playerID, ServerCommands.message);
		jSpace.put(playerID, msg);
	}
	/**
	 * Sends the standard header for communication with the client.
	 * 
	 * Sends the ServerCommand to the player on the jSpace
	 * @param jSpace
	 * @param playerID
	 * @param server cmd	
	 * @throws InterruptedException
	 */
	private void sendHeader(Space jSpace, int playerID, ServerCommands cmd) throws InterruptedException {
		jSpace.put(cmd, playerID);
	}
 
		
}
