package main;

import java.util.ArrayList;
import java.util.List;

public class Keys {
	public class Key {
		public final String name;
		public boolean isTapped;
		public boolean isTappedDown;
		public boolean isPressedDown;
		public boolean nextState;
		
		public Key(String name) {
			this.name = name;
			Keys.this.all.add(this);
		}
		
		public void tick() {
			if (isTapped) {
				isTappedDown = nextState;
				isPressedDown = false;
			}
			else {
				isPressedDown = nextState;
				isTappedDown = false;
			}
		}
	}
	
	List<Key> all = new ArrayList<Key>();
	
	public Key up = new Key("up");
	public Key down = new Key("down");
	public Key left = new Key("left");
	public Key right = new Key("right");
	
	public Key W = new Key("W");
	public Key S = new Key("S");
	public Key A = new Key("A");
	public Key D = new Key("D");
	
	public void tick() {
		for (Key k : all)
			k.tick();
	}
}
