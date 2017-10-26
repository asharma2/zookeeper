package com.knowledge.zookeeper.connection;

import com.knowledge.zookeeper.exception.ConnectionException;

public interface Connection<T> {

	T getSession() throws ConnectionException;

	void start() throws ConnectionException;

	void close() throws ConnectionException;
}
