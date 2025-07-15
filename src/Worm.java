
// Worm.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Contains the worm's internal data structure (a circular buffer)
   and code for deciding on the position and compass direction
   of the next worm move.
*/

import java.awt.*;

import java.awt.geom.*;


public class Worm{
  // size and number of dots in a worm
  // compass direction/bearing constants
	private int X=10;
	private int Y=0;
	private int Xoff = 20;
	private int Yoff = 10;
	private int currCompass;  // stores the current compass dir/bearing

  // Stores the increments in each of the compass dirs.
  // An increment is added to the old head position to get the
  // new position.
	Point2D.Double incrs[];
	// probabiliy info for selecting a compass dir.
	private static final int NUM_PROBS = 9;
	private int probsForOffset[];
	public boolean InAir = true;
	public boolean OnGround = false;
	public boolean doublejumpavailable = true;
	public boolean doublejumpused = false;
	public double xvel = 0;
	public double Truexvel=0;
	public double yvel = 0;
	public double accVal = 0.1;
	public double grav = 0.3;
	public boolean xMoving = false;
	public boolean AccYP = false;
	public boolean AccYN = false;
	public boolean AccXP = false;
	public boolean AccXN = false;
	public boolean Accgrav = true;
	public boolean jumpReleased = false;
	public boolean onWall = false;
	public boolean onLeftWall = false;
	public boolean onRightWall = false;
	public boolean onEdge = false;
	
	// cells[] stores the dots making up the worm
	// it is treated like a circular buffer
	public boolean inBlock = false;
	private Point cells[];
	private int nPoints;
	private int tailPosn, headPosn;   // the tail and head of the buffer
	private int pWidth, pHeight;   // panel dimensions
	private long startTime;        // in ms
	private Obstacles obs;
	
	public Worm(int pW, int pH, Obstacles os)
	{
		obs = os;
	
	} // end of Worm()
	
	public void move() {
		Xoff+=xvel;
		Yoff+=yvel;
		X+=xvel;
		Y+=yvel;
		if(X<=0) {
			if(xvel<0) {
				xvel = 0;
				Truexvel=0;
			}
		}
		if(X>=400) {
			onEdge = true;
			if(xvel>0) {
				xvel = 0;
			}
		}
		if(X<400&&X>0){
			onEdge = false;
		}
		
		if(Accgrav) {
			yvel+=grav;
		}
		if(AccYP) {
			if(InAir) {
				yvel+=(3*accVal);
			}
		}
		if(AccYN) {
			if(OnGround) {
				yvel=-10;
				//InAir = true;
				OnGround = false;
				doublejumpavailable = false;
				jumpReleased = false;
			}else if(onWall && jumpReleased) {
				yvel=-10;
				if(onLeftWall) {
					xvel=5;
					Truexvel=5;
				}
				if(onRightWall) {
					xvel=-5;
					Truexvel=-5;
				}
				InAir = true;
				OnGround = false;
				onWall = false;
				jumpReleased = false;
				doublejumpavailable = false;
			}else if(!OnGround && !onWall && doublejumpavailable){
				yvel=-10;
				InAir=true;
				OnGround=false;
				
				doublejumpavailable = false;
				doublejumpused = true;
			}
		}
		if(AccXP) {
			if(Math.abs(xvel)<=6) {
				xvel+=accVal;
				Truexvel+=accVal;
				if(xvel<0) {
					xvel+=0.5;
					Truexvel+=0.5;
				}
			}
			if(onWall) {
				xMoving = false;
				if(yvel>0&&InAir) {
					if(AccYP) {
						yvel=3;
					}else {
						yvel=1;
					}
				}
			}
			if(onEdge) {
				obs.AcXN=true;
				obs.xGvel=Truexvel;
			}else {
				obs.AcXN=false;
				xMoving = true;
			}
		}
		if(AccXN) {
			if(Math.abs(xvel)<=6) {
				xvel-=accVal;
				Truexvel-=accVal;
				if(xvel>0) {
					xvel-=0.5;
					Truexvel-=0.5;
				}
			}
			if(onWall) {
				xMoving = false;
				if(yvel>0&&InAir) {
					if(AccYP) {
						yvel=3;
					}else {
						yvel=1;
					}
				}
			}else {
				xMoving = true;
			}
		}
		if(!AccYN&&InAir&&!doublejumpused) {
			doublejumpavailable = true;
		}
		if(!AccXP&&!AccXN) {
			if(Math.abs(xvel)!=0.0) {
				xMoving = true;
			}
			if(OnGround) {
				double tempacc = 0.1;
				if(xvel>0) {
					xvel = Math.abs(Math.abs(xvel)-tempacc);
					Truexvel = Math.abs(Math.abs(Truexvel)-tempacc);
				}else if(xvel<0) {
					xvel = xvel+tempacc;
					Truexvel+=tempacc;
				}else {
					xvel=0;
					Truexvel=0;
					xMoving = false;
				}
			}
		}
		if(!AccYN) {
			jumpReleased =true;
		}
		//System.out.println(obs.AcXN);
	}

	public void draw(Graphics g)
	  // draw a black worm with a red head
	  {
	    
	      g.setColor(Color.black);
	      
	      g.fillRect(X, Y, 10, 10);
	    
	  }  // end of draw()
  
}  // end of Worm class

