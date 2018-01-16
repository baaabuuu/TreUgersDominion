package engine;

import java.util.ArrayList;
import java.util.List;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;
import org.jspace.Tuple;

import cards.Card;
import log.Log;
import network.Writer;
import objects.BoardState;
import objects.CardOption;
import objects.ClientCommands;
import objects.PlayerHand;
import objects.ServerCommands;
import objects.TurnValues;

/**
 * The game - Handles actual game knowledge - is starting through StartGame
 * @author s164166
 */
public class Game
{
	private Player[] players;
	private Player currPlayer;
	private Board board;
	private int turn;
	private int initialTurn;
	private int phase;
	private int playerCount;
	private EffectHandler effects = new EffectHandler(this);
	private Space space;
	private Writer writer;
	
	/**
	 * In seconds this is how much time each player has for their turns.
	 */
	private int waitTime = 4;
	
		
	/**
	 * Transmits turn values of that player to them
	 * @param playerID
	 * @throws InterruptedException
	 */
	public void sendDisconnect(int playerID) throws InterruptedException
	{
		Player player = players[playerID];
		if (player.isConnected())
		{
			player.setConnected(Boolean.FALSE);
			Log.log("Disconnected player " + player.getName() + "#" + playerID);
			writer.sendMessage(new Tuple(playerID, ServerCommands.newConnection));
		}
	}
	
	/**
	 * Send to a player that something was invalid.
	 * @param message
	 * @param target
	 * @throws InterruptedException
	 */
	public void sendInvalid(String message, int target) throws InterruptedException
	{
		if (players[target].isConnected())
		{
			Log.log("Sending invalid message to " + players[target].getName() + "\n" + message);
			writer.sendMessage(new Tuple(target, ServerCommands.invalid, message));
		}
	}
	
	/**
	 * Updates the turn values - displayed to 1 player
	 * @param playerID
	 * @throws InterruptedException
	 */
	public void sendMessage(String message, int target) throws InterruptedException
	{
		if (players[target].isConnected())
		{
			Log.log("Sending message to " + players[target].getName() + "\n" + message);
			writer.sendMessage(new Tuple(target, ServerCommands.message, message));
		}
	}
	
	/**
	 * Updates the turn values - displayed to all connected players
	 * @param playerID
	 * @throws InterruptedException
	 */
	public void sendMessageAll(String message) throws InterruptedException
	{		
		Log.log("Sending message to all players" + "\n" + message);
		for (int i = 0; i < players.length; i++)
		{
			if (players[i].isConnected())
			{
				writer.sendMessage(new Tuple(i, ServerCommands.message, message));
			}
		}
		
	}
	
	
	/**
	 * Creates a game object, a game object runs the actually game and handles game play logic. 
	 * @param board  
	 * @param playerNames
	 * @param playerCount
	 * @param turn
	 */
	public Game(Board board, String[] playerNames, int playerCount, int turn, Writer writer, Space space)
	{
		this.board = board;
		this.playerCount = playerCount;
		players = setupPlayers(playerNames);
		this.turn = turn;
		currPlayer = players[turn];
		Log.important("Initial player is: " + currPlayer.getName() + "'s turn.");
		this.writer = writer;
		this.space = space;
		initialTurn = turn;
	}
	
	/**
	 * Transmits board state to all players.
	 * @throws InterruptedException 
	 */
	public void sendBoardState() throws InterruptedException
	{
		int boardSize = board.getBoardSize();
		ArrayList<String> boardNames = board.getBoardNamesList();
		int[] shopCount = new int[boardSize];
		for (int i = 0; i < boardSize; i++)
		{
			shopCount[i] = board.getCopiesLeft(boardNames.get(i));
		}
		int[] handCount = new int[players.length];
		int[] deckCount = new int[players.length];
		int[] discardCount = new int[players.length];
		int[] vpCount = new int[players.length];
		for (int i = 0; i < players.length; i++)
		{
			handCount[i] = players[i].getHandSize();
			deckCount[i] = players[i].getDeckSize();
			discardCount[i] = players[i].getDiscardSize();
			vpCount[i] = players[i].getVictoryPoints();
		}
		int trashCount = board.getTrashSize();		
		BoardState boardState = new BoardState(shopCount, handCount, deckCount, discardCount, trashCount, vpCount);
		for (int playerID = 0; playerID < players.length; playerID++)
		{
			if (players[playerID].isConnected())
			{
				Log.log("Transmitting BoardState to " + players[playerID].getName()+ "#" + playerID);
				writer.sendMessage(new Tuple(playerID, ServerCommands.setBoardState, boardState));
			}
		}
	}
	
	/**
	 * Send a card option to the following players.
	 * @param playerID
	 * @param message
	 * @param count
	 * @param list
	 * @throws InterruptedException
	 */
	public void sendCardOption(int playerID, String message, int count, List<Card> list, boolean may) throws InterruptedException
	{
		if (players[playerID].isConnected())
		{
			Log.log("Transmitting cardOption to player " + players[playerID].getName() + "#" + playerID);
			CardOption option = new CardOption(message, count, list, may);
			writer.sendMessage(new Tuple(playerID, ServerCommands.playerSelect, option));
		}
	}
	
	/**
	 * Sends the playerHand to the player
	 * @throws InterruptedException 
	 */
	public void sendPlayerHand(int playerID, int targetID) throws InterruptedException
	{
		if (players[playerID].isConnected())
		{
			Log.log("Transmitting playerHand of " + players[targetID].getName() + "#" + targetID + " to " + players[playerID].getName() + "#" + playerID);
			PlayerHand hand = new PlayerHand(players[targetID].getHand());
			writer.sendMessage(new Tuple(playerID, ServerCommands.setPlayerHand, hand));
		}
	}
	
	/**
	 * Returns the current phase.
	 * @return phase
	 */
	public int getPhase()
	{
		return phase;
	}
	
	/**
	 * Returns the current turn
	 * @return turn
	 */
	public int getTurn()
	{
		return turn;
	}
	
	/**
	 * Returns the currentPlayer
	 * @return
	 */
	public Player getCurrentPlayer()
	{
		return currPlayer;
	}
	
	/**
	 * Returns a player with the following index
	 * @param index
	 * @return
	 */
	public Player getPlayer(int index)
	{
		return players[index];
	}
	
	/**
	 * Returns the array of players
	 * @return
	 */
	public Player[] getPlayers()
	{
		return players;
	}
	
	/**
	 * Start a game - runs the turns
	 * @throws InterruptedException
	 */
	public void start() throws InterruptedException {
		space.get(new ActualField(ServerCommands.gameStart));
		Log.important("Game started");
		startGameActions();
		while(Boolean.TRUE)
		{
			if (takeTurn())
				break;
		}
		Log.important("Game has ended.");
	}
	
	/**
	 * Sends the player game actions
	 * @throws InterruptedException
	 */
	private void startGameActions() throws InterruptedException
	{
		for (int i = 0; i < playerCount; i++)
		{
			if (i != turn)
				sendPlayerHand(i, i);
		}
	}
	
	/**
	 * Transmit to the player they have to take a turn.
	 * @param playerID
	 * @throws InterruptedException
	 */
	private void sendTakeTurn(int playerID) throws InterruptedException
	{
		if (players[playerID].isConnected())
		{
			int boardSize = board.getBoardSize();
			ArrayList<String> boardNames = board.getBoardNamesList();
			int[] shopCount = new int[boardSize];
			for (int i = 0; i < boardSize; i++)
			{
				shopCount[i] = board.getCopiesLeft(boardNames.get(i));
			}
			int[] handCount = new int[players.length];
			int[] deckCount = new int[players.length];
			int[] discardCount = new int[players.length];
			int[] vpCount = new int[players.length];
			for (int i = 0; i < players.length; i++)
			{
				handCount[i] = players[i].getHandSize();
				deckCount[i] = players[i].getDeckSize();
				discardCount[i] = players[i].getDiscardSize();
				vpCount[i] = players[i].getVictoryPoints();
			}
			int trashCount = board.getTrashSize();		
			BoardState boardState = new BoardState(shopCount, handCount, deckCount, discardCount, trashCount, vpCount);
			Player player = players[playerID];
			PlayerHand hand = new PlayerHand(players[playerID].getHand());
			TurnValues values = new TurnValues(player.getActions(), player.getBuys(), player.getMoney());
			Log.log("Transmitting takeTurn to " + player.getName() + "#" + playerID);
			writer.sendMessage(new Tuple(playerID, ServerCommands.takeTurn, boardState, hand, values));
		}
	}
	
	/**
	 * Used to handle turn taking - makes the person take a turn essentially and handles that.
	 * @return
	 * @throws InterruptedException
	 */
	private boolean takeTurn() throws InterruptedException
	{
		sendTakeTurn(turn);
		int counter = 0;
		while(Boolean.TRUE)
		{
			//Check if its a playCard
			Object[] command = space.getp(new FormalField(Integer.class), new ActualField(ClientCommands.playCard),
					new FormalField(Integer.class));
			if (command != null)
			{
				Integer playerNum = (Integer) command[0];
				Integer cardNum = (Integer) command[2];
				Card card = players[playerNum].getHand().get(cardNum);
				Log.important(players[playerNum].getName() + "#" + playerNum + " played " + card.getName());
				if (playerNum == turn)
				{
					boolean result = currPlayer.playCard(card, phase);
					if (result == Boolean.TRUE)
					{
						String[] types = card.getTypes();
						int typeCount = card.getTypeCount();
						for (int i = 0; i < typeCount; i++)
						{
							if (types[i].equals("action"))
							{
								int code = card.getEffectCode()[i];
								effects.triggerEffect(code, currPlayer, card, board, players);
							}
						}
						sendMessageAll(currPlayer.getName() + " played " + card.getName());
						return false;
					}
					else 
					{
						sendInvalid("You cannot play this card right now.", playerNum);
					}
				}
				else
				{
					sendInvalid("You cannot play a card when its not your turn", playerNum);
				}
			}
			else
			{
				command = space.getp(new FormalField(Integer.class), new ActualField(ClientCommands.changePhase));
				if (command != null)
				{
					Log.log("Recieved change phase command");
					Integer playerNum = (Integer) command[0];
					if (playerNum == turn)
					{
						return nextPhase();
					}
					else
					{
						//If a player tries to cheat...
						sendMessage("You cannot force the player to change phase when its not your turn", playerNum);
					}
				}
				else
				{
					command = space.getp(new FormalField(Integer.class), new ActualField(ClientCommands.buyCard), new FormalField(String.class));
					if (command != null)
					{
						Integer playerNum = (Integer) command[0];
						String cardName = (String) command[2];
						Log.log("Recieved buy command from " + players[playerNum] + " for the card " + cardName);

						if (playerNum == turn)
						{
							Card buying = board.canGain(cardName);
							if (buying != null)
							{
								if (currPlayer.buy(buying, phase))
								{
									board.cardRemove(cardName);
									sendMessageAll(currPlayer.getName() + " bought " + cardName + " copies left: " + board.getCopiesLeft(cardName));
								}
								else
								{
									sendInvalid("You can't buy this card.", playerNum);
								}
							}
							else
							{
								sendInvalid("You can't buy this card.", playerNum);
							}
						}
						else
						{
							sendInvalid("You cannot buy whilst its not your turn.", playerNum);
						}
					}
				}
			}
			counter++;
			if (counter > waitTime)
			{
				Log.important(currPlayer.getName() + "#" + turn + " didnt take their action!");
				sendDisconnect(turn);
				return newTurn();
			}
			Thread.sleep(10);
		}
		return false;
	}
	
	/**
	 * A new turn, used at the end of the turn - also checks if game is over.
	 */
	private boolean newTurn()
	{
		if (checkGameEnd())
		{
			Log.important("Game is over");
			return true;
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
			{
				Log.important("Skipping - " + currPlayer.getName() + "'s turn due to disconnected state.");
				for (Player player : players)
				{
					Log.log(player.getName() + " connecton " + player.isConnected());
					if (player.isConnected())
						return newTurn();
				}
				Log.important("No players left in the game - GAME OVER");
				return true;

			}
		}
		return false;
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
			players[i] = new Player(i);
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

	/**
	 * Go to next phase
	 */
	private boolean nextPhase()
	{
		phase++;
		Log.log("Switching to phase " + phase);

		if (phase > 1)
			return newTurn();
		return false;
	}
	

}