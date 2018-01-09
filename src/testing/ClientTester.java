package testing;

import java.io.IOException;

import Objects.BoardState;
import Objects.CardOption;
import Objects.OotAction;
import cards.Card;
import cards.CardReader;
import client.ClientActions;
import client.ClientActions.*;

public class ClientTester {
	public static void main(String[] args) {
		
		
		
		try {
			CardReader cards = new CardReader();
			Card[] card = {cards.getBase().get(4),cards.getBase().get(7),cards.getBase().get(10),cards.getBase().get(13)};
			
			ClientActions.playerHand(card);
			
			CardOption test2 = new CardOption("Select cards to discard", 3, card);
			ClientActions.playerSelect(test2);
			
			OotAction test1 = new OotAction("Discard down to three cards", 2);
			ClientActions.nonTurnAction(test1);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//ClientActions.playerHand();
		
	}
}
