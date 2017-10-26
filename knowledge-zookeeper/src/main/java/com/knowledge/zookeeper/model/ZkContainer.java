package com.knowledge.zookeeper.model;

public class ZkContainer<T extends ZooId> {

	private T data;

	public ZkContainer(T data) {
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Container [data=" + data + "]";
	}

}
