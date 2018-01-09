package engine;

import java.util.ArrayList;
import java.util.Scanner;

import cards.Card;
import log.Log;

/**
 * The game - Handles actual game knowledge - is starting through StartGame
 * @author Arada
 *
 */
public class Game {

	private Player[] players;
	private Player currPlayer;
	private Board board;
	private int turn;
	private int phase;
	private int playerCount;
	private EffectHandler effects = new EffectHandler();
	private final String[] phases = {"Action Phase", "Buy phase", "Clean-up Phase"};

	
	/**
	 * This method is used to test a dummy game on 1/7/2018
	 */
	@SuppressWarnings("unchecked")
	public void dummyGame()
	{
		Scanner scanner = new Scanner(System.in);
		String input;
		Log.important("Starting a DUMMY game, please use console commands.");
		Log.log("[CONSOLE INPUT] Available commands: ");
		Log.log("[CONSOLE INPUT] GetHand : displays the hand of a player.");
		Log.log("[CONSOLE INPUT] Phase : Go to next phase.");
		Log.log("[CONSOLE INPUT] getSupply : Get the supply cards.");
		Log.log("[CONSOLE INPUT] Play [index] : play the card located at index.");
		Log.log("[CONSOLE INPUT] Money [count] : add count money.");
		Log.log("[CONSOLE INPUT] Action [count] : add count actions.");
		Log.log("[CONSOLE INPUT] Buys [count] : add count buys.");
		Log.log("[CONSOLE INPUT] draw [count] : draw count cards.");
		Log.log("[CONSOLE INPUT] Buy [CardName] : Try to buy the card.");
		while(true)
		{
			Log.important("[CONSOLE INPUT] Please type your next command.");
			input = scanner.nextLine();
			Object[] action = action(input);
			if (action[0] == "gethand")
			{
				Log.important("[CONSOLE OUTPUT] Displaying hand: ");
				int counter = 0;
				ArrayList<Card> hand = (ArrayList<Card>) action[1];
				for (Card card : hand)
				{
					Log.log("Card: " + counter + " " + card.getName() + " - " + card.getDesc());
					counter++;
				}
			}
			if (action[0] == "play")
			{
				if ((boolean) action[1])
				{
					Log.important(currPlayer.getName() + " just succeded in playing a card");
				}
				else
				{
					Log.important(currPlayer.getName() + " failed to play a card");
				}
			}
			if (action[0] == "close")
			{
				Log.important("Ending game");
				break;
			}
			if (action[0] == "getsupply")
			{
				Object[] takeout = (Object[]) action[1];
				ArrayList<Card> cards = (ArrayList<Card>) takeout[0];
				ArrayList<Integer> copies = (ArrayList<Integer>) takeout[1];
				for (int i = 0; i < cards.size(); i++)
				{
					Log.log(i + " " + cards.get(i).getName() + " cost " + cards.get(i).getCost() + " x" + copies.get(i));
				}
				
				
			}
			if (action[0] == "phase")
			{
				nextPhase();
			}
			Log.log("current phase: " + phases[phase]  + " current player: " + currPlayer.getName() + " money: " + currPlayer.getMoney() + " buys: " + currPlayer.getBuys() +
					" actions: " + currPlayer.getActions());
		}
		scanner.close();
	}
	
	/**
	 * If no actions - no action phase
	 * <br> Cannot skip buy phase - Treasure can potentially have buys on them.
	 */
	private void checkNextPhase()
	{
		if (phase == 0 && !currPlayer.canPlayAction())
				nextPhase();
	}
	
	/**
	 * Tries to perform the following action
	 * @param action
	 */
	public Object[] action(String action)
	{
		String handle = action;
		String additional = "0";
		if (action.contains(" "))
		{
			handle = action.substring(0, action.indexOf(" "));
			additional = action.substring(action.indexOf(" ") + 1);
		}
		handle = handle.toLowerCase();
		Object[] output = new Object[2];
		switch (handle)
		{
			case "gethand":
				output[0] = "gethand";
				output[1] = currPlayer.getHand();
				break;
			case "play" :
				Card played = currPlayer.getHand().get(Integer.parseInt(additional));
				Boolean out = currPlayer.playCard(played, phase);
				if (out)
				{
					String[] types = played.getTypes();
					int typeCount = played.getTypeCount();
					for (int i = 0; i < typeCount; i++)
					{
						if (types[i].equals("action"))
						{
							int code = played.getEffectCode()[i];
							Log.important("Effect being played: " + code);
							effects.triggerEffect(code, currPlayer, played, board, players);
						}
					}
					currPlayer.discardCard(played);
					currPlayer.removeFromHand(Integer.parseInt(additional));
				}
				output[0] = "play";
				output[1] = out;
				break;
			case "draw" :
				currPlayer.drawCard(Integer.parseInt(additional));
				output[0] = null;
				output[1] = null;
				break;
			case "buy" :
				output[0] = "buy";
				output[1] = null;
				Card buying = board.canGain(additional);
				if (buying != null)
				{
					if (currPlayer.buy(buying, phase))
					{
						board.cardRemove(additional);
					}
				}
				break;
			case "money" :
				currPlayer.addMoney(Integer.parseInt(additional));
				output[0] = null;
				output[1] = null;
				break;
			case "action" :
				currPlayer.addActions(Integer.parseInt(additional));
				output[0] = null;
				output[1] = null;
				break;
			case "buys" :
				currPlayer.addActions(Integer.parseInt(additional));
				output[0] = null;
				output[1] = null;
				break;
			case "close" :
				output[0] = "close";
				output[1] = null;
				break;
			case "getsupply" :
				output[0] = "getsupply";
				ArrayList<Card> supply = new ArrayList<Card>();
				ArrayList<Integer> supplyCopies = new ArrayList<Integer>();
				for (String cardName : board.getBoardNamesList())
				{
					supply.add(board.canGain(cardName));
				}
				for (String cardName : board.getBoardNamesList())
				{
					supplyCopies.add(board.getCopiesLeft(cardName));
				}
				Object[] secondary = new Object[2];
				secondary[0] = supply;
				secondary[1] = supplyCopies;
				output[1] = secondary;
				break;
			case "phase" :
				output[0] = "phase";
				output[1] = null;
				break;
			default:
				output[0] = null;
				output[1] = null;
				Log.important("Invalid action " + action);
				break;
		}
		return output;
	}
	
	
	/**
	 * Creates a game object, a game object runs the actually game and handles game play logic. 
	 * @param board
	 * @param playerNames
	 * @param playerCount
	 * @param turn
	 */
	public Game(Board board, String[] playerNames, int playerCount, int turn)
	{
		this.board = board;
		this.playerCount = playerCount;
		players = setupPlayers(playerNames);
		this.turn = turn;
		currPlayer = players[turn];
		Log.important("Initial player is: " + currPlayer.getName() + "'s turn.");
		
	}
	
	/**
	 * A new turn, used at the end of the turn - also checks if game is over.
	 */
	public void newTurn()
	{
		if (checkGameEnd())
		{
			Log.important("Game is over");
		}
		else
		{
			phase = 0;
			turn++;
			if (turn >= playerCount)
			{
				turn = 0;
			}
			resetPlayer();
			currPlayer = players[turn];
			Log.important("Turn switch - switching to " + currPlayer.getName() + "'s turn.");
			if (!currPlayer.isConnected())
				newTurn();
		}
		
	}
	
	/**
	 * Checks if the game has ended
	 * @return true if conditions are met in Board.checkend
	 */
	private boolean checkGameEnd()
	{
		return board.checkEnd();
	}
	
	public void nextPhase()
	{
		phase++;
		if (phase > 1)
			newTurn();
	}


	/**
	 * Resets a player
	 */
	private void resetPlayer()
	{
		currPlayer.setActions(1);
		currPlayer.resetBuys();
		currPlayer.resetMoney();
		Card card;
		Log.important("Discarding " + currPlayer.getName() + " hand");
		while (currPlayer.getHandSize() > 0)
		{
			card = currPlayer.getHand().get(0);
			currPlayer.removeFromHand(card);
			currPlayer.discardCard(card);
		}
		currPlayer.drawCard(5);
	}
	
	/**
	 * Generates the array of players initializing the player objects
	 * @param playerNames
	 * @param playerCount
	 * @return
	 */
	private Player[] setupPlayers(String[] playerNames)
	{
		Player[] players = new Player[playerCount];
		Card estate = board.canGain("Estate");
		Card copper = board.canGain("Copper");
		
		for (int i = 0; i < playerCount; i++)
		{	
			players[i] = new Player();
			for (int a = 0; a < 3; a++)
			{
				players[i].addCardDecktop(estate.copyOf());
			}
				
			for (int a = 0; a < 7; a++)
			{
				players[i].addCardDecktop(copper.copyOf());
			}
			players[i].setName(playerNames[i]);
			players[i].shuffleDeck();
			players[i].drawCard(5);
			players[i].setActions(1);
			players[i].resetMoney();
			players[i].resetBuys();
		}
		return players;
	}

	public void start() {
		// TODO Auto-generated method stub
		
	}

}
