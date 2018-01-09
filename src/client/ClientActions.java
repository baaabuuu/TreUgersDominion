package client;

import java.util.ArrayList;
import java.util.Scanner;

import Objects.BoardState;
import Objects.OotAction;
import cards.Card;

public class ClientActions {
	private static String[] buyArea;
	private static String[] playerNames;
	private static Card[] playerHand;
	
	public static void updateBoard(BoardState input) {
		System.out.println(input.getTrashCount());
	}
	public static void takeTurn() {
		
	}
	/**
	 * An action affecting the players hand, while it is not the players turn.
	 * @param OotAction
	 */
	public static void nonTurnAction(OotAction input) {
		System.out.println("\n" + input.getMessage());
		System.out.println("Your hand contains: ");
		printHand();
		selectCard(input.getAmount(),playerHand);
		
	}
	public static void playerSelect() {
		
	}
	private static void selectCard(int count, Card[] cards) {
		Scanner scanner = new Scanner(System.in);
		
		ArrayList<Integer> selected = new ArrayList<Integer>();
		String number;
		int value;
		boolean locked;
		// For the amount of cards that is to be removed
		for(int i = 1; i<=count;i++) {
			// Until a valid input is given, this code will run.
			locked = true;
			while(locked) {
				System.out.println("Select card " + i + ": ");
				number = scanner.nextLine();
				// Test if integer.
				try {
					value = Integer.parseInt(number);
					// If integer is not representing a card in hand.
					if(value <= 0 || value > playerHand.length) {
						System.out.println("Input is not a valid card.");
					} else { // If an integer not already in list, add to list and unlock while loop.
						if(selected.contains(value)) {
							System.out.println("That card has already been selected.");
						}else {
							selected.add(value);
							locked = false;
						}
					}
				}catch(NumberFormatException e) {
					System.out.println("Input is not a valid integer.");
				}
			}
		}
		scanner.close();
	}
	public static void playerHand(Card[] newHand) {
		playerHand = newHand;
		System.out.println("Your new hand contains: ");
		printHand();
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
	/**
	 * Prints out the cards in the players hand.
	 */
	public static void printHand() {
		
		// Simple print of current hand.
		for(int i = 0; i < playerHand.length; i++){
			System.out.println("Card " + (i+1) + ": " + playerHand[i].getName());
		}
		/*
		// More detailed print of current hand.
		for(int i = 0; i < playerHand.length; i++){
			System.out.print("******* \nCard " + (i+1) + ": " + playerHand[i].getName() + "  \n" + playerHand[i].getDesc() + " \nType: ");
			Arrays.asList(playerHand[i].getDisplayTypes()).stream().forEach(s -> System.out.print(s + " "));
			System.out.print("\n******* \n");
		}
		*/		
	}
	
	
	
	
}
