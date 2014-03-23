package level;

public class WayPoint {
	
	//private int xPosition;
	//private int yPosition;
	
	private WayPoint target;
	
	//private int pixelColor;
	//private int data;
	
	//A flag that determines if two areas are "linked" together nicely in the OverWorld.
	private boolean isLinked;
	
	public Area entrance;
	public Area exit;
	
	public WayPoint(int x, int y, int color) {
		//X and Y both represents the position.
		//Data value (int) means which waypoint goes to which in one area.
		//Data value is relevant to word transitions and jumping around in an area.
		
		//If data value is -1, it's basically the first position the player starts from.
		//If data value is 0, it's basically the last position the player has saved after loading from save file.
		
		//I like the color blue, therefore blue represents waypoint, and green/red both represents the data id for the waypoints.

		//this.xPosition = x;
		//this.yPosition = y;
		//this.pixelColor = color;
		
		//Leaving out the "blue" channel, we can see where this waypoint is leading to.
		//this.data = (color >> 8) & 0xFFFFFF;
		
		//We assume the flag isn't set, since the way point had just been set.
		this.isLinked = false;
		this.target = null;
	}
	
	public boolean isAreaLinked() {
		return isLinked;
	}
	
	public void setLinkage(Area src, Area tgt) {
		if (!isLinked) {
			this.entrance = src;
			this.exit = tgt;
			isLinked = true;
		}
	}
	
	public void setTargetWayPoint(WayPoint target) {
		if (target.entrance == this.exit && target.exit == this.entrance)
			this.target = target;
	}
	
	public void destroyLinkage() {
		isLinked = false;
	}
	
	public WayPoint getTargetWayPoint() {
		return this.target;
	}
	
	public boolean check(WayPoint point) {
		//TODO: Work on a method that compares two distinct way point targets' entrance and exit areas.
		//WayPoint A targets WayPoint B, thus WayPoint B targets WayPoint A.
		//In WayPoint A, A's entrance is B's exit; A's exit is B's entrance. Same goes for WayPoint B.
		return true;
	}
}
