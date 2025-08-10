
import javax.swing.*;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotMarioMain extends GameFrame{
	private static int DEFAULT_FPS = 80;
	//private NotMarioPanel mp;
	private JTextField jtfBox;   // displays no.of boxes used
	private JTextField jtfTime;  // displays time spent in game
	private NotMarioMain nmTop;
	private DefinitelyNotMario mario;
	private Obstacles obs;
	private int MWIDTH = 10;
	private int MHEIGHT = 10;
	private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
	private int score = 0;
	private Font font;
	private FontMetrics metrics;
	
	public List<Key> keys = new ArrayList<Key>();
	public Key up = new Key();
	public Key down = new Key();
	public Key left = new Key();
	public Key right = new Key();
	public Key exit = new Key();
	public Key shift = new Key();
	public Key p = new Key();
	public Runnable runnable;
    public Thread thread;
    //public PlayMidi md;
	  
	public NotMarioMain(long period) {
		//super("Not Mario");
		
		
		super(period);
		addKeyListener( new KeyAdapter() {
		       public void keyPressed(KeyEvent e)
		       { keyPress(e, true);  }
		       public void keyReleased(KeyEvent e) {
		    	   keyPress(e, false);
		       }
		       public void keyTyped(KeyEvent e) {
		    	   keyPress(e, true);
		       }
		       
		});
		
		//System.out.println(pWidth);
		//addWindowListener(this);
		//pack();
		//setResizable(false);
		//setVisible(true);
		
		
	}
	public void makeGUI(long period) {
		Container c = getContentPane();
		
		//c.add(mp, "Center");
		
		JPanel ctrls = new JPanel();
		ctrls.setLayout(new BoxLayout(ctrls, BoxLayout.X_AXIS));
		
		 jtfBox = new JTextField("Boxes used: 0");
		 jtfBox.setEditable(false);
		 ctrls.add(jtfBox);
		 
		 jtfTime = new JTextField("Time Spent: 0 secs");
		 jtfTime.setEditable(false);
		 ctrls.add(jtfTime);
		
		c.add(ctrls, "South");
		
		
	}
	 public void setBoxNumber(int no)
	  {  jtfBox.setText("Boxes used: " + no);  }

	  public void setTimeSpent(long t)
	  {  jtfTime.setText("Time Spent: " + t + " secs"); }
	  
	
	public void windowClosed(WindowEvent e) {
		
	}
	public void windowOpened(WindowEvent e) {
		
	}
	
	public static void main(String args[]) {
		int fps = DEFAULT_FPS;
	    if (args.length != 0)
	      fps = Integer.parseInt(args[0]);

	    long period = (long) 1000.0/fps;
	    //System.out.println("fps: " + fps + "; period: " + period + " ms");
		new NotMarioMain((long)period*1000000L);
	}
	
	protected void simpleRender(Graphics dbg) {
		dbg.setColor(Color.blue);
		dbg.setFont(font);
		dbg.drawString("Average FPS/UPS: " + df.format(averageFPS) + ", "
				+ df.format(averageUPS), 20, 25); // was (10,55)
		dbg.drawString("Blocks Touched: " + obs.scoreTouched + ", Distance: "
				+ obs.scoreDist,20, 50); // was (10,55)
		dbg.setColor(Color.black);
		
		mario.draw(dbg);
		obs.draw(dbg);
	}
	
	protected void gameOverMessage(Graphics g) {
		String msg = "Game Over";
		String msg2 = "Score: " + score;
		Font tempfont1 = new Font ("Helvetica", Font.BOLD, 300);
		
		Font tempfont2 = new Font ("Helvetica", Font.BOLD, 100);
		FontMetrics tempmetrics1 = getFontMetrics(tempfont1);
		FontMetrics tempmetrics2 = getFontMetrics(tempfont2);
		int x1 = (pWidth - tempmetrics1.stringWidth(msg)) / 2;
		int y1 = (pHeight - tempmetrics1.getHeight()) / 2;
		int x2 = (pWidth - tempmetrics2.stringWidth(msg2)) / 2;
		int y2 = (pHeight - tempmetrics2.getHeight()) / 2+10;
		g.setColor(Color.black);
		g.setFont(tempfont1);
		g.drawString(msg, x1, y1);
		g.setFont(tempfont2);
		g.drawString(msg2, x2, y2);
	}
	protected void simpleUpdate() {
		
		obs.move();
		mario.move();
		if(mario.GAMEISOVER) {
			gameOver = true;
			score = obs.scoreDist*obs.scoreTouched;
			
		}
		//nmTop.setTimeSpent(timeSpentInGame);
	}
	@Override
	protected void mousePress(int x, int y) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void mouseMove(int x, int y) {
		// TODO Auto-generated method stub
		
	}
	
	protected void simpleInitialize() {
		obs = new Obstacles(pWidth, pHeight);
		MWIDTH = 10;
		MHEIGHT = 10;
		mario = new DefinitelyNotMario(MWIDTH,MHEIGHT,obs,pWidth,pHeight);
		
		font = new Font("SansSerif", Font.BOLD, 24);
		metrics = this.getFontMetrics(font);
		
	}
	protected void keyPress(KeyEvent e, boolean pressed) {
	      if (e.getKeyCode() == KeyEvent.VK_W) up.toggle(pressed);
	      if (e.getKeyCode() == KeyEvent.VK_S) down.toggle(pressed);
	      if (e.getKeyCode() == KeyEvent.VK_A) left.toggle(pressed);
	      if (e.getKeyCode() == KeyEvent.VK_D) right.toggle(pressed);
	      if (e.getKeyCode() == KeyEvent.VK_ESCAPE) exit.toggle(pressed);
	      if (e.getKeyCode() == KeyEvent.VK_SHIFT) shift.toggle(pressed);
	      if (e.getKeyCode() == KeyEvent.VK_P) p.toggle(pressed);
	      
	      if (!isPaused && !gameOver) {
		    	// move the sprite and ribbons based on the arrow key pressed
	    	  
	    	  mario.AccXN = left.down;
	    	  mario.AccXP = right.down;
	    	  mario.AccYN = up.down;
	    	  mario.AccYP = down.down; 
	    	  //obs.AcXN = right.down;
	    	  //obs.AcXP = left.down;
	    	  if(p.down) {
	    		  this.pauseGame();
	    	  }
	      }
	      if(!gameOver&&isPaused) {
	    	  if(p.down) {
	    		  this.resumeGame();
	    	  }
	      }
	}
	public class Key{
		public boolean down;
		public Key() {
			keys.add(this);
			
		}
		public void toggle(boolean pressed) {
			if(pressed!=down) down = pressed;
		}
	}
	
	
}




