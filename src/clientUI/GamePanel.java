package clientUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.metal.MetalScrollBarUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;

import cards.Card;
import log.Log;

public class GamePanel extends JPanel implements ActionListener, KeyListener, ListSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8457104452489324773L;
	private JButton actionSend, chatSend;
	private JScrollBar sbEvent, sbChat, sbItem, sbList;
	private DefaultCaret eventCaret, chatCaret;
	private JScrollPane eventAreaScroll, chatAreaScroll, itemScroll, listScroll;
	public JLabel lblEvent, lblChat, lblRemainingWordsChat, lblRemainingWordsAction;
	public JLabel lblP1, lblP2, lblP3, lblP4;
	public JLabel lblUsername, lblP1VP, lblP2VP, lblP3VP, lblP4VP;
	public JLabel lblActions, lblBuys, lblMoney, lblList;
	public JLabel lblP1Hand, lblP2Hand, lblP3Hand, lblP4Hand, lblDiscardCount, lblDeck, lblTrash;
	public JTextArea eventArea, actionArea, chatArea, chatTypArea, itemArea, backBlack, myTextArea, myTextArea_1, myTextArea_2;
	public JList<String> itemList;
	public DefaultListModel<String> listModel;
	private DefaultStyledDocument docAction, docChat;
	
	private UIController controller;
	
	
	/**
	 * Create the panel.
	 * @param mainFrame 
	 */
	public GamePanel(UIController controller) {
			this.controller = controller;
			setLayout(null);
			setOpaque(false);
			
			// A document filter/listener that ensures that too many
			// characters cannot enter the text field through either
			// typing or copy-pasting.
			docAction = new DefaultStyledDocument();
			docAction.setDocumentFilter(new DocumentSizeFilter(255));
			docAction.addDocumentListener(new DocumentListener(){
				public void changedUpdate(DocumentEvent e) {updateCount(1);}
				public void insertUpdate(DocumentEvent e) {updateCount(1);}
				public void removeUpdate(DocumentEvent e) {updateCount(1);}
	        });
			docChat = new DefaultStyledDocument();
			docChat.setDocumentFilter(new DocumentSizeFilter(255));
			docChat.addDocumentListener(new DocumentListener(){
				public void changedUpdate(DocumentEvent e) {updateCount(2);}
				public void insertUpdate(DocumentEvent e) {updateCount(2);}
				public void removeUpdate(DocumentEvent e) {updateCount(2);}
	        });
			
			eventArea = new JTextArea("Welcome to Dominion: The blatent ripoff!");
			eventArea.setFont(new Font("Bookman Old Style", Font.PLAIN, 14));
			eventArea.setForeground(Color.white);
			eventArea.setOpaque(false);
			eventArea.setToolTipText("Read me!");
			eventArea.setLineWrap(true);
			eventArea.setWrapStyleWord(true);
			eventArea.setEditable(false);
			eventCaret = (DefaultCaret)eventArea.getCaret();
			eventCaret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM);
			
			eventAreaScroll = new JScrollPane(eventArea);
			eventAreaScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			eventAreaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			eventAreaScroll.setOpaque(false);
			eventAreaScroll.getViewport().setOpaque(false);
			eventAreaScroll.setBounds(10, 50, 600, 440);
			sbEvent = eventAreaScroll.getVerticalScrollBar();
	        sbEvent.setUI(new MyScrollbarUI());
	        
	        lblTrash = new JLabel("Trashed count: ");
	        lblTrash.setForeground(Color.WHITE);
	        lblTrash.setBounds(730, 156, 140, 14);
	        add(lblTrash);
	        add(eventAreaScroll);
			
			lblDiscardCount = new JLabel("Discard count: ");
			lblDiscardCount.setForeground(Color.WHITE);
			lblDiscardCount.setBounds(730, 196, 140, 14);
			add(lblDiscardCount);
			
			lblDeck = new JLabel("Deck count: ");
			lblDeck.setForeground(Color.WHITE);
			lblDeck.setBounds(730, 176, 140, 14);
			add(lblDeck);
			
			chatArea = new MyTextArea("Welcome to the chat!");
			chatArea.setFont(new Font("Bookman Old Style", Font.PLAIN, 14));
			chatArea.setForeground(Color.white);
			chatArea.setOpaque(false);
			chatArea.setToolTipText("Read me!");
			chatArea.setLineWrap(true);
			chatArea.setWrapStyleWord(true);
			chatArea.setEditable(false);
			chatCaret = (DefaultCaret)chatArea.getCaret();
			chatCaret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM);
			
			chatAreaScroll = new JScrollPane(chatArea);
			chatAreaScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			chatAreaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			chatAreaScroll.setOpaque(false);
			chatAreaScroll.getViewport().setOpaque(false);
			chatAreaScroll.setBounds(880, 51, 385, 440);
			chatAreaScroll.setBackground(new Color(219, 142, 27));
			sbChat = chatAreaScroll.getVerticalScrollBar();
	        sbChat.setUI(new MyScrollbarUI());
			//add(chatAreaScroll);
			
			itemArea = new MyTextArea("Card discription area.");
			itemArea.setFont(new Font("Bookman Old Style", Font.PLAIN, 11));
			itemArea.setForeground(Color.white);
			itemArea.setOpaque(false);
			itemArea.setLineWrap(true);
			itemArea.setWrapStyleWord(true);
			itemArea.setEditable(false);
			
			itemScroll = new JScrollPane(itemArea);
			itemScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			itemScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			itemScroll.setOpaque(false);
			itemScroll.getViewport().setOpaque(false);
			itemScroll.setBounds(620, 221, 250, 150);
			itemScroll.setBackground(new Color(219, 142, 27));
			sbItem = itemScroll.getVerticalScrollBar();
			sbItem.setUI(new MyScrollbarUI());
			add(itemScroll);
			
			listModel = new DefaultListModel<String>();
			
			itemList = new JList<String>(listModel);
			itemList.setForeground(Color.white);
			itemList.setBackground(Color.darkGray);
			itemList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			itemList.setVisibleRowCount(-1);
			itemList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			itemList.addListSelectionListener(this);
			itemList.addKeyListener(this);
			
			listScroll = new JScrollPane(itemList);
			listScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			listScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			listScroll.setOpaque(false);
			listScroll.getViewport().setOpaque(false);
			listScroll.setBounds(620, 395, 250, 180);
			listScroll.setBackground(new Color(219, 142, 27));
			sbList = listScroll.getVerticalScrollBar();
			sbList.setUI(new MyScrollbarUI());
			add(listScroll);

			actionArea = new MyTextArea("");
			actionArea.setForeground(Color.white);
			actionArea.setOpaque(false);
			actionArea.setLineWrap(true);
			actionArea.setWrapStyleWord(true);
			actionArea.setToolTipText("Type me!");
			actionArea.addKeyListener(this);
			actionArea.setBounds(10, 500, 500, 75);
			actionArea.setEditable(false);
			actionArea.setDocument(docAction);
			add(actionArea);
			
			chatTypArea = new MyTextArea("");
			chatTypArea.setForeground(Color.white);
			chatTypArea.setOpaque(false);
			chatTypArea.setLineWrap(true);
			chatTypArea.setWrapStyleWord(true);
			chatTypArea.setToolTipText("Type me!");
			chatTypArea.addKeyListener(this);
			chatTypArea.setBounds(880, 500, 285, 75);
			chatTypArea.setDocument(docChat);
			//add(chatTypArea);
			
			// Buttons.
			actionSend = new JButton("Send");
			actionSend.setForeground(Color.white);
			actionSend.setFocusPainted(false);
			
			actionSend.setBackground(new Color(219, 142, 27));
			actionSend.setFont(new Font("Tahoma", Font.BOLD, 12));
			actionSend.setBounds(510, 500, 100, 75);
			actionSend.addActionListener(new ActionListener() {	// Adds actionListener containing the following...
	            public void actionPerformed(ActionEvent e) {
	            	sendFromActionArea();
	            }
	        });
			add(actionSend);
			
			chatSend = new JButton("Send");
			chatSend.setForeground(Color.white);
			chatSend.setFocusPainted(false);
			chatSend.addActionListener(this);
			chatSend.setBackground(new Color(219, 142, 27));
			chatSend.setFont(new Font("Tahoma", Font.BOLD, 12));
			chatSend.setBounds(1164, 500, 100, 75);
			//add(chatSend);
			
			// label counters.
			lblRemainingWordsAction = new JLabel("Action");
			lblRemainingWordsAction.setForeground(Color.white);
			lblRemainingWordsAction.setBounds(10, 585, 200, 14);
			add(lblRemainingWordsAction);
			
			lblRemainingWordsChat = new JLabel("Chat");
			lblRemainingWordsChat.setForeground(Color.white);
			lblRemainingWordsChat.setBounds(800, 585, 200, 14);
			//add(lblRemainingWordsChat);
			
			// Middle Area Labels.
			lblP1 = new JLabel("P1: ");
			lblP1.setForeground(Color.white);
			lblP1.setBounds(620, 76, 100, 14);
			add(lblP1);
			
			lblP2 = new JLabel("P2: ");
			lblP2.setForeground(Color.white);
			lblP2.setBounds(620, 96, 100, 14);
			add(lblP2);
			
			lblP3 = new JLabel("P3: ");
			lblP3.setForeground(Color.white);
			lblP3.setBounds(620, 116, 100, 14);
			add(lblP3);
			
			lblP4 = new JLabel("P4: ");
			lblP4.setForeground(Color.white);
			lblP4.setBounds(620, 136, 100, 14);
			add(lblP4);
			
			lblActions = new JLabel("Actions: ");
			lblActions.setForeground(Color.white);
			lblActions.setBounds(620, 156, 85, 14);
			add(lblActions);
			
			lblBuys = new JLabel("Buys: ");
			lblBuys.setForeground(Color.white);
			lblBuys.setBounds(620, 176, 85, 14);
			add(lblBuys);
			
			lblMoney = new JLabel("Money: ");
			lblMoney.setForeground(Color.white);
			lblMoney.setBounds(620, 196, 85, 14);
			add(lblMoney);
			
			lblP1VP = new JLabel("VP: ");
			lblP1VP.setForeground(Color.white);
			lblP1VP.setBounds(825, 76, 45, 14);
			add(lblP1VP);
			
			lblP2VP = new JLabel("VP: ");
			lblP2VP.setForeground(Color.WHITE);
			lblP2VP.setBounds(825, 96, 45, 14);
			add(lblP2VP);
			
			lblP3VP = new JLabel("VP: ");
			lblP3VP.setForeground(Color.WHITE);
			lblP3VP.setBounds(825, 116, 45, 14);
			add(lblP3VP);
			
			lblP4VP = new JLabel("VP: ");
			lblP4VP.setForeground(Color.WHITE);
			lblP4VP.setBounds(825, 136, 45, 14);
			add(lblP4VP);
			
			lblList = new JLabel("Select card to get description.");
			lblList.setForeground(Color.WHITE);
			lblList.setBounds(620, 376, 230, 14);
			add(lblList);
			
			lblUsername = new JLabel("Username: ");
			lblUsername.setForeground(Color.WHITE);
			lblUsername.setBounds(620, 56, 120, 14);
			add(lblUsername);
			
			lblP1Hand = new JLabel("Card Count: ");
			lblP1Hand.setForeground(Color.WHITE);
			lblP1Hand.setBounds(730, 76, 85, 14);
			add(lblP1Hand);
			
			lblP2Hand = new JLabel("Card Count: ");
			lblP2Hand.setForeground(Color.WHITE);
			lblP2Hand.setBounds(730, 96, 85, 14);
			add(lblP2Hand);
			
			lblP3Hand = new JLabel("Card Count: ");
			lblP3Hand.setForeground(Color.WHITE);
			lblP3Hand.setBounds(730, 116, 85, 14);
			add(lblP3Hand);
			
			lblP4Hand = new JLabel("Card Count: ");
			lblP4Hand.setForeground(Color.WHITE);
			lblP4Hand.setBounds(730, 136, 85, 14);
			add(lblP4Hand);
			
			backBlack = new MyTextArea("");
			backBlack.setEditable(false);
			backBlack.setForeground(Color.white);
			backBlack.setOpaque(false);
			backBlack.setBounds(620, 51, 250, 159);
			add(backBlack);
			
			myTextArea = new MyTextArea("");
			myTextArea.setOpaque(false);
			myTextArea.setForeground(Color.WHITE);
			myTextArea.setEditable(false);
			myTextArea.setBounds(620, 376, 250, 14);
			add(myTextArea);
			
			myTextArea_1 = new MyTextArea("");
			myTextArea_1.setOpaque(false);
			myTextArea_1.setForeground(Color.WHITE);
			myTextArea_1.setEditable(false);
			myTextArea_1.setBounds(10, 50, 600, 440);
			add(myTextArea_1);
			
			myTextArea_2 = new MyTextArea("");
			myTextArea_2.setOpaque(false);
			myTextArea_2.setForeground(Color.WHITE);
			myTextArea_2.setEditable(false);
			myTextArea_2.setBounds(620, 401, 250, 174);
			add(myTextArea_2);
			
			//The kill server button got out-dated by an update to jSpace
			JButton btnKillServer = new JButton("KILL SERVER!");
			btnKillServer.setBackground(Color.RED);
			btnKillServer.setFocusPainted(false);
			btnKillServer.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					controller.killServer();
				}
			});
			btnKillServer.setBounds(620, 586, 170, 51);
			//add(btnKillServer);
			
			// Update remainingWords labels.
			updateCount(1);
			//updateCount(2);
	}
	private void updateCount(int i) {
		if(i == 1){
			lblRemainingWordsAction.setText((255 -docAction.getLength()) + " characters remaining");
		}else if(i == 2){
			//lblRemainingWordsChat.setText((255 -docChat.getLength()) + " characters remaining");
		}
    }
	public void updBuyList(Card[] cards, Integer[] integers){
		listModel.clear();
		for(int i = 0; i < cards.length; i++) {
			//addElement is very weird and will throw nullpointerExceptions for literally no reason.
			try {
				listModel.addElement((i+1) + ". " + cards[i].getName() + ": " + integers[i] + "  ");
			}catch(NullPointerException e0){
				Log.important("addElement made a NullPointerException");
			}
		}
	}
	private String eventAreaInfo;
	/**
	 * Update event area, by adding input text to the current text.
	 */
	public void updEventArea(String text){
		if(eventArea.getLineCount() > 39){
			try {
				eventArea.replaceRange("", 0, eventArea.getLineEndOffset(0));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		eventAreaInfo = eventArea.getText();
		eventAreaInfo += "\n" + text;
		
		eventArea.setText(eventAreaInfo);
	}
	public void sendFromActionArea() {
		//Message must be more than 0 and less than 4 characters.
		if(actionAreaTemp != null && actionAreaTemp.length() > 0 && actionAreaTemp.length() < 4){
			actionArea.setEditable(false);
			Log.log("Sent to eventOutput: " + actionAreaTemp);
			controller.eventOutput(actionAreaTemp);
			
			actionAreaTemp = "";
		}
		actionArea.setText("");
	}
	public void keyTyped(KeyEvent e) {}
	private String actionAreaTemp;
	private String chatTypAreaTemp;
	
	public void keyPressed(KeyEvent e) {
		//On ENTER pressed in actionArea, create temporary string without newline
		//and update actionArea without newline.
		if(e.getSource() == actionArea && e.getKeyCode() == KeyEvent.VK_ENTER){
			actionAreaTemp = actionArea.getText().replace("\n","");
			actionArea.setText(actionAreaTemp);
		}else if(e.getSource() == chatTypArea && e.getKeyCode() == KeyEvent.VK_ENTER){
			chatTypAreaTemp = chatTypArea.getText().replace("\n","");
			chatTypArea.setText(chatTypAreaTemp);
		}
	}
	public void keyReleased(KeyEvent e) {
		//On ENTER release from actionArea, update eventArea with temporary
		//text and reset actionArea.
		if(e.getSource() == actionArea && e.getKeyCode() == KeyEvent.VK_ENTER){
			sendFromActionArea();
		}else if(e.getSource() == chatTypArea && e.getKeyCode() == KeyEvent.VK_ENTER){
			if(chatTypAreaTemp.length() > 0){
				//Implement Chat
			}
			chatTypArea.setText("");
		}
		if(e.getSource() == itemList && e.getKeyCode() == KeyEvent.VK_ENTER){
			
		}
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == chatSend){
			if(chatTypAreaTemp != null && chatTypAreaTemp.length() > 0){
				//Implement Chat
			}
			chatTypArea.setText("");
		}
	}
	public void valueChanged(ListSelectionEvent arg0) {
		if(controller.getBuyArea() != null && itemList.getSelectedIndex() != -1){
			String displayTypes = "";
			for(String s : controller.getBuyArea()[itemList.getSelectedIndex()].getDisplayTypes()) {
				displayTypes += s + " - ";
			}
			displayTypes = displayTypes.replaceAll(" - $", "");
			itemArea.setText(controller.getBuyArea()[itemList.getSelectedIndex()].getName() + "   " +
					"Cost: " + controller.getBuyArea()[itemList.getSelectedIndex()].getCost() + "\n\n" +
					"To buy, type '" + (itemList.getSelectedIndex()+1) + "'\n" +
					"Effect:\n" + controller.getBuyArea()[itemList.getSelectedIndex()].getDesc() + "\n\n" +
					"Types: " + displayTypes
					);
			itemArea.setCaretPosition(0);
		}
	}
}
//Modified JTextarea that adds a background.
class MyTextArea extends JTextArea {
  /**
	 * 
	 */
	private static final long serialVersionUID = 7052434801330245652L;
private Image img;
  public MyTextArea(String text) {
  	super(text);
      img = new ImageIcon(MainFrame.class.getResource("TransparentBlack.png")).getImage();
  }
  protected void paintComponent(Graphics g) {
      g.drawImage(img,0,0,null);
      super.paintComponent(g);
  }
}
class MyScrollbarUI extends MetalScrollBarUI {
    private Image imageThumb, imageTrack;
    private JButton b = new JButton() {
        /**
		 * 
		 */
		private static final long serialVersionUID = -7469001016279802969L;

		@Override
        public Dimension getPreferredSize() {
            return new Dimension(0, 0);
        }
    };
    MyScrollbarUI() {
        imageThumb = FauxImage.create(32, 32, new Color(219, 142, 27));
        imageTrack = FauxImage.create(32, 32, Color.darkGray);
    }
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        g.setColor(Color.blue);
        ((Graphics2D) g).drawImage(imageThumb,
            r.x, r.y, r.width, r.height, null);
    }
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        ((Graphics2D) g).drawImage(imageTrack,
            r.x, r.y, r.width, r.height, null);
    }
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return b;
    }
    @Override
    protected JButton createIncreaseButton(int orientation) {
        return b;
    }
}
class FauxImage {
    static public Image create(int w, int h, Color c) {
        BufferedImage bi = new BufferedImage(
            w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setPaint(c);
        g2d.fillRect(0, 0, w, h);
        g2d.dispose();
        return bi;
    }
}
