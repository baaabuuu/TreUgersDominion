package engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import cards.Card;

public class CardTest
{	
	@Test
	public void copyNonIdentical()
	{
		Card dummyCard = new Card();
		boolean result = (dummyCard == dummyCard.copyOf());
		assertFalse("Copies are no the same object", result);
	}
	
	@Test
	public void minCostTesting1()
	{
		Card dummyCard = new Card();
		dummyCard.setCostMod(5);
		assertEquals("Card costs 0", 0, dummyCard.getCost());
	}
	
	@Test
	public void minCostTesting2()
	{
		Card dummyCard = new Card();
		assertEquals("Card costs 0",0 , dummyCard.getCost());
	}
	
}
