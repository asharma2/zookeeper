package com.knowledge.zookeeper.service;

import java.util.List;

import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.Watcher;

import com.knowledge.zookeeper.constants.PersistenceLevel;
import com.knowledge.zookeeper.exception.ZooIdAlreadyExistsException;
import com.knowledge.zookeeper.exception.ZooIdDoesNotExistsException;
import com.knowledge.zookeeper.exception.ZooStorageException;
import com.knowledge.zookeeper.model.ZkContainer;
import com.knowledge.zookeeper.model.ZooId;

public interface CrudRepository<T extends ZooId> {

	ZkContainer<T> save(ZkContainer<T> zooStorage, PersistenceLevel persistenceLevel) throws ZooIdAlreadyExistsException, ZooStorageException;
	
	ZkContainer<T> saveAsync(ZkContainer<T> zooStorage, PersistenceLevel persistenceLevel, BackgroundCallback backgroundCallback) throws ZooIdAlreadyExistsException, ZooStorageException;

	ZkContainer<T> update(ZkContainer<T> zooStorage) throws ZooStorageException;
	
	ZkContainer<T> updateAsync(ZkContainer<T> zooStorage,BackgroundCallback backgroundCallback) throws ZooStorageException;

	void delete(ZkContainer<T> zooStorage) throws ZooStorageException;
	
	void delete(Class<T> klass) throws ZooStorageException;

	ZkContainer<T> findByZooId(Class<T> klass, ZooId zooId) throws ZooStorageException;

	List<String> getChildrens(Class<T> klass, boolean recursive) throws ZooIdDoesNotExistsException, ZooStorageException;

	List<String> getChildrens(String path, boolean recursive) throws ZooIdDoesNotExistsException, ZooStorageException;

	void addWatcher(String path, Watcher watcher) throws ZooIdDoesNotExistsException;

	void removeWatcher(String path, Watcher watcher) throws ZooIdDoesNotExistsException;

	void addListener(String path, TreeCacheListener listener) throws ZooIdDoesNotExistsException;

	void removeListener(String path, TreeCacheListener listener) throws ZooIdDoesNotExistsException;

}
