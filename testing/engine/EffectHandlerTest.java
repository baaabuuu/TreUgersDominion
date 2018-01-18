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
		LinkedBlockingDeque<Card> deck = new LinkedBlockingDeque<Card>();
		deck.add(cardMock);
		deck.add(cardMock2);
		
		when(boardMock.canGain(anyString())).thenReturn(cardMock);
		when(playerMock2.getDeck()).thenReturn(deck);
		when(cardMock.getName()).thenReturn("Copper");
		when(cardMock2.getName()).thenReturn("Silver");
		
		
		
		
		String[] displayTypes = {"Treasure"};		
		when(cardMock.getDisplayTypes()).thenReturn(displayTypes);
		when(cardMock2.getDisplayTypes()).thenReturn(displayTypes);
		when(spaceMock.getp(
					new ActualField(playerMock1.getID()),
					new ActualField(ClientCommands.selectCard),
					new FormalField(ArrayListObject.class))).thenReturn(null);
		
		handler.triggerEffect(15, playerMock1, cardMock, boardMock, players);
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
}
