package client;

import cards.Card;
import objects.BoardState;
import objects.PlayerHand;
import objects.TurnValues;

public interface UIControllerInter {
	Card[] getBuyArea();
	public default void attemptConnection(String newUri){}
	public default void connectionError() {}
	public default void newConnectionError() {}
	public default void newBuyArea(Card[] buyArea){}
	public default void newPlayers(String[] input){}
	public default void newTurnValues(TurnValues input){}
	public default void newBoardState(BoardState input){}
	public default void newPlayerHand(PlayerHand input){}
	public default void chatInput(){}
	public default void chatOutput(){}
	public default void awaitingUserInput() {}
	public default void eventInput(String input){}
	public default void eventOutput(String input){}
}
