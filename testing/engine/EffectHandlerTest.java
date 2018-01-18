package engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cards.Card;
import objects.PlayerEffects;
/**
 *  Used to test the effectHandler
 * @author s164166
 */
public class EffectHandlerTest
{	
	@Mock
	Game gameMock;
	@Mock
	Player playerMock1;
	Player playerMock2;
	@Mock
	Card cardMock;
	@Mock
	Board boardMock;
	
	Player[] players = new Player[2];
	EffectHandler handler;
	
	@Before
	public void setupHandler()
	{
		MockitoAnnotations.initMocks(this);
		gameMock = mock(Game.class);
		playerMock1 = mock(Player.class);
		playerMock2 = mock(Player.class);
		players[0] = playerMock1;
		players[1] = playerMock2;
		when(playerMock1.getEffects()).thenReturn(new ArrayList<PlayerEffects>());
		when(playerMock2.getEffects()).thenReturn(new ArrayList<PlayerEffects>());
		cardMock = mock(Card.class);
		String[] types = {};
		when(cardMock.getTypes()).thenReturn(types);
		boardMock = mock(Board.class);
		handler = new EffectHandler(gameMock);
	}
	
	@Test
	public void playNoEffectCode() throws InterruptedException
	{
		handler.triggerEffect(0, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playCellarNoCardsinHand() throws InterruptedException
	{
		when(playerMock1.getHandSize()).thenReturn(0);
		handler.triggerEffect(1, playerMock1, cardMock, boardMock, players);
		int actionCount = playerMock1.getActions();
		assertEquals("Action count is now 1", 1, actionCount);
	}
	
	@Test
	public void playCellarCardsinHand() throws InterruptedException
	{
		when(playerMock1.getHandSize()).thenReturn(0);
		handler.triggerEffect(1, playerMock1, cardMock, boardMock, players);
	}

}
