package engine;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;


import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import cards.Card;
import objects.ArrayListObject;
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

	ArrayListObject arrayListObjectMock;
	
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
		when(playerMock1.getID()).thenReturn(0);
		when(playerMock2.getID()).thenReturn(1);
		when(playerMock1.isConnected()).thenReturn(true);
		when(playerMock2.isConnected()).thenReturn(true);
		cardMock = mock(Card.class);
		cardMock2 = mock(Card.class);
		String[] types = {};
		when(cardMock.getTypes()).thenReturn(types);
		when(cardMock2.getTypes()).thenReturn(types);
		boardMock = mock(Board.class);
		spaceMock = mock(Space.class);
		when(gameMock.getSpace()).thenReturn(spaceMock);
		handler = new EffectHandler(gameMock);
		when(gameMock.getWaitTime()).thenReturn(2);
		arrayListObjectMock = mock(ArrayListObject.class);
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
		ArrayList<Card> hand = new ArrayList<Card>();
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		
		String[] cardsDrawn = {"Card1"};
		hand.add(cardMock);
		when(playerMock1.getHandSize()).thenReturn(1);
		when(playerMock1.getHand()).thenReturn(hand);
		when(playerMock1.getID()).thenReturn(1);
		when(playerMock1.drawCard(anyInt())).thenReturn(cardsDrawn);
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		when(spaceMock.getp(new ActualField(playerMock1.getID()),
					new ActualField(ClientCommands.selectCard),
					new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		
		handler.triggerEffect(1, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playCellarNoneDecided() throws InterruptedException
	{
		
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(-1);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		ArrayList<Card> hand = new ArrayList<Card>();
		String[] cardsDrawn = {"Card1"};
		hand.add(cardMock);
		when(playerMock1.getHandSize()).thenReturn(1);
		when(playerMock1.getHand()).thenReturn(hand);
		when(playerMock1.getID()).thenReturn(1);
		when(playerMock1.drawCard(anyInt())).thenReturn(cardsDrawn);
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		when(spaceMock.getp(new ActualField(playerMock1.getID()),
					new ActualField(ClientCommands.selectCard),
					new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		
		handler.triggerEffect(1, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playCellarTimeout() throws InterruptedException
	{
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock);
		when(playerMock1.getHandSize()).thenReturn(1);
		when(playerMock1.getHand()).thenReturn(hand);
		when(playerMock1.getID()).thenReturn(1);
		when(spaceMock.getp(new ActualField(playerMock1.getID()),
					new ActualField(ClientCommands.selectCard),
					new FormalField(ArrayListObject.class))).thenReturn(null);
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
				new FormalField(ArrayListObject.class))).thenReturn(null);
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
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		String[] types = {"attack"};
		
		when(cardMock.getTypes()).thenReturn(types);
		when(cardMock.getName()).thenReturn("Moat");
		when(cardMock2.getName()).thenReturn("AbsolutelyNotMoat");
		Object[] response = {1, ClientCommands.selectCard, arrayListObjectMock};
		when(playerMock2.getHand()).thenReturn(hand);
		when(playerMock2.isConnected()).thenReturn(true);
		when(spaceMock.getp(new ActualField(playerMock2.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(response);
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
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		String[] types = {"attack"};
		
		when(cardMock.getTypes()).thenReturn(types);
		when(cardMock.getName()).thenReturn("Moat");
		when(cardMock2.getName()).thenReturn("AbsolutelyNotMoat");
		Object[] response = {1, ClientCommands.selectCard, arrayListObjectMock};
		when(playerMock2.getHand()).thenReturn(hand);
		when(playerMock2.isConnected()).thenReturn(true);
		when(spaceMock.getp(new ActualField(playerMock2.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(response);
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
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		LinkedBlockingDeque<Card> discardPile = new LinkedBlockingDeque<Card>();
		discardPile.add(cardMock);
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		when(playerMock1.getDiscardSize()).thenReturn(1);
		when(playerMock1.getDiscard()).thenReturn(discardPile);
		when(spaceMock.getp(new ActualField(playerMock1.getID()),
					new ActualField(ClientCommands.selectCard),
					new FormalField(ArrayListObject.class))).thenReturn(responseMock);		
		handler.triggerEffect(2, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playHarbingerMaySelected() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(-1);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		LinkedBlockingDeque<Card> discardPile = new LinkedBlockingDeque<Card>();
		discardPile.add(cardMock);
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		when(playerMock1.getDiscardSize()).thenReturn(1);
		when(playerMock1.getDiscard()).thenReturn(discardPile);
		when(spaceMock.getp(new ActualField(playerMock1.getID()),
					new ActualField(ClientCommands.selectCard),
					new FormalField(ArrayListObject.class))).thenReturn(responseMock);		
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
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		LinkedBlockingDeque<Card> deck = new LinkedBlockingDeque<Card>();
		deck.add(cardMock);
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock);
		String[] cardsDrawn = {"dummy"};
		String[] types = {"Action"};
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		
		when(playerMock1.getHand()).thenReturn(hand);
		when(playerMock1.getHandSize()).thenReturn(1);
		when(playerMock1.drawCard(anyInt())).thenReturn(cardsDrawn);
		when(playerMock1.getDeck()).thenReturn(deck);
		when(cardMock.getDisplayTypes()).thenReturn(types);
		when(spaceMock.getp(new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		handler.triggerEffect(6, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playVassalMay() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(-1);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		LinkedBlockingDeque<Card> deck = new LinkedBlockingDeque<Card>();
		deck.add(cardMock);
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock);
		String[] cardsDrawn = {"dummy"};
		String[] types = {"Action"};
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		
		when(playerMock1.getHand()).thenReturn(hand);
		when(playerMock1.getHandSize()).thenReturn(1);
		when(playerMock1.drawCard(anyInt())).thenReturn(cardsDrawn);
		when(playerMock1.getDeck()).thenReturn(deck);
		when(cardMock.getDisplayTypes()).thenReturn(types);
		when(spaceMock.getp(new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		handler.triggerEffect(6, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playVassalTimeout() throws InterruptedException
	{
		LinkedBlockingDeque<Card> deck = new LinkedBlockingDeque<Card>();
		deck.add(cardMock);
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock);
		String[] cardsDrawn = {"dummy"};
		String[] types = {"Action"};
		
		when(playerMock1.getHand()).thenReturn(hand);
		when(playerMock1.getHandSize()).thenReturn(1);
		when(playerMock1.drawCard(anyInt())).thenReturn(cardsDrawn);
		when(playerMock1.getDeck()).thenReturn(deck);
		when(cardMock.getDisplayTypes()).thenReturn(types);
		when(spaceMock.getp(new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(null);
		handler.triggerEffect(6, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playVassalWrongType() throws InterruptedException
	{
		LinkedBlockingDeque<Card> deck = new LinkedBlockingDeque<Card>();
		deck.add(cardMock);
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock);
		String[] cardsDrawn = {"dummy"};
		String[] types = {"WrongType"};
		
		when(playerMock1.getHand()).thenReturn(hand);
		when(playerMock1.getHandSize()).thenReturn(1);
		when(playerMock1.drawCard(anyInt())).thenReturn(cardsDrawn);
		when(playerMock1.getDeck()).thenReturn(deck);
		when(cardMock.getDisplayTypes()).thenReturn(types);
		handler.triggerEffect(6, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playVassalNoCards() throws InterruptedException
	{
		String[] cardsDrawn = {null};
		when(playerMock1.drawCard(anyInt())).thenReturn(cardsDrawn);
		handler.triggerEffect(6, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playVillage() throws InterruptedException
	{
		handler.triggerEffect(7, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playWorkshop() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(1);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		
		ArrayList<Card> boardList = new ArrayList<Card>();
		boardList.add(cardMock);
		boardList.add(cardMock2);
		
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		
		when(boardMock.getCardStream()).thenReturn(boardList.stream());
		when(cardMock.getName()).thenReturn("Card 1");
		when(cardMock2.getName()).thenReturn("Card 2");
		when(cardMock.getCost()).thenReturn(4);
		when(cardMock2.getCost()).thenReturn(3);
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);	
		handler.triggerEffect(8, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playWorkshopTimeout() throws InterruptedException
	{		
		ArrayList<Card> boardList = new ArrayList<Card>();
		boardList.add(cardMock);
		boardList.add(cardMock2);
				
		when(boardMock.getCardStream()).thenReturn(boardList.stream());
		when(cardMock.getName()).thenReturn("Card 1");
		when(cardMock2.getName()).thenReturn("Card 2");
		when(cardMock.getCost()).thenReturn(4);
		when(cardMock2.getCost()).thenReturn(3);
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(null);
		handler.triggerEffect(8, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playBureaucrat2Choices() throws InterruptedException
	{		
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		list.add(cardMock2);
		
		when(boardMock.canGain(anyString())).thenReturn(cardMock);
		String[] types = {"Victory"};
		when(cardMock.getName()).thenReturn("Silver");
		when(cardMock.getDisplayTypes()).thenReturn(types);
		when(cardMock2.getDisplayTypes()).thenReturn(types);
		when(playerMock2.getHand()).thenReturn(list);
		Object[] responseMock = {playerMock2.getID(), ClientCommands.selectCard, arrayListObjectMock};
		
		when(spaceMock.getp(
				new FormalField(Integer.class),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);
				
		handler.triggerEffect(9, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playBureaucrat2ChoicesTimeout() throws InterruptedException
	{		
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		list.add(cardMock2);
		
		when(boardMock.canGain(anyString())).thenReturn(cardMock);
		String[] types = {"Victory"};
		when(cardMock.getName()).thenReturn("Silver");
		when(cardMock.getDisplayTypes()).thenReturn(types);
		when(cardMock2.getDisplayTypes()).thenReturn(types);
		when(playerMock2.getHand()).thenReturn(list);
		
		when(spaceMock.getp(
				new FormalField(Integer.class),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(null);
				
		handler.triggerEffect(9, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playBureaucrat1() throws InterruptedException
	{		
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		
		when(boardMock.canGain(anyString())).thenReturn(cardMock);
		String[] types = {"Victory"};
		when(cardMock.getName()).thenReturn("Silver");
		when(cardMock.getDisplayTypes()).thenReturn(types);
		when(cardMock2.getDisplayTypes()).thenReturn(types);
		when(playerMock2.getHand()).thenReturn(list);
		
				
		handler.triggerEffect(9, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playBureaucratNone() throws InterruptedException
	{		
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		list.add(cardMock2);
		
		when(boardMock.canGain(anyString())).thenReturn(cardMock);
		String[] types = {"NOT A VICTORY CARD"};
		when(cardMock.getName()).thenReturn("MARK1");
		when(cardMock.getName()).thenReturn("MARK2");
		when(cardMock.getDisplayTypes()).thenReturn(types);
		when(cardMock2.getDisplayTypes()).thenReturn(types);
		when(playerMock2.getHand()).thenReturn(list);
		
		when(spaceMock.getp(
				new FormalField(Integer.class),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(null);
				
		handler.triggerEffect(9, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playBureaucratDisconnevted() throws InterruptedException
	{		
		when(playerMock2.isConnected()).thenReturn(false);
		handler.triggerEffect(9, playerMock1, cardMock, boardMock, players);
	}
		
	@Test //Garden has no play effect
	public void playGardenEffect() throws InterruptedException
	{			
		handler.triggerEffect(10, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playMilitiaDiscard1() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		selection.add(1);
		selection.add(2);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock);
		hand.add(cardMock);
		hand.add(cardMock);
		hand.add(cardMock);
		Object[] responseMock = {playerMock2.getID(), ClientCommands.selectCard, arrayListObjectMock};
		
		when(playerMock2.getHand()).thenReturn(hand);
		when(spaceMock.getp(
				new FormalField(Integer.class),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		
		handler.triggerEffect(11, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playMilitiaTimeout() throws InterruptedException
	{
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock);
		hand.add(cardMock);
		hand.add(cardMock);
		hand.add(cardMock);
		
		when(playerMock2.getHand()).thenReturn(hand);
		when(spaceMock.getp(
				new FormalField(Integer.class),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(null);
		
		handler.triggerEffect(11, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playMilitiaDisconnected() throws InterruptedException
	{
		when(playerMock2.isConnected()).thenReturn(false);
		handler.triggerEffect(11, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playMilitiaSmallHand() throws InterruptedException
	{
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock);
		hand.add(cardMock);
		hand.add(cardMock);
		
		when(playerMock2.getHand()).thenReturn(hand);
		
		handler.triggerEffect(11, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playMoneyLender() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock2);
		hand.add(cardMock);
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		when(cardMock2.getName()).thenReturn("NotCopper");
		when(cardMock.getName()).thenReturn("Copper");
		when(playerMock1.getHand()).thenReturn(hand);
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		handler.triggerEffect(12, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playMoneyLenderMay() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(-1);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock2);
		hand.add(cardMock);
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		when(cardMock2.getName()).thenReturn("NotCopper");
		when(cardMock.getName()).thenReturn("Copper");
		when(playerMock1.getHand()).thenReturn(hand);
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		handler.triggerEffect(12, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playMoneyLenderNoCopper() throws InterruptedException
	{
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock2);
		hand.add(cardMock);

		when(cardMock2.getName()).thenReturn("NotCopper");
		when(cardMock.getName()).thenReturn("NotCopper");
		when(playerMock1.getHand()).thenReturn(hand);
		handler.triggerEffect(12, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playMoneyLenderTimeout() throws InterruptedException
	{
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock2);
		hand.add(cardMock);
		when(cardMock2.getName()).thenReturn("NotCopper");
		when(cardMock2.getName()).thenReturn("NotCopper");
		when(playerMock1.getHand()).thenReturn(hand);
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(null);
		handler.triggerEffect(12, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playPoacher() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock);
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		String[] names = {"testCard"};
		
		when(boardMock.getBoardNamesArray()).thenReturn(names);
		when(boardMock.canGain(anyString())).thenReturn(null);
		
		when(cardMock2.getName()).thenReturn("NotCopper");
		when(cardMock.getName()).thenReturn("Copper");
		when(playerMock1.getHand()).thenReturn(hand);
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		handler.triggerEffect(13, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playPoacherTimeout() throws InterruptedException
	{
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock);
		String[] names = {"testCard"};
		
		when(boardMock.getBoardNamesArray()).thenReturn(names);
		when(boardMock.canGain(anyString())).thenReturn(null);
		
		when(cardMock2.getName()).thenReturn("NotCopper");
		when(cardMock.getName()).thenReturn("Copper");
		when(playerMock1.getHand()).thenReturn(hand);
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(null);
		handler.triggerEffect(13, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playRemodel() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock);
		ArrayList<Card> boardList = new ArrayList<Card>();
		boardList.add(cardMock2);
		
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		when(playerMock1.getHand()).thenReturn(hand);
		when(cardMock.getName()).thenReturn("Card 1");
		when(cardMock2.getName()).thenReturn("Card 2");
		when(cardMock.getCost()).thenReturn(4);
		when(cardMock2.getCost()).thenReturn(3);
		when(boardMock.getCardStream()).thenReturn(boardList.stream());
		when(spaceMock.getp(
					new ActualField(playerMock1.getID()),
					new ActualField(ClientCommands.selectCard),
					new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		
		handler.triggerEffect(14, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playRemodelTimeout() throws InterruptedException
	{
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock);
		ArrayList<Card> boardList = new ArrayList<Card>();
		boardList.add(cardMock2);
		
		when(playerMock1.getHand()).thenReturn(hand);
		when(cardMock.getName()).thenReturn("Card 1");
		when(cardMock2.getName()).thenReturn("Card 2");
		when(cardMock.getCost()).thenReturn(4);
		when(cardMock2.getCost()).thenReturn(3);
		when(spaceMock.getp(
					new ActualField(playerMock1.getID()),
					new ActualField(ClientCommands.selectCard),
					new FormalField(ArrayListObject.class))).thenReturn(null);
		
		handler.triggerEffect(14, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playThroneroom() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock);
		
		int[] effectCodes = {0};
		String[] displayTypes = {"Action"};		
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		
		when(cardMock.getEffectCode()).thenReturn(effectCodes);
		when(cardMock.getDisplayTypes()).thenReturn(displayTypes);
		when(cardMock.getName()).thenReturn("Card Name");
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		when(playerMock1.getHand()).thenReturn(hand);
		when(spaceMock.getp(
					new ActualField(playerMock1.getID()),
					new ActualField(ClientCommands.selectCard),
					new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		
		handler.triggerEffect(15, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playThroneroomnoAction() throws InterruptedException
	{
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock);
		
		int[] effectCodes = {0};
		String[] displayTypes = {"notanAction"};
		when(cardMock.getEffectCode()).thenReturn(effectCodes);
		when(cardMock.getDisplayTypes()).thenReturn(displayTypes);
		when(cardMock.getName()).thenReturn("Card Name");
		when(playerMock1.getHand()).thenReturn(hand);
		handler.triggerEffect(15, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playThroneroomTimeOut() throws InterruptedException
	{
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock);
		
		int[] effectCodes = {0};
		String[] displayTypes = {"Action"};		
		
		when(cardMock.getEffectCode()).thenReturn(effectCodes);
		when(cardMock.getDisplayTypes()).thenReturn(displayTypes);
		when(cardMock.getName()).thenReturn("Card Name");
		when(playerMock1.getHand()).thenReturn(hand);
		when(spaceMock.getp(
					new ActualField(playerMock1.getID()),
					new ActualField(ClientCommands.selectCard),
					new FormalField(ArrayListObject.class))).thenReturn(null);
		
		handler.triggerEffect(15, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playBandit() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		
		LinkedBlockingDeque<Card> deck = new LinkedBlockingDeque<Card>();
		deck.add(cardMock2);
		deck.add(cardMock2);
		
		String[] displayTypes = {"Treasure"};
		String[] draw = {"Siver", "Silver"};
		
		ArrayList<Card> playerHand = new ArrayList<Card>();
		playerHand.add(cardMock2);
		playerHand.add(cardMock2);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		when(playerMock2.getID()).thenReturn(1);
		
		Object[] responseMock = {playerMock2.getID(), ClientCommands.selectCard, arrayListObjectMock};

		when(boardMock.canGain(anyString())).thenReturn(cardMock);
		when(playerMock2.getDeck()).thenReturn(deck);
		when(cardMock2.getName()).thenReturn("Silver");
		when(playerMock2.drawCard(anyInt())).thenReturn(draw);
		when(playerMock2.getHandSize()).thenReturn(2);
		when(playerMock2.getHand()).thenReturn(playerHand);
		when(cardMock2.getDisplayTypes()).thenReturn(displayTypes);
		when(spaceMock.getp(
					new FormalField(Integer.class),
					new ActualField(ClientCommands.selectCard),
					new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		
		handler.triggerEffect(16, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playBanditDisconnect() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		when(playerMock2.isConnected()).thenReturn(false);
		
		handler.triggerEffect(16, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playBanditTimeout() throws InterruptedException
	{
		LinkedBlockingDeque<Card> deck = new LinkedBlockingDeque<Card>();
		deck.add(cardMock2);
		deck.add(cardMock2);
		
		String[] displayTypes = {"Treasure"};
		String[] draw = {"Siver", "Silver"};
		
		ArrayList<Card> playerHand = new ArrayList<Card>();
		playerHand.add(cardMock2);
		playerHand.add(cardMock2);

		when(boardMock.canGain(anyString())).thenReturn(cardMock);
		when(playerMock2.getDeck()).thenReturn(deck);
		when(cardMock2.getName()).thenReturn("Silver");
		when(playerMock2.drawCard(anyInt())).thenReturn(draw);
		when(playerMock2.getHandSize()).thenReturn(2);
		when(playerMock2.getHand()).thenReturn(playerHand);
		
		when(cardMock2.getDisplayTypes()).thenReturn(displayTypes);
		when(spaceMock.getp(
				new FormalField(Integer.class),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(null);
		
		handler.triggerEffect(16, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playBanditNone() throws InterruptedException
	{
		LinkedBlockingDeque<Card> deck = new LinkedBlockingDeque<Card>();
		deck.add(cardMock2);
		deck.add(cardMock2);
		
		String[] displayTypes = {"NOPE"};
		String[] draw = {"Siver", "Silver"};
		
		ArrayList<Card> playerHand = new ArrayList<Card>();
		playerHand.add(cardMock2);
		playerHand.add(cardMock2);

		when(boardMock.canGain(anyString())).thenReturn(cardMock);
		when(playerMock2.getDeck()).thenReturn(deck);
		when(cardMock2.getName()).thenReturn("Silver");
		when(playerMock2.drawCard(anyInt())).thenReturn(draw);
		when(playerMock2.getHandSize()).thenReturn(2);
		when(playerMock2.getHand()).thenReturn(playerHand);
		when(cardMock2.getDisplayTypes()).thenReturn(displayTypes);
		
		handler.triggerEffect(16, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playBanditSilverCopper() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		
		LinkedBlockingDeque<Card> deck = new LinkedBlockingDeque<Card>();
		deck.add(cardMock2);
		deck.add(cardMock);
		
		String[] displayTypes = {"Treasure"};
		String[] draw = {"Silver", "Copper"};
		
		Object[] responseMock = {playerMock2.getID(), ClientCommands.selectCard, arrayListObjectMock};

		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		when(boardMock.canGain(anyString())).thenReturn(cardMock);
		when(playerMock2.getDeck()).thenReturn(deck);
		when(cardMock2.getName()).thenReturn("Silver");
		when(cardMock.getName()).thenReturn("Copper");
		when(playerMock2.drawCard(anyInt())).thenReturn(draw);
		when(playerMock2.getHandSize()).thenReturn(2);
		when(playerMock2.getHand()).thenAnswer( new Answer<Object>()
		{
			private int count = 0;
			ArrayList<Card> playerHand = new ArrayList<Card>();
			
			@Override
			public Object answer(InvocationOnMock arg0) throws Throwable
			{
				count++;
				if (count == 1)
				{
					playerHand.add(cardMock2);
					playerHand.add(cardMock);
					return playerHand;
				}
				playerHand.add(cardMock);
				playerHand.add(cardMock2);
				return playerHand;
			}
		});
		
		when(cardMock2.getDisplayTypes()).thenReturn(displayTypes);
		when(spaceMock.getp(
					new FormalField(Integer.class),
					new ActualField(ClientCommands.selectCard),
					new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		
		handler.triggerEffect(16, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playCouncilroom() throws InterruptedException
	{
		String[] draw = {"ActionCard"};
		when(playerMock2.drawCard(anyInt())).thenReturn(draw);
		handler.triggerEffect(17, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playCouncilroomDisconnect() throws InterruptedException
	{
		when(playerMock2.isConnected()).thenReturn(false);
		handler.triggerEffect(17, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playFestival() throws InterruptedException
	{
		handler.triggerEffect(18, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playLaboratory() throws InterruptedException
	{
		handler.triggerEffect(19, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playLibrary() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		
		
		LinkedBlockingDeque<Card> deck = new LinkedBlockingDeque<Card>();
		deck.add(cardMock2);
		String[] displayTypes = {"Action"};
		
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		when(playerMock1.getDeckSize()).thenReturn(1);
		when(playerMock1.getHandSize()).thenAnswer( new Answer<Object>()
		{
			private int count = 0;
			
			@Override
			public Object answer(InvocationOnMock arg0) throws Throwable
			{
				count++;
				return count;
			}
		});
		
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock2);
		when(playerMock1.getHand()).thenReturn(hand);
		when(cardMock2.getDisplayTypes()).thenReturn(displayTypes);
		
		
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		
		handler.triggerEffect(20, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playLibraryNonAction() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);

		
		LinkedBlockingDeque<Card> deck = new LinkedBlockingDeque<Card>();
		deck.add(cardMock2);
		String[] displayTypes = {"Treasure"};
		
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		when(playerMock1.getDeckSize()).thenReturn(1);
		when(playerMock1.getHandSize()).thenAnswer( new Answer<Object>()
		{
			private int count = 0;
			
			@Override
			public Object answer(InvocationOnMock arg0) throws Throwable
			{
				count++;
				return count;
			}
		});
		
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock2);
		when(playerMock1.getHand()).thenReturn(hand);
		when(cardMock2.getDisplayTypes()).thenReturn(displayTypes);
		
		
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		
		handler.triggerEffect(20, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playLibraryKeepAll() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(-1);
		
		
		LinkedBlockingDeque<Card> deck = new LinkedBlockingDeque<Card>();
		deck.add(cardMock2);
		String[] displayTypes = {"Action"};
		
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		when(playerMock1.getDeckSize()).thenReturn(1);
		when(playerMock1.getHandSize()).thenAnswer( new Answer<Object>()
		{
			private int count = 0;
			
			@Override
			public Object answer(InvocationOnMock arg0) throws Throwable
			{
				count++;
				return count;
			}
		});
		
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock2);
		when(playerMock1.getHand()).thenReturn(hand);
		when(cardMock2.getDisplayTypes()).thenReturn(displayTypes);
		
		
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		
		handler.triggerEffect(20, playerMock1, cardMock, boardMock, players);
	}
	@Test
	public void playLibraryTimeout() throws InterruptedException
	{
		LinkedBlockingDeque<Card> deck = new LinkedBlockingDeque<Card>();
		deck.add(cardMock2);
		String[] displayTypes = {"Action"};
		
		when(playerMock1.getDeckSize()).thenReturn(1);
		when(playerMock1.getHandSize()).thenReturn(0);
		when(playerMock1.isConnected()).thenAnswer(new Answer<Object>()
		{
			int count = 0;
			@Override
			public Object answer(InvocationOnMock arg0) throws Throwable
			{
				count++;
				return count < 2;
			}
		});
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(cardMock2);
		when(playerMock1.getHand()).thenReturn(hand);
		when(cardMock2.getDisplayTypes()).thenReturn(displayTypes);
		
		
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(null);
		
		handler.triggerEffect(20, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playMarket() throws InterruptedException
	{
		handler.triggerEffect(21, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playMine() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		
		String[] cardTypes = {"Treasure"};
		
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		
		when(cardMock.getDisplayTypes()).thenReturn(cardTypes);
		when(cardMock.getCost()).thenReturn(4);
		when(cardMock.getName()).thenReturn("TreasureDummy");

		when(boardMock.getCardStream()).thenReturn(list.stream());
		when(playerMock1.getHand()).thenReturn(list);
		
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);	
		handler.triggerEffect(22, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playMineNoTreasure() throws InterruptedException
	{
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		
		String[] cardTypes = {"ACTION"};
		
		when(cardMock.getDisplayTypes()).thenReturn(cardTypes);
		when(cardMock.getCost()).thenReturn(4);
		when(cardMock.getName()).thenReturn("TreasureDummy");

		when(boardMock.getCardStream()).thenReturn(list.stream());
		when(playerMock1.getHand()).thenReturn(list);
		
		handler.triggerEffect(22, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playMineTimeOut() throws InterruptedException
	{
		
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		
		String[] cardTypes = {"Treasure"};
		
		
		when(cardMock.getDisplayTypes()).thenReturn(cardTypes);
		when(cardMock.getCost()).thenReturn(4);
		when(cardMock.getName()).thenReturn("TreasureDummy");

		when(boardMock.getCardStream()).thenReturn(list.stream());
		when(playerMock1.getHand()).thenReturn(list);
		
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(null);	
		handler.triggerEffect(22, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playMineTimeout2() throws InterruptedException
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		
		String[] cardTypes = {"Treasure"};
		
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		
		when(cardMock.getDisplayTypes()).thenReturn(cardTypes);
		when(cardMock.getCost()).thenReturn(4);
		when(cardMock.getName()).thenReturn("TreasureDummy");

		when(boardMock.getCardStream()).thenReturn(list.stream());
		when(playerMock1.getHand()).thenReturn(list);
		
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenAnswer(new Answer<Object>()
				{
					int count = 0;
					@Override
					public Object answer(InvocationOnMock arg0) throws Throwable
					{
						if (count++ == 1)
							return responseMock;
						return null;
					}
				});
		handler.triggerEffect(22, playerMock1, cardMock, boardMock, players);
	}

	@Test
	public void playSentryDiscard() throws InterruptedException 
	{
		String[] drawn = {"card1", "card2"};
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		selection.add(1);
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		list.add(cardMock2);

		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};

		when(playerMock1.getHand()).thenReturn(list);
		
		when(playerMock1.getHandSize()).thenReturn(2);
		when(cardMock.getName()).thenReturn("Card Mock");
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		when(playerMock1.drawCard(anyInt())).thenReturn(drawn);
		when(boardMock.canGain(anyString())).thenReturn(cardMock2);
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		
		handler.triggerEffect(23, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playSentryDiscardTimeout() throws InterruptedException 
	{
		String[] drawn = {"card1", "card2"};
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		list.add(cardMock2);


		when(playerMock1.getHand()).thenReturn(list);
		
		when(playerMock1.getHandSize()).thenReturn(2);
		when(cardMock.getName()).thenReturn("Card Mock");
		when(playerMock1.drawCard(anyInt())).thenReturn(drawn);
		when(boardMock.canGain(anyString())).thenReturn(cardMock2);
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(null);
		
		handler.triggerEffect(23, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playSentryTrash() throws InterruptedException 
	{
		String[] drawn = {"card1", "card2"};
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		list.add(cardMock2);

		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};

		when(playerMock1.getHand()).thenReturn(list);
		
		when(playerMock1.getHandSize()).thenReturn(2);
		when(cardMock.getName()).thenReturn("Card Mock");
		when(arrayListObjectMock.getArrayList()).thenAnswer(new Answer<Object>()
		{
			int count = 0;
			@Override
			public Object answer(InvocationOnMock arg0) throws Throwable
			{
				ArrayList<Integer> selection = new ArrayList<Integer>();
				count++;
				if (count == 1)
				{
					selection.add(-1);
				}
				else
				{
					selection.add(0);
					selection.add(1);
				}
				return selection;
			}
		});
		when(playerMock1.drawCard(anyInt())).thenReturn(drawn);
		when(boardMock.canGain(anyString())).thenReturn(cardMock2);
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		
		handler.triggerEffect(23, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playSentryTrashTimeout() throws InterruptedException 
	{
		String[] drawn = {"card1", "card2"};
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		list.add(cardMock2);

		when(playerMock1.getHand()).thenReturn(list);
		when(playerMock1.isConnected()).thenAnswer(new Answer<Object>()
		{
			int count = 0;
			@Override
			public Object answer(InvocationOnMock arg0) throws Throwable
			{
				count++;
				return count != 1;
			}
		});
				
		when(playerMock1.getHandSize()).thenReturn(2);
		when(cardMock.getName()).thenReturn("Card Mock");
		when(arrayListObjectMock.getArrayList()).thenAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(InvocationOnMock arg0) throws Throwable
			{
				ArrayList<Integer> selection = new ArrayList<Integer>();

				selection.add(-1);
				return selection;
			}
		});
		when(playerMock1.drawCard(anyInt())).thenReturn(drawn);
		when(boardMock.canGain(anyString())).thenReturn(cardMock2);
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenAnswer(new Answer<Object>()
				{
					int count = 0;
					@Override
					public Object answer(InvocationOnMock arg0) throws Throwable
					{
						count++;
						if (count == 1)
						{
							Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
							return responseMock;
						}
						return null;
					}
				});
		
		handler.triggerEffect(23, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playSentryOntoDeckTimeout() throws InterruptedException 
	{
		String[] drawn = {"card1", "card2"};
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		list.add(cardMock2);

		when(playerMock1.getHand()).thenReturn(list);
		when(playerMock1.isConnected()).thenAnswer(new Answer<Object>()
		{
			int count = 0;
			@Override
			public Object answer(InvocationOnMock arg0) throws Throwable
			{
				count++;
				return count != 2;
			}
		});
				
		when(playerMock1.getHandSize()).thenReturn(2);
		when(cardMock.getName()).thenReturn("Card Mock");
		when(arrayListObjectMock.getArrayList()).thenAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(InvocationOnMock arg0) throws Throwable
			{
				ArrayList<Integer> selection = new ArrayList<Integer>();
				selection.add(-1);
				return selection;
			}
		});
		when(playerMock1.drawCard(anyInt())).thenReturn(drawn);
		when(boardMock.canGain(anyString())).thenReturn(cardMock2);
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenAnswer(new Answer<Object>()
				{
					int count = 0;
					@Override
					public Object answer(InvocationOnMock arg0) throws Throwable
					{
						count++;
						if (count < 3)
						{
							Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
							return responseMock;
						}
						return null;
					}
				});
		
		handler.triggerEffect(23, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playSentryOntoDeck() throws InterruptedException 
	{
		String[] drawn = {"card1", "card2"};
		
		LinkedBlockingDeque<Card> deck = new LinkedBlockingDeque<Card>();
		
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		list.add(cardMock2);

		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};

		when(cardMock.getName()).thenReturn("Card Mock1");
		when(cardMock2.getName()).thenReturn("Card Mock2");
		when(arrayListObjectMock.getArrayList()).thenAnswer(new Answer<Object>()
		{
			int count = 0;
			@Override
			public Object answer(InvocationOnMock arg0) throws Throwable
			{
				ArrayList<Integer> selection = new ArrayList<Integer>();

				count++;
				if (count == 3)
				{
					selection.add(0);
					selection.add(1);
				}
				else
				{
					selection.add(-1);
				}
				return selection;
			}
		});
		when(playerMock1.drawCard(anyInt())).thenReturn(drawn);
		when(playerMock1.getHand()).thenReturn(list);
		when(playerMock1.getDeck()).thenReturn(deck);
		when(playerMock1.getHandSize()).thenReturn(2);
		when(boardMock.canGain(anyString())).thenReturn(cardMock2);
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		
		handler.triggerEffect(23, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playWitch() throws InterruptedException
	{
		when(boardMock.canGain(anyString())).thenReturn(cardMock2);
		handler.triggerEffect(24, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playWitchDisconnected() throws InterruptedException
	{
		when(playerMock2.isConnected()).thenReturn(false);
		handler.triggerEffect(24, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playWitchNoMore() throws InterruptedException
	{
		when(boardMock.canGain(anyString())).thenReturn(null);
		handler.triggerEffect(24, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playArtisan() throws InterruptedException 
	{
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		LinkedBlockingDeque<Card> cards = new LinkedBlockingDeque<Card>();
		cards.add(cardMock);
		
		when(cardMock.getCost()).thenReturn(5);
		when(cardMock.getName()).thenReturn("cardMock");
		when(playerMock1.getDeck()).thenReturn(cards);
		when(playerMock1.getHand()).thenReturn(list);
		
		when(boardMock.getCardStream()).thenReturn(list.stream());
		when(boardMock.canGain(anyString())).thenReturn(cardMock);
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		handler.triggerEffect(25, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playArtisanTimeout1() throws InterruptedException 
	{
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		
		when(cardMock.getCost()).thenReturn(5);
		when(cardMock.getName()).thenReturn("cardMock");
		
		when(boardMock.getCardStream()).thenReturn(list.stream());
		when(boardMock.canGain(anyString())).thenReturn(cardMock);
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(null);
		handler.triggerEffect(25, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playArtisanTimeou2() throws InterruptedException 
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		when(playerMock1.isConnected()).thenReturn(false);
		
		
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		LinkedBlockingDeque<Card> cards = new LinkedBlockingDeque<Card>();
		cards.add(cardMock);
		
		when(cardMock.getCost()).thenReturn(5);
		when(cardMock.getName()).thenReturn("cardMock");
		when(playerMock1.getDeck()).thenReturn(cards);
		when(playerMock1.getHand()).thenReturn(list);
		
		when(boardMock.getCardStream()).thenReturn(list.stream());
		when(boardMock.canGain(anyString())).thenReturn(cardMock);
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenAnswer(new Answer<Object>()
				{
					int count = 0;
					@Override
					public Object answer(InvocationOnMock arg0) throws Throwable
					{
						count++;
						if (count == 1)
						{
							Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
							return responseMock;
						}
						return null;
					}
				});
		handler.triggerEffect(25, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playArtisanNonetoGain() throws InterruptedException 
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		
		
		ArrayList<Card> list = new ArrayList<Card>();
		list.add(cardMock);
		list.add(cardMock2);
		
		LinkedBlockingDeque<Card> cards = new LinkedBlockingDeque<Card>();
		cards.add(cardMock);
		
		when(cardMock.getCost()).thenReturn(0);
		when(cardMock.getName()).thenReturn("cardMock1");
		when(cardMock2.getCost()).thenReturn(10);
		when(cardMock2.getName()).thenReturn("cardMock2");
		
		
		when(playerMock1.getDeck()).thenReturn(cards);
		when(playerMock1.getHand()).thenReturn(list);
		
		when(boardMock.getCardStream()).thenReturn(list.stream());
		when(boardMock.canGain(cardMock.getName())).thenReturn(null);
		when(boardMock.canGain(cardMock2.getName())).thenReturn(cardMock2);

		handler.triggerEffect(25, playerMock1, cardMock, boardMock, players);
	}
	
	
	@Test
	public void playChapel() throws InterruptedException 
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(0);
		ArrayList<Card> cards = new ArrayList<Card>();
		cards.add(cardMock);
		
		
		when(playerMock1.getHand()).thenReturn(cards);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		handler.triggerEffect(26, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playChapelMay() throws InterruptedException 
	{
		ArrayList<Integer> selection = new ArrayList<Integer>();
		selection.add(-1);
		ArrayList<Card> cards = new ArrayList<Card>();
		cards.add(cardMock);
		
		
		when(playerMock1.getHand()).thenReturn(cards);
		when(arrayListObjectMock.getArrayList()).thenReturn(selection);
		Object[] responseMock = {playerMock1.getID(), ClientCommands.selectCard, arrayListObjectMock};
		
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(responseMock);
		handler.triggerEffect(26, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playChapelTimeout() throws InterruptedException 
	{
		ArrayList<Card> cards = new ArrayList<Card>();
		cards.add(cardMock);
		
		when(playerMock1.getHand()).thenReturn(cards);
		
		when(spaceMock.getp(
				new ActualField(playerMock1.getID()),
				new ActualField(ClientCommands.selectCard),
				new FormalField(ArrayListObject.class))).thenReturn(null);
		handler.triggerEffect(26, playerMock1, cardMock, boardMock, players);
	}
	
	@Test
	public void playSmithy() throws InterruptedException
	{
		handler.triggerEffect(27, playerMock1, cardMock, boardMock, players);
	}
}
