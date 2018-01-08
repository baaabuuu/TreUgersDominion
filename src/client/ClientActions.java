package client;

public class ClientActions {
	private static String[] buyArea;
	private static String[] playerNames;
	
	public static void updateBoard(bState input) {
		System.out.println(input.trashCount);
	}
	public static void takeTurn() {
		
	}
	public static void nonTurnAction(ootAction input) {
		
	}
	public static void playerHand() {
		
	}
	public static void buyArea(String[] input) {
		buyArea = input;
	}
	public static void setPlayerNames(String[] input) {
		playerNames = input;
	}
	public static void displayLaunge() {
		
	}
	public static void displayLobby() {
		
	}
	public static void currentPlayer() {
		
	}
	public static void gameEnd() {
		
	}
	
	public class bState {
		private int[] shopArea, handCount, deckCount, discardCount, vpCount;
		private int trashCount;
		public bState(int[] a, int[] b, int[] c, int[] d, int e, int[] f) {
			this.shopArea = a;
			this.handCount = b;
			this.deckCount = c;
			this.discardCount = d;
			this.trashCount = e;
			this.vpCount = f;
		}
		int[] getShopArea(){
			return shopArea;
		}
		int[] getHandCount(){
			return handCount;
		}
		int[] getDeckCount(){
			return deckCount;
		}
		int[] getDiscardCount(){
			return discardCount;
		}
		int getTrashCount(){
			return trashCount;
		}
		int[] getVpCount(){
			return vpCount;
		}
	}
	public class ootAction {
		private String message;
		private int amount;
		public ootAction(String message, int amount) {
			this.message = message;
			this.amount = amount;
		}
		String getMessage() {
			return message;
		}
		int getAmount() {
			return amount;
		}
	}
}
