/**
 * 
 */
package common;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created a SHA-2 algorithm generator to create unique checksums.
 * 
 * @see https://en.wikipedia.org/wiki/SHA-2#Pseudocode
 * @author tlee
 */
public class Sha2Utils {
	public static String generateRandom(String input) {
		try {
			byte[] salt = new byte[512];
			Random random = new Random();
			random.nextBytes(salt);

			MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
			messageDigest.update(salt);
			messageDigest.update(input.getBytes());
			return new BigInteger(1, messageDigest.digest()).toString(16);
		}
		catch (NoSuchAlgorithmException e) {
			Debug.error("No such algorithm found. Returning original input string. ", e);
		}
		return input;
	}

	public static String generate(String input) {
		try {
			long seed = 1L;
			byte[] inputBytes = input.getBytes();
			for (int i = 0; i < inputBytes.length; i++) {
				byte b = inputBytes[i];
				seed |= b << (seed << 7) * i;
				seed |= b << (b ^ b << 13) * (i + 1);
				seed |= b << (seed << 23) * (i + 2);
				seed |= b << (b ^ b << 31) * (i + 3);
			}

			byte[] salt = new byte[512];
			Random random = new Random();
			random.setSeed(seed);
			random.nextBytes(salt);

			MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
			messageDigest.update(salt);
			messageDigest.update(input.getBytes());
			return new BigInteger(1, messageDigest.digest()).toString(16);
		}
		catch (NoSuchAlgorithmException e) {
			Debug.error("No such algorithm found. Returning original input string. ", e);
		}
		return input;
	}
}
