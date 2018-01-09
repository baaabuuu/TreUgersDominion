package client;

import org.jspace.Space;

public class Receiver implements Runnable {
	Space clientSpace;
	public Receiver(Space clientSpace) {
		this.clientSpace = clientSpace;
	}
	
	@Override
	public void run() {
		
	}
	
}
