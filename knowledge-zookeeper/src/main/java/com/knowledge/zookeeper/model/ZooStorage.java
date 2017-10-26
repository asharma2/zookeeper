package com.knowledge.zookeeper.model;

import org.apache.zookeeper.data.Stat;

public class ZooStorage<T extends ZooId> {

	private T data;
	private Stat stat;
	private int version;

	public ZooStorage(T data, int version) {
		setData(data);
		setVersion(version);
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Stat getStat() {
		return stat;
	}

	public void setStat(Stat stat) {
		this.stat = stat;
	}

}
