package client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import Objects.BoardState;
import Objects.OotAction;
import cards.Card;
import client.ClientActions;

public class Consumer implements Runnable {
	private Space clientSpace;
	private String name;
	
	public Consumer(Space space, String name) {
		this.clientSpace = space;
		this.name = name;
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
							ClientActions.updateBoard((BoardState) input[1]);
							break;
					case 2: ClientActions.takeTurn();
							break;
					case 3: input = clientSpace.getp(new ActualField(name), 
								new FormalField(OotAction.class));
							ClientActions.nonTurnAction((OotAction)input[1]);
							break;
					case 4: input = clientSpace.getp(new ActualField(name), 
								new FormalField(Object.class));
							ClientActions.playerHand((Card[])input[1]);
							break;
					default: break;
				}
				
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

}
