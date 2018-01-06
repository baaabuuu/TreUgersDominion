package client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;
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
		
		while(true) {
			try {
				
				Object[] objs = clientSpace.getp(new ActualField(name), 
						new FormalField(String.class));
				
				if(objs[0] == "1") {
					client.ClientActions();
				}
				else if(objs[0] == "2") {
					
				}
				else if(objs[0] == "3") {
					
				}
				
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
