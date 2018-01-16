package testing;

import java.io.IOException;

import cards.Card;
import cards.CardReader;
import client.ClientActions;
import objects.BoardState;
import objects.CardOption;

public class ClientTester {
	public static void main(String[] args) {
		try {
			
			//ClientActions action = new ClientActions(null, null, null);
			CardReader cards = new CardReader();
			Card[] card = {cards.getBase().get(4),cards.getBase().get(7),cards.getBase().get(10),cards.getBase().get(13)};
			
			//action.setPlayerHand(card);
			
			//CardOption test2 = new CardOption("Select cards to discard", 3, card);
			//action.playerSelect(test2, null);
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//ClientActions.playerHand();
		
	}
}
