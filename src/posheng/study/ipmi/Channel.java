package posheng.study.ipmi;

import java.net.SocketException;

public abstract class Channel {
	String name;
	String description;

	int maxRequestDataSize;
	int maxResponseDataSize;

	boolean opened = false;

	Channel(String name, String description) {
		this.name = name;
		this.description = description;
		this.maxRequestDataSize = 0;
		this.maxResponseDataSize = 0;
	}

	abstract public void open() throws SocketException;

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
		return "Channel [name=" + name + ", description=" + description + ", opened=" + opened + "]";
	}

}
