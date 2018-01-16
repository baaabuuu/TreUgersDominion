package network;

import org.jspace.Space;
import org.jspace.Tuple;

import objects.ServerCommands;

public class Writer {

	private Space clientSpace;
	String[] players;
	private static int port = 8181;
	private static String host = "localhost";
	private static String uri = "tcp://"+ host + ":" + port + "/?keep";
	
	
	
	public Writer(Space clientSpace, String[] players){
	
		this.clientSpace = clientSpace;
		this.players = players;
	}
	
	public void sendMessage(Tuple tuple) throws InterruptedException{

		ServerCommands cmd = tuple.getElementAt(ServerCommands.class, 1);
		
		switch(cmd){
		case newConnection:
			clientSpace.put(tuple.getElementAt(Integer.class, 0), tuple.getElementAt(ServerCommands.class, 1));
			clientSpace.put(tuple.getElementAt(Integer.class, 0), tuple.getElementAt(ServerCommands.class, 1), uri);
		case takeTurn: 
			clientSpace.put(tuple.getElementAt(Integer.class, 0), tuple.getElementAt(ServerCommands.class, 1));
		default:	
			clientSpace.put(tuple.getElementAt(Integer.class, 0), tuple.getElementAt(ServerCommands.class, 1));
			clientSpace.put(tuple);
		}
	}
	
	
	
	
	
	
}
