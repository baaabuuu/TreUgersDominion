package engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import cards.Card;
/**
 *  Used to test copy Non identical and minCosting.
 *  Non-identical was mainly to get higher coverage.
 * @author s164166
 */
public class EffectHandlerTest
{	
	@Test
	public void copyNonIdentical()
	{
		Card dummyCard = new Card();
		Card dummyCard2 = dummyCard.copyOf();
		assertNotEquals("Copies are no the same object", dummyCard, dummyCard2);
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
