package editor;

public class EditorConstants {
	
	//TODO: Add additional pixel data properties that can be edited/modified for the area.
	
	public static int getEditorIDFromPath(String path) {
		String[] file = path.split("\\\\");
		String compare = file[file.length - 1];
		//Blank tile data
		if (compare.equals("no_png.png"))
			return 0;
		
		//Path
		else if (compare.equals("grass.png"))
			return 0x01000000;
		else if (compare.equals("mt_ground.png"))
			return 0x01010000;
		else if (compare.equals("path.png"))
			return 0x01020000;
		
		//Ledge
		else if (compare.equals("ledge_bottom.png"))
			return 0x02000000;
		else if (compare.equals("ledge_bottom_left.png"))
			return 0x02010000;
		else if (compare.equals("ledge_left.png"))
			return 0x02020000;
		else if (compare.equals("ledge_top_left.png"))
			return 0x02030000;
		else if (compare.equals("ledge_top.png"))
			return 0x02040000;
		else if (compare.equals("ledge_top_right.png"))
			return 0x02050000;
		else if (compare.equals("ledge_right.png"))
			return 0x02060000;
		else if (compare.equals("ledge_bottom_right.png"))
			return 0x02070000;
		
		//Ledge - Mountain
		else if (compare.equals("ledge_mt_bottom.png"))
			return 0x02080000;
		else if (compare.equals("ledge_mt_bottom_left.png"))
			return 0x02090000;
		else if (compare.equals("ledge_mt_left.png"))
			return 0x020A0000;
		else if (compare.equals("ledge_mt_top_left.png"))
			return 0x020B0000;
		else if (compare.equals("ledge_mt_top.png"))
			return 0x020C0000;
		else if (compare.equals("ledge_mt_top_right.png"))
			return 0x020D0000;
		else if (compare.equals("ledge_mt_right.png"))
			return 0x020E0000;
		else if (compare.equals("ledge_mt_bottom_right.png"))
			return 0x020F0000;
		
		//Ledge - Inner Ledges
		else if (compare.equals("ledge_inner_bottom.png"))
			return 0x02100000;
		else if (compare.equals("ledge_inner_bottom_left.png"))
			return 0x02110000;
		else if (compare.equals("ledge_inner_left.png"))
			return 0x02120000;
		else if (compare.equals("ledge_inner_top_left.png"))
			return 0x02130000;
		else if (compare.equals("ledge_inner_top.png"))
			return 0x02140000;
		else if (compare.equals("ledge_inner_top_right.png"))
			return 0x02150000;
		else if (compare.equals("ledge_inner_right.png"))
			return 0x02160000;
		else if (compare.equals("ledge_inner_bottom_right.png"))
			return 0x02170000;
		
		//Small tree
		else if (compare.equals("treeSmall.png"))
			return 0x03000000;
		
		//Warp point
		else if (compare.equals("forestEntrance.png"))
			return 0x04000000;
		
		//Stairs
		else if (compare.equals("stairs_bottom.png"))
			return 0x06000000;
		else if (compare.equals("stairs_left.png"))
			return 0x06010000;
		else if (compare.equals("stairs_top.png"))
			return 0x06020000;
		else if (compare.equals("stairs_right.png"))
			return 0x06030000;
		else if (compare.equals("stairs_mt_bottom.png"))
			return 0x06040000;
		else if (compare.equals("stairs_mt_left.png"))
			return 0x06050000;
		else if (compare.equals("stairs_mt_top.png"))
			return 0x06060000;
		else if (compare.equals("stairs_mt_right.png"))
			return 0x06070000;
		
		//Sign
		else if (compare.equals("sign.png"))
			return 0x08000000;
		
		//House
		else if (compare.equals("house_bottom.png"))
			return 0x09010000;
		else if (compare.equals("house_bottom_left.png"))
			return 0x09020000;
		else if (compare.equals("house_bottom_right.png"))
			return 0x09030000;
		
		//House Door
		else if (compare.equals("house_door.png"))
			return 0x0A000000;
		else
			return 0;
	}
}