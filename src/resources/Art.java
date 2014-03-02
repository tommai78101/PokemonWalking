package resources;

import screen.BaseBitmap;
import screen.BaseScreen;

public class Art {
	
	//Entities
	public static BaseBitmap[][] player;
	public static BaseBitmap[][] testDownAnimation;
	
	//Tiles
	public static BaseBitmap testTile;
	public static BaseBitmap smallTree;
	
	//Area
	public static BaseBitmap testArea;
	
	//Others
	public static BaseBitmap sprite;
	
	public static void loadAllResources(BaseScreen screen) {
		sprite = screen.load("/art/test.png");
		player = screen.cut("/art/player/player.png", 16, 16, 0, 0);
		testTile = screen.load("/art/floor/testTile.png");
		smallTree = screen.load("/art/wall/treeSmall.png");
		testDownAnimation = screen.cut("/art/player/test_walk_down_animation.png", 16, 16, 0, 0);
		
		testArea = screen.load("/area/test/testArea.png");
	}
}
