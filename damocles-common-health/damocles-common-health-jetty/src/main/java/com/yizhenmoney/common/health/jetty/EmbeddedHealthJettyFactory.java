package com.yizhenmoney.common.health.jetty;

import java.util.Set;

import com.yizhenmoney.common.health.core.EmbeddedHealthFactory;
import com.yizhenmoney.common.health.core.indicator.HealthIndicator;

public class EmbeddedHealthJettyFactory implements EmbeddedHealthFactory {
	@Override
	public EmbeddedHealthJetty doStart(Set<HealthIndicator> healthIndicators, Integer port) {
		return new EmbeddedHealthJetty(healthIndicators, port);

	}
}
