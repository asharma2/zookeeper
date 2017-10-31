package com.knowledge.zookeeper.service;

public interface DistributionService {

	void start();

	void cleanup();

	void loadData();

	void distributeData();

	void stop();
	
	void execute() throws Exception;
}
