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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import screen.BaseBitmap;
import screen.Scene;

public class Mod {
	private static final String[] names = new String[] { "area", "script" };
	public static List<Map.Entry<BaseBitmap, Integer>> moddedAreas = new ArrayList<Map.Entry<BaseBitmap, Integer>>();
	// public static List<Map.Entry<Script, Integer>> moddedScripts = new
	// ArrayList<Map.Entry<Script, Integer>>();
	private static boolean hasLoaded = false;

	private static Comparator<File> ALPHABETICAL_ORDER = new Comparator<File>() {
		@Override
		public int compare(File file1, File file2) {
			String str1 = file1.getName();
			String str2 = file2.getName();
			int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
			return (res != 0) ? res : str1.compareTo(str2);
		}
	};

	public static void resetLoading() {
		Mod.hasLoaded = false;
	}

	public static void loadModdedResources() {
		if (Mod.hasLoaded)
			return;
		Mod.initialization();
		File directory = new File("mod");
		if (directory.exists()) {
			List<File> list = Mod.getMapContents(directory);
			Collections.sort(list, ALPHABETICAL_ORDER);
			int id = 1;
			for (File f : list) {
				Mod.moddedAreas.add(new AbstractMap.SimpleEntry<BaseBitmap, Integer>(Scene.load(f), id + 1000));
				id++;
			}
			// ArrayList<Script> moddedScripts = Script.loadModdedScripts();
			// id = 1;
			// for (Script s : moddedScripts){
			// Mod.moddedScripts.add(new AbstractMap.SimpleEntry<Script, Integer>(s, id++));
			// }
		} else
			throw new RuntimeException("Something is wrong with detecting the mod folder.");
		Mod.hasLoaded = true;
	}

	/**
	 * <p>
	 * Initializes the "mod" folder directory if the game doesn't see it. Even if
	 * the game sees the folder, if the contents of the folder and its sub-folders
	 * are missing, it will recreate the basic necessary contents. This is
	 * deliberate and intentionally designed, unless re-developed.
	 * </p>
	 * 
	 * @return Nothing.
	 */
	private static void initialization() {
		File directory = new File("mod");
		if (!directory.exists())
			directory.mkdir();
		if (directory.exists()) {
			for (int namesIterator = 0; namesIterator < names.length; namesIterator++) {
				File folder = new File(directory.getPath() + File.separator + names[namesIterator]);
				if (!folder.exists())
					folder.mkdir();
				if (folder.exists()) {
					switch (namesIterator) {
					case 0: {// area
						File text = new File(folder.getPath() + File.separator + "readme.txt");
						if (!text.isFile()) {
							BufferedWriter writer = null;
							try {
								writer = new BufferedWriter(
										new OutputStreamWriter(new FileOutputStream(text), "utf-8"));
								writer.write("This is a readme generated by Java code.");
								writer.newLine();
								writer.newLine();
								writer.write(
										"Put your custom maps in this folder, and start the game. This will allow the game to load your");
								writer.newLine();
								writer.write(
										"custom maps into the game, and take advantage of the editor's basic capabilities. But, the game");
								writer.newLine();
								writer.write(
										"will then no longer load the default maps that were there, unless all of the custom maps have");
								writer.newLine();
								writer.write("been removed, or the game has been placed in a separate folder.");
								writer.newLine();
								writer.newLine();
								writer.write(
										"All custom maps must be created and saved by the editor. The file format of the maps is PNG.");
								writer.newLine();
								writer.newLine();
								writer.write(
										"Note that the game first sorts your maps in alphabetical order, then apply IDs according to");
								writer.newLine();
								writer.write("the order of the sorted maps, from first to last. Here's an example:");
								writer.newLine();
								writer.newLine();
								writer.write("\"my_area.png\", \"my_cave.png\"");
								writer.newLine();
								writer.newLine();
								writer.write(
										"The first map has the ID of 1, the second map has the ID of 2. If there are more maps, it");
								writer.newLine();
								writer.write(
										"continues counting up. 3, 4, 5, and so on. Use these generated IDs for your editor, if you");
								writer.newLine();
								writer.write(
										"want to have area connections in the OverWorld. Please read the specifications hosted at");
								writer.newLine();
								writer.write(
										"GitHub (link down below) for more information on applying area connections in the level editor.");
								writer.newLine();
								writer.newLine();
								writer.write(
										"Thanks for reading. If you appreciated this game, you may leave a message on the forums below:");
								writer.newLine();
								writer.newLine();
								writer.write(
										"The Helper Forums: http://www.thehelper.net/threads/java-pok%C3%A9mon-walking-algorithm.159287/");
								writer.newLine();
								writer.write(
										"Java-Gaming.org: http://www.java-gaming.org/topics/pok-mon-walking/32546/view.html");
								writer.newLine();
								writer.write("GitHub Project Page: http://github.com/tommai78101/PokemonWalking");
								writer.newLine();
								writer.newLine();
								writer.write("All messages are welcomed. We (developers) loved to read feedbacks!");
							} catch (Exception e) {
							} finally {
								try {
									writer.close();
								} catch (Exception e) {
								}
							}
						}
						break;
					}
					case 1: { // script
						File text = new File(folder.getPath() + File.separator + "readme.txt");
						if (!text.isFile()) {
							BufferedWriter writer = null;
							try {
								writer = new BufferedWriter(
										new OutputStreamWriter(new FileOutputStream(text), "utf-8"));
								writer.write("This is a readme generated by Java code.");
								writer.newLine();
								writer.newLine();
								writer.write(
										"Put your scripts in this folder, and start the game. This will allow the game to load your");
								writer.newLine();
								writer.write(
										"custom scripts into the game. But, the game will then no longer load the default scripts, unless");
								writer.newLine();
								writer.write(
										"all of the custom scripts have been removed, or the game has been placed in a separate folder.");
								writer.newLine();
								writer.newLine();
								writer.write(
										"All custom scripts must follow the layout set forth by the documentation. It is strictly");
								writer.newLine();
								writer.write(
										"adhered to the game codes, which makes it more difficult to modify and edit.");
								writer.newLine();
								writer.newLine();
								writer.write("------");
								writer.newLine();
								writer.newLine();
								writer.write("Automation Script Format:");
								writer.newLine();
								writer.newLine();
								writer.write(
										"Entities that can walk, or run, must be required to have movements for the game");
								writer.write(
										"to feel lively.                                                                ");
								writer.newLine();
								writer.newLine();
								writer.write("More commands to come.");
								writer.newLine();
								writer.newLine();
								writer.write("_: Whitespaces.");
								writer.newLine();
								writer.write("@: Trigger name.");
								writer.newLine();
								writer.write("^: [Direction, Steps]. Can be chained for delaying scripted movements.");
								writer.newLine();
								writer.write(
										"$: Start of script. Appears at beginning of script. Uses numeric ID values as Trigger IDs");
								writer.newLine();
								writer.write("%: Script delimiter. Always appear at end of script.");
								writer.newLine();
								writer.write("#: Speech Dialogue.");
								writer.newLine();
								writer.write(
										"/: Comments. Whole line gets ignored, even if it's somewhere in the middle of the line.");
								writer.newLine();
								writer.write("?: Question Dialogue.");
								writer.newLine();
								writer.write("+: Affirmative dialogue.");
								writer.newLine();
								writer.write("-: Negative dialogue.");
								writer.newLine();
								writer.write("[: Affirmative Action.");
								writer.newLine();
								writer.write("]: Negative Action.");
								writer.newLine();
								writer.write(";: Repeat Flag. If contains ';', it means it's enabled by default.");
								writer.newLine();
								writer.newLine();
								writer.write("Example:");
								writer.newLine();
								writer.newLine();
								writer.write("$0");
								writer.newLine();
								writer.write("@Eraser");
								writer.newLine();
								writer.write("%");
							} catch (Exception e) {
							} finally {
								try {
									writer.close();
								} catch (Exception e) {
								}
							}
						}
						break;
					}
					default: {
						break;
					}
					}
				}
			}
		}
	}

	private static List<File> getMapContents(File file) {
		List<File> results = new ArrayList<File>();
		if (file != null && file.exists()) {
			if (file.isDirectory()) {
				File[] list = file.listFiles();
				for (int i = 0; i < list.length; i++) {
					results.addAll(Mod.getMapContents(list[i]));
				}
			} else {
				String filepath = file.getPath();
				int dotCount = filepath.lastIndexOf('.');
				int pathDotCount = Math.max(filepath.lastIndexOf('/'), filepath.lastIndexOf('\\'));
				if (dotCount > pathDotCount) {
					if (filepath.substring(dotCount + 1).toLowerCase().equals("png"))
						results.add(file);
				}
			}
		}
		return results;
	}

	@SuppressWarnings("unused")
	private static int getFilesCount(File file) {
		File[] files = file.listFiles();
		int count = 0;
		for (File f : files)
			if (f.isDirectory())
				count += getFilesCount(f);
			else
				count++;
		return count;
	}
}
