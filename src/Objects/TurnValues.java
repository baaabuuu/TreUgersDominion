package Objects;

public class TurnValues {
	private int action;
	private int buy;
	private int money;
	public TurnValues(int action, int buy, int money) {
		this.action = action;
		this.buy = buy;
		this.money = money;
	}
	public int getAction() {
		return action;
	}
	public int getBuy() {
		return buy;
	}
	public int getMoney() {
		return money;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public void setBuy(int buy) {
		this.buy = buy;
	}
	public void setMoney(int money) {
		this.money = money;
	}
}
