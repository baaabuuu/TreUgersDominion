package clientUI;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import objects.ServerCommands;

public class Consumer implements Runnable {
	private Space userSpace;
	private UIController controller;
	
	public Consumer(UIController controller, Space userSpace) {
		this.userSpace = userSpace;
		this.controller = controller;
	}
	
	@Override
	public void run() {
		
		Object[] objs;
		while(true) {
			try {
				objs = userSpace.get(new ActualField("client"), new FormalField(String.class),
						new FormalField(String.class));
				
				switch ((String)objs[1]) {
					case "message":
						controller.eventInput((String)objs[2]);
						break;
					case "chat": //Implement chat
						break;
					default: break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
