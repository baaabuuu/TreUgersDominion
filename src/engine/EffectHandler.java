package engine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.jspace.*;
import cards.Card;
import log.Log;
import objects.ClientCommands;
public class EffectHandler
{	

	private Game game;
	private Space rSpace;
	public EffectHandler(Game game) {
		this.game=game;
		this.rSpace=game.getSpace();
	}


	/**
	 * Coordinator of effects- Call this on a per-board basis
	 * @param n - Specifies the effect code
	 * @param player - PLayer that the effect should a apply toString[] players
	 * @param card - The card that was played.
	 * @param board - current board state
	 * @param players - List of playerObjects
	 * @throws InterruptedException 
	 */

	public void triggerEffect(int n, Player player, Card card, Board board, Player[] players) throws InterruptedException{
		//First check if playing this card would trigger any other effect
		for(Player p: players) {
			if(p.getEffects().size()== 0) {
				continue;
			}
			else {
				for(String s: p.getEffects()) {
					playerEffects(s,p,card);
				}
			}
		}
		//If it is an attack, can someone use a reaction card?
		Player[] affectedPlayers=null;
		for(String type: card.getTypes()) {
			if(type.equals("attack")) {
				affectedPlayers=(Player[]) findCounterPlays(player, card, board, players).toArray();
			}
			else {
				affectedPlayers = players;
			}

		}

		Log.important("Effect code: "+ n +" was called by "+ card.getName() +" played by "+ player.getName()+".");
		switch(n)
		{
		//Do nothing
		case 0: 
			break;
			//Discard any number of cards, then draw that many.
		case 1: 
			discardNDrawN(player); 
			break;
			//Draw 2 cards
		case 2: 
			player.drawCard(2);
			break;
			//Reveal and become immune to an attack card
		case 3://Implement reveal
			//does not trigger on play
			break;
			//Look through discard and maybe put one on top of the deck
		case 4: 
			browseDiscard1OnTop(player);
			break;
			//Next time a silver is played, gain +1 temp monies
		case 5: 
			player.addEffect("NextSilverMoney1");
			break;
			//Discard top of deck, if action card, play it.
		case 6: 
			playVassal(player);
			break;
			//Draw 1, get 2 actions
		case 7: 
			draw1Get2Actions(player);
			break;
			//Gain a card costing upto 4
		case 8: 
			gain1MaxCost4(player, board);
			break;
			//Silver on deck, reveal VP cards
		case 9:
			silverOnDeckRevealVC(player, board, affectedPlayers);
			break;
			//get 1 VP for every 10 cards
		case 10: 
			player.addEffect("1VPPer10Cards"); //this might need to go somehwere else
			break;
			//Get 2 temp money, others discard untill they have 3 cards left
		case 11: 
			get2TempOthersDiscard(player,affectedPlayers);
			break;
			//May trash copper, if so, add +3 tempmoney
		case 12: 
			mayTrashCopperGain2(player,board);
			break;
		case 13: 
			discardPerEmptySupply(player,board);
			break;
		case 14:
			trashFromHandGainPlus2(player, board);
			break;
		case 15:
			playThroneRoom(player, board, affectedPlayers);
			break;
		case 16:
			gainGoldOthersReveal(player,board, affectedPlayers);
			break;
		case 17:
			draw4Buy1OthersDraw(player,affectedPlayers);
			break;
		case 18:
			action2Buy1Money2(player);
			break;
		case 19:
			draw2Action1(player);
			break;
		case 20:
			drawTill7(player);
			break;
		case 21:
			draw1Action1Buy1Tempmoney(player);
			break;
		case 22://Requires trash
			mayTrashTreasure(player,board);
			break;
		case 23:
			draw1Action1Look2(player);
			break;
		case 24:
			draw2OthersCurse(player,affectedPlayers, board);
			break;
		case 25:
			gainPlus5(player,board);
			break;
		case 26: trashUpTo4(player,board);
		break;
		default:
			Log.important("Invalid effect code: "+n);
			break;//Invalid effect error here;

		}
		//After every effect, update player hand
		game.sendPlayerHand(player.getID(), player.getID());
	}

	/**
	 * A player has the option to trash up to 4 cards from their hand
	 * @param player
	 * @param board
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	private void trashUpTo4(Player player, Board board) throws InterruptedException
	{
		game.sendCardOption(player.getID(), "Select up to 4 cards you would like to trash", 4, player.getHand(), true);
		//---[BEGIN TIMEOUT BLOCK]---
		int counter = 0; // timeout
		Object[] tempResponse = null;
		while(true) {
			tempResponse=rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
			if(tempResponse != null)
			{
				//---[BEGIN CODE BLOCK]---
				ArrayList<Integer> response = (ArrayList<Integer>) tempResponse[2];
				if(response.get(0) == -1)
				{
					game.sendMessage("No cards trashed", player.getID());
				}
				else {
					ArrayList<Card> tempHand = player.getHand();
					ArrayList<Card> toTrash = new ArrayList<Card>();
					for(int index:response)
					{
						toTrash.add(tempHand.get(index));
					}
					for(Card c: toTrash)
					{
						player.removeFromHand(c);
						board.trashCard(c);
						player.trash(c);
					}
				}
				//---[END CODE BLOCK]---
				break;
			}
			counter++;
			if (counter > game.getWaitTime())
			{

				Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
				game.sendDisconnect(player.getID());
				break;
			}
			Thread.sleep(10);
		}
		//---[END TIMEOUT BLOCK]---

	}
	/**
	 * Effect code - gain a gold and force others to reveal
	 * @param player
	 * @param board
	 * @param players
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	private void gainGoldOthersReveal(Player player, Board board, Player[] players) throws InterruptedException
	{
		Card gold = board.canGain("Gold");
		ArrayList<Player> expectedResponses = new ArrayList<Player>();
		ArrayList<ArrayList<Card>> selectedCards = new ArrayList<ArrayList<Card>>();
		if(gold != null)
		{
			player.discardCard(gold);
			board.cardRemove("Gold");
			player.gain(gold);
		}
		for(Player p : players)
		{
			if(p.equals(player))
			{
				continue;
			}
			String[] drawn = p.drawCard(2);
			//Measure to avoid index out of bounds in case of empty discard and deck
			StringBuilder drawnToString = new StringBuilder();
			for(String s: drawn)
			{
				drawnToString.append(s);
				drawnToString.append(", ");
			}
			drawnToString.substring(0, drawnToString.length() - 2);

			game.sendMessageAll(p.getName() + " has revealed " + drawnToString.toString());
			//Remove cards from hand
			ArrayList<Card> tempCards = new ArrayList<Card>();
			for(int i = 1; i <= drawn.length; i++)
			{
				tempCards.add(p.getHand().get(p.getHandSize() - i));
				p.removeFromHand(p.getHand().get(p.getHandSize() - i));
			}

			//See if any cards in hand are treasures - but not coppers
			boolean hasTreasure = tempCards.stream().anyMatch(c -> Arrays.stream(c.getDisplayTypes()).anyMatch(type -> type.equals("Victory") ) && !c.getName().equals("Copper"));
			if(hasTreasure)
			{
				//If requirements met, trash one, discard the other (if there is another)
				game.sendCardOption(p.getID(), "Choose a card to trash, other will be discarded", 1, tempCards, false);
				expectedResponses.add(p);
				selectedCards.add(tempCards);
			}
			else
			{
				for(int i = tempCards.size() - 1;i >= 0 ; i--)
				{
					p.addCardDecktop(tempCards.get(i));
				}
			}
		}
		//---[BEGIN TIMEOUT BLOCK]---
		int counter = 0; // timeout
		Object[] tempResponse = null;
		while(expectedResponses.size() > 0)
		{	
			while(true)
			{
				tempResponse = rSpace.getp(new FormalField(Integer.class), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
				if(tempResponse != null)
				{
					int pID = (int) tempResponse[0];
					ArrayList<Integer> response = (ArrayList<Integer>) tempResponse[2];
					//Find out what player responded
					for(Player rPlayer : expectedResponses)
					{
						if(rPlayer.getID() == pID)
						{
							int cardIndex = expectedResponses.indexOf(rPlayer);
							//---[BEGIN CODE BLOCK]---
							Card selection = selectedCards.get(cardIndex).get(response.get(0));
							board.trashCard(selection);
							selectedCards.get(cardIndex).remove(selection);
							rPlayer.trash(selection);
							Card nullCheck = selectedCards.get(cardIndex).get(0);
							if (nullCheck != null)
							{
								rPlayer.discardCard(nullCheck);
							}
							//---[END CODE BLOCK]---
							expectedResponses.remove(rPlayer);
							selectedCards.remove(cardIndex);
							counter = 0; // reset timeout after player response
							break;
						}
					}
					break;
				}
				counter++;
				if (counter > game.getWaitTime())
				{
					for(Player dPlayer : expectedResponses)
					{
						Log.important(dPlayer.getName() + "#" + dPlayer.getID() + " has been timed out!");
						game.sendDisconnect(dPlayer.getID());

					}
					break;
				}
				Thread.sleep(10);
			}
		}
		//---[END TIMEOUT BLOCK]---
	}

	/**
	 * Gain a card costing up to 5.
	 * @param player
	 * @param board
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	private void gainPlus5(Player player, Board board) throws InterruptedException
	{
		ArrayList<Card> choice = getChoice(bcard -> bcard.getCost() <= 5, board);
		ArrayList<Card> tempHand = player.getHand();
		Card c;
		if(choice.size() > 0)
		{
			game.sendCardOption(player.getID(), "Select a card to gain", 1, choice, false);
			//---[BEGIN TIMEOUT BLOCK]---	
			int counter = 0; // timeout
			Object[] tempResponse = null;
			while(true) 
			{
				tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
				if(tempResponse != null) {
					//---[BEGIN CODE BLOCK]---
					ArrayList<Integer> response = (ArrayList<Integer>) tempResponse[2];
					c =choice.get(response.get(0));
					board.cardRemove(c.getName());
					tempHand.add(c);
					player.setHand(tempHand);
					player.gain(c);

					game.sendCardOption(player.getID(), "Select a card to put on top of deck", 1, tempHand, false);
					//---[BEGIN TIMEOUT BLOCK]---
					int counter2 = 0; // timeout
					tempResponse = null;
					while(true)
					{
						tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
						if(tempResponse != null) 
						{
							//---[BEGIN CODE BLOCK]---
							response =(ArrayList<Integer>) tempResponse[2];
							c = tempHand.get(response.get(0));
							player.removeFromHand(c);
							player.addCardDecktop(c);
							//---[END CODE BLOCK]---
							break;
						}
						counter2++;
						if (counter2 > game.getWaitTime())
						{
							Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
							game.sendDisconnect(player.getID());
							break;
						}
						Thread.sleep(10);
					}
					//---[END TIMEOUT BLOCK]---		
					break;
				}
				counter++;
				if (counter > game.getWaitTime())
				{
					Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
					game.sendDisconnect(player.getID());
					break;
				}
				Thread.sleep(10);
			}
			//---[END TIMEOUT BLOCK]---	
		}	

	}
	/**
	 * Allows a player to trash a card costing up to 3 or more.
	 * @param player
	 * @param board
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	private void mayTrashTreasure(Player player, Board board) throws InterruptedException
	{
		ArrayList<Card> choice = (ArrayList<Card>) player.getHand().stream().filter(c -> Arrays.stream(c.getDisplayTypes()).anyMatch(type -> type.equals("Treasure"))).collect(Collectors.toList());
		if(choice.size() > 0)
		{
			game.sendCardOption(player.getID(), "Trash treasure card from your hand to gain a treasure costing upto 3 more.", 1, choice, true);
			//---[BEGIN TIMEOUT BLOCK]--
			int counter = 0; // timeout
			Object[] tempResponse = null;
			while(true)
			{
				tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
				if(tempResponse != null)
				{
					//---[BEGIN CODE BLOCK]---
					ArrayList<Integer> response = (ArrayList<Integer>) tempResponse[2];
					if(response.get(0) != -1) {
						Card c = choice.get(response.get(0));
						choice = getChoice(bcard -> bcard.getCost() <= c.getCost() + 3 && Arrays.stream(bcard.getDisplayTypes()).anyMatch(type -> type.equals("Treasure")), board);
						if(choice.size() > 0) 
						{
							game.sendCardOption(player.getID(), "Choose a card to gain", 1, choice, false);
							//---[BEGIN TIMEOUT BLOCK]--
							int counter2 = 0; // timeout
							tempResponse = null;
							while(true)
							{
								tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
								if(tempResponse != null)
								{
									//---[BEGIN CODE BLOCK]---
									response = (ArrayList<Integer>) tempResponse[2];
									Card c2 = choice.get(response.get(0));
									player.discardCard(c2);
									board.cardRemove(c2.getName());
									player.gain(c2);
									board.trashCard(c);
									player.trash(c);
									player.removeFromHand(c);
									//---[END CODE BLOCK]---
									break;
								}
								counter2++;
								if (counter2 > game.getWaitTime())
								{
									Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
									game.sendDisconnect(player.getID());
									break;
								}
								Thread.sleep(10);
							}
							//---[END TIMEOUT BLOCK]--
						}
						else
						{
							game.sendMessage("No cards to gain, your treasure has not been trashed", player.getID());
						}
					}
					else
					{
						game.sendMessage("No treasure in hand", player.getID());
					}
					//---[END CODE BLOCK]---
					break;
				}
				counter++;
				if (counter > game.getWaitTime())
				{
					Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
					game.sendDisconnect(player.getID());
					break;
				}
				Thread.sleep(10);
			}
			//---[END TIMEOUT BLOCK]--
		}
	}

	@SuppressWarnings("unchecked")
	private void drawTill7(Player player) throws InterruptedException
	{
		ArrayList<Card> toBeDiscarded = new ArrayList<Card>();
		while(player.getHandSize() < 7 && player.getDeckSize() > 0  && player.getDiscardSize() > 0) {
			player.drawCard(1);
			game.sendPlayerHand(player.getID(), player.getID());
			Card currentDraw = player.getHand().get(player.getHandSize() - 1);
			for(String dispType: currentDraw.getDisplayTypes())
			{
				if(dispType.equals("Action"))
				{
					ArrayList<Card> choice = new ArrayList<Card>();
					ArrayList<Integer> response = new ArrayList<Integer>();
					choice.add(currentDraw);
					game.sendCardOption(player.getID(), "Do you wish to keep "+currentDraw.getName()+" ?", 1, choice, true);
					//---[BEGIN TIMEOUT BLOCK]---
					int counter = 0; // timeout
					Object[] tempResponse = null;
					while(true)
					{
						tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
						if(tempResponse != null)
						{
							//---[BEGIN CODE BLOCK]---
							response = (ArrayList<Integer>) tempResponse[2];
							//---[END CODE BLOCK]---
							break;
						}
						counter++;
						if (counter > game.getWaitTime())
						{
							Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
							game.sendDisconnect(player.getID());
							break;
						}
						Thread.sleep(10);
					}
					//---[END TIMEOUT BLOCK]---
					if(response.get(0)!= -1)
					{
						break;
					}
					else 
					{
						player.removeFromHand(currentDraw);
						toBeDiscarded.add(currentDraw);
					}
					break;
				}

			}

		}
		for (Card cardToDiscard: toBeDiscarded) 
		{
			player.discardCard(cardToDiscard);
		}

	}
	/**
	 * Trash a card from your hand then choose a card to gain
	 * @param player
	 * @param board
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	private void trashFromHandGainPlus2(Player player, Board board) throws InterruptedException
	{
		ArrayList<Card> tempHand = player.getHand();
		if(tempHand.size()> 0)
		{
			game.sendCardOption(player.getID(), "Choose a card to trash from your hand.",1, tempHand, false);
			//---[BEGIN TIMEOUT BLOCK]---
			int counter = 0; // timeout
			Object[] tempResponse = null;
			while(true)
			{
				tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
				if(tempResponse != null)
				{
					//---[BEGIN CODE BLOCK]---
					ArrayList<Integer> response=(ArrayList<Integer>) tempResponse[2];
					Card trashCard = tempHand.get(response.get(0));
					board.trashCard(trashCard);
					player.trash(trashCard);
					ArrayList<Card> choice = getChoice(bcard -> bcard.getCost() <= trashCard.getCost()+2,board);
					game.sendCardOption(player.getID(), "Choose 1 card to gain.", 1,choice, false);
					//---[BEGIN TIMEOUT BLOCK]---
					int counter2 = 0; // timeout
					tempResponse = null;
					while(true)
					{
						tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
						if(tempResponse != null)
						{
							//---[BEGIN CODE BLOCK]---
							response = (ArrayList<Integer>) tempResponse[2];
							//Since we only ask for one choice, we always know where it is.
							Card c = choice.get(response.get(0));
							player.discardCard(c);
							board.cardRemove(c.getName());
							player.gain(c);
							//---[END CODE BLOCK]---
							break;
						}
						counter2++;
						if (counter2 > game.getWaitTime())
						{
							Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
							game.sendDisconnect(player.getID());
							break;
						}
						Thread.sleep(10);
					}
					//---[END TIMEOUT BLOCK]---

					//---[END CODE BLOCK]---
					break;
				}
				counter++;
				if (counter > game.getWaitTime())
				{
					Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
					game.sendDisconnect(player.getID());
					break;
				}
				Thread.sleep(10);
			}
			//---[END TIMEOUT BLOCK]---
		}
	}
	
	/**
	 * Discard a card per empty supply pile.
	 * @param player
	 * @param board
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	private void discardPerEmptySupply(Player player, Board board) throws InterruptedException
	{
		int count = 0;
		for (String cardName : board.getBoardNamesArray())
		{
			if (board.canGain(cardName) == null)
			{
				count++;
			}
		}
		game.sendCardOption(player.getID(), "Choose " + count + " cards to discard", count, player.getHand(), false);

		//---[BEGIN TIMEOUT BLOCK]---
		int counter = 0; // timeout
		Object[] tempResponse = null;
		while(true)
		{
			tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
			if(tempResponse != null)
			{
				//---[BEGIN CODE BLOCK]---
				ArrayList<Integer> response = (ArrayList<Integer>) tempResponse[2]; // Response here
				ArrayList<Card> toDiscard = new ArrayList<Card>();
				ArrayList<Card> tempHand = player.getHand();
				for(int i: response)
				{
					toDiscard.add(tempHand.get(i));
				}
				for(Card c: toDiscard)
				{
					tempHand.remove(c);
				}
				player.setHand(tempHand);
				//---[END CODE BLOCK]---
				break;
			}
			counter++;
			if (counter > game.getWaitTime())
			{
				Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
				game.sendDisconnect(player.getID());
				break;
			}
			Thread.sleep(10);
		}
		//---[END TIMEOUT BLOCK]---
	}
	
	/**
	 * You may trash a copper to gain a card costing up to 2 more.
	 * @param player
	 * @param board
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	private void mayTrashCopperGain2(Player player,Board board) throws InterruptedException
	{
		for(Card copper: player.getHand())
		{
			if(copper.getName().equals("Copper"))
			{
				ArrayList<Card> copperList = new ArrayList<Card>();
				copperList.add(copper);
				game.sendCardOption(player.getID(), "Trash copper to gain card costing upto 2 more.", 1, copperList, true);

				//---[BEGIN TIMEOUT BLOCK]---
				ArrayList<Integer> response = new ArrayList<Integer>();
				int counter = 0; // timeout
				Object[] tempResponse = null;
				while(true)
				{
					tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
					if(tempResponse != null)
					{
						//---[BEGIN CODE BLOCK]---
						response = (ArrayList<Integer>) tempResponse[2];				
						//---[END CODE BLOCK]---
						break;
					}
					counter++;
					if (counter > game.getWaitTime())
					{
						response.set(0, -1);//Break out of functionality once we time out
						Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
						game.sendDisconnect(player.getID());
						break;
					}
					Thread.sleep(10);
				}
				//---[END TIMEOUT BLOCK]---
				if(response.get(0) == -1)
				{
					//break if we choose not to or timeout
					break;
				}
				else
				{
					ArrayList<Card> choice = getChoice(bcard-> bcard.getCost() <= copper.getCost() + 2,board);
					if(choice.size() == 0)
					{
						game.sendMessage("No cards meeting requirements available", player.getID());
						break;
					}
					else 
					{
						game.sendCardOption(player.getID(), "Select the card you would like to gain", 1, choice, false);
						//---[BEGIN TIMEOUT BLOCK]---
						int counter2 = 0; // timeout
						tempResponse = null;
						while(true)
						{
							tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
							if(tempResponse != null)
							{
								//---[BEGIN CODE BLOCK]---
								response=(ArrayList<Integer>) tempResponse[2];
								//We already checked that atleast one copy is available in the deck
								Card cardToGain = board.canGain(choice.get(response.get(0)).getName());
								player.discardCard(cardToGain);
								board.cardRemove(cardToGain.getName());
								player.gain(cardToGain);
								ArrayList<Card> tempHand = player.getHand();
								tempHand.remove(copper);
								board.trashCard(copper);
								player.trash(copper);
								player.setHand(tempHand);
								game.sendPlayerHand(player.getID(), player.getID());
								//---[END CODE BLOCK]---
								break;
							}
							counter2++;
							if (counter2 > game.getWaitTime())
							{
								Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
								game.sendDisconnect(player.getID());
								break;
							}
							Thread.sleep(10);
						}
						//---[END TIMEOUT BLOCK]---
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Draw 2 cards, other players draw a curse card.
	 * @param player
	 * @param players
	 * @param board
	 * @throws InterruptedException
	 */
	private void draw2OthersCurse(Player player, Player[] players,Board board) throws InterruptedException
	{
		player.drawCard(2);
		for(Player other: players)
		{
			if(other.equals(player))
			{
				continue;
			}
			game.sendMessage("Gained a Curse from "+player.getName()+"'s card", other.getID());
			Card curse = board.canGain("Curse");
			if(curse != null)
			{
				other.discardCard(curse);
				board.cardRemove("Curse");
				other.gain(curse);
			}
		}
	}
	/**
	 * Draw a card, gain 1 action Look at the top two cards of your deck.
	 * @param player
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	private void draw1Action1Look2(Player player) throws InterruptedException //This method is bound to have errors, remember this for testing
	{ 
		LinkedBlockingDeque<Card> tempDeck = player.getDeck();
		ArrayList<Card> choice = new ArrayList<Card>();
		player.drawCard(1);
		player.addActions(1);
		Card card1 = tempDeck.pollFirst();
		Card card2 = tempDeck.pollFirst();
		choice.add(card1);
		choice.add(card2);

		game.sendCardOption(player.getID(), "Select the cards you would like to discard if any. (In order)", 2, choice, true);
		//---[BEGIN TIMEOUT BLOCK]---
		int counter = 0; // timeout
		Object[] tempResponse = null;
		while(true)
		{
			tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
			if(tempResponse != null)
			{
				//---[BEGIN CODE BLOCK]---
				ArrayList<Integer> response = (ArrayList<Integer>) tempResponse[2]; // response here
				List<Card> choice2 = choice;
				if(response.get(0) != -1)
				{
					for(int i: response )
					{
						player.discardCard(choice.get(i));
						choice2.remove(choice.get(i));
					}
				}
				if (response.size() != 2)
				{
					game.sendCardOption(player.getID(), "Select the cards you would like to trash if any. (In order)", response.size(), choice2, true);
					//---[BEGIN TIMEOUT BLOCK]---
					int counter2 = 0; // timeout
					tempResponse = null;
					while(true)
					{
						tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
						if(tempResponse != null)
						{
							//---[BEGIN CODE BLOCK]---
							response = (ArrayList<Integer>) tempResponse[2]; //response here
							List<Card> choice3 = choice2;
							if(response.get(0)!=-1)
							{
								for(int i: response)
								{
									player.discardCard(choice2.get(i));
									choice3.remove(choice2.get(i));
								}
							}
							if(response.size() != 2)
							{
								game.sendCardOption(player.getID(), "Select the order you would like to put the remaining cards back in", response.size(), choice3, false);
								//---[BEGIN TIMEOUT BLOCK]---
								int counter3 = 0; // timeout
								tempResponse = null;
								while(true)
								{
									tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
									if(tempResponse != null)
									{
										//---[BEGIN CODE BLOCK]---
										response = (ArrayList<Integer>) tempResponse[2]; //response here		
										for(int i: response)
										{
											tempDeck.addFirst(choice3.get(i));
										}
										//---[END CODE BLOCK]---
										break;
									}
									counter3++;
									if (counter3 > game.getWaitTime())
									{
										Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
										game.sendDisconnect(player.getID());
										break;
									}
									Thread.sleep(10);
								}
								//---[END TIMEOUT BLOCK]---
							}
							//---[END CODE BLOCK]---
							break;
						}
						counter2++;
						if (counter2 > game.getWaitTime())
						{
							Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
							game.sendDisconnect(player.getID());
							break;
						}
						Thread.sleep(10);
					}
					//---[END TIMEOUT BLOCK]---
				}
				//---[END CODE BLOCK]---
				break;
			}
			counter++;
			if (counter > game.getWaitTime())
			{
				Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
				game.sendDisconnect(player.getID());
				break;
			}
			Thread.sleep(10);
		}
		//---[END TIMEOUT BLOCK]---

		player.setDeck(tempDeck);
	}
	/**
	 * Draw a card, gain an action, get 1 buy, get 1 money.
	 * @param player
	 */
	private void draw1Action1Buy1Tempmoney(Player player)
	{
		player.drawCard(1);
		player.addActions(1);
		player.addBuys(1);
		player.addMoney(1);
	}
	
	/**
	 * Draw 2 cards, gain 1 action.
	 * @param player
	 */
	private void draw2Action1(Player player) 
	{
		player.drawCard(2);
		player.addActions(1);
	}
	
	/**
	 * Gain 2 actions, 1 buy, 2 money.
	 * @param player
	 */
	private void action2Buy1Money2(Player player)
	{
		player.addActions(2);
		player.addBuys(1);
		player.addMoney(2);

	}
	
	/**
	 * Draw 4 cards, add 1 buy, other player draw a card
	 * @param player
	 * @param players
	 * @throws InterruptedException
	 */
	private void draw4Buy1OthersDraw(Player player, Player[] players) throws InterruptedException
	{
		player.drawCard(4);
		player.addBuys(1);		
		for(Player other: players)
		{
			if(other.equals(player))
			{
				continue;
			}
			game.sendMessage("Drawing one card from the effect of " + player.getName() + "'s card", other.getID());
			String cardName = other.drawCard(1)[0];
			game.sendMessage("You drew " + cardName, other.getID());
			game.sendPlayerHand(other.getID(), other.getID());
		}
	}
	/**
	 * You may select an action card from your hand, if so play it twice.
	 * @param player
	 * @param board
	 * @param players
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	private void playThroneRoom(Player player, Board board, Player[] players) throws InterruptedException
	{
		ArrayList<Card> actionInHand = (ArrayList<Card>) player.getHand().stream().filter(card -> Arrays.stream(card.getDisplayTypes()).filter(s -> s.equals("Action")).findAny().isPresent()).collect(Collectors.toList());
		if(actionInHand.size() != 0)
		{
			game.sendCardOption(player.getID(), "Select an action to be played twice", 1, actionInHand, true);
			//---[BEGIN TIMEOUT BLOCK]---
			int counter = 0; // timeout
			Object[] tempResponse = null;
			while(true)
			{
				tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
				if(tempResponse != null)
				{
					//---[BEGIN CODE BLOCK]---
					ArrayList<Integer> response = (ArrayList<Integer>) tempResponse[2];
					//Response form networking goes here.
					int selected = response.get(0);
					if(selected == -1)
					{
						game.sendMessage("No card has been played", player.getID());
					}
					else 
					{
						Card cardSelected = player.getHand().get(selected);
						//Do the twice
						for(int o =0; o < 2; o++)
						{
							for(int i:cardSelected.getEffectCode())
							{
								triggerEffect(i,player, cardSelected, board, players);					
							}
						}
						player.putIntoPlay(cardSelected);
					}
					//---[END CODE BLOCK]---
					break;
				}
				counter++;
				if (counter > game.getWaitTime())
				{
					Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
					game.sendDisconnect(player.getID());
					break;
				}
				Thread.sleep(10);
			}
			//---[END TIMEOUT BLOCK]---
		}
	}
	
	/**
	 * Discard N cards from your hand then draw that many.
	 * @param player
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	private void discardNDrawN(Player player) throws InterruptedException
	{

		if(player.getHandSize()!=0)
		{
			game.sendCardOption(player.getID(), "Selecy any number of Cards, then discard them.", player.getHandSize(), player.getHand(), true);
			//---[BEGIN TIMEOUT BLOCK]---
			int counter = 0; // timeout
			Object[] tempResponse = null;
			while(true)
			{
				tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
				if(tempResponse != null)
				{
					//---[BEGIN CODE BLOCK]---
					ArrayList<Integer> response = (ArrayList<Integer>) tempResponse[2];
					ArrayList<Card> responseToCard = new ArrayList<Card>();
					ArrayList<Card> tempHand =player.getHand();
					int i = response.size();
					if(i > 0)
					{
						for(int index : response)
						{
							responseToCard.add(tempHand.get(index));
						}
						for(Card c: responseToCard)
						{
							player.discardCard(c);
							player.removeFromHand(c);
						}
						StringBuilder builder = new StringBuilder();
						//The call drawCard returns a list of the names drawn
						for(String s : player.drawCard(i))
						{
							builder.append(s);
							builder.append(", ");
						}
						builder.setLength(builder.length() - 2);
						game.sendMessage("You have drawn: " + builder.toString(), player.getID());
					}
					//---[END CODE BLOCK]---
					break;
				}
				counter++;
				if (counter > game.getWaitTime())
				{
					Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
					game.sendDisconnect(player.getID());
					break;
				}
				Thread.sleep(10);
			}
			//---[END TIMEOUT BLOCK]---
		}
	}
	/**
	 * Idk.
	 * @param player
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	private void browseDiscard1OnTop(Player player) throws InterruptedException
	{
		if(player.getDiscardSize() != 0)
		{
			ArrayList<Card> choice	=	new ArrayList<Card>(player.getDiscard());
			game.sendCardOption(player.getID(), "Select a card to put on top of your deck(if any)", 1, choice, true);
			//---[BEGIN TIMEOUT BLOCK]---
			int counter = 0; // timeout
			Object[] tempResponse = null;
			while(true)
			{
				tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
				if(tempResponse != null) 
				{
					//---[BEGIN CODE BLOCK]---
					ArrayList<Integer> response = (ArrayList<Integer>) tempResponse[2];
					Card selection = choice.get(response.get(0));
					choice.remove(selection);
					player.addCardDecktop(selection);
					player.setDiscard(new LinkedBlockingDeque<Card>(choice));
					//---[END CODE BLOCK]---
					break;
				}
				counter++;
				if (counter > game.getWaitTime())
				{
					Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
					game.sendDisconnect(player.getID());
					break;
				}
				Thread.sleep(10);
			}
			//---[END TIMEOUT BLOCK]---
		}
	}

	/**
	 * Discard the top card of your deck, if its an action card play it.
	 * @param player
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	private void playVassal(Player player) throws InterruptedException
	{
		//Assume card drawn is not action card
		boolean discard = true;
		//Draw card
		player.drawCard(1);
		//Find card in hand
		Card topCard = player.getHand().get(player.getHandSize()-1);
		//Check if it is an action card
		for(String type: topCard.getDisplayTypes())
		{
			if(type.equals("Action"))
			{
				ArrayList<Card> tempChoice = new ArrayList<Card>();
				tempChoice.add(topCard);
				game.sendCardOption(player.getID(), "You drew a " + topCard.getName() +" do you wish to play it?", 1,tempChoice , true);
				//---[BEGIN TIMEOUT BLOCK]---
				int counter = 0; // timeout
				Object[] tempResponse = null;
				while(true)
				{
					tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
					if(tempResponse != null) 
					{
						//---[BEGIN CODE BLOCK]---
						ArrayList<Integer>response = (ArrayList<Integer>) tempResponse[2];
						if(response.get(0) != -1)
						{
							player.playCard(topCard, 0);
							//Don't discard it
							discard = false;
						}
						//---[END CODE BLOCK]---
						break;
					}
					counter++;
					if (counter > game.getWaitTime())
					{
						Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
						game.sendDisconnect(player.getID());
						break;
					}
					Thread.sleep(10);
				}
				//---[END TIMEOUT BLOCK]---
				//If action card, play it
				break;
			}
		}
		if(discard)
		{
			//Discard if not action card
			player.removeFromHand(topCard);
			player.discardCard(topCard);
		}
	}
	/**
	 * Draw 1 card, gain 2 actions
	 * @param player
	 */
	private void draw1Get2Actions(Player player)
	{
		player.drawCard(1);
		player.addActions(2);
	}

	/**
	 * Gain 1 card costing up to 4.
	 * @param player
	 * @param board
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	private void gain1MaxCost4(Player player, Board board) throws InterruptedException
	{
		ArrayList<Card> choice = getChoice(c-> c.getCost() <= 4,board);
		game.sendCardOption(player.getID(), "Select a card to gain costing up to 4.", 1, choice, false);
		//---[BEGIN TIMEOUT BLOCK]---
		int counter = 0; // timeout
		Object[] tempResponse = null;
		while(true)
		{
			tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayList.class));
			if(tempResponse != null)
			{
				//---[BEGIN CODE BLOCK]---
				ArrayList<Integer> response= (ArrayList<Integer>) tempResponse[2];
				Card selection = choice.get(response.get(0));
				board.cardRemove(selection.getName());
				player.discardCard(selection);
				player.gain(selection);
				//---[END CODE BLOCK]---
				break;
			}
			counter++;
			if (counter > game.getWaitTime())
			{
				Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
				game.sendDisconnect(player.getID());
				break;
			}
			Thread.sleep(10);
		}
		//---[END TIMEOUT BLOCK]---
	}
	/**
	 * Add a silver ontop top of deck.
	 * <p>Other players take a victory card from their hand and put ontop of their deck. 
	 * <p>If other players have no victory cards - they reveal their hand
	 * @param player
	 * @param board
	 * @param players
	 * @throws InterruptedException
	 */
	private void silverOnDeckRevealVC(Player player,Board board, Player[] players) throws InterruptedException
	{
		ArrayList<Player> expectedResponses= new ArrayList<Player>();
		ArrayList<ArrayList<Card>> selectedCards= new ArrayList<ArrayList<Card>>();
		Card silver = board.canGain("Silver");
		if (silver != null)
		{
			player.addCardDecktop(silver);
		}
		for(Player other: players)
		{
			if(other.equals(player))
			{
				continue;
			}
			//Find out if has any victory cards in hand
			ArrayList<Card> choice = (ArrayList<Card>) other.getHand().stream().filter(c -> Arrays.stream(c.getDisplayTypes()).anyMatch(type -> type.equals("Victory") ) ).collect(Collectors.toList());
			if (choice.size() == 0) 
			{
				StringBuilder hand = new StringBuilder();
				for(Card c : other.getHand())
				{
					hand.append(c.getName());
					hand.append(", ");
				}
				hand.setLength(hand.length() - 2);
				game.sendMessageAll(other.getName() + " reveals a hand with NO vicotry cards: " + hand.toString());
			}
			else if(choice.size() == 1) 
			{
				Card tempCard = choice.get(0);
				game.sendMessageAll(other.getName() + " reveals a " + tempCard.getName() + " and puts it ontop of their deck.");
				other.removeFromHand(tempCard);
				other.addCardDecktop(tempCard);
			}
			else 
			{
				//ask player what card to reveal
				game.sendCardOption(other.getID(), "Select a victory card to reveal", 1, choice, false);
				expectedResponses.add(other);
				selectedCards.add(choice);
			}

		}
		//---[BEGIN TIMEOUT BLOCK]---
		int counter = 0; // timeout
		Object[] tempResponse = null;
		while(expectedResponses.size()>0) {

			while(true) {
				tempResponse=rSpace.getp(new FormalField(Integer.class),new ActualField(ClientCommands.selectCard),new FormalField(ArrayList.class));
				if(tempResponse != null) {


					int pID = (int) tempResponse[0];
					ArrayList<Integer> response =(ArrayList<Integer>) tempResponse[2];
					//Find out what player responded
					for(Player rPlayer : expectedResponses) {
						if(rPlayer.getID() == pID) {
							int cardIndex=expectedResponses.indexOf(rPlayer);
							//---[BEGIN CODE BLOCK]---

							Card tempCard = selectedCards.get(cardIndex).get(response.get(0));
							game.sendMessageAll(rPlayer.getName()+" reveals a "+tempCard.getName());
							rPlayer.removeFromHand(tempCard);
							rPlayer.addCardDecktop(tempCard);
							//---[END CODE BLOCK]---
							expectedResponses.remove(rPlayer);
							selectedCards.remove(cardIndex);
							counter =0; // reset timeout after player response
							break;
						}
					}




					break;
				}
				counter++;
				if (counter > game.getWaitTime())
				{
					for(Player dPlayer : expectedResponses) {
						Log.important(dPlayer.getName() + "#" + dPlayer.getID() + " has been timed out!");
						game.sendDisconnect(dPlayer.getID());
					}

					break;
				}
				Thread.sleep(10);
			}}
		//---[END TIMEOUT BLOCK]---

	}
	private void get2TempOthersDiscard(Player player, Player[] players) throws InterruptedException
	{
		ArrayList<Player> expectedResponses= new ArrayList<Player>();
		ArrayList<ArrayList<Card>> selectedCards= new ArrayList<ArrayList<Card>>();
		player.addMoney(2);
		for(Player other: players) {
			//Player should not discard
			if(other.equals(player))
				continue;
			ArrayList<Card> tempHand = other.getHand();
			if(tempHand.size()<= 3) {
				continue;
			}else {
				game.sendCardOption(other.getID(), "Select 3 cards to keep, the rest is discarded", 3, tempHand, false);
				expectedResponses.add(other);
				selectedCards.add(tempHand);
			}
		}

		//---[BEGIN TIMEOUT BLOCK]---
		int counter = 0; // timeout
		Object[] tempResponse = null;
		while(expectedResponses.size()>0) {

			while(true) {
				tempResponse=rSpace.getp(new FormalField(Integer.class),new ActualField(ClientCommands.selectCard),new FormalField(ArrayList.class));
				if(tempResponse != null) {


					int pID = (int) tempResponse[0];
					ArrayList<Integer> response =(ArrayList<Integer>) tempResponse[2];
					//Find out what player responded
					for(Player rPlayer : expectedResponses) {
						if(rPlayer.getID() == pID) {
							int cardIndex=expectedResponses.indexOf(rPlayer);
							//---[BEGIN CODE BLOCK]---

							ArrayList<Card> newHand = new ArrayList<Card>();
							for(int i: response) {
								newHand.add(selectedCards.get(cardIndex).get(i));
							}
							rPlayer.setHand(newHand);
							//---[END CODE BLOCK]---
							expectedResponses.remove(rPlayer);
							selectedCards.remove(cardIndex);
							counter =0; // reset timeout after player response
							break;
						}
					}
					break;
				}
				counter++;
				if (counter > game.getWaitTime())
				{
					for(Player dPlayer : expectedResponses) {
						Log.important(dPlayer.getName() + "#" + dPlayer.getID() + " has been timed out!");
						game.sendDisconnect(dPlayer.getID());
					}
					break;
				}
				Thread.sleep(10);
			}
		}
		//---[END TIMEOUT BLOCK]---
	}

	/**
	 * Helper function to return list of available cards based on a predicate
	 */
	private ArrayList<Card> getChoice(Predicate<Card> p, Board board)
	{
		ArrayList<Card> choice =(ArrayList<Card>) board.getCardStream().filter(p).collect(Collectors.toList());
		return choice;
	}

	/**
	 * Looks through the player for possible counter players from a play played by a specific player
	 * @param player
	 * @param card
	 * @param board
	 * @param players
	 * @return
	 * @throws InterruptedException
	 */
	private ArrayList<Player> findCounterPlays(Player player, Card card, Board board, Player[] players) throws InterruptedException {
		boolean counterPlay;
		ArrayList<Player> affectedPlayers = new ArrayList<Player>();
		for(Player p: players)
		{
			counterPlay = false;
			if(!player.isConnected())
				break;
			for(Card c : p.getHand()) {
				//Reaction card handling
				switch(c.getName()) {

				case "Moat": 
					game.sendCardOption(p.getID(), "Do you wish to reveal your Moat to be unaffected by"+ card.getName()+"?", 1, (List<Card>) c, true);
					//---[BEGIN TIMEOUT BLOCK]---
					int counter = 0; // timeout
					Object[] tempResponse = null;
					while(true) {
						tempResponse=rSpace.getp(new ActualField(p.getID()),new ActualField(ClientCommands.selectCard),new FormalField(ArrayList.class));
						if(tempResponse != null) {
							//---[BEGIN CODE BLOCK]---
							ArrayList<Integer> response = (ArrayList<Integer>) tempResponse[2];
							if(response.get(0) != -1) {
								counterPlay = true;
								game.sendMessageAll(p.getName()+" has revealed a Moat");
							}
							//---[END CODE BLOCK]---
							break;
						}
						counter++;
						if (counter > game.getWaitTime())
						{

							Log.important(player.getName() + "#" + player.getID() + " has been timed out!");
							game.sendDisconnect(player.getID());
							break;
						}
						Thread.sleep(10);
					}
					//---[END TIMEOUT BLOCK]---

					//Based on result, set counter to true or false
					break;
				}

			}
			if(counterPlay) {

				continue;
			}
			else {
				affectedPlayers.add(p);
			}
		}
		return affectedPlayers;
	}

	/**
	 * Expandable "on play" card handler. Currently only needs three params but will
	 * expand with future implementation
	 * @param effect String with the c
	 * @param owner
	 * @param card
	 */
	private void playerEffects(String effect,Player owner, Card card) {
		switch(effect) {

		case "SilverNextMoney1":
			if(card.getName().equals("Silver")) {
				owner.addMoney(1);
				owner.removeEffect(effect);
			}
			break;
		}
	}

}