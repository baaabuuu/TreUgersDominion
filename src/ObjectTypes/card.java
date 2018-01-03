package ObjectTypes;

import java.util.ArrayList;

public class card {

	
	private ArrayList<String> types = new ArrayList<String>();
	private ArrayList<String> dispTypes = new ArrayList<String>();
	private String name, description;
	private int cost, money, effectCode, victoryPoints;
	private int costMod, minCost = 0;
	
	/**
	 *  Returns a list of game types - not display types
	 *  Used for things like card with a normal effect and a display effect
	 * @return types
	 */
	public ArrayList<String> getTypes()
	{
		return types;
	}
	
	/**
	 * Return a list of display types - action, treasure, reaction, curse card.
	 * @return
	 */
	public ArrayList<String> getDisplayTypes()
	{
		return dispTypes;
	}
	
	/**
	 * Returns the description of a card - card text essentially
	 * @return
	 */
	public String getDesc()
	{
		return description;
	}
	
	/**
	 * Returns the effect code - the "effect" that occurs when a card is played.
	 * @return
	 */
	public int getEffectCode()
	{
		return effectCode;
	}
	
	/**
	 * Returns the money the card generates - USED FOR TREASURE CARDS ONLY.
	 * @return money
	 */
	public int getMoney()
	{
		return money;
	}
	
	/**
	 * Returns how many points a card is worth, curse cards and victory cards only.
	 * @return victoryPoints
	 */
	public int getVP()
	{
		return victoryPoints;
	}
	
	/**
	 * The cost of a card.
	 * @return cost - costMod
	 */
	public int getCost()
	{
		return (cost - costMod < minCost) ? minCost : cost - costMod;
	}
	
	/**
	 * Sets the cost modifier to mod
	 */
	public void setCostMod(int mod)
	{
		costMod = mod;
	}
	
	/**
	 * Returns the cost mod of an item
	 * @return costMod
	 */
	public int getCostMod()
	{
		return costMod;
	}
	
	/**
	 * Gets the name of a card.
	 * @return name
	 */
	public String getName()
	{
		return name;
	}
}
