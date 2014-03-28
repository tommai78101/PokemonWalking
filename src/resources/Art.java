package resources;

import screen.BaseBitmap;
import screen.BaseScreen;

public class Art {
	
	//Entities
	public static BaseBitmap[][] player;
	public static BaseBitmap[][] testDownAnimation;
	
	//Tiles
	public static BaseBitmap grass;
	public static BaseBitmap smallTree;
	public static BaseBitmap forestEntrance;
	public static BaseBitmap ledge_bottom;
	public static BaseBitmap ledge_bottom_left;
	public static BaseBitmap ledge_left;
	public static BaseBitmap ledge_top_left;
	public static BaseBitmap ledge_top;
	public static BaseBitmap ledge_top_right;
	public static BaseBitmap ledge_right;
	public static BaseBitmap ledge_bottom_right;
	
	//Area
	public static BaseBitmap testArea;
	public static BaseBitmap testArea2;
	public static BaseBitmap testArea3;
	public static BaseBitmap testArea4;
	public static BaseBitmap testArea_debug;
	
	//Others
	public static BaseBitmap sprite;
	public static BaseBitmap shadow;
	
	public static void loadAllResources(BaseScreen screen) {
		//Wall
		smallTree = screen.load("art/wall/treeSmall.png");
		
		//Ledges
		ledge_bottom = screen.load("art/wall/ledge_bottom.png");
		ledge_bottom_left = screen.load("art/wall/ledge_bottom_left.png");
		ledge_left = screen.load("art/wall/ledge_left.png");
		ledge_top_left = screen.load("art/wall/ledge_top_left.png");
		ledge_top = screen.load("art/wall/ledge_top.png");
		ledge_top_right = screen.load("art/wall/ledge_top_right.png");
		ledge_right = screen.load("art/wall/ledge_right.png");
		ledge_bottom_right = screen.load("art/wall/ledge_bottom_right.png");
		
		//Floor
		grass = screen.load("art/floor/grass.png");
		forestEntrance = screen.load("art/floor/forestEntrance.png");
		
		//Player, NPCs
		testDownAnimation = screen.cut("art/player/test_walk_down_animation.png", 16, 16, 0, 0);
		player = screen.cut("art/player/player.png", 16, 16, 0, 0);
		shadow = screen.load("art/player/shadow.png");
		
		//Areas
		testArea = screen.load("area/test/testArea.png");
		testArea2 = screen.load("area/test/testArea2.png");
		testArea3 = screen.load("area/test/testArea3.png");
		testArea4 = screen.load("area/test/testArea4.png");
		testArea_debug = screen.load("area/test/testArea_debug.png");
	}
}
