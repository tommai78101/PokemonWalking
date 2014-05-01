/**
 * THIS IS CREATED BY tom_mai78101. GIVE PROJECT CREATOR ITS CREDITS.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. THIS IS A CLONE. 
 * */

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
	public int alpha, red, green, blue;
	public Image image;
	public JButton button;
	public boolean areaTypeIncluded;
	public DataType areaTypeIDType;
	public char areaType;
}
