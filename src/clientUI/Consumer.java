package clientUI;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

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
				objs = userSpace.get(new ActualField("UI"), new FormalField(String.class),
						new FormalField(String.class));
				
				switch ((String)objs[1]) {
					//Currently unused, as the client will call the eventInput function directly
					//instead of going through jSpace.
					case "message":
						controller.eventInput((String)objs[2]);
						break;
					//For later use if chat is to be implemented.
					case "chat": 
						break;
					default: break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
