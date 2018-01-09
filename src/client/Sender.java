package client;

import org.jspace.Space;

public class Sender implements Runnable {
	Space clientSpace;
	public Sender(Space clientSpace) {
		this.clientSpace = clientSpace;
	}

	@Override
	public void run() {
		
	}
}
