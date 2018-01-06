package testing;

import static org.junit.Assert.assertEquals;

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

}
