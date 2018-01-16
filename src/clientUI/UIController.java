package clientUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.List;

import org.jspace.Space;

import cards.Card;
import cards.CardReader;
import client.ClientController;
import log.Log;
import objects.BoardState;
import objects.TurnValues;

public class UIController implements client.UIControllerInter {
	private MainFrame mainFrame;
	private GamePanel gamePanel;
	private GameBackground gameBG;
	private ServerPanel serverPanel;
	private ClientController client;
	private UIController controller = this;
	
	private Space userSpace;
	private Card[] buyArea;
	private String playerName;
	
	public UIController(int port, String host, ClientController client, Space userSpace) {
		this.client = client;
		this.userSpace = userSpace;
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Create new mainFrame and set its size. 
					mainFrame = new MainFrame();
					mainFrame.setVisible(true);
					mainFrame.setSize(1280,650);
					// Start game background and set layout.
					gameBG = new GameBackground();
					gameBG.setLayout(new BorderLayout());
					// Create new gamePanel.
					gamePanel = new GamePanel(controller);
					// Start server selection and add a reference to main.
					serverPanel = new ServerPanel(controller, port, host);
					// Add server selection to background pane
					gameBG.add(serverPanel, BorderLayout.CENTER);
					// Add background pane to main.
					mainFrame.setContentPane(gameBG);
					
					/*
					CardReader cards = new CardReader();
					Card[] card = {cards.getBase().get(2),cards.getBase().get(6),cards.getBase().get(8),cards.getBase().get(14)};
					newBuyArea(card);
					int[] a = {1,2,3,4};
					gamePanel.updBuyList(buyArea,a);
					*/
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public Card[] getBuyArea() {
		return buyArea;
	}
	public void attemptConnection(String newUri){
		client.attemptConnection(newUri);
	}
	public void connectionError() {
		serverPanel.setError("Host was not found.");
	}
	public void newConnectionError() {
		gameBG.remove(gamePanel);
		gameBG.add(serverPanel, BorderLayout.CENTER);
		serverPanel.setError("Host was not found.");
	}
	public void newBuyArea(Card[] buyArea){
		this.buyArea = buyArea;
	}
	public void newPlayers(String[] names){
		if(names[0] != "") {
			gamePanel.lblP1.setText("P1: " + names[0]);
		}
		if(names[1] != "") {
			gamePanel.lblP2.setText("P2: " + names[1]);
		}
		if(names[2] != "") {
			gamePanel.lblP3.setText("P3: " + names[2]);
		}
		if(names[3] != "") {
			gamePanel.lblP4.setText("P4: " + names[3]);
		}
	}
	public void newTurnValues(TurnValues values){
		gamePanel.lblActions.setText("Actions: " + values.getAction());
		gamePanel.lblActions.setText("buys: " + values.getBuy());
		gamePanel.lblActions.setText("Money in play: " + values.getMoney());
	}
	public void newBoardState(BoardState input){
		
		gamePanel.updBuyList(buyArea,input.getShopArea());
	}
	public void newPlayerHand(List<Card> playerHand){
		for(int i = 0; i < playerHand.size(); i++){
			eventInput("Card " + (i+1) + ": " + playerHand.get(i).getName());
		}
	}
	public void chatInput(){
		//Implement chat
	}
	public void chatOutput(){
		//Implement chat
	}
	public void awaitingUserInput() {
		gamePanel.actionArea.setEditable(true);
	}
	public void eventInput(String input){
		gamePanel.updEventArea(input);
	}
	public void eventOutput(String input){
		try {
			userSpace.put("client","eventOutput",input);
		} catch (InterruptedException e) {
			Log.important("InterruptedException");
		}
	}
	public void setUserName(String name) {
		this.playerName = name;
	}
}
