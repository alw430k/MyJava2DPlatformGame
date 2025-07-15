
// Obstacles.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* A collection of boxes which the worm cannot move over
*/

import java.awt.*;

import java.util.ArrayList;



public class Obstacles
	{
	private static final int BOX_LENGTH = 100;
  	
  	   // arraylist of Rectangle objects
  	
  	public ArrayList<Integer> boundD;
  	public ArrayList<Integer> boundR;
  	public ArrayList<Integer> boxesX;
  	public ArrayList<Integer> boxesY;
  	public ArrayList<Boolean> onBlock;
  	public int GX = 500;
  	public int GY = 300;
  	private int difficulty =0;
  	public double xGvel = 0;
  	public double yGvel = 0;
  	public boolean AcXN = false;
  	public boolean Start = true;
  	public boolean spawn = false;
  	
	public Obstacles()
	  { 	
		  	
	  		boundD = new ArrayList();
	  		boundR = new ArrayList();
	  		boxesX = new ArrayList();
	  		boxesY = new ArrayList();
	  		onBlock = new ArrayList();
	  }
	  synchronized public void add()
	  { 
		 
			int randy = (int) (Math.random()*101)+300;
			boxesX.add(500);
			boxesY.add(randy);
			boundR.add(500+BOX_LENGTH);
			boundD.add(randy+BOX_LENGTH);
			onBlock.add(false);
			
			
	  }
	  synchronized public void move() {
		  if(Start) {
			  	
				boxesX.add(0);
				boxesY.add(350);
				boundR.add(0+BOX_LENGTH);
				boundD.add(350+BOX_LENGTH);
				
				boxesX.add(120);
				boxesY.add(350);
				boundR.add(120+BOX_LENGTH);
				boundD.add(350+BOX_LENGTH);
				
				boxesX.add(280);
				boxesY.add(350);
				boundR.add(280+BOX_LENGTH);
				boundD.add(350+BOX_LENGTH);
				
				boxesX.add(200);
				boxesY.add(250);
				boundR.add(200+BOX_LENGTH);
				boundD.add(250+BOX_LENGTH);
				Start = false;
				
				
				
		  }
		  if(AcXN) {
			  if(!boxesX.isEmpty()) {
				  	for(int i=0; i<boxesX.size(); i++) {
				  		boxesX.set(i,(int) (boxesX.get(i)-xGvel));
				  	}
			  }
			  //System.out.println(GX);
			  
		  }
		  if(!AcXN) {
			  if(xGvel>0) {
				  xGvel +=0.1;
			  }else {
				  xGvel = 0;
			  }
		  }
	  }
	
	  synchronized public void draw(Graphics g)
	  // draw a series of blue boxes
	  {
	    
		  g.setColor(Color.blue);
		  if(!boxesX.isEmpty()) {
			  for(int i=0; i<boxesX.size(); i++) {
				  g.fillRect(boxesX.get(i), boxesY.get(i),BOX_LENGTH, BOX_LENGTH);
				  
			  }
		  }
		  //g.fillRect(GX,GY,BOX_LENGTH, BOX_LENGTH);
		  
	  }  // end of draw()
	
	
	  	synchronized public int getNumObstacles()
	  	{  
		  return GX;
	  	}  

}  // end of Obstacles class
