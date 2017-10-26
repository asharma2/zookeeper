package com.knowledge.zookeeper;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.shaded.com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.knowledge.zookeeper.config.ZooKeeperConfig;
import com.knowledge.zookeeper.constants.PersistenceLevel;
import com.knowledge.zookeeper.model.Movie;
import com.knowledge.zookeeper.model.ZkContainer;
import com.knowledge.zookeeper.service.CuratorCrudRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ZooKeeperConfig.class, loader = AnnotationConfigContextLoader.class)
public class TestCuratorCrudRepository {

	private static final Logger logger = LoggerFactory.getLogger(TestCuratorCrudRepository.class);

	@Autowired
	private CuratorCrudRepository<Movie> movieCrudRepo;

	@Test
	public void createMovide() throws Exception {
		TimeUnit.SECONDS.sleep(2);
		Movie movie = new Movie();
		movie.setId("AC#1");
		movie.setActors(Sets.newHashSet("Robert Dawyne", "Chris Hemsworth", "Chris Evans"));
		movie.setBudget(100);
		movie.setCategory("Comic");
		movie.setName("Avengers");
		movie.setReleasedOn(new Date(System.currentTimeMillis()));
		TreeCacheListener tcl = new TreeCacheListener() {

			@Override
			public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
				logger.info("EventType: {}, Path: {} , Data: {}", event.getType(), event.getData().getPath(),
						new String(event.getData().getData()));
			}
		};
		movieCrudRepo.addListener("/knowledge/com.knowledge.zookeeper.model.Movie", tcl);
		TimeUnit.SECONDS.sleep(2);
		movieCrudRepo.save(new ZkContainer<Movie>(movie), PersistenceLevel.PERSIST);
		TimeUnit.SECONDS.sleep(2);
		logger.info("delete");
		movieCrudRepo.delete(new ZkContainer<Movie>(movie));
	}

}
