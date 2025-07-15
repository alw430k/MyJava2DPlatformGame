// WormPanel.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* The game's drawing surface. It shows:
 - the moving worm
 - the obstacles (blue boxes)
 - the current average FPS and UPS
 */

import java.awt.Color;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class WormPanel extends GamePanel {

	private static final long serialVersionUID = 1825086766957972644L;

	private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
	private String dir = "";
	private WormChase wcTop;
	private Worm fred; // the worm
	private Obstacles obs; // the obstacles
	public List<Key> keys = new ArrayList<Key>();
	// used at game termination
	
	private int score = 0;
	private Font font;
	private FontMetrics metrics;
	
	public Key up = new Key();
	public Key down = new Key();
	public Key left = new Key();
	public Key right = new Key();
	public Key exit = new Key();
	public Key shift = new Key();
	   
	
	public WormPanel(WormChase wc, long period) {
		super(period);
		wcTop = wc;
		addKeyListener( new KeyAdapter() {
		       public void keyPressed(KeyEvent e)
		       { toggle(e, true);  }
		       public void keyReleased(KeyEvent e) {
		    	   toggle(e, false);
		       }
		       public void keyTyped(KeyEvent e) {
		    	   toggle(e, true);
		       }
		       
		});
	} // end of WormPanel()
	private void toggle(KeyEvent e, boolean pressed) {
	      if (e.getKeyCode() == KeyEvent.VK_W) up.toggle(pressed);
	      if (e.getKeyCode() == KeyEvent.VK_S) down.toggle(pressed);
	      if (e.getKeyCode() == KeyEvent.VK_A) left.toggle(pressed);
	      if (e.getKeyCode() == KeyEvent.VK_D) right.toggle(pressed);
	      if (e.getKeyCode() == KeyEvent.VK_ESCAPE) exit.toggle(pressed);
	      if (e.getKeyCode() == KeyEvent.VK_SHIFT) shift.toggle(pressed);
	      
	      if (!isPaused && !gameOver) {
		    	// move the sprite and ribbons based on the arrow key pressed
	    	  fred.AccXN = left.down;
	    	  fred.AccXP = right.down;
	    	  fred.AccYN = up.down;
	    	  fred.AccYP = down.down; 
	    	  obs.AcXN = right.down;
	  
		    }
	   }
	public void releaseAll() {
	      for (Key key : keys) key.down = false;
	   }
	protected void simpleUpdate() {
		fred.move();
		obs.move();
		//wcTop.setTimeSpent(timeSpentInGame);

	} // end of gameUpdate()
	protected void mousePress(int x, int y)
	// is (x,y) near the head or should an obstacle be added?
	{
		if (!isPaused && !gameOver) {
			
		}
	} // end of testPress()
	public class Key{
		public boolean down;
		public Key() {
			keys.add(this);
			
		}
		public void toggle(boolean pressed) {
			if(pressed!=down) down = pressed;
		}
	}
	
	
	      	
	   
	  // end of processKey()
	

	

	protected void simpleInitialize() {
		// create game components
		obs = new Obstacles();
		fred = new Worm(PWIDTH, PHEIGHT, obs);

		// set up message font
		font = new Font("SansSerif", Font.BOLD, 24);
		metrics = this.getFontMetrics(font);
		
	}

	protected void simpleRender(Graphics dbg) {
		dbg.setColor(Color.blue);
		dbg.setFont(font);

		// report frame count & average FPS and UPS at top left
		// dbg.drawString("Frame Count " + frameCount, 10, 25);
		//dbg.drawString("Average FPS/UPS: " + df.format(averageFPS) + ", "+ df.format(averageUPS), 20, 25); // was (10,55)
		dbg.drawString(dir, 20, 20);
		dbg.setColor(Color.black);
		// draw game elements: the obstacles and the worm
		obs.draw(dbg);
		fred.draw(dbg);
	}

	protected void gameOverMessage(Graphics g)
	// center the game-over message in the panel
	{
		String msg = "Game Over. Your Score: " + score;
		int x = (PWIDTH - metrics.stringWidth(msg)) / 2;
		int y = (PHEIGHT - metrics.getHeight()) / 2;
		g.setColor(Color.red);
		g.setFont(font);
		g.drawString(msg, x, y);
	} // end of gameOverMessage()
} // end of WormPanel class
