package cards;

public class Card {

	private String[] types;
	private String[] dispTypes;
	private int[] effectCodes;
	private String name, description, set;
	private int cost, money, victoryPoints;
	private int minCost, costMod, typeCount, dispTypeCount, effectCodeCount = 0;
	
	/**
	 * Due to the fact that if the same object is placed into a data structure is a reference to the original object and not its own unique object this is used to ensure that no references to other players decks
	 * are added to the others players and each copy "bought" is added to that data structure.
	 * @return copy of original object.
	 */
	public Card copyOf()
	{
		Card copy = new Card();
		copy.types = getTypes();
		copy.dispTypes = getDisplayTypes();
		copy.effectCodes = getEffectCode();
		copy.name = getName();
		copy.description = getDesc();
		copy.set = getSet();
		copy.cost = cost;
		copy.money = getMoney();
		copy.victoryPoints = getVP();
		copy.minCost = minCost;
		copy.costMod = getCostMod();
		copy.typeCount = getTypeCount();
		copy.dispTypeCount = getDisplayCountCount();
		copy.effectCodeCount = getEffectCount();
		return copy;
	}
	
	public Card()
	{
		types = new String[] {"action"};
		dispTypes = new String[] {"Action"};
		effectCodes = new int[] {0};
		typeCount = 1;
		dispTypeCount = 1;
		effectCodeCount = 0;
		name = "testCard";
		description = "testing Purposes - not avail to players";
		set = "base";
		cost = 0;
		money = 0;
		costMod = 0;
		victoryPoints = 0;
		
	}
	
	/**
	 *  Returns a list of game types - not display types
	 *  Used for things like card with a normal effect and a display effect
	 * @return types
	 */
	public String[] getTypes()
	{
		return types;
	}
	
	/**
	 * Return a list of display types - action, treasure, reaction, curse card.
	 * @return
	 */
	public String[] getDisplayTypes()
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
	 * Returns the effect codes - the "effect" that occurs when a card is used depending on the type
	 * @return
	 */
	public int[] getEffectCode()
	{
		return effectCodes;
	}
	
	/**
	 * Returns the effect code count.
	 * @return
	 */
	public int getEffectCount()
	{
		return effectCodeCount;
	}
	
	/**
	 * Returns the type count.
	 * @return
	 */
	public int getTypeCount()
	{
		return typeCount;
	}
	
	/**
	 * Returns the display type count count.
	 * @return
	 */
	public int getDisplayCountCount()
	{
		return dispTypeCount;
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
	/**
	 * Returns the set the card is from.
	 * @return
	 */
	public String getSet()
	{
		return set;
	}
}
