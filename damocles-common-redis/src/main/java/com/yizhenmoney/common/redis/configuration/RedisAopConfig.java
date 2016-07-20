package com.yizhenmoney.common.redis.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.yizhenmoney.common.redis.aop.DistributedLockAspect;
import com.yizhenmoney.common.redis.aop.RefuseResubmitAspect;
import com.yizhenmoney.common.redis.service.RedisLockService;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class RedisAopConfig {

	@Bean
	public DistributedLockAspect distributedLockAspect(RedisLockService redisLockService) {
		return new DistributedLockAspect(redisLockService);
	}

	@Bean
	public RefuseResubmitAspect refuseResubmitAspect(RedisLockService redisLockService) {
		return new RefuseResubmitAspect(redisLockService);
	}

}
