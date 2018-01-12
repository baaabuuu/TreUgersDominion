package testing;

import java.io.IOException;

import cards.Card;
import cards.CardReader;
import client.ClientActions;
import objects.BoardState;
import objects.CardOption;
import objects.OotAction;

public class ClientTester {
	public static void main(String[] args) {
		try {
			
			ClientActions action = new ClientActions(null);
			CardReader cards = new CardReader();
			Card[] card = {cards.getBase().get(4),cards.getBase().get(7),cards.getBase().get(10),cards.getBase().get(13)};
			
			//action.setPlayerHand(card);
			
			//CardOption test2 = new CardOption("Select cards to discard", 3, card);
			//action.playerSelect(test2, null);
			
			OotAction test1 = new OotAction("Discard down to three cards", 2);
			action.nonTurnAction(test1, null);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//ClientActions.playerHand();
		
	}
}
