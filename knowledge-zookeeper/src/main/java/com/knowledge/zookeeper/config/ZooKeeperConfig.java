package com.knowledge.zookeeper.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.knowledge.zookeeper.conditions.JSONCondition;
import com.knowledge.zookeeper.conditions.XEDCondition;
import com.knowledge.zookeeper.conditions.XMCondition;
import com.knowledge.zookeeper.connection.ZooKeeperConnection;
import com.knowledge.zookeeper.constants.PropConstants;
import com.knowledge.zookeeper.model.Movie;
import com.knowledge.zookeeper.service.CuratorCrudRepository;
import com.knowledge.zookeeper.service.JsonSerializationService;
import com.knowledge.zookeeper.service.MetadataConfigurationService;
import com.knowledge.zookeeper.service.SerializationService;
import com.knowledge.zookeeper.service.XmlEDSerializationService;
import com.knowledge.zookeeper.service.XmlMapSerializationService;

import org.apache.curator.framework.CuratorFrameworkFactory.Builder;

@Configuration
@ComponentScan(basePackages = { "com.knowledge.zookeeper.service", "com.knowledge.zookeeper.template" })
@PropertySource(value = "classpath:zoo.properties")
public class ZooKeeperConfig {

	@Autowired
	private Environment environment;

	@Bean(initMethod = "start", destroyMethod = "close")
	public ZooKeeperConnection zooKeeperConnection() {
		String zkContactPoints = environment.getProperty(PropConstants.CONTACT_POINTS);
		return new ZooKeeperConnection(zkContactPoints);
	}

	@Bean(initMethod = "start", destroyMethod = "close")
	public CuratorFramework curatorFramework() {
		String zkContactPoints = environment.getProperty(PropConstants.CONTACT_POINTS);
		RetryPolicy rp = new ExponentialBackoffRetry(1000, 3);
		Builder builder = CuratorFrameworkFactory.builder().connectString(zkContactPoints).connectionTimeoutMs(5000)
				.sessionTimeoutMs(5000).retryPolicy(rp);
		builder.namespace(environment.getProperty(PropConstants.NAME_SPACE));
		CuratorFramework curatorFramework = builder.build();
		return curatorFramework;
	}

	@Bean
	@Conditional(XMCondition.class)
	public <T> SerializationService<T, String> xmSerializationService() {
		return new XmlMapSerializationService<T>();
	}

	@Bean
	@Conditional(XEDCondition.class)
	public <T> SerializationService<T, String> xedSerializationService() {
		return new XmlEDSerializationService<T>();
	}

	@Bean
	@Conditional(JSONCondition.class)
	public <T> SerializationService<T, String> serializationService() {
		return new JsonSerializationService<T>();
	}

	@Bean
	public MetadataConfigurationService metadataConfigurationService() {
		return new MetadataConfigurationService();
	}
	
	@Bean
	public CuratorCrudRepository<Movie> movieCrudRepo() {
		return new CuratorCrudRepository<Movie>();
	}

}
