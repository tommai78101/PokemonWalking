/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */
package screen;

public class Rect {
	public int topLeftCorner_X;
	public int topLeftCorner_Y;
	public int bottomRightCorner_X;
	public int bottomRightCorner_Y;
	public int width;
	public int height;
	
	public Rect(int x, int y, int w, int h) {
		this.topLeftCorner_X = x;
		this.topLeftCorner_Y = y;
		this.width = w;
		this.height = h;
		this.bottomRightCorner_X = x + w;
		this.bottomRightCorner_Y = y + h;
	}
	
	public Rect adjust(BaseScreen screen) {
		if (this.topLeftCorner_X < 0)
			this.topLeftCorner_X = 0;
		if (this.topLeftCorner_Y < 0)
			this.topLeftCorner_Y = 0;
		if (this.bottomRightCorner_X > screen.width)
			this.bottomRightCorner_X = screen.width;
		if (this.bottomRightCorner_Y > screen.height)
			this.bottomRightCorner_Y = screen.height;
		return this;
	}
	
}
