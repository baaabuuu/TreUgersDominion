package client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import cards.Card;
import log.Log;
import objects.*;

public class ClientActions {
	private String playerName;
	private TurnValues playerValues;
	private Card[] buyArea;
	private String[] names;
	private List<Card> playerHand;
	static Scanner scan;
	
	
	public ClientActions(String playerName){
		this.playerName = playerName;
	}
	
	public void displayBoardState(BoardState input) {
		
	}
	/**
	 * Runs the player turn.
	 * @param Space (local)
	 * @param Space (remote)
	 * @throws InterruptedException 
	 */
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
		
		System.out.println("BUY PHASE");
		buyPhase(clientSpace, hostSpace);
		
		System.out.println("CLEANUP PHASE");
		System.out.println("Your board is being cleared of used cards, you gain a new hand and your turn ends.");
		
		clientSpace.get(new ActualField(playerName), new ActualField(ServerCommands.setPlayerHand));
		Object[] objs = clientSpace.get(new ActualField(playerName), 
				new FormalField(PlayerHand.class));
		
		setPlayerHand((PlayerHand)objs[1]);
		
		System.out.println("Your hand contains: ");
		printCards(playerHand);
	}
	/**
	 * Resolves a card being played.
	 * @param Space (local)
	 * @param Space (remote)
	 * @throws InterruptedException 
	 */
	private void resolvePlay(Space clientSpace, Space hostSpace) throws InterruptedException {
		Object[] objs;
		Object[] input;
		boolean lock = true;
		while(lock) {
			objs = clientSpace.get(new ActualField(playerName), 
					new FormalField(ServerCommands.class));
			
			switch ((ServerCommands)objs[1]) {
				case message: Log.log("Recieved message command");
						input = clientSpace.get(new ActualField(playerName), 
							new FormalField(String.class));
						System.out.println((String)input[1]);
						lock = false;
						break;
				case takeTurn: Log.log("Recieved takeTurn command");
						input = clientSpace.get(new ActualField(playerName), 
							new FormalField(BoardState.class));
						displayBoardState((BoardState)input[1]);
						
						input = clientSpace.get(new ActualField(playerName), 
							new FormalField(PlayerHand.class));
						setPlayerHand((PlayerHand)input[1]);
						
						input = clientSpace.get(new ActualField(playerName), 
							new FormalField(TurnValues.class));
						setTurnValues((TurnValues)input[1]);
						System.out.println("Your hand contains: ");
						printCards(playerHand);
						lock = false;
						break;
				case playerSelect: Log.log("Recieved playerSelect command");
						input = clientSpace.get(new ActualField(playerName), 
							new FormalField(CardOption.class));
						playerSelect((CardOption)input[1],hostSpace);
						break;
				default: break;
			}
		}
	}
	/**
	 * Runs the action phase of the game.
	 * @param Space (local)
	 * @param Space (remote)
	 * @throws InterruptedException 
	 */
	private void actionPhase(Space clientSpace, Space hostSpace) throws InterruptedException {
		
		scan = new Scanner(System.in);
		String number;
		int value;
		boolean lock = true;
		while(lock) {
			System.out.println("Play an Action Card, or skip the action phase by typing '0'.");
			number = scan.nextLine();
			
			try {
				value = Integer.parseInt(number);
				
				if(value < 0 || value > playerHand.size()) {
					System.out.println("Input is not a valid card.");
				
				//If player wants to get out of Action phase
				} else if(value == 0) {
					System.out.println("Action phase has ended.");
					hostSpace.put(playerName, ClientCommands.changePhase);
					lock = false;
				}else {
					hostSpace.put(playerName, ClientCommands.playCard);
					hostSpace.put(playerName, value);
					resolvePlay(clientSpace, hostSpace);
				}
			}catch(NumberFormatException e) {
				System.out.println("Input is not a valid integer.");
			}
		}
	}
	/**
	 * Runs the buy phase of the game.
	 * @param Space (local)
	 * @param Space (remote)
	 * @throws InterruptedException 
	 */
	public void buyPhase(Space clientSpace, Space hostSpace) throws InterruptedException {
		
		scan = new Scanner(System.in);
		String scanInput;
		int value;
		boolean lock = true;
		boolean lock2;
		
		while(lock) {
			lock2 = true;
			System.out.println("Either play non-action cards or buy cards, or skip the Buy phase by typing '0'.");
			System.out.println("To play cards type 'p', to buy cards type 'b'.");
			scanInput = scan.nextLine();
			switch(scanInput) {
				case "p":
					printCards(playerHand);					
					while(lock2) {
						System.out.println("Play a non-action card from your hand, type '0' when done playing cards: ");
						scanInput = scan.nextLine();
						try {
							value = Integer.parseInt(scanInput);
							
							if(value < 0 || value > playerHand.size()) {
								System.out.println("Input is not a valid card.");
							
							//If player wants to get out of Action phase
							} else if(value == 0) {
								System.out.println("");
								lock2 = false;
							}else {
								hostSpace.put(playerName, ClientCommands.playCard);
								hostSpace.put(playerName, value);
								resolvePlay(clientSpace, hostSpace);
							}
						}catch(NumberFormatException e) {
							System.out.println("Input is not a valid integer.");
						}
					}
					break;
				case "b":
					while(lock2) {
						System.out.println("Select a card to buy, type '0' when done playing cards: ");
						printBuyArea();
						scanInput = scan.nextLine();
						try {
							value = Integer.parseInt(scanInput);
							
							if(value < 0 || value > buyArea.length) {
								System.out.println("Input is not a valid card.");
							
							//If player wants to get out of Action phase
							} else if(value == 0) {
								System.out.println("");
								lock2 = false;
							}else {
								hostSpace.put(playerName, ClientCommands.buyCard);
								hostSpace.put(playerName, buyArea[value]);
								resolvePlay(clientSpace, hostSpace);
							}
						}catch(NumberFormatException e) {
							System.out.println("Input is not a valid integer.");
						}
					}
					break;
				case "0":
					System.out.println("Buy phase has ended.");
					hostSpace.put(playerName, ClientCommands.changePhase);
					lock = false;
				default: System.out.println("Not a valid input!");
					break;
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
	 * @param CardOption
	 * @param Space
	 * @throws InterruptedException 
	 */
	public void playerSelect(CardOption input, Space hostSpace) throws InterruptedException {
		System.out.println("\n" + input.getMessage());
		System.out.println("Your options are: ");
		printCards(input.getCards());
		selectCard(input.getAmount(),input.getCards(), hostSpace);
	}
	private void selectCard(int count, List<Card> list, Space hostSpace) throws InterruptedException {
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
					if(value <= 0 || value > playerHand.size()) {
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
			hostSpace.put(playerName, ClientCommands.selectCard);
			hostSpace.put(playerName, selected);
		}
	}
	public void setNames(String[] names) {
		this.names = names;
	}
	public void setPlayerHand(PlayerHand input) {
		playerHand = input.getCards();
		System.out.println("Your new hand contains: ");
		printCards(playerHand);
	}
	public void setBuyArea(Card[] input) {
		this.buyArea = input;
	}
	public void displayLaunge(Launge lobbies) {
		System.out.println("Server sent a list of lobbies: ");
		for(int i = 0 ; i < lobbies.getLobbies().length; i++) {
			System.out.println("Lobby " + lobbies.getLobbies()[i] + " - " + lobbies.getplayerCount()[i] + "//4 players.");
		}
		System.out.println("Server sent a list of lobbies: ");
	}
	public void displayLobby() {
		
	}
	public void gameEnd() {
		
	}
	public void serverMessage(String message) {
		System.out.println("Server says: " + message);
	}
	/**
	 * Prints out the given list of cards.
	 * @param List<Card>
	 */
	private void printCards(List<Card> playerHand) {
		
		// Simple print of current hand.
		for(int i = 0; i < playerHand.size(); i++){
			System.out.println("Card " + (i+1) + ": " + playerHand.get(i).getName());
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
