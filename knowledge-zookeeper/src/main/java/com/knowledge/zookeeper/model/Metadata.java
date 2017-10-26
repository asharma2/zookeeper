package com.knowledge.zookeeper.model;

public class Metadata extends ZooId {

	private String name;
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		setId(this.name);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Metadata [name=" + name + ", value=" + value + ", id=" + id + "]";
	}

}
