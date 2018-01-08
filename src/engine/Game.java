package engine;

import java.util.ArrayList;
import java.util.Scanner;

import cards.Card;
import log.Log;

public class Game {

	private Player[] players;
	private Player currPlayer;
	private Board board;
	private int turn;
	private int phase;
	private int playerCount;

	
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
		Log.log("[CONSOLE INPUT] SwitchPhase : Go to next phase.");
		Log.log("[CONSOLE INPUT] play [index] : play the card located at index.");
		while(true)
		{
			Log.important("[CONSOLE INPUT] Please type your next command.");
			input = scanner.nextLine().toLowerCase();
			Object[] action = action(input);
			if (action[0] == "gethand")
			{
				Log.log("Displaying hand: ");
				int counter = 0;
				ArrayList<Card> hand = (ArrayList<Card>) action[1];
				for (Card card : (ArrayList<Card>) action[1])
				{
					Log.log("index: " + counter + " card name: " + card.getName());
					Log.log("description: " + card.getDesc());
					counter++;
				}
			}	
			if(action[0] == "close")
			{
				break;
			}
			Log.log("current phase: " + phase  + " current player: " + currPlayer.getName() );
		}
		scanner.close();
	}
	
	/**
	 * Tries to perform the following action
	 * @param action
	 */
	public Object[] action(String action)
	{
		String handle = action.substring(0, action.indexOf(" "));
		String additional = action.substring(action.indexOf(" "));
		Object[] output = new Object[2];
		switch (handle)
		{
			case "gethand":
				output[0] = "gethand";
				output[1] = currPlayer.getHand();
				break;
			case "play" :
				Card played = currPlayer.getHand().get(Integer.parseInt(additional));
				Log.important("" + currPlayer.getName() + " is trying to play: " + played.getName());
			default:
				Log.important("Invalid action " + action);
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
			if (currPlayer.isConnected())
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


	/**
	 * Resets a player
	 */
	private void resetPlayer()
	{
		currPlayer.setActions(1);
		currPlayer.resetBuys();
		currPlayer.resetMoney();
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
		Card estate = board.canBuy("Estate");
		Card copper = board.canBuy("Copper");
		
		for (int i = 0; i < playerCount; i++)
		{
			for (int a = 0; a < 3; a++)
				players[i].addCardDecktop(estate.copyOf());
			for (int a = 0; a < 7; a++)
				players[i].addCardDecktop(copper.copyOf());
			players[i] = new Player();
			players[i].setName(playerNames[i]);
			players[i].shuffleDeck();
			players[i].drawCard(5);
		}
		return players;
	}

	public void start() {
		// TODO Auto-generated method stub
		
	}

}
