package objects;

import java.util.List;

import cards.Card;

public class CardOption {
	private String message;
	private int amount;
	private List<Card> cards;
	private boolean may;
	public CardOption(String message, int amount, List<Card> cards, boolean may) {
		this.message = message;
		this.amount = amount;
		this.cards = cards;
		this.may = may;
	}
	
	public boolean getMay()
	{
		return may;
	}
	public void setmay(boolean may)
	{
		this.may = may;
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
