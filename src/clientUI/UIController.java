package clientUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import org.jspace.Space;

import cards.Card;
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
					gameBG.add(gamePanel, BorderLayout.CENTER);
					// Add background pane to main.
					mainFrame.setContentPane(gameBG);
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
	public void newPlayers(){
		
	}
	public void newTurnValues(TurnValues values){
		gamePanel.lblActions.setText("Actions: " + values.getAction());
		gamePanel.lblActions.setText("buys: " + values.getBuy());
		gamePanel.lblActions.setText("Money in play: " + values.getMoney());
	}
	public void newBoardState(BoardState input){
		
		gamePanel.updBuyList(buyArea,input.getShopArea());
	}
	public void newPlayerHand(){
		
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
			userSpace.put("UI",input);
		} catch (InterruptedException e) {
			Log.important("InterruptedException");
		}
	}
}
