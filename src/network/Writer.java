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

		int playerID = tuple.getElementAt(Integer.class, 0);
		ServerCommands cmd = tuple.getElementAt(ServerCommands.class, 1);
		
		Log.log("Sending message: " + cmd.toString());
		
		switch(cmd){
		case newConnection:
			
			sendStandardHeader(clientSpace, playerID, cmd);
			clientSpace.put(playerID, uri);
			break;
		
		case setBoardState: 
			sendStandardHeader(clientSpace, playerID, cmd);
			clientSpace.put(playerID, tuple.getElementAt(BoardState.class, 2));
			break;
			
		case playerSelect: 
			sendStandardHeader(clientSpace, playerID, cmd);
			clientSpace.put(playerID, tuple.getElementAt(CardOption.class, 2));
			break;
			
		case setPlayerHand: 
			sendStandardHeader(clientSpace, playerID, cmd);
			clientSpace.put(playerID, tuple.getElementAt(PlayerHand.class, 2));
			break;
		
		case takeTurn: 
			sendStandardHeader(clientSpace, playerID, cmd);
			clientSpace.put(playerID, tuple.getElementAt(BoardState.class, 2), tuple.getElementAt(PlayerHand.class,3), tuple.getElementAt(TurnValues.class, 4));
			break;
		
		case invalid:
		case message:
			sendStandardHeader(clientSpace, playerID, cmd);
			clientSpace.put(playerID, tuple.getElementAt(String.class, 2));
			break;
			
		default:	
			Log.important("Unknown message recived. Ignoring");
			break;
		}
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
	private void sendStandardHeader(Space jSpace, int playerID, ServerCommands cmd) throws InterruptedException {
		jSpace.put(cmd, playerID);
	}
	
	
	
	
}
