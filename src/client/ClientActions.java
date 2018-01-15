package client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import cards.Card;
import clientUI.UIController;
import log.Log;
import objects.BoardState;
import objects.ClientCommands;
import objects.PlayerHand;
import objects.TurnValues;
import objects.*;

public class ClientActions {
	private String playerName;
	private TurnValues playerValues;
	private Card[] buyArea;
	private String[] names;
	private List<Card> playerHand;
	static Scanner scan;
	
	Space userSpace;
	private UIController userInterface;
	
	public ClientActions(String playerName, Space userSpace, UIController userInterface){
		this.playerName = playerName;
		this.userSpace = userSpace;
		this.userInterface = userInterface;
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
		
		userSpace.put("client","message","\\n----------------------");
		userSpace.put("client","message","");
		userSpace.put("client","message","\nYOUR TURN HAS BEGUN!");
		userSpace.put("client","message","Your hand contains: ");
		printCards(playerHand);
		setTurnValues(new TurnValues(1,1,0));
		printTurnValues(playerValues);
		userSpace.put("client","message","\n----------------------");
		userSpace.put("client","message","The Buy Area contains: ");
		printBuyArea();
		userSpace.put("client","message","ACTION PHASE");
		actionPhase(clientSpace, hostSpace);
		
		userSpace.put("client","message","BUY PHASE");
		buyPhase(clientSpace, hostSpace);
		
		userSpace.put("client","message","CLEANUP PHASE");
		userSpace.put("client","message","Your board is being cleared of used cards, you gain a new hand and your turn ends.");
		
		clientSpace.get(new ActualField(playerName), new ActualField(ServerCommands.setPlayerHand));
		Object[] objs = clientSpace.get(new ActualField(playerName), 
				new FormalField(PlayerHand.class));
		
		setPlayerHand((PlayerHand)objs[1]);
		
		userSpace.put("client","message","Your hand contains: ");
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
			//Wait for a server command
			objs = clientSpace.get(new ActualField(playerName), 
					new FormalField(ServerCommands.class));
			//Reacy dependent on which command was received.
			switch ((ServerCommands)objs[1]) {
				// Display the received message.
				case message: Log.log("Recieved message command");
						input = clientSpace.get(new ActualField(playerName), 
							new FormalField(String.class));
						userSpace.put("client","message",(String)input[1]);
						break;
				case invalid: Log.log("Recieved invalid command");
						input = clientSpace.get(new ActualField(playerName), 
								new FormalField(String.class));
						userSpace.put("client","message",(String)input[1]);
						lock = false;
						break;
				case takeTurn: Log.log("Recieved takeTurn command");
						input = clientSpace.get(new ActualField(playerName), 
								new FormalField(Object[].class));
						
						displayBoardState((BoardState)(((Object[])input[1])[0]));
						setPlayerHand((PlayerHand)(((Object[])input[1])[1]));
						setTurnValues((TurnValues)(((Object[])input[1])[2]));
						
						userSpace.put("client","message","Your hand contains: ");
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
			userSpace.put("client","message","Play an Action Card, or skip the action phase by typing '0'.");
			number = scan.nextLine();
			
			try {
				value = Integer.parseInt(number);
				
				if(value < 0 || value > playerHand.size()) {
					userSpace.put("client","message","Input is not a valid card.");
				
				//If player wants to get out of Action phase
				} else if(value == 0) {
					userSpace.put("client","message","Action phase has ended.");
					hostSpace.put(playerName, ClientCommands.changePhase);
					lock = false;
				}else {
					hostSpace.put(playerName, ClientCommands.playCard);
					hostSpace.put(playerName, value);
					resolvePlay(clientSpace, hostSpace);
				}
			}catch(NumberFormatException e) {
				userSpace.put("client","message","Input is not a valid integer.");
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
			userSpace.put("client","message","Either play non-action cards or buy cards, or skip the Buy phase by typing '0'.");
			userSpace.put("client","message","To play cards type 'p', to buy cards type 'b'.");
			scanInput = scan.nextLine();
			switch(scanInput) {
				case "p":
					printCards(playerHand);					
					while(lock2) {
						userSpace.put("client","message","Play a non-action card from your hand, type '0' when done playing cards: ");
						scanInput = scan.nextLine();
						try {
							value = Integer.parseInt(scanInput);
							
							if(value < 0 || value > playerHand.size()) {
								userSpace.put("client","message","Input is not a valid card.");
							
							//If player wants to get out of Action phase
							} else if(value == 0) {
								userSpace.put("client","message","");
								lock2 = false;
							}else {
								hostSpace.put(playerName, ClientCommands.playCard);
								hostSpace.put(playerName, value);
								resolvePlay(clientSpace, hostSpace);
							}
						}catch(NumberFormatException e) {
							userSpace.put("client","message","Input is not a valid integer.");
						}
					}
					break;
				case "b":
					while(lock2) {
						userSpace.put("client","message","Select a card to buy, type '0' when done playing cards: ");
						printBuyArea();
						scanInput = scan.nextLine();
						try {
							value = Integer.parseInt(scanInput);
							
							if(value < 0 || value > buyArea.length) {
								userSpace.put("client","message","Input is not a valid card.");
							
							//If player wants to get out of Action phase
							} else if(value == 0) {
								userSpace.put("client","message","");
								lock2 = false;
							}else {
								hostSpace.put(playerName, ClientCommands.buyCard);
								hostSpace.put(playerName, buyArea[value-1].getName());
								resolvePlay(clientSpace, hostSpace);
							}
						}catch(NumberFormatException e) {
							userSpace.put("client","message","Input is not a valid integer.");
						}
					}
					break;
				case "0":
					userSpace.put("client","message","Buy phase has ended.");
					hostSpace.put(playerName, ClientCommands.changePhase);
					lock = false;
				default: userSpace.put("client","message","Not a valid input!");
					break;
			}
		}
	}
	/**
	 * An action that provides the player a choice of cards.
	 * @param CardOption
	 * @param Space
	 * @throws InterruptedException 
	 */
	public void playerSelect(CardOption input, Space hostSpace) throws InterruptedException {
		userSpace.put("client","message","\n" + input.getMessage());
		userSpace.put("client","message","Your options are: ");
		printCards(input.getCards());
		
		if(input.getMay()) {
			userSpace.put("client","message","You can choose to stop selecting cards by typing '0'.");
		}
		
		scan = new Scanner(System.in);
		
		ArrayList<Integer> selected = new ArrayList<Integer>();
		String number;
		int value;
		boolean locked;
		// For the amount of cards that is to be removed
		for(int i = 0; i<input.getAmount();i++) {
			// Until a valid input is given, this code will run.
			locked = true;
			while(locked) {
				userSpace.put("client","message","Select card " + (i+1) + ": ");
				number = scan.nextLine();
				// Test if integer.
				try {
					value = Integer.parseInt(number);
					//If getMay is true and if integer is not either a value representing a card or 0.
					if(input.getMay() && (value < 0 || value > input.getCards().size())) {
						userSpace.put("client","message","Input is not a valid card.");
					// if getMay is false and if integer is not a value representing a card.
					} else if(!input.getMay() && (value <= 0 || value > input.getCards().size())){
						userSpace.put("client","message","Input is not a valid card.");
					} else { // If an integer not already in list, add to list and unlock while loop.
						if(selected.contains(value)) {
							userSpace.put("client","message","That card has already been selected.");
						}else {
							if(value == 0) {
								i = input.getAmount();
							} else {
								selected.add(value);
							}
							locked = false;
						}
					}
				}catch(NumberFormatException e) {
					userSpace.put("client","message","Input is not a valid integer.");
				}
			}
		}
		hostSpace.put(playerName, ClientCommands.selectCard);
		hostSpace.put(playerName, selected);
	}
	public void setNames(String[] names) {
		this.names = names;
	}
	public void setPlayerHand(PlayerHand input) throws InterruptedException {
		playerHand = input.getCards();
		userSpace.put("client","message","Your new hand contains: ");
		printCards(playerHand);
	}
	public void setBuyArea(Card[] input) {
		this.buyArea = input;
	}
	public void displayLaunge(Launge lobbies) throws InterruptedException {
		userSpace.put("client","message","Server sent a list of lobbies: ");
		for(int i = 0 ; i < lobbies.getLobbies().length; i++) {
			userSpace.put("client","message","Lobby " + lobbies.getLobbies()[i] + " - " + lobbies.getplayerCount()[i] + "//4 players.");
		}
		userSpace.put("client","message","Server sent a list of lobbies: ");
	}
	public void gameEnd() {
		
	}
	public void serverMessage(String message) throws InterruptedException {
		userSpace.put("client","message","Server says: " + message);
	}
	/**
	 * Prints out the given list of cards.
	 * @param List<Card>
	 * @throws InterruptedException 
	 */
	private void printCards(List<Card> playerHand) throws InterruptedException {
		
		// Simple print of current hand.
		for(int i = 0; i < playerHand.size(); i++){
			userSpace.put("client","message","Card " + (i+1) + ": " + playerHand.get(i).getName());
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
	private void printBuyArea() throws InterruptedException {
		for(int i = 0; i < buyArea.length; i++){
			userSpace.put("client","message","Card " + (i+1) + ": " + buyArea[i].getName() + "\nCost: "
					+ buyArea[i].getCost() + "\nDescription: \n" + buyArea[i].getDesc() + "\n");
		}
	}
	private void setTurnValues(TurnValues values) {
		this.playerValues = values;
	}
	private void printTurnValues(TurnValues values) throws InterruptedException {
		userInterface.newTurnValues(values);
	}
	
	
	
}
