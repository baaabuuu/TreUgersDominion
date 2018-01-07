package engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingDeque;

import cards.Card;

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
	public void dummyGame()
	{
		Scanner scanner = new Scanner(System.in);
		String input;
		Log.important("[Game] Starting a DUMMY game, please use console commands.");
		Log.log("[Game][CONSOLE INPUT] Available commands: ");
		Log.log("[Game][CONSOLE INPUT] GetHand : displays the hand of a player.");
		Log.log("[Game][CONSOLE INPUT] SwitchPhase : Go to next phase.");
		Log.log("[Game][CONSOLE INPUT] play [index] : play the card located at index.");
		while(true)
		{
			Log.important("[Game][CONSOLE INPUT] Please type your next command.");
			input = scanner.nextLine().toLowerCase();
			Object[] action = action(input);
			if (action[0] == "gethand")
			{
				Log.log("[Game] Displaying hand: ");
				int counter = 0;
				ArrayList<Card> hand = (ArrayList<Card>) action[1];
				for (Card card : (ArrayList<Card>) action[1])
				{
					Log.log("[Game] index: " + counter + " card name: " + card.getName());
					Log.log("[Game] description: " + card.getDesc());
					counter++;
				}
			}		
			Log.log("[Game] current phase: " + phase  + " current player: " + currPlayer.getName() );
		}
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
				Log.important("[Game] " + currPlayer.getName() + " is trying to play: " + played.getName());
			default:
				Log.important("[Game] Invalid action " + action);
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
		Log.important("[Game] Initial player is: " + currPlayer.getName() + "'s turn.");
		
	}
	
	/**
	 * A new turn, used at the end of the turn - also checks if game is over.
	 */
	public void newTurn()
	{
		if (checkGameEnd())
		{
			Log.important("[Game] Game is over");
		}
		else
		{
			phase = 0;
			turn++;
			if (turn >= playerCount)
			{
				turn = 0;
			}
			currPlayer = players[turn];
			Log.important("[Game] Turn switch - switching to " + currPlayer.getName() + "'s turn.");
			resetPlayer();
		}
		
	}
	
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
			Card[] baseDeck = new Card[] {estate.copyOf(), estate.copyOf(), estate.copyOf(), copper.copyOf(), copper.copyOf(), copper.copyOf(),
					copper.copyOf(), copper.copyOf(), copper.copyOf(), copper.copyOf()};

			players[i] = new Player();
			players[i].setName(playerNames[i]);
			players[i].setDeck(new LinkedBlockingDeque<Card> (Arrays.asList(Arrays.copyOf(baseDeck, 10))));
			players[i].shuffleDeck();
			players[i].drawCard(5);
		}
		
		return players;
	}

	public void start() {
		// TODO Auto-generated method stub
		
	}

}
