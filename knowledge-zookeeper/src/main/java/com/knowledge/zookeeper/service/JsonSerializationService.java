package com.knowledge.zookeeper.service;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledge.zookeeper.constants.ZkSerializer;
import com.knowledge.zookeeper.exception.SerializationException;

public class JsonSerializationService<T> implements SerializationService<T, String> {

	public String serialize(T input) throws SerializationException {
		try {
			ObjectMapper xmlMapper = new ObjectMapper();
			return xmlMapper.writeValueAsString(input);
		} catch (Exception e) {
			throw new SerializationException(e);
		}
	}

	public T deserialize(String output, Class<T> klass) throws SerializationException {
		try {
			ObjectMapper xmlMapper = new ObjectMapper();
			return xmlMapper.readValue(output.getBytes(StandardCharsets.UTF_8), klass);
		} catch (Exception e) {
			throw new SerializationException(e);
		}
	}

	public ZkSerializer zkSerializer() {
		return ZkSerializer.JSON;
	}

}
