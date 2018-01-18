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
/**
 * The UIController contains all functions that the client will
 * communicate with. It implements UIControllerInter, which ensures
 * this class contains all functions the client uses.
 */
public class UIController implements client.UIControllerInter {
	private MainFrame mainFrame;
	private GamePanel gamePanel;
	private GameBackground gameBG;
	private ServerPanel serverPanel;
	private ClientController client;
	private UIController controller = this;

	private Space userSpace;
	private Card[] buyArea;

	/**
	 * The constructor of UIController.
	 * @param int port
	 * @param String host
	 * @param ClientController client
	 * @param Space userSpace
	 */
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

					CardReader cards = new CardReader();
					Card[] card = {cards.getBase().get(2),cards.getBase().get(6),cards.getBase().get(8),cards.getBase().get(14)};
					newBuyArea(card);
					Integer[] a = {1,2,3,4};
					gamePanel.updBuyList(buyArea,a);


				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * Gets buyArea Card array.
	 * @return Card[] buyArea
	 */
	public Card[] getBuyArea() {
		return buyArea;
	}
	/**
	 * Client is prompted to make a new connection.
	 * @param String newUri
	 * @param String userName
	 */
	public void attemptConnection(String newUri, String userName){
		setUsername(userName);
		client.setUserName(userName);
		client.attemptConnection(newUri);
	}
	/**
	 * Displays a connection error to the user.
	 */
	public void connectionError() {
		serverPanel.setError("Host was not found.");
	}
	/**
	 * Displays a connection error to the user, when.
	 */
	public void newConnectionError() {
		gameBG.remove(gamePanel);
		gameBG.add(serverPanel, BorderLayout.CENTER);
		mainFrame.setContentPane(gameBG);
		serverPanel.setError("Host was not found.");
	}
	public void newBuyArea(Card[] buyArea){
		this.buyArea = buyArea;
	}
	public void newPlayers(String[] names){
		//Set text in relevant fields depending on player names.
		//Check if the array contains a field before trying to access it.
		if((0 >= 0) && (0 < names.length)) {
			gamePanel.lblP1.setText("P1: " + names[0]);
		}
		if((1 >= 0) && (1 < names.length)) {
			gamePanel.lblP2.setText("P2: " + names[1]);
		}
		if((2 >= 0) && (2 < names.length)) {
			gamePanel.lblP3.setText("P3: " + names[2]);
		}
		if((3 >= 0) && (3 < names.length)) {
			gamePanel.lblP4.setText("P4: " + names[3]);
		}
	}
	public void newTurnValues(TurnValues values){
		gamePanel.lblActions.setText("Actions: " + values.getAction());
		gamePanel.lblBuys.setText("buys: " + values.getBuy());
		gamePanel.lblMoney.setText("Money: " + values.getMoney());
	}
	public void newBoardState(BoardState input){
		gamePanel.lblP1Hand.setText("Card Count: " + input.getHandCount()[0]);
		gamePanel.lblP2Hand.setText("Card Count: " + input.getHandCount()[1]);
		if (input.getHandCount().length > 2)
			gamePanel.lblP3Hand.setText("Card Count: " + input.getHandCount()[2]);
		if (input.getHandCount().length > 3)
			gamePanel.lblP4Hand.setText("Card Count: " + input.getHandCount()[3]);
		
		gamePanel.lblP1VP.setText("VP: " + input.getVpCount()[0]);
		gamePanel.lblP2VP.setText("VP: " + input.getVpCount()[1]);
		if (input.getVpCount().length > 2)
			gamePanel.lblP3VP.setText("VP: " + input.getVpCount()[2]);
		if (input.getVpCount().length > 3)
			gamePanel.lblP4VP.setText("VP: " + input.getVpCount()[3]);
		
		
		gamePanel.updBuyList(buyArea,input.getShopArea());
	}
	public void newPlayerHand(List<Card> playerHand){
		eventInput("Your new hand contains: ");
		String output = "";
		for(int i = 0; i < playerHand.size(); i++){
			output += (i+1) + ". " + playerHand.get(i).getName() + " - ";
		}
		output = output.replaceAll(" - $", "");
		eventInput(output);
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
		Log.log("Adding to eventArea: " + input);
		gamePanel.updEventArea(input);
	}
	public void eventOutput(String input){
		try {
			userSpace.put("client","eventOutput",input);
			Log.log("Sent to userSpace: " + input);
		} catch (InterruptedException e) {
			Log.important("InterruptedException");
		}
	}
	public void setUsername(String username) {
		gamePanel.lblUsername.setText(username);
	}
	public void startGame() {
		gameBG.remove(serverPanel);
		gameBG.add(gamePanel, BorderLayout.CENTER);
		mainFrame.setContentPane(gameBG);
	}
	//The killServer function is not working anymore due to a jSpace update.
	public void killServer() {
		client.killServer();
	}
}
