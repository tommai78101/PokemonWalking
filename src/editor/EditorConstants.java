/**
 * THIS IS CREATED BY tom_mai78101. GIVE PROJECT CREATOR ITS CREDITS.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. THIS IS A CLONE. 
 * */

package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;

import abstracts.Tile;

public class EditorConstants {
	
	//TODO: Add additional pixel data properties that can be edited/modified for the area.
	
	private final HashMap<Integer, Data> tileMap;
	private static final EditorConstants instance = new EditorConstants();
	
	public static final Color GRASS_GREEN = new Color(164, 231, 103);
	public static final Color ROAD_WHITE = new Color(255, 244, 201);
	public static final Color DIRT_SIENNA = new Color(202, 143, 3);
	public static final Color WATER_BLUE = new Color(0, 65, 255);
	
	private EditorConstants() {
		int id = 0;
		tileMap = new HashMap<Integer, Data>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(EditorConstants.class.getClassLoader().getResourceAsStream("art/editor/data.txt")));
			String line;
			while ((line = reader.readLine()) != null) {
				Data data = new Data();
				if (line.contains("*")) {
					data.areaTypeIncluded = true;
					Data.DataType type = Data.DataType.BLUE;
					short i = 0;
					byte count = 0x00;
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
				if (line.startsWith("#")) {
					data = null;
					continue;
				}
				else if (line.startsWith("-")) {
					//TODO: Modify GUI to categorize tiles into groups.
					data = null;
					continue;
				}
				else if (line.startsWith("%")) {
					String trim = line.trim().replaceAll("\\s+", "").replaceAll("@", "00");
					String[] tokens = trim.split("%");
					data.name = tokens[1].replace('_', ' ');
					data.alpha = Integer.valueOf(tokens[2], 16);
					data.red = Integer.valueOf(tokens[3], 16);
					data.green = Integer.valueOf(tokens[4], 16);
					data.blue = Integer.valueOf(tokens[5], 16);
					data.filepath = tokens[6];
					data.editorID = id;
					data.image = ImageIO.read(EditorConstants.class.getClassLoader().getResource(tokens[6].split("res/")[1]));
					ImageIcon icon = new ImageIcon(data.image);
					data.button = new JButton(icon) {
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
					data.button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
					data.button.setMargin(new Insets(0, 0, 0, 0));
					data.button.setBorder(null);
					if (data.areaTypeIncluded) {
						for (char i = 0; i < 0x06; i++) {
							data.areaType = i;
							tileMap.put(data.editorID, data);
						}
					}
					else
						tileMap.put(data.editorID, data);
					id++;
					data = null;
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load data correctly in EditorConstants.", e);
		}
	}
	
	public static EditorConstants getInstance() {
		return instance;
	}
	
	public HashMap<Integer, Data> getTileMap() {
		return this.tileMap;
	}
	
	public List<Map.Entry<Integer, Data>> getSortedTileMap() {
		final int size = this.tileMap.size();
		final List<Map.Entry<Integer, Data>> list = new ArrayList<Map.Entry<Integer, Data>>(size);
		list.addAll(this.tileMap.entrySet());
		final Comparator<Map.Entry<Integer, Data>> c = new Comparator<Map.Entry<Integer, Data>>() {
			@Override
			public int compare(Map.Entry<Integer, Data> d1, Map.Entry<Integer, Data> d2) {
				Data e1 = d1.getValue();
				Data e2 = d2.getValue();
				if (e1.editorID < e2.editorID)
					return -1;
				if (e1.editorID > e2.editorID)
					return 1;
				return 0;
			}
		};
		Collections.sort(list, c);
		return list;
	}
}
