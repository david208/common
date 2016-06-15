package com.yizhenmoney.common.redis.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.ValueOperations;

/**
 * 分布式锁
 * 
 * @author sm
 *
 */
public class RedisLockService extends AbstractRedisService{

	private ValueOperations<String, String> valueOperations;


	public RedisLockService(ValueOperations<String, String> valueOperations, String sysName) {
		this.valueOperations = valueOperations;
		this.sysName = sysName;
	}

	/**
	 * 锁定
	 * 
	 * @param type
	 * @param key
	 * @param timeout
	 * @param unit
	 * @return
	 */
	public boolean lock(String type, String key, long timeout, TimeUnit unit) {
		boolean result = lockWithoutTimeout(type, key);
		if (result)
			valueOperations.getOperations().expire(genKey(type, key), timeout, unit);
		return result;
	}

	/**
	 * 锁定,有效期1分钟
	 * 
	 * @param type
	 * @param key
	 * @return
	 */
	public boolean lock(String type, String key) {
		return this.lock(type, key, 1, TimeUnit.MINUTES);
	}

	public boolean lockWithoutTimeout(String type, String key) {
		return valueOperations.setIfAbsent(genKey(type, key), "");
	}

	/**
	 * 解锁
	 * 
	 * @param type
	 * @param key
	 * @return
	 */
	public void unlock(String type, String key) {
		valueOperations.getOperations().delete(genKey(type, key));
	}

}
