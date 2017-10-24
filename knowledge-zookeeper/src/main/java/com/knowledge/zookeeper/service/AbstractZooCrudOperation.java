package com.knowledge.zookeeper.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.knowledge.zookeeper.constants.PersistenceLevel;
import com.knowledge.zookeeper.exception.ZooStorageException;
import com.knowledge.zookeeper.listeners.MetadataListener;
import com.knowledge.zookeeper.model.ZooId;
import com.knowledge.zookeeper.model.ZooStorage;
import com.knowledge.zookeeper.utils.PathUtils;

public abstract class AbstractZooCrudOperation<T extends ZooId> implements ZooCrudOperation<T> {

	@Autowired
	private SerializationService<T, String> serializationService;
	@Autowired
	private CuratorFramework curatorFramework;

	private Map<String, TreeCache> listeners = new HashMap<>();
	private Multimap<TreeCache, TreeCacheListener> tlisteners = HashMultimap.create();

	@Override
	public ZooStorage<T> save(ZooStorage<T> zooStorage, PersistenceLevel persistenceLevel) throws ZooStorageException {
		try {
			String path = PathUtils.createPath(zooStorage);
			String data = serializationService.serialize(zooStorage.getData());
			String result = curatorFramework.create().creatingParentsIfNeeded().withMode(createMode(persistenceLevel))
					.forPath(path, data.getBytes());
			System.out.println("Result: " + result);
			return zooStorage;
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public boolean isExists(ZooStorage<T> zooStorage) throws ZooStorageException {
		try {
			String path = PathUtils.createPath(zooStorage);
			return curatorFramework.checkExists().forPath(path) != null;
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public List<String> getListOfChildrens(ZooStorage<T> zooStorage) throws ZooStorageException {
		try {
			String path = PathUtils.createPath(zooStorage);
			return curatorFramework.getChildren().forPath(path);
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public void delete(ZooStorage<T> zooStorage, boolean childrens) throws ZooStorageException {
		try {
			String path = PathUtils.createPath(zooStorage);
			if (childrens) {
				curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
			} else {
				curatorFramework.delete().guaranteed().forPath(path);
			}
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public void addListener(ZooStorage<T> zooStorage) throws ZooStorageException {
		String path = PathUtils.createPath(zooStorage);
		TreeCache cache = new TreeCache(curatorFramework, path);
		try {
			cache.start();
			MetadataListener ml = new MetadataListener();
			cache.getListenable().addListener(ml);
			listeners.put(path, cache);
			tlisteners.put(cache, ml);
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public void removeListener(ZooStorage<T> zooStorage) throws ZooStorageException {
		String path = PathUtils.createPath(zooStorage);
		try {
			if (listeners.containsKey(path)) {
				TreeCache cache = listeners.get(path);
				Collection<TreeCacheListener> tcl = tlisteners.get(cache);
				for (TreeCacheListener treeCacheListener : tcl) {
					cache.getListenable().removeListener(treeCacheListener);
				}
				cache.close();
				listeners.remove(path);
			}
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	private CreateMode createMode(PersistenceLevel persistenceLevel) {
		switch (persistenceLevel) {
		case EPHEMERAL:
			return CreateMode.EPHEMERAL;
		case EPHEMERAL_SEQUENCE:
			return CreateMode.EPHEMERAL_SEQUENTIAL;
		case PERSIST:
			return CreateMode.PERSISTENT;
		case PERSIST_SEQUENCE:
			return CreateMode.PERSISTENT_SEQUENTIAL;
		}
		return CreateMode.PERSISTENT;
	}

}
