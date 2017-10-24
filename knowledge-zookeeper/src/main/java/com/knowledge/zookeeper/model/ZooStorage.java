package com.knowledge.zookeeper.model;

public class ZooStorage<T extends ZooId> {

	private T data;
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

}
