package objects;

import java.util.HashMap;

public class LoungeObject {
	private HashMap<Integer, Integer> games = new HashMap<Integer, Integer>();

	public LoungeObject(){
		
	}
	
	public HashMap<Integer, Integer> getGames(){
		return games;
	}
	public void setGames(HashMap<Integer, Integer> games){
		this.games = games;
	}
}
