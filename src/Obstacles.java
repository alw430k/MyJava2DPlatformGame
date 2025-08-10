import java.awt.*;
import java.util.ArrayList;
import java.util.Random;


public class Obstacles{
	private static final int BOX_LENGTH = 40;
	private ArrayList boxes;
	private ArrayList<Integer> boxesTouched;
	public int scoreDist = 0;
	public int scoreTouched = 0;
	private int maxBoxes = 30;
	public boolean AcXN = false;
	public boolean AcXP = false;
	public double accVal = 0.1;
	public ArrayList<Integer> boundD;
  	public ArrayList<Integer> boundR;
  	public ArrayList<Integer> boxesX;
  	public ArrayList<Integer> boxesY;
  	public ArrayList<Boolean> onBlock;
  	public int GX;
  	public int GY;
  	private int difficulty =0;
  	public double xGvel = 0;
  	public double yGvel = 0;
  	public boolean Start = true;
  	public boolean spawn = false;
	
	Random rand = new Random();
	
	public Obstacles() {
		boxes = new ArrayList<Rectangle>();
		boxesTouched =new ArrayList<Integer>();
		boundD = new ArrayList();
  		boundR = new ArrayList();
  		boxesX = new ArrayList();
  		boxesY = new ArrayList();
		GX = 500;
		GY = 400;
		initialGen();
	}
	public Obstacles(int sw, int sh) {
		boxes = new ArrayList<Rectangle>();
		boxesTouched =new ArrayList<Integer>();
		boundD = new ArrayList();
  		boundR = new ArrayList();
  		boxesX = new ArrayList();
  		boxesY = new ArrayList();
		GX = sw;
		GY = sh;
		initialGen();
	}
	synchronized public void initialGen() {
		int space = 0;
		int space2;
		int h;
		int randy;
		for(int i = 0; i<10; i++) {
			addBox(space, GY-100);
			space+=BOX_LENGTH;
		}
		
		h = (GX-space)/(maxBoxes-10);
		
		for(int k = 10; k<maxBoxes; k++) {
			randy = rand.nextInt(GY);
			addBox(space,randy);
			space+=h;
		}
		
	}
	synchronized public void genVert() {
		int newY = 0-BOX_LENGTH;
		int newX = rand.nextInt(GX);
		addBox(newX, newY);
	}
	synchronized public void genHoriz() {
		int newX = GX+BOX_LENGTH;
		int newY = rand.nextInt(GY);
		addBox(newX,newY);
	}
	
	
	synchronized public void addBox(int x, int y) {
		boxes.add(new Rectangle(x,y,BOX_LENGTH, 3*BOX_LENGTH));
		boxesX.add(x);
		boxesTouched.add(0);
		
	}
	
	synchronized public void remove() {
		boxes.remove(0);
	}
	//0 - doesnt hit
	//1 - feet and roof hit
	//2 - front and LWall hit
	//3 - back and RWall hit
	//4 - head and bottom hit
	synchronized public int hits(Point p, int w, int h, double velx, double vely, Rectangle b) {
		
		int hits = 0;
		
		double[][] vs;
		int[] corners = new int[4]; //stores hits status for each corner of player character
		double[] cDists = new double[4];
		double minCDist = 0;
		
		double iTop;
		double iLeft;
		double iRight;
		double iBot;
		double distToTop;
		double distToLeft;
		double distToRight;
		double distToBot;
		double minDist = 0;
		boolean intsTop;
		boolean intsLeft;
		boolean intsRight;
		boolean intsBot;
		boolean intsVectX;
		boolean intsVectY;
		boolean hitsTop;
		boolean hitsLeft;
		boolean hitsRight;
		boolean hitsBot;
		
		int topSide = b.y;               //yvar
		int leftSide = b.x;   //xvar
		int rightSide = b.x+b.width-1;			//xvar
		int bottomSide = b.y+b.height-1; //yvar
		
		//get corner velocity vectors
		vs = vVectors(p,w,h,velx,vely);
		
		//4 player box corners, 4 obs box sides
		for(int i = 0; i<vs.length; i++) {
			
			//assign intersection values
			if(vs[i][6]==1) { //avoid /0 arith error
				iTop = vs[i][0];
				iBot = vs[i][0];
			}else {
				iTop = iX(vs[i][0],vs[i][1],topSide);
				iBot = iX(vs[i][0],vs[i][1],bottomSide);	
			}
			
			iLeft = iY(vs[i][0],vs[i][1],leftSide);
			iRight = iY(vs[i][0],vs[i][1],rightSide);
				
			//System.out.println("i: "+i+", iTop: "+iTop+", iLeft: "+iLeft+", iRight: "+iRight+", iBot: "+iBot);
			//booleans sides that get intersected with 
			intsTop = iTop>=leftSide&&iTop<=rightSide&&topSide>=Math.min(vs[i][3],vs[i][5])&&topSide<=Math.max(vs[i][3],vs[i][5]);
			intsLeft = iLeft>topSide &&iLeft < bottomSide&&leftSide>=Math.min(vs[i][2],vs[i][4])&&leftSide<=Math.max(vs[i][2],vs[i][4]);
			intsRight = iRight>topSide &&iRight<bottomSide&&rightSide>=Math.min(vs[i][2],vs[i][4])&&rightSide<=Math.max(vs[i][2],vs[i][4]);
			intsBot = iBot>=leftSide&&iBot<=rightSide&&bottomSide>=Math.min(vs[i][3],vs[i][5])&&bottomSide<=Math.max(vs[i][3],vs[i][5]);
			//System.out.println("i: "+i+", intsTop: "+intsTop+", intsLeft: "+intsLeft+", intsRight: "+intsRight+", intsBot: "+intsBot);
			
			//distances from corner point to side intersection
			distToTop = distance(vs[i][2],vs[i][3],iTop,topSide);
			distToLeft = distance(vs[i][2],vs[i][3],leftSide,iLeft);
			distToRight = distance(vs[i][2],vs[i][3],rightSide,iRight);
			distToBot = distance(vs[i][2],vs[i][3],iBot,bottomSide);
			//System.out.println("i: "+i+", distToTop: "+distToTop+", distToLeft: "+distToLeft+", distToRight: "+distToRight+", distToBot: "+distToBot);
				
			//if more than one intersection occurs
			if((intsTop?1:0)+(intsLeft?1:0)+(intsRight?1:0)+(intsBot?1:0)>1) {
				if(intsTop&&intsLeft) {
					minDist = Math.min(distToTop,distToLeft);
				}else if(intsTop&&intsRight) {
					minDist = Math.min(distToTop,distToRight);
				}else if(intsTop&&intsBot) {
					minDist = Math.min(distToTop,distToBot);
				}else if(intsLeft&&intsRight) {
					minDist = Math.min(distToLeft,distToRight);
				}else if(intsLeft&&intsBot) {
					minDist = Math.min(distToLeft,distToBot);
				}else if(intsRight&&intsBot){
					minDist = Math.min(distToRight,distToBot);
				}
				
				
				hitsTop = minDist == distToTop;
				hitsLeft = minDist == distToLeft;
				hitsRight = minDist == distToRight;
				hitsBot = minDist == distToBot;
				cDists[i] = minDist;
			//System.out.println("i: "+i+", hitsTop: "+hitsTop+", hitsLeft: "+hitsLeft+", hitsRight: "+hitsRight+", hitsBot: "+hitsBot+", minDist: "+minDist);	
			}else {//for single to no intersections:
				hitsTop = intsTop;
				hitsLeft = intsLeft;
				hitsRight = intsRight;
				hitsBot = intsBot;
			}
			
			//check which side the corner will hit
			if(hitsTop) {
				corners[i] = 1;
				cDists[i] = distToTop;
			}else if(hitsLeft) {
				corners[i] = 2;
				cDists[i] = distToLeft;
			}else if(hitsRight) {
				corners[i] = 3;
				cDists[i] = distToRight;
			}else if(hitsBot) {
				corners[i] = 4;
				cDists[i] = distToBot;
			}else {
				corners[i] = 0;
			}
			if(hitsTop||hitsLeft||hitsRight||hitsBot) {
				//System.out.println("i: "+i+", distToTop: "+distToTop+", distToLeft: "+distToLeft+", distToRight: "+distToRight+", distToBot: "+distToBot);
				//System.out.println("i: "+i+", hitsTop: "+hitsTop+", hitsLeft: "+hitsLeft+", hitsRight: "+hitsRight+", hitsBot: "+hitsBot+", minDist: "+minDist);	
				//System.out.println("i: "+i+", corners[0]: "+corners[0]+", corners[1]: "+corners[1]+", corners[2]: "+corners[2]+", corners[3]: "+corners[3]);
				//System.out.println("i: "+i+", cDists[0]: "+cDists[0]+", cDists[1]: "+cDists[1]+", cDists[2]: "+cDists[2]+", cDists[3]: "+cDists[3]);
				//System.out.println(corners[i]);
			}
			
		}
		
		//check if at least one corner hits
		if(corners[0]!=0||corners[1]!=0||corners[2]!=0||corners[3]!=0) {
			//System.out.println("corners[0]: "+corners[0]+", corners[1]: "+corners[1]+", corners[2]: "+corners[2]+", corners[3]: "+corners[3]);
			//multiple corners hit:
			
			if(((corners[0]!=0)?1:0)+((corners[1]!=0)?1:0)+((corners[2]!=0)?1:0)+((corners[3]!=0)?1:0)==1) {
				//System.out.println("1 corner hit");
				if(corners[0]!=0) {
					minCDist = cDists[0];
				}else if(corners[1]!=0) {
					minCDist = cDists[1];
				}else if(corners[2]!=0) {
					minCDist = cDists[2];
				}else if(corners[3]!=0) {
					minCDist = cDists[3];
				}
				
				
				
				
			}else if(((corners[0]!=0)?1:0)+((corners[1]!=0)?1:0)+((corners[2]!=0)?1:0)+((corners[3]!=0)?1:0)==2) { //if two corners hit
				//System.out.println("2 corners hit");
				if(corners[0]!=0&&corners[1]!=0) {
					minCDist = Math.min(cDists[0],cDists[1]);
				}else if(corners[0]!=0&&corners[2]!=0) {
					minCDist = Math.min(cDists[0],cDists[2]);
				}else if(corners[0]!=0&&corners[3]!=0) {
					minCDist = Math.min(cDists[0],cDists[3]);
				}else if(corners[1]!=0&&corners[2]!=0) {
					minCDist = Math.min(cDists[1],cDists[2]);
				}else if(corners[1]!=0&&corners[3]!=0) {
					minCDist = Math.min(cDists[1],cDists[3]);
				}else if(corners[2]!=0&&corners[3]!=0) {
					minCDist = Math.min(cDists[2],cDists[3]);
					
				}
					
				
			}else if(((corners[0]!=0)?1:0)+((corners[1]!=0)?1:0)+((corners[2]!=0)?1:0)+((corners[3]!=0)?1:0)==3) { //if three corners hit
				//System.out.println("3 corners hit");
				if(corners[0]!=0&&corners[1]!=0&&corners[2]!=0) {
					minCDist = Math.min(Math.min(cDists[0],cDists[1]),cDists[2]);
				}else if(corners[0]!=0&&corners[1]!=0&&corners[3]!=0) {
					minCDist = Math.min(Math.min(cDists[0],cDists[1]),cDists[3]);
				}else if(corners[0]!=0&&corners[2]!=0&&corners[3]!=0) {
					minCDist = Math.min(Math.min(cDists[0],cDists[2]),cDists[3]);
				}else if(corners[1]!=0&&corners[2]!=0&&corners[3]!=0) {
					minCDist = Math.min(Math.min(cDists[1],cDists[2]),cDists[3]);
				}
			}else if(((corners[0]!=0)?1:0)+((corners[1]!=0)?1:0)+((corners[2]!=0)?1:0)+((corners[3]!=0)?1:0)==4) { //if all 4 hit
				//System.out.println("4 corners hits");
				//System.out.println("Math.min(cDists[0], cDists[1]): "+Math.min(cDists[0], cDists[1]));
				//System.out.println("Math.min(cDists[2], cDists[3]): "+Math.min(cDists[2], cDists[3]));
				//System.out.println("Math.min(Math.min(cDists[0], cDists[1]), Math.min(cDists[2], cDists[3])): "+Math.min(Math.min(cDists[0], cDists[1]), Math.min(cDists[2], cDists[3])));
				minCDist = Math.min(Math.min(cDists[0], cDists[1]), Math.min(cDists[2], cDists[3]));
			}
			
			
			if(minCDist == cDists[0]) {
				hits = corners[0];
			}else if(minCDist == cDists[1]) {
				hits = corners[1];
			}else if(minCDist == cDists[2]) {
				hits = corners[2];
			}else if(minCDist == cDists[3]) {
				hits = corners[3];
			}else{
				hits = 0;
			}
			//System.out.println("corners[0]: "+corners[0]+", corners[1]: "+corners[1]+", corners[2]: "+corners[2]+", corners[3]: "+corners[3]);
			//System.out.println(" cDists[0]: "+cDists[0]+", cDists[1]: "+cDists[1]+", cDists[2]: "+cDists[2]+", cDists[3]: "+cDists[3]+", minCDist: "+minCDist);
			//System.out.println("hits: "+hits);
		}else {
			hits = 0;
		}
		
		
		//System.out.println("hits: "+hits);
		
		
		
		//if all corner hits stats are the same
		
		//System.out.println(minCDist);
		
		return hits;
		
		
	}
	synchronized public double distance(double ax, double ay, double bx, double by) {
		return Math.hypot(ax-bx, ay-by);
	}
	synchronized public double iX(double a,double b, int y) {
		double X = (y-b)/a;
		
		return X;
		
	}
	synchronized public double iY(double a,double b, int x) {
		double Y = a*x+b;
		
		return Y;
	}
	
	synchronized public double[][] vVectors(Point p, int w, int h, double velx, double vely) {
		double[][] vecs = new double[4][7];
		int pX = p.x;
		int pY = p.y;
		pX+=velx;
		pY+=vely;
		
		Point a1 = p;
		Point b1 = new Point(p.x+w-1,p.y);
		Point c1 = new Point(p.x+w-1,p.y+h-1);
		Point d1 = new Point(p.x,p.y+h-1);
		
		Point a2 = new Point(pX,pY);
		Point b2 = new Point(pX+w-1,pY);
		Point c2 = new Point(pX+w-1,pY+h-1);
		Point d2 = new Point(pX,pY+h-1);
		
		vecs[0] = AB(a1,a2);
		vecs[1] = AB(b1,b2);
		vecs[2] = AB(c1,c2);
		vecs[3] = AB(d1,d2);
		
		
		
		return vecs;
	}
	
	//get y = mx+b;
	synchronized public double[] AB(Point a, Point b) {
		double[] ab = new double[7];
		
		if((b.x-a.x)==0) { //avoid divide by zero
			ab[0] = a.x; 
			
			ab[6] = 1; //div zero bool "bit"
		}else {
			ab[0] = (b.y-a.y)/(b.x-a.x);  	//a
			ab[1] = a.y-ab[0]*a.x;  		//b
			ab[6] = 0; //div zero bool "bit"
		}
			
		
		
		ab[2] = a.x;			//Point A x val
		ab[3] = a.y;			//Point A y val
		ab[4] = b.x;			//Point B x val
		ab[5] = b.y;			//Point B y val
		return ab;
	}
	
	
	//0 = no collision
	//1 = floor collision
	//2 = left wall collision
	//3 = right wall collision
	//4 = ciel collision
	synchronized public int[] Collision(Point p, int w, int h, double velx, double vely) {
		Rectangle r = new Rectangle(p.x,p.y,w,h);
		Rectangle box;
		int[] returnList = new int[8];
		int[] hits = new int[boxes.size()];
		boolean horizToBlock;
		boolean vertToBlock;
		
		boolean TopColl = false;
		boolean LeftColl = false;
		boolean RightColl = false;
		boolean BotColl =false;
		
		boolean feetinblock;
		boolean frontinblock;
		boolean backinblock;
		boolean headinblock;
		
		boolean inTopHalf;
		boolean inLeftHalf;
		boolean inRightHalf;
		boolean inBotHalf;
		
		boolean feetOn;
		boolean onLWall;
		boolean onRWall;
		boolean headUnder;
		//System.out.println("velx: "+velx+"   vely: "+vely);
		returnList[0] = 0;
		returnList[2] = 0;
		returnList[4] = 0;
		returnList[6] = 0;
		for(int i = 0; i<boxes.size(); i++) {
			box = (Rectangle)boxes.get(i);
			
			hits[i] = hits(p, w, h, velx, vely, box);
			
			
			
			vertToBlock = ((r.x+r.width-1)>=box.x)&&(r.x<=(box.x+box.width-1));
			feetinblock = ((r.y+r.height-1)>=(box.y))&&vertToBlock;
			headinblock = (r.y<=(box.y+box.height-1))&&vertToBlock;
			
			feetOn = ((r.y+r.height-1)==(box.y-1))&&vertToBlock;
			headUnder = (r.y==(box.y+box.height))&&vertToBlock;
			//System.out.println((r.y+r.height-1) + " " + (box.y-1));
			//System.out.println(feetOn);
			
			horizToBlock = ((r.y+r.height-1)>=box.y)&&(r.y<=(box.y+box.height-1));
			frontinblock = ((r.x+r.width-1)>=(box.x))&&horizToBlock;
			backinblock = (r.x<=(box.x+box.width-1))&&horizToBlock;
			
			//System.out.println("Player Front: "+(r.x+r.width-1)+", BoxLeftWall: "+(box.x-1)+", i: "+i);
			onLWall = ((r.x+r.width-1)==(box.x-1))&&horizToBlock;
			onRWall = (r.x==(box.x+box.width))&&horizToBlock;
			
			inTopHalf = (feetinblock&&((r.y+r.height)<(box.y+(box.height/2)-1)));
			inLeftHalf = (frontinblock&&((r.x+r.width)<(box.x+(box.width/2)-1)));
			inRightHalf = (backinblock&&(r.x>(box.x+(box.width/2)-1)));
			inBotHalf = (headinblock&&(r.y>(box.y+(box.height/2)-1)));
			
			//System.out.println("onLWall: "+onLWall+", onRWall: "+onRWall);
			//System.out.println(r.height);
			
			
			if(hits[i]==1||inTopHalf||feetOn) {
				
				returnList[0] = 1;
				returnList[1] = box.y-r.height;
				
				if(boxesTouched.get(i)==0) {
					boxesTouched.set(i, 1);
					scoreTouched++;
				}
				
				//System.out.println("X: "+p.x+", Y: "+p.y+", xvel: "+velx+", yvel: "+vely+", hits: "+hits);
				//System.out.println(hits);
				
			}
			if(hits[i]==2||inLeftHalf||onLWall) {
				if(boxesTouched.get(i)==0) {
					boxesTouched.set(i, 1);
					scoreTouched++;
				}
				returnList[2] = 2;
				returnList[3] = box.x-r.width;
				//System.out.println(hits);
				//System.out.println("X: "+p.x+", Y: "+p.y+", xvel: "+velx+", yvel: "+vely+", hits: "+hits);
				
			}
			if(hits[i]==3||inRightHalf||onRWall) {
				if(boxesTouched.get(i)==0) {
					boxesTouched.set(i, 1);
					scoreTouched++;
				}
				returnList[4] = 3;
				returnList[5] = box.x+box.width;
				//System.out.println(hits);
				//System.out.println("X: "+p.x+", Y: "+p.y+", xvel: "+velx+", yvel: "+vely+", hits: "+hits);
				
			}
			if(hits[i]==4||inBotHalf||headUnder) {
				if(boxesTouched.get(i)==0) {
					boxesTouched.set(i, 1);
					scoreTouched++;
				}
				returnList[6] = 4;
				returnList[7] = box.y+box.height;
				//System.out.println(hits);
				
				//System.out.println("X: "+p.x+", Y: "+p.y+", xvel: "+velx+", yvel: "+vely+", hits: "+hits);
				
			}
			
			/*else if(feetOn) {
			
				returnList[0] = 5;
				returnList[1] = box.y-r.height;
				return returnList;
			}else if(onLWall) { 
				//System.out.println("onLWall");
				returnList[0] = 6;
				returnList[1] = box.x-r.width;
				return returnList;
			}else if(onRWall) {
				returnList[0] = 7;
				returnList[1] = box.x+box.width;
				return returnList;
			}else if(headUnder) {
				returnList[0] = 8;
				returnList[1] = box.y+box.height;
				return returnList;
			}*/
			//System.out.println("hits: "+hits[i]+", i: "+i);
		}
		
		//System.out.println("Up: "+returnList[0]+", Left: "+returnList[2]+", Right: "+returnList[4]+", Down: "+returnList[6]);
		
		return returnList;
	}
	synchronized public void move() {
		Rectangle boxcurr;
		Rectangle boxnext;
		double newX;
		
		if(!boxes.isEmpty()) {
				  	for(int i=0; i<boxes.size(); i++) {
				  		boxcurr = (Rectangle) boxes.get(i);
				  		newX = (boxcurr.x-=xGvel);
				  		boxnext = new Rectangle((int) newX,boxcurr.y,boxcurr.width,boxcurr.height);
				  		boxes.set(i, boxnext);
				  		if(boxnext.x+boxnext.width<0) {
				  			boxes.remove(i);
				  			boxesTouched.remove(i);
				  			genHoriz();
				  			scoreDist++;
				  		}
				  	}
			  }
		
	}
	synchronized public void draw(Graphics g) {
		Rectangle box;
		
		
		for(int i = 0; i<boxes.size(); i++) {
			box = (Rectangle) boxes.get(i);
			g.setColor(Color.blue);
			if(boxesTouched.get(i)==1) {
				g.setColor(Color.red);
				
			}
			g.fillRect(box.x, box.y, box.width, box.height);
			
		}
		
	}
	synchronized public int getNumObstacles()
	{
		return boxes.size();
	}
}
