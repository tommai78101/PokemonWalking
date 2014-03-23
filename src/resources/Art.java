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
	
	//Area
	public static BaseBitmap testArea;
	public static BaseBitmap testArea2;
	
	//Others
	public static BaseBitmap sprite;
	
	public static void loadAllResources(BaseScreen screen) {
		//Misc.
		sprite = screen.load("art/test.png");
		
		//Wall
		smallTree = screen.load("art/wall/treeSmall.png");
		
		//Floor
		grass = screen.load("art/floor/grass.png");
		forestEntrance = screen.load("art/floor/forestEntrance.png");

		//Player, NPCs
		testDownAnimation = screen.cut("art/player/test_walk_down_animation.png", 16, 16, 0, 0);
		player = screen.cut("art/player/player.png", 16, 16, 0, 0);
		
		//Areas
		testArea = screen.load("area/test/testArea.png");
		testArea2 = screen.load("area/test/warp_area.png");
	}
}
