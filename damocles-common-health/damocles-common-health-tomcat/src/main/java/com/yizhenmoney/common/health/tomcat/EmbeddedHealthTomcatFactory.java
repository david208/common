package com.yizhenmoney.common.health.tomcat;

import java.util.Set;

import com.yizhenmoney.common.health.core.EmbeddedHealthFactory;
import com.yizhenmoney.common.health.core.indicator.HealthIndicator;

public class EmbeddedHealthTomcatFactory implements EmbeddedHealthFactory {
	@Override
	public EmbeddedHealthTomcat doStart(Set<HealthIndicator> healthIndicators, Integer port) {
		return new EmbeddedHealthTomcat(healthIndicators, port);

	}
}
