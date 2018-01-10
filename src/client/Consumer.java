package client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import Objects.BoardState;
import Objects.OotAction;
import cards.Card;

public class Consumer implements Runnable {
	private Space clientSpace;
	private String name;
	private Space hostSpace;
	private ClientActions action;
	
	public Consumer(Space space, String name, Space hostSpace) {
		this.clientSpace = space;
		this.name = name;
		this.hostSpace = hostSpace;
		this.action = new ClientActions(name);
	}
	
	@Override
	public void run() {
		
		Object[] objs;
		Object[] input;
		while(true) {
			try {
				
				objs = clientSpace.getp(new ActualField(name), 
						new FormalField(Integer.class));
				
				
				switch ((int)objs[1]) {
					case 1: input = clientSpace.getp(new ActualField(name), 
								new FormalField(BoardState.class));
							action.updateBoard((BoardState) input[1]);
							break;
					case 2: action.takeTurn(clientSpace, hostSpace);
							break;
					case 3: input = clientSpace.getp(new ActualField(name), 
								new FormalField(OotAction.class));
							action.nonTurnAction((OotAction)input[1], hostSpace);
							break;
					case 4: input = clientSpace.getp(new ActualField(name), 
								new FormalField(Card[].class));
							action.setPlayerHand((Card[])input[1]);
							break;
					default: break;
				}
				
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

}
