package com.yizhenmoney.common.redis.aop;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yizhenmoney.common.redis.service.AbstractRedisService;
import com.yizhenmoney.common.redis.service.RedisLockService;

/**
 * 
 * @description 重复提交过滤切面
 * @author sm
 */
@Aspect
public class DistributedLockAspect {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DistributedLockAspect.class);


	private RedisLockService redisLockService;

	public DistributedLockAspect(RedisLockService redisLockService) {
		this.redisLockService = redisLockService;
	}

	/**
	 * 
	 * @description 切点
	 * @author sm
	 */
	@Pointcut("@annotation(com.yizhenmoney.common.redis.aop.DistributedLock)")
	public void distributedLock() {

	}

	/**
	 * 
	 * @description 用户操作日志通知，围绕方法会记录结果
	 * @param jp
	 *            连接点
	 * @author sm
	 * @throws Exception
	 * @throws Throwable
	 */
	@Around("distributedLock()")
	public Object distributedLockOperate(ProceedingJoinPoint jp) throws Throwable {
		DistributedLock distributedLock = this.getAnnotation(jp);
		if (!redisLockService.lock(distributedLock.type(), distributedLock.key(), distributedLock.expire(),
				TimeUnit.SECONDS)) {
			String message = "分布式锁:" + distributedLock.type() + AbstractRedisService.UNDERLINE + distributedLock.key() + "锁定中";
			Exception e = new RuntimeException(
					message);
			LOGGER.error(message,e);
			throw e;
		}
		try {
			return jp.proceed();
		} catch (Throwable e) {
			throw e;
		} finally {
			redisLockService.unlock(distributedLock.type(), distributedLock.key());
		}
	}

	private DistributedLock getAnnotation(JoinPoint jp) {
		MethodSignature signature = (MethodSignature) jp.getSignature();
		Method method = signature.getMethod();
		return method.getAnnotation(DistributedLock.class);
	}

}
