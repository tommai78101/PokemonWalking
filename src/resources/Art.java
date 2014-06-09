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
	public static BaseBitmap testArea4;
	public static BaseBitmap testArea_debug;
	
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
		dialogue_next = screen.load("art/dialog/dialogue_next.png");
		dialogue_bottom = screen.load("art/dialog/dialogue_bottom.png");
		dialogue_bottom_left = screen.load("art/dialog/dialogue_bottom_left.png");
		dialogue_left = screen.load("art/dialog/dialogue_left.png");
		dialogue_top_left = screen.load("art/dialog/dialogue_top_left.png");
		dialogue_top = screen.load("art/dialog/dialogue_top.png");
		dialogue_top_right = screen.load("art/dialog/dialogue_top_right.png");
		dialogue_right = screen.load("art/dialog/dialogue_right.png");
		dialogue_bottom_right = screen.load("art/dialog/dialogue_bottom_right.png");
		dialogue_background = screen.load("art/dialog/dialogue_bg.png");
		dialogue_pointer = screen.load("art/dialog/dialogue_pointer.png");
		
		// Editor
		error = screen.load("art/editor/no_png.png");
		
		// Floor
		grass = screen.load("art/floor/grass.png");
		mt_ground = screen.load("art/floor/mt_ground.png");
		forestEntrance = screen.load("art/floor/forestEntrance.png");
		path = screen.load("art/floor/path.png");
		stairs_left = screen.load("art/floor/stairs_left.png");
		stairs_top = screen.load("art/floor/stairs_top.png");
		stairs_right = screen.load("art/floor/stairs_right.png");
		stairs_bottom = screen.load("art/floor/stairs_bottom.png");
		stairs_mt_left = screen.load("art/floor/stairs_mt_left.png");
		stairs_mt_top = screen.load("art/floor/stairs_mt_top.png");
		stairs_mt_right = screen.load("art/floor/stairs_mt_right.png");
		stairs_mt_bottom = screen.load("art/floor/stairs_mt_bottom.png");
		carpet_indoors = screen.load("art/floor/carpet_indoors.png");
		carpet_outdoors = screen.load("art/floor/carpet_outdoors.png");
		hardwood_indoors = screen.load("art/floor/hardwood_indoors.png");
		tatami_1_indoors = screen.load("art/floor/tatami_1_indoors.png");
		tatami_2_indoors = screen.load("art/floor/tatami_2_indoors.png");
		
		// House
		house_door = screen.load("art/house/house_door.png");
		house_bottom = screen.load("art/house/house_bottom.png");
		house_bottom_left = screen.load("art/house/house_bottom_left.png");
		house_left = screen.load("art/house/house_left.png");
		house_left_windows_right = screen.load("art/house/house_left_windows_right.png");
		house_right = screen.load("art/house/house_right.png");
		house_right_windows_left = screen.load("art/house/house_right_windows_left.png");
		house_center = screen.load("art/house/house_center.png");
		house_center_windows_center = screen.load("art/house/house_center_windows_center.png");
		house_center_windows_left = screen.load("art/house/house_center_windows_left.png");
		house_center_windows_right = screen.load("art/house/house_center_windows_right.png");
		house_bottom_right = screen.load("art/house/house_bottom_right.png");
		house_roof_left = screen.load("art/house/house_roof_left.png");
		house_roof_middle = screen.load("art/house/house_roof_middle.png");
		house_roof_right = screen.load("art/house/house_roof_right.png");
		
		// Inventory
		inventory_gui = screen.load("art/inventory/inventory_gui.png");
		inventory_backpack_potions = screen.load("art/inventory/backpack_potions.png");
		inventory_backpack_keyItems = screen.load("art/inventory/backpack_keyitems.png");
		inventory_backpack_pokeballs = screen.load("art/inventory/backpack_pokeballs.png");
		inventory_backpack_TM_HM = screen.load("art/inventory/backpack_tm_hm.png");
		inventory_tag_potions = screen.load("art/inventory/potions.png");
		inventory_tag_keyItems = screen.load("art/inventory/keyitems.png");
		inventory_tag_pokeballs = screen.load("art/inventory/pokeballs.png");
		inventory_tag_TM_HM = screen.load("art/inventory/tm_hm.png");
		
		// Ledges
		ledge_bottom = screen.load("art/ledge/ledge_bottom.png");
		ledge_bottom_left = screen.load("art/ledge/ledge_bottom_left.png");
		ledge_left = screen.load("art/ledge/ledge_left.png");
		ledge_top_left = screen.load("art/ledge/ledge_top_left.png");
		ledge_top = screen.load("art/ledge/ledge_top.png");
		ledge_top_right = screen.load("art/ledge/ledge_top_right.png");
		ledge_right = screen.load("art/ledge/ledge_right.png");
		ledge_bottom_right = screen.load("art/ledge/ledge_bottom_right.png");
		ledge_mt_bottom = screen.load("art/ledge/ledge_mt_bottom.png");
		ledge_mt_bottom_left = screen.load("art/ledge/ledge_mt_bottom_left.png");
		ledge_mt_left = screen.load("art/ledge/ledge_mt_left.png");
		ledge_mt_top_left = screen.load("art/ledge/ledge_mt_top_left.png");
		ledge_mt_top = screen.load("art/ledge/ledge_mt_top.png");
		ledge_mt_top_right = screen.load("art/ledge/ledge_mt_top_right.png");
		ledge_mt_right = screen.load("art/ledge/ledge_mt_right.png");
		ledge_mt_bottom_right = screen.load("art/ledge/ledge_mt_bottom_right.png");
		ledge_inner_bottom = screen.load("art/ledge/ledge_inner_bottom.png");
		ledge_inner_bottom_left = screen.load("art/ledge/ledge_inner_bottom_left.png");
		ledge_inner_left = screen.load("art/ledge/ledge_inner_left.png");
		ledge_inner_top_left = screen.load("art/ledge/ledge_inner_top_left.png");
		ledge_inner_top = screen.load("art/ledge/ledge_inner_top.png");
		ledge_inner_top_right = screen.load("art/ledge/ledge_inner_top_right.png");
		ledge_inner_right = screen.load("art/ledge/ledge_inner_right.png");
		ledge_inner_bottom_right = screen.load("art/ledge/ledge_inner_bottom_right.png");
		
		// Object
		item = screen.load("art/object/item.png");
		
		// Obstacle
		logs = screen.load("art/obstacle/logs.png");
		planks = screen.load("art/obstacle/planks.png");
		scaffolding_left= screen.load("art/obstacle/scaffolding_left.png");
		scaffolding_right= screen.load("art/obstacle/scaffolding_right.png");
		smallTree = screen.load("art/obstacle/small_tree.png");
		sign = screen.load("art/obstacle/sign.png");
		
		// Player, NPCs
		player = screen.cut("art/player/player.png", 16, 16, 0, 0);
		player_surf = screen.cut("art/player/player_surf.png", 16, 16, 0, 0);
		player_bicycle = screen.cut("art/player/player_bicycle.png", 16, 16, 0, 0);
		shadow = screen.load("art/player/shadow.png");
		
		// Areas
		testArea = screen.load("area/test/testArea.png");
		testArea2 = screen.load("area/test/testArea2.png");
		testArea3 = screen.load("area/test/testArea3.png");
		testArea4 = screen.load("area/test/testArea4.png");
		testArea_debug = screen.load("area/test/testArea_debug.png");
		
		// Miscellaneous
		font = loadFont("font/font.ttf");
		
	}
	
	private static BaseBitmap[] loadAnimation(BaseScreen screen, int frames, String filename) {
		BaseBitmap[] result = new BaseBitmap[frames];
		for (int i = 0; i < frames; i++) {
			// There are 16 frames for the water.
			if (i < 10) {
				result[i] = screen.load(filename + "0" + String.valueOf(i) + ".png");
			}
			else {
				result[i] = screen.load(filename + String.valueOf(i) + ".png");
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
		for (; urls.hasMoreElements();) {
			url = urls.nextElement();
			System.out.println(url.toString());
		}
		
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
		}
		return result.deriveFont(Font.PLAIN, 8f * MainComponent.GAME_SCALE);
	}
	
	public static BaseBitmap changeColors(BaseBitmap bitmap, int color, int alphaColor) {
		BaseBitmap result = new BaseBitmap(bitmap.getWidth(), bitmap.getHeight());
		int[] pixels = bitmap.getPixels();
		int[] resultPixels = result.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			int alpha = (pixels[i] >> 24) & 0xFF;
			// May be possible this will expand in the future.
			switch (alpha) {
				case 0x01:
					resultPixels[i] = BaseScreen.lighten(color, 0.2f);
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
