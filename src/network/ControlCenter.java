package network;


import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import log.Log;
import objects.ArrayListObject;
import objects.ClientCommands;

public class ControlCenter extends Thread {
	
	private Space safeSpace;
	
	private Space clientSpace;
	
	public ControlCenter(Space clientSpace, Space safeSpace){
		
		this.clientSpace = clientSpace;
		this.safeSpace = safeSpace;
		
	}
	
	public void run(){
		
		int id = -1;
		ClientCommands cmd = null;
		
		while(true){
			
			try {
				Log.log("Looking for message");
				Object[] firstInput = clientSpace.get(new FormalField(Integer.class), new FormalField(ClientCommands.class));
				
				id = (Integer) firstInput[0];
				cmd = (ClientCommands) firstInput[1];
				
				
				Log.log("Found command: " + cmd.toString() + " from ID: " + id);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			Object[] secondInput;
			try {
				switch(cmd)
				{
				case buyCard:
						Log.log("Finding string for the bought card");
						secondInput = clientSpace.get(new ActualField(id), new FormalField(String.class));
						
						Log.log("Found string: \"" + (String) secondInput[1] + "\". Sending to server Space");
						
						safeSpace.put(id, cmd, (String) secondInput[1]);
						break;
						
				case playCard:
					Log.log("Finding int for the played card.");
					secondInput = clientSpace.get(new ActualField(id), new FormalField(Integer.class));
					
					Log.log("Found int: \"" + (Integer) secondInput[1] + "\". Sending to server Space");
					
					safeSpace.put(id, cmd, (Integer) secondInput[1]);
					break;
					
				case selectCard:

						Log.log("Finding arraylist for the played card.");
						secondInput = clientSpace.get(new ActualField(id), new FormalField(ArrayListObject.class));
					
						Log.log("Found arraylist: \"" + (((ArrayListObject) secondInput[1]).getArrayList()).toString() + "\" sending to server Space");
						
						safeSpace.put(id, cmd, ((ArrayListObject) secondInput[1]));
						break;
						
				case changePhase:
					Log.log("Sending change phase to server space");
					safeSpace.put(id, cmd);
					break;
				default:
						Log.important("Unknown command. Ignoring");
						break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}

}
