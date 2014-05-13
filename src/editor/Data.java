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
