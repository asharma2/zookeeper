package com.knowledge.zookeeper.service;

import com.knowledge.zookeeper.constants.ZkSerializer;
import com.knowledge.zookeeper.exception.SerializationException;

public interface SerializationService<I, O> {

	O serialize(I input) throws SerializationException;

	I deserialize(O output, Class<I> klass) throws SerializationException;

	ZkSerializer zkSerializer();
}
