package com.yizhenmoney.common.redis.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.yizhenmoney.common.redis.aop.RefuseResubmitAspect;
import com.yizhenmoney.common.redis.service.RedisLockService;

@Configuration
@Import(RedisConfig.class)
public class RefuseResbumitConfig {

	@Bean
	public RefuseResubmitAspect refuseResubmitAspect(RedisLockService redisLockService) {
		return new RefuseResubmitAspect(redisLockService);
	}

}
