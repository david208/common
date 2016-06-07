package com.yizhenmoney.common.health.core.indicator.impl;

import java.util.Collection;

import com.yizhenmoney.common.health.core.health.Health.Builder;
import com.yizhenmoney.common.health.core.indicator.AbstractHealthIndicator;
import com.yizhenmoney.common.health.core.metric.Metric;
import com.yizhenmoney.common.health.core.metric.SystemPublicMetrics;

public class SystemHealthIndicator extends AbstractHealthIndicator {

	private SystemPublicMetrics systemPublicMetrics;

	public SystemHealthIndicator(SystemPublicMetrics systemPublicMetrics) {
		this.systemPublicMetrics = systemPublicMetrics;
	}

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		Collection<Metric<?>> set = systemPublicMetrics.metrics();
		builder.up();
		for (Metric<?> metric : set) {
			builder.withDetail(metric.getName(), metric.getValue());
		}

	}
}
