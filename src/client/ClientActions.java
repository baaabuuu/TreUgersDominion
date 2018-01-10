package client;

import java.util.ArrayList;
import java.util.Scanner;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import Objects.BoardState;
import Objects.CardOption;
import Objects.OotAction;
import Objects.turnValues;
import cards.Card;

public class ClientActions {
	private String playerName;
	private turnValues playerValues;
	private Card[] buyArea;
	private String[] playerNames;
	private Card[] playerHand;
	static Scanner scan;
	
	
	public ClientActions(String playerName){
		this.playerName = playerName;
	}
	
	public void updateBoard(BoardState input) {
		System.out.println(input.getTrashCount());
	}
	public void takeTurn(Space clientSpace, Space hostSpace) throws InterruptedException {
		
		System.out.println("\n----------------------");
		System.out.println("\nYOUR TURN HAS BEGUN!");
		System.out.println("Your hand contains: ");
		printCards(playerHand);
		setTurnValues(new turnValues(1,1,0));
		printTurnValues(playerValues);
		System.out.println("\n----------------------");
		System.out.println("The Buy Area contains: ");
		printBuyArea();
		System.out.println("ACTION PHASE");
		scan = new Scanner(System.in);
		String number;
		int value;
		Object[] serverInput;
		boolean playerLock = true;
		boolean lock = true;
		while(playerLock) {
			System.out.println("Play an Action Card, or skip the action phase by typing '0'.");
			lock = true;
			while(lock) {
				number = scan.nextLine();
				
				try {
					value = Integer.parseInt(number);
					
					if(value < 0 || value > playerHand.length) {
						System.out.println("Input is not a valid card.");
					} else if(value == 0) {
						playerLock = false;
						lock = false;
					}else {
						hostSpace.put(playerName, value);
						serverInput = clientSpace.getp(new ActualField(playerName), 
								new FormalField(turnValues.class));
						setTurnValues((turnValues)serverInput[1]);
						
						serverInput = clientSpace.getp(new ActualField(playerName), 
								new FormalField(Card[].class));
						setPlayerHand((Card[])serverInput[1]);
						printCards(playerHand);
						
						
						
					}
				}catch(NumberFormatException e) {
					System.out.println("Input is not a valid integer.");
				}
				
				
			}
		}
		System.out.println("Select card: ");
		
		
		
		
		
		
		
		System.out.println("BUY PHASE");
		System.out.println("Either play non-action cards or buy cards, or skip the action phase by typing '0'.");
		
		System.out.println("CLEANUP PHASE");
		System.out.println("Your board is being cleared of used cards.");
		
		System.out.println("\nYour turn has ended.");
	}
	/**
	 * An action affecting the players hand, while it is not the players turn.
	 * @param OotAction
	 * @throws InterruptedException 
	 */
	public void nonTurnAction(OotAction input, Space hostSpace) throws InterruptedException {
		System.out.println("\n" + input.getMessage());
		System.out.println("Your hand contains: ");
		printCards(playerHand);
		selectCard(input.getAmount(),playerHand, hostSpace);
		
	}
	/**
	 * An action that provides the player a choice of cards.
	 * @param OotAction
	 * @throws InterruptedException 
	 */
	public void playerSelect(CardOption input, Space hostSpace) throws InterruptedException {
		System.out.println("\n" + input.getMessage());
		System.out.println("Your options are: ");
		printCards(input.getCards());
		selectCard(input.getAmount(),input.getCards(), hostSpace);
	}
	private void selectCard(int count, Card[] cards, Space hostSpace) throws InterruptedException {
		scan = new Scanner(System.in);
		
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
				number = scan.nextLine();
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
			hostSpace.put(playerName,selected);
		}
	}
	public void setPlayerHand(Card[] newHand) {
		playerHand = newHand;
		System.out.println("Your new hand contains: ");
		printCards(playerHand);
	}
	public void setBuyArea(Card[] input) {
		this.buyArea = input;
	}
	public void setPlayerNames(String[] input) {
		this.playerNames = input;
	}
	public void displayLaunge() {
		
	}
	public void displayLobby() {
		
	}
	public void currentPlayer() {
		
	}
	public void gameEnd() {
		
	}
	/**
	 * Prints out the given Card[].
	 * @param Card[]
	 */
	private void printCards(Card[] cards) {
		
		// Simple print of current hand.
		for(int i = 0; i < cards.length; i++){
			System.out.println("Card " + (i+1) + ": " + cards[i].getName());
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
	private void printBuyArea() {
		for(int i = 0; i < buyArea.length; i++){
			System.out.println("Card " + (i+1) + ": " + buyArea[i].getName() + "\nCost: "
					+ buyArea[i].getCost() + "\nDescription: \n" + buyArea[i].getDesc() + "\n");
		}
	}
	private void setTurnValues(turnValues values) {
		this.playerValues = values;
	}
	private void printTurnValues(turnValues values) {
		System.out.println("Actions: " + values.getAction());
		System.out.println("Buys: " + values.getBuy());
		System.out.println("Money: " + values.getMoney());
	}
	
	
	
}
