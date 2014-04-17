package editor;

import java.awt.Image;
import javax.swing.JButton;

public class Data {
	enum DataType {
		ALPHA, RED, GREEN, BLUE;
	};
	
	public String name;
	public String filepath;
	public int editorID;
	public byte alpha, red, green, blue;
	public Image image;
	public JButton button;
	public boolean areaTypeIncluded;
	public DataType areaTypeIDType;
}
