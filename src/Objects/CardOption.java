package objects;

import java.util.List;

import cards.Card;

public class CardOption {
	private String message;
	private int amount;
	private List<Card> cards;
	public CardOption(String message, int amount, List<Card> cards) {
		this.message = message;
		this.amount = amount;
		this.cards = cards;
	}
	public String getMessage() {
		return message;
	}
	public int getAmount() {
		return amount;
	}
	public List<Card> getCards() {
		return cards;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
}
