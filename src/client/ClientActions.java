package client;

import Objects.BoardState;
import Objects.OotAction;

public class ClientActions {
	private static String[] buyArea;
	private static String[] playerNames;
	private static String[] playerHand;
	
	public static void updateBoard(BoardState input) {
		System.out.println(input.getTrashCount());
	}
	public static void takeTurn() {
		
	}
	public static void nonTurnAction(OotAction input) {
		System.out.println(input.getMessage());
		System.out.print("Your cards are:");
		System.out.print("Select card: ");
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
	public static void displayHand() {
		
	}
	
	
	
}
