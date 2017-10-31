package com.knowledge.zookeeper.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistributionServiceImpl extends LeaderSelectorListenerAdapter implements DistributionService {

	private static final Logger logger = LoggerFactory.getLogger(DistributionServiceImpl.class);
	@Autowired
	private CuratorFramework curatorFramework;
	private LeaderLatch leaderLatch;
	private CountDownLatch signal = new CountDownLatch(1);
	private static final String LEADER_PATH = "/aks/distribution";
	private TreeCache cache;

	public final void execute() throws Exception {
		start();
		if (this.leaderLatch.hasLeadership()) {
			cleanup();
		}
		signal.await(10, TimeUnit.SECONDS);
		loadData();
		distributeData();
		stop();
	}

	@Override
	public void start() {
		logger.info("Going to start the leader selection ..");
		this.leaderLatch = new LeaderLatch(curatorFramework, LEADER_PATH);
		try {
			this.leaderLatch.start();
			this.cache = new TreeCache(curatorFramework, LEADER_PATH);
			this.cache.start();
			this.cache.getListenable().addListener(new TreeCacheListener() {

				@Override
				public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
					logger.info("event: " + event.getType());
					logger.info("TreeNode added: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
					if (event.getData().getData() != null) {
						String data = new String(event.getData().getData());
						if (StringUtils.containsIgnoreCase(data, "cleanup_done")) {
							signal.countDown();
						}
					}
				}
			});
		} catch (Exception e) {
			logger.error("Exception while leader election", e);
		}
	}

	@Override
	public void cleanup() {
		logger.info("clean up");
		try {
			TimeUnit.SECONDS.sleep(5);
			curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(LEADER_PATH, "cleanup_done".getBytes());
		} catch (Exception e) {
			logger.error("error in cleanup", e);
		}
	}

	@Override
	public void loadData() {
		logger.info("load data");
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
		}

	}

	@Override
	public void distributeData() {
		logger.info("distribute data");
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void stop() {
		try {
			this.leaderLatch.close();
			this.cache.close();
		} catch (Exception e) {
			logger.error("Exception while leader election", e);
		}
	}

	@Override
	public void takeLeadership(CuratorFramework client) throws Exception {
		logger.info("take leader ship called");
	}

}
