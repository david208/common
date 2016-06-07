package com.yizhenmoney.common.health.indicator;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.yizhenmoney.common.health.core.health.Health;
import com.yizhenmoney.common.health.core.indicator.AbstractHealthIndicator;

/**
 *
 * @author sm
 * @since 1.3.0
 */
public class JmsHealthIndicator extends AbstractHealthIndicator implements InitializingBean {

	@Autowired(required = false)
	private ConnectionFactory connectionFactory;

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		if (valid) {
			Connection connection = this.connectionFactory.createConnection();
			try {
				builder.up().withDetail("provider", connection.getMetaData().getJMSProviderName());
			} finally {
				connection.close();
			}
		} else {
			builder.none();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			Assert.notNull(this.connectionFactory, "ConnectionFactory must not be null");
		} catch (Exception e) {
			valid = false;
		}
	}

}