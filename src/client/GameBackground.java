package client;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

class GameBackground extends JPanel{
	private static final long serialVersionUID = 1L;
	Image bg = new ImageIcon(MainFrame.class.getResource("BackgroundImage.jpg")).getImage();
    public void paintComponent(Graphics g) {
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }
}
