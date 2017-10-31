package com.knowledge.zookeeper.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.knowledge.zookeeper.config.ZooKeeperConfig;
import com.knowledge.zookeeper.service.DistributionTemplate;

public class DistributionA {

	public static void main(String[] args) throws Exception {
		AbstractApplicationContext aac = new AnnotationConfigApplicationContext(ZooKeeperConfig.class);
		DistributionTemplate dt = aac.getBean(DistributionTemplate.class);
		dt.distribute();
		aac.close();
	}
}
