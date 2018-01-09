package engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import cards.Card;
import engine.Board;

@RunWith(MockitoJUnitRunner.class)
public class BoardTest
{	
	@Mock
	private Card copperMock;
	@Mock
	private Card estateMock;
	@Mock
	private Card actionMock;
	@Mock
	private Card provinceMock;
	private Board board;	
	
	@Before
	public void setupBoard()
	{
		MockitoAnnotations.initMocks(this);

		copperMock = mock(Card.class);
		estateMock = mock(Card.class);
		actionMock = mock(Card.class);
		provinceMock = mock(Card.class);
		
		when(copperMock.getName()).thenReturn("Copper");
		when(estateMock.getName()).thenReturn("Estate");
		when(actionMock.getName()).thenReturn("Action");
		when(provinceMock.getName()).thenReturn("Province");
		
		ArrayList<Card> dummyCards = new ArrayList<Card>();
		for (int i = 0; i < 10; i++)
		{
			dummyCards.add(actionMock);
		}
		
		ArrayList<Card> dummySetup = new ArrayList<Card>();
		dummySetup.add(copperMock);
		for (int i = 0; i < 3; i++)
		{
			dummySetup.add(new Card());
		}
		dummySetup.add(estateMock);
		dummySetup.add(new Card());
		dummySetup.add(provinceMock);
		board = new Board(2, dummyCards, dummySetup);
	}
	
	@Test(expected = NullPointerException.class) 
	public void createboardNoSetupNoCards()
	{
		new Board(0, null, null);
	}
	
	@Test(expected = NullPointerException.class) 
	public void createBoardNoCards()
	{		
		new Board(0, null, new ArrayList<Card>());
	}

	@Test(expected = NullPointerException.class) 
	public void createBoardNoSetup()
	{
		new Board(0, new ArrayList<Card>(), null);
	}
	
	@Test 
	public void createBoardCards()
	{
		ArrayList<Card> dummyCards = new ArrayList<Card>();
		for (int i = 0; i < 10; i++)
		{
			dummyCards.add(new Card());
		}
		ArrayList<Card> dummySetup = new ArrayList<Card>();
		for (int i = 0; i < 7; i++)
		{
			dummySetup.add(new Card());
		}
		assertEquals("Dummy setup size : 7", dummySetup.size(), 7);
		assertEquals("Dummy cards size : 10", dummyCards.size(), 10);
		new Board(2, dummyCards, dummySetup);
	}
	
	@Test
	public void createBoardEmptyLists()
	{
		new Board(0, new ArrayList<Card>(), new ArrayList<Card>());
	}
	
	@Test
	public void canGainNonExisting()
	{
		Card card = board.canGain("HELLO WORLD");
		board.cardRemove("HELLO WORLD");
		assertNull("Card does not exist", card);
	}
	
	@Test
	public void canGainNoneLeft()
	{
		Card card = board.canGain("Action");
		board.cardRemove("Action");
		for(int i = 0; i < 10; i++)
		{
			assertNotNull("card not equal to null", card);
			card = board.canGain("Action");
			board.cardRemove("Action");
		}
		assertNull("Card does not exist", card);
	}
	
	@Test
	public void canGain()
	{
		Card card = board.canGain("testCard");
		assertNotNull("Card exists", card);
	}
	
	@Test
	public void getCopiesLeft()
	{
		int remain = board.getCopiesLeft("Action");
		assertEquals("10 copies left: ", 10, remain);
		remain = board.getCopiesLeft("Estate");
		assertEquals("8 copies left: ", 8, remain);
	}
	
	@Test
	public void getCopiesNonExisting()
	{
		int remain = board.getCopiesLeft("null");
		assertEquals("0 copies exist", 0, remain);
	}
	
	@Test
	public void checkEndNoneApply()
	{
		Boolean result = board.checkEnd();
		assertFalse("Game is not over", result);
	}
	
	@Test
	public void checkEndProvince()
	{
		for (int i = 0; i < 8; i++)
			board.cardRemove("Province");
		Boolean result = board.checkEnd();
		assertTrue("Game is over", result);
	}
	
	@Test
	public void checkEndThreePiles()
	{
		for (int i = 0; i < 8; i++)
			board.cardRemove("Estate");
		for (int i = 0; i < 10; i++)
		{
			board.cardRemove("Action");
			board.cardRemove("testCard");
		}
			
		Boolean result = board.checkEnd();
		assertTrue("Game is over", result);
	}

	
	
	


}
