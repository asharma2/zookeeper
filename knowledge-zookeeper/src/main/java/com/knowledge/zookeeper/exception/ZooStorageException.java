package com.knowledge.zookeeper.exception;

public class ZooStorageException extends Exception {

	private static final long serialVersionUID = 1L;

	public ZooStorageException() {
	}

	public ZooStorageException(String error) {
		super(error);
	}

	public ZooStorageException(Throwable cause) {
		super(cause);
	}

	public ZooStorageException(String error, Throwable cause) {
		super(error, cause);
	}

}
