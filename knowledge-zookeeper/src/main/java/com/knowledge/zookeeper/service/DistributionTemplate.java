package com.knowledge.zookeeper.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class DistributionTemplate {

	@Autowired
	private DistributionService distributionService;

	public void distribute() throws Exception {
		distributionService.execute();
	}
}
