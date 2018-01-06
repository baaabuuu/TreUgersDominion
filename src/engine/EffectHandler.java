package engine;
import Engine.Player;
import cards.Card;

import java.util.concurrent.LinkedBlockingDeque;

import org.jspace.*;
public class EffectHandler {

/**
 * Creates an instance of the EffectHandler
 */
public EffectHandler() {};
/**
 * 
 * @param n - Specifies the effect code
 * @param player - PLayer that the effect should a apply to
 * @param card - The card that was played.
 */

public void triggerEffect(int n,Player player,Card card) {
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
		case 3: //Implement reveal
			break;
		case 4: browseDiscard1OnTop(player);
			break;
			
		}
	}
private void discardNDrawN(Player player){
	int i=0; //counter to determine draws
	
	//access connected space
	//wait for pspace with identifier
	//if discard, then then discard+increment counter
	//else draw equal to counter
	
}
private void browseDiscard1OnTop(Player player) {
	LinkedBlockingDeque<Card> discardTemp= player.getDiscard();
	for (Card card: discardTemp) {
		//Ui show card
	}
	int select;
	//network either get number or "no"
	//either do nothing or add
	player.addCardDecktop(discardTemp.toArray());
}
}
