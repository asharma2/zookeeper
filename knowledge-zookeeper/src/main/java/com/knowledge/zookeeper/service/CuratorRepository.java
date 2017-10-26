package com.knowledge.zookeeper.service;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

import org.apache.zookeeper.CreateMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.knowledge.zookeeper.builder.PathBuilder;
import com.knowledge.zookeeper.constants.PersistenceLevel;
import com.knowledge.zookeeper.exception.ZooIdDoesNotExistsException;
import com.knowledge.zookeeper.exception.ZooStorageException;
import com.knowledge.zookeeper.listeners.MetadataListener;
import com.knowledge.zookeeper.model.ZooId;
import com.knowledge.zookeeper.model.ZooStorage;
import com.knowledge.zookeeper.utils.PathUtils;

public abstract class CuratorRepository<T extends ZooId> implements ZooCrudOperation<T> {

	private static final Logger logger = LoggerFactory.getLogger(CuratorRepository.class);
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
			logger.info("Going to persist the record for path: {}", path);
			String data = serializationService.serialize(zooStorage.getData());
			String result = curatorFramework.create().creatingParentsIfNeeded().withMode(createMode(persistenceLevel))
					.forPath(path, data.getBytes());
			System.out.println("Result: " + result);
			return zooStorage;
		} catch (Exception e) {
			logger.error("Exception while persisting the zoo data.", e);
			throw new ZooStorageException(e);
		}
	}

	@Override
	public boolean isExists(ZooStorage<T> zooStorage) throws ZooStorageException {
		try {
			String path = PathUtils.createPath(zooStorage);
			logger.info("Going to check the existence for path: {}", path);
			return curatorFramework.checkExists().forPath(path) != null;
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public List<String> getListOfChildrens(ZooStorage<T> zooStorage) throws ZooStorageException {
		try {
			String path = PathUtils.createPath(zooStorage);
			logger.info("Get list of children for the path: {}", path);
			return curatorFramework.getChildren().forPath(path);
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public void delete(Class<T> klass, boolean childrens) throws ZooStorageException {
		try {
			String path = new PathBuilder().with(klass).build();
			logger.info("Going to delete the zoo data for path: {}, children: {}", path, childrens);
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
		try {
			TreeCache cache = null;
			logger.info("Path: {}", path);
			if (listeners.containsKey(path)) {
				cache = listeners.get(path);
			} else {
				cache = new TreeCache(curatorFramework, path);
				cache.start();
			}
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

	@Override
	public ZooStorage<T> lookup(Class<T> klass, ZooId zooId) throws ZooIdDoesNotExistsException, ZooStorageException {
		String path = PathUtils.createPath(klass, zooId.getId());
		try {
			String data = new String(curatorFramework.getData().forPath(path), StandardCharsets.UTF_8);
			logger.info("Going to delete the zoo data for path: {}", path);
			T zooData = serializationService.deserialize(data, klass);
			return new ZooStorage<T>(zooData, 0);
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public void delete(String path, boolean childrens) throws ZooStorageException {
		try {
			logger.info("Going to delete the zoo data for path: {}, children: {}", path, childrens);
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
	public ZooStorage<T> update(ZooStorage<T> zooStorage) throws ZooStorageException {
		String path = PathUtils.createPath(zooStorage);
		logger.info("Going to update the zoo data for path: {}", path);
		try {
			String data = serializationService.serialize(zooStorage.getData());
			curatorFramework.setData().forPath(path, data.getBytes(StandardCharsets.UTF_8));
			return zooStorage;
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public void delete(ZooStorage<T> zooStorage) throws ZooStorageException {
		String path = PathUtils.createPath(zooStorage);
		logger.info("Going to update the zoo data for path: {}", path);
		try {
			curatorFramework.delete().deletingChildrenIfNeeded().forPath(path);
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
