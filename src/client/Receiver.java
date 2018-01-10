package client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

public class Receiver implements Runnable {
	private Space clientSpace;
	private String userName;
	private Space hostSpace;
	
	public Receiver(Space clientSpace, String userName, Space hostSpace) {
		this.clientSpace = clientSpace;
		this.userName = userName;
		this.hostSpace = hostSpace;
	}

	@Override
	public void run() {
		
		while(true) {
			try {
				Object[] input = hostSpace.get(new ActualField(userName), new FormalField(Object.class));
				clientSpace.put(input);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
