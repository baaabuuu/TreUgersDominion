package network;

import org.jspace.FormalField;
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
		
		String name = "";
		ClientCommands cmd = null;
		
		while(true){
			
			try {
				Object[] firstInput = clientSpace.get(new FormalField(String.class), new FormalField(ClientCommands.class));
				
				name = (String) firstInput[0];
				cmd = (ClientCommands) firstInput[1];
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			Object[] secondInput;
			try {
				switch(cmd)
				{
				case buyCard:
						secondInput = clientSpace.get(new FormalField(Card.class));
						
						safeSpace.put(name, cmd);
						safeSpace.put(name,(Card) secondInput[0]);
				case playCard:
				case selectCard:
						secondInput = clientSpace.get(new FormalField(Integer.class));
					
						safeSpace.put(name, cmd);
						safeSpace.put(name, (int) secondInput[0]);
				
				default:
						safeSpace.put(name, cmd);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}

}
