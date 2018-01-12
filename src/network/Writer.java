package network;

import org.jspace.Space;
import org.jspace.Tuple;

public class Writer {

	private Space clientSpace;
	String[] players;
	
	public Writer(Space clientSpace, String[] players){
	
		this.clientSpace = clientSpace;
		this.players = players;
	}
	
	public void sendMessage(Tuple tuple) throws InterruptedException{
	
		clientSpace.put(tuple.getElementAt(0), tuple.getElementAt(1));
		clientSpace.put(tuple);
		
	}
	
	public void sendMessageToOthers(Tuple tuple) throws InterruptedException{
		
		for(String tempPlayer:players){
			if(tuple.getElementAt(0) != tempPlayer){
				clientSpace.put(tuple.getElementAt(0), tuple.getElementAt(1));
				clientSpace.put(tuple);
			}
		}
		
	}
	
	
	
	
}
