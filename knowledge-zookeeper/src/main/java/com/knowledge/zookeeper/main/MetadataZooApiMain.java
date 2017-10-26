package com.knowledge.zookeeper.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.knowledge.zookeeper.config.ZooKeeperConfig;
import com.knowledge.zookeeper.constants.PersistenceLevel;
import com.knowledge.zookeeper.model.Metadata;
import com.knowledge.zookeeper.model.ZooStorage;
import com.knowledge.zookeeper.service.MetadataRepository;

public class MetadataZooApiMain {

	public static void main(String[] args) throws Exception {
		AbstractApplicationContext aac = new AnnotationConfigApplicationContext(ZooKeeperConfig.class);
		MetadataRepository mcs = aac.getBean(MetadataRepository.class);

		Metadata metadata1 = new Metadata();
		metadata1.setName("threads");
		metadata1.setValue("100");
		mcs.addListener(new ZooStorage<Metadata>(metadata1, 0));

		Metadata metadata2 = new Metadata();
		metadata2.setName("sync_flag");
		metadata2.setValue("true");

		mcs.save(new ZooStorage<Metadata>(metadata1, 0), PersistenceLevel.PERSIST);
		mcs.save(new ZooStorage<Metadata>(metadata2, 0), PersistenceLevel.PERSIST);
		System.out.println("--- saved -----");
		ZooStorage<Metadata> zMetadata1 = mcs.lookup(Metadata.class, metadata1);
		ZooStorage<Metadata> zMetadata2 = mcs.lookup(Metadata.class, metadata2);
		System.out.println("--- lookup -----" + zMetadata1.getData());
		System.out.println("--- lookup -----" + zMetadata2.getData());
		zMetadata1.getData().setValue("20");
		zMetadata2.getData().setValue("false");
		mcs.update(zMetadata1);
		mcs.update(zMetadata2);
		System.out.println("--- updated -----");
		mcs.delete(zMetadata1);
		mcs.delete(zMetadata2);
		System.out.println("--- deleted -----");
	}
}
