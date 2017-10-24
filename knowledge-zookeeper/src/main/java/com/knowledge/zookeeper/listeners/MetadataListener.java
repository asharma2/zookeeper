package com.knowledge.zookeeper.listeners;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.utils.ZKPaths;

public class MetadataListener implements TreeCacheListener {

	@Override
	public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
		switch (event.getType()) {
		case NODE_ADDED: {
			System.out.println("TreeNode added: " + ZKPaths.getNodeFromPath(event.getData().getPath()) + ", value: "
					+ new String(event.getData().getData()));
			break;
		}
		case NODE_UPDATED: {
			System.out.println("TreeNode changed: " + ZKPaths.getNodeFromPath(event.getData().getPath()) + ", value: "
					+ new String(event.getData().getData()));
			break;
		}
		case NODE_REMOVED: {
			System.out.println("TreeNode removed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
			break;
		}
		default:
			System.out.println("Other event: " + event.getType().name());
		}
	}

}
