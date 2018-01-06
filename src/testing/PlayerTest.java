package testing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cards.Card;
import engine.Player;

public class PlayerTest
{	
	
	@Test
	public void drawCard()
	{
		Player player = new Player();
		player.newHand();
		player.addCardDeckBottom(new Card());
		assertEquals("No cards drawn - hand size is 0", 0, player.getHandSize());
	}

}
