package network;


import org.jspace.ActualField;
import org.jspace.Space;

import engine.StartGame;

public class PlayerThread extends Thread {
	Space gameSpace;
	String playerName;
	StartGame game;
	
	public PlayerThread(Space gameSpace, String playerName, StartGame game){
		
		this.gameSpace = gameSpace;
		this.playerName = playerName;
		this.game = game;
	}
	
	public void run(){
		try {
			gameSpace.put(new ActualField(playerName),new ActualField("gameStart"));
			//gameSpace.put(new ActualField(playerName),new ActualField("gameStart"),new ActualField(game.));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
