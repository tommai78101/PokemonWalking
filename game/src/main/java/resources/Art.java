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
import screen.Bitmap;
import screen.Scene;

public class Art {

	// Animation
	// Tiles
	public static Bitmap[] water;
	public static Bitmap[] water_top;
	public static Bitmap[] water_top_left;
	public static Bitmap[] water_left;
	public static Bitmap[] water_top_right;
	public static Bitmap[] water_right;
	// ----------------------------------------------------------
	public static Bitmap[] exit_arrow;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Dialog
	public static Bitmap dialogue_next;
	public static Bitmap dialogue_bottom;
	public static Bitmap dialogue_bottom_left;
	public static Bitmap dialogue_left;
	public static Bitmap dialogue_top_left;
	public static Bitmap dialogue_top;
	public static Bitmap dialogue_top_right;
	public static Bitmap dialogue_right;
	public static Bitmap dialogue_bottom_right;
	public static Bitmap dialogue_background;
	public static Bitmap dialogue_pointer;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Editor
	public static Bitmap error;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Floor
	public static Bitmap grass;
	public static Bitmap mt_ground;
	public static Bitmap path;
	public static Bitmap forestEntrance;
	public static Bitmap carpet_indoors;
	public static Bitmap carpet_outdoors;
	public static Bitmap hardwood_indoors;
	public static Bitmap tatami_1_indoors;
	public static Bitmap tatami_2_indoors;
	// ----------------------------------------------------------
	public static Bitmap stairs_left;
	public static Bitmap stairs_top;
	public static Bitmap stairs_right;
	public static Bitmap stairs_bottom;
	public static Bitmap stairs_mt_left;
	public static Bitmap stairs_mt_top;
	public static Bitmap stairs_mt_right;
	public static Bitmap stairs_mt_bottom;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// House
	public static Bitmap house_door;
	public static Bitmap house_bottom;
	public static Bitmap house_bottom_left;
	public static Bitmap house_bottom_right;
	public static Bitmap house_left;
	public static Bitmap house_left_windows_right;
	public static Bitmap house_center;
	public static Bitmap house_center_windows_center;
	public static Bitmap house_center_windows_left;
	public static Bitmap house_center_windows_right;
	public static Bitmap house_right;
	public static Bitmap house_right_windows_left;
	public static Bitmap house_roof_left;
	public static Bitmap house_roof_middle;
	public static Bitmap house_roof_right;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Inventory
	public static Bitmap inventory_gui;
	public static Bitmap inventory_backpack_potions;
	public static Bitmap inventory_backpack_keyItems;
	public static Bitmap inventory_backpack_pokeballs;
	public static Bitmap inventory_backpack_TM_HM;
	public static Bitmap inventory_backpack_unsorted;
	public static Bitmap inventory_tag_potions;
	public static Bitmap inventory_tag_keyItems;
	public static Bitmap inventory_tag_pokeballs;
	public static Bitmap inventory_tag_TM_HM;
	public static Bitmap inventory_tag_unsorted;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Ledge
	public static Bitmap ledge_bottom;
	public static Bitmap ledge_bottom_left;
	public static Bitmap ledge_left;
	public static Bitmap ledge_top_left;
	public static Bitmap ledge_top;
	public static Bitmap ledge_top_right;
	public static Bitmap ledge_right;
	public static Bitmap ledge_bottom_right;
	public static Bitmap Ledge_bottom_left_corner;
	public static Bitmap Ledge_bottom_right_corner;
	// ----------------------------------------------------------
	public static Bitmap ledge_mt_bottom;
	public static Bitmap ledge_mt_bottom_left;
	public static Bitmap ledge_mt_left;
	public static Bitmap ledge_mt_top_left;
	public static Bitmap ledge_mt_top;
	public static Bitmap ledge_mt_top_right;
	public static Bitmap ledge_mt_right;
	public static Bitmap ledge_mt_bottom_right;
	// ----------------------------------------------------------
	public static Bitmap ledge_inner_bottom;
	public static Bitmap ledge_inner_bottom_left;
	public static Bitmap ledge_inner_left;
	public static Bitmap ledge_inner_top_left;
	public static Bitmap ledge_inner_top;
	public static Bitmap ledge_inner_top_right;
	public static Bitmap ledge_inner_right;
	public static Bitmap ledge_inner_bottom_right;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Object (Items, Movable)
	public static Bitmap item;
	public static Bitmap bicycle;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Obstacle
	public static Bitmap smallTree;
	public static Bitmap logs;
	public static Bitmap planks;
	public static Bitmap scaffolding_left;
	public static Bitmap scaffolding_right;
	public static Bitmap sign;
	public static Bitmap workbench_left;
	public static Bitmap workbench_right;
	public static Bitmap deadSmallTree;

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Player (Entities)
	public static Bitmap[][] player;
	public static Bitmap[][] player_surf;
	public static Bitmap[][] player_bicycle;
	public static Bitmap shadow;

	// NPCs (Entities)
	public static Bitmap[][] joe;

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Areas
	public static Bitmap debugArea;
	public static Bitmap testArea;
	public static Bitmap testArea2;

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
		Art.dialogue_next = Bitmap.load("art/dialog/dialogue_next.png");
		Art.dialogue_bottom = Bitmap.load("art/dialog/dialogue_bottom.png");
		Art.dialogue_bottom_left = Bitmap.load("art/dialog/dialogue_bottom_left.png");
		Art.dialogue_left = Bitmap.load("art/dialog/dialogue_left.png");
		Art.dialogue_top_left = Bitmap.load("art/dialog/dialogue_top_left.png");
		Art.dialogue_top = Bitmap.load("art/dialog/dialogue_top.png");
		Art.dialogue_top_right = Bitmap.load("art/dialog/dialogue_top_right.png");
		Art.dialogue_right = Bitmap.load("art/dialog/dialogue_right.png");
		Art.dialogue_bottom_right = Bitmap.load("art/dialog/dialogue_bottom_right.png");
		Art.dialogue_background = Bitmap.load("art/dialog/dialogue_bg.png");
		Art.dialogue_pointer = Bitmap.load("art/dialog/dialogue_pointer.png");

		// Editor
		Art.error = Bitmap.load("art/editor/no_png.png");

		// Floor
		Art.grass = Bitmap.load("art/floor/grass.png");
		Art.mt_ground = Bitmap.load("art/floor/mt_ground.png");
		Art.forestEntrance = Bitmap.load("art/floor/forestEntrance.png");
		Art.path = Bitmap.load("art/floor/path.png");
		Art.stairs_left = Bitmap.load("art/floor/stairs_left.png");
		Art.stairs_top = Bitmap.load("art/floor/stairs_top.png");
		Art.stairs_right = Bitmap.load("art/floor/stairs_right.png");
		Art.stairs_bottom = Bitmap.load("art/floor/stairs_bottom.png");
		Art.stairs_mt_left = Bitmap.load("art/floor/stairs_mt_left.png");
		Art.stairs_mt_top = Bitmap.load("art/floor/stairs_mt_top.png");
		Art.stairs_mt_right = Bitmap.load("art/floor/stairs_mt_right.png");
		Art.stairs_mt_bottom = Bitmap.load("art/floor/stairs_mt_bottom.png");
		Art.carpet_indoors = Bitmap.load("art/floor/carpet_indoors.png");
		Art.carpet_outdoors = Bitmap.load("art/floor/carpet_outdoors.png");
		Art.hardwood_indoors = Bitmap.load("art/floor/hardwood_indoors.png");
		Art.tatami_1_indoors = Bitmap.load("art/floor/tatami_1_indoors.png");
		Art.tatami_2_indoors = Bitmap.load("art/floor/tatami_2_indoors.png");

		// House
		Art.house_door = Bitmap.load("art/house/house_door.png");
		Art.house_bottom = Bitmap.load("art/house/house_bottom.png");
		Art.house_bottom_left = Bitmap.load("art/house/house_bottom_left.png");
		Art.house_bottom_right = Bitmap.load("art/house/house_bottom_right.png");
		Art.house_center = Bitmap.load("art/house/house_center.png");
		Art.house_center_windows_center = Bitmap.load("art/house/house_center_windows_center.png");
		Art.house_center_windows_left = Bitmap.load("art/house/house_center_windows_left.png");
		Art.house_center_windows_right = Bitmap.load("art/house/house_center_windows_right.png");
		Art.house_left = Bitmap.load("art/house/house_left.png");
		Art.house_left_windows_right = Bitmap.load("art/house/house_left_windows_right.png");
		Art.house_right = Bitmap.load("art/house/house_right.png");
		Art.house_right_windows_left = Bitmap.load("art/house/house_right_windows_left.png");
		Art.house_roof_left = Bitmap.load("art/house/house_roof_left.png");
		Art.house_roof_middle = Bitmap.load("art/house/house_roof_middle.png");
		Art.house_roof_right = Bitmap.load("art/house/house_roof_right.png");

		// Inventory
		Art.inventory_gui = Bitmap.load("art/inventory/inventory_gui.png");
		Art.inventory_backpack_potions = Bitmap.load("art/inventory/backpack_potions.png");
		Art.inventory_backpack_keyItems = Bitmap.load("art/inventory/backpack_keyitems.png");
		Art.inventory_backpack_pokeballs = Bitmap.load("art/inventory/backpack_pokeballs.png");
		Art.inventory_backpack_TM_HM = Bitmap.load("art/inventory/backpack_tm_hm.png");
		Art.inventory_backpack_unsorted = Bitmap.load("art/inventory/backpack_unsorted.png");
		Art.inventory_tag_potions = Bitmap.load("art/inventory/potions.png");
		Art.inventory_tag_keyItems = Bitmap.load("art/inventory/keyitems.png");
		Art.inventory_tag_pokeballs = Bitmap.load("art/inventory/pokeballs.png");
		Art.inventory_tag_TM_HM = Bitmap.load("art/inventory/tm_hm.png");
		Art.inventory_tag_unsorted = Bitmap.load("art/inventory/unsorted.png");

		// Ledges
		Art.ledge_bottom = Bitmap.load("art/ledge/ledge_bottom.png");
		Art.ledge_bottom_left = Bitmap.load("art/ledge/ledge_bottom_left.png");
		Art.ledge_left = Bitmap.load("art/ledge/ledge_left.png");
		Art.ledge_top_left = Bitmap.load("art/ledge/ledge_top_left.png");
		Art.ledge_top = Bitmap.load("art/ledge/ledge_top.png");
		Art.ledge_top_right = Bitmap.load("art/ledge/ledge_top_right.png");
		Art.ledge_right = Bitmap.load("art/ledge/ledge_right.png");
		Art.ledge_bottom_right = Bitmap.load("art/ledge/ledge_bottom_right.png");
		Art.Ledge_bottom_left_corner = Bitmap.load("art/ledge/ledge_bottom_left_corner.png");
		Art.Ledge_bottom_right_corner = Bitmap.load("art/ledge/ledge_bottom_right_corner.png");
		Art.ledge_mt_bottom = Bitmap.load("art/ledge/ledge_mt_bottom.png");
		Art.ledge_mt_bottom_left = Bitmap.load("art/ledge/ledge_mt_bottom_left.png");
		Art.ledge_mt_left = Bitmap.load("art/ledge/ledge_mt_left.png");
		Art.ledge_mt_top_left = Bitmap.load("art/ledge/ledge_mt_top_left.png");
		Art.ledge_mt_top = Bitmap.load("art/ledge/ledge_mt_top.png");
		Art.ledge_mt_top_right = Bitmap.load("art/ledge/ledge_mt_top_right.png");
		Art.ledge_mt_right = Bitmap.load("art/ledge/ledge_mt_right.png");
		Art.ledge_mt_bottom_right = Bitmap.load("art/ledge/ledge_mt_bottom_right.png");
		Art.ledge_inner_bottom = Bitmap.load("art/ledge/ledge_inner_bottom.png");
		Art.ledge_inner_bottom_left = Bitmap.load("art/ledge/ledge_inner_bottom_left.png");
		Art.ledge_inner_left = Bitmap.load("art/ledge/ledge_inner_left.png");
		Art.ledge_inner_top_left = Bitmap.load("art/ledge/ledge_inner_top_left.png");
		Art.ledge_inner_top = Bitmap.load("art/ledge/ledge_inner_top.png");
		Art.ledge_inner_top_right = Bitmap.load("art/ledge/ledge_inner_top_right.png");
		Art.ledge_inner_right = Bitmap.load("art/ledge/ledge_inner_right.png");
		Art.ledge_inner_bottom_right = Bitmap.load("art/ledge/ledge_inner_bottom_right.png");

		// Object
		Art.item = Bitmap.load("art/object/item.png");
		Art.bicycle = Bitmap.load("art/object/bicycle.png");

		// Obstacle
		Art.logs = Bitmap.load("art/obstacle/logs.png");
		Art.planks = Bitmap.load("art/obstacle/planks.png");
		Art.scaffolding_left = Bitmap.load("art/obstacle/scaffolding_left.png");
		Art.scaffolding_right = Bitmap.load("art/obstacle/scaffolding_right.png");
		Art.smallTree = Bitmap.load("art/obstacle/small_tree.png");
		Art.sign = Bitmap.load("art/obstacle/sign.png");
		Art.workbench_left = Bitmap.load("art/obstacle/workbench_left.png");
		Art.workbench_right = Bitmap.load("art/obstacle/workbench_right.png");
		Art.deadSmallTree = Bitmap.load("art/obstacle/dead_small_tree.png");

		// Player
		Art.player = screen.cut("art/player/player.png", 16, 16, 0, 0);
		Art.player_surf = screen.cut("art/player/player_surf.png", 16, 16, 0, 0);
		Art.player_bicycle = screen.cut("art/player/player_bicycle.png", 16, 16, 0, 0);
		Art.shadow = Bitmap.load("art/player/shadow.png");

		// NPCs
		Art.joe = screen.cut("art/player/joe.png", 16, 16, 0, 0);

		// Areas (level areas for rapid iterations)
		Art.testArea = Bitmap.load("art/area/test/testArea.png");
		Art.testArea2 = Bitmap.load("art/area/test/testArea2.png");

		// Areas (debug area for testing only)
		Art.debugArea = Bitmap.load("art/area/test/debug.png");

		// Miscellaneous
		Art.font = Art.loadFont("art/font/font.ttf");
	}

	private static Bitmap[] loadAnimation(Scene screen, int frames, String filename) {
		Bitmap[] result = new Bitmap[frames];
		for (int i = 0; i < frames; i++) {
			// There are 16 frames for the water.
			if (i < 10) {
				result[i] = Bitmap.load(filename + "0" + String.valueOf(i) + ".png");
			}
			else {
				result[i] = Bitmap.load(filename + String.valueOf(i) + ".png");
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
		catch (FontFormatException | IOException e) {
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
				catch (FontFormatException | IOException e2) {
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

	public static Bitmap changeColors(Bitmap bitmap, int color) {
		Bitmap result = new Bitmap(bitmap.getWidth(), bitmap.getHeight());
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
