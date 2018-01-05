package engine;

import java.util.ArrayList;
import java.util.HashMap;

import cards.Card;

public class Board
{
	HashMap<String, Object[]> shop = new HashMap<String, Object[]>();
	ArrayList<Card> additionalCards;
	int playerCount = 0;
	public Board(String[] playerNames, int playerCount, ArrayList<Card> cards, ArrayList<Card> setupCards)
	{
		this.playerCount = playerCount;
		Log.important("setting up buying area:");		
		int special = (playerCount == 2) ? 8 : 12;
		int[] cardSize = {60-(7*playerCount), 40, 30, (playerCount - 1)*10, special, special, special};
		Card card;
		Log.log("Treasures, Curse cards and Victory Cards being setup.");
		for (int i = 0; i < setupCards.size(); i++)
		{
			card = setupCards.get(i);
			Object[] input = {cardSize[i], card};
			shop.put(card.getName(), input);
			Log.log("Card name: " + card.getName() + " count: " + cardSize[i]);
		}

		for (Card card2 : cards)
		{
			Object[] input = {10, card2};
			shop.put(card2.getName(), input);
			Log.log("Card name: " + card2.getName() + " count: 10");
		}
		
	}

}
