package network;

import org.jspace.ActualField;
import org.jspace.Space;

import cards.Card;

public class ControlCenter extends Thread {
	
	private Space clientSpace, safeSpace;
	
	public ControlCenter(Space clientSpace, Space safeSpace){
		
		this.clientSpace = clientSpace;
		this.safeSpace = safeSpace;
		
	}
	
	public void run(){
		
		String name;
		ClientCommands cmd;
		Card card;
		int index;
		
		while(true){
			
			try {
				clientSpace.get(new ActualField(name), new ActualField(cmd));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			switch(cmd){
				buyCard:
					clientSpace.get(new ActualField(card));
					
					safeSpace.put(name, cmd);
					safeSpace.put(card);
				playCard:
					clientSpace.get(new ActualField(index));
				
					safeSpace.put(name, cmd);
					safeSpace.put(index);
				selectCard:
					clientSpace.get(new ActualField(index));
				
					safepace.put(name, cmd);
					safeSpace.put(index);	
				default:
					safeSpace.put(name, cmd);
			}
			
		}
		
		
	}

}
