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

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Data {
	enum DataType {
		ALPHA, RED, GREEN, BLUE;
	};
	
	public String name;
	public String filepath;
	public int editorID;
	public int alpha, red, green, blue;
	public ImageIcon image;
	public JButton button;
	public boolean areaTypeIncluded;
	public DataType areaTypeIDType;
	public char areaType;
	public boolean alphaByEditor;
	public boolean redByEditor;
	public boolean greenByEditor;
	public boolean blueByEditor;
	
	public Data(){
		name = filepath = "";
		alpha = red = green = blue = editorID = 0;
		image = null;
		button = null;
		areaTypeIDType = DataType.ALPHA;
		areaTypeIncluded = alphaByEditor = redByEditor = greenByEditor = blueByEditor = false;
	}
	
	public boolean compare(Data d){
		int dataValue1 = (this.alpha << 24) | (this.red << 16) | (this.green << 8) | this.blue;
		int dataValue2 = (d.alpha << 24) | (d.red << 16) | (d.green << 8) | d.blue;
		if (dataValue1 == dataValue2)
			return true;
		return false;
	}
	
	public int getColorValue(){
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}
}
