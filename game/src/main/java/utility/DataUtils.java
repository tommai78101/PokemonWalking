package utility;

public class DataUtils {
	public static int createDataId(byte alpha, byte red, byte green, byte blue) {
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	public static byte getAlphaData(int pixelColor) {
		return (byte) ((pixelColor >> 24) & 0xFF);
	}
	
	public static byte getRedData(int pixelColor) {
		return (byte) ((pixelColor >> 16) & 0xFF);
	}
	
	public static byte getGreenData(int pixelColor) {
		return (byte) ((pixelColor >> 8) & 0xFF);
	}
	
	public static byte getBlueData(int pixelColor) {
		return (byte) (pixelColor & 0xFF);
	}
	
	public static byte getByteData(int pixelColor, int bitShift) {
		return (byte) ((pixelColor >> bitShift) & 0xFF);
	}
}
