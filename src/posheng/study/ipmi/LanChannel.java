package posheng.study.ipmi;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class LanChannel extends Channel {
	static final int IPMI_LAN_RETRY = 4;
	static final int IPMI_LAN_PORT = 0x26F;
	static final int IPMI_LAN_CHANNEL_E = 0x0E;

	public enum AuthTYPE {
		UNKNOWN(0x00), NONE(0x01), MD2(0x02), MD5(0x04), KEY(0x10), PASSWORD(0x10), OEM(0x20), RMCP_PLUS(0x80);

		private final int authType;

		private AuthTYPE(int authType) {
			this.authType = authType;
		}

		public int getByteValue() {
			return this.authType;
		}
	}

	public enum PrivilegeLevel {
		UNSPECIFIED(0x00), CALLBACK(0x01), USER(0x02), OPERATOR(0x03), ADMIN(0x04), OEM(0x05);

		private final int privLevel;

		private PrivilegeLevel(int privLevel) {
			this.privLevel = privLevel;
		}

		public int getByteValue() {
			return this.privLevel;
		}
	}

	public class Session {
		AuthTYPE authtype;
		int sequenceNumber;
		int sessionID;
		int[] authCode;

		PrivilegeLevel privLevel;
		boolean active;

		public Session() {
			this.authtype = AuthTYPE.UNKNOWN;
			this.sequenceNumber = 0;
			this.sessionID = 0;
			this.authCode = null;

			this.privLevel = PrivilegeLevel.ADMIN;
			this.active = false;
		}

	}

	Session session;

	String hostname;
	String username;
	String password;
	int timeout;
	int retry;

	DatagramSocket socket;
	DatagramPacket requestPacket;
	DatagramPacket responsetPacket;

	public LanChannel() {
		super("Lan", "IPMI v1.5 LAN Interface");

		setMaxRequestDataSize(38);
		setMaxResponseDataSize(34);

		this.session = new Session();

		this.hostname = "localhost";
		this.username = "";
		this.password = "";
		this.timeout = 2;
		this.retry = 4;

		this.socket = null;
		requestPacket = null;
		responsetPacket = null;
	}

	public LanChannel(String hostname, String username, String password) {
		this();

		this.hostname = hostname;
		this.username = username;
		this.password = password;
	}

	@Override
	public void open() throws SocketException {
		// TODO Auto-generated method stub
		this.socket = new DatagramSocket();
		setOpened(true);

		activateSession();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public int sendrecv(Message msg) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int activateSession() {

		return 0;
	}

}
