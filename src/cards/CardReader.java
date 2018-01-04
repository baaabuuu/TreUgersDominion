package cards;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class CardReader {

	private static ArrayList<Card> setup = new ArrayList<Card>();
	private static ArrayList<Card> base = new ArrayList<Card>();
	
	public static void main(String args[]) throws IOException
	{

		
		//Inits the Card Reader
		CardReader cardReader = new CardReader();
		for (Card card : setup)
		{
			System.out.println(card.getDesc());
		}
		
		for (Card card : base)
		{
			System.out.println(card.getDesc());
		}
		
		
	}
	
	public CardReader() throws IOException
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Type listType =  new TypeToken<List<Card>>() {}.getType();
		//Read the file
		String input = getCards("setupCards");		
		setup = gson.fromJson(input, listType);
		input = getCards("baseCards");		
		base = gson.fromJson(input, listType);
	}
	
	public String getCards(String filename) throws IOException
	{
		String output = "";
		String line = "";
		FileReader fileReader = new FileReader("src\\cards\\" + filename + ".json");
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		while((line = bufferedReader.readLine()) != null) {
			output = output + line +"\n";
		}
		bufferedReader.close();
		return output;
	}
	
	/**
	 * Returns the setup Cards
	 * Estate, Duchy, Province
	 * Copper, Silver, Gold
	 * Curse Card
	 * @return setup
	 */
	public static ArrayList<Card> getSetup()
	{
		return setup;
	}
	
	/**
	 * Returns the base Game Cards
	 * @return setup
	 */
	public static ArrayList<Card> getBase()
	{
		return base;
	}

}
