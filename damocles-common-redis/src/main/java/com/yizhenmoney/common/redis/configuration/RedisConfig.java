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
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.yizhenmoney.common.redis.aop.DistributedLockAspect;
import com.yizhenmoney.common.redis.service.RedisListService;
import com.yizhenmoney.common.redis.service.RedisLockService;
import com.yizhenmoney.common.redis.service.RedisMapService;
import com.yizhenmoney.common.redis.service.RedisSetService;
import com.yizhenmoney.common.redis.service.RedisStringService;

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
	public CacheManager cacheManager(@SuppressWarnings("rawtypes") RedisTemplate redisTemplate,
			RedisCachePrefix redisCachePrefix, @Value("${redis.cluster.cache.expiration:3600}") Long expiration) {
		// configure and return an implementation of Spring's CacheManager SPI
		RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
		cacheManager.setUsePrefix(true);
		cacheManager.setCachePrefix(redisCachePrefix);
		cacheManager.setDefaultExpiration(expiration);
		return cacheManager;
	}

	@Bean
	public RedisCachePrefix yizhenRedisCachePrefix(@Value("${sys.self.name}") String systemName,RedisOperationsSessionRepository sessionRepository) {
		YizhenRedisCachePrefix cachePrefix = new YizhenRedisCachePrefix();
		cachePrefix.setPrefix(systemName);
		sessionRepository.setRedisKeyNamespace(systemName);
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
			@Value("${sys.self.name}") String systemName) {
		return new RedisLockService(redisTemplate.opsForValue(), systemName);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public RedisListService redisListService(RedisTemplate redisTemplate,
			@Value("${sys.self.name}") String systemName) {
		return new RedisListService(redisTemplate.opsForList(), systemName);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public RedisSetService redisSetService(RedisTemplate redisTemplate, @Value("${sys.self.name}") String systemName) {
		return new RedisSetService(redisTemplate.opsForSet(), systemName);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public RedisStringService redisStringService(RedisTemplate redisTemplate,
			@Value("${sys.self.name}") String systemName) {
		return new RedisStringService(redisTemplate.opsForValue(), systemName);
	}

	@Bean
	public DistributedLockAspect distributedLockAspect(RedisLockService redisLockService) {
		return new DistributedLockAspect(redisLockService);
	}
	

}
