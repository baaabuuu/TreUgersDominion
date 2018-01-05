package engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import cards.Card;
import cards.CardReader;

public class StartGame
{
	
	private Board board;
	private int playerCount;
	private String[] playerNames;
	private CardReader cards;
	private ArrayList<Card> gameCards = new ArrayList<Card>();
	private Random random;
	
	public static void main(String[] args) throws IOException
	{
		String[] playerNames = {"Hillary Clinton", "Donald J. Trump"};
		ArrayList<String> expansions = new ArrayList<String>();
		expansions.add("base");	
		new StartGame(2, playerNames, new CardReader(), expansions, new Random());
	}
	/**
	 * @param cards
	 * @param random2 
	 * @throws IOException
	 */
	public StartGame(int playerCount, String[] playerNames, CardReader cards, ArrayList<String> sets, Random random) throws IOException
	{
		this.playerCount = playerCount;
		this.playerNames = playerNames;
		this.cards = cards;
		this.random = random;
		setupRandomCards(sets);
		board = new Board(playerNames, playerCount, gameCards, cards.getSetup());
	}
	
	/**
	 * Randomizes based on the sets used, add more sets to allow for more
	 */
	public void setupRandomCards(ArrayList<String> cardSets)
	{
		ArrayList<Card> unrandomized = new ArrayList<Card>();
		for (String expansionName : cardSets)
		{
			switch(expansionName)
			{
				case("base") :
					for (Card card : cards.getBase())
						unrandomized.add(card);
					break;
				default :
					break;
			}
		}
		Log.important("StartGame Object -> Selecting 10 random cards from card set.");
		int index;
		Card card;
		for (int i = 0; i < 10; i++)
		{
			index = random.nextInt(unrandomized.size());
	        card = unrandomized.get(index);
	        unrandomized.remove(index);
	        Log.log(card.getName());
		}
		Log.important("Done selecting cards.");
	}
	
	

}
