package testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import cards.Card;
import engine.Player;

public class PlayerTest
{	
	@Test
	public void drawCardNoDeck()
	{
		Player player = new Player();
		assertEquals("No cards drawn - hand size is 0", 0, player.getHandSize());
		player.drawCard(1);
		assertEquals("No cards drawn - deck is empty - hand size is 0", 0, player.getHandSize());
	}
	
	@Test
	public void drawACard()
	{
		Player player = new Player();
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
		int n = 2;
		Player player = new Player();
		player.addCardDeckBottom(new Card());
		player.addCardDeckBottom(new Card());
		assertEquals("No cards drawn - hand size is 0", 0, player.getHandSize());
		player.drawCard(n);
		assertEquals("n cards drawn - hand size is n", n, player.getHandSize());
		player.drawCard(n);
		assertEquals("no cards drawn - hand size is still n", n, player.getHandSize());
	}
	
	@Test
	public void drawReshuffle()
	{
		Player player = new Player();
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
	public void buyNoBuys()
	{
		Player player = new Player();
		player.addMoney(Integer.MAX_VALUE);
		assertEquals("Player has enough money", player.getMoney(), Integer.MAX_VALUE); 
		assertEquals("Player has no buys", player.getBuys(), 0);
		player.buy(new Card());
		assertEquals("Player discard pile size: 0", 0, player.getDiscardSize());
	}
	
	@Test
	public void buyNoMoney()
	{
		Player player = new Player();
		player.addMoney(Integer.MIN_VALUE);
		player.addBuys(Integer.MAX_VALUE);
		assertEquals("Player has MIN_value money", player.getMoney(), Integer.MIN_VALUE); 
		assertEquals("Player has MAX value buys", player.getBuys(), Integer.MAX_VALUE);
		player.buy(new Card());
		assertEquals("Player discard pile size: 0", 0, player.getDiscardSize());
	}
	
	@Test
	public void buy()
	{
		Player player = new Player();
		player.addMoney(Integer.MAX_VALUE);
		player.addBuys(Integer.MAX_VALUE);
		assertEquals("Player has MAX_value money", player.getMoney(), Integer.MAX_VALUE); 
		assertEquals("Player has MAX value buys", player.getBuys(), Integer.MAX_VALUE);
		player.buy(new Card());
		assertEquals("Player discard pile size: 1", 1, player.getDiscardSize());
	}
	
	@Test
	public void shuffleDeckNoCards()
	{
		Player player = new Player();
		assertEquals("Deck size = 0", 0, player.getDeckSize());
		player.shuffleDeck();
		assertEquals("Deck size = 0", 0, player.getDeckSize());
	}
	@Test
	public void shuffleDeck()
	{
		Player player = new Player();
		player.addCardDeckBottom(new Card());
		assertEquals("Deck size = 1", 1, player.getDeckSize());
		player.shuffleDeck();
		assertEquals("Deck size = 1", 1, player.getDeckSize());
	}
	
	@Test
	public void reshuffleNoDiscardNoDeck()
	{
		Player player = new Player();
		assertEquals("Deck size = 0", 0, player.getDeckSize());
		assertEquals("Discard size = 0", 0, player.getDiscardSize());
		player.reshuffleDeck();
		assertEquals("Deck size = 0", 0, player.getDeckSize());
		assertEquals("Discard size = 0", 0, player.getDiscardSize());
	}
	
	@Test
	public void reshuffleNoDiscard()
	{
		Player player = new Player();
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
		Player player = new Player();
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
		Player player = new Player();
		player.addCardDeckBottom(new Card());
		player.discardCard(new Card());
		assertEquals("Deck size = 1", 1, player.getDeckSize());
		assertEquals("Discard size = 1", 1, player.getDiscardSize());
		player.reshuffleDeck();
		assertEquals("Deck size = 2", 2, player.getDeckSize());
		assertEquals("Discard size = 0", 0, player.getDiscardSize());
	}
	
	@Test
	public void playCardNoAction()
	{
		Player player = new Player();
		player.playCard(new Card(), 0);
		assertEquals("Action size = 0", 0, player.getActions());
	}
	
	@Test
	public void playCardAction()
	{
		Player player = new Player();
		player.addActions(1);
		assertEquals("Action size = 1", 1, player.getActions());
		player.playCard(new Card(), 0);
		assertEquals("Action size = 0", 0, player.getActions());
	}

}
