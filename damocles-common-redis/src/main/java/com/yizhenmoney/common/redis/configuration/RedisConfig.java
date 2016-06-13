package com.yizhenmoney.common.redis.configuration;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.yizhenmoney.common.redis.service.RedisLockService;
import com.yizhenmoney.common.redis.service.RedisMapService;

@Configuration
@EnableRedisHttpSession
@EnableCaching
public class RedisConfig {

	@Bean
	public JedisConnectionFactory jedisConnFactory(RedisClusterConfiguration redisClusterConfiguration) {
		JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisClusterConfiguration);
		connectionFactory.setUsePool(true);
		return connectionFactory;
	}

	@Bean
	public RedisClusterConfiguration redisClusterConfiguration(
			@Value("${redis.cluster.nodes}") Collection<String> clusterNodes) {
		return new RedisClusterConfiguration(clusterNodes);
	}

	@SuppressWarnings("rawtypes")
	@Bean
	public RedisTemplate redisTemplate(JedisConnectionFactory jedisConnFactory) {
		RedisTemplate redisTemplate = new RedisTemplate();
		redisTemplate.setConnectionFactory(jedisConnFactory);
		return redisTemplate;
	}

	@Bean
	public CacheManager cacheManager(@SuppressWarnings("rawtypes") RedisTemplate redisTemplate, RedisCachePrefix redisCachePrefix) {
		// configure and return an implementation of Spring's CacheManager SPI
		RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
		cacheManager.setUsePrefix(true);
		cacheManager.setCachePrefix(redisCachePrefix);
		return cacheManager;
	}

	@Bean
	public RedisCachePrefix yizhenRedisCachePrefix(@Value("${sys.self.name}") String systemName) {
		YizhenRedisCachePrefix cachePrefix = new YizhenRedisCachePrefix();
		cachePrefix.setPrefix(systemName);
		return cachePrefix;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public RedisMapService redisMapService(RedisTemplate redisTemplate, @Value("${sys.self.name}") String systemName) {
		return new RedisMapService(redisTemplate.opsForHash(), systemName);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public RedisLockService redisLockService(RedisTemplate redisTemplate,
			@Value("${systemCode:crm}") String systemName) {
		return new RedisLockService(redisTemplate.opsForValue(), systemName);
	}

}
