package main;

import java.util.ArrayList;
import java.util.List;

public class Keys {
	public class Key {
		public final String name;
		//Input handling variables.
		//		public boolean isTapped;
		//		public boolean nextState;
		public boolean keyStateDown;
		
		//Key related variables.
		public boolean isTappedDown;
		public boolean isPressedDown;
		
		public Key(String name) {
			this.name = name;
			Keys.this.all.add(this);
		}
		
		//		public void tick(List<Key> all) {
		//			if (isTapped) {
		//				isTappedDown = nextState;
		//				isPressedDown = false;
		//			}
		//			else {
		//				isPressedDown = nextState;
		//				isTappedDown = false;
		//
		//			}
		//			for (int i = 0; i < all.size() - 1; i++) {
		//				for (int j = i + 1; j < all.size(); j++) {
		//					Key k = all.get(i);
		//					if (k.isTappedDown || k.isPressedDown) {
		//						Key l = all.get(j);
		//						l.isTappedDown = false;
		//						l.isPressedDown = false;
		//						//l.nextState = false;
		//					}
		//				}
		//			}
		//		}
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
}
