package Engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import cards.Card;

public class Player {
	private int money, actions, buys, victoryPoints;
	//Insertion in the back and in the front in O(1) time due to linkedBlockingQueue
	private LinkedBlockingDeque <Card> deck = new LinkedBlockingDeque<Card>();
	private LinkedBlockingDeque <Card> discard = new LinkedBlockingDeque<Card>();
	private ArrayList<Card> hand = new ArrayList<Card>();
	private ArrayList<String> effects = new ArrayList<String>();
	
	private boolean connected = true;
	private String name = "";
	
	
	/**
	 * Draw n cards - if the deck is empty try to reshuffle, if that can't be done stop drawing.
	 * @param n - cards to draw
	 */
	public void drawCard(int n)
	{
		for (int i = 0; i < n; i++)
		{
			if (deck.isEmpty())
			{
				if (discard.isEmpty())
					break;
				reshuffleDeck();
			}
			hand.add(deck.poll());			
		}
	}
	public void addEffect(String effect) {
		this.effects.add(effect);
	}
	/**
	 * Removes a card from the hand with the selected index.
	 * @param index
	 */
	public void removeFromHand(int index)
	{
		hand.remove(index);
	}
	/**
	 * Adds money to the player's money.
	 * @param money
	 */
	public void addMoney(int money)
	{
		this.money += money;
	}
	
	/**
	 * Removes a specific card from the hand
	 * @param card
	 */
	public void removeFromHand(Card card)
	{
		hand.remove(card);
	}
	
	/**
	 * Adds the card to the discard list
	 * @param card
	 */
	public void discardCard(Card card)
	{
		discard.addFirst(card);
	}
	
	/**
	 * Shuffles the players deck - does not include discard pile.
	 */
	public void shuffleDeck()
	{
		List<Card> tempList = new LinkedList<Card>();
		for (Card card : deck)
			tempList.add(card);
		Collections.shuffle(tempList);
		deck = new LinkedBlockingDeque<Card> (tempList);
	}
	
	/**
	 * Shuffles the discard pile into the deck.
	 */
	public void reshuffleDeck()
	{
		List<Card> tempList = new LinkedList<Card>();
		for (Card card : deck)
			tempList.add(card);
		for (Card card : discard)
			tempList.add(card);
		discard = new LinkedBlockingDeque<Card>();
		Collections.shuffle(tempList);
		deck = new LinkedBlockingDeque<Card> (tempList);
	}
	
	public boolean canPlayAction()
	{
		return actions > 0;
	}
	
	public void playCard(Card card)
	{
		ArrayList<Card> tempHand= getHand();
		if (canPlayAction())
		{
			actions--;
			tempHand.remove(card);
			setHand(tempHand);
		}
	}
	
	/**
	 * Returns the money a player has.
	 * @return
	 */
	public int getMoney() 
	{
		return money;
	}
	
	/**
	 * Checks if the player has enough money left to buy.
	 * @param cost
	 * @return
	 */
	public boolean canPay(int cost)
	{
		return money >= cost;
	}
	/**
	 * Buys the card triggers - buyEffects
	 * @return
	 */
	public void buy(Card card)
	{
		if (getBuys() > 0 && canPay(card.getCost()))
		{
			money -= card.getCost();
			buyEffects(card);
		}
		
	}
	
	/**
	 * Buy effects, used to run effects that do something instead of putting it ontop of the discard pile.
	 * @param card
	 */
	private void buyEffects(Card card)
	{
		//Used to mark whether the buy action was affected by the placement.
		Boolean deckPlacementEffect = false;
		for (String effect : effects)
		{
			switch (effect)
			{
				case "BuyOnTopDeck":
					deckPlacementEffect = true;
					break;
				default :
					break;
			}
			
		}
		if (!deckPlacementEffect)
		{
			//Whenever a card is bought - and there is no card buying effects affected placements - its added to the top of the discard pile.
			discardCard(card);
		}
	}


	public int getActions() {
		return actions;
	}

	public void setActions(int actions) {
		this.actions = actions;
	}
	
	public void addActions(int actions) 
	{
		this.actions += actions;
	}


	public int getVictoryPoints() {
		return victoryPoints;
	}



	public void setVictoryPoints(int victoryPoints) {
		this.victoryPoints = victoryPoints;
	}

	/**
	 * Gets the buys for a player
	 * @return buys
	 */
	public int getBuys()
	{
		return buys;
	}
	
	/**
	 * Reset the buys for a player to 1
	 */
	public void resetBuys()
	{
		buys = 1;
	}
	
	/**
	 * Removes a buy from the player
	 */
	public void removeBuy()
	{
		buys--;
	}
	
	/**
	 * Adds n buys to the player
	 * @param n
	 */
	public void addBuys(int n)
	{
		buys += n;
	}

	public LinkedBlockingDeque<Card> getDeck() {
		return deck;
	}

	public void setDeck(LinkedBlockingDeque<Card> deck) {
		this.deck = deck;
	}

	public ArrayList<Card> getHand() {
		return hand;
	}

	public void setHand(ArrayList<Card> hand) {
		this.hand = hand;
	}

	public LinkedBlockingDeque<Card> getDiscard() {
		return discard;
	}

	public void setDiscard(LinkedBlockingDeque<Card> discard) {
		this.discard = discard;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Puts a card at the bottom of the deck
	 */
	public void addCardDeckBottom(Card card)
	{
		deck.offerLast(card);
	}
	
	/**
	 * Puts a card at the top of the deck
	 */
	public void addCardDecktop(Card card)
	{
		deck.offerFirst(card);
	}
	
	/**
	 * Returns the handsize of the player.
	 * @return
	 */
	public int getHandSize()
	{
		return hand.size();
	}
	
	/**
	 * Gets the discard stack size
	 * @return discard.size();
	 */
	public int getDiscardSize()
	{
		return discard.size();
	}
	/**
	 * Gets the deck stack size
	 * @return deck.size();
	 */
	public int getDeckSize()
	{
		return deck.size();
	}
	/**
	 *  Selects a card form a list of cards
	 * @param list - List of cards
	 * @param index - Card to be taken
	 */
	public Card select(List<Card> list,int index) {
		Card selected = list.get(index);
		return selected ;
	}
}
