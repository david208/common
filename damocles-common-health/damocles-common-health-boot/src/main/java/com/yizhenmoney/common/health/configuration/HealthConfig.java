package com.yizhenmoney.common.health.configuration;

import java.util.Set;

import javax.sql.DataSource;

import org.apache.solr.client.solrj.SolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.redis.connection.RedisServer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import com.google.code.ssm.CacheFactory;
import com.yizhenmoney.common.health.core.EmbeddedHealthContainer;
import com.yizhenmoney.common.health.core.EmbeddedHealthFactory;
import com.yizhenmoney.common.health.core.indicator.HealthIndicator;
import com.yizhenmoney.common.health.indicator.DataSourceHealthIndicator;
import com.yizhenmoney.common.health.indicator.GridFsHealthIndicator;
import com.yizhenmoney.common.health.indicator.MemcachedHealthIndicator;
import com.yizhenmoney.common.health.indicator.MongoHealthIndicator;
import com.yizhenmoney.common.health.indicator.RedisHealthIndicator;
import com.yizhenmoney.common.health.indicator.SolrHealthIndicator;

@Configuration
@AutoConfigureAfter(FactoryConfig.class)
public class HealthConfig {

	@Value("${management.port:7005}")
	private Integer port;

	@Value("${management.health.enabled:false}")
	private boolean enabled;

	@Autowired
	Set<HealthIndicator> healthIndicators;

	@Autowired
	EmbeddedHealthFactory embeddedHealthFactory;

	@Bean
	public EmbeddedHealthContainer embeddedHealthContainer() {
		if (!isEnabled()) {
			return null;
		}
		if (StringUtils.isEmpty(port))
			return embeddedHealthFactory.doStart(healthIndicators, null);
		else
			return embeddedHealthFactory.doStart(healthIndicators, Integer.valueOf(port));

	}

	public boolean isEnabled() {
		return enabled;
	}

	@ConditionalOnClass({ DataSource.class, JdbcTemplate.class })
	public static class dataSourceHealthConfig {

		@Bean
		public DataSourceHealthIndicator dataSourceHealthIndicator() {
			return new DataSourceHealthIndicator();
		}
	}

	@ConditionalOnClass(RedisServer.class)
	public static class redisHealthConfig {
		@Bean
		public RedisHealthIndicator redisHealthIndicator() {
			return new RedisHealthIndicator();
		}

	}

	@ConditionalOnClass(MongoTemplate.class)
	public static class mongoHealthConfig {

		@Bean
		public MongoHealthIndicator mongoHealthIndicator() {
			return new MongoHealthIndicator();
		}

	}

	@ConditionalOnClass(GridFsTemplate.class)
	public static class gridFsHealthConfig {
		@Bean
		public GridFsHealthIndicator gridFsHealthIndicator() {
			return new GridFsHealthIndicator();
		}

	}

	@ConditionalOnBean(SolrServer.class)
	@AutoConfigureAfter(SolrAutoConfiguration.class)
	public static class solrHealthConfig {
		@Bean
		public SolrHealthIndicator solrHealthIndicator() {
			return new SolrHealthIndicator();
		}

	}

	@ConditionalOnBean(CacheFactory.class)
	public static class memcachedHealthConfig {
		@Bean
		public MemcachedHealthIndicator memcachedHealthIndicator() {
			return new MemcachedHealthIndicator();
		}
	}

}
