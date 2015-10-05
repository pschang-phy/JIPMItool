package posheng.study.ipmi;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Session {

	public enum AuthType {
		NONE(0x00),
		MD2(0x01),
		MD5(0x02),
		KEY(0x04),
		PASSWORD(0x04),
		OEM(0x05),
		RMCP_PLUS(0x06);

		private final int authType;

		private AuthType(int authType) {
			this.authType = authType;
		}

		public int value() {
			return this.authType;
		}

		public byte getByte() {
			return (byte) this.authType;
		}
	}

	AuthType authType;
	int seq;
	int id;
	byte[] authCode;

	boolean active;

	private ByteBuffer header;

	public Session() {
		this.authType = AuthType.NONE;
		this.seq = 0;
		this.id = 0;
		this.authCode = null;

		this.active = false;

		this.header = ByteBuffer.allocate(25).order(ByteOrder.LITTLE_ENDIAN);
	}

	int activate() {
		this.active = true;
		return 0;
	}

	ByteBuffer header(byte[] payload, String password) {
		header.clear();

		header.put(this.authType.getByte());
		header.putInt(this.seq);
		header.putInt(this.id);

		if (this.active && AuthType.NONE != this.authType) {
			genAuthCode(payload, password);
			header.put(this.authCode);
		}
		header.flip();

		return header;
	}

	void clearAuthCode() {
		this.authCode = null;
	}

	private void genAuthCode(byte[] payload, String password) {
		String algorithm = "NONE";
		if (!this.active)
			algorithm = "NONE";
		else if (AuthType.MD5 == this.authType)
			algorithm = "MD5";
		else if (AuthType.MD2 == this.authType)
			algorithm = "MD2";

		if (null != payload && null != password && "NONE" != algorithm) {
			byte[] passwordBuf = Arrays.copyOf(password.getBytes(), 16);

			int bufferSize = 40 + payload.length;
			ByteBuffer authCode = ByteBuffer.allocate(bufferSize);
			authCode.order(ByteOrder.LITTLE_ENDIAN);

			authCode.put(passwordBuf);
			authCode.putInt(this.id);
			authCode.put(payload);
			authCode.putInt(this.seq);
			authCode.put(passwordBuf);
			authCode.flip();

			try {
				MessageDigest msgDigest = MessageDigest.getInstance(algorithm);
				msgDigest.update(authCode);
				this.authCode = msgDigest.digest();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

		} else {
			this.authCode = null;
		}
	}
}
