/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package resources;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import common.Debug;
import main.MainComponent;
import screen.BaseBitmap;
import screen.Scene;

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
	// Areas
	public static BaseBitmap debugArea;
	public static BaseBitmap testArea;
	public static BaseBitmap testArea2;

	// Font
	public static Font font;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Miscellaneous
	public static int COLOR_DEBUG_GREEN = 0xA4E767;

	public static void loadAllResources(Scene screen) {
		// Animation
		Art.water = Art.loadAnimation(screen, 16, "art/animation/water/water00");
		Art.water_top = Art.loadAnimation(screen, 16, "art/animation/water/water_top00");
		Art.water_top_left = Art.loadAnimation(screen, 16, "art/animation/water/water_top_left00");
		Art.water_left = Art.loadAnimation(screen, 16, "art/animation/water/water_left00");
		Art.water_top_right = Art.loadAnimation(screen, 16, "art/animation/water/water_top_right00");
		Art.water_right = Art.loadAnimation(screen, 16, "art/animation/water/water_right00");
		// ----------------------------------------------------------
		Art.exit_arrow = Art.loadAnimation(screen, 10, "art/animation/arrow/arrow00");

		// Dialogue
		Art.dialogue_next = BaseBitmap.load("art/dialog/dialogue_next.png");
		Art.dialogue_bottom = BaseBitmap.load("art/dialog/dialogue_bottom.png");
		Art.dialogue_bottom_left = BaseBitmap.load("art/dialog/dialogue_bottom_left.png");
		Art.dialogue_left = BaseBitmap.load("art/dialog/dialogue_left.png");
		Art.dialogue_top_left = BaseBitmap.load("art/dialog/dialogue_top_left.png");
		Art.dialogue_top = BaseBitmap.load("art/dialog/dialogue_top.png");
		Art.dialogue_top_right = BaseBitmap.load("art/dialog/dialogue_top_right.png");
		Art.dialogue_right = BaseBitmap.load("art/dialog/dialogue_right.png");
		Art.dialogue_bottom_right = BaseBitmap.load("art/dialog/dialogue_bottom_right.png");
		Art.dialogue_background = BaseBitmap.load("art/dialog/dialogue_bg.png");
		Art.dialogue_pointer = BaseBitmap.load("art/dialog/dialogue_pointer.png");

		// Editor
		Art.error = BaseBitmap.load("art/editor/no_png.png");

		// Floor
		Art.grass = BaseBitmap.load("art/floor/grass.png");
		Art.mt_ground = BaseBitmap.load("art/floor/mt_ground.png");
		Art.forestEntrance = BaseBitmap.load("art/floor/forestEntrance.png");
		Art.path = BaseBitmap.load("art/floor/path.png");
		Art.stairs_left = BaseBitmap.load("art/floor/stairs_left.png");
		Art.stairs_top = BaseBitmap.load("art/floor/stairs_top.png");
		Art.stairs_right = BaseBitmap.load("art/floor/stairs_right.png");
		Art.stairs_bottom = BaseBitmap.load("art/floor/stairs_bottom.png");
		Art.stairs_mt_left = BaseBitmap.load("art/floor/stairs_mt_left.png");
		Art.stairs_mt_top = BaseBitmap.load("art/floor/stairs_mt_top.png");
		Art.stairs_mt_right = BaseBitmap.load("art/floor/stairs_mt_right.png");
		Art.stairs_mt_bottom = BaseBitmap.load("art/floor/stairs_mt_bottom.png");
		Art.carpet_indoors = BaseBitmap.load("art/floor/carpet_indoors.png");
		Art.carpet_outdoors = BaseBitmap.load("art/floor/carpet_outdoors.png");
		Art.hardwood_indoors = BaseBitmap.load("art/floor/hardwood_indoors.png");
		Art.tatami_1_indoors = BaseBitmap.load("art/floor/tatami_1_indoors.png");
		Art.tatami_2_indoors = BaseBitmap.load("art/floor/tatami_2_indoors.png");

		// House
		Art.house_door = BaseBitmap.load("art/house/house_door.png");
		Art.house_bottom = BaseBitmap.load("art/house/house_bottom.png");
		Art.house_bottom_left = BaseBitmap.load("art/house/house_bottom_left.png");
		Art.house_bottom_right = BaseBitmap.load("art/house/house_bottom_right.png");
		Art.house_center = BaseBitmap.load("art/house/house_center.png");
		Art.house_center_windows_center = BaseBitmap.load("art/house/house_center_windows_center.png");
		Art.house_center_windows_left = BaseBitmap.load("art/house/house_center_windows_left.png");
		Art.house_center_windows_right = BaseBitmap.load("art/house/house_center_windows_right.png");
		Art.house_left = BaseBitmap.load("art/house/house_left.png");
		Art.house_left_windows_right = BaseBitmap.load("art/house/house_left_windows_right.png");
		Art.house_right = BaseBitmap.load("art/house/house_right.png");
		Art.house_right_windows_left = BaseBitmap.load("art/house/house_right_windows_left.png");
		Art.house_roof_left = BaseBitmap.load("art/house/house_roof_left.png");
		Art.house_roof_middle = BaseBitmap.load("art/house/house_roof_middle.png");
		Art.house_roof_right = BaseBitmap.load("art/house/house_roof_right.png");

		// Inventory
		Art.inventory_gui = BaseBitmap.load("art/inventory/inventory_gui.png");
		Art.inventory_backpack_potions = BaseBitmap.load("art/inventory/backpack_potions.png");
		Art.inventory_backpack_keyItems = BaseBitmap.load("art/inventory/backpack_keyitems.png");
		Art.inventory_backpack_pokeballs = BaseBitmap.load("art/inventory/backpack_pokeballs.png");
		Art.inventory_backpack_TM_HM = BaseBitmap.load("art/inventory/backpack_tm_hm.png");
		Art.inventory_tag_potions = BaseBitmap.load("art/inventory/potions.png");
		Art.inventory_tag_keyItems = BaseBitmap.load("art/inventory/keyitems.png");
		Art.inventory_tag_pokeballs = BaseBitmap.load("art/inventory/pokeballs.png");
		Art.inventory_tag_TM_HM = BaseBitmap.load("art/inventory/tm_hm.png");

		// Ledges
		Art.ledge_bottom = BaseBitmap.load("art/ledge/ledge_bottom.png");
		Art.ledge_bottom_left = BaseBitmap.load("art/ledge/ledge_bottom_left.png");
		Art.ledge_left = BaseBitmap.load("art/ledge/ledge_left.png");
		Art.ledge_top_left = BaseBitmap.load("art/ledge/ledge_top_left.png");
		Art.ledge_top = BaseBitmap.load("art/ledge/ledge_top.png");
		Art.ledge_top_right = BaseBitmap.load("art/ledge/ledge_top_right.png");
		Art.ledge_right = BaseBitmap.load("art/ledge/ledge_right.png");
		Art.ledge_bottom_right = BaseBitmap.load("art/ledge/ledge_bottom_right.png");
		Art.Ledge_bottom_left_corner = BaseBitmap.load("art/ledge/ledge_bottom_left_corner.png");
		Art.Ledge_bottom_right_corner = BaseBitmap.load("art/ledge/ledge_bottom_right_corner.png");
		Art.ledge_mt_bottom = BaseBitmap.load("art/ledge/ledge_mt_bottom.png");
		Art.ledge_mt_bottom_left = BaseBitmap.load("art/ledge/ledge_mt_bottom_left.png");
		Art.ledge_mt_left = BaseBitmap.load("art/ledge/ledge_mt_left.png");
		Art.ledge_mt_top_left = BaseBitmap.load("art/ledge/ledge_mt_top_left.png");
		Art.ledge_mt_top = BaseBitmap.load("art/ledge/ledge_mt_top.png");
		Art.ledge_mt_top_right = BaseBitmap.load("art/ledge/ledge_mt_top_right.png");
		Art.ledge_mt_right = BaseBitmap.load("art/ledge/ledge_mt_right.png");
		Art.ledge_mt_bottom_right = BaseBitmap.load("art/ledge/ledge_mt_bottom_right.png");
		Art.ledge_inner_bottom = BaseBitmap.load("art/ledge/ledge_inner_bottom.png");
		Art.ledge_inner_bottom_left = BaseBitmap.load("art/ledge/ledge_inner_bottom_left.png");
		Art.ledge_inner_left = BaseBitmap.load("art/ledge/ledge_inner_left.png");
		Art.ledge_inner_top_left = BaseBitmap.load("art/ledge/ledge_inner_top_left.png");
		Art.ledge_inner_top = BaseBitmap.load("art/ledge/ledge_inner_top.png");
		Art.ledge_inner_top_right = BaseBitmap.load("art/ledge/ledge_inner_top_right.png");
		Art.ledge_inner_right = BaseBitmap.load("art/ledge/ledge_inner_right.png");
		Art.ledge_inner_bottom_right = BaseBitmap.load("art/ledge/ledge_inner_bottom_right.png");

		// Object
		Art.item = BaseBitmap.load("art/object/item.png");

		// Obstacle
		Art.logs = BaseBitmap.load("art/obstacle/logs.png");
		Art.planks = BaseBitmap.load("art/obstacle/planks.png");
		Art.scaffolding_left = BaseBitmap.load("art/obstacle/scaffolding_left.png");
		Art.scaffolding_right = BaseBitmap.load("art/obstacle/scaffolding_right.png");
		Art.smallTree = BaseBitmap.load("art/obstacle/small_tree.png");
		Art.sign = BaseBitmap.load("art/obstacle/sign.png");
		Art.workbench_left = BaseBitmap.load("art/obstacle/workbench_left.png");
		Art.workbench_right = BaseBitmap.load("art/obstacle/workbench_right.png");
		Art.deadSmallTree = BaseBitmap.load("art/obstacle/dead_small_tree.png");

		// Player, NPCs
		Art.player = screen.cut("art/player/player.png", 16, 16, 0, 0);
		Art.player_surf = screen.cut("art/player/player_surf.png", 16, 16, 0, 0);
		Art.player_bicycle = screen.cut("art/player/player_bicycle.png", 16, 16, 0, 0);
		Art.shadow = BaseBitmap.load("art/player/shadow.png");

		// Areas (level areas for rapid iterations)
		Art.testArea = BaseBitmap.load("art/area/test/testArea.png");
		Art.testArea2 = BaseBitmap.load("art/area/test/testArea2.png");

		// Areas (debug area for testing only)
		Art.debugArea = BaseBitmap.load("art/area/test/debug.png");

		// Miscellaneous
		Art.font = Art.loadFont("art/font/font.ttf");
	}

	private static BaseBitmap[] loadAnimation(Scene screen, int frames, String filename) {
		BaseBitmap[] result = new BaseBitmap[frames];
		for (int i = 0; i < frames; i++) {
			// There are 16 frames for the water.
			if (i < 10) {
				result[i] = BaseBitmap.load(filename + "0" + String.valueOf(i) + ".png");
			}
			else {
				result[i] = BaseBitmap.load(filename + String.valueOf(i) + ".png");
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
			Debug.info(filename);
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
					resultPixels[i] = 0xFF000000 | Scene.lighten(color, 0.2f);
					break;
				default:
					resultPixels[i] = 0xFF000000 | Scene.darken(color, 0.1f);
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
