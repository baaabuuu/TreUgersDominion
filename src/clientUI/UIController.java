package clientUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import cards.Card;
import client.ClientController;

public class UIController {
	private MainFrame mainFrame;
	private GamePanel gamePanel;
	private GameBackground gameBG;
	private ServerPanel serverPanel;
	private ClientController client;
	private UIController controller = this;
	
	private Card[] buyArea;
	
	public UIController(int port, String host, ClientController client) {
		this.client = client;
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
		gamePanel.updBuyList(buyArea);
	}
	public void newPlayers(){
		
	}
	public void newTurnValues(){
		
	}
	public void newBoardState(){
		
	}
	public void newPlayerHand(){
		
	}
	public void chatInput(){
		
	}
	public void chatOutput(){
		
	}
	public void eventInput(){
		
	}
	public void eventOutput(){
		
	}
}
