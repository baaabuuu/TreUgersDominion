package objects;

public class OotAction {
	private String message;
	private int amount;
	public OotAction(String message, int amount) {
		this.message = message;
		this.amount = amount;
	}
	public String getMessage() {
		return message;
	}
	public int getAmount() {
		return amount;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
}

