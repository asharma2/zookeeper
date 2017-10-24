package com.knowledge.zookeeper.service;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.knowledge.zookeeper.constants.ZkSerializer;
import com.knowledge.zookeeper.exception.SerializationException;

public class XmlMapSerializationService<T> implements SerializationService<T, String> {

	public String serialize(T input) throws SerializationException {
		try {
			XmlMapper xmlMapper = new XmlMapper();
			return xmlMapper.writeValueAsString(input);
		} catch (Exception e) {
			throw new SerializationException(e);
		}
	}

	public T deserialize(String output, Class<T> klass) throws SerializationException {
		try {
			XmlMapper xmlMapper = new XmlMapper();
			return xmlMapper.readValue(output.getBytes(StandardCharsets.UTF_8), klass);
		} catch (Exception e) {
			throw new SerializationException(e);
		}
	}

	public ZkSerializer zkSerializer() {
		return ZkSerializer.XML_MAP;
	}

}
