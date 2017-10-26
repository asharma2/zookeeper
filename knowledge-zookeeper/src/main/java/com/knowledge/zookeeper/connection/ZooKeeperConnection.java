package com.knowledge.zookeeper.connection;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import com.knowledge.zookeeper.exception.ConnectionException;

public class ZooKeeperConnection implements Connection<ZooKeeper> {

	private ZooKeeper zooKeeper;
	private CountDownLatch signal = new CountDownLatch(1);
	private String contactPoinsts;
	private boolean isConnectionAvailable;

	public ZooKeeperConnection(String contactPoints) {
		this.contactPoinsts = contactPoints;
	}

	@Override
	public ZooKeeper getSession() throws ConnectionException {
		if (!isConnectionAvailable)
			throw new ConnectionException("Unable to acquire connection !!");
		return this.zooKeeper;
	}

	@Override
	public void start() throws ConnectionException {
		try {
			zooKeeper = new ZooKeeper(contactPoinsts, 5000, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					if (event.getState() == KeeperState.SyncConnected) {
						signal.countDown();
					}
				}
			});
			signal.await();
			isConnectionAvailable = true;
		} catch (Exception e) {
			throw new ConnectionException(e);
		}
	}

	@Override
	public void close() throws ConnectionException {

	}

}
