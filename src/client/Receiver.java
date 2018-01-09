package client;

import org.jspace.Space;

public class Receiver implements Runnable {
	Space gameSpace;
	public Receiver(Space gameSpace) {
		this.gameSpace = gameSpace;
	}
	
	@Override
	public void run() {
		
	}
	
}
