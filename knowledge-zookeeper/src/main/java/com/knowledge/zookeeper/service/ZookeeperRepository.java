package com.knowledge.zookeeper.service;

import java.util.List;
import java.util.Map;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.google.common.collect.Maps;
import com.knowledge.zookeeper.builder.PathBuilder;
import com.knowledge.zookeeper.connection.ZooKeeperConnection;
import com.knowledge.zookeeper.constants.PersistenceLevel;
import com.knowledge.zookeeper.constants.PropConstants;
import com.knowledge.zookeeper.exception.ConnectionException;
import com.knowledge.zookeeper.exception.ZooIdAlreadyExistsException;
import com.knowledge.zookeeper.exception.ZooIdDoesNotExistsException;
import com.knowledge.zookeeper.exception.ZooStorageException;
import com.knowledge.zookeeper.model.ZooId;
import com.knowledge.zookeeper.model.ZooStorage;

public class ZookeeperRepository<T extends ZooId> implements ZooCrudOperation<T> {

	private static final Logger logger = LoggerFactory.getLogger(ZookeeperRepository.class);
	@Autowired
	private ZooKeeperConnection zooKeeperConnection;
	@Autowired
	private SerializationService<T, String> serializationService;
	@Autowired
	private Environment environment;

	private Map<String, Watcher> watchers = Maps.newHashMap();

	@Override
	public ZooStorage<T> save(ZooStorage<T> zooStorage, PersistenceLevel persistenceLevel)
			throws ZooIdAlreadyExistsException, ZooStorageException {
		return saveOrUpdate(zooStorage, persistenceLevel, true);
	}

	@Override
	public ZooStorage<T> update(ZooStorage<T> zooStorage) throws ZooStorageException {
		return saveOrUpdate(zooStorage, null, false);
	}

	protected ZooStorage<T> saveOrUpdate(ZooStorage<T> zooStorage, PersistenceLevel persistenceLevel, boolean insert)
			throws ZooStorageException {
		try {
			String path = new PathBuilder().with(environment.getProperty(PropConstants.NAME_SPACE))
					.with(zooStorage.getData()).build();
			String data = serializationService.serialize(zooStorage.getData());
			if (insert) {
				zooKeeperConnection.getSession().create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE,
						createMode(persistenceLevel));
			} else {
				Stat stat = zooKeeperConnection.getSession().setData(path, data.getBytes(), zooStorage.getVersion());
				zooStorage.setVersion(stat.getAversion());
				zooStorage.setStat(stat);
			}
			return zooStorage;
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public ZooStorage<T> lookup(Class<T> klass, ZooId zooId) throws ZooIdDoesNotExistsException, ZooStorageException {
		String _path = new PathBuilder().with(environment.getProperty(PropConstants.NAME_SPACE)).with(klass)
				.with(zooId.getId()).build();
		Stat stat;
		try {
			stat = zooKeeperConnection.getSession().exists(_path, false);
			byte[] bytes = zooKeeperConnection.getSession().getData(_path, true, stat);
			T deserData = serializationService.deserialize(new String(bytes), klass);
			return new ZooStorage<T>(deserData, stat.getVersion());
		} catch (KeeperException e) {
			if (e.code() == Code.NONODE) {
				throw new ZooIdDoesNotExistsException(e);
			}
			throw new ZooStorageException(e);
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public boolean isExists(ZooStorage<T> zooStorage) throws ZooStorageException {
		String path = new PathBuilder().with(environment.getProperty(PropConstants.NAME_SPACE))
				.with(zooStorage.getData()).build();
		try {
			Stat stat = zooKeeperConnection.getSession().exists(path, true);
			return stat != null;
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public List<String> getListOfChildrens(ZooStorage<T> zooStorage) throws ZooStorageException {
		try {
			String path = new PathBuilder().with(environment.getProperty(PropConstants.NAME_SPACE))
					.with(zooStorage.getData().getClass()).build();
			return zooKeeperConnection.getSession().getChildren(path, true);
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public void delete(Class<T> klass, boolean childrens) throws ZooStorageException {
		try {
			String path = new PathBuilder().with(environment.getProperty(PropConstants.NAME_SPACE)).with(klass).build();
			zooKeeperConnection.getSession().delete(path, 1);
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public void delete(String path, boolean childrens) throws ZooStorageException {
		try {
			zooKeeperConnection.getSession().delete(path, 1);
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public void addListener(ZooStorage<T> zooStorage) throws ZooStorageException {
		String path = new PathBuilder().with(environment.getProperty(PropConstants.NAME_SPACE))
				.with(zooStorage.getData().getClass()).build();
		try {
			Watcher watcher = new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					String path = event.getPath();
					logger.info("Path: {}", path);
					EventType et = event.getType();
					switch (et) {
					case NodeChildrenChanged:
						logger.info("child changed ");
						break;
					case NodeCreated:
						logger.info("child created ");
						break;
					case NodeDeleted:
						logger.info("child deleted ");
						break;
					case NodeDataChanged:
						logger.info("node data changed");
						break;
					case None:
						logger.info("no event occured");
						break;
					}
				}
			};
			if (!watchers.containsKey(path)) {
				watchers.put(path, watcher);
				zooKeeperConnection.getSession().getChildren(path, watcher);
			}
		} catch (KeeperException | InterruptedException | ConnectionException e) {
			throw new ZooStorageException(e);
		}
	}

	@Override
	public void removeListener(ZooStorage<T> zooStorage) throws ZooStorageException {
		String path = new PathBuilder().with(environment.getProperty(PropConstants.NAME_SPACE))
				.with(zooStorage.getData()).build();
		if (!watchers.containsKey(path)) {
			watchers.remove(path);
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

	@Override
	public void delete(ZooStorage<T> zooStorage) throws ZooStorageException {
		try {
			String path = new PathBuilder().with(environment.getProperty(PropConstants.NAME_SPACE))
					.with(zooStorage.getData()).build();
			zooKeeperConnection.getSession().delete(path, 1);
		} catch (Exception e) {
			throw new ZooStorageException(e);
		}
	}

}
