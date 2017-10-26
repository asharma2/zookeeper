package com.knowledge.zookeeper.conditions;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.knowledge.zookeeper.constants.PropConstants;
import com.knowledge.zookeeper.constants.ZkSerializer;

public class JSONCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		String serializer = context.getEnvironment().getProperty(PropConstants.ZOO_SERIALIZER_NAME);
		return ZkSerializer.JSON.name().equalsIgnoreCase(serializer);
	}

}
