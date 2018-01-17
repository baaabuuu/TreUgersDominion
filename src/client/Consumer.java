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
	private Space hostSpace;
	private Space clientSpace;
	private int playerID;
	private ClientActions action;
	private UIController userInterface;
	private ClientController clientController;
	
	/**
	 * Consumes relevant data from the servers space and places it into the clients space.
	 * @param clientController 
	 */
	public Consumer(Space space, int playerID, Space hostSpace, Space userSpace, UIController userInterface, ClientController clientController) {
		this.clientSpace = space;
		this.playerID = playerID;
		this.hostSpace = hostSpace;
		this.userInterface = userInterface;
		this.clientController = clientController;
		this.action = new ClientActions(playerID, userSpace, userInterface);
	}
	/**
	 * Runs the consumer.  
	 */
	@Override
	public void run() {
		
		Object[] objs;
		Object[] input;
		boolean lock = true;
		while(lock) {
			try {
				
				//Consumes all tuples that contains a playerID and a value from the ServerCommands class.
				try {
					objs = hostSpace.get(new FormalField(ServerCommands.class), new ActualField(playerID));
				}catch(InterruptedException e) {
					Log.log("Consumer was interrupted");
					objs = new Object[1];
					objs[0] = ServerCommands.newConnection;
				}
				switch ((ServerCommands)objs[0]) {
					case setBoardState: Log.log("Recieved a setBoardState");
							input = hostSpace.get(new ActualField(playerID), 
								new FormalField(BoardState.class));
							userInterface.newBoardState((BoardState) input[1]);
							break;
					case takeTurn: Log.log("Recieved a takeTurn");
							action.takeTurn(hostSpace);
							break;
					case playerSelect: Log.log("Recieved a playerSelect");
							input = hostSpace.get(new ActualField(playerID), 
								new FormalField(CardOption.class));
							action.playerSelect((CardOption)input[1], hostSpace);
							break;
					case setPlayerHand: Log.log("Recieved a setPlayerHand");
							input = hostSpace.get(new ActualField(playerID), 
								new FormalField(PlayerHand.class));
							Log.log("Recieved a PlayerHand");
							action.setPlayerHand((PlayerHand)input[1]);
							break;
					case message:Log.log("Recieved a message");
							input = hostSpace.get(new ActualField(playerID), 
								new FormalField(String.class));
							action.serverMessage((String)input[1]);
							break;
					case setNames: Log.log("Recieved a setNames");
							input = hostSpace.get(new ActualField(playerID), 
								new FormalField(String[].class));
							action.setNames((String[])input[1]);
							break;
					case setBuyArea: Log.log("Recieved a setBuyArea");
							input = hostSpace.get(new ActualField(playerID), 
								new FormalField(Card[].class));
							action.setBuyArea((Card[])input[1]);
							break;
					case setLaunge: Log.log("Recieved a setLaunge");
							input = hostSpace.get(new ActualField(playerID), 
								new FormalField(Object.class));
							Log.log("Recieved the object");
							action.displayLaunge(((LoungeObject)input[1]).getGames(), hostSpace);
							break;
					case newConnection: Log.log("Recieved a newConnection");
							lock = false;
							break;
					default: break;
				}
				
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
