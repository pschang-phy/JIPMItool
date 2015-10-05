package posheng.study.ipmi;

public abstract class Channel {

	public enum PrivilegeLevel {
		UNSPECIFIED(0x00),
		CALLBACK(0x01),
		USER(0x02),
		OPERATOR(0x03),
		ADMIN(0x04),
		OEM(0x05);

		private final int privLevel;

		private PrivilegeLevel(int privLevel) {
			this.privLevel = privLevel;
		}

		public int value() {
			return this.privLevel;
		}

		public byte getByte() {
			return (byte) this.privLevel;
		}
	}

	String name;
	String description;

	int maxRequestDataSize = 0;
	int maxResponseDataSize = 0;

	boolean opened = false;

	Channel(String name, String description) {
		this.name = name;
		this.description = description;
	}

	abstract public void open();

	abstract public void close();

	abstract public int sendrecv(Message msg);

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMaxRequestDataSize() {
		return maxRequestDataSize;
	}

	public void setMaxRequestDataSize(int maxRequestDataSize) {
		this.maxRequestDataSize = maxRequestDataSize;
	}

	public int getMaxResponseDataSize() {
		return maxResponseDataSize;
	}

	public void setMaxResponseDataSize(int maxResponseDataSize) {
		this.maxResponseDataSize = maxResponseDataSize;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	@Override
	public String toString() {
		return String.format("Channel [name=%s, description=%s, opened=%b]",
				name, description, opened);
	}

}
