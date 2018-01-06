package engine;

import java.util.ArrayList;
import java.util.HashMap;

import cards.Card;

public class Board
{
	public HashMap<String, Object[]> shop = new HashMap<String, Object[]>();
	public ArrayList<Card> additionalCards;
	public int playerCount = 0;
	public Board(int playerCount, ArrayList<Card> cards, ArrayList<Card> setupCards)
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
	
	public void cardRemove(String cardName)
	{
		if (shop.containsKey(cardName))
		{
			Object[] array = shop.get(cardName);
			if ((int) array[0] > 0)
			{
				array[0] = (int) array[0] - 1;
				shop.put(cardName, array);
				Log.important("card: "+ cardName + " cards left: " + array[0]);
			}
			Log.important("Cannot remove " + cardName + " no more left");	
		} 
		else
		{
			Log.important("Cannot remove " + cardName + " card not on map");
		}
	}
	
	public Card canBuy(String cardName)
	{
		if (shop.containsKey(cardName))
		{
			Object[] array = shop.get(cardName);
			if ((int) array[0] > 0)
			{
				return (Card) array[1];
			}
			Log.important("Cannot buy " + cardName + " no more left");	
		} 
		else
		{
			Log.important("Cannot buy " + cardName + " card not on map");
		}
		return null;
	}

}
