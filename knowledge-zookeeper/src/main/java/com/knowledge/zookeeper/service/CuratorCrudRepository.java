package com.knowledge.zookeeper.service;

import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.knowledge.zookeeper.builder.PathBuilder;
import com.knowledge.zookeeper.constants.PersistenceLevel;
import com.knowledge.zookeeper.exception.ZooIdAlreadyExistsException;
import com.knowledge.zookeeper.exception.ZooIdDoesNotExistsException;
import com.knowledge.zookeeper.exception.ZooStorageException;
import com.knowledge.zookeeper.model.ZkContainer;
import com.knowledge.zookeeper.model.ZooId;
import com.knowledge.zookeeper.utils.ByteUtils;

public class CuratorCrudRepository<T extends ZooId> implements CrudRepository<T> {

	private static final Logger logger = LoggerFactory.getLogger(CuratorCrudRepository.class);
	@Autowired
	private CuratorFramework curatorFramework;
	@Autowired
	private SerializationService<T, String> serializationService;

	private Map<String, TreeCache> treeCaches = Maps.newHashMap();
	private Multimap<TreeCache, TreeCacheListener> treeCacheListeners = HashMultimap.create();

	@Override
	public ZkContainer<T> save(ZkContainer<T> zooStorage, PersistenceLevel persistenceLevel)
			throws ZooIdAlreadyExistsException, ZooStorageException {
		String path = new PathBuilder().with(zooStorage).build();
		try {
			logger.info("Path: {}", path);
			byte[] data = ByteUtils.getBytes(serializationService.serialize(zooStorage.getData()));
			curatorFramework.create().creatingParentsIfNeeded().withMode(createMode(persistenceLevel)).forPath(path,
					data);
			return zooStorage;
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public ZkContainer<T> saveAsync(ZkContainer<T> zooStorage, PersistenceLevel persistenceLevel,
			BackgroundCallback backgroundCallback) throws ZooIdAlreadyExistsException, ZooStorageException {
		String path = new PathBuilder().with(zooStorage).build();
		try {
			logger.info("Path: {}", path);
			byte[] data = ByteUtils.getBytes(serializationService.serialize(zooStorage.getData()));
			curatorFramework.create().creatingParentsIfNeeded().inBackground(backgroundCallback).forPath(path, data);
			return zooStorage;
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public ZkContainer<T> update(ZkContainer<T> zooStorage) throws ZooStorageException {
		String path = new PathBuilder().with(zooStorage).build();
		try {
			logger.info("Path: {}", path);
			byte[] data = ByteUtils.getBytes(serializationService.serialize(zooStorage.getData()));
			curatorFramework.setData().forPath(path, data);
			return zooStorage;
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public ZkContainer<T> updateAsync(ZkContainer<T> zooStorage, BackgroundCallback backgroundCallback)
			throws ZooStorageException {
		try {
			String path = new PathBuilder().with(zooStorage).build();
			logger.info("Path: {}", path);
			byte[] data = ByteUtils.getBytes(serializationService.serialize(zooStorage.getData()));
			curatorFramework.setData().inBackground(backgroundCallback).forPath(path, data);
			return zooStorage;
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public void delete(ZkContainer<T> zooStorage) throws ZooStorageException {
		try {
			String path = new PathBuilder().with(zooStorage).build();
			logger.info("Path: {}", path);
			curatorFramework.delete().guaranteed().forPath(path);
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public void delete(Class<T> klass) throws ZooStorageException {
		try {
			String path = new PathBuilder().with(klass).build();
			logger.info("Path: {}", path);
			curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public ZkContainer<T> findByZooId(Class<T> klass, ZooId zooId) throws ZooStorageException {
		try {
			String path = new PathBuilder().with(klass).with(zooId.getId()).build();
			logger.info("Path: {}", path);
			String data = ByteUtils.getString(curatorFramework.getData().forPath(path));
			T zooData = serializationService.deserialize(data, klass);
			return new ZkContainer<T>(zooData);
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public List<String> getChildrens(Class<T> klass, boolean recursive)
			throws ZooIdDoesNotExistsException, ZooStorageException {
		try {
			String path = new PathBuilder().with(klass).build();
			logger.info("Path: {}", path);
			return curatorFramework.getChildren().forPath(path);
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public List<String> getChildrens(String path, boolean recursive)
			throws ZooIdDoesNotExistsException, ZooStorageException {
		try {
			logger.info("Path: {}", path);
			return curatorFramework.getChildren().forPath(path);
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public void addWatcher(String path, Watcher watcher) throws ZooIdDoesNotExistsException {
		try {
			logger.info("Path: {}", path);
			curatorFramework.getChildren().usingWatcher(watcher).forPath(path);
		} catch (Exception e) {
			throw new ZooIdDoesNotExistsException(e);
		}
	}

	@Override
	public void removeWatcher(String path, Watcher watcher) throws ZooIdDoesNotExistsException {
		try {
			logger.info("Path: {}", path);
		} catch (Exception e) {
			throw new ZooIdDoesNotExistsException(e);
		}
	}

	@Override
	public void addListener(String path, TreeCacheListener listener) throws ZooIdDoesNotExistsException {
		try {
			logger.info("Path: {}", path);
			if (!treeCaches.containsKey(path)) {
				TreeCache cache = new TreeCache(curatorFramework, path);
				cache.start();
				treeCaches.put(path, cache);
			}
			treeCacheListeners.get(treeCaches.get(path)).add(listener);
			treeCaches.get(path).getListenable().addListener(listener);
		} catch (Exception e) {
			throw new ZooIdDoesNotExistsException(e);
		}
	}

	@Override
	public void removeListener(String path, TreeCacheListener listener) throws ZooIdDoesNotExistsException {
		try {
			logger.info("Path: {}", path);
			if (treeCaches.containsKey(path)) {
				treeCaches.get(path).getListenable().removeListener(listener);
				// treeCaches.get(path).close();
			}
		} catch (Exception e) {
			throw new ZooIdDoesNotExistsException(e);
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
