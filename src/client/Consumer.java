package client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import clientUI.UIController;
import objects.*;

public class Consumer implements Runnable {
	private Space clientSpace;
	private Space hostSpace;
	private Space userSpace;
	private String name;
	private ClientActions action;
	private UIController userInterface;
	
	public Consumer(Space space, String name, Space hostSpace, Space userSpace, UIController userInterface) {
		this.clientSpace = space;
		this.name = name;
		this.hostSpace = hostSpace;
		this.userSpace = userSpace;
		this.userInterface = userInterface;
		this.action = new ClientActions(name, userSpace, userInterface);
	}
	
	@Override
	public void run() {
		
		Object[] objs;
		Object[] input;
		while(true) {
			try {
				
				objs = clientSpace.get(new ActualField(name), 
						new FormalField(ServerCommands.class));
				
				
				switch ((ServerCommands)objs[1]) {
					case setBoardState: input = clientSpace.get(new ActualField(name), 
								new FormalField(BoardState.class));
							action.displayBoardState((BoardState) input[1]);
							break;
					case takeTurn: action.takeTurn(clientSpace, hostSpace);
							break;
					case playerSelect: input = clientSpace.get(new ActualField(name), 
								new FormalField(CardOption.class));
							action.playerSelect((CardOption)input[1], hostSpace);
							break;
					case setPlayerHand: input = clientSpace.get(new ActualField(name), 
								new FormalField(PlayerHand.class));
							action.setPlayerHand((PlayerHand)input[1]);
							break;
					case message: input = clientSpace.get(new ActualField(name), 
								new FormalField(String.class));
							action.serverMessage((String)input[1]);
							break;
					case setNames: input = clientSpace.get(new ActualField(name), 
								new FormalField(String[].class));
							action.setNames((String[])input[1]);
							break;
					case setLaunge: input = clientSpace.get(new ActualField(name), 
								new FormalField(Launge.class));
							action.displayLaunge((Launge)input[1]);
							break;
					default: break;
				}
				
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
