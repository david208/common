/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yizhenmoney.common.health.core.indicator.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.yizhenmoney.common.health.core.health.Health;
import com.yizhenmoney.common.health.core.health.Status;
import com.yizhenmoney.common.health.core.indicator.HealthIndicator;

/**
 * {@link HealthIndicator} that returns health indications from all registered
 * delegates.
 * 
 * @author Tyler J. Frederick
 * @author Phillip Webb
 * @author Christian Dupuis
 * @since 1.1.0
 */
public class CompositeHealthIndicator implements HealthIndicator {

	private final Map<String, HealthIndicator> indicators;

	/**
	 * Create a new {@link CompositeHealthIndicator} from the specified
	 * indicators.
	 * 
	 * @param healthAggregator
	 *            the health aggregator
	 * @param indicators
	 *            a map of {@link HealthIndicator}s with the key being used as
	 *            an indicator name.
	 */
	public CompositeHealthIndicator(Map<String, HealthIndicator> indicators) {
		this.indicators = new LinkedHashMap<String, HealthIndicator>(indicators);
	}

	public CompositeHealthIndicator(Set<HealthIndicator> indicators) {
		Map<String, HealthIndicator> indicatorMap = new LinkedHashMap<String, HealthIndicator>();

		for (HealthIndicator healthIndicator : indicators) {
			indicatorMap.put(StringUtils.substringBefore(healthIndicator.getClass().getSimpleName(), "Health").toLowerCase(), healthIndicator);
		}
		this.indicators = indicatorMap;
	}

	public void addHealthIndicator(String name, HealthIndicator indicator) {
		this.indicators.put(name, indicator);
	}

	@Override
	public Health health() {
		Map<String, Health> healths = new LinkedHashMap<String, Health>();
		Status allStatus = Status.UP;
		for (Map.Entry<String, HealthIndicator> entry : this.indicators.entrySet()) {
			Health health = entry.getValue().health();
			if (!health.getStatus().equals(Status.NONE)) {
				if (!health.getStatus().equals(Status.UP))
					allStatus = health.getStatus();
				healths.put(entry.getKey(), health);
			}
		}
		return new Health.Builder(allStatus, healths).build();
	}

}
