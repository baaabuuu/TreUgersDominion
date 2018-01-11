package network;


import org.jspace.ActualField;
import org.jspace.Space;

import engine.Game;

public class PlayerThread extends Thread {
	Space gameSpace;
	String playerName;
	Game game;
	
	public PlayerThread(Space gameSpace, String playerName, Game game)
	{
		
		this.gameSpace = gameSpace;
		this.playerName = playerName;
		this.game = game;
	}
	
	public void run(){
		try {
			gameSpace.put(new ActualField(playerName), new ActualField("gameStart"));
			//gameSpace.put(new ActualField(playerName),new ActualField("gameStart"),new ActualField(game.));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void sendMessage(String cmd){
		// TODO
	}
	
	public String getPlayerName(){
		return playerName;
	}
}
