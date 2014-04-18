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
					data.alpha = (char) (Integer.parseInt(tokens[2], 16) & 0xFF);
					data.red = (char) (Integer.parseInt(tokens[3], 16) & 0xFF);;
					data.green = (char) (Integer.parseInt(tokens[4], 16) & 0xFF);
					data.blue = (char) (Integer.parseInt(tokens[5], 16) & 0xFF);
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
	
	//	public static int getEditorIDFromPath(String path) {
	//		String[] file = path.split("\\\\");
	//		String compare = file[file.length - 1];
	//		//Blank tile data
	//		if (compare.equals("no_png.png"))
	//			return 0;
	//		
	//		//Path
	//		else if (compare.equals("grass.png"))
	//			return 0x01000000;
	//		else if (compare.equals("mt_ground.png"))
	//			return 0x01010000;
	//		else if (compare.equals("path.png"))
	//			return 0x01020000;
	//		
	//		//Ledge
	//		else if (compare.equals("ledge_bottom.png"))
	//			return 0x02000000;
	//		else if (compare.equals("ledge_bottom_left.png"))
	//			return 0x02010000;
	//		else if (compare.equals("ledge_left.png"))
	//			return 0x02020000;
	//		else if (compare.equals("ledge_top_left.png"))
	//			return 0x02030000;
	//		else if (compare.equals("ledge_top.png"))
	//			return 0x02040000;
	//		else if (compare.equals("ledge_top_right.png"))
	//			return 0x02050000;
	//		else if (compare.equals("ledge_right.png"))
	//			return 0x02060000;
	//		else if (compare.equals("ledge_bottom_right.png"))
	//			return 0x02070000;
	//		
	//		//Ledge - Mountain
	//		else if (compare.equals("ledge_mt_bottom.png"))
	//			return 0x02080000;
	//		else if (compare.equals("ledge_mt_bottom_left.png"))
	//			return 0x02090000;
	//		else if (compare.equals("ledge_mt_left.png"))
	//			return 0x020A0000;
	//		else if (compare.equals("ledge_mt_top_left.png"))
	//			return 0x020B0000;
	//		else if (compare.equals("ledge_mt_top.png"))
	//			return 0x020C0000;
	//		else if (compare.equals("ledge_mt_top_right.png"))
	//			return 0x020D0000;
	//		else if (compare.equals("ledge_mt_right.png"))
	//			return 0x020E0000;
	//		else if (compare.equals("ledge_mt_bottom_right.png"))
	//			return 0x020F0000;
	//		
	//		//Ledge - Inner Ledges
	//		else if (compare.equals("ledge_inner_bottom.png"))
	//			return 0x02100000;
	//		else if (compare.equals("ledge_inner_bottom_left.png"))
	//			return 0x02110000;
	//		else if (compare.equals("ledge_inner_left.png"))
	//			return 0x02120000;
	//		else if (compare.equals("ledge_inner_top_left.png"))
	//			return 0x02130000;
	//		else if (compare.equals("ledge_inner_top.png"))
	//			return 0x02140000;
	//		else if (compare.equals("ledge_inner_top_right.png"))
	//			return 0x02150000;
	//		else if (compare.equals("ledge_inner_right.png"))
	//			return 0x02160000;
	//		else if (compare.equals("ledge_inner_bottom_right.png"))
	//			return 0x02170000;
	//		
	//		//Small tree
	//		else if (compare.equals("treeSmall.png"))
	//			return 0x03000000;
	//		
	//		//Warp point
	//		else if (compare.equals("forestEntrance.png"))
	//			return 0x04000000;
	//		
	//		//Stairs
	//		else if (compare.equals("stairs_bottom.png"))
	//			return 0x06000000;
	//		else if (compare.equals("stairs_left.png"))
	//			return 0x06010000;
	//		else if (compare.equals("stairs_top.png"))
	//			return 0x06020000;
	//		else if (compare.equals("stairs_right.png"))
	//			return 0x06030000;
	//		else if (compare.equals("stairs_mt_bottom.png"))
	//			return 0x06040000;
	//		else if (compare.equals("stairs_mt_left.png"))
	//			return 0x06050000;
	//		else if (compare.equals("stairs_mt_top.png"))
	//			return 0x06060000;
	//		else if (compare.equals("stairs_mt_right.png"))
	//			return 0x06070000;
	//		
	//		//Sign
	//		else if (compare.equals("sign.png"))
	//			return 0x08000000;
	//		
	//		//House
	//		else if (compare.equals("house_bottom.png"))
	//			return 0x09010000;
	//		else if (compare.equals("house_bottom_left.png"))
	//			return 0x09020000;
	//		else if (compare.equals("house_bottom_right.png"))
	//			return 0x09030000;
	//		
	//		//House Door
	//		else if (compare.equals("house_door.png"))
	//			return 0x0A000000;
	//		else
	//			return 99;
	//	}
	
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
