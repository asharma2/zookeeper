package com.knowledge.zookeeper.service;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import com.knowledge.zookeeper.constants.ZkSerializer;
import com.knowledge.zookeeper.exception.SerializationException;

public class XmlEDSerializationService<T> implements SerializationService<T, String> {

	public String serialize(T input) throws SerializationException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); XMLEncoder xmlEncoder = new XMLEncoder(baos)) {
			xmlEncoder.writeObject(input);
			return baos.toString();
		} catch (Exception e) {
			throw new SerializationException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public T deserialize(String output, Class<T> klass) throws SerializationException {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8));
				XMLDecoder xmlDecoder = new XMLDecoder(bais)) {
			return (T) xmlDecoder.readObject();
		} catch (Exception e) {
			throw new SerializationException(e);
		}
	}

	public ZkSerializer zkSerializer() {
		return ZkSerializer.XML_ED;
	}

}
