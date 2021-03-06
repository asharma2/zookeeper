package com.knowledge.zookeeper.service;

import java.util.List;

import com.knowledge.zookeeper.constants.PersistenceLevel;
import com.knowledge.zookeeper.exception.ZooIdAlreadyExistsException;
import com.knowledge.zookeeper.exception.ZooIdDoesNotExistsException;
import com.knowledge.zookeeper.exception.ZooStorageException;
import com.knowledge.zookeeper.model.ZooId;
import com.knowledge.zookeeper.model.ZooStorage;

public interface ZooCrudOperation<T extends ZooId> {

	ZooStorage<T> save(ZooStorage<T> zooStorage, PersistenceLevel persistenceLevel)
			throws ZooIdAlreadyExistsException, ZooStorageException;

	ZooStorage<T> update(ZooStorage<T> zooStorage) throws ZooStorageException;

	ZooStorage<T> lookup(Class<T> klass, ZooId zooId) throws ZooIdDoesNotExistsException, ZooStorageException;

	boolean isExists(ZooStorage<T> zooStorage) throws ZooStorageException;

	List<String> getListOfChildrens(ZooStorage<T> zooStorage) throws ZooStorageException;

	void delete(ZooStorage<T> zooStorage) throws ZooStorageException;;

	void delete(Class<T> klass, boolean childrens) throws ZooStorageException;

	void delete(String path, boolean childrens) throws ZooStorageException;

	void addListener(ZooStorage<T> zooStorage) throws ZooStorageException;

	void removeListener(ZooStorage<T> zooStorage) throws ZooStorageException;

}
