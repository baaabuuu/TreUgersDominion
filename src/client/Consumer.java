package client;

import java.util.HashMap;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;
import org.jspace.Tuple;

import cards.Card;
import clientUI.UIController;
import log.Log;
import objects.*;

public class Consumer implements Runnable {
	private Space clientSpace;
	private Space hostSpace;
	private int playerID;
	private ClientActions action;
	private UIController userInterface;
	
	/**
	 * Consumes relevant data from the servers space and places it into the clients space.
	 */
	public Consumer(Space space, int playerID, Space hostSpace, Space userSpace, UIController userInterface) {
		this.clientSpace = space;
		this.playerID = playerID;
		this.hostSpace = hostSpace;
		this.userInterface = userInterface;
		this.action = new ClientActions(playerID, userSpace, userInterface);
	}
	/**
	 * Runs the consumer.  
	 */
	@Override
	public void run() {
		
		Object[] objs;
		Object[] input;
		while(true) {
			try {
				//Consumes all tuples that contains a playerID and a value from the ServerCommands class.
				objs = hostSpace.get(new FormalField(ServerCommands.class), new ActualField(playerID));
				
				Log.log("Recieved a command");
				switch ((ServerCommands)objs[0]) {
					case setBoardState: input = clientSpace.get(new ActualField(playerID), 
								new FormalField(BoardState.class));
							userInterface.newBoardState((BoardState) input[1]);
							break;
					case takeTurn: action.takeTurn(clientSpace, hostSpace);
							break;
					case playerSelect: input = clientSpace.get(new ActualField(playerID), 
								new FormalField(CardOption.class));
							action.playerSelect((CardOption)input[1], hostSpace);
							break;
					case setPlayerHand: input = clientSpace.get(new ActualField(playerID), 
								new FormalField(PlayerHand.class));
							action.setPlayerHand((PlayerHand)input[1]);
							break;
					case message: input = clientSpace.get(new ActualField(playerID), 
								new FormalField(String.class));
							action.serverMessage((String)input[1]);
							break;
					case setNames: input = clientSpace.get(new ActualField(playerID), 
								new FormalField(String[].class));
							action.setNames((String[])input[1]);
							break;
					case setBuyArea: input = clientSpace.get(new ActualField(playerID), 
								new FormalField(Card[].class));
							action.setBuyArea((Card[])input[1]);
							break;
					case setLaunge: Log.log("Recieved a setLaunge");
							input = clientSpace.get(new ActualField(playerID), 
								new FormalField(Tuple.class));
							Log.log("Recieved a HashMap");
							action.displayLaunge((HashMap<Integer,Integer>)((Tuple) input[1]).getElementAt(0), hostSpace);
							break;
					default: break;
				}
				
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
