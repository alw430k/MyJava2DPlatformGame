import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class DefinitelyNotMario {
	// size and number of dots in a worm
	  // compass direction/bearing constants
		private int X = 10;
		private int Y = 10;
		private int befCollCheckX;
		private int befCollCheckY;
		private int TrueX;
		private int TrueY;
		private int befCollCheckTX;
		private int befCollCheckTY;
		private int Xoff;
		private int Yoff;
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
		public boolean DisableXP = false;
		public boolean DisableXN = false;
		public boolean DisableYP = false;
		public boolean DisableYN = false;
		public boolean DisableGrav = false;
		public boolean GAMEISOVER = false;
		public int GAMESCORE = 0;
		public boolean Accgrav = true;
		public boolean jumpReleased = false;
		public boolean onWall = false;
		public boolean onLeftWall = false;
		public boolean onRightWall = false;
		public boolean onEdge = false;
		public boolean onBackEdge = false;
		
		
		private ImagesLoader imsLoader;
		private final static String IMS_FILE = "imsInfo.txt";
		private BufferedImage fighter;
		private ImagesPlayer merioPlayer;
		
		public Point MP;
		public int W;
		public int H;
		public int SW;
		public int SH;
		// cells[] stores the dots making up the worm
		// it is treated like a circular buffer
		public boolean inBlock = false;
		private Point cells[];
		private int nPoints;
		private int tailPosn, headPosn;   // the tail and head of the buffer
		private int pWidth, pHeight;   // panel dimensions
		private long startTime;        // in ms
		private Obstacles obs;
		private int[] cV;
		
		public DefinitelyNotMario(int pW, int pH, Obstacles os, int sw, int sh)
		{
			obs = os;
			MP = new Point(X,Y);
			W = pW;
			H = pH;
			SW = sw;
			SH = sh;
			Xoff = X+pW;
			Yoff = Y+pH;
			TrueX = X;
			TrueY = Y;
			//fighter = imsLoader.getImage("fighter", "left");
			//imsLoader = new ImagesLoader(IMS_FILE);
			//merioRun = imsLoader.getImage("merioRun");
			//System.out.println(H);
		} // end of Worm()
		
		synchronized public void move() {
			//merioRun.updateTick();
			MP.x = X;
			MP.y = Y;
			
			
			
			
			//System.out.println(cV[0]);
			//System.out.println("beforeinputcheck: X: "+ X+", Y: " + Y+", xvel: "+xvel+", yvel: "+yvel);
			if(AccXP&&!DisableXP) {
				if(Math.abs(xvel)<=6) { //speed limit
					xvel+=accVal;
					
					if(xvel<0) { //reverse direction ease
						xvel+=0.5;
						
					}
				}
				if(Math.abs(Truexvel)<=6) { //speed limit
					Truexvel+=accVal;
					
					if(Truexvel<0) { //reverse direction ease
						Truexvel+=0.5;
						
					}
				}
				
				
				if(onWall) {
					xMoving = false;
					if(yvel>0&&!OnGround) {
						if(AccYP) {
							yvel=3;
						}else {
							yvel=1;
						}
					}
				}
				
				
			}
			
			
			if(AccXN&&!DisableXN) {
				if(Math.abs(xvel)<=6) {
					xvel-=accVal;
					if(xvel>0) {
						xvel-=0.5;
					}
				}
				if(Math.abs(Truexvel)<=6) {
					Truexvel-=accVal;
					if(Truexvel>0) {
						Truexvel-=0.5;
					}
				}
				if(onWall) {
					xMoving = false;
					if(yvel>0&&!OnGround) {
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
			if(AccYN&&!DisableYN) {
				if(OnGround) {
					yvel=-10;
					doublejumpavailable = false;
					jumpReleased = false;
				}else if(onWall && jumpReleased) {
					yvel=-10;
					if(onLeftWall) {
						xvel = -5;
					}
					if(onRightWall){
						xvel = 5;
					}
					jumpReleased = false;
					doublejumpavailable = false;
				}else if(!OnGround&&!onWall&&doublejumpavailable) {
					yvel = -10;
					doublejumpavailable = false;
					doublejumpused = true;
					jumpReleased = false;
				}
				
			}
			if(AccYP&&InAir) {
				yvel+=(3*accVal);
			}
			
			//if(!DisableGrav) {
			yvel+=grav;
			//}
			
			befCollCheckX = X;
			befCollCheckY = Y;
			befCollCheckTX = TrueX;
			befCollCheckTY = TrueY;
			
			//System.out.println("beforecoll check: X: "+ X+", Y: " + Y+", xvel: "+xvel+", yvel: "+yvel);
			cV = obs.Collision(MP, W, H, xvel, yvel); //check collision
			
			
			
			if(cV[0]==0) {
				//System.out.println("not hitting anything");
				OnGround = false;
				InAir = true;
				onWall = false;
				onLeftWall = false;
				onRightWall = false;
				DisableGrav = false;
			}
			
			if((cV[0])==1||(cV[0])==5) { //collides with floor
				//System.out.println("hits floor");
				if(yvel>0) {
					InAir = false;
					OnGround = true;
					DisableGrav = true;
					doublejumpavailable = true;
					doublejumpused = false;
					onWall=false;
					onLeftWall=false;
					onRightWall=false;
					yvel=0;
					Y = cV[1];
				}
			}else {
				DisableGrav = false;
			}
			if(cV[2]==2||(cV[2])==6) {
				//System.out.println("hitting LWall");
				if(xvel>0) {
					InAir = false;
					onWall = true;
					onLeftWall = true;
					//DisableXP = true;
					xvel=-1;
					Truexvel = -1;
					X = cV[3];
					TrueX = befCollCheckTX + (X-befCollCheckX);
				}
			}else {
				//DisableXP = false;
			}
			if(cV[4]==3||(cV[4])==7) {
				//System.out.println("hitting RWall");
				if(xvel<0) {
					InAir = false;
					onWall = true;
					onRightWall = true;
					//DisableXN = true;
					xvel=0;
					Truexvel = 0;
					X = cV[5];
					TrueX = befCollCheckTX + (X-befCollCheckX);
				}
				
			}else {
				//DisableXN = false;
			}
			if(cV[6]==4||(cV[6])==8) {
				if(yvel<0) {
					OnGround = false;
					DisableYN = true;
					yvel=0;
					
					Y = cV[7];
				}
			}else {
				DisableYN = false;
			}
			//System.out.println("xMoving: "+xMoving+", xvel: "+(int)xvel+", Truexvel: "+(int)Truexvel+", onEdge: "+onEdge+", onBackEdge: "+onBackEdge+", TrueX: "+TrueX+", TrueY: "+TrueY+", X: "+X+", Y: "+Y);
			//System.out.println("InAir: "+InAir+", OnGround: "+OnGround+", onLeftWall: "+onLeftWall+", onRightWall: "+onRightWall+", jumpReleased: "+jumpReleased+", onEdge: "+onEdge);
			//System.out.println("jumpReleased: "+jumpReleased+", doublejumpavailable: "+doublejumpavailable+", doublejumpused: "+doublejumpused);
			
			//System.out.println("afcollisionchech: X: "+ X+", Y: " + Y+", xvel: "+xvel+", yvel: "+yvel);
			
			if(X<=0) {
				onBackEdge = true;
				if(xvel<0) {
					xvel = 0;
					
				}
				if(Truexvel<0) {
					
					Truexvel=0;
				}
			}
			if(X>=SW) {
				onEdge = true;
				if(xvel>0) {
					xvel = 0;
				}
			}
			if(X<SW&&X>0){
				onEdge = false;
				onBackEdge = false;
			}
			if(!AccYN&&InAir&&!doublejumpused) {   
				doublejumpavailable = true;
			}
			if(!AccXP&&!AccXN) {   //ground friction
				if(Math.abs(Truexvel)!=-1) {
						xMoving = true;
				}
				if(OnGround) {
					
					double tempacc = 0.2;
					if(xvel>-obs.xGvel) {
						xvel = Math.abs(xvel)-tempacc;
					}else if(xvel<-obs.xGvel) {
						xvel = xvel+tempacc;
					}else {
						xvel=-obs.xGvel;
						
						xMoving = false;
					}
					
					if(Truexvel>0) {
						Truexvel = Math.abs(Math.abs(Truexvel)-tempacc);
					}else if(xvel<0) {
						Truexvel = Truexvel+tempacc;
					}else {
						Truexvel=0;
						xMoving = false;
					}
				}
			}
			if(!AccYN) {
				jumpReleased =true;
			}
			Y+=yvel;
			X+=xvel;
			TrueY+=yvel;
			TrueX+=Truexvel;
			GAMESCORE = obs.scoreDist*obs.scoreTouched;
			if(onEdge) {
				obs.xGvel=Truexvel;
			}else {
				obs.xGvel = 1;
			}
			if(Y>=SH) {
				GAMEISOVER = true;
			}
			
			//System.out.println("  newXYvals     : X: "+ X+", Y: " + Y+", xvel: "+xvel+", yvel: "+yvel);
			//System.out.println(Y+H-1);
			
			
			
		}
		private void drawImage(Graphics g, BufferedImage im, int x, int y) {
			/* Draw the image, or a yellow box with ?? in it if there is no image. */
			if (im == null) {
				// System.out.println("Null image supplied");
				g.setColor(Color.yellow);
				g.fillRect(x, y, 20, 20);
				g.setColor(Color.black);
				g.drawString("??", x + 10, y + 10);
			} else
				g.drawImage(im, x, y, (ImageObserver) this);
		} // end of drawImage()
		synchronized public void draw(Graphics g)
		  // draw a black worm with a red head
		  {
			
		      g.setColor(Color.black);
		      //drawImage(g, merioRun, 350, 250);
		      g.fillRect(X, Y, 10, 10);
		    
		  }  // end of draw()
	
}
