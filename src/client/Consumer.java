package client;

import java.util.HashMap;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import cards.Card;
import clientUI.UIController;
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
				objs = clientSpace.get(new ActualField(playerID), 
						new FormalField(ServerCommands.class));
				
				
				switch ((ServerCommands)objs[1]) {
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
					case setLaunge: input = clientSpace.get(new ActualField(playerID), 
								new FormalField(HashMap.class));
							action.displayLaunge((HashMap<Integer,Integer>)input[1], hostSpace);
							break;
					default: break;
				}
				
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
