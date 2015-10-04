package posheng.study.ipmi;

public final class Util {
	public static void printbuf(byte[] buf, int offset, int length,
			String desc) {

		if (offset < 0 || offset >= buf.length)
			throw new ArrayIndexOutOfBoundsException(offset);

		int sentinel = offset + length > buf.length ? buf.length
				: offset + length;

		String output = "";
		if (null != desc)
			output += String.format("%s (%d %s)\n", desc, sentinel - offset,
					sentinel - offset > 1 ? "bytes" : "byte");

		for (int i = offset, j = 1; i < sentinel; i++, j++) {
			output += String.format("%02X", buf[i] & 0xFF);
			output += (0 == (j & 0x0F)) ? "\n" : " ";
		}

		System.out.println(output);
	}

	public static void printbuf(byte[] buf, String desc) {
		printbuf(buf, 0, buf.length, desc);
	}

	public static void printbuf(byte[] buf, int offset, int length) {
		printbuf(buf, offset, length, null);
	}

	public static void printbuf(byte[] buf) {
		printbuf(buf, 0, buf.length, null);
	}

	public static byte ipmiChecksum(byte[] data, int start, int end) {
		int checksum = 0;
		for (int i = start; i < end; i++)
			checksum = (checksum + data[i]) & 0xFF;

		return (byte) -checksum;
	}
}
