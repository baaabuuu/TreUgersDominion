package testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;


import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cards.Card;
import engine.Board;

public class BoardTest
{	
	@Mock
	private Card copperMock;
	@Mock
	private Card estateMock;
	@Mock
	private Card actionMock;
	
	private Board board;
	
	@Before
	public void setupBoard()
	{
		copperMock = mock(Card.class);
		estateMock = mock(Card.class);
		actionMock = mock(Card.class);
		
		when(copperMock.getName()).thenReturn("Copper");
		when(estateMock.getName()).thenReturn("Estate");
		when(actionMock.getName()).thenReturn("Action");
        MockitoAnnotations.initMocks(this);

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
		for (int i = 0; i < 2; i++)
		{
			dummySetup.add(new Card());
		}
		board = new Board(2, dummyCards, dummySetup);
	}
	@Test(expected = NullPointerException.class) 
	public void createboardNoSetupNoCards()
	{
		Board board = new Board(0, null, null);
	}
	@Test(expected = NullPointerException.class) 
	public void createBoardNoCards()
	{		
		Board board = new Board(0, null, new ArrayList<Card>());
	}
	@Test(expected = NullPointerException.class) 
	public void createBoardNoSetup()
	{
		Board board = new Board(0, new ArrayList<Card>(), null);
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
		Board board = new Board(2, dummyCards, dummySetup);
	}
	
	@Test
	public void createBoardEmptyLists()
	{
		Board board = new Board(0, new ArrayList<Card>(), new ArrayList<Card>());
	}
	
	@Test
	public void canBuyNonExisting()
	{
		Card card = board.canBuy("NULL");
		assertNull("Card does not exist", card);
	}
	@Test
	public void canBuy()
	{
		Card card = board.canBuy("testCard");
		assertNotNull("Card exists", card);
	}
	@Test
	public void canBuyNoneLeft()
	{
		System.out.println("Setting up can buyNoneLeft");
		for(String name : board.shop.keySet())
			System.out.println(name);
		Card card = board.canBuy("Action");
		
		
		board.cardRemove("Action");
		assertNotNull("card not equal to null ", card);
		
		for(int i = 0; i < 9; i++)
		{
			assertNotNull("card not equal to null", card);
			board.canBuy("Action");
			board.cardRemove("Action");
		}
		assertNull("Card does not exist", card);
	}
	
	


}
