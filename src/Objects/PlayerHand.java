package objects;

import cards.Card;

public class PlayerHand {
	Card[] cards;
	public PlayerHand(Card[] cards) {
		this.cards = cards;
	}
	public Card[] getCards(){
		return cards;
	}
	public void setCards(Card[] cards) {
		this.cards = cards;
	}
	
}
