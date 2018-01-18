package engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import cards.Card;
import log.Log;
import objects.PlayerEffects;

/**
 * A simple player object, used to handle the players in the game.
 * @author s164166
 */
public class Player 
{
	private int money, actions, buys, victoryPoints;
	//Insertion in the back and in the front in O(1) time due to linkedBlockingQueue
	private LinkedBlockingDeque <Card> deck = new LinkedBlockingDeque<Card>();
	private LinkedBlockingDeque <Card> discard = new LinkedBlockingDeque<Card>();
	private ArrayList<Card> hand = new ArrayList<Card>();
	private ArrayList<PlayerEffects> effects = new ArrayList<PlayerEffects>();
	private ArrayList<Card> playArea = new ArrayList<Card>();
	private ArrayList<Card> secretStack = new ArrayList<Card>();
	
	private boolean connected = true;
	private String name = "";
	private int id;
	
	/**
	 * Creates a player with the ID
	 * @param val
	 */
	public Player(int val)
	{
		id = val;
	}
	
	/**
	 * Returns the ID of a player.
	 * @return
	 */
	public int getID()
	{
		return id;
	}
	
	/**
	 * Draw n cards - if the deck is empty try to reshuffle, if that can't be done stop drawing.
	 * @param n - cards to draw
	 */
	public String[] drawCard(int n)
	{
		String[] draw = new String[n];
		for (int i = 0; i < n; i++)
		{
			if (deck.isEmpty())
			{
				if (discard.isEmpty())
				{
					draw[i] = null;
					break;
				}
				reshuffleDeck();
			}
			Card drawn = deck.poll();
			draw[i] = drawn.getName();
			hand.add(drawn);
			Log.log(getName() + " has drawn " + drawn.getName());
		}
		return draw;
	}
	/**
	 * Adds a PlayerEffects effect to the player
	 * @param effect
	 */
	public void addEffect(PlayerEffects effect)
	{
		Log.log(getName() + " added the effect " + effect);
		effects.add(effect);
	}
	/**
	 * Returns the list of effects
	 * @return
	 */
	public ArrayList<PlayerEffects> getEffects()
	{
		return effects;
	}
	/**
	 * Remove a specific player Effect
	 * @param effectName
	 */
	public void removeEffect(PlayerEffects effect)
	{
		effects.remove(effect);
	}
	
	/**
	 * Removes a card from the hand with the selected index.
	 * @param index
	 */
	public boolean removeFromHand(int index)
	{
		if (index >= 0 && index < hand.size() )
		{
			Log.log(getName() + " removed from hand: " + hand.get(index).getName());
			hand.remove(index);
			return true;
		}
		return false;
	}
	
	/**
	 * Removes a specific card from the hand
	 * @param card
	 * @return 
	 */
	public boolean removeFromHand(Card card)
	{
		Log.log(getName() + " removed from hand: " + card.getName());
		return hand.remove(card);
	}
	
	/**
	 * Adds money to the player's money.
	 * @param money
	 */
	public void addMoney(int money)
	{
		Log.log(getName() + " gained money " + money);
		this.money += money;
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
	 * Adds the card to the discard list
	 * @param card
	 */
	public void discardCard(Card card)
	{
		Log.log(getName() + " added to their discard pile: " + card.getName());
		discard.addFirst(card);
	}
	
	/**
	 * Shuffles the players deck - does not include discard pile.
	 */
	public void shuffleDeck()
	{
		Log.log(getName() + " just shuffled their deck.");
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
		Log.log(getName() + " just reshuffled their deck.");
		List<Card> tempList = new LinkedList<Card>();
		for (Card card : deck)
			tempList.add(card);
		for (Card card : discard)
			tempList.add(card);
		discard = new LinkedBlockingDeque<Card>();
		Collections.shuffle(tempList);
		deck = new LinkedBlockingDeque<Card> (tempList);
	}
	
	/**
	 * Checks if actions are left 
	 * @return
	 */
	public boolean canPlayAction() 
	{
		Log.log(getName() + " action count " + actions);
		return actions > 0;
	}
	
	
	/**
	 * Plays a card using an int
	 * @param index - index on hand
	 * @param phase - game phase 0 for actions, 1 for treasure
	 * @return
	 */
	public boolean playCard(int index, int phase)
	{
		if (index >= 0 && index < hand.size() )
		{
			Card card = getHand().get(index);
			List<String> types = Arrays.asList(card.getDisplayTypes());
			Log.important("contains actions: " + types.contains("Action") + " phase is " + phase
					+ " action playable: " + canPlayAction());
			if (types.contains("Action") && phase == 0 && canPlayAction())
			{
				actions--;
				removeFromHand(card);
				putIntoPlay(card);
				Log.log(getName() + " played the action card " + card.getName());
				return true;
			}
			else
			{
				if (types.contains("Treasure") && phase == 1)
				{
					removeFromHand(card);
					putIntoPlay(card);
					addMoney(card.getMoney());
					Log.log(getName() + " played the treasure card " + card.getName());
					return true;
				}
			}
		}		
		return false;
	}
	
	/**
	 * @param card
	 * @return
	 */
	public boolean playCard(Card card, int phase)
	{
		List<String> types = Arrays.asList(card.getDisplayTypes());
		if (types.contains("Action") && phase == 0 && canPlayAction())
		{
			actions--;
			removeFromHand(card);
			putIntoPlay(card);
			addMoney(card.getMoney());
			Log.log(getName() + " played the action card " + card.getName());
			return true;
		}
		else
		{
			if (types.contains("Treasure") && phase == 1)
			{
				addMoney(card.getMoney());
				removeFromHand(card);
				putIntoPlay(card);
				Log.log(getName() + " played the treasure card " + card.getName());
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the player has enough money left to buy.
	 * @param cost
	 * @return
	 */
	public boolean canPay(int cost)
	{
		Log.log(getName() + " tries to pay " + cost + " has " + money + " gold");
		return money >= cost;
	}
	/**
	 * Buys the card triggers - buyEffects
	 * @return
	 */
	public boolean buy(Card card, int phase)
	{
		if (phase == 1 && getBuys() > 0 && canPay(card.getCost()))
		{
			Log.log(getName() + " bought " + card.getName());
			money -= card.getCost();
			buys--;
			buyEffects(card);
			return true;
		}
		return false;
		
	}
	
	/**
	 * Buy effects, used to run effects that do something instead of putting it ontop of the discard pile.
	 * Note, none of these are used as non in the base game utilize such as an effect
	 * However, the skeleton for it is here and an example is given for a card
	 * @param card
	 */
	private void buyEffects(Card card)
	{
		//Used to mark whether the buy action was affected by the placement.
		for (int i = 0; i < card.getTypeCount(); i++)
		{
			if (card.getTypes()[i].equals("buyEffect"))
			{
				switch(card.getEffectCode()[i])
				{
					default:
						break;
				}
			}
		}
		
		Boolean deckPlacementEffect = false;
		for (PlayerEffects effect : effects)
		{
			switch (effect)
			{
				case ontoDeck:
					deckPlacementEffect = true;
					deck.addFirst(card);
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
	
	/**
	 * puts a card in the secret stack used to calculate totalsizes and victory Points
	 * @param card
	 */
	public void gain(Card card)
	{
		secretStack.add(card);
		calcVictory();
	}
	
	/**
	 * When a trash.
	 * @param card
	 */
	public void trash(Card card)
	{
		secretStack.remove(card);
		calcVictory();
	}

	/**
	 * Calculates victory Points for the player
	 */
	public void calcVictory()
	{
		int size = secretStack.size();
		victoryPoints = 0;
		for (Card card: secretStack)
		{
			for (int i = 0; i < card.getTypeCount(); i++)
			{
				if (card.getTypes()[i].equals("victory"))
				{
					victoryPoints += card.getVP();
				}
				else if (card.getTypes()[i].equals("victoryEffect"))
				{
					switch (card.getEffectCode()[i])
					{
					case 10 : //Garden
						victoryPoints += Math.floorDiv(size, 10);
						break;
					default :
						break;
					}
				}					
			}
		}
		Log.log("Calculated " + getName() + "'s victory points to " + victoryPoints);
	}

	public int getActions()
	{
		return actions;
	}

	public void setActions(int actions)
	{
		this.actions = actions;
	}
	
	public void addActions(int actions) 
	{
		Log.log(getName() + " added " + actions +" actions");
		this.actions += actions;
	}


	public int getVictoryPoints()
	{
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
	 * Sets the money of a player to 0.
	 */
	public void resetMoney()
	{
		money = 0;
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

	/**
	 * Returns whether a player is connected or not.
	 * @return
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Sets whether a played is connected or not.
	 * @param connected
	 */
	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	/**
	 * Gets the name of a player
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of a player.
	 * @param name
	 */
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
		Log.important("hand size: " + hand.size());
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
	
	public void putIntoPlay(Card card)
	{
		playArea.add(card);
	}
	
	public int getFirstIndexOf(String cardName)
	{
		for (int i = 0; i < hand.size(); i++)
			if (hand.get(i).getName().equals(cardName))
				return i;
		return -1;
	}
	
	public ArrayList<Card> getAllCards()
	{
		return secretStack;
	}
}
