package com.knowledge.zookeeper.exception;

public class ZooIdDoesNotExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public ZooIdDoesNotExistsException() {
	}

	public ZooIdDoesNotExistsException(String error) {
		super(error);
	}

	public ZooIdDoesNotExistsException(Throwable cause) {
		super(cause);
	}

	public ZooIdDoesNotExistsException(String error, Throwable cause) {
		super(error, cause);
	}

}
