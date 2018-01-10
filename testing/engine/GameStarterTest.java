package engine;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

import cards.CardReader;

public class GameStarterTest
{	
	@Test(expected = NullPointerException.class) 
	public void createStartGameNullThrown() throws IOException
	{
		new GameStarter(0, null, null, null, null);
	}
	
	@Test 
	public void createStartGame() throws IOException
	{
		ArrayList<String> sets = new ArrayList<String>();
		sets.add("base");
		int playerCount = 2;
		String[] playerNames = {"Test Person1", "Test person2"};
		CardReader cards = new CardReader();
		Random rand = new Random();
		GameStarter game =  new GameStarter(playerCount, playerNames, cards, sets, rand);
		assertNotNull("Game is not equal to null", game);
	}
	
	@Test 
	public void createStartGameInvalidSet() throws IOException
	{
		ArrayList<String> sets = new ArrayList<String>();
		sets.add("base");
		sets.add("INVALID");
		int playerCount = 2;
		String[] playerNames = {"Test Person1", "Test person2"};
		CardReader cards = new CardReader();
		Random rand = new Random();
		GameStarter game =  new GameStarter(playerCount, playerNames, cards, sets, rand);
		assertNotNull("Game is not equal to null", game);
	}
	
	@Test(expected = NullPointerException.class) 
	public void createStartGameOnlyInvalidSet() throws IOException
	{
		ArrayList<String> sets = new ArrayList<String>();
		sets.add(null);
		int playerCount = 2;
		String[] playerNames = {"Test Person1", "Test person2"};
		CardReader cards = new CardReader();
		Random rand = new Random();
		new GameStarter(playerCount, playerNames, cards, sets, rand);
	}
	
	@Test 
	public void createStartGameNoExapnsions() throws IOException
	{
		ArrayList<String> sets = new ArrayList<String>();
		int playerCount = 2;
		String[] playerNames = {"Test Person1", "Test person2"};
		CardReader cards = new CardReader();
		Random rand = new Random();
		GameStarter game =  new GameStarter(playerCount, playerNames, cards, sets, rand);
		assertNotNull("Game is not equal to null", game);
	}
	


}
