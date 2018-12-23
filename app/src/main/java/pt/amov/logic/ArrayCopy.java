package pt.amov.logic;

public class ArrayCopy {

	public static void copyBinaryArray(byte[][] src, byte[][] dest) {
		for (int i = 0; i < 8; i++) {
			System.arraycopy(src[i], 0, dest[i], 0, 8);
		}
	}
	
}
