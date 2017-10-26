package com.knowledge.zookeeper.utils;

import java.nio.charset.StandardCharsets;

public final class ByteUtils {

	public static final byte[] getBytes(String data) {
		return data.getBytes(StandardCharsets.UTF_8);
	}

	public static final String getString(byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_8);
	}
}
