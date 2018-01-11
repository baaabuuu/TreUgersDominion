package ai;

import objects.BoardState;
import objects.Commands;

public class BigMoney extends AI
{	
	String aiType = "BigMoney";
	
	public void runTurn()
	{
		nextPhase();
		int money = getMoney();
		int silverCount = 0;
		int goldCount = 0;
		int provinceCount = stateGetCount("Province");
		int low = 0;
		int count;
		for (int i = 0; i < supply.length; i++)
		{
			if (supply[i].getName() != "Province")
			{
				count = stateGetCount(supply[i].getName());
				if (count >= 0 && count < 2)
					low++;
			}
		}
		//If money is 2 buy nothing unless game is near end then buy Estate
		if (money == 2)
		{
			if (provinceCount <= 3 || low >= 2)
			{
				buy("Estate");
			}
		}
		//If money is 3 or 4 buy a silver unless game is near end then buy Estate
		if (money == 3 || money == 4)
		{
			if (provinceCount <= 2 || low >= 2)
			{
				buy("Estate");
			}
			else
			{
				buy("Silver");
			}
		}
		//If 5 money, buy silver unless game is near end then buy Duchy
		if (money == 5)
		{
			if (provinceCount <= 5 || low >= 2)
			{
				buy("Duchy");
			}
			else
			{
				buy("Silver");
			}
		}
		//If money is 6 or 7 buy a gold unless game is near end then buy Duchy
		if (money == 6 || money == 7)
		{
			if (provinceCount <= 2 || low >= 2)
			{
				buy("Duchy");
			}
			else
			{
				buy("Gold");
			}
		}
		if (money >= 8)
		{
			if (provinceCount > 4 || low < 2)
			{
				//Check money density
				
			}
			buy("Province");
		}
		nextPhase();
		
	}
}
