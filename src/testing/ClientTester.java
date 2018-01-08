package testing;

import client.ClientActions;
import client.ClientActions.*;
import Objects.BoardState;
import Objects.OotAction;

public class ClientTester {
	public static void main(String[] args) {
		
		OotAction test1 = new OotAction("Hello World", 1);
		ClientActions.nonTurnAction(test1);
		
		
	}
}
