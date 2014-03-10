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
		
		public void tick(List<Key> all) {
			if (isTapped) {
				isTappedDown = nextState;
				isPressedDown = false;
			}
			else {
				isPressedDown = nextState;
				isTappedDown = false;

			}
			for (int i = 0; i < all.size() - 1; i++) {
				for (int j = i + 1; j < all.size(); j++) {
					Key k = all.get(i);
					if (k.isTappedDown || k.isPressedDown) {
						Key l = all.get(j);
						l.isTappedDown = false;
						l.isPressedDown = false;
						//l.nextState = false;
					}
				}
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
	
	//	public Random r = new Random();
	//	public int test = 30;

	public void tick() {
		//		if (test < 0) {
		//			test = 40;
		//			for (Key k : all)
		//				k.tick();
		//			switch (r.nextInt(4)) {
		//				case 0:
		//					down.isPressedDown = r.nextBoolean();
		//					System.out.println("DOWN");
		//					break;
		//				case 1:
		//					up.isPressedDown = r.nextBoolean();
		//					System.out.println("UP");
		//					break;
		//				case 2:
		//					right.isPressedDown = r.nextBoolean();
		//					System.out.println("RIGHT");
		//					break;
		//				case 3:
		//					left.isPressedDown = r.nextBoolean();
		//					System.out.println("LEFT");
		//					break;
		//			}
		//		}
		//		test--;
		for (Key k : all)
			k.tick(all);
	}
}
