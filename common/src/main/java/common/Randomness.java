package common;

import java.util.Random;

public class Randomness {
	private static final Random rand = new Random();

	public static int randInt() {
		return Math.abs(Randomness.rand.nextInt());
	}

	public static int randDirection() {
		return Math.abs(Randomness.rand.nextInt()) % 4;
	}

	public static boolean randBool() {
		return Randomness.rand.nextBoolean();
	}
}
