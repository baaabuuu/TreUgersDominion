package engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.MockitoAnnotations;

import cards.Card;
import engine.Player;
import log.Log;

/**
 * Tests the base engine ensuring everything works
 * @author s164166
 */
public class PlayerTest
{	
	@Test
	public void drawCardNoDeck()
	{
		Player player = new Player(0);
		assertEquals("No cards drawn - hand size is 0", 0, player.getHandSize());
		player.drawCard(1);
		assertEquals("No cards drawn - deck is empty - hand size is 0", 0, player.getHandSize());
	}
	
	@Test
	public void drawACard()
	{
		Player player = new Player(0);
		player.addCardDeckBottom(new Card());
		assertEquals("No cards drawn - hand size is 0", 0, player.getHandSize());
		player.drawCard(1);
		assertEquals("1 cards drawn - hand size is 1", 1, player.getHandSize());
		player.drawCard(1);
		assertEquals("no cards drawn - hand size is still 1", 1, player.getHandSize());
	}
	
	@Test
	public void drawNCards()
	{
		Log.important("testing drawNcards");
		int n = 2;
		Player player = new Player(0);
		player.addCardDeckBottom(new Card());
		player.addCardDeckBottom(new Card());
		assertEquals("No cards drawn - hand size is 0", 0, player.getHandSize());
		player.drawCard(2);
		assertEquals("Deck size is now 0", 0, player.getDeckSize());
		assertEquals("n cards drawn - hand size is n", n, player.getHandSize());
		player.drawCard(n);
		assertEquals("no cards drawn - hand size is still n", n, player.getHandSize());
	}
	
	@Test
	public void drawReshuffle()
	{
		Player player = new Player(0);
		player.addCardDeckBottom(new Card());
		player.discardCard(new Card());
		assertEquals("No cards drawn - hand size is 0", 0, player.getHandSize());
		player.drawCard(1);
		assertEquals("1 cards drawn - hand size is 1", 1, player.getHandSize());
		player.drawCard(1);
		assertEquals("2 cards drawn - hand size is 2 - reshuffled discard pile", 2, player.getHandSize());
		assertEquals("0 cards in discard pile - reshuffled discard pile", 0, player.getDiscard().size());
		player.drawCard(1);
		assertEquals("no cards drawn - hand size is 2 - no more cards can be drawn", 2, player.getHandSize());
	}
	
	@Test
	public void removeNoCards()
	{
		Player player = new Player(0);
		Boolean result = player.removeFromHand(0);
		assertFalse("Cannot remove if no card in at index 0", result);
		 result = player.removeFromHand(new Card());
		assertFalse("Cannot remove if card does not exist in hand", result);
	}
	
	@Test
	public void removeFromHandUnderIndex()
	{
		Player player = new Player(0);
		player.getHand().add(new Card());
		Boolean result = player.removeFromHand(-1);
		assertFalse("Cannot remove if index under 0", result);
	}
	
	@Test
	public void removeFromHand()
	{
		Player player = new Player(0);
		player.getHand().add(new Card());
		Boolean result = player.removeFromHand(0);
		assertTrue("Card Removed", result);
	}
	
	@Test
	public void removeSpecificCard()
	{
		Player player = new Player(0);
		Card card = new Card();
		player.getHand().add(card);
		Boolean result = player.removeFromHand(card);
		assertTrue("Card removed from hand", result);
	}
	
	@Test
	public void buyNoBuys()
	{
		Player player = new Player(0);
		player.addMoney(Integer.MAX_VALUE);
		assertEquals("Player has enough money", player.getMoney(), Integer.MAX_VALUE); 
		assertEquals("Player has no buys", player.getBuys(), 0);
		player.buy(new Card(), 1);
		assertEquals("Player discard pile size: 0", 0, player.getDiscardSize());
	}
	
	@Test
	public void buyNoMoney()
	{
		Player player = new Player(0);
		player.addMoney(Integer.MIN_VALUE);
		player.addBuys(Integer.MAX_VALUE);
		assertEquals("Player has MIN_value money", player.getMoney(), Integer.MIN_VALUE); 
		assertEquals("Player has MAX value buys", player.getBuys(), Integer.MAX_VALUE);
		player.buy(new Card(), 1);
		assertEquals("Player discard pile size: 0", 0, player.getDiscardSize());
	}
	
	@Test
	public void buyWrongPhase()
	{
		Player player = new Player(0);
		player.addMoney(Integer.MAX_VALUE);
		player.addBuys(Integer.MAX_VALUE);
		assertEquals("Player has MAX_value money", player.getMoney(), Integer.MAX_VALUE); 
		assertEquals("Player has MAX value buys", player.getBuys(), Integer.MAX_VALUE);
		player.buy(new Card(), 0);
		assertEquals("Player discard pile size: 0", 0, player.getDiscardSize());
	}
	
	@Test
	public void buy()
	{
		Player player = new Player(0);
		player.addMoney(Integer.MAX_VALUE);
		player.addBuys(Integer.MAX_VALUE);
		assertEquals("Player has MAX_value money", player.getMoney(), Integer.MAX_VALUE); 
		assertEquals("Player has MAX value buys", player.getBuys(), Integer.MAX_VALUE);
		player.buy(new Card(), 1);
		assertEquals("Player discard pile size: 1", 1, player.getDiscardSize());
	}
	
	@Test
	public void shuffleDeckNoCards()
	{
		Player player = new Player(0);
		assertEquals("Deck size = 0", 0, player.getDeckSize());
		player.shuffleDeck();
		assertEquals("Deck size = 0", 0, player.getDeckSize());
	}
	@Test
	public void shuffleDeck()
	{
		Player player = new Player(0);
		player.addCardDeckBottom(new Card());
		assertEquals("Deck size = 1", 1, player.getDeckSize());
		player.shuffleDeck();
		assertEquals("Deck size = 1", 1, player.getDeckSize());
	}
	
	@Test
	public void reshuffleNoDiscardNoDeck()
	{
		Player player = new Player(0);
		assertEquals("Deck size = 0", 0, player.getDeckSize());
		assertEquals("Discard size = 0", 0, player.getDiscardSize());
		player.reshuffleDeck();
		assertEquals("Deck size = 0", 0, player.getDeckSize());
		assertEquals("Discard size = 0", 0, player.getDiscardSize());
	}
	
	@Test
	public void reshuffleNoDiscard()
	{
		Player player = new Player(0);
		player.addCardDeckBottom(new Card());
		assertEquals("Deck size = 1", 1, player.getDeckSize());
		assertEquals("Discard size = 0", 0, player.getDiscardSize());
		player.reshuffleDeck();
		assertEquals("Deck size = 1", 1, player.getDeckSize());
		assertEquals("Discard size = 0", 0, player.getDiscardSize());
	}
	
	@Test
	public void reshuffleNoDeck()
	{
		Player player = new Player(0);
		player.discardCard(new Card());
		assertEquals("Deck size = 0", 0, player.getDeckSize());
		assertEquals("Discard size = 1", 1, player.getDiscardSize());
		player.reshuffleDeck();
		assertEquals("Deck size = 1", 1, player.getDeckSize());
		assertEquals("Discard size = 0", 0, player.getDiscardSize());
	}
	
	@Test
	public void reshuffle()
	{
		Player player = new Player(0);
		player.addCardDeckBottom(new Card());
		player.discardCard(new Card());
		assertEquals("Deck size = 1", 1, player.getDeckSize());
		assertEquals("Discard size = 1", 1, player.getDiscardSize());
		player.reshuffleDeck();
		assertEquals("Deck size = 2", 2, player.getDeckSize());
		assertEquals("Discard size = 0", 0, player.getDiscardSize());
	}
	
	@Test
	public void playActionCardNoActions()
	{
		MockitoAnnotations.initMocks(this);
		Card actionMock = mock(Card.class);
	    String[] bc = {"Action"};
	    when(actionMock.getDisplayTypes()).thenReturn(bc);
		Player player = new Player(0);
		player.playCard(actionMock, 0);
		assertEquals("Action size = 0", 0, player.getActions());
	}
	
	@Test
	public void playCardWrongPhase()
	{
		MockitoAnnotations.initMocks(this);
		Card actionMock = mock(Card.class);
	    String[] bc = {"Action"};
	    when(actionMock.getDisplayTypes()).thenReturn(bc);
		Player player = new Player(0);
		player.addActions(1);
		player.playCard(actionMock, 1);

		assertEquals("Action size = 1", 1, player.getActions());
	}
	
	@Test
	public void playCardAction()
	{
		MockitoAnnotations.initMocks(this);
		Card actionMock = mock(Card.class);
	    String[] bc = {"Action"};
	    when(actionMock.getDisplayTypes()).thenReturn(bc);
		Player player = new Player(0);
		player.addActions(1);
		player.getHand().add(actionMock);
		assertEquals("Hand size = 1", 1, player.getHandSize());
		assertEquals("Action size = 1", 1, player.getActions());
		player.playCard(actionMock, 0);
		assertEquals("Action size = 0", 0, player.getActions());
		assertEquals("Hand size = 0", 0, player.getHandSize());
	}
	
	@Test
	public void playTreasureWrongPhase()
	{
		MockitoAnnotations.initMocks(this);
		Card treasureMock = mock(Card.class);
	    String[] bc = {"Treasure"};                                    
		when(treasureMock.getDisplayTypes()).thenReturn(bc);
		when(treasureMock.getMoney()).thenReturn(1);
		Player player = new Player(0);
		assertEquals("Money = 0", 0, player.getMoney());
		player.playCard(treasureMock, 0);
		assertEquals("Money = 0", 0, player.getMoney());
	}
	
	@Test
	public void playTreasure()
	{
		MockitoAnnotations.initMocks(this);
		Card treasureMock = mock(Card.class);
	    String[] bc = {"Treasure"};                                    
		when(treasureMock.getDisplayTypes()).thenReturn(bc);
		when(treasureMock.getMoney()).thenReturn(1);
		Player player = new Player(0);
		player.getHand().add(treasureMock);
		assertEquals("Money = 0", 0, player.getMoney());
		assertEquals("Hand size = 1", 1, player.getHandSize());
		player.playCard(treasureMock, 1);
		assertEquals("Money = 1", 1, player.getMoney());
		assertEquals("Hand size = 0", 0, player.getHandSize());
	}
	
	@Test
	public void playActionCardNoActionIndex()
	{
		MockitoAnnotations.initMocks(this);
		Card actionMock = mock(Card.class);
	    String[] bc = {"Action"};
	    when(actionMock.getDisplayTypes()).thenReturn(bc);
		Player player = new Player(0);
		boolean result = player.playCard(0, 0);
		assertFalse("Card wasnt played: ", result);
		assertEquals("Action size = 0", 0, player.getActions());
	}
	
	@Test
	public void playCardWrongPhaseIndex()
	{
		MockitoAnnotations.initMocks(this);
		Card actionMock = mock(Card.class);
	    String[] bc = {"Action"};
	    when(actionMock.getDisplayTypes()).thenReturn(bc);
		Player player = new Player(0);
		player.addActions(1);
		boolean result = player.playCard(0, 1);
		assertFalse("Card wasnt played: ", result);
		assertEquals("Action size = 1", 1, player.getActions());
	}
	
	@Test
	public void playCardActionIndex()
	{
		MockitoAnnotations.initMocks(this);
		Card actionMock = mock(Card.class);
	    String[] bc = {"Action"};
	    when(actionMock.getDisplayTypes()).thenReturn(bc);
		Player player = new Player(0);
		player.addActions(1);
		player.getHand().add(actionMock);
		assertEquals("Hand size = 1", 1, player.getHandSize());
		assertEquals("Action size = 1", 1, player.getActions());
		player.playCard(0, 0);
		assertEquals("Hand size = 0", 0, player.getHandSize());
		assertEquals("Action size = 0", 0, player.getActions());
	}
	
	@Test
	public void playTreasureWrongPhaseIndex()
	{
		MockitoAnnotations.initMocks(this);
		Card treasureMock = mock(Card.class);
	    String[] bc = {"Treasure"};                                    
		when(treasureMock.getDisplayTypes()).thenReturn(bc);
		when(treasureMock.getMoney()).thenReturn(1);
		
		Player player = new Player(0);
		player.getHand().add(treasureMock);

		assertEquals("Money = 0", 0, player.getMoney());
		player.playCard(0, 0);
		assertEquals("Money = 0", 0, player.getMoney());
	}
	
	@Test
	public void playTreasureIndex()
	{
		MockitoAnnotations.initMocks(this);
		Card treasureMock = mock(Card.class);
	    String[] bc = {"Treasure"};                                    
		when(treasureMock.getDisplayTypes()).thenReturn(bc);
		when(treasureMock.getMoney()).thenReturn(1);
		Player player = new Player(0);
		assertEquals("Money = 0", 0, player.getMoney());
		player.getHand().add(treasureMock);
		player.playCard(0, 1);
		assertEquals("Money = 1", 1, player.getMoney());
	}
	
	@Test
	public void getFirstIndexOf()
	{
		MockitoAnnotations.initMocks(this);
		Card dummy = mock(Card.class);
		when(dummy.getName()).thenReturn("Dummy");
		Player player = new Player(0);
		player.getHand().add(dummy);
		int i = player.getFirstIndexOf("Dummy");
		assertEquals("i is equal to 0", i, 0);
	}
	
	@Test
	public void getFirstIndexNonExistant()
	{
		MockitoAnnotations.initMocks(this);
		Card dummy = mock(Card.class);
		when(dummy.getName()).thenReturn("Dummy");
		Player player = new Player(0);
		player.getHand().add(dummy);
		int i = player.getFirstIndexOf("nonexistant");
		assertEquals("i is equal to -1", i, -1);
	}


}
