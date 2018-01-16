package client;

import java.util.ArrayList;
import java.util.List;

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
	private Card[] buyArea;
	private String[] names;
	private List<Card> playerHand;
	
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
		
		userInterface.eventInput("\\n----------------------");
		userInterface.eventInput("");
		userInterface.eventInput("YOUR TURN HAS BEGUN!");
		userInterface.eventInput("Your hand contains: ");
		userInterface.newPlayerHand(playerHand);
		setTurnValues(new TurnValues(1,1,0));
		userInterface.eventInput("ACTION PHASE");
		actionPhase(clientSpace, hostSpace);
		
		userInterface.eventInput("BUY PHASE");
		buyPhase(clientSpace, hostSpace);
		
		userInterface.eventInput("CLEANUP PHASE");
		userInterface.eventInput("Your board is being cleared of used cards, you gain a new hand and your turn ends.");
		
		clientSpace.get(new ActualField(playerName), new ActualField(ServerCommands.setPlayerHand));
		Object[] objs = clientSpace.get(new ActualField(playerName), 
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
						userInterface.eventInput((String)input[1]);
						break;
				case invalid: Log.log("Recieved invalid command");
						input = clientSpace.get(new ActualField(playerName), 
								new FormalField(String.class));
						userInterface.eventInput((String)input[1]);
						lock = false;
						break;
				case takeTurn: Log.log("Recieved takeTurn command");
						input = clientSpace.get(new ActualField(playerName), 
								new FormalField(Object[].class));
						
						displayBoardState((BoardState)(((Object[])input[1])[0]));
						setPlayerHand((PlayerHand)(((Object[])input[1])[1]));
						setTurnValues((TurnValues)(((Object[])input[1])[2]));
						
						userInterface.eventInput("Your hand contains: ");
						userInterface.newPlayerHand(playerHand);
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
		
		int value;
		Object input[];
		boolean lock = true;
		while(lock) {
			userInterface.eventInput("Play an Action Card, or skip the action phase by typing '0'.");
			input = userSpace.get(new ActualField("client"),new ActualField("eventOutput"),new FormalField(String.class));
			
			try {
				value = Integer.parseInt((String)input[2]);
				
				if(value < 0 || value > playerHand.size()) {
					userInterface.eventInput("Input is not a valid card.");
				
				//If player wants to get out of Action phase
				} else if(value == 0) {
					userInterface.eventInput("Action phase has ended.");
					hostSpace.put(playerName, ClientCommands.changePhase);
					lock = false;
				}else {
					hostSpace.put(playerName, ClientCommands.playCard);
					hostSpace.put(playerName, value);
					resolvePlay(clientSpace, hostSpace);
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
	public void buyPhase(Space clientSpace, Space hostSpace) throws InterruptedException {
		
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
								hostSpace.put(playerName, ClientCommands.playCard);
								hostSpace.put(playerName, value);
								resolvePlay(clientSpace, hostSpace);
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
							input = userSpace.get(new ActualField("client"),new ActualField("eventOutput"),new FormalField(String.class));
							value = Integer.parseInt((String)input[2]);
							
							if(value < 0 || value > buyArea.length) {
								userInterface.eventInput("Input is not a valid card.");
							
							//If player wants to get out of Action phase
							} else if(value == 0) {
								userInterface.eventInput("");
								lock2 = false;
							}else {
								hostSpace.put(playerName, ClientCommands.buyCard);
								hostSpace.put(playerName, buyArea[value-1].getName());
								resolvePlay(clientSpace, hostSpace);
							}
						}catch(NumberFormatException e) {
							userInterface.eventInput("Input is not a valid integer.");
						}
					}
					break;
				case "0":
					userInterface.eventInput("Buy phase has ended.");
					hostSpace.put(playerName, ClientCommands.changePhase);
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
		hostSpace.put(playerName, ClientCommands.selectCard);
		hostSpace.put(playerName, selected);
	}
	public void setNames(String[] names) {
		this.names = names;
	}
	public void setPlayerHand(PlayerHand input) throws InterruptedException {
		playerHand = input.getCards();
		userInterface.eventInput("Your new hand contains: ");
		userInterface.newPlayerHand(input);
	}
	public void setBuyArea(Card[] input) {
		this.buyArea = input;
	}
	public void displayLaunge(Launge lobbies) throws InterruptedException {
		userInterface.eventInput("Server sent a list of lobbies: ");
		for(int i = 0 ; i < lobbies.getLobbies().length; i++) {
			userInterface.eventInput("Lobby " + lobbies.getLobbies()[i] + " - " + lobbies.getplayerCount()[i] + "/4 players.");
		}
		userInterface.eventInput("Server sent a list of lobbies: ");
	}
	public void serverMessage(String message) throws InterruptedException {
		userInterface.eventInput("Server says: " + message);
	}
	private void setTurnValues(TurnValues values) {
		userInterface.newTurnValues(values);
	}
}
