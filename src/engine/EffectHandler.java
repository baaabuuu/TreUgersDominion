package engine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import cards.Card;
import log.Log;
public class EffectHandler
{	
	
	private Game game;
	public EffectHandler(Game game) {
		this.game=game;
		
	}
	/**
	 * Helper function to return list of available cards based on a predicate
	 */
private ArrayList<Card> getChoice(Predicate<Card> p, Board board) {
	ArrayList<Card> choice =(ArrayList<Card>) board.getCardStream().filter(p).collect(Collectors.toList());
	 
		return choice;
	}
	private ArrayList<Player> findCounterPlays(Player player, Card card, Board board, Player[] players) throws InterruptedException {
		boolean counter;
		ArrayList<Player> affectedPlayers = new ArrayList<Player>();
		for(Player p: players) {
			counter = false;
			if(!player.isConnected()) break;
			for(Card c : p.getHand()) {
				//Reaction card handling
				switch(c.getName()) {
				
				case "Moat": 
					game.sendCardOption(p.getID(), "Do you wish to reveal your Moat to be unaffected by"+ card.getName()+"?", 1, (List<Card>) c, true);
					
					//Based on result, set counter to true or false
					break;
				}
				
			}
			if(counter) {
				
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
		//If it is an attack, can somoene use a reaction card?
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
				discardTopPlayAction(player);
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
				playActionFromHandtwice(player, board, affectedPlayers);
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
			default:
				Log.important("Invalid effect code: "+n);
				break;//Invalid effect error here;

		}
		//After every effect, update player hand
		game.sendPlayerHand(player.getID(), player.getID());
	}
	private void gainGoldOthersReveal(Player player, Board board, Player[] players) throws InterruptedException {
		Card gold =board.canGain("Gold");
		if(gold != null) {
			player.discardCard(gold);
			board.cardRemove("Gold");
		}
		for(Player p : players) {
			if(p.equals(player)) {
				continue;
			}
			String[]drawn = p.drawCard(2);
			//Measure to avoid index out of bounds in case of empty discard and deck
			String drawnToString="";
			for(String s:drawn) {
				drawnToString+= s+" ";
			}
				
			game.sendMessageAll(p.getName()+" has revealed "+drawnToString);
			//Remove cards from hand
			ArrayList<Card> tempCards = new ArrayList<Card>();
			for(int i =1;i<=drawn.length;i++) {
				tempCards.add(p.getHand().get(p.getHandSize()-i));
				p.removeFromHand(p.getHand().get(p.getHandSize()-i));
			}
			boolean hasTreasure =false;
			//See if cards are treasure
			for(Card c:tempCards) {
				for(String s:c.getDisplayTypes()) {
					if(s.equals("Treasure")&& !c.getName().equals("Copper")) {
						hasTreasure =true;
						break;
					}
				}
			}
			if(hasTreasure) {
				game.sendCardOption(p.getID(), "Choose a card to trash, other will be discarded", 1, tempCards, false);
				ArrayList<Integer> response = null;
				Card selection =tempCards.get(response.get(0));
				board.trashCard(selection);
				tempCards.remove(selection);
				
				Card nullCheck = tempCards.get(0);
				if (nullCheck != null) {
					p.discardCard(nullCheck);
				}
				
			}else {
				for(int i =tempCards.size()-1;i >= 0 ;i--) {
					p.addCardDecktop(tempCards.get(i));
				}
			}
		}
	}
	private void gainPlus5(Player player, Board board) throws InterruptedException {
		ArrayList<Card> choice = getChoice(bcard-> bcard.getCost()<=5, board);
		
		game.sendCardOption(player.getID(), "Select a card to gain", 1, choice, false);
		
		ArrayList<Integer> response = null;
		Card c =choice.get(response.get(0));
		board.cardRemove(c.getName());
		ArrayList<Card> tempHand = player.getHand();
		tempHand.add(c);
		player.setHand(tempHand);
		game.sendCardOption(player.getID(), "Select a card to put on top of deck", 1, tempHand, false);
		response =null;
		c = tempHand.get(response.get(0));
		player.removeFromHand(c);
		player.addCardDecktop(c);
	}
	private void mayTrashTreasure(Player player, Board board) throws InterruptedException {
		ArrayList<Card> choice = new ArrayList<Card>();
		for(Card c : player.getHand()) {
			for(String type: c.getDisplayTypes()) {
				if(type.equals("Treasure")) {
				choice.add(c);
				break;
				}
			}
		}
		if(choice.size() > 0) {
			
		game.sendCardOption(player.getID(), "Trash treasure to gain treasure costing upto 3 more?", 1, choice, true);
		ArrayList<Integer> response = null;
			if(response.get(0) !=-1) {
				Card c = choice.get(response.get(0));
				
				choice = getChoice(bcard ->bcard.getCost()<=c.getCost()+3&& Arrays.stream(bcard.getDisplayTypes()).anyMatch(type -> type.equals("Treasure")), board);
				if(choice.size() >0) {
				game.sendCardOption(player.getID(), "Choose a card to gain", 1, choice, false);
				response = null;
				Card c2 = choice.get(response.get(0));
				player.discardCard(c2);
				board.cardRemove(c2.getName());
				board.trashCard(c);
				player.removeFromHand(c);
				}
				else {
					game.sendMessage("No cards to gain, your treasure has not been trashed", player.getID());
				}
			}
		else {
			game.sendMessage("No treasure in hand", player.getID());
			}
		}
	}
	
	private void drawTill7(Player player) throws InterruptedException {
		ArrayList<Card> toBeDiscarded = new ArrayList<Card>();
		while(player.getHandSize() <7 || (player.getDeckSize()>0 && player.getDiscardSize()>0)) {
			player.drawCard(1);
			game.sendPlayerHand(player.getID(), player.getID());
			Card currentDraw =player.getHand().get(player.getHandSize()-1);
			
			
			for(String dispType: currentDraw.getDisplayTypes()) {
				if(dispType.equals("Action")) {
					ArrayList<Card> choice = new ArrayList<Card>();
					ArrayList<Integer> response = new ArrayList<Integer>();
					choice.add(currentDraw);
					game.sendCardOption(player.getID(), "Do you wish to keep "+currentDraw.getName()+" ?", 1, choice, true);
					
					if(response.get(0)!= -1) {
						break;
					}
					else {
						player.removeFromHand(currentDraw);
						toBeDiscarded.add(currentDraw);
					}
					break;
				}
				
			}
			
			
		}
		for (Card cardToDiscard: toBeDiscarded) {
			player.discardCard(cardToDiscard);
		}
		
	}
	private void trashFromHandGainPlus2(Player player, Board board) throws InterruptedException {
	
		ArrayList<Card> tempHand = player.getHand();
		if(tempHand.size()> 0) {
		game.sendCardOption(player.getID(), "Choose a card to trash",1, tempHand, false);
		
		ArrayList<Integer> response=null;
		Card trashCard = tempHand.get(response.get(0));
		board.trashCard(trashCard);
		ArrayList<Card> choice = getChoice(bcard -> bcard.getCost()<= trashCard.getCost()+2,board);
		
		game.sendCardOption(player.getID(), "Choose 1 card to gain", 1,choice, false);
		response = null;
		//Since we only ask for one choice, we always know where it is.
		Card c =choice.get(response.get(0));
		player.discardCard(c);
		board.cardRemove(c.getName());
		}
		else {
			game.sendMessage("No cards in hand, nothing to trash", player.getID());
		}
		
	}
	private void discardPerEmptySupply(Player player, Board board) throws InterruptedException {
		int count =0;
		for (String cardName : board.getBoardNamesArray())
		{
			if (board.canGain(cardName) == null)
			{
				count++;
			}
				
		}
	game.sendCardOption(player.getID(), "Choose "+count+" cards to discard", count, player.getHand(), false);
	ArrayList<Integer> response = null; // Response here
	ArrayList<Card> toDiscard = new ArrayList<Card>();
	ArrayList<Card> tempHand = player.getHand();
	for(int i: response) {
		toDiscard.add(tempHand.get(i));
	}
	for(Card c: toDiscard) {
		tempHand.remove(c);
	}
	player.setHand(tempHand);
	//Hand setting happens at the endof the effect trigger
		
	}
	private void mayTrashCopperGain2(Player player,Board board) throws InterruptedException {
		for(Card copper: player.getHand()) {
			if(copper.getName().equals("Copper")) {
				
				ArrayList<Card> copperList = new ArrayList<Card>();
				copperList.add(copper);
				game.sendCardOption(player.getID(), "Trash copper to gain card costing 2 more?(You can cancel if you do not want any of the available cards)", 1, copperList, true);
				ArrayList<Integer> response= null;
				if(response.get(0)==-1){
					break;
				}else {
					ArrayList<Card> choice = getChoice(bcard-> bcard.getCost()<=copper.getCost()+2,board);
				if(choice.size()==0) {
					game.sendMessage("No cards meeting requirements available", player.getID());
					break;
				}
				else {
					game.sendCardOption(player.getID(), "Select the card you would like to gain", 1, choice, false);
					response=null;
					//We already checked that atleast one copy is available in the deck
					player.discardCard(board.canGain(choice.get(response.get(0)).getName()));
					board.cardRemove(choice.get(response.get(0)).getName());
					
					ArrayList<Card> tempHand = player.getHand();
					tempHand.remove(copper);
					board.trashCard(copper);
					player.setHand(tempHand);
					game.sendPlayerHand(player.getID(), player.getID());
					
					break;
				}
				
				}
			}
		}
		//Tell player: You don't have copper
	}
	private void draw2OthersCurse(Player player, Player[] players,Board board) throws InterruptedException {
		player.drawCard(2);
		for(Player other: players) {
			if(other.equals(player)) {
				continue;
			}
			game.sendMessage("Gained a Curse from "+player.getName()+"'s card", other.getID());
			Card curse = board.canGain("Curse");
			if(curse != null){
				other.discardCard(curse);
				board.cardRemove("Curse");
			}
			
		}
		
		
	}
	private void draw1Action1Look2(Player player) throws InterruptedException { //This method is bound to have errors, remember this for testing
		 LinkedBlockingDeque<Card> tempDeck = player.getDeck();
		 ArrayList<Card> choice = new ArrayList<Card>();
		player.drawCard(1);
		player.addActions(1);
		Card card1 = tempDeck.pollFirst();
		Card card2 = tempDeck.pollFirst();
		choice.add(card1);
		choice.add(card2);
		
		game.sendCardOption(player.getID(), "Select the cards you would like to discard if any(In order)", 2, choice, true);
		
		ArrayList<Integer> response = null; // response here
		
		List<Card> choice2 = choice;
		if(response.get(0)!=-1) {
			for(int i: response ) {
				player.discardCard(choice.get(i));
				choice2.remove(choice.get(i));
				
			}
		}
		if (response.size()!=2) {
			game.sendCardOption(player.getID(), "Select the cards you would like to trash if any(In order)", response.size(), choice2, true);
			ArrayList<Integer> response2 = null; //response here
			List<Card> choice3 = choice2;
			if(response2.get(0)!=-1) {
				for(int i: response2 ) {
				player.discardCard(choice2.get(i));
				choice3.remove(choice2.get(i));
					}
			}
			if(response2.size()!=2) {
			game.sendCardOption(player.getID(), "Select the order you would like to put the remaining cards back in", response2.size(), choice3, false);
			ArrayList<Integer> response3 = null; //response here		
				for(int i: response3) {
							tempDeck.addFirst(choice3.get(i));
				}
			}
		}
		
		player.setDeck(tempDeck);
	}	
	private void draw1Action1Buy1Tempmoney(Player player) {
		player.drawCard(1);
		player.addActions(1);
		player.addBuys(1);
		player.addMoney(1);
	}
	private void draw2Action1(Player player) {
		player.drawCard(2);
		player.addActions(1);
		
	}
	private void action2Buy1Money2(Player player) {
		player.addActions(2);
		player.addBuys(1);
		player.addMoney(2);
		
	}
	private void draw4Buy1OthersDraw(Player player, Player[] players) throws InterruptedException {
		player.drawCard(4);
		player.addBuys(1);
		//NETWORK 
		//Allow all others to draw one card
		
		for(Player other: players) {
			if(other.equals(player)) {
				continue;
			}
			game.sendMessage("Drawing one card from the effect of "+player.getName()+"'s card", other.getID());
			other.drawCard(1);
			game.sendPlayerHand(other.getID(), other.getID());
		}
		
		
	}
	private void playActionFromHandtwice(Player player,Board board,Player[] players) throws InterruptedException {
		
		ArrayList<Card> actionInHand = new ArrayList<Card>();
		for(Card c : player.getHand()) {
			for(String s : c.getDisplayTypes()) {
				if (s.equals("Action")){
					actionInHand.add(c);
				}
			}
		}
		if(actionInHand.size()==0) {
			game.sendMessage("No action card found in hand", player.getID());
		}
		else {
		game.sendCardOption(player.getID(), "Select an action to be played twice", 1, actionInHand, true);
		}
		
		
		int selected =-1; //Response form networking goes here.
		if(selected == -1) {
			//Maybe tell the player that no card has been played?
		}
		else {
		Card cardSelected = player.getHand().get(selected);
		//Do the twice
			for(int o =0;o<2;o++) {
				for(int i:cardSelected.getEffectCode()) {
				triggerEffect(i,player, cardSelected, board, players);					
				}
			}
			player.putIntoPlay(cardSelected);
		}
	}
	private void discardNDrawN(Player player) throws InterruptedException
	{
		
		
		
		
		; //number of discards
		if(player.getHandSize()==0)
		{
			game.sendMessage("You cannot discard cards from a hand with size 0", player.getID());
		}
		else
		{
			game.sendCardOption(player.getID(), "Discard as many cards as you would like, then draw the same amount", player.getHandSize(), player.getHand(), true);
			ArrayList<Integer> response = new ArrayList<Integer>();
			ArrayList<Card> responseToCard = new ArrayList<Card>();
			ArrayList<Card> tempHand =player.getHand();
			int i= response.size();
			for(int index:response) {
				responseToCard.add(tempHand.get(index));
			}
			for(Card c: responseToCard) {
				player.discardCard(c);
				player.removeFromHand(c);
			}
			String allCards="";
			//The call drawCard returns a list of the names drawn
			for(String s:player.drawCard(i)) {
				allCards+=" "+s+" ";
			}
			game.sendMessage("You have drawn: "+allCards, player.getID());
			

		}
	}
	private void browseDiscard1OnTop(Player player)
	{
		if(player.getDiscardSize() == 0)
		{
			//Display invalid action
		}
		else
		{
			LinkedBlockingDeque<Card> discardTemp= player.getDiscard();
			for (Card card: discardTemp)
			{
				//UI show card
			}
			int select = 0; //Error suppression, does not actually need to be initialized
			//NETWORK either get number or "no"
			//either do nothing or add
			//Create temp discard pile.
			LinkedBlockingDeque<Card> tempDiscard = player.getDiscard();
			//Convert playerinput to Card object
			Card selectedCard= player.select(new ArrayList<Card>(tempDiscard), select);
			//Remove selected card fom temp discard
			tempDiscard.remove(selectedCard);
			//Set players discardpile to the temp pile
			player.setDiscard(tempDiscard);
			//Finally add the selected card on top of the deck
			player.addCardDecktop(selectedCard);
		}
	}
	private void discardTopPlayAction(Player player)
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
				//If action card, play it
				player.playCard(topCard, 0);
				//Don't discard it
				discard = false;
				break;
			}
		}
		if(discard)
		{
			//Discard if not action card
			player.discardCard(topCard);
		}
	}
	private void draw1Get2Actions(Player player)
	{
		player.drawCard(1);
		player.addActions(2);
	}

	private void gain1MaxCost4(Player player, Board board)
	{
		
		//NETWORK
		//Ask players what card to gain
		String cardName = "Placeholder";
		
		Card gainedCard = board.canGain(cardName);
		if(gainedCard.getCost() >4)
		{
			//Figure out how we do repetition with NETWORKING
		}
		else 
		{
			player.discardCard(gainedCard);
			board.cardRemove(cardName);
		}
	}
	private void silverOnDeckRevealVC(Player player,Board board, Player[] players)
	{
			board.canGain("Silver");
	}
	private void get2TempOthersDiscard(Player player, Player[] players)
	{
		player.addMoney(2);
		for(Player other: players) {
			//Player should not discard
			if(other.equals(player))
				continue;

			else {
				//NETWORK
				//Ask other players what cards they want to discard
			}

		}
	}

}