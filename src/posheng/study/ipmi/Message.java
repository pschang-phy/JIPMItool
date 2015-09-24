package posheng.study.ipmi;

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

		public int getByteValue() {
			return this.netFn;
		}
	}

	NetworkFunction netFn;
	int lun;
	int command;
	int[] data;

	public Message() {
		this.netFn = NetworkFunction.APP;
		this.lun = 0;
		this.command = 0;
		this.data = null;
	}

	Message setNetFn(NetworkFunction netFn) {
		this.netFn = netFn;
		return this;
	}

	Message setLUN(int lun) {
		this.lun = lun;
		return this;
	}

	Message setCommand(int command) {
		this.command = command;
		return this;
	}

	Message setData(int[] data) {
		this.data = data;
		return this;
	}
}
