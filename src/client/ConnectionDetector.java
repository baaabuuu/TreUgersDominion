package client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import log.Log;
import objects.ServerCommands;
import objects.Uri;
/**
 * Detects when the server commands the client to make a new connection and .
 * @param Space (local)
 * @param Space (remote)
 * @throws InterruptedException 
 */
public class ConnectionDetector implements Runnable {
	private Space clientSpace;
	private int playerID;
	private ConnectionHandler handler;
	public ConnectionDetector(Space clientSpace, ConnectionHandler handler) {
		this.clientSpace = clientSpace;
		this.handler = handler;
	}

	@Override
	public void run() {
		Object[] obj;
		
		while(Boolean.TRUE) {
			try {
				clientSpace.get(new ActualField(playerID), new ActualField(ServerCommands.newConnection));
				obj = clientSpace.get(new ActualField(playerID), new FormalField(Uri.class));
				Log.important("Received newConnection message and an URI.");
				handler.newConnection(((Uri)obj[1]).getUri());
			} catch (InterruptedException e) {
				Log.important("Thread interrupted!");
				e.printStackTrace();
			}
		}
	}
}
