/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package resources;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import main.MainComponent;
import screen.BaseBitmap;
import screen.BaseScreen;

public class Art {

	// Animation
	// Tiles
	public static BaseBitmap[] water;
	public static BaseBitmap[] water_top;
	public static BaseBitmap[] water_top_left;
	public static BaseBitmap[] water_left;
	public static BaseBitmap[] water_top_right;
	public static BaseBitmap[] water_right;
	// ----------------------------------------------------------
	public static BaseBitmap[] exit_arrow;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Dialog
	public static BaseBitmap dialogue_next;
	public static BaseBitmap dialogue_bottom;
	public static BaseBitmap dialogue_bottom_left;
	public static BaseBitmap dialogue_left;
	public static BaseBitmap dialogue_top_left;
	public static BaseBitmap dialogue_top;
	public static BaseBitmap dialogue_top_right;
	public static BaseBitmap dialogue_right;
	public static BaseBitmap dialogue_bottom_right;
	public static BaseBitmap dialogue_background;
	public static BaseBitmap dialogue_pointer;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Editor
	public static BaseBitmap error;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Floor
	public static BaseBitmap grass;
	public static BaseBitmap mt_ground;
	public static BaseBitmap path;
	public static BaseBitmap forestEntrance;
	public static BaseBitmap carpet_indoors;
	public static BaseBitmap carpet_outdoors;
	public static BaseBitmap hardwood_indoors;
	public static BaseBitmap tatami_1_indoors;
	public static BaseBitmap tatami_2_indoors;
	// ----------------------------------------------------------
	public static BaseBitmap stairs_left;
	public static BaseBitmap stairs_top;
	public static BaseBitmap stairs_right;
	public static BaseBitmap stairs_bottom;
	public static BaseBitmap stairs_mt_left;
	public static BaseBitmap stairs_mt_top;
	public static BaseBitmap stairs_mt_right;
	public static BaseBitmap stairs_mt_bottom;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// House
	public static BaseBitmap house_door;
	public static BaseBitmap house_bottom;
	public static BaseBitmap house_bottom_left;
	public static BaseBitmap house_bottom_right;
	public static BaseBitmap house_left;
	public static BaseBitmap house_left_windows_right;
	public static BaseBitmap house_center;
	public static BaseBitmap house_center_windows_center;
	public static BaseBitmap house_center_windows_left;
	public static BaseBitmap house_center_windows_right;
	public static BaseBitmap house_right;
	public static BaseBitmap house_right_windows_left;
	public static BaseBitmap house_roof_left;
	public static BaseBitmap house_roof_middle;
	public static BaseBitmap house_roof_right;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Inventory
	public static BaseBitmap inventory_gui;
	public static BaseBitmap inventory_backpack_potions;
	public static BaseBitmap inventory_backpack_keyItems;
	public static BaseBitmap inventory_backpack_pokeballs;
	public static BaseBitmap inventory_backpack_TM_HM;
	public static BaseBitmap inventory_tag_potions;
	public static BaseBitmap inventory_tag_keyItems;
	public static BaseBitmap inventory_tag_pokeballs;
	public static BaseBitmap inventory_tag_TM_HM;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Ledge
	public static BaseBitmap ledge_bottom;
	public static BaseBitmap ledge_bottom_left;
	public static BaseBitmap ledge_left;
	public static BaseBitmap ledge_top_left;
	public static BaseBitmap ledge_top;
	public static BaseBitmap ledge_top_right;
	public static BaseBitmap ledge_right;
	public static BaseBitmap ledge_bottom_right;
	public static BaseBitmap Ledge_bottom_left_corner;
	public static BaseBitmap Ledge_bottom_right_corner;
	// ----------------------------------------------------------
	public static BaseBitmap ledge_mt_bottom;
	public static BaseBitmap ledge_mt_bottom_left;
	public static BaseBitmap ledge_mt_left;
	public static BaseBitmap ledge_mt_top_left;
	public static BaseBitmap ledge_mt_top;
	public static BaseBitmap ledge_mt_top_right;
	public static BaseBitmap ledge_mt_right;
	public static BaseBitmap ledge_mt_bottom_right;
	// ----------------------------------------------------------
	public static BaseBitmap ledge_inner_bottom;
	public static BaseBitmap ledge_inner_bottom_left;
	public static BaseBitmap ledge_inner_left;
	public static BaseBitmap ledge_inner_top_left;
	public static BaseBitmap ledge_inner_top;
	public static BaseBitmap ledge_inner_top_right;
	public static BaseBitmap ledge_inner_right;
	public static BaseBitmap ledge_inner_bottom_right;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Object (Items, Movable)
	public static BaseBitmap item;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Obstacle
	public static BaseBitmap smallTree;
	public static BaseBitmap logs;
	public static BaseBitmap planks;
	public static BaseBitmap scaffolding_left;
	public static BaseBitmap scaffolding_right;
	public static BaseBitmap sign;
	public static BaseBitmap workbench_left;
	public static BaseBitmap workbench_right;
	public static BaseBitmap deadSmallTree;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Player (Entities)
	public static BaseBitmap[][] player;
	public static BaseBitmap[][] player_surf;
	public static BaseBitmap[][] player_bicycle;
	public static BaseBitmap shadow;

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Area
	public static BaseBitmap testArea;
	public static BaseBitmap testArea2;
	public static BaseBitmap testArea3;

	// Font
	public static Font font;

	public static void loadAllResources(BaseScreen screen) {

		// Animation
		water = loadAnimation(screen, 16, "art/animation/water/water00");
		water_top = loadAnimation(screen, 16, "art/animation/water/water_top00");
		water_top_left = loadAnimation(screen, 16, "art/animation/water/water_top_left00");
		water_left = loadAnimation(screen, 16, "art/animation/water/water_left00");
		water_top_right = loadAnimation(screen, 16, "art/animation/water/water_top_right00");
		water_right = loadAnimation(screen, 16, "art/animation/water/water_right00");
		// ----------------------------------------------------------
		exit_arrow = loadAnimation(screen, 10, "art/animation/arrow/arrow00");

		// Dialogue
		dialogue_next = BaseScreen.load("art/dialog/dialogue_next.png");
		dialogue_bottom = BaseScreen.load("art/dialog/dialogue_bottom.png");
		dialogue_bottom_left = BaseScreen.load("art/dialog/dialogue_bottom_left.png");
		dialogue_left = BaseScreen.load("art/dialog/dialogue_left.png");
		dialogue_top_left = BaseScreen.load("art/dialog/dialogue_top_left.png");
		dialogue_top = BaseScreen.load("art/dialog/dialogue_top.png");
		dialogue_top_right = BaseScreen.load("art/dialog/dialogue_top_right.png");
		dialogue_right = BaseScreen.load("art/dialog/dialogue_right.png");
		dialogue_bottom_right = BaseScreen.load("art/dialog/dialogue_bottom_right.png");
		dialogue_background = BaseScreen.load("art/dialog/dialogue_bg.png");
		dialogue_pointer = BaseScreen.load("art/dialog/dialogue_pointer.png");

		// Editor
		error = BaseScreen.load("art/editor/no_png.png");

		// Floor
		grass = BaseScreen.load("art/floor/grass.png");
		mt_ground = BaseScreen.load("art/floor/mt_ground.png");
		forestEntrance = BaseScreen.load("art/floor/forestEntrance.png");
		path = BaseScreen.load("art/floor/path.png");
		stairs_left = BaseScreen.load("art/floor/stairs_left.png");
		stairs_top = BaseScreen.load("art/floor/stairs_top.png");
		stairs_right = BaseScreen.load("art/floor/stairs_right.png");
		stairs_bottom = BaseScreen.load("art/floor/stairs_bottom.png");
		stairs_mt_left = BaseScreen.load("art/floor/stairs_mt_left.png");
		stairs_mt_top = BaseScreen.load("art/floor/stairs_mt_top.png");
		stairs_mt_right = BaseScreen.load("art/floor/stairs_mt_right.png");
		stairs_mt_bottom = BaseScreen.load("art/floor/stairs_mt_bottom.png");
		carpet_indoors = BaseScreen.load("art/floor/carpet_indoors.png");
		carpet_outdoors = BaseScreen.load("art/floor/carpet_outdoors.png");
		hardwood_indoors = BaseScreen.load("art/floor/hardwood_indoors.png");
		tatami_1_indoors = BaseScreen.load("art/floor/tatami_1_indoors.png");
		tatami_2_indoors = BaseScreen.load("art/floor/tatami_2_indoors.png");

		// House
		house_door = BaseScreen.load("art/house/house_door.png");
		house_bottom = BaseScreen.load("art/house/house_bottom.png");
		house_bottom_left = BaseScreen.load("art/house/house_bottom_left.png");
		house_bottom_right = BaseScreen.load("art/house/house_bottom_right.png");
		house_center = BaseScreen.load("art/house/house_center.png");
		house_center_windows_center = BaseScreen.load("art/house/house_center_windows_center.png");
		house_center_windows_left = BaseScreen.load("art/house/house_center_windows_left.png");
		house_center_windows_right = BaseScreen.load("art/house/house_center_windows_right.png");
		house_left = BaseScreen.load("art/house/house_left.png");
		house_left_windows_right = BaseScreen.load("art/house/house_left_windows_right.png");
		house_right = BaseScreen.load("art/house/house_right.png");
		house_right_windows_left = BaseScreen.load("art/house/house_right_windows_left.png");
		house_roof_left = BaseScreen.load("art/house/house_roof_left.png");
		house_roof_middle = BaseScreen.load("art/house/house_roof_middle.png");
		house_roof_right = BaseScreen.load("art/house/house_roof_right.png");

		// Inventory
		inventory_gui = BaseScreen.load("art/inventory/inventory_gui.png");
		inventory_backpack_potions = BaseScreen.load("art/inventory/backpack_potions.png");
		inventory_backpack_keyItems = BaseScreen.load("art/inventory/backpack_keyitems.png");
		inventory_backpack_pokeballs = BaseScreen.load("art/inventory/backpack_pokeballs.png");
		inventory_backpack_TM_HM = BaseScreen.load("art/inventory/backpack_tm_hm.png");
		inventory_tag_potions = BaseScreen.load("art/inventory/potions.png");
		inventory_tag_keyItems = BaseScreen.load("art/inventory/keyitems.png");
		inventory_tag_pokeballs = BaseScreen.load("art/inventory/pokeballs.png");
		inventory_tag_TM_HM = BaseScreen.load("art/inventory/tm_hm.png");

		// Ledges
		ledge_bottom = BaseScreen.load("art/ledge/ledge_bottom.png");
		ledge_bottom_left = BaseScreen.load("art/ledge/ledge_bottom_left.png");
		ledge_left = BaseScreen.load("art/ledge/ledge_left.png");
		ledge_top_left = BaseScreen.load("art/ledge/ledge_top_left.png");
		ledge_top = BaseScreen.load("art/ledge/ledge_top.png");
		ledge_top_right = BaseScreen.load("art/ledge/ledge_top_right.png");
		ledge_right = BaseScreen.load("art/ledge/ledge_right.png");
		ledge_bottom_right = BaseScreen.load("art/ledge/ledge_bottom_right.png");
		Ledge_bottom_left_corner = BaseScreen.load("art/ledge/ledge_bottom_left_corner.png");
		Ledge_bottom_right_corner = BaseScreen.load("art/ledge/ledge_bottom_right_corner.png");
		ledge_mt_bottom = BaseScreen.load("art/ledge/ledge_mt_bottom.png");
		ledge_mt_bottom_left = BaseScreen.load("art/ledge/ledge_mt_bottom_left.png");
		ledge_mt_left = BaseScreen.load("art/ledge/ledge_mt_left.png");
		ledge_mt_top_left = BaseScreen.load("art/ledge/ledge_mt_top_left.png");
		ledge_mt_top = BaseScreen.load("art/ledge/ledge_mt_top.png");
		ledge_mt_top_right = BaseScreen.load("art/ledge/ledge_mt_top_right.png");
		ledge_mt_right = BaseScreen.load("art/ledge/ledge_mt_right.png");
		ledge_mt_bottom_right = BaseScreen.load("art/ledge/ledge_mt_bottom_right.png");
		ledge_inner_bottom = BaseScreen.load("art/ledge/ledge_inner_bottom.png");
		ledge_inner_bottom_left = BaseScreen.load("art/ledge/ledge_inner_bottom_left.png");
		ledge_inner_left = BaseScreen.load("art/ledge/ledge_inner_left.png");
		ledge_inner_top_left = BaseScreen.load("art/ledge/ledge_inner_top_left.png");
		ledge_inner_top = BaseScreen.load("art/ledge/ledge_inner_top.png");
		ledge_inner_top_right = BaseScreen.load("art/ledge/ledge_inner_top_right.png");
		ledge_inner_right = BaseScreen.load("art/ledge/ledge_inner_right.png");
		ledge_inner_bottom_right = BaseScreen.load("art/ledge/ledge_inner_bottom_right.png");

		// Object
		item = BaseScreen.load("art/object/item.png");

		// Obstacle
		logs = BaseScreen.load("art/obstacle/logs.png");
		planks = BaseScreen.load("art/obstacle/planks.png");
		scaffolding_left = BaseScreen.load("art/obstacle/scaffolding_left.png");
		scaffolding_right = BaseScreen.load("art/obstacle/scaffolding_right.png");
		smallTree = BaseScreen.load("art/obstacle/small_tree.png");
		sign = BaseScreen.load("art/obstacle/sign.png");
		workbench_left = BaseScreen.load("art/obstacle/workbench_left.png");
		workbench_right = BaseScreen.load("art/obstacle/workbench_right.png");
		deadSmallTree = BaseScreen.load("art/obstacle/dead_small_tree.png");

		// Player, NPCs
		player = screen.cut("art/player/player.png", 16, 16, 0, 0);
		player_surf = screen.cut("art/player/player_surf.png", 16, 16, 0, 0);
		player_bicycle = screen.cut("art/player/player_bicycle.png", 16, 16, 0, 0);
		shadow = BaseScreen.load("art/player/shadow.png");

		// Areas
		testArea = BaseScreen.load("area/test/testArea.png");
		testArea2 = BaseScreen.load("area/test/testArea2.png");

		// Miscellaneous
		font = loadFont("font/font.ttf");

	}

	private static BaseBitmap[] loadAnimation(BaseScreen screen, int frames, String filename) {
		BaseBitmap[] result = new BaseBitmap[frames];
		for (int i = 0; i < frames; i++) {
			// There are 16 frames for the water.
			if (i < 10) {
				result[i] = BaseScreen.load(filename + "0" + String.valueOf(i) + ".png");
			}
			else {
				result[i] = BaseScreen.load(filename + String.valueOf(i) + ".png");
			}
		}
		return result;
	}

	public static Font loadFont(String filename) {
		Enumeration<URL> urls = null;
		URL url = null;
		try {
			urls = Art.class.getClassLoader().getResources(filename);
		}
		catch (IOException e2) {
			e2.printStackTrace();
		}
		for (; urls.hasMoreElements();)
			url = urls.nextElement();

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font result = null;
		try {
			result = Font.createFont(Font.TRUETYPE_FONT, url.openStream());
		}
		catch (FontFormatException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			try {
				result = Font.createFont(Font.TRUETYPE_FONT, Art.class.getResourceAsStream(filename));
			}
			catch (FontFormatException e1) {
				e1.printStackTrace();
			}
			catch (IOException e1) {
				e1.printStackTrace();
				try {
					result = Font.createFont(Font.TRUETYPE_FONT, new File("res/font/font.ttf"));
				}
				catch (FontFormatException e2) {
					e2.printStackTrace();
				}
				catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
		if (result != null) {
			ge.registerFont(result);
			System.out.println(filename);
		}
		return result.deriveFont(Font.PLAIN, 8f * MainComponent.GAME_SCALE);
	}

	public static BaseBitmap changeColors(BaseBitmap bitmap, int color) {
		BaseBitmap result = new BaseBitmap(bitmap.getWidth(), bitmap.getHeight());
		int[] pixels = bitmap.getPixels();
		int[] resultPixels = result.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			int alpha = (pixels[i] >> 24) & 0xFF;
			// May be possible this will expand in the future.
			switch (alpha) {
				case 0x01:
					resultPixels[i] = 0xFF000000 | BaseScreen.lighten(color, 0.2f);
					break;
				default:
					resultPixels[i] = 0xFF000000 | BaseScreen.darken(color, 0.1f);
					break;
			}
		}
		return result;
	}

	public static int blendPixels(int bgColor, int blendColor) {
		int alphaBlend = (blendColor >> 24) & 0xFF;
		int alphaBackground = 256 - alphaBlend;

		int bgRed = (bgColor >> 16) & 0xFF;
		int bgGreen = (bgColor >> 8) & 0xFF;
		int bgBlue = bgColor & 0xFF;

		int blendRed = (blendColor >> 16) & 0xFF;
		int blendGreen = (blendColor >> 8) & 0xFF;
		int blendBlue = blendColor & 0xFF;

		int red = ((blendRed * alphaBlend + bgRed * alphaBackground) >> 8) & 0xFF;
		int green = ((blendGreen * alphaBlend + bgGreen * alphaBackground) >> 8) & 0xFF;
		int blue = ((blendBlue * alphaBlend + bgBlue * alphaBackground) >> 8) & 0xFF;

		return 0xFF000000 | red | green | blue;
	}
}
