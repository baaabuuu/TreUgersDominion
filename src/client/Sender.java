package client;

import org.jspace.Space;

public class Sender implements Runnable {
	Space gameSpace;
	public Sender(Space gameSpace) {
		this.gameSpace = gameSpace;
	}

	@Override
	public void run() {
		
	}
}
