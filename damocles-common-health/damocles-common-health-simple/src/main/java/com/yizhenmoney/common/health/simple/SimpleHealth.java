package com.yizhenmoney.common.health.simple;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yizhenmoney.common.health.core.health.Health;
import com.yizhenmoney.common.health.core.indicator.HealthIndicator;
import com.yizhenmoney.common.health.core.indicator.impl.ApplicationHealthIndicator;
import com.yizhenmoney.common.health.core.indicator.impl.CompositeHealthIndicator;
import com.yizhenmoney.common.health.core.indicator.impl.DiskSpaceHealthIndicator;
import com.yizhenmoney.common.health.core.indicator.impl.DiskSpaceHealthIndicatorProperties;
import com.yizhenmoney.common.health.core.indicator.impl.SystemHealthIndicator;
import com.yizhenmoney.common.health.core.metric.SystemPublicMetrics;

/**
 * 简易监测模块
 * 
 * @author stone
 */
public class SimpleHealth implements HealthIndicator {
	private HealthIndicator healthIndicator;
	private ObjectMapper objectMapper = new ObjectMapper();

	public SimpleHealth() {
		Set<HealthIndicator> set = new HashSet<HealthIndicator>();
		set.add(new DiskSpaceHealthIndicator(new DiskSpaceHealthIndicatorProperties()));
		set.add(new ApplicationHealthIndicator());
		set.add(new SystemHealthIndicator(new SystemPublicMetrics()));
		healthIndicator = new CompositeHealthIndicator(set);
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	}

	@Override
	public Health health() {
		return healthIndicator.health();
	}

	/**
	 * 返回json格式化信息
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	public String toJson() throws JsonProcessingException {
		return objectMapper.writeValueAsString(health());
	}
}
