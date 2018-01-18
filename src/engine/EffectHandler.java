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
import objects.ArrayListObject;
import objects.ClientCommands;
import objects.PlayerEffects;
public class EffectHandler
{	

	private Game game;
	private Space rSpace;
	public EffectHandler(Game game)
	{
		this.game = game;
		this.rSpace = game.getSpace();
	}


	/**
	 * Coordinator of effects - calls the effect of cards.
	 * @param effectCode - Specifies the effect code
	 * @param player - PLayer that the effect should a apply toString[] players
	 * @param card - The card that was played.
	 * @param board - current board state
	 * @param players - List of playerObjects
	 * @throws InterruptedException 
	 */
	public void triggerEffect(int effectCode, Player player, Card card, Board board, Player[] players) throws InterruptedException{
		//First check if playing this card would trigger any other effect
		for(Player p: players)
		{
			if(p.getEffects().size() == 0)
			{
				continue;
			}
			else 
			{
				playerEffects(p, card);
			}
		}
		//If it is an attack, can someone use a reaction card?
		Player[] affectedPlayers = players;
		for(String type: card.getTypes())
		{
			if(type.equals("attack"))
			{
				ArrayList<Player> target = findCounterPlays(player, card, board, players);
				affectedPlayers = target.toArray(new Player[target.size()]);
				break;
			}
		}

		Log.important("Effect code: " + effectCode + " was called by " + card.getName() +
				" played by " + player.getName() + ".");
		switch(effectCode)
		{
		case 0: //Do nothing
			break;
		case 1: //Cellar - +1 Action Discard any number of Cards, then draw that many.
			playCellar(player); 
			break;
			//Draw 2 cards
		case 2: //Moat - +2 Cards
			playMoat(player);
			break;
		case 3://Moat - reveal reaction effect - dosn't trigger on play
			break;
		case 4: //Harbinger Look through your discard pile. You may put a card from it onto your deck.
			playHarbinger(player);
			break;
		case 5: //Merchant - Next time a silver is played, gain +1 temp monies
			playMerchant(player);
			break;
		case 6: //Vassal - +2 coin\nDiscard the top card of your deck. If it's an Action card, you may play it.
			playVassal(player);
			break;
		case 7: //Village - Draw 1 card, gain 2 actions
			playVillage(player);
			break;
		case 8: //Gain a card costing up to 4
			playWorkshop(player, board);
			break;
		case 9: //Bureaucrat - Silver on deck, reveal VP cards
			playBureaucrat(player, board, affectedPlayers);
			break;
		case 10: //	Garden - get 1 VP for every 10 cards
			break;
		case 11: //Militia - +2 money, each other player discards down to 3 Cards in hand.
			playMilitia(player, affectedPlayers);
			break;
		case 12: //Moneylender - You may trash a Copper from your hand for +3 money.
			playMoneylender(player, board);
			break;
		case 13: //Poacher - +1 Card, +1 action, +1 money\nDiscard a card per empty Supply pile.
			playPoacher(player, board);
			break;
		case 14: //Remodel - Trash a card from your hand. Gain a card costing up to 2 more than it.
			playRemodel(player, board);
			break;
		case 15: //Throne Room - You may play an Action card from your hand twice
			playThroneRoom(player, board, affectedPlayers);
			break;
		case 16: //Bandit - Gain a Gold. Each other player reveals the top 2 cards of their deck, trashes a revealed Treasure other than Copper, and discards the rest.
			playBandit(player, board, affectedPlayers);
			break;
		case 17: //Council Room - +4 Cards +1 Buy. Each other player draws a card"
			playCouncilroom(player, affectedPlayers);
			break;
		case 18: //Festival - +2 Actions, +1 buy, +2 money
			playFestival(player);
			break;
		case 19: //Laboratory - +2 Cards\n +1 action"
			playLaboratory(player);
			break;
		case 20: //Library - Draw until you have 7 cards in hand, skipping any Action cards you chose to; set those aside, discarding them afterwards.
			playLibrary(player);
			break;
		case 21: //Market - +1 card\n +1 action\n +1 buy\n +1 money
			playMarket(player);
			break;
		case 22://Mine - You may trash a Treasure from your hand. Gain a treasure to your hand costing up to 3 more than it.
			playMine(player, board);
			break; //Sentry - +1 Card\n +1 Action\nLook at the top 2 cards of your deck. Trash and/or discard any number of them. Put the rest back on top in any order.
		case 23:
			playSentry(player);
			break;
		case 24: //Witch - +2 Cards\nEach other player gains a Curse.
			playWitch(player, affectedPlayers, board);
			break;
		case 25: //Gain a card to your hand costing up to 5. Put a card from your hand onto your deck
			playArtisan(player, board);
			break;
		case 26: //Chapel - Trash up to 4 Cards from your hand.
			playChapel(player, board);
			break;
		case 27: //Smithy - +3 cards
			playSmithy(player);
			break;
		default:
			Log.important("Invalid effect code: " + effectCode);
			break;//Invalid effect error here;

		}
		//After every effect, update player hand
		game.sendPlayerHand(player.getID(), player.getID());
	}

	private void playMoat(Player player)
	{
		player.drawCard(2);
	}


	/**
	 * Draw 3 cards.
	 * @param player
	 */
	private void playSmithy(Player player)
	{
		player.drawCard(3);
	}


	/**
	 * Draw a card, gain an action.
	 * The first silver you play this turn gives +1 money - stacks
	 * @param player
	 */
	private void playMerchant(Player player)
	{
		player.drawCard(1);
		player.addActions(1);
		player.addEffect(PlayerEffects.Merchant);		
	}


	/**
	 * A player has the option to trash up to 4 cards from their hand
	 * @param player
	 * @param board
	 * @throws InterruptedException
	 */
	private void playChapel(Player player, Board board) throws InterruptedException
	{
		game.sendCardOption(player.getID(), "Select up to 4 cards you would like to trash", 4, player.getHand(), true);
		//---[BEGIN TIMEOUT BLOCK]---   
		int counter = 0; // timeout
		Object[] tempResponse = null;
		while(true) {
			tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
			if(tempResponse != null)
			{
				//---[BEGIN CODE BLOCK]---
				ArrayList<Integer> response = ((ArrayListObject) tempResponse[2]).getArrayList();
				
				if(response.get(0) == -1)
				{
					game.sendMessage("No cards trashed", player.getID());
				}
				else
				{
					ArrayList<Card> tempHand = player.getHand();
					ArrayList<Card> toTrash = new ArrayList<Card>();
					for(int index : response)
					{
						toTrash.add(tempHand.get(index));
					}
					for(Card card : toTrash)
					{
						game.sendMessageAll(player.getName() + "#" + player.getID() + " trashed " + card.getName() + "!");
						player.removeFromHand(card);
						board.trashCard(card);
						player.trash(card);
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
	 * Gain a Gold. Each other player reveals the top 2 cards of their deck, trashes a revealed Treasure other than Copper, and discards the rest.
	 * @param player
	 * @param board
	 * @param players
	 * @throws InterruptedException
	 */
	private void playBandit(Player player, Board board, Player[] players) throws InterruptedException
	{
		Card gold = board.canGain("Gold");
		ArrayList<Player> expectedResponses = new ArrayList<Player>();
		ArrayList<ArrayList<Card>> selectedCards = new ArrayList<ArrayList<Card>>();
		if(gold != null)
		{
			player.discardCard(gold);
			board.cardRemove("Gold");
			player.gain(gold);
			game.sendMessageAll(player.getName() + "#" + player.getID() + " gained a Gold!!");

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
				tempResponse = rSpace.getp(new FormalField(Integer.class), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
				if(tempResponse != null)
				{
					int pID = (int) tempResponse[0];
					ArrayList<Integer> response = ((ArrayListObject) tempResponse[2]).getArrayList();
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
							game.sendMessageAll(player.getName() + "#" + player.getID() + " discarded " + selection.getName() + "!");
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
	 * Gain a card to your hand costing up to 5.
	 * <p>Put a card from your hand onto your deck
	 * @param player
	 * @param board
	 * @throws InterruptedException
	 */
	private void playArtisan(Player player, Board board) throws InterruptedException
	{
		ArrayList<Card> choice = getChoice(bcard -> bcard.getCost() <= 5 && board.canGain(bcard.getName()) != null, board);
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
				tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
				if(tempResponse != null)
				{
					//---[BEGIN CODE BLOCK]---
					ArrayList<Integer> response = ((ArrayListObject) tempResponse[2]).getArrayList();
					c = choice.get(response.get(0));
					board.cardRemove(c.getName());
					tempHand.add(c);
					game.sendMessageAll(player.getName() + "#" + player.getID() + " addeded " + c.getName() + " to their hand!");
					player.setHand(tempHand);
					player.gain(c);

					game.sendCardOption(player.getID(), "Select a card to put on top of deck", 1, tempHand, false);
					//---[BEGIN TIMEOUT BLOCK]---
					int counter2 = 0; // timeout
					tempResponse = null;
					while(true)
					{
						tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
						if(tempResponse != null) 
						{
							//---[BEGIN CODE BLOCK]---
							 response = ((ArrayListObject) tempResponse[2]).getArrayList();
							c = tempHand.get(response.get(0));
							player.removeFromHand(c);
							player.addCardDecktop(c);
							game.sendMessageAll(player.getName() + "#" + player.getID() + " put " + c.getName() + " ontop of their deck!");
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
	private void playMine(Player player, Board board) throws InterruptedException
	{
		ArrayList<Card> choice = player.getHand().stream().filter(c -> Arrays.stream(c.getDisplayTypes()).anyMatch(type -> type.equals("Treasure"))).collect(Collectors.toCollection(ArrayList::new));
		if(choice.size() > 0)
		{
			game.sendCardOption(player.getID(), "Trash treasure card from your hand to gain a treasure costing upto 3 more.", 1, choice, true);
			//---[BEGIN TIMEOUT BLOCK]--
			int counter = 0; // timeout
			Object[] tempResponse = null;
			while(true)
			{
				tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
				if(tempResponse != null)
				{
					//---[BEGIN CODE BLOCK]---
					ArrayList<Integer> response = ((ArrayListObject) tempResponse[2]).getArrayList();
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
								tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
								if(tempResponse != null)
								{
									//---[BEGIN CODE BLOCK]---
									response = ((ArrayListObject) tempResponse[2]).getArrayList();
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

	/**
	 * Draw until you have 7 cards in hand, skipping any Action cards you chose to; set those aside, discarding them afterwards.
	 * @param player
	 * @throws InterruptedException
	 */
	private void playLibrary(Player player) throws InterruptedException
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
						tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
						if(tempResponse != null)
						{
							//---[BEGIN CODE BLOCK]---
							response = ((ArrayListObject) tempResponse[2]).getArrayList();
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
	 * Trash a card from your hand. Gain a card costing up to 2 more than it.
	 * @param player
	 * @param board
	 * @throws InterruptedException
	 */

	private void playRemodel(Player player, Board board) throws InterruptedException
	{
		ArrayList<Card> tempHand = player.getHand();
		Log.important("" + tempHand.size());
		if(tempHand.size() > 0)
		{
			game.sendCardOption(player.getID(), "Choose a card to trash from your hand.", 1, tempHand, false);
			//---[BEGIN TIMEOUT BLOCK]---
			int counter = 0; // timeout
			while(true)
			{
				Object[] tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
				if(tempResponse != null)
				{
					//---[BEGIN CODE BLOCK]---
					ArrayList<Integer> response = ((ArrayListObject) tempResponse[2]).getArrayList();
					Card trashCard = tempHand.get(response.get(0));
					board.trashCard(trashCard);
					player.trash(trashCard);
					ArrayList<Card> choice = getChoice(bcard -> bcard.getCost() <= trashCard.getCost() + 2,board);
					game.sendCardOption(player.getID(), "Choose 1 card to gain.", 1, choice, false);
					//---[BEGIN TIMEOUT BLOCK]---
					int counter2 = 0; // timeout
					tempResponse = null;
					while(true)
					{
						tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
						if(tempResponse != null)
						{
							//---[BEGIN CODE BLOCK]---
							response = ((ArrayListObject) tempResponse[2]).getArrayList();
							//Since we only ask for one choice, we always know where it is.
							int reply = response.get(0);
							Card c = choice.get(reply);
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
	 * Draw a card, gain an action.
	 * <p>Discard a card per empty supply pile.
	 * @param player
	 * @param board
	 * @throws InterruptedException
	 */

	private void playPoacher(Player player, Board board) throws InterruptedException
	{
		player.drawCard(1);
		player.addActions(1);
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
			tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
			if(tempResponse != null)
			{
				//---[BEGIN CODE BLOCK]---
				ArrayList<Integer> response = ((ArrayListObject) tempResponse[2]).getArrayList();
				ArrayList<Card> toDiscard = new ArrayList<Card>();
				ArrayList<Card> tempHand = player.getHand();
				for(int i: response)
				{
					toDiscard.add(tempHand.get(i));
				}
				for(Card card: toDiscard)
				{
					game.sendMessageAll(player.getName() + "#" + player.getID() + " discarded " + card.getName() + "!");
					tempHand.remove(card);
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
	private void playMoneylender(Player player, Board board) throws InterruptedException
	{
		int index = player.getFirstIndexOf("Copper");
		if (index == 0)
		{
			Card card = player.getHand().get(index);
			ArrayList<Card> copperList = new ArrayList<Card>();
			copperList.add(card);
			game.sendCardOption(player.getID(), "Trash a copper to gain +3 money.", 1, copperList, true);
			int counter = 0; // timeout
			while(true)
			{
				Object[] tempResponse = rSpace.getp(
						new ActualField(player.getID()), 
						new ActualField(ClientCommands.selectCard),
						new FormalField(ArrayListObject.class));
				if (tempResponse != null)
				{
				
					ArrayList<Integer> choices = ((ArrayListObject) tempResponse[2]).getArrayList();
					if (choices.get(0) == 0)
					{
						player.addMoney(3);
						player.trash(card);
						board.trashCard(card);
						game.sendMessageAll(player.getName() + "#" + player.getID() + " trashed " + card.getName() + " and gained 3 money!");
					}
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
		}
	}
	
	/**
	 * Draw 2 cards, other players draw a curse card.
	 * @param player
	 * @param players
	 * @param board
	 * @throws InterruptedException
	 */
	private void playWitch(Player player, Player[] players, Board board) throws InterruptedException
	{
		player.drawCard(2);
		for(Player other: players)
		{
			if(other.equals(player) || !other.isConnected())
			{
				continue;
			}
			game.sendMessageAll(other.getName() + " got a curse from " + player.getName());
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
	 * +1 Card +1 Action. 
	 * <p>Look at the top 2 cards of your deck. Trash and/or discard any number of them. Put the rest back on top in any order.
	 * @param player
	 * @throws InterruptedException
	 */

	private void playSentry(Player player) throws InterruptedException //This method is bound to have errors, remember this for testing
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
			tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
			if(tempResponse != null)
			{
				//---[BEGIN CODE BLOCK]---
				ArrayList<Integer> response = ((ArrayListObject) tempResponse[2]).getArrayList();
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
						tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
						if(tempResponse != null)
						{
							//---[BEGIN CODE BLOCK]---
							response = ((ArrayListObject) tempResponse[2]).getArrayList();
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
									tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
									if(tempResponse != null)
									{
										//---[BEGIN CODE BLOCK]---
										response = ((ArrayListObject) tempResponse[2]).getArrayList();	
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
	 * +1 card\n +1 action\n +1 buy\n +1 money
	 * @param player
	 */
	private void playMarket(Player player)
	{
		player.drawCard(1);
		player.addActions(1);
		player.addBuys(1);
		player.addMoney(1);
	}
	
	/**
	 * +2 Cards +1 action"
	 * @param player
	 */
	private void playLaboratory(Player player) 
	{
		player.drawCard(2);
		player.addActions(1);
	}
	
	/**
	 * +2 Actions, +1 buy, +2 money
	 * @param player
	 */
	private void playFestival(Player player)
	{
		player.addActions(2);
		player.addBuys(1);
		player.addMoney(2);

	}
	
	/**
	 * +4 Cards +1 Buy
	 * <p>Each other player draws a card"
	 * @param player
	 * @param players
	 * @throws InterruptedException
	 */
	private void playCouncilroom(Player player, Player[] players) throws InterruptedException
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

	private void playThroneRoom(Player player, Board board, Player[] players) throws InterruptedException
	{
		ArrayList<Card> actionInHand =  player.getHand().stream().filter(card -> Arrays.stream(card.getDisplayTypes()).filter(s -> s.equals("Action")).findAny().isPresent()).collect(Collectors.toCollection(ArrayList::new));
		if(actionInHand.size() != 0)
		{
			game.sendCardOption(player.getID(), "Select an action to be played twice", 1, actionInHand, true);
			//---[BEGIN TIMEOUT BLOCK]---
			int counter = 0; // timeout
			Object[] tempResponse = null;
			while(true)
			{
				tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
				if(tempResponse != null)
				{
					//---[BEGIN CODE BLOCK]---
					ArrayList<Integer> response = ((ArrayListObject) tempResponse[2]).getArrayList();
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
	 * +1 Action
	 * <p> Discard any number of Cards, then draw that many.
	 * @param player
	 * @throws InterruptedException
	 */

	private void playCellar(Player player) throws InterruptedException
	{
		player.addActions(1);
		if(player.getHandSize() != 0)
		{
			game.sendCardOption(player.getID(), "Selecy any number of Cards, then discard them.", player.getHandSize(), player.getHand(), true);
			//---[BEGIN TIMEOUT BLOCK]---
			int counter = 0; // timeout
			Object[] tempResponse = null;
			while(true)
			{
				tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
				if(tempResponse != null)
				{
					//---[BEGIN CODE BLOCK]---
					ArrayList<Integer> response = ((ArrayListObject) tempResponse[2]).getArrayList();
					ArrayList<Card> responseToCard = new ArrayList<Card>();
					ArrayList<Card> tempHand = player.getHand();
					int i = response.get(0);
					if(i >= 0)
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
	 * 
	 * @param player
	 * @throws InterruptedException
	 */

	private void playHarbinger(Player player) throws InterruptedException
	{
		player.drawCard(1);
		player.addActions(1);
		if(player.getDiscardSize() != 0)
		{
			ArrayList<Card> choice	=	new ArrayList<Card>(player.getDiscard());
			game.sendCardOption(player.getID(), "You may select a card to put on top of your deck.", 1, choice, true);
			//---[BEGIN TIMEOUT BLOCK]---
			int counter = 0; // timeout
			while(true)
			{
				Object[] tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
				if(tempResponse != null) 
				{
					//---[BEGIN CODE BLOCK]---
					ArrayList<Integer> response = ((ArrayListObject) tempResponse[2]).getArrayList();
					int count = response.get(0);
					if (count >= 0)
					{
						Card selection = choice.get(response.get(0));
						player.addCardDecktop(selection);
						player.getDiscard().remove(selection);
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
	 * +2 money, Discard the top card of your deck, if its an action card play it.
	 * @param player
	 * @throws InterruptedException
	 */

	private void playVassal(Player player) throws InterruptedException
	{
		boolean discard = true;
		player.addMoney(2);
		String[] result = player.drawCard(1);
		if (result[0] != null)
		{
			Card topCard = player.getHand().get(player.getHandSize() - 1);
			for(String type: topCard.getDisplayTypes())
			{
				if(type.equals("Action"))
				{
					ArrayList<Card> tempChoice = new ArrayList<Card>();
					tempChoice.add(topCard);
					game.sendCardOption(player.getID(), "You drew a " + topCard.getName() +" do you wish to play it?", 1, tempChoice, true);
					//---[BEGIN TIMEOUT BLOCK]---
					int counter = 0; // timeout
					Object[] tempResponse = null;
					while(true)
					{
						tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
						if(tempResponse != null) 
						{
							//---[BEGIN CODE BLOCK]---
							ArrayList<Integer>response = ((ArrayListObject) tempResponse[2]).getArrayList();
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
		
	}
	/**
	 * Draw 1 card, gain 2 actions
	 * @param player
	 */
	private void playVillage(Player player)
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

	private void playWorkshop(Player player, Board board) throws InterruptedException
	{
		ArrayList<Card> choice = getChoice(c-> c.getCost() <= 4,board);
		game.sendCardOption(player.getID(), "Select a card to gain costing up to 4.", 1, choice, false);
		//---[BEGIN TIMEOUT BLOCK]---
		int counter = 0; // timeout
		Object[] tempResponse = null;
		while(true)
		{
			tempResponse = rSpace.getp(new ActualField(player.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
			if(tempResponse != null)
			{
				//---[BEGIN CODE BLOCK]---
				ArrayList<Integer> response = ((ArrayListObject) tempResponse[2]).getArrayList();
				Card selection = choice.get(response.get(0));
				game.sendMessageAll(player.getName() + " gained a " + selection.getName());
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
		Log.important("are we out yet boys");
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

	private void playBureaucrat(Player player,Board board, Player[] players) throws InterruptedException
	{
		ArrayList<Player> expectedResponses = new ArrayList<Player>();
		ArrayList<ArrayList<Card>> selectedCards = new ArrayList<ArrayList<Card>>();
		Card silver = board.canGain("Silver");
		if (silver != null)
		{
			player.addCardDecktop(silver);
		}
		for(Player other: players)
		{
			if(other.equals(player) || !other.isConnected())
			{
				continue;
			}
			//Find out if has any victory cards in hand
			ArrayList<Card> choice =  other.getHand().stream().filter(c -> Arrays.stream(c.getDisplayTypes()).anyMatch(type -> type.equals("Victory") ) ).collect(Collectors.toCollection(ArrayList::new));
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
		while(expectedResponses.size() > 0)
		{
			while(true)
			{
				tempResponse = rSpace.getp(
						new FormalField(Integer.class), 
						new ActualField(ClientCommands.selectCard),
						new FormalField(ArrayListObject.class));
				if(tempResponse != null)
				{
					Log.important("No time out");
					int pID = (int) tempResponse[0];
					ArrayList<Integer> response = ((ArrayListObject) tempResponse[2]).getArrayList();
					//Find out what player responded
					for(Player rPlayer : expectedResponses)
					{
						if(rPlayer.getID() == pID)
						{
							int cardIndex=expectedResponses.indexOf(rPlayer);
							//---[BEGIN CODE BLOCK]---

							Card tempCard = selectedCards.get(cardIndex).get(response.get(0));
							game.sendMessageAll(rPlayer.getName()+" reveals a "+tempCard.getName());
							rPlayer.removeFromHand(tempCard);
							rPlayer.addCardDecktop(tempCard);
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
					@SuppressWarnings("unchecked")
					ArrayList<Player> expec = (ArrayList<Player>) expectedResponses.clone();
					for(Player dPlayer : expec)  
					{
						expectedResponses.remove(dPlayer);
						Log.important(dPlayer.getName() + "#" + dPlayer.getID() + " has been timed out!");
						game.sendDisconnect(dPlayer.getID());
					}

					break;
				}
				Thread.sleep(10);
			}}
		//---[END TIMEOUT BLOCK]---

	}

	private void playMilitia(Player player, Player[] players) throws InterruptedException
	{
		ArrayList<Player> expectedResponses			= new ArrayList<Player>();
		ArrayList<ArrayList<Card>> selectedCards	= new ArrayList<ArrayList<Card>>();
		player.addMoney(2);
		for(Player other: players)
		{
			//Player should not discard
			if(other.equals(player) || !other.isConnected())
				continue;
			ArrayList<Card> tempHand = other.getHand();
			if(tempHand.size() <= 3)
			{
				continue;
			}
			else
			{
				game.sendCardOption(other.getID(), "Select 3 cards to keep, the rest is discarded", 3, tempHand, false);
				expectedResponses.add(other);
				selectedCards.add(tempHand);
			}
		}

		//---[BEGIN TIMEOUT BLOCK]---
		int counter = 0; // timeout
		Object[] tempResponse = null;
		while(expectedResponses.size() > 0)
		{
			while(true)
			{
				tempResponse = rSpace.getp(new FormalField(Integer.class),
						new ActualField(ClientCommands.selectCard),
						new FormalField(ArrayListObject.class));
				if(tempResponse != null)
				{
					int pID = (int) tempResponse[0];
					ArrayList<Integer> response = ((ArrayListObject) tempResponse[2]).getArrayList();
					//Find out what player responded
					for(Player rPlayer : expectedResponses)
					{
						if(rPlayer.getID() == pID)
						{
							int cardIndex = expectedResponses.indexOf(rPlayer);
							//---[BEGIN CODE BLOCK]---
							ArrayList<Card> newHand = new ArrayList<Card>();
							for(int i: response)
							{
								newHand.add(selectedCards.get(cardIndex).get(i));
							}
							rPlayer.setHand(newHand);
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
				Log.important("" + counter);
				Log.important("" + game.getWaitTime());
				if (counter > game.getWaitTime())
				{
					@SuppressWarnings("unchecked")
					ArrayList<Player> playerList = (ArrayList<Player>) (expectedResponses.clone());
					for(Player dPlayer : playerList) {
						expectedResponses.remove(dPlayer);
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
		ArrayList<Card> choice =  board.getCardStream().filter(p).collect(Collectors.toCollection(ArrayList::new));
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
	private ArrayList<Player> findCounterPlays(Player player, Card card, Board board, Player[] players) throws InterruptedException
	{
		boolean counterPlay;
		ArrayList<Player> affectedPlayers = new ArrayList<Player>();
		for(Player p: players)
		{
			counterPlay = false;
			if(!p.isConnected())
				continue;
			for(Card c : p.getHand())
			{
				//Reaction card handling
				switch(c.getName())
				{
				case "Moat":
					ArrayList<Card> list = new ArrayList<Card>();
					list.add(c);
					game.sendCardOption(p.getID(), "Do you wish to reveal your Moat to be unaffected by" + card.getName() + "?", 1, list, true);
					//---[BEGIN TIMEOUT BLOCK]---
					int counter = 0; // timeout
					Object[] tempResponse = null;
					while(true)
					{
						tempResponse = rSpace.getp(new ActualField(p.getID()), new ActualField(ClientCommands.selectCard), new FormalField(ArrayListObject.class));
						if(tempResponse != null)
						{
							//---[BEGIN CODE BLOCK]---
							ArrayList<Integer> response = ((ArrayListObject) tempResponse[2]).getArrayList();
							if(response.get(0) != -1)
							{
								counterPlay = true;
								game.sendMessageAll(p.getName() + " has revealed a Moat");
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
				default:
					break;
				}
			}
			if(counterPlay)
			{
				continue;
			}
			else
			{
				affectedPlayers.add(p);
			}
		}
		return affectedPlayers;
	}

	/**
	 * Expandable "on play" card handler. Currently only needs three params but will
	 * expand with future implementation
	 * @param player
	 * @param card
	 */
	private void playerEffects(Player player, Card card)
	{
		for (PlayerEffects effect : player.getEffects())
		{
			switch(effect)
			{
			case Merchant: //The first time you play a silver this turn, +1 money.
				merchantEffect(player, card);
				break;
			default :
				break;
			}
		}
		
	}
	
	/**
	 * If the player has the merchant effect - this is triggered.
	 * @param player
	 * @param card
	 */
	private void merchantEffect(Player player, Card card)
	{
		if (card.getName().equals("Silver") && player.equals(game.getCurrentPlayer()))
		{
			player.addMoney(1);
			player.removeEffect(PlayerEffects.Merchant);
		}		
	}

}