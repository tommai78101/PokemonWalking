package main;

import java.util.ArrayList;
import java.util.List;

public class Keys {
	public class Key {
		public final String name;
		
		//Input handling variables.
		public boolean keyStateDown;
		
		//Key related variables.
		public boolean isTappedDown;
		public boolean isPressedDown;
		
		public Key(String name) {
			this.name = name;
			Keys.this.all.add(this);
		}
	}
	
	List<Key> all = new ArrayList<Key>();
	
	public Key up = new Key("up");
	public Key down = new Key("down");
	public Key left = new Key("left");
	public Key right = new Key("right");
	public Key Z = new Key("Z");
	public Key X = new Key("X");
	
	public Key W = new Key("W");
	public Key S = new Key("S");
	public Key A = new Key("A");
	public Key D = new Key("D");
	public Key SLASH = new Key("/");
	public Key PERIOD = new Key(".");
}
