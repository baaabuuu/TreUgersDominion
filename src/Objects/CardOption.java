package Objects;

import cards.Card;

public class CardOption {
	private String message;
	private int amount;
	private Card[] cards;
	public CardOption(String message, int amount) {
		this.message = message;
		this.amount = amount;
	}
	public String getMessage() {
		return message;
	}
	public int getAmount() {
		return amount;
	}
	public Card[] getCards() {
		return cards;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public void setCards(Card[] cards) {
		this.cards = cards;
	}
}
