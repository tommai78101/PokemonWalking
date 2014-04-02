package resources;

import screen.BaseBitmap;
import screen.BaseScreen;

public class Art {
	
	//Entities
	public static BaseBitmap[][] player;
	public static BaseBitmap[][] player_surf;
	public static BaseBitmap[][] testDownAnimation;
	
	//Tiles
	public static BaseBitmap grass;
	public static BaseBitmap mt_ground;
	public static BaseBitmap smallTree;
	public static BaseBitmap forestEntrance;
	public static BaseBitmap path;
	public static BaseBitmap ledge_bottom;
	public static BaseBitmap ledge_bottom_left;
	public static BaseBitmap ledge_left;
	public static BaseBitmap ledge_top_left;
	public static BaseBitmap ledge_top;
	public static BaseBitmap ledge_top_right;
	public static BaseBitmap ledge_right;
	public static BaseBitmap ledge_bottom_right;
	public static BaseBitmap ledge_mt_bottom;
	public static BaseBitmap ledge_mt_bottom_left;
	public static BaseBitmap ledge_mt_left;
	public static BaseBitmap ledge_mt_top_left;
	public static BaseBitmap ledge_mt_top;
	public static BaseBitmap ledge_mt_top_right;
	public static BaseBitmap ledge_mt_right;
	public static BaseBitmap ledge_mt_bottom_right;
	public static BaseBitmap ledge_inner_bottom;
	public static BaseBitmap ledge_inner_bottom_left;
	public static BaseBitmap ledge_inner_left;
	public static BaseBitmap ledge_inner_top_left;
	public static BaseBitmap ledge_inner_top;
	public static BaseBitmap ledge_inner_top_right;
	public static BaseBitmap ledge_inner_right;
	public static BaseBitmap ledge_inner_bottom_right;
	public static BaseBitmap stairs_left;
	public static BaseBitmap stairs_top;
	public static BaseBitmap stairs_right;
	public static BaseBitmap stairs_bottom;
	public static BaseBitmap stairs_mt_left;
	public static BaseBitmap stairs_mt_top;
	public static BaseBitmap stairs_mt_right;
	public static BaseBitmap stairs_mt_bottom;
	
	//Area
	public static BaseBitmap testArea;
	public static BaseBitmap testArea2;
	public static BaseBitmap testArea3;
	public static BaseBitmap testArea4;
	public static BaseBitmap testArea_debug;
	
	//Others
	public static BaseBitmap sprite;
	public static BaseBitmap shadow;
	public static BaseBitmap error;
	
	//Animation
	public static BaseBitmap[] water;
	
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
		ledge_mt_bottom = screen.load("art/wall/ledge_mt_bottom.png");
		ledge_mt_bottom_left = screen.load("art/wall/ledge_mt_bottom_left.png");
		ledge_mt_left = screen.load("art/wall/ledge_mt_left.png");
		ledge_mt_top_left = screen.load("art/wall/ledge_mt_top_left.png");
		ledge_mt_top = screen.load("art/wall/ledge_mt_top.png");
		ledge_mt_top_right = screen.load("art/wall/ledge_mt_top_right.png");
		ledge_mt_right = screen.load("art/wall/ledge_mt_right.png");
		ledge_mt_bottom_right = screen.load("art/wall/ledge_mt_bottom_right.png");
		ledge_inner_bottom = screen.load("art/wall/ledge_inner_bottom.png");
		ledge_inner_bottom_left = screen.load("art/wall/ledge_inner_bottom_left.png");
		ledge_inner_left = screen.load("art/wall/ledge_inner_left.png");
		ledge_inner_top_left = screen.load("art/wall/ledge_inner_top_left.png");
		ledge_inner_top = screen.load("art/wall/ledge_inner_top.png");
		ledge_inner_top_right = screen.load("art/wall/ledge_inner_top_right.png");
		ledge_inner_right = screen.load("art/wall/ledge_inner_right.png");
		ledge_inner_bottom_right = screen.load("art/wall/ledge_inner_bottom_right.png");
		stairs_left = screen.load("art/floor/stairs_left.png");
		stairs_top = screen.load("art/floor/stairs_top.png");
		stairs_right = screen.load("art/floor/stairs_right.png");
		stairs_bottom = screen.load("art/floor/stairs_bottom.png");
		stairs_mt_left = screen.load("art/floor/stairs_mt_left.png");
		stairs_mt_top = screen.load("art/floor/stairs_mt_top.png");
		stairs_mt_right = screen.load("art/floor/stairs_mt_right.png");
		stairs_mt_bottom = screen.load("art/floor/stairs_mt_bottom.png");
		
		//Floor
		grass = screen.load("art/floor/grass.png");
		mt_ground = screen.load("art/floor/mt_ground.png");
		forestEntrance = screen.load("art/floor/forestEntrance.png");
		path = screen.load("art/floor/path.png");
		
		//Player, NPCs
		testDownAnimation = screen.cut("art/player/test_walk_down_animation.png", 16, 16, 0, 0);
		player = screen.cut("art/player/player.png", 16, 16, 0, 0);
		shadow = screen.load("art/player/shadow.png");
		player_surf = screen.cut("art/player/player_surf.png", 16, 16, 0, 0);
		
		//Areas
		testArea = screen.load("area/test/testArea.png");
		testArea2 = screen.load("area/test/testArea2.png");
		testArea3 = screen.load("area/test/testArea3.png");
		testArea4 = screen.load("area/test/testArea4.png");
		testArea_debug = screen.load("area/test/testArea_debug.png");
		
		//Miscellaneous
		error = screen.load("art/debug/no_png.png");
		
		//Animation
		water = new BaseBitmap[16];
		for (int i = 0; i < water.length; i++) {
			//There are 16 frames for the water.
			if (i < 10) {
				water[i] = screen.load("art/animation/water/water000" + String.valueOf(i) + ".png");
			}
			else {
				water[i] = screen.load("art/animation/water/water00" + String.valueOf(i) + ".png");
			}
		}
	}
}
