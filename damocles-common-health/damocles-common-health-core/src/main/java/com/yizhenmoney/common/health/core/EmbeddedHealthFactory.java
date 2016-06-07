package com.yizhenmoney.common.health.core;

import java.util.Set;

import com.yizhenmoney.common.health.core.indicator.HealthIndicator;

public interface EmbeddedHealthFactory {
	public EmbeddedHealthContainer doStart(Set<HealthIndicator> healthIndicators, Integer port);

}
