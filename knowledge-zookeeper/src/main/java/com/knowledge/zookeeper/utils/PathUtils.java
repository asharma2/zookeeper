package com.knowledge.zookeeper.utils;

import org.apache.commons.lang3.StringUtils;

import com.knowledge.zookeeper.model.ZooId;
import com.knowledge.zookeeper.model.ZooStorage;

public final class PathUtils {

	public static final String SEPARATOR = "/";

	public static final <T extends ZooId> String createPath(ZooStorage<T> zooStorage) {
		StringBuilder path = new StringBuilder();
		if (zooStorage != null) {
			path.append(SEPARATOR).append(zooStorage.getData().getClass().getName()).append(SEPARATOR)
					.append(zooStorage.getData().getId());
		}
		return path.toString();
	}

	public static final <T extends ZooId> String createPath(Class<T> klass, String id) {
		StringBuilder path = new StringBuilder();
		if (klass != null) {
			path.append(SEPARATOR).append(klass.getName());
		}
		if (StringUtils.isNotBlank(id)) {
			path.append(SEPARATOR).append(id);
		}
		return path.toString();
	}
}
