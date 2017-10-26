package com.knowledge.zookeeper.exception;

public class ConnectionException extends Exception {

	private static final long serialVersionUID = 1L;

	public ConnectionException() {
	}

	public ConnectionException(String error) {
		super(error);
	}

	public ConnectionException(Throwable cause) {
		super(cause);
	}

	public ConnectionException(String error, Throwable cause) {
		super(error, cause);
	}

}
