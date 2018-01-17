package network;

import java.util.ArrayList;
import java.util.Random;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

import cards.CardReader;
import log.Log;
import objects.ClientCommands;
import objects.ServerCommands;
import engine.GameStarter;


public class Lobby extends Thread  {
	
	private String uri;
	private Space clientSpace, safeSpace;
	private int noOfPlayers;
	private int activePlayers;
	

	private ArrayList<String> expansions = new ArrayList<String>();
	private CardReader cardReader;
	private ArrayList<String> players = new ArrayList<String>();
	private Writer writer;
	private ControlCenter controlCenter;
	private boolean gameRunning;
	
	public Lobby(String uri, CardReader cardReader, Space clientSpace){
		
		gameRunning = false;
		
		this.uri = uri;
		this.clientSpace = clientSpace;
		
		//Setup safe space
		safeSpace = new SequentialSpace();
				
		this.noOfPlayers = 2;
		this.activePlayers = 0;
	
		this.cardReader = cardReader;
		
		expansions.add("base");
		
		
		}
	
	public void run(){
		
		
		Object[] input = null;
		while(activePlayers < noOfPlayers){
			Log.log("Searching for players");
			try {
				clientSpace.get(new ActualField(-1), new ActualField(ClientCommands.newPlayer));
				
				Log.log("Player found. ID sent: " + activePlayers);
				
				clientSpace.put(ServerCommands.playerID, activePlayers);
				
				Log.log("Looking for playername");
				clientSpace.get(new ActualField(activePlayers), new ActualField(ClientCommands.playerName));
				input = clientSpace.get(new ActualField(activePlayers), new FormalField(String.class));
				
				players.add((String) input[1]);
				Log.log("Player registred. Name of user: " + (String) input[1] + " ID: " + activePlayers);
				
				Log.log("Sending waiting message");
				
				clientSpace.put(ServerCommands.message, activePlayers);
				clientSpace.put(activePlayers, "Welcome " + players.get(activePlayers) + " you are player number: " + activePlayers);
				
				clientSpace.put(ServerCommands.message, activePlayers);
				clientSpace.put(activePlayers, "Waiting for other players");
				
				
				activePlayers++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
		
		Log.log("Setting up game");
		
		
		//Setup writer
		writer = new Writer(clientSpace, players.toArray(new String[noOfPlayers]));
		
		//Setup controlcenter
		controlCenter = new ControlCenter(clientSpace, safeSpace);
		controlCenter.start();
		gameRunning = true;
		
		//Setup game		
		GameStarter gameStarter = new GameStarter(noOfPlayers, players.toArray(new String[noOfPlayers]), cardReader, expansions, new Random(), writer, safeSpace);
		
		Log.log("Sending game start message");
		try {
			gameStarter.startGame();
			safeSpace.put(ServerCommands.gameStart);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.log("Game start");
		
	}
	
	public String getURI(){
		return uri;
	}
	public int getActivePlayers() {
		return activePlayers;
	}	
	
	public boolean getGameRunning(){
		return gameRunning;
	}
	
}


