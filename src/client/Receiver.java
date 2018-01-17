package client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import log.Log;

public class Receiver implements Runnable {
	private Space clientSpace;
	private int playerID;
	private Space hostSpace;
	
	public Receiver(Space clientSpace, int playerID, Space hostSpace) {
		this.clientSpace = clientSpace;
		this.playerID = playerID;
		this.hostSpace = hostSpace;
	}

	@Override
	public void run() {
		
		while(true) {
			/*
			try {
				Object[] input = hostSpace.get(new FormalField(Object.class), new ActualField(playerID));
				clientSpace.put(input[1], playerID);
				Log.log("Recieved package from host marked for: " + playerID);
			} catch (InterruptedException e) {
				Log.important("InterruptedException");
			}
			*/
		}
	}
	
}
