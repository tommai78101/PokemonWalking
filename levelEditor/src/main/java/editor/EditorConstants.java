/**
 * Open-source Game Boy inspired game.
 *
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo.
 */

package editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import common.Debug;
import common.Tileable;
import enums.DataType;
import enums.ScriptTags;
import level.WorldConstants;

public class EditorConstants {
	// TODO: Add additional pixel data properties that can be edited/modified for
	// the area.
	private final List<Category> categories = new ArrayList<>();
	private final List<Map.Entry<Integer, SpriteData>> datas = new ArrayList<>();
	private final List<Map.Entry<Integer, SpriteData>> npcs = new ArrayList<>();
	private final List<Trigger> triggers = new ArrayList<>();

	private static final EditorConstants instance = new EditorConstants();
	private static final int KeyColor_Alpha_NPC = 0x0E;

	public static final Color GRASS_GREEN = new Color(164, 231, 103);
	public static final Color ROAD_WHITE = new Color(255, 244, 201);
	public static final Color DIRT_SIENNA = new Color(202, 143, 3);
	public static final Color WATER_BLUE = new Color(0, 65, 255);

	public enum Tools {
		ControlPanel,
		Properties
	}

	/**
	 * Level Editor Metadata Layers. Think of Google Maps information overlays, with each overlay
	 * depicting relevant information based on what layer was selected.
	 *
	 * @author tlee
	 */
	public enum Metadata {
		Tilesets("Pixel Data"),
		Triggers("Triggers"),
		NonPlayableCharacters("NPCs");

		private String name;

		Metadata(String text) {
			this.name = text;
		}

		public String getName() {
			return this.name;
		}
	}

	public static Tools chooser = Tools.ControlPanel;
	public static Metadata metadata = Metadata.Tilesets;

	private EditorConstants() {
		this.loadTilesetData();
		this.loadTriggers();
	}

	private void loadTilesetData() {
		try {
			BufferedReader reader = new BufferedReader(
				new InputStreamReader(
					EditorConstants.class.getClassLoader().getResourceAsStream("art/editor/data.txt")
				)
			);
			String line;
			String[] tokens;
			int categoryID = 0;
			int editorID = 0;
			Category categoryTemp = null;
			List<SpriteData> dataTemp = new ArrayList<>();
			List<SpriteData> npcDataTemp = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#"))
					continue;
				else if (line.startsWith("-")) {
					tokens = line.replace("\\W", "").replace("_", " ").split("-");
					if (!dataTemp.isEmpty() && categoryTemp != null) {
						categoryTemp.nodes.addAll(dataTemp);
						categoryID = categoryTemp.setIdByData(dataTemp.get(0));
						this.categories.add(categoryTemp);
						dataTemp.clear();
					}
					categoryTemp = new Category(tokens[1], categoryID);
				}
				else if (line.startsWith("%")) {
					SpriteData data = new SpriteData();
					// In the art/editor/data.txt, we use the last bit of data, the area type inclusion flag '*', to
					// filter and specify which area type the tileset belongs in.

					if (line.contains("*")) {
						data.areaTypeIncluded = true;
						// By default, the data type specified as NULL, means the alpha, red, green, and blue values are
						// used for data integrity checks.
						DataType type = null;
						short i = 0;
						byte count = 0;
						while (line.charAt(i) != '*') {
							if (line.charAt(i) == '%') {
								count++;
								type = switch (count) {
									case 2 -> DataType.ALPHA;
									case 3 -> DataType.RED;
									case 4 -> DataType.GREEN;
									case 5 -> DataType.BLUE;
									default -> DataType.NONE;
								};
							}
							i++;
						}
						data.areaTypeIDType = type;
						line = line.replace('*', '@');
					}
					if (line.contains("!")) {
						data.alphaByEditor = true;
						line = line.replaceAll("!+", "00");
					}
					if (line.contains("^")) {
						data.redByEditor = true;
						line = line.replaceAll("^+", "00");
					}
					if (line.contains("`")) {
						data.greenByEditor = true;
						line = line.replaceAll("`+", "00");
					}
					if (line.contains("=")) {
						data.blueByEditor = true;
						line = line.replaceAll("=+", "00");
					}
					tokens = line.trim().replaceAll("\\s+", "").replaceAll("@", "00").split("%");
					data.name = tokens[1].replace('_', ' ');
					data.alpha = Integer.parseInt(tokens[2], 16);
					data.red = Integer.parseInt(tokens[3], 16);
					data.green = Integer.parseInt(tokens[4], 16);
					data.blue = Integer.parseInt(tokens[5], 16);
					data.filepath = tokens[6];
					data.editorID = editorID++;
					data.image = new ImageIcon(
						EditorConstants.class.getClassLoader().getResource(tokens[6].split("res/")[1])
					);
					data.button = new JButton(data.image) {
						private static final long serialVersionUID = 1L;

						@Override
						public Dimension getMinimumSize() {
							return new Dimension(Tileable.WIDTH, Tileable.HEIGHT);
						}

						@Override
						public Dimension getSize() {
							return new Dimension(Tileable.WIDTH, Tileable.HEIGHT);
						}

						@Override
						public Dimension getPreferredSize() {
							return new Dimension(Tileable.WIDTH, Tileable.HEIGHT);
						}
					};
					data.button.setAlignmentX(Component.CENTER_ALIGNMENT);
					data.button.setMargin(new Insets(0, 0, 0, 0));
					data.button.setBorder(null);
					this.datas.add(Map.entry(data.getColorValue(), data));
					dataTemp.add(data);

					if (data.alpha == EditorConstants.KeyColor_Alpha_NPC) {
						npcDataTemp.add(data);
					}
				}
				else if (line.startsWith("+")) {
					categoryTemp.nodes.addAll(dataTemp);
					this.categories.add(categoryTemp);
					dataTemp.clear();

					npcDataTemp.stream()
						.forEach(
							data -> {
								this.npcs.add(Map.entry(data.getColorValue(), data));
							}
						);
					npcDataTemp.clear();
				}
			}

			Collections.sort(this.datas, (d1, d2) -> {
				if (d1.getValue().editorID < d2.getValue().editorID)
					return -1;
				if (d1.getValue().editorID > d2.getValue().editorID)
					return 1;
				return 0;
			});

			Collections.sort(this.npcs, (d1, d2) -> {
				if (d1.getValue().editorID < d2.getValue().editorID)
					return -1;
				if (d1.getValue().editorID > d2.getValue().editorID)
					return 1;
				return 0;
			});

			Collections.sort(this.categories, (d1, d2) -> {
				if (d1.id < d2.id)
					return -1;
				if (d1.id > d2.id)
					return 1;
				return 0;
			});

		}
		catch (

			Exception e
		) {
			e.printStackTrace();
		}
	}

	private void loadTriggers() {
		URL uri = EditorConstants.class.getResource(WorldConstants.ScriptsDefaultPath);
		try {
			final File[] directory = new File(uri.toURI()).listFiles();
			for (File f : directory) {
				if (f.getName().endsWith(".script")) {
					this.loadTriggers(WorldConstants.ScriptsDefaultPath + File.separator + f.getName());
				}
			}
		}
		catch (URISyntaxException e) {
			Debug.error("Unable to load trigger scripts for the level editor.", e);
		}
	}

	private void loadTriggers(String filename) {
		try (
			BufferedReader reader = new BufferedReader(
				new InputStreamReader(
					EditorConstants.class.getResourceAsStream(filename)
				)
			)
		) {
			String line;
			Trigger trigger = null;
			String checksum = null;
			while ((line = reader.readLine()) != null) {
				if (ScriptTags.Comment.beginsAt(line)) {}
				else if (checksum == null && ScriptTags.Checksum.beginsAt(line)) {
					checksum = ScriptTags.Checksum.removeScriptTag(line);
				}
				else if (ScriptTags.BeginScript.beginsAt(line)) {
					// This is where the script begins. The proceeding number is the trigger ID value.
					if (trigger == null)
						trigger = new Trigger();
					if (checksum != null)
						trigger.setChecksum(checksum);
					int value = Integer.parseInt(ScriptTags.BeginScript.removeScriptTag(line));
					if (value != 0) {
						trigger.setTriggerID((short) (value & 0xFFFF));
					}
					else {
						// Ignore the trigger value that's equal to 0. This is the Eraser.
					}
				}
				else if (ScriptTags.NpcScript.beginsAt(line)) {
					// This is where the script begins. The proceeding number is the trigger ID value.
					if (trigger == null)
						trigger = new Trigger();
					if (checksum != null)
						trigger.setChecksum(checksum);
					int value = Integer.parseInt(ScriptTags.NpcScript.removeScriptTag(line));
					if (value != 0) {
						trigger.setNpcTriggerID((short) (value & 0xFFFF));
					}
					else {
						// Ignore the trigger value that's equal to 0. This is the Eraser.
					}
				}
				else if (ScriptTags.ScriptName.beginsAt(line)) {
					// This is the trigger script name.
					if (trigger != null)
						trigger.setName(ScriptTags.ScriptName.removeScriptTag(line));
				}
				else if (ScriptTags.EndScript.beginsAt(line)) {
					// This is the delimiter / end of the trigger script section.
					this.triggers.add(trigger);
					trigger = null;
				}
			}
		}
		catch (Exception e) {
			Debug.error("Unhandled exception when loading level editor triggers.", e);
		}
	}

	public static EditorConstants getInstance() {
		return EditorConstants.instance;
	}

	public List<Category> getCategories() {
		return this.categories;
	}

	public List<Map.Entry<Integer, SpriteData>> getDatas() {
		return this.datas;
	}

	public List<Map.Entry<Integer, SpriteData>> getNpcs() {
		return this.npcs;
	}

	public List<Trigger> getTriggers() {
		return this.triggers;
	}

	public static SpriteData getData(int alpha, int red, int green, int blue) {
		List<Map.Entry<Integer, SpriteData>> dataList = EditorConstants.getInstance().datas.stream().filter(entry -> {
			SpriteData d = entry.getValue();
			if (d.areaTypeIncluded) {
				// Area type ID is value used in the data value. We want to exclude this when doing comparison
				// checks. Data integrity checks is done here.
				switch (d.areaTypeIDType) {
					case ALPHA:
						return (d.red == red && d.green == green && d.blue == blue);
					case RED:
						return (d.alpha == alpha && d.green == green && d.blue == blue);
					case GREEN:
						return (d.alpha == alpha && d.red == red && d.blue == blue);
					case BLUE:
						return (d.alpha == alpha && d.red == red && d.green == green);
					case NONE:
					default:
						return (d.alpha == alpha && d.red == red && d.green == green && d.blue == blue);
				}
			}
			else {
				return (d.alpha == alpha && d.red == red && d.green == green && d.blue == blue);
			}
		}).collect(Collectors.toList());
		if (dataList.isEmpty())
			return EditorConstants.getInstance().getDatas().get(0).getValue();
		return dataList.get(0).getValue();
	}

	public static SpriteData getData(int colorValue) {
		int alpha = (colorValue & 0xFF000000) >> 24;
		int red = (colorValue & 0xFF0000) >> 16;
		int green = (colorValue & 0xFF00) >> 8;
		int blue = (colorValue & 0xFF);
		return EditorConstants.getData(alpha, red, green, blue);
	}
}
