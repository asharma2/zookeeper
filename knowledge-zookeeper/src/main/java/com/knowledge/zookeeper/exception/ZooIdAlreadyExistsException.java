package com.knowledge.zookeeper.exception;

public class ZooIdAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public ZooIdAlreadyExistsException() {
	}

	public ZooIdAlreadyExistsException(String error) {
		super(error);
	}

	public ZooIdAlreadyExistsException(Throwable cause) {
		super(cause);
	}

	public ZooIdAlreadyExistsException(String error, Throwable cause) {
		super(error, cause);
	}

}
