package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
	private int playerID;
	private Card[] buyArea;
	private List<Card> playerHand;
	
	Space userSpace;
	private UIController userInterface;
	
	public ClientActions(int playerID, Space userSpace, UIController userInterface){
		this.playerID = playerID;
		this.userSpace = userSpace;
		this.userInterface = userInterface;
	}
	/**
	 * Runs the player turn.
	 * @param Space (local)
	 * @param Space (remote)
	 * @throws InterruptedException 
	 */
	public void takeTurn(Space hostSpace) throws InterruptedException {
		
		userInterface.eventInput("\n----------------------");
		userInterface.eventInput("");
		userInterface.eventInput("YOUR TURN HAS BEGUN!");
		userInterface.eventInput("Your hand contains: ");
		userInterface.newPlayerHand(playerHand);
		setTurnValues(new TurnValues(1,1,0));
		userInterface.eventInput("ACTION PHASE");
		actionPhase(hostSpace);
		
		userInterface.eventInput("BUY PHASE");
		buyPhase(hostSpace);
		
		userInterface.eventInput("CLEANUP PHASE");
		userInterface.eventInput("Your board is being cleared of used cards, you gain a new hand and your turn ends.");
		
		hostSpace.get(new ActualField(ServerCommands.setPlayerHand), new ActualField(playerID));
		Object[] objs = hostSpace.get(new ActualField(playerID), 
				new FormalField(PlayerHand.class));
		
		setPlayerHand((PlayerHand)objs[1]);
		
		userInterface.eventInput("Your hand contains: ");
		userInterface.newPlayerHand(playerHand);
	}
	/**
	 * Resolves a card being played.
	 * @param Space (local)
	 * @param Space (remote)
	 * @throws InterruptedException 
	 */
	private void resolvePlay(Space hostSpace) throws InterruptedException {
		Log.log("Resolving a play.");
		Object[] objs;
		Object[] input;
		boolean lock = true;
		while(lock) {
			//Wait for a server command
			objs = hostSpace.get(new FormalField(ServerCommands.class), new ActualField(playerID));
			//Reacy dependent on which command was received.
			switch ((ServerCommands)objs[0]) {
				// Display the received message.
				case message: Log.log("Recieved message command");
						input = hostSpace.get(new ActualField(playerID), 
							new FormalField(String.class));
						userInterface.eventInput((String)input[1]);
						break;
				case invalid: Log.log("Recieved invalid command");
						input = hostSpace.get(new ActualField(playerID), 
								new FormalField(String.class));
						userInterface.eventInput((String)input[1]);
						lock = false;
						break;
				case takeTurn: Log.log("Recieved takeTurn command");
						input = hostSpace.get(new ActualField(playerID), 
								new FormalField(BoardState.class), new FormalField(PlayerHand.class), new FormalField(TurnValues.class));
						
						userInterface.newBoardState((BoardState)input[1]);
						setPlayerHand((PlayerHand)input[2]);
						setTurnValues((TurnValues)input[3]);
						
						userInterface.newPlayerHand(playerHand);
						lock = false;
						break;
				case playerSelect: Log.log("Recieved playerSelect command");
						input = hostSpace.get(new ActualField(playerID), 
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
	private void actionPhase(Space hostSpace) throws InterruptedException {
		
		int value;
		Object input[];
		boolean lock = true;
		while(lock) {
			userInterface.eventInput("Play an Action Card, or skip the action phase by typing '0'.");
			userInterface.awaitingUserInput();
			input = userSpace.get(new ActualField("client"),new ActualField("eventOutput"),new FormalField(String.class));
			try {
				value = Integer.parseInt((String)input[2]);
				
				if(value < 0 || value > playerHand.size()) {
					userInterface.eventInput("Input is not a valid card.");
				
				//If player wants to get out of Action phase
				} else if(value == 0) {
					userInterface.eventInput("Action phase has ended.");
					hostSpace.put(playerID, ClientCommands.changePhase);
					lock = false;
				}else {
					hostSpace.put(playerID, ClientCommands.playCard);
					hostSpace.put(playerID, value-1);
					resolvePlay(hostSpace);
				}
			}catch(NumberFormatException e) {
				userInterface.eventInput("Input is not a valid integer.");
			}
		}
	}
	/**
	 * Runs the buy phase of the game.
	 * @param Space (local)
	 * @param Space (remote)
	 * @throws InterruptedException 
	 */
	public void buyPhase(Space hostSpace) throws InterruptedException {
		
		Object[] input;
		int value;
		boolean lock = true;
		boolean lock2;
		
		while(lock) {
			lock2 = true;
			userInterface.eventInput("Either play non-action cards or buy cards, or skip the Buy phase by typing '0'.");
			userInterface.eventInput("To play cards type 'p', to buy cards type 'b'.");
			//promt
			userInterface.awaitingUserInput();
			input = userSpace.get(new ActualField("client"),new ActualField("eventOutput"),new FormalField(String.class));
			switch((String)input[2]) {
				case "p":
					userInterface.newPlayerHand(playerHand);
					while(lock2) {
						userInterface.eventInput("Play a non-action card from your hand, type '0' when done playing cards: ");
						userInterface.awaitingUserInput();
						input = userSpace.get(new ActualField("client"),new ActualField("eventOutput"),new FormalField(String.class));
						try {
							value = Integer.parseInt((String)input[2]);
							
							if(value < 0 || value > playerHand.size()) {
								userInterface.eventInput("Input is not a valid card.");
							
							//If player wants to get out of Action phase
							} else if(value == 0) {
								userInterface.eventInput("");
								lock2 = false;
							}else {
								hostSpace.put(playerID, ClientCommands.playCard);
								hostSpace.put(playerID, value-1);
								resolvePlay(hostSpace);
							}
						}catch(NumberFormatException e) {
							userInterface.eventInput("Input is not a valid integer.");
						}
					}
					break;
				case "b":
					while(lock2) {
						userInterface.eventInput("Select a card to buy, type '0' when done playing cards: ");
						try {
							userInterface.awaitingUserInput();
							input = userSpace.get(new ActualField("client"),new ActualField("eventOutput"),new FormalField(String.class));
							value = Integer.parseInt((String)input[2]);
							if(value < 0 || value > buyArea.length) {
								userInterface.eventInput("Input is not a valid card.");
							
							//If player wants to get out of Action phase
							} else if(value == 0) {
								userInterface.eventInput("You ended the Action Phase\n");
								lock2 = false;
							}else {
								Log.important("Sending buyCard command for: " + buyArea[value-1].getName());
								hostSpace.put(playerID, ClientCommands.buyCard);
								hostSpace.put(playerID, buyArea[value-1].getName());
								resolvePlay(hostSpace);
							}
						}catch(NumberFormatException e) {
							userInterface.eventInput("Input is not a valid integer.");
						}
					}
					break;
				case "0":
					userInterface.eventInput("Buy phase has ended.");
					hostSpace.put(playerID, ClientCommands.changePhase);
					lock = false;
				default: userInterface.eventInput("Not a valid input!");
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
	public void playerSelect(CardOption option, Space hostSpace) throws InterruptedException {
		userInterface.eventInput("\n" + option.getMessage());
		userInterface.eventInput("Your options are: ");
		userInterface.newPlayerHand(option.getCards());
		
		if(option.getMay()) {
			userInterface.eventInput("You can choose to stop selecting cards by typing '0'.");
		}
		
		ArrayList<Integer> selected = new ArrayList<Integer>();
		Object[] input;
		int value;
		boolean locked;
		// For the amount of cards that is to be removed
		for(int i = 0; i<option.getAmount();i++) {
			// Until a valid input is given, this code will run.
			locked = true;
			while(locked) {
				userInterface.eventInput("Select card " + (i+1) + ": ");
				input = userSpace.get(new ActualField("client"),new ActualField("eventOutput"),new FormalField(String.class));
				
				// Test if integer.
				try {
					value = Integer.parseInt((String)input[2]);
					//If getMay is true and if integer is not either a value representing a card or 0.
					if(option.getMay() && (value < 0 || value > option.getCards().size())) {
						userInterface.eventInput("Input is not a valid card.");
					// if getMay is false and if integer is not a value representing a card.
					} else if(!option.getMay() && (value <= 0 || value > option.getCards().size())){
						userInterface.eventInput("Input is not a valid card.");
					} else { // If an integer not already in list, add to list and unlock while loop.
						if(selected.contains(value)) {
							userInterface.eventInput("That card has already been selected.");
						}else {
							if(value == 0) {
								i = option.getAmount();
							} else {
								selected.add(value);
							}
							locked = false;
						}
					}
				}catch(NumberFormatException e) {
					userInterface.eventInput("Input is not a valid integer.");
				}
			}
		}
		hostSpace.put(playerID, ClientCommands.selectCard);
		hostSpace.put(playerID, selected);
	}
	public void setNames(String[] names) {
		userInterface.newPlayers(names);
	}
	public void setPlayerHand(PlayerHand input) throws InterruptedException {
		playerHand = input.getCards();
		userInterface.newPlayerHand(input.getCards());
	}
	public void setBuyArea(Card[] input) {
		this.buyArea = input;
		userInterface.newBuyArea(input);
	}
	public void displayLaunge(HashMap<Integer, Integer> lobbies, Space hostSpace) throws InterruptedException {
		userInterface.eventInput("Server sent a list of lobbies: ");
		Set<Integer> p = lobbies.keySet();
		for(int i : p) {
			userInterface.eventInput("Lobby " + i + " - " + lobbies.get(i) + "/4 players.");
		}
		userInterface.eventInput("To connect to a lobby, type in 'c'.");
		userInterface.eventInput("To make a new lobby, type in 'm'.");
		userInterface.eventInput("To update lobby list, type in 'u'.");
		Object[] input;
		userInterface.awaitingUserInput();
		Log.log("Waiting for user input");
		input = userSpace.get(new ActualField("client"),new ActualField("eventOutput"),new FormalField(String.class));
		Log.log("Recieved from userSpace: " + (String)input[2]);
		switch((String)input[2]) {
			case "c":
				int output;
				userInterface.eventInput("Type a lobby-number to connect to.");
				userInterface.awaitingUserInput();
				input = userSpace.get(new ActualField("client"),new ActualField("eventOutput"),new FormalField(String.class));
				
				if(((String)input[2]).matches("[0-9]+") && ((String)input[2]).length() < 5) {
					output = Integer.parseInt(((String)input[2]));
					
					if(lobbies.get(output) == null) {
						userInterface.eventInput("That game has not yet been created.");
						hostSpace.put(playerID,ClientCommands.getLobbies);
					}else {
						hostSpace.put(playerID,ClientCommands.enterLobby);
						hostSpace.put(playerID,output);
					}
				}else {
					userInterface.eventInput("Invalid input");
					hostSpace.put(playerID,ClientCommands.getLobbies);
				}
				break;
			case "m":
				hostSpace.put(playerID,ClientCommands.createLobby);
				break;
			case "u": 
				hostSpace.put(playerID,ClientCommands.getLobbies);
				Log.log("Sent getLobbies");
				break;
			default: userInterface.eventInput("Not a valid input!");
				hostSpace.put(playerID,ClientCommands.getLobbies);
				break;
		}
		
	}
	public void serverMessage(String message) throws InterruptedException {
		userInterface.eventInput(message);
	}
	private void setTurnValues(TurnValues values) {
		userInterface.newTurnValues(values);
	}
}
