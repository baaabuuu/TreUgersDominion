package client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import log.Log;
import objects.ServerCommands;
/**
 * Detects when the server commands the client to make a new connection and .
 * @param Space (local)
 * @param Space (remote)
 * @throws InterruptedException 
 */
public class ConnectionDetector implements Runnable {
	private Space hostSpace;
	private int playerID;
	private ClientController handler;
	public ConnectionDetector(Space hostSpace, ClientController handler) {
		this.hostSpace = hostSpace;
		this.handler = handler;
	}

	@Override
	public void run() {
		Object[] obj;
		try {
			obj = hostSpace.get(new ActualField(playerID), new ActualField(ServerCommands.newConnection), new FormalField(String.class));
			
			Log.important("Received newConnection message and an URI.");
			handler.newConnection((String)obj[2]);
		} catch (InterruptedException e) {
			Log.important("Thread interrupted!");
			e.printStackTrace();
		}
	}
}
