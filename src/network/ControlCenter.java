package network;

import org.jspace.ActualField;
import org.jspace.Space;

public class ControlCenter extends Thread {
	
	private Space clientSpace, safeSpace;
	
	public ControlCenter(Space clientSpace, Space safeSpace){
		
		this.clientSpace = clientSpace;
		this.safeSpace = safeSpace;
		
	}
	
	public void run(){
		
		String name;
		ClientCommands cmd;
		
		while(true){
			
			try {
				clientSpace.get(new ActualField(name), new ActualField(cmd) );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			switch(cmd){
			
				default:
			}
			
		}
		
		
	}

}
