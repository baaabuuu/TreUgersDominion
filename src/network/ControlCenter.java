package network;

import org.jspace.ActualField;
import org.jspace.Space;

import cards.Card;
import objects.ClientCommands;

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
			
			switch(cmd)
			{
			case buyCard:
					clientSpace.get(new ActualField(card));
					
					safeSpace.put(name, cmd);
					safeSpace.put(card);
			case playCard:
					clientSpace.get(new ActualField(index));
				
					safeSpace.put(name, cmd);
					safeSpace.put(index);
			case selectCard:
					clientSpace.get(new ActualField(index));
					safeSpace.put(name, cmd);
					safeSpace.put(index);	
			default:
					safeSpace.put(name, cmd);
			}
			
		}
		
		
	}

}
