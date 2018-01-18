package engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyInt;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cards.Card;
import log.Log;
import objects.ClientCommands;
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
	Card cardMock2;
	@Mock
	Board boardMock;
	@Mock
	Space spaceMock;
	
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
		cardMock2 = mock(Card.class);
		String[] types = {};
		when(cardMock.getTypes()).thenReturn(types);
		when(cardMock2.getTypes()).thenReturn(types);
		boardMock = mock(Board.class);
		spaceMock = mock(Space.class);
		when(gameMock.getSpace()).thenReturn(spaceMock);
		handler = new EffectHandler(gameMock);
	}
	
	@Test
	public void playNoEffectCode() throws InterruptedException
	{
		handler.triggerEffect(0, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playInvalidEffectCode() throws InterruptedException
	{
		handler.triggerEffect(-1, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playCellarNoCardsinHand() throws InterruptedException
	{
		when(playerMock1.getHandSize()).thenReturn(0);
		handler.triggerEffect(1, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playCellarCardsinHandDiscardDrawN() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		ArrayList<Card> hand = new ArrayList<Card>();
		String[] cardsDrawn = {"Card1"};
		hand.add(cardMock);
		when(playerMock1.getHandSize()).thenReturn(1);
		when(playerMock1.getHand()).thenReturn(hand);
		when(playerMock1.getID()).thenReturn(1);
		when(playerMock1.drawCard(anyInt())).thenReturn(cardsDrawn);
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, selection};
		when(spaceMock.getp(new ActualField(playerMock1.getID()),
					new ActualField(ClientCommands.selectCard),
					new FormalField(ArrayList.class))).thenReturn(responseMock);
		
		handler.triggerEffect(1, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playCellarNoneDecided() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(-1);
		ArrayList<Card> hand = new ArrayList<Card>();
		String[] cardsDrawn = {"Card1"};
		hand.add(cardMock);
		when(playerMock1.getHandSize()).thenReturn(1);
		when(playerMock1.getHand()).thenReturn(hand);
		when(playerMock1.getID()).thenReturn(1);
		when(playerMock1.drawCard(anyInt())).thenReturn(cardsDrawn);
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, selection};
		when(spaceMock.getp(new ActualField(playerMock1.getID()),
					new ActualField(ClientCommands.selectCard),
					new FormalField(ArrayList.class))).thenReturn(responseMock);
		
		handler.triggerEffect(1, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playCellarTimeout() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock);
		when(playerMock1.getHandSize()).thenReturn(1);
		when(playerMock1.getHand()).thenReturn(hand);
		when(playerMock1.getID()).thenReturn(1);
		when(spaceMock.getp(new ActualField(playerMock1.getID()),
					new ActualField(ClientCommands.selectCard),
					new FormalField(ArrayList.class))).thenReturn(null);
		handler.triggerEffect(1, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playMoatEffect1() throws InterruptedException
	{	
		handler.triggerEffect(2, playerMock1, cardMock, boardMock, players);
	}
	
	@Test //Reaction part can only be tested during an attack
	public void playMoatEffect2() throws InterruptedException
	{	
		handler.triggerEffect(3, playerMock1, cardMock, boardMock, players);
	}
	
	@Test //Reaction part can only be tested during an attack
	public void playMoatAttackReactDisconnected() throws InterruptedException
	{	
		String[] types = {"attack"};
		when(cardMock.getTypes()).thenReturn(types);
		when(playerMock2.isConnected()).thenReturn(false);
		handler.triggerEffect(0, playerMock1, cardMock, boardMock, players);
	}
	
	@Test //Reaction part can only be tested during an attack
	public void playMoatReactionTimeOut() throws InterruptedException
	{			
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock2);
		hand.add(cardMock);
		String[] types = {"attack"};
		
		when(cardMock.getTypes()).thenReturn(types);
		when(cardMock.getName()).thenReturn("Moat");
		when(cardMock2.getName()).thenReturn("AbsolutelyNotMoat");
		
		when(playerMock2.getHand()).thenReturn(hand);
		when(playerMock2.isConnected()).thenReturn(true);
		when(spaceMock.getp(new ActualField(playerMock2.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayList.class))).thenReturn(null);
		handler.triggerEffect(0, playerMock1, cardMock, boardMock, players);
	}
	
	@Test //Reaction part can only be tested during an attack
	public void playMoatReactionMay() throws InterruptedException
	{			
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock2);
		hand.add(cardMock);
		
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(-1);
		
		String[] types = {"attack"};
		
		when(cardMock.getTypes()).thenReturn(types);
		when(cardMock.getName()).thenReturn("Moat");
		when(cardMock2.getName()).thenReturn("AbsolutelyNotMoat");
		Object[] response = {1, ClientCommands.selectCard, selection};
		when(playerMock2.getHand()).thenReturn(hand);
		when(playerMock2.isConnected()).thenReturn(true);
		when(spaceMock.getp(new ActualField(playerMock2.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayList.class))).thenReturn(response);
		handler.triggerEffect(0, playerMock1, cardMock, boardMock, players);
	}
	
	@Test //Reaction part can only be tested during an attack
	public void playMoatReaction() throws InterruptedException
	{			
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock2);
		hand.add(cardMock);
		
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		
		String[] types = {"attack"};
		
		when(cardMock.getTypes()).thenReturn(types);
		when(cardMock.getName()).thenReturn("Moat");
		when(cardMock2.getName()).thenReturn("AbsolutelyNotMoat");
		Object[] response = {1, ClientCommands.selectCard, selection};
		when(playerMock2.getHand()).thenReturn(hand);
		when(playerMock2.isConnected()).thenReturn(true);
		when(spaceMock.getp(new ActualField(playerMock2.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayList.class))).thenReturn(response);
		handler.triggerEffect(0, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playHarbingerNoneInDiscard() throws InterruptedException
	{
		when(playerMock1.getDiscardSize()).thenReturn(0);
		handler.triggerEffect(2, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playHarbinger() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		LinkedBlockingDeque<Card> discardPile = new LinkedBlockingDeque<Card>();
		discardPile.add(cardMock);
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, selection};
		when(playerMock1.getDiscardSize()).thenReturn(1);
		when(playerMock1.getDiscard()).thenReturn(discardPile);
		when(spaceMock.getp(new ActualField(playerMock1.getID()),
					new ActualField(ClientCommands.selectCard),
					new FormalField(ArrayList.class))).thenReturn(responseMock);		
		handler.triggerEffect(2, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playHarbingerMaySelected() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(-1);
		LinkedBlockingDeque<Card> discardPile = new LinkedBlockingDeque<Card>();
		discardPile.add(cardMock);
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, selection};
		when(playerMock1.getDiscardSize()).thenReturn(1);
		when(playerMock1.getDiscard()).thenReturn(discardPile);
		when(spaceMock.getp(new ActualField(playerMock1.getID()),
					new ActualField(ClientCommands.selectCard),
					new FormalField(ArrayList.class))).thenReturn(responseMock);		
		handler.triggerEffect(4, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playMerchant() throws InterruptedException
	{
		ArrayList<PlayerEffects> effects = new ArrayList<PlayerEffects>();
		effects.add(PlayerEffects.Merchant);
		when(playerMock1.getEffects()).thenReturn(effects);
		when(cardMock.getName()).thenReturn("Merchant");
		when(cardMock2.getName()).thenReturn("Silver");
		when(gameMock.getCurrentPlayer()).thenReturn(playerMock1);
		
		handler.triggerEffect(5, playerMock1, cardMock, boardMock, players);
		handler.triggerEffect(0, playerMock1, cardMock2, boardMock, players);
	}
	
	@Test
	public void playVassal() throws InterruptedException
	{
		LinkedBlockingDeque<Card> deck = new LinkedBlockingDeque<Card>();
		deck.add(cardMock);
		when(playerMock1.getDeck()).thenReturn(deck);
		String[] cardsDrawn = {"dummy"};
		playerMock1.setDeck(deck);
		when(playerMock1.drawCard(anyInt())).thenReturn(cardsDrawn);
		
		handler.triggerEffect(6, playerMock1, cardMock, boardMock, players);
	}

}
