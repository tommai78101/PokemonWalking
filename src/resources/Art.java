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
	public static BaseBitmap ledge_horizontal_bottom;
	
	//Area
	public static BaseBitmap testArea;
	public static BaseBitmap testArea2;
	public static BaseBitmap testArea3;
	public static BaseBitmap testArea4;
	
	//Others
	public static BaseBitmap sprite;
	public static BaseBitmap shadow;
	
	public static void loadAllResources(BaseScreen screen) {
		//Wall
		smallTree = screen.load("art/wall/treeSmall.png");
		ledge_horizontal_bottom = screen.load("art/wall/ledge_horizontal_bottom.png"); //FIXME: Redo bitmap.
		
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
	}
}
