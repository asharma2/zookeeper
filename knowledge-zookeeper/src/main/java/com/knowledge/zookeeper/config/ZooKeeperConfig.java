package com.knowledge.zookeeper.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.knowledge.zookeeper.service.MetadataConfigurationService;
import com.knowledge.zookeeper.service.SerializationService;
import com.knowledge.zookeeper.service.XmlMapSerializationService;

import org.apache.curator.framework.CuratorFrameworkFactory.Builder;

@Configuration
@ComponentScan(basePackages = { "com.knowledge.zookeeper.service", "com.knowledge.zookeeper.template" })
@PropertySource(value = "classpath:zoo.properties")
public class ZooKeeperConfig {

	@Autowired
	private Environment environment;

	@Bean(initMethod = "start", destroyMethod = "close")
	public CuratorFramework curatorFramework() {
		String zkContactPoints = environment.getProperty("zoo.contactpoints");
		RetryPolicy rp = new ExponentialBackoffRetry(1000, 3);
		Builder builder = CuratorFrameworkFactory.builder().connectString(zkContactPoints).connectionTimeoutMs(5000)
				.sessionTimeoutMs(5000).retryPolicy(rp);
		builder.namespace(environment.getProperty("zoo.namespace"));
		CuratorFramework curatorFramework = builder.build();
		return curatorFramework;
	}

	@Bean
	public <T> SerializationService<T, String> serializationService() {
		return new XmlMapSerializationService<T>();
	}

	@Bean
	public MetadataConfigurationService metadataConfigurationService() {
		return new MetadataConfigurationService();
	}

}
