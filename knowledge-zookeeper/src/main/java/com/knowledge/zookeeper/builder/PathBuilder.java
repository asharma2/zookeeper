package com.knowledge.zookeeper.builder;

import org.apache.commons.lang3.StringUtils;

import com.knowledge.zookeeper.model.ZkContainer;
import com.knowledge.zookeeper.model.ZooId;

public class PathBuilder {

	public static final String SEPARATOR = "/";
	private StringBuilder path = new StringBuilder();

	public PathBuilder with(String... paths) {
		for (String path : paths) {
			if (StringUtils.isNotBlank(path)) {
				this.path.append(SEPARATOR).append(path);
			}
		}
		return this;
	}

	public PathBuilder with(Class<?> klass) {
		if (klass != null) {
			this.path.append(SEPARATOR).append(klass.getName());
		}
		return this;
	}

	public <T extends ZooId> PathBuilder with(T data) {
		if (data != null) {
			this.path.append(SEPARATOR).append(data.getClass().getName());
			this.path.append(SEPARATOR).append(data.getId());
		}
		return this;
	}

	public <T extends ZooId> PathBuilder with(ZkContainer<T> zooStorage) {
		if (zooStorage != null) {
			this.path.append(SEPARATOR).append(zooStorage.getData().getClass().getName());
			this.path.append(SEPARATOR).append(zooStorage.getData().getId());
		}
		return this;
	}

	public String build() {
		return path.toString();
	}
}
