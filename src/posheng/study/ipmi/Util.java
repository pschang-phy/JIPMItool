package posheng.study.ipmi;

public final class Util {

	public static void printbuf(int[] buf, int offset, int length, String desc) {
		System.out.printf("%s (%d)\n", desc, length);
		for (int i = 0; i < length; i++) {
			if (0 != i && 0 == (i & 0x0F))
				System.out.println("");
			System.out.printf(" 0x%02X", buf[i] & 0xFF);
		}
		System.out.println("");
	}
}
