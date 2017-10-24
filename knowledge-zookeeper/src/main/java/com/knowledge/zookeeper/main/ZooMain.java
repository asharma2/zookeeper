package com.knowledge.zookeeper.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.knowledge.zookeeper.config.ZooKeeperConfig;
import com.knowledge.zookeeper.constants.PersistenceLevel;
import com.knowledge.zookeeper.model.Metadata;
import com.knowledge.zookeeper.model.ZooStorage;
import com.knowledge.zookeeper.service.MetadataConfigurationService;

public class ZooMain {

	public static void main(String[] args) throws Exception {
		AbstractApplicationContext aac = new AnnotationConfigApplicationContext(ZooKeeperConfig.class);
		MetadataConfigurationService mcs = aac.getBean(MetadataConfigurationService.class);
		Metadata m = new Metadata();
		m.setName("thread.count");
		m.setValue("100");
		mcs.addListener(new ZooStorage<Metadata>(m, 0));
		mcs.save(new ZooStorage<Metadata>(m, 0), PersistenceLevel.EPHEMERAL);
		mcs.save(new ZooStorage<Metadata>(m, 0), PersistenceLevel.EPHEMERAL);
		System.out.println("Created....");
	}
}
