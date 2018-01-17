package network;

import java.util.ArrayList;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import cards.Card;
import log.Log;
import objects.ClientCommands;

public class ControlCenter extends Thread {
	
	private Space clientSpace, safeSpace;
	
	public ControlCenter(Space clientSpace, Space safeSpace){
		
		this.clientSpace = clientSpace;
		this.safeSpace = safeSpace;
		
	}
	
	public void run(){
		
		int id = -1;
		ClientCommands cmd = null;
		
		while(true){
			
			try {
				Object[] firstInput = clientSpace.get(new FormalField(ClientCommands.class), new FormalField(Integer.class));
				
				cmd = (ClientCommands) firstInput[0];
				id = (Integer) firstInput[1];
				
				Log.log("Found command: " + cmd.toString() + " from ID: " + id);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			Object[] secondInput;
			try {
				switch(cmd)
				{
				case buyCard:
						Log.log("Finding string for the bought card");
						secondInput = clientSpace.get(new ActualField(id), new FormalField(String.class));
						
						Log.log("Found string: " + (String) secondInput[1] + " sending to server Space");
						
						safeSpace.put(id, cmd, (String) secondInput[1]);
						break;
						
				case playCard:
					Log.log("Finding int for the played card.");
					secondInput = clientSpace.get(new ActualField(id), new FormalField(Integer.class));
					
					Log.log("Found int: " + (Integer) secondInput[1] + " sending to server Space");
					
					safeSpace.put(id, cmd, (Integer) secondInput[1]);
					break;
					
				case selectCard:

						Log.log("Finding arraylist for the played card.");
						secondInput = clientSpace.get(new ActualField(id), new FormalField(ArrayList.class));
					
						Log.log("Found int: " + ((ArrayList<Integer>) secondInput[1]).toString() + " sending to server Space");
						
						safeSpace.put(id, cmd, (ArrayList<Integer>) secondInput[1]);
						break;
						
				case changePhase:
					Log.log("Sending change phase to server space");
					safeSpace.put(id, cmd);
					break;
				default:
						Log.log("Unknown command. Ignoring");
						break;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}

}
