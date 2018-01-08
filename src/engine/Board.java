package engine;

import java.util.ArrayList;
import java.util.HashMap;

import cards.Card;
import log.Log;

public class Board
{
	private HashMap<String, Object[]> board = new HashMap<String, Object[]>();
	
	/**
	 * Creates the board, needs playercount, cards, and setupCards
	 * @param playerCount
	 * @param cards
	 * @param setupCards
	 */
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
			board.put(card.getName(), input);
			Log.log("Setup Card name: " + card.getName() + " copies: " + cardSize[i]);
		}

		for (Card card2 : cards)
		{
			Object[] input = {10, card2};
			board.put(card2.getName(), input);
			Log.log("Setup Card name: " + card2.getName() + " copies: 10");
		}
	}
	/**
	 * Remove the card if it contains
	 * @param cardName
	 */
	public void cardRemove(String cardName)
	{
		if (board.containsKey(cardName))
		{
			Object[] array = board.get(cardName);
			if ((int) array[0] > 0)
			{
				array[0] = (int) array[0] - 1;
				board.put(cardName, array);
				Log.important("Card removed: "+ cardName + " copies left: " + array[0]);
			}
			Log.important("Cannot remove " + cardName + " no more left");	
		} 
		else
		{
			Log.important("Cannot remove " + cardName + " card not on map");
		}
	}
	
	/**
	 * If a card is in the supply pile and has more than 0 copies left, return the card.
	 * @param cardName
	 * @return
	 */
	public Card canBuy(String cardName)
	{
		if (board.containsKey(cardName))
		{
			Object[] array = board.get(cardName);
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
		return board.size();
	}
	
	/**
	 * Gets the name of every card in the game and returns it as an array
	 * @return card names
	 */
	public String[] getBoardNamesArray()
	{
		return board.keySet().toArray(new String[getBoardSize()]);
	}
	
	/**
	 * Gets the name of every card in the game and returns it as a list
	 * @return card names
	 */
	public ArrayList<String> getBoardNamesList()
	{
		return new ArrayList<String> (board.keySet());
	}

	/**
	 * Checks if the game is over by looking at the following board conditions -
	 * <br><b>The Provinces supply pile is empty.</b>
	 * <br><b>Three different supply piles are empty.</b>
	 * @return
	 */
	public boolean checkEnd() 
	{
		int count = 0;
		if (canBuy("Province") == null)
		{
			Log.important("Province pile is empty");
			return true;
		}
		for (String cardName : board.keySet())
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
