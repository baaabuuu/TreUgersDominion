package engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import cards.Card;
import cards.CardReader;
import log.Log;

/**
 * Starts a game
 * <p>TODO - Threading, pass this into some sorta lobby mechanism.
 * <p>TODO - also get rid of main, dummyGame setup game properly
 * <p>TODO - Networking. Need to establish how network communication is done.
 * @author s164166
 */
public class GameStarter
{
	private Board board;
	private Game game;
	private CardReader cards;
	private ArrayList<Card> gameCards = new ArrayList<Card>();
	private Random random;
	
	public static void main(String[] args) throws IOException
	{
		String[] playerNames = {"Hillary Rodham Clinton", "Donald J. Trump"};
		ArrayList<String> expansions = new ArrayList<String>();
		expansions.add("base");
		new GameStarter(2, playerNames, new CardReader(), expansions, new Random()).startDummyGame();
	}
	/**
	 * @param cards
	 * @param random2 
	 * @throws IOException
	 */
	public GameStarter(int playerCount, String[] playerNames, CardReader cards, ArrayList<String> sets, Random random) throws IOException
	{
		this.cards = cards;
		this.random = random;
		setupRandomCards(sets);
		board = new Board(playerCount, gameCards, cards.getSetup());
		game = new Game(board, playerNames, playerCount, random.nextInt(playerCount));
	}
	
	/**
	 * Starts a dummy game running on the console.
	 */
	public void startDummyGame()
	{
		//game.dummyGame();
	}
	
	/**
	 * Starts a game
	 */
	public void startGame()
	{
		game.start();
	}
	
	/**
	 * Gets the card.
	 * @return
	 */
	public Game getGame()
	{
		return game;
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
		if (unrandomized.size() == 0)
		{
			Log.important("No sets were selected - adding base set");
			for (Card card : cards.getBase())
				unrandomized.add(card);
		}
		Log.important("Selecting 10 random cards from card set.");
		int index;
		Card card;
		for (int i = 0; i < 10; i++)
		{
			index = random.nextInt(unrandomized.size());
	        card = unrandomized.get(index);
	        unrandomized.remove(index);
	        gameCards.add(card);
	        Log.log("Card selected: " +  card.getName());
		}
		Log.important("Done selecting cards.");
	}
	
	

}
