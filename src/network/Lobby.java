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
	

	private ArrayList<String> expansions;
	private CardReader cardReader;
	private ArrayList<String> players;
	private Writer writer;
	private ControlCenter controlCenter;
	
	public Lobby(String uri, CardReader cardReader, Space clientSpace){
		
		this.uri = uri;
		this.clientSpace = clientSpace;
		
		//Setup safe space
		safeSpace = new SequentialSpace();
				
		this.noOfPlayers = 4;
		this.activePlayers = 0;
	
		this.cardReader = cardReader;
		
		expansions.add("base");
		
		
		}
	
	public void run(){
		
		
		Object[] input = null;
		Log.log("Searching for players");
		while(activePlayers < noOfPlayers){
			try {
				input = clientSpace.get( new ActualField(ClientCommands.newPlayer), new FormalField(Integer.class));
				
				Log.log("Player found. ID sent: " + activePlayers);
				
				clientSpace.put(ServerCommands.playerID, activePlayers);
				
				Log.log("Looking for playername");
				input = clientSpace.get( new ActualField(ClientCommands.playerName), new FormalField(String.class));
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			players.add((String) input[1]);
			Log.log("Player registred. Name of user: " + (String) input[1] + " ID: " + activePlayers);
			
			activePlayers++;
			
		}
		

		//Setup writer
		writer = new Writer(clientSpace, players.toArray(new String[noOfPlayers]));
		
		//Setup controlcenter
		controlCenter = new ControlCenter(clientSpace, safeSpace);
		controlCenter.start();
		
		//Setup game		
		GameStarter gameStarter = new GameStarter(noOfPlayers, players.toArray(new String[noOfPlayers]), cardReader, expansions, new Random(), writer, safeSpace);
		try {
			gameStarter.startGame();
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
	
}


