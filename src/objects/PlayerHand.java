package objects;

import java.util.List;

import cards.Card;

public class PlayerHand {
	List<Card> cards;
	public PlayerHand(List<Card> cards) {
		this.cards = cards;
	}
	public List<Card> getCards(){
		return cards;
	}
	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	
}
