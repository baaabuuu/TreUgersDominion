package engine;
import Engine.Player;
import cards.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import org.jspace.*;
public class EffectHandler {

/**
 * Creates an instance of the EffectHandler
 */
public EffectHandler() {};
/**
 * Coodinator of effects- Call this on a per-board basis
 * @param n - Specifies the effect code
 * @param player - PLayer that the effect should a apply toString[] players
 * @param card - The card that was played.
 * @param board - current board state
 * @param players - List of playerIDs
 */

public void triggerEffect(int n,Player player,Card card,Board board,ArrayList<Player> players) {
	switch(n) {
		//Do nothing
		case 0: 
			break;
		//Discard any number of cards, then draw that many.
		case 1: discardNDrawN(player); 
			break;
		//Draw 2 cards
		case 2: player.drawCard(2);
			break;
		//Reveal and become immune to an attack card
		case 3: //Implement reveal
			break;
		//Look through discard and maybe put one on top of the deck
		case 4: browseDiscard1OnTop(player);
			break;
		//Next time a silver is played, gain +1 temp monies
		case 5: player.addEffect("1TempOnNextSilver");
			break;
		//Discard top of deck, if action card, play it.
		case 6: discardTopPlayAction(player);
			break;
		//Draw 1, get 2 actions
		case 7: draw1Get2Actions(player);
			break;
		//Gain a card costing upto 4
		case 8: gain1MaxCost4();
			break;
		//Silver on deck, reveal VP cards
		case 9:silverOnDeckRevealVC(player,players);
			break;
		//get 1 VP for every 10 cards
		case 10: player.addEffect("1VPPer10Cards"); //this might need to go somehwere else
			break;
		//Get 2 temp money, others discard untill they have 3 cards left
		case 11: get2TempOthersDiscard(player,players);
			break;
		//May trash copper, if so, add +3 tempmoney
		case 12: //wait for trash
			break;
		default: //Invalid effect error here;
		
	}
}
private void discardNDrawN(Player player){
	int i=0; //number of discards
	if(player.getHandSize()==0) {
		//Display "invalid action"
	}
	else {
	//counter to determine draws
	//NETWORK
	//access connected spacenetwork
	//wait for pspace with identifier
	//if discard, then then discard+increment counter
	//else draw equal to counter
		
		if(false) {
			
		}
		else {
			player.drawCard(i);
		}
		//condition here
			
	}
}
private void browseDiscard1OnTop(Player player) {
	if(player.getDiscardSize()==0) {
	//Display invalid action
	}
	else
	{
	LinkedBlockingDeque<Card> discardTemp= player.getDiscard();
	for (Card card: discardTemp) {
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
private void discardTopPlayAction(Player player) {
		//Assume card drawn is not action card
	boolean discard = true;
		//Draw card
	player.drawCard(1);
		//Find card in hand
	Card topCard = player.getHand().get(player.getHandSize()-1);
		//Check if it is an action card
	for(String type: topCard.getDisplayTypes()) {
		if(type.equals("action")) {
				//If action card, play it
			player.playCard(topCard);
				//Don't discard it
			discard = false;
			break;
		}
	}
	if(discard) {
			//Discard if not action card
		player.discardCard(topCard);
	}
}
private void draw1Get2Actions(Player player){
	player.drawCard(1);
	player.addActions(2);
	}

private void gain1MaxCost4() {
	//Requires methods in board 
	}
private void silverOnDeckRevealVC(Player player,List<Player> players) {
	//Requires a reveal and some stuff from patrick
	}
private void get2TempOthersDiscard(Player player, List<Player> players) {
	player.addMoney(2);
	for(Player other: players) {
		//Player should not discard
		if(other.equals(player))
			continue;
		
		else {
			//Ask other players what cards they want to discard
	}
	
}
}

}