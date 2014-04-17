package editor;

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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import abstracts.Tile;

public class EditorConstants {
	
	//TODO: Add additional pixel data properties that can be edited/modified for the area.
	
	private final HashMap<String, Data> tileMap;
	private static final EditorConstants instance = new EditorConstants();
	
	public static final String DEFAULT_TILE = "Empty";
	
	private EditorConstants() {
		int id = 1;
		tileMap = new HashMap<String, Data>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(EditorConstants.class.getClassLoader().getResourceAsStream("art/editor/data.txt")));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#"))
					continue;
				else if (line.startsWith("-"))
					//TODO: Modify GUI to categorize tiles into groups.
					continue;
				else if (line.startsWith("%")) {
					String trim = line.trim().replaceAll("\\s+", "").replaceAll("@", "00");
					String[] tokens = trim.split("%");
					Data data = new Data();
					data.name = tokens[1].replace('_', ' ');
					data.alpha = (byte) (Integer.parseInt(tokens[2], 16) & 0xFF);
					data.red = (byte) (Integer.parseInt(tokens[3], 16) & 0xFF);;
					data.green = (byte) (Integer.parseInt(tokens[4], 16) & 0xFF);
					data.blue = (byte) (Integer.parseInt(tokens[5], 16) & 0xFF);
					data.filepath = tokens[6];
					data.editorID = id;
					ImageIcon icon = new ImageIcon(tokens[6]);
					data.image = icon.getImage();
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
					tileMap.put(tokens[1], data);
					id++;
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
	
	public HashMap<String, Data> getTileMap() {
		return this.tileMap;
	}
	
	public List<Map.Entry<String, Data>> getSortedTileMap() {
		final int size = this.tileMap.size();
		final List<Map.Entry<String, Data>> list = new ArrayList<Map.Entry<String, Data>>(size);
		list.addAll(this.tileMap.entrySet());
		final Comparator<Map.Entry<String, Data>> c = new Comparator<Map.Entry<String, Data>>() {
			@Override
			public int compare(Map.Entry<String, Data> d1, Map.Entry<String, Data> d2) {
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
