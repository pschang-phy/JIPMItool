package posheng.study.ipmi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * @author Po-Sheng
 *
 */
public class LanChannel extends Channel {
	static final boolean DEBUG = true;

	static final int LAN_MAX_REQUEST_SIZE = 38;
	static final int LAN_MAX_RESPONSE_SIZE = 34;

	static final int IPMI_LAN_RETRY = 4;
	static final int IPMI_LAN_TIMEOUT = 2;

	static final int IPMI_LAN_PORT = 0x26F;
	static final int IPMI_LAN_CHANNEL_E = 0x0E;

	static final int IPMI_BMC_SLAVE_ADDR = 0x20;
	static final int IPMI_REMOTE_SWID = 0x81;

	static final byte[] rmcpHeader = { 0x06, 0x00, (byte) 0xFF, 0x07 };

	Session session;
	int rsAddr;
	int rqAddr;
	int rqSeq;

	String hostname;
	String username;
	String password;
	PrivilegeLevel privLevel;

	int timeout;
	int retry;

	private InetSocketAddress socketAddress;

	private ByteBuffer packetBuffer;

	private LanChannel() {
		super("Lan", "IPMI v1.5 LAN Interface");

		setMaxRequestDataSize(LAN_MAX_REQUEST_SIZE);
		setMaxResponseDataSize(LAN_MAX_RESPONSE_SIZE);

		this.session = new Session();
		this.rsAddr = IPMI_BMC_SLAVE_ADDR;
		this.rqAddr = IPMI_REMOTE_SWID;
		this.rqSeq = 0;

		this.hostname = "localhost";
		this.username = "";
		this.password = "";
		this.privLevel = PrivilegeLevel.ADMIN;

		this.timeout = IPMI_LAN_TIMEOUT;
		this.retry = IPMI_LAN_RETRY;

		this.packetBuffer = ByteBuffer.allocate(256)
				.order(ByteOrder.LITTLE_ENDIAN);
	}

	public LanChannel(String hostname, String username, String password) {
		this();

		this.hostname = hostname;
		this.username = username;
		this.password = password;

		this.socketAddress = new InetSocketAddress(hostname, IPMI_LAN_PORT);
	}

	@Override
	public void open() {
		setOpened(true);

		session.activate();
	}

	@Override
	public void close() {
		setOpened(false);
	}

	@Override
	public int sendrecv(Message msg) {
		if (!isOpened()) {
			return -1;
		}

		int retry = 0;
		while (retry < this.retry) {
			buildRequestPacket(msg, retry);

			if (DEBUG) {
				Util.printbuf(packetBuffer.array(), 0, packetBuffer.remaining(),
						"send packet:");
				break;
			} else {
				try {
					send(packetBuffer, socketAddress);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			retry++;
		}

		return 0;
	}

	/**
	 * <h1>IPMI LAN Request Message Format</h1>
	 * 
	 * <pre>
	 * +--------------------+
	 * |  rmcp.ver          | 4 bytes
	 * |  rmcp.__reserved   |
	 * |  rmcp.seq          |
	 * |  rmcp.class        |
	 * +--------------------+
	 * |  session.authtype  | 9 bytes
	 * |  session.seq       |
	 * |  session.id        |
	 * +--------------------+
	 * | [session.authcode] | 16 bytes (AUTHTYPE != none)
	 * +--------------------+
	 * |  message length    | 1 byte
	 * +--------------------+
	 * |  message.rs_addr   | 6 bytes
	 * |  message.netfn_lun |
	 * |  message.checksum  |
	 * |  message.rq_addr   |
	 * |  message.rq_seq    |
	 * |  message.cmd       |
	 * +--------------------+
	 * | [request data]     | data_len bytes
	 * +--------------------+
	 * |  checksum          | 1 byte
	 * +--------------------+
	 * </pre>
	 * 
	 * @author Po-Sheng
	 * @since 2015-09-25
	 */
	private void buildRequestPacket(Message msg, int retry) {
		packetBuffer.clear();

		if (0 == retry)
			this.rqSeq++;

		// Payload Data
		byte[] payload = buildPayload(msg);

		// RMCP header
		packetBuffer.put(rmcpHeader);

		// Session header:
		// Authentication Type
		// Session Sequence Number
		// IPMI v1.5 Session ID
		// Message Authentication Code
		packetBuffer.put(session.header(payload, password));

		// IPMI Message/Payload length
		packetBuffer.put((byte) payload.length);
		packetBuffer.put(payload);

		packetBuffer.flip();

		if (0 != session.seq)
			session.seq++;
	}

	private byte[] buildPayload(Message msg) {
		int payloadLength = 7;
		if (null != msg.data)
			payloadLength += msg.data.length;

		byte[] payload = new byte[payloadLength];

		int start, end;
		int index = 0;

		start = index;
		payload[index++] = (byte) this.rsAddr;
		payload[index++] = (byte) (msg.netFn.value() << 2 | msg.lun & 3);
		end = index;

		payload[index++] = Util.ipmiChecksum(payload, start, end);

		start = index;
		payload[index++] = (byte) this.rqAddr;
		payload[index++] = (byte) (rqSeq << 2);
		payload[index++] = (byte) msg.command;

		if (null != msg.data && msg.data.length > 0) {
			for (int data : msg.data)
				payload[index++] = (byte) data;
		}
		end = index;

		payload[index++] = Util.ipmiChecksum(payload, start, end);

		return payload;
	}

	private static int send(ByteBuffer packet, SocketAddress target)
			throws IOException {

		try (DatagramChannel channel = DatagramChannel.open()) {
			Selector selector = Selector.open();

			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_READ);

			channel.send(packet, target);

			if (0 == selector.select(IPMI_LAN_TIMEOUT))
				throw new SocketTimeoutException("Receive timed out");

			packet.clear();
			channel.receive(packet);
			packet.flip();
		}

		return packet.limit();
	}

}
