package engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import cards.Card;
import cards.CardReader;


public class GameTest
{	
	private Game game;
	private Board board;
	ArrayList<Card> cards;
	ArrayList<Card> treasures;
	
	@Before
	public void createObjects() throws IOException
	{
		cards = new ArrayList<Card>();
		ArrayList<Card> setup = new CardReader().getBase();
		for (int i = 0; i < 10; i++)
		{
			cards.add(setup.get(i));
		}
		
		setup = new CardReader().getSetup();
		treasures = new ArrayList<Card>();
		for (int i = 0; i < 7; i++)
		{
			treasures.add(setup.get(i));
		}
		
		String[] names = {"Test Person1", "Test Person2"};
		
		board = new Board(2, cards, treasures);
		game = new Game(board, names, names.length, 0);
	}
	
	@Test
	public void create2Player()
	{
		String[] names = {"Test Person1", "Test Person2"};
		assertEquals("Treasure size: ", treasures.size(), 7);
		Board board = new Board(2, cards, treasures);
		new Game(board, names, names.length, 0);
	}
	
	@Test
	public void create3Player()
	{
		String[] names = {"Test Person1", "Test Person2", "Test Person3"};
		Board board = new Board(3, cards, treasures);
		new Game(board, names, names.length, 0);
	}
	
	@Test
	public void create4Player()
	{
		String[] names = {"Test Person1", "Test Person2", "Test Person3", "Test Person4"};
		Board board = new Board(4, cards, treasures);
		new Game(board, names, names.length, 0);
	}
	
	@Test(expected = NullPointerException.class)
	public void createGame() throws IOException
	{
		new Game(null, null, 0, 0);
	}
	
	@Test
	public void nextPhase()
	{
		game.nextPhase();
		assertEquals("Current phase: 1", game.getPhase(), 1);
		game.nextPhase();
		assertEquals("Current phase: 0", game.getPhase(), 0);
	}
	
	@Test
	public void newTurn()
	{
		assertEquals("Current turn: 0", game.getTurn(), 0);
		game.newTurn();
		assertEquals("Current turn: 1", game.getTurn(), 1);
		game.newTurn();
		assertEquals("Current turn: 0", game.getTurn(), 0);
	}
	
	@Test
	public void newTurnEnd()
	{
		when(board.checkEnd()).thenReturn(true);
		boolean checkTurn = game.newTurn();
		assertTrue("Game is over", checkTurn);
	}
}