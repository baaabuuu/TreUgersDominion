package network;


import java.util.ArrayList;

import org.jspace.ActualField;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

import cards.CardReader;
import log.Log;


public class Lounge {
	//Setup
	private static Lobby[] gamesRunning = new Lobby[1000];
	private static int indexID;
	
	//Setup the uri
	private static int port = 8181;
	private static String host = "localhost";
	private static String uri = "tcp://"+ host + ":" + port + "/?keep";
	
	public static void lobby(CardReader cardReader) throws InterruptedException{
		
		
		indexID = 0;
		//Setup the Repository
		SpaceRepository repository = new SpaceRepository();
		repository.addGate(uri);
		
		//Setup the lobby
		Space lobby = new SequentialSpace();
		repository.add("lobby", lobby);
		
		//Setting up value holders
		String name = "";
		ClientCommand cmd 	= "";
		String tempURI = "";
		int gameID = 0;
		
		
		
		
		//Wait for command
		while(true){
			//Reads command
			lobby.get(new ActualField(name),new ActualField(cmd));
			
			//Obeys command
			switch(cmd){
			case "enter":		
				lobby.get(new ActualField(name), new ActualField(gameID));
				
				
				if(indexID < gameID){
					lobby.put("roomURI", name, gameID, gamesRunning[gameID].getURI());
				} else {
					lobby.put(new ActualField("roomURI"),new ActualField(name),new ActualField(gameID),new ActualField("gameNotFoundException"));
				}
			case "create":
				if(indexID < 1000){
					tempURI = "tcp://"+ host + ":" + port + "/" + indexID +"?keep";
					Lobby tempInit = new Lobby(tempURI, cardReader);
					gamesRunning[indexID] = tempInit;
					lobby.put(new ActualField("roomURI"),new ActualField(name),new ActualField(indexID),new ActualField(tempURI));
					indexID++;
				} else {
					lobby.put(new ActualField("roomURI"),new ActualField(name),new ActualField(1000),new ActualField("toManyGamesException"));
				}
			default:
				
			}
		}
	
	
	}
 
		
}
