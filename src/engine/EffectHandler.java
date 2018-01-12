package engine;
import cards.Card;
import log.Log;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
public class EffectHandler
{	
	
	public ArrayList<Player> findCounterPlays(Player player, Card card, Board board, Player[] players) {
		boolean counter;
		ArrayList<Player> affectedPlayers = new ArrayList<Player>();
		for(Player p: players) {
			counter = false;
			for(Card c : p.getHand()) {
				if(c.getName().equals("Moat")){ //To be replaced with switch statement for future expansion
					//NETWORK query ask if want to counter this card
					//Set counter here
					Log.log(p + " has chosen to use their reaction from "+c.getName());
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
	public void playerEffects(String effect,Player owner, Card card) {
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
	 * @param players - List of playerIDs
	 */
	
	public void triggerEffect(int n, Player player, Card card, Board board, Player[] players){
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
				draw2OthersCurse(player,affectedPlayers);
				break;
			case 25:
				gainPlus5(player,board);
				break;
			default:
				Log.important("Invalid effect code: "+n);
				break;//Invalid effect error here;

		}
	}
	private void gainGoldOthersReveal(Player player, Board board, Player[] players) {
		Card gold =board.canGain("Gold");
		if(gold != null) {
			player.discardCard(gold);
			board.cardRemove("Gold");
		}
			//reveal 2 top cards
			//if revealed and is Treasure
			//not equal tó copper, trash one of them
			//And discard the others
		
	}
	private void gainPlus5(Player player, Board board) {
		//NETWORK
		//loop
		//What card to gain
		String cardName ="Placeholder";
		Card gainedCard =board.canGain(cardName);
		if(gainedCard != null && gainedCard.getCost()<=5) {
		board.cardRemove(cardName);
		}
		else {
			//Invalid card break networking loop
		}
	//loopend
	//NETWORK select a card
		int input =0; //Placeholder value. Should be player choice
	
		Card selected =player.select(player.getHand(), input);
		player.removeFromHand(selected);
		player.addCardDecktop(selected);
	}
	private void mayTrashTreasure(Player player, Board board) {
		//NETWORK
		//Query: Want to trash?
		//If yes, trash card from hand
		//Check of card is valid
		Card placeholder = new Card();
		for(String type: placeholder.getDisplayTypes()) {
			if(type.equals("Treasure")) {
				board.trashCard(placeholder);
				break;
			}
			
		}
		
		
		//Query: What card to gain?
		
		//Some kind of loop here
		
		String placeholder2= "placeholder";
		Card gainedCard=board.canGain(placeholder2);
		for(String type: gainedCard.getDisplayTypes()) {
			if(type.equals("Treasure")) {
				
				if(gainedCard != null && gainedCard.getCost()<= placeholder.getCost()+3) {
					board.cardRemove(placeholder2);
					ArrayList<Card> tempHand = player.getHand();
					tempHand.add(gainedCard);
					player.setHand(tempHand);
					}
					else {
						//Error, request new card
						
					}
				break;
			}
			
		}
		
		
	}
	
	private void drawTill7(Player player) {
		ArrayList<Card> toBeDiscarded = new ArrayList<Card>();
		while(player.getHandSize() <7 || player.getDeckSize()>0) {
			player.drawCard(1);
			Card currentDraw =player.select(player.getHand(), player.getHandSize()-1);
			for(String dispType: currentDraw.getDisplayTypes()) {
				if(dispType.equals("Action")) {
					//NETWORK
					//Ask if discard or keep
					boolean placeholder =true; //Keep card?
					if(placeholder) {
						break;
					}
					else {
						ArrayList<Card> tempHand = player.getHand();
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
	private void trashFromHandGainPlus2(Player player, Board board) {
	//NETWORK
	//Query: Want to trash?
	//If yes, trash card, 
	Card placeholder = new Card();
	board.trashCard(placeholder);
	//Query: What card to gain?
	
	//Some kind of loop here
	
	String placeholder2= "placeholder";
	Card gainedCard=board.canGain(placeholder2);
	if(gainedCard != null && gainedCard.getCost()<= placeholder.getCost()+2) {
	board.cardRemove(placeholder2);
	player.discardCard(gainedCard);
	}
	else {
		//Error, request new card
		
	}
	
	//Loop end
		
	}
	private void discardPerEmptySupply(Player player, Board board) {
		int count =0;
		for (String cardName : board.getBoardNamesArray())
		{
			if (board.canGain(cardName) == null)
			{
				count++;
			}
				
		}
		Log.log(player+ " needs to discard "+count+ " cards.");
		//NETWORK
		//Ask player to discard "count" cards
		//Reponse
		
		for(int i =0;i<count;i++) {
			//discard cards chosen
		}
		
	}
	private void mayTrashCopperGain2(Player player,Board board) {
		for(Card copper: player.getHand()) {
			if(copper.getName().equals("Copper")) {
				//Only network if player has copper
				//NETWORK 
				//Ask if player wants to trash if yes
				 ArrayList<Card> tempHand = player.getHand();
				tempHand.remove(copper);
				board.trashCard(copper);
				player.setHand(tempHand);
				break;
				
			}
		}
		//Tell player: You don't have copper
	}
	private void draw2OthersCurse(Player player, Player[] players) {
		player.drawCard(2);
		for(Player other: players) {
			if(other.equals(player)) {
				continue;
			}
			//NETWORK
			//Gain curse for everyone else
		}
		
		
	}
	private void draw1Action1Look2(Player player) {
		 LinkedBlockingDeque<Card> tempDeck = player.getDeck();
		player.drawCard(1);
		player.addActions(1);
		Card card1 = tempDeck.pollFirst();
		Card card2 = tempDeck.pollFirst();
		//Ask what cards to discard
		//Ask what cards to trash
		
		//Discard Routine based on input from NETWORK
		//Trash Routine based on input from NETWORK
		
		//Place remaining cards back on top of the deck in chosen order
		//player.setDeck();
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
	private void draw4Buy1OthersDraw(Player player, Player[] players) {
		player.drawCard(4);
		player.addBuys(1);
		//NETWORK 
		//Allow all others to draw one card
		for(Player other: players) {
			if(other.equals(player)) {
				continue;
			}
			//Do some tuple magic here
		}
		
	}
	private void playActionFromHandtwice(Player player,Board board,Player[] players) {
		//NETWORK
		//Select card to be played twice or if none 
		boolean placeholder=false;
		if(placeholder) {
			
		}
		else {
		//If wants to play action w/ double battlecry
			
		
		int selected =0; //Response form networking goes here.
		Card cardSelected = player.select(player.getHand(), selected);
		//Do the twice
			for(int o =0;o<2;o++) {
				for(int i:cardSelected.getEffectCode()) {
				triggerEffect(i,player, cardSelected, board, players);					
				}
			}
		}
	}
	private void discardNDrawN(Player player)
	{
		int i=0; //number of discards
		if(player.getHandSize()==0)
		{
			//Display "invalid action"
		}
		else
		{
			//counter to determine draws
			//NETWORK
			//access connected spacenetwork
			//wait for pspace with identifier
			//if discard, then then discard+increment counter
			//else draw equal to counter

			if(false)
			{

			}
			else 
			{
								player.drawCard(i);
			}
			//condition here

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
		
		Card gainedCard =board.canGain(cardName);
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