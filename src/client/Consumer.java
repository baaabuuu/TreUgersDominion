package client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;
import client.ClientActions;
import client.ClientActions.bState;

public class Consumer implements Runnable {
	private Space clientSpace;
	private String name;
	
	public Consumer(Space space, String name) {
		this.clientSpace = space;
		this.name = name;
	}
	
	@Override
	public void run() {
		
		while(true) {
			try {
				
				Object[] objs = clientSpace.getp(new ActualField(name), 
						new FormalField(String.class));
				
				if(objs[0] == "1") {
					bState board = (bState) objs[1];
					ClientActions.updateBoard(board);
				}
				else if(objs[0] == "2") {
					ClientActions.takeTurn();
				}
				else if(objs[0] == "3") {
					ClientActions.nonTurnAction();
				}
				
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

}
