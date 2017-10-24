package com.knowledge.zookeeper.exception;

public class SerializationException extends Exception {

	private static final long serialVersionUID = 1L;

	public SerializationException() {
	}

	public SerializationException(String error) {
		super(error);
	}

	public SerializationException(Throwable cause) {
		super(cause);
	}

	public SerializationException(String error, Throwable cause) {
		super(error, cause);
	}

}
