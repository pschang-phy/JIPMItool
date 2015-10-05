package posheng.study.ipmi;

import static java.lang.String.format;

public class Message {

	public enum NetworkFunction {
		CHASSIS(0x00),
		CHASSIS_RSP(0x01),
		BRIDGE(0x02),
		BRIDGE_RSP(0x03),
		SENSOR_EVENT(0x04),
		SENSOR_EVENT_RSP(0x05),
		APP(0x06),
		APP_RSP(0x07),
		FIRMWARE(0x08),
		FIRMWARE_RSP(0x09),
		STORAGE(0x0A),
		STORAGE_RSP(0x0B),
		TRANSPORT(0x0C),
		TRANSPORT_RSP(0x0D);

		private final int netFn;

		private NetworkFunction(int netFn) {
			this.netFn = netFn;
		}

		public int value() {
			return this.netFn;
		}

		public byte getByte() {
			return (byte) this.netFn;
		}
	}

	NetworkFunction netFn;
	int lun = 0;
	int command;
	int[] data = null;

	public Message(NetworkFunction netFn, int cmd) {
		setNetFn(netFn);
		setCommand(cmd);
	}

	public NetworkFunction getNetFn() {
		return netFn;
	}

	public void setNetFn(NetworkFunction netFn) {
		this.netFn = netFn;
	}

	public int getLun() {
		return lun;
	}

	public void setLun(int lun) {
		this.lun = lun;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public int[] getData() {
		return data;
	}

	public void setData(int[] data) {
		this.data = data;
	}

	@Override
	public String toString() {
		String output = format("Message [netFn=%s, lun=%d, command=0x%02X]\n",
				netFn, lun, command);

		if (null != data) {
			output += format("  Data (%d %s)", data.length,
					data.length > 1 ? "bytes" : "byte");

			for (int i = 0; i < data.length; i++) {
				if (0 == (i & 0x0F))
					output += "\n   ";
				output += format(" %02X", data[i] & 0xFF);
			}

		} else {
			output += "  Empty data";
		}

		return output;
	}

}
