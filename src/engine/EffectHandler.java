package engine;
import Engine.Player;
import cards.Card;

import org.jspace.*;
public class EffectHandler {

/**
 * Creates an instance of the EffectHandler
 */
public EffectHandler() {};

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
		case 4: browseDiscard1OnTop();
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
private void browseDiscard1OnTop() {
	
}
}
