package network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.jspace.ActualField;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

import cards.CardReader;
import log.Log;
import engine.Game;
import engine.GameStarter;


public class GameThread extends Thread  {
	
	private SpaceRepository repository;
	private String uri;
	private Space gameSpace;
	private int noOfPlayers;
	private int activePlayers;
	private ArrayList<String> expansions;
	private CardReader cardReader;
	private PlayerThread[] players;
	
	
	public GameThread(String uri, CardReader cardReader){
			//Setup the jSpace
			repository = new SpaceRepository();
			repository.addGate(uri);
			gameSpace = new SequentialSpace();
			repository.add("game", gameSpace);
			
			this.uri = uri;
			
			this.noOfPlayers = 4;
			this.activePlayers = 0;
		
			this.cardReader = cardReader;
			
			expansions.add("base");
			
		
		}
	
	public void run(){
		
		players = new PlayerThread[noOfPlayers];
		String name = "";
		ArrayList<String> playerNames = new ArrayList<String>();
		Log.log("Searching for players");
		while(activePlayers < noOfPlayers){
			try {
				gameSpace.get(new ActualField("server"), new ActualField("enter"), new ActualField(name) );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.log("Player found. Name of user: " + name);
			
			playerNames.add(name);
			
			
		}
		Log.log("Game start");
		GameStarter gameStarter = new GameStarter(noOfPlayers,playerNames.toArray(new String[noOfPlayers]), cardReader, expansions, new Random());
		gameStarter.startGame();
		Game game = gameStarter.getGame();
		
		for(int i = 0; i<noOfPlayers ; i++){
			players[i] = new PlayerThread(gameSpace, name, game);
			players[i].start();
		}
		
	}
	
	public String getURI(){
		return uri;
	}
	
	
	public void sendToOthers(String excludedName, String cmd){
		
		for(int i = 0; i<noOfPlayers ; i++){
			if(players[i].getPlayerName() != excludedName){
			players[i].sendMessage(cmd);
			}
		}
		
	}
	
	public void sendToPlayer(String name, String cmd){
		
		for(int i = 0; i<noOfPlayers ; i++){
			if(players[i].getPlayerName() == name){
			players[i].sendMessage(cmd);
			}
		}
	}
	
}
