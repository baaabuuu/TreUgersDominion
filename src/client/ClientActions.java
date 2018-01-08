package client;

import java.util.ArrayList;
import java.util.Arrays;
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
	public static void nonTurnAction(OotAction input) {
		System.out.print("\n\n" + input.getMessage());
		System.out.print("\nYour hand contains: ");
		printHand();
		
		Scanner scanner = new Scanner(System.in);
		
		ArrayList<Integer> selected = new ArrayList<Integer>();
		String number;
		int value;
		boolean locked;
		// For the amount of cards that is to be removed
		for(int i = 1; i<=input.getAmount();i++) {
			// Until a valid input is given, this code will run.
			locked = true;
			while(locked) {
				System.out.print("\nSelect card " + i + ": ");
				number = scanner.next();
				// Test if integer.
				try {
					value = Integer.parseInt(number);
					// If integer is not representing a card in hand.
					if(value <= 0 || value > playerHand.length) {
						System.out.println("Input is not a valid card.");
					} else { // If a valid integer, add to list and unlock while loop.
						selected.add(value);
						locked = false;
					}
				}catch(NumberFormatException e) {
					System.out.print("\nInput is not a valid integer.");
				}
			}
		}
		scanner.close();
	}
	public static void playerHand(Card[] newHand) {
		playerHand = newHand;
		System.out.print("\nYour new hand contains: ");
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
	public static void printHand() {
		
		// Simple print of current hand.
		for(int i = 0; i < playerHand.length; i++){
			System.out.print("\nCard " + (i+1) + ": " + playerHand[i].getName());
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
