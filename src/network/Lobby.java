package network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.jspace.ActualField;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;
import org.jspace.Tuple;

import cards.CardReader;
import log.Log;
import engine.Game;
import engine.GameStarter;


public class Lobby extends Thread  {
	
	private SpaceRepository repository;
	private String uri;
	private Space clientSpace, safeSpace;
	private int noOfPlayers;
	private int activePlayers;
	

	private ArrayList<String> expansions;
	private CardReader cardReader;
	private ArrayList<String> players;
	private Writer writer;
	private ControlCenter controlCenter;
	
	public Lobby(String uri, CardReader cardReader){
		
		//Setup client space
		this.uri = uri;
		repository = new SpaceRepository();
		repository.addGate(uri);
		clientSpace = new SequentialSpace();
		repository.add("game", clientSpace);
		
		//Setup safe space
		safeSpace = new SequentialSpace();
		
		
		this.noOfPlayers = 4;
		this.activePlayers = 0;
	
		this.cardReader = cardReader;
		
		expansions.add("base");
		
		
		}
	
	public void run(){
		
		
		String name = "";
		Log.log("Searching for players");
		while(activePlayers < noOfPlayers){
			try {
				clientSpace.get(new ActualField(name), new ActualField("enter") );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.log("Player found. Name of user: " + name);
			
			players.add(name);
			
			
		}
		

		//Setup writer
		writer = new Writer(clientSpace, players.toArray(new String[noOfPlayers]));
		
		//Setup controlcenter
		controlCenter = new ControlCenter(clientSpace, safeSpace);
		controlCenter.start();
		
		//Setup game		
		GameStarter gameStarter = new GameStarter(noOfPlayers, players.toArray(new String[noOfPlayers]), cardReader, expansions, new Random(), writer, safeSpace);
		gameStarter.startGame();
		Log.log("Game start");
		
	}
	
	public String getURI(){
		return uri;
	}
	public int getActivePlayers() {
		return activePlayers;
	}	
	
}


