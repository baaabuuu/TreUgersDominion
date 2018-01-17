package network;

import org.jspace.Space;
import org.jspace.Tuple;

import log.Log;
import objects.BoardState;
import objects.CardOption;
import objects.PlayerHand;
import objects.ServerCommands;
import objects.TurnValues;

public class Writer {

	private Space clientSpace;
	String[] players;
	private static int port = 8181;
	private static String host = "localhost";
	private static String uri = "tcp://"+ host + ":" + port + "/?keep";
	
	
	
	public Writer(Space clientSpace, String[] players){
	
		this.clientSpace = clientSpace;
		this.players = players;
	}
	
	public void sendMessage(Tuple tuple) throws InterruptedException{

		ServerCommands cmd = tuple.getElementAt(ServerCommands.class, 1);
		int playerID = tuple.getElementAt(Integer.class, 0);
		
		Log.log("Sending message: " + cmd.toString());
		
		switch(cmd){
		case newConnection:
			clientSpace.put(cmd, playerID);
			clientSpace.put(playerID, uri);
			break;
		
		case gameStart: 
			for(int i = 0; i < players.length; i++)
			clientSpace.put(cmd, i);
			break;
			
		case setBoardState: 
			clientSpace.put(cmd, playerID);
			clientSpace.put(playerID, tuple.getElementAt(BoardState.class, 2));
			break;
			
		case playerSelect: 
			clientSpace.put(cmd, playerID);
			clientSpace.put(playerID, tuple.getElementAt(CardOption.class, 2));
			break;
			
		case setPlayerHand: 
			clientSpace.put(cmd, playerID);
			clientSpace.put(playerID, tuple.getElementAt(PlayerHand.class, 2));
			break;
		
		case takeTurn: 
			clientSpace.put(cmd, playerID);
			clientSpace.put(playerID, tuple.getElementAt(BoardState.class, 2), tuple.getElementAt(PlayerHand.class,3), tuple.getElementAt(TurnValues.class, 4));
			break;
		
		case invalid:
		case message: 
			clientSpace.put(cmd, playerID);
			clientSpace.put(playerID, tuple.getElementAt(String.class, 2));
			break;
			
			
		default:	
			clientSpace.put(cmd, playerID);
			clientSpace.put(tuple);
			break;
		}
	}
	
	
	
	
	
	
}
