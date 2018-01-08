package cards;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class CardReader {

	private static ArrayList<Card> setup = new ArrayList<Card>();
	private static ArrayList<Card> base = new ArrayList<Card>();
	
	/**
	 * Creates a card reader contains setup, base etc.
	 * @throws IOException
	 */
	public CardReader() throws IOException
	{
		Gson gson = new GsonBuilder().create();
		Type listType =  new TypeToken<List<Card>>() {}.getType();
		//Read the file
		String input = getCards("setupCards");		
		setup = gson.fromJson(input, listType);
		input = getCards("baseCards");
		base = gson.fromJson(input, listType);
		
	}
	
	/**
	 * gets the card from the file:
	 * @param filename
	 * @return
	 * @throws IOException
	 */
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
	public ArrayList<Card> getSetup()
	{
		return setup;
	}
	
	/**
	 * Returns the base Game Cards
	 * @return setup
	 */
	public ArrayList<Card> getBase()
	{
		return base;
	}

}
