package client;

public class ClientActions {
	private static String[] buyArea;
	private static String[] playerNames;
	
	public static void updateBoard(bState input) {
		System.out.println(input.trashCount);
	}
	public static void takeTurn() {
		
	}
	public static void nonTurnAction() {
		
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
	public static void gameEnd() {
		
	}
	
	
	
	
	
	public class bState {
		public int[] shopArea;
		public int[] handCount;
		public int[] deckCount;
		public int[] discardCount;
		public int trashCount;
		public int[] vpCount; 
		public bState(int[] a, int[] b, int[] c, int[] d, int e, int[] f) {
			this.shopArea = a;
			this.handCount = b;
			this.deckCount = c;
			this.discardCount = d;
			this.trashCount = e;
			this.vpCount = f;
		}
	}
}
