package client;

import java.util.ArrayList;
import java.util.Scanner;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import Objects.BoardState;
import Objects.CardOption;
import Objects.Commands;
import Objects.OotAction;
import Objects.PlayerHand;
import Objects.TurnValues;
import cards.Card;

public class ClientActions {
	private String playerName;
	private TurnValues playerValues;
	private Card[] buyArea;
	private String[] names;
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
		setTurnValues(new TurnValues(1,1,0));
		printTurnValues(playerValues);
		System.out.println("\n----------------------");
		System.out.println("The Buy Area contains: ");
		printBuyArea();
		System.out.println("ACTION PHASE");
		actionPhase(clientSpace, hostSpace);
		
		
		
		
		System.out.println("Select card: ");
		
		
		
		
		
		
		
		System.out.println("BUY PHASE");
		System.out.println("Either play non-action cards or buy cards, or skip the action phase by typing '0'.");
		
		System.out.println("CLEANUP PHASE");
		System.out.println("Your board is being cleared of used cards.");
		
		System.out.println("\nYour turn has ended.");
	}
	private void actionPhase(Space clientSpace, Space hostSpace) throws InterruptedException {
		
		scan = new Scanner(System.in);
		String number;
		int value;
		Object[] serverInput;
		boolean lock = true;
		boolean lock2 = true;
		Object[] objs;
		Object[] input;
		
		while(lock) {
			System.out.println("Play an Action Card, or skip the action phase by typing '0'.");
			number = scan.nextLine();
			
			try {
				value = Integer.parseInt(number);
				
				if(value < 0 || value > playerHand.length) {
					System.out.println("Input is not a valid card.");
				
				//If player wants to get out of Action phase
				} else if(value == 0) {
					System.out.println("Action phase has ended");
					hostSpace.put(playerName, value);
					lock = false;
				}else {
					hostSpace.put(playerName, value);
					while(lock2) {
						objs = clientSpace.get(new ActualField(playerName), 
								new FormalField(Commands.class));
						
						switch ((Commands)objs[1]) {
							case message:
									input = clientSpace.get(new ActualField(playerName), 
										new FormalField(String.class));
									System.out.println((String)input[1]);
									lock2 = false;
									break;
							case takeTurn:
									input = clientSpace.get(new ActualField(playerName), 
										new FormalField(String.class));
									lock2 = false;
									break;
							case playerSelect:
									input = clientSpace.get(new ActualField(playerName), 
										new FormalField(CardOption.class));
									playerSelect((CardOption)input[1],hostSpace);
									break;
							default: break;
						}
					}
					
					input = clientSpace.get(new ActualField(playerName), 
							new FormalField(BoardState.class));
						updateBoard((BoardState)input[1]);
						
					input = clientSpace.get(new ActualField(playerName), 
								new FormalField(PlayerHand.class));
						setPlayerHand((PlayerHand)input[1]);
						
					input = clientSpace.get(new ActualField(playerName), 
							new FormalField(TurnValues.class));
						setTurnValues((TurnValues)input[1]);
						
					
					
					serverInput = clientSpace.get(new ActualField(playerName), 
							new FormalField(TurnValues.class));
					setTurnValues((TurnValues)serverInput[1]);
					
					serverInput = clientSpace.get(new ActualField(playerName), 
							new FormalField(PlayerHand.class));
					setPlayerHand((PlayerHand)serverInput[1]);
					System.out.println("Your hand contains: ");
					printCards(playerHand);
					
					
					
				}
			}catch(NumberFormatException e) {
				System.out.println("Input is not a valid integer.");
			}
			
			
		}
		
		
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
	public void setPlayerHand(PlayerHand input) {
		playerHand = input.getCards();
		System.out.println("Your new hand contains: ");
		printCards(playerHand);
	}
	public void setBuyArea(Card[] input) {
		this.buyArea = input;
	}
	public void setNames(String[] input) {
		this.names = input;
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
	private void printCards(Card[] playerHand) {
		
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
	private void printBuyArea() {
		for(int i = 0; i < buyArea.length; i++){
			System.out.println("Card " + (i+1) + ": " + buyArea[i].getName() + "\nCost: "
					+ buyArea[i].getCost() + "\nDescription: \n" + buyArea[i].getDesc() + "\n");
		}
	}
	private void setTurnValues(TurnValues values) {
		this.playerValues = values;
	}
	private void printTurnValues(TurnValues values) {
		System.out.println("Actions: " + values.getAction());
		System.out.println("Buys: " + values.getBuy());
		System.out.println("Money: " + values.getMoney());
	}
	
	
	
}
