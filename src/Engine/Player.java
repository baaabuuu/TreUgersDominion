package Engine;

import java.util.ArrayList;

import cards.Card;

public class Player {
	private int money;
	private int actions;
	private int buys;
	private int victoryPoints;
	private ArrayList<Card> deck;
	private ArrayList<Card> hand;
	private ArrayList<Card> discard;
	private ArrayList<String> effects;
	private boolean connected = true;
	
	private String name = "";
	
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
	 * Buy the card
	 * @return
	 */
	public void buy(Card card, int cost)
	{
		money -= cost;
		buyEffects(card);
	}
	
	/**
	 * Buy effects, used to run effects that do something instead of putting it ontop of the discard pile.
	 * @param card
	 */
	private void buyEffects(Card card)
	{
		for (String effect : effects)
		{
			
		}
	}


	public int getActions() {
		return actions;
	}



	public void setActions(int actions) {
		this.actions = actions;
	}



	public int getVictoryPoints() {
		return victoryPoints;
	}



	public void setVictoryPoints(int victoryPoints) {
		this.victoryPoints = victoryPoints;
	}



	public ArrayList<Card> getDeck() {
		return deck;
	}



	public void setDeck(ArrayList<Card> deck) {
		this.deck = deck;
	}



	public ArrayList<Card> getHand() {
		return hand;
	}



	public void setHand(ArrayList<Card> hand) {
		this.hand = hand;
	}



	public ArrayList<Card> getDiscard() {
		return discard;
	}



	public void setDiscard(ArrayList<Card> discard) {
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


}
