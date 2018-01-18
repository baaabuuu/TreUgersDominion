package engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import cards.Card;
import cards.CardReader;
import network.Writer;
import objects.ClientCommands;
import objects.ServerCommands;

/**
 * Used to test the game engine
 * @author s164166
 */
public class GameTest
{	
	private Game game;
	private Board board;
	ArrayList<Card> cards;
	ArrayList<Card> treasures;
	@Mock
	private Space safeSpace;
	
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
		Space jSpace = new SequentialSpace();
		Space safeSpace = new SequentialSpace();
		Writer writer = new Writer(jSpace, names);
		board = new Board(2, cards, treasures);
		game = new Game(board, names, names.length, 0, writer, safeSpace);
	}
	
	@Test
	public void create2Player()
	{
		String[] names = {"Test Person1", "Test Person2"};
		assertEquals("Treasure size: ", treasures.size(), 7);
		Board board = new Board(2, cards, treasures);
		Space jSpace = new SequentialSpace();
		Space safeSpace = new SequentialSpace();
		Writer writer = new Writer(jSpace, names);
		new Game(board, names, names.length, 0, writer, safeSpace);
	}
	
	@Test
	public void create3Player()
	{
		String[] names = {"Test Person1", "Test Person2", "Test Person3"};
		Board board = new Board(3, cards, treasures);
		Space jSpace = new SequentialSpace();
		Space safeSpace = new SequentialSpace();
		Writer writer = new Writer(jSpace, names);
		new Game(board, names, names.length, 0, writer, safeSpace);
	}
	
	@Test
	public void create4Player()
	{
		String[] names = {"Test Person1", "Test Person2", "Test Person3", "Test Person4"};
		Board board = new Board(4, cards, treasures);
		Space jSpace = new SequentialSpace();
		Space safeSpace = new SequentialSpace();
		Writer writer = new Writer(jSpace, names);
		new Game(board, names, names.length, 0, writer, safeSpace);
	}
	
	@Test(expected = NullPointerException.class)
	public void createGame() throws IOException
	{
		new Game(null, null, 0, 0, null, null);
	}
	
	@Test
	public void sendDisconnect() throws InterruptedException
	{
		game.sendDisconnect(0);
		assertFalse("Player is not connected", game.getPlayer(0).isConnected());
		//Try to disconnect them again - will not change anything
		game.sendDisconnect(0);
		assertFalse("Player is STILL not connected", game.getPlayer(0).isConnected());
	}
	
	@Test
	public void sendInvalidConnected() throws InterruptedException
	{
		game.sendInvalid("Testing invalid", 0);
	}
	
	@Test
	public void sendInvalidDisconnected() throws InterruptedException
	{
		game.getPlayer(0).setConnected(Boolean.FALSE);
		game.sendInvalid("Testing invalid", 0);
	}
	
	@Test
	public void sendMessage() throws InterruptedException
	{
		game.sendMessage("sending hello", 0);
	}
	
	@Test
	public void sendMessageDisconnected() throws InterruptedException
	{
		game.getPlayer(0).setConnected(Boolean.FALSE);
		game.sendMessage("sending hello", 0);
	}
	
	@Test
	public void sendMessageAllDisconnected() throws InterruptedException
	{
		game.getPlayer(0).setConnected(Boolean.FALSE);
		game.sendMessageAll("sending hello all");
	}
	
	@Test
	public void sendMessageAll() throws InterruptedException
	{
		game.sendMessageAll("sending hello all");
	}
	
	@Test
	public void sendBoardState() throws InterruptedException
	{
		game.sendBoardState();
	}
	
	@Test
	public void sendBoardStateDisconnected() throws InterruptedException
	{
		game.getCurrentPlayer().setConnected(Boolean.FALSE);
		game.sendBoardState();
	}
	
	@Test
	public void sendCardOptionMayFalse() throws InterruptedException
	{
		game.sendCardOption(0, "testing", 10, new ArrayList<Card> (), false);
	}
	
	@Test
	public void sendCardOptionMayTrue() throws InterruptedException
	{
		game.sendCardOption(0, "testing", 10, new ArrayList<Card> (), true);
	}
	
	@Test
	public void sendCardOptionDisconnected() throws InterruptedException
	{
		game.getCurrentPlayer().setConnected(Boolean.FALSE);
		game.sendCardOption(0, "testing", 10, new ArrayList<Card> (), true);
	}
	
	@Test
	public void sendPlayerHand() throws InterruptedException
	{
		game.sendPlayerHand(0, 0);
	}
	
	@Test
	public void sendPlayerHandDisconnected() throws InterruptedException
	{
		game.getCurrentPlayer().setConnected(Boolean.FALSE);
		game.sendPlayerHand(0, 0);
	}
	
	@Test
	public void gameStart() throws InterruptedException, IOException
	{
		MockitoAnnotations.initMocks(this);
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
		Space jSpace = new SequentialSpace();
		safeSpace = mock(SequentialSpace.class);
		Object[] newObject = new Object[1];
		when(safeSpace.get(new ActualField(ServerCommands.gameStart))).thenReturn(newObject);
		Writer writer = new Writer(jSpace, names);
		board = new Board(2, cards, treasures);
		game = new Game(board, names, names.length, 0, writer, safeSpace);
		game.start();
	}
	
	@Test
	public void gameStartGameEnd1() throws InterruptedException, IOException
	{
		MockitoAnnotations.initMocks(this);
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
		Space jSpace = new SequentialSpace();
		safeSpace = mock(SequentialSpace.class);
		Object[] newObject = new Object[1];
		when(safeSpace.get(new ActualField(ServerCommands.gameStart))).thenReturn(newObject);
		Writer writer = new Writer(jSpace, names);
		board = new Board(2, cards, treasures);
		game = new Game(board, names, names.length, 0, writer, safeSpace);
		Card province = board.canGain("Province");
		game.getPlayer(0).addMoney(5000);
		game.getPlayer(0).addBuys(5000);
		game.getPlayer(0).buy(province, 1);
		for (int i = board.getCopiesLeft("Province"); i > 0; i--)
		{
			board.cardRemove("Province");
		}
		game.start();
	}
	
	@Test
	public void gameStartGameEnd2() throws InterruptedException, IOException
	{
		MockitoAnnotations.initMocks(this);
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
		Space jSpace = new SequentialSpace();
		safeSpace = mock(SequentialSpace.class);
		Object[] newObject = new Object[1];
		when(safeSpace.get(new ActualField(ServerCommands.gameStart))).thenReturn(newObject);
		Writer writer = new Writer(jSpace, names);
		board = new Board(2, cards, treasures);
		game = new Game(board, names, names.length, 0, writer, safeSpace);
		Card province = board.canGain("Province");
		game.getPlayer(0).addMoney(5000);
		game.getPlayer(0).addBuys(5000);
		game.getPlayer(0).buy(province, 1);
		for (int i = board.getCopiesLeft("Gold"); i > 0; i--)
		{
			board.cardRemove("Gold");
		}
		for (int i = board.getCopiesLeft("Estate"); i > 0; i--)
		{
			board.cardRemove("Estate");
		}
		for (int i = board.getCopiesLeft("Duchy"); i > 0; i--)
		{
			board.cardRemove("Duchy");
		}
		game.start();
	}
	
	@Test
	public void gameStartPlayCopper() throws InterruptedException, IOException
	{
		MockitoAnnotations.initMocks(this);
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
		Space jSpace = new SequentialSpace();
		safeSpace = mock(SequentialSpace.class);
		Object[] newObject = new Object[1];
		
		Writer writer = new Writer(jSpace, names);
		board = new Board(2, cards, treasures);
		game = new Game(board, names, names.length, 0, writer, safeSpace);
		
		when(safeSpace.get(new ActualField(ServerCommands.gameStart))).thenReturn(newObject);
		when(safeSpace.getp(new FormalField(Integer.class), new ActualField(ClientCommands.playCard),
				new FormalField(Integer.class)))
		.thenAnswer( new Answer<Object>()
		{
			private int count = 0;
			private Object[] ob1 = null;
			private Object[] ob2 = {0, 0, game.getPlayer(0).getFirstIndexOf("Copper")};
			
			@Override
			public Object answer(InvocationOnMock arg0) throws Throwable
			{
				if (count++ == 2)
				{
					return ob2;
				}
				return ob1;
			}
		});
		when(safeSpace.getp(new FormalField(Integer.class), new ActualField(ClientCommands.changePhase)))
		.thenAnswer( new Answer<Object>()
		{
			private int count = 0;
			private Object[] ob1 = {0, 0};
			private Object[] ob2 = null;
			
			@Override
			public Object answer(InvocationOnMock arg0) throws Throwable {
				count++;
				if (count < 2 || count == 4 || count == 5)
				{
					return ob1;
				}
				return ob2; 
			}
		});
		game.start();
	}
	
	@Test
	public void gameStartBuyCurse() throws InterruptedException, IOException
	{
		MockitoAnnotations.initMocks(this);
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
		Space jSpace = new SequentialSpace();
		safeSpace = mock(SequentialSpace.class);
		Object[] newObject = new Object[1];
		
		Writer writer = new Writer(jSpace, names);
		board = new Board(2, cards, treasures);
		game = new Game(board, names, names.length, 0, writer, safeSpace);
		
		when(safeSpace.get(new ActualField(ServerCommands.gameStart))).thenReturn(newObject);
		when(safeSpace.getp(new FormalField(Integer.class), new ActualField(ClientCommands.buyCard),
				new FormalField(String.class)))
		.thenAnswer( new Answer<Object>()
		{
			private int count = 0;
			private Object[] ob1 = null;
			private Object[] ob2 = {0, 0, "Curse"};
			
			@Override
			public Object answer(InvocationOnMock arg0) throws Throwable
			{
				count++;
				if (count == 1)
				{
					return ob2;
				}
				return ob1;
			}
		});
		when(safeSpace.getp(new FormalField(Integer.class), new ActualField(ClientCommands.changePhase)))
		.thenAnswer( new Answer<Object>()
		{
			private int count = 0;
			private Object[] ob1 = {0, 0};
			private Object[] ob2 = null;
			
			@Override
			public Object answer(InvocationOnMock arg0) throws Throwable {
				count++;
				if (count == 1)
				{
					return ob1;
				}
				return ob2; 
			}
		});
		game.start();
	}
	
	
	
}
