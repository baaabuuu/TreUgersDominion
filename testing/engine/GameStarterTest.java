package engine;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import network.Writer;
import cards.CardReader;

/**
 * Used to test the GameStarter in engine
 * @author s164166
 */
public class GameStarterTest
{	
	@Test(expected = NullPointerException.class) 
	public void createStartGameNullThrown() throws IOException
	{
		new GameStarter(0, null, null, null, null, null, null);
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
		Space jSpace = new SequentialSpace();
		Space safeSpace = new SequentialSpace();
		Writer writer = new Writer(jSpace, playerNames);
		GameStarter game =  new GameStarter(playerCount, playerNames, cards, sets, rand, writer, safeSpace);
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
		Space jSpace = new SequentialSpace();
		Space safeSpace = new SequentialSpace();
		Writer writer = new Writer(jSpace, playerNames);
		GameStarter game =  new GameStarter(playerCount, playerNames, cards, sets, rand, writer, safeSpace);
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
		Space jSpace = new SequentialSpace();
		Space safeSpace = new SequentialSpace();
		Writer writer = new Writer(jSpace, playerNames);
		new GameStarter(playerCount, playerNames, cards, sets, rand, writer, safeSpace);
	}
	
	@Test 
	public void createStartGameNoExapnsions() throws IOException
	{
		ArrayList<String> sets = new ArrayList<String>();
		int playerCount = 2;
		String[] playerNames = {"Test Person1", "Test person2"};
		CardReader cards = new CardReader();
		Random rand = new Random();
		Space jSpace = new SequentialSpace();
		Space safeSpace = new SequentialSpace();
		Writer writer = new Writer(jSpace, playerNames);
		GameStarter game =  new GameStarter(playerCount, playerNames, cards, sets, rand, writer, safeSpace);
		assertNotNull("Game is not equal to null", game);
	}
	


}
