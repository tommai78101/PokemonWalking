/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

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
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import entity.Tile;

public class EditorConstants {
	// TODO: Add additional pixel data properties that can be edited/modified for
	// the area.
	private final ArrayList<Category> categories = new ArrayList<Category>();
	private final ArrayList<Map.Entry<Integer, Data>> datas = new ArrayList<Map.Entry<Integer, Data>>();
	private final ArrayList<Trigger> triggers = new ArrayList<Trigger>();

	private static final EditorConstants instance = new EditorConstants();

	public static final Color GRASS_GREEN = new Color(164, 231, 103);
	public static final Color ROAD_WHITE = new Color(255, 244, 201);
	public static final Color DIRT_SIENNA = new Color(202, 143, 3);
	public static final Color WATER_BLUE = new Color(0, 65, 255);

	public static enum Tools {
		ControlPanel, Properties
	};

	public static enum Metadata {
		Pixel_Data("Pixel Data"), Triggers("Triggers");

		private String name;

		private Metadata(String text) {
			this.name = text;
		}

		public String getName() {
			return this.name;
		}
	};

	public static Tools chooser = Tools.ControlPanel;
	public static Metadata metadata = Metadata.Pixel_Data;

	private EditorConstants() {
		loadTilesetData();
		loadTriggers();
	}

	private void loadTilesetData() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					EditorConstants.class.getClassLoader().getResourceAsStream("art/editor/data.txt")));
			String line;
			String[] tokens;
			int categoryID = 0;
			int editorID = 0;
			Category c = null;
			ArrayList<Data> temp = new ArrayList<Data>();
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#"))
					continue;
				else if (line.startsWith("-")) {
					tokens = line.replace("\\W", "").replace("_", " ").split("-");
					if (!temp.isEmpty() && c != null) {
						c.nodes.addAll(temp);
						this.categories.add(c);
						temp.clear();
					}
					c = new Category();
					c.name = tokens[1];
					c.id = categoryID++;
				} else if (line.startsWith("%")) {
					Data data = new Data();
					if (line.contains("*")) {
						data.areaTypeIncluded = true;
						Data.DataType type = Data.DataType.BLUE;
						short i = 0;
						byte count = 0;
						while (line.charAt(i) != '*') {
							if (line.charAt(i) == '%') {
								count++;
								if (count == 2)
									type = Data.DataType.ALPHA;
								else if (count == 3)
									type = Data.DataType.RED;
								else if (count == 4)
									type = Data.DataType.GREEN;
								else if (count == 5)
									type = Data.DataType.BLUE;
								else if (count >= 7)
									type = Data.DataType.BLUE;
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
							EditorConstants.class.getClassLoader().getResource(tokens[6].split("res/")[1]));
					data.button = new JButton(data.image) {
						private static final long serialVersionUID = 1L;

						@Override
						public Dimension getMinimumSize() {
							return new Dimension(Tile.WIDTH, Tile.HEIGHT);
						}

						@Override
						public Dimension getSize() {
							return new Dimension(Tile.WIDTH, Tile.HEIGHT);
						}

						@Override
						public Dimension getPreferredSize() {
							return new Dimension(Tile.WIDTH, Tile.HEIGHT);
						}
					};
					data.button.setAlignmentX(Component.CENTER_ALIGNMENT);
					data.button.setMargin(new Insets(0, 0, 0, 0));
					data.button.setBorder(null);
					this.datas.add(new AbstractMap.SimpleEntry<Integer, Data>(data.getColorValue(), data));
					temp.add(data);
				} else if (line.startsWith("+")) {
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

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadTriggers() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					EditorConstants.class.getClassLoader().getResourceAsStream("script/scripts.txt")));
			String line;
			Trigger trigger = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("/"))
					continue;
				else if (line.startsWith("$")) {
					if (trigger == null)
						trigger = new Trigger();
					int value = Integer.valueOf(line.substring(1));
					if (value != 0) {
						trigger.setTriggerID((short) (value & 0xFFFF));
					}

				} else if (line.startsWith("@")) {
					if (trigger != null)
						trigger.setName(line.substring(1));
				} else if (line.startsWith("%")) {
					this.triggers.add(trigger);
					trigger = null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static EditorConstants getInstance() {
		return instance;
	}

	public ArrayList<Category> getCategories() {
		return this.categories;
	}

	public ArrayList<Map.Entry<Integer, Data>> getDatas() {
		return this.datas;
	}

	public ArrayList<Trigger> getTriggers() {
		return this.triggers;
	}

	public static Data getData(int alpha, int red, int green, int blue) {
		ArrayList<Map.Entry<Integer, Data>> list = EditorConstants.getInstance().datas;
		Data temp = null;
		for (Map.Entry<Integer, Data> entry : list) {
			Data d = entry.getValue();
			if (d.areaTypeIncluded) {
				switch (d.areaTypeIDType) {
				case ALPHA:
					if (d.red == red && d.green == green && d.blue == blue)
						temp = d;
					break;
				case RED:
					if (d.alpha == alpha && d.green == green && d.blue == blue)
						temp = d;
					break;
				case GREEN:
					if (d.alpha == alpha && d.red == red && d.blue == blue)
						temp = d;
					break;
				case BLUE:
					if (d.alpha == alpha && d.red == red && d.green == green)
						temp = d;
					break;
				default:
					if (d.alpha == alpha && d.red == red && d.green == green && d.blue == blue)
						temp = d;
					break;
				}
			}
		}
		if (temp != null)
			return temp;
		return EditorConstants.getInstance().getDatas().get(0).getValue();
	}
}
