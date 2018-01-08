package engine;

import java.util.ArrayList;
import java.util.HashMap;

import cards.Card;
import log.Log;

public class Board
{
	private HashMap<String, Object[]> shop = new HashMap<String, Object[]>();
	public Board(int playerCount, ArrayList<Card> cards, ArrayList<Card> setupCards)
	{
		Log.important("Setting up supply piles:");		
		int special = (playerCount == 2) ? 8 : 12;
		int[] cardSize = {60-(7*playerCount), 40, 30, (playerCount - 1)*10, special, special, special};
		Card card;
		for (int i = 0; i < setupCards.size(); i++)
		{
			card = setupCards.get(i);
			Object[] input = {cardSize[i], card};
			shop.put(card.getName(), input);
			Log.log("Setup Card name: " + card.getName() + " copies: " + cardSize[i]);
		}

		for (Card card2 : cards)
		{
			Object[] input = {10, card2};
			shop.put(card2.getName(), input);
			Log.log("Setup Card name: " + card2.getName() + " copies: 10");
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
				Log.important("Card removed: "+ cardName + " copies left: " + array[0]);
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
	/**
	 * Returns the amount of cards on the board including setup cards etc.
	 * @return shop.size();
	 */
	public int getBoardSize()
	{
		return shop.size();
	}
	/**
	 * Gets the name of every card in the game and returns it as an array
	 * @return card names
	 */
	public String[] getBoardNamesArray()
	{
		return shop.keySet().toArray(new String[getBoardSize()]);
	}
	/**
	 * Gets the name of every card in the game and returns it as a list
	 * @return card names
	 */
	public ArrayList<String> getBoardNamesList()
	{
		return new ArrayList<String> (shop.keySet());
	}

	/**
	 * Checks if the game is over by looking at the following board conditions -
	 * <br><b>The Provinces supply pile is empty.</b>
	 * <br><b>Three different supply piles are empty.</b>
	 * @return
	 */
	public boolean checkEnd() {
		int count = 0;
		if (canBuy("Province") == null)
		{
			Log.important("Province pile is empty");
			return true;
		}
			
		for (String cardName : shop.keySet())
		{
			if (canBuy(cardName) == null)
			{
				Log.important("Empty supply pile: " + cardName);
				count++;
			}
				
		}
		return count >= 3;
		
	}

}
