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
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import common.Tileable;
import enums.ScriptTags;

public class EditorConstants {
	// TODO: Add additional pixel data properties that can be edited/modified for
	// the area.
	private final List<Category> categories = new ArrayList<>();
	private final List<Map.Entry<Integer, Data>> datas = new ArrayList<>();
	private final List<Trigger> triggers = new ArrayList<>();

	private static final EditorConstants instance = new EditorConstants();

	public static final Color GRASS_GREEN = new Color(164, 231, 103);
	public static final Color ROAD_WHITE = new Color(255, 244, 201);
	public static final Color DIRT_SIENNA = new Color(202, 143, 3);
	public static final Color WATER_BLUE = new Color(0, 65, 255);

	public static enum Tools {
		ControlPanel,
		Properties
	}

	public static enum Metadata {
		Pixel_Data("Pixel Data"),
		Triggers("Triggers");

		private String name;

		private Metadata(String text) {
			this.name = text;
		}

		public String getName() {
			return this.name;
		}
	}

	public static Tools chooser = Tools.ControlPanel;
	public static Metadata metadata = Metadata.Pixel_Data;

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
			Category c = null;
			List<Data> temp = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#"))
					continue;
				else if (line.startsWith("-")) {
					tokens = line.replace("\\W", "").replace("_", " ").split("-");
					if (!temp.isEmpty() && c != null) {
						c.nodes.addAll(temp);
						categoryID = c.setIdByData(temp.get(0));
						this.categories.add(c);
						temp.clear();
					}
					c = new Category(tokens[1], categoryID);
				}
				else if (line.startsWith("%")) {
					Data data = new Data();
					// In the art/editor/data.txt, we use the last bit of data, the area type inclusion flag '*', to
					// filter and specify which area type the tileset belongs in.

					if (line.contains("*")) {
						data.areaTypeIncluded = true;
						// By default, the data type specified as NULL, means the alpha, red, green, and blue values are
						// used for data integrity checks.
						Data.DataType type = null;
						short i = 0;
						byte count = 0;
						while (line.charAt(i) != '*') {
							if (line.charAt(i) == '%') {
								count++;
								switch (count) {
									case 2:
										// If data type is ALPHA, we only need to data integrity check red, green, and blue values.
										type = Data.DataType.ALPHA;
										break;
									case 3:
										// If data type is RED, we only need to data integrity check alpha, green, and blue values.
										type = Data.DataType.RED;
										break;
									case 4:
										// If data type is GREEN, we only need to data integrity check alpha, red, and blue values.
										type = Data.DataType.GREEN;
										break;
									case 5:
										// If data type is BLUE, we only need to data integrity check alpha, red, and green values.
										type = Data.DataType.BLUE;
										break;
									default:
										// Data integrity check all values.
										type = Data.DataType.NONE;
										break;
								}
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
					data.alpha = Integer.valueOf(tokens[2], 16);
					data.red = Integer.valueOf(tokens[3], 16);
					data.green = Integer.valueOf(tokens[4], 16);
					data.blue = Integer.valueOf(tokens[5], 16);
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
					this.datas.add(new AbstractMap.SimpleEntry<>(data.getColorValue(), data));
					temp.add(data);
				}
				else if (line.startsWith("+")) {
					c.nodes.addAll(temp);
					this.categories.add(c);
					temp.clear();
				}
			}

			Collections.sort(this.datas, new Comparator<Map.Entry<Integer, Data>>() {

				@Override
				public int compare(Map.Entry<Integer, Data> d1, Map.Entry<Integer, Data> d2) {
					if (d1.getValue().editorID < d2.getValue().editorID)
						return -1;
					if (d1.getValue().editorID > d2.getValue().editorID)
						return 1;
					return 0;
				}
			});

			Collections.sort(this.categories, new Comparator<Category>() {
				@Override
				public int compare(Category d1, Category d2) {
					if (d1.id < d2.id)
						return -1;
					if (d1.id > d2.id)
						return 1;
					return 0;
				}
			});

		}
		catch (

			Exception e
		) {
			e.printStackTrace();
		}
	}

	private void loadTriggers() {
		try {
			BufferedReader reader = new BufferedReader(
				new InputStreamReader(
					EditorConstants.class.getClassLoader().getResourceAsStream("art/script/scripts.txt")
				)
			);
			String line;
			Trigger trigger = null;
			while ((line = reader.readLine()) != null) {
				if (ScriptTags.Comment.beginsAt(line)) {
					// This is a comment.
					continue;
				}
				else if (ScriptTags.BeginScript.beginsAt(line)) {
					// This is where the script begins. The proceeding number is the trigger ID value.
					if (trigger == null)
						trigger = new Trigger();
					int value = Integer.valueOf(ScriptTags.BeginScript.removeScriptTag(line));
					if (value != 0) {
						trigger.setTriggerID((short) (value & 0xFFFF));
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
			e.printStackTrace();
		}
	}

	public static EditorConstants getInstance() {
		return EditorConstants.instance;
	}

	public List<Category> getCategories() {
		return this.categories;
	}

	public List<Map.Entry<Integer, Data>> getDatas() {
		return this.datas;
	}

	public List<Trigger> getTriggers() {
		return this.triggers;
	}

	public static Data getData(int alpha, int red, int green, int blue) {
		List<Map.Entry<Integer, Data>> dataList = EditorConstants.getInstance().datas.stream().filter(entry -> {
			Data d = entry.getValue();
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

	public static Data getData(int colorValue) {
		int alpha = (colorValue & 0xFF000000) >> 24;
		int red = (colorValue & 0xFF0000) >> 16;
		int green = (colorValue & 0xFF00) >> 8;
		int blue = (colorValue & 0xFF);
		return EditorConstants.getData(alpha, red, green, blue);
	}
}
