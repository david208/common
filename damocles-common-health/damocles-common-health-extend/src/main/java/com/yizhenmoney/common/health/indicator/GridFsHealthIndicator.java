/*
 * Copyright 2012-2014 the original author or authors.
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

package com.yizhenmoney.common.health.indicator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.util.Assert;

import com.yizhenmoney.common.health.core.health.Health;
import com.yizhenmoney.common.health.core.indicator.AbstractHealthIndicator;
import com.yizhenmoney.common.health.core.indicator.HealthIndicator;

/**
 * Simple implementation of a {@link HealthIndicator} returning status
 * information for Mongo data stores.
 * 
 * @author sm
 */
public class GridFsHealthIndicator extends AbstractHealthIndicator implements InitializingBean {

	@Autowired(required = false)
	private GridFsTemplate gridFsTemplate;

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		if (valid) {
			this.gridFsTemplate.getResource("测试文件");
			builder.up();
			addBuilder(builder);
		}
		else{
			builder.none();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			Assert.notNull(gridFsTemplate, "MongoTemplate must not be null");
		} catch (Exception e) {
			valid = false;
		}
	}

}
