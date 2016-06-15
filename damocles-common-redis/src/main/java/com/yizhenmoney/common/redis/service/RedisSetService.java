package com.yizhenmoney.common.redis.service;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.SetOperations;

/**
 * 封装redis的set操作
 * 
 * @author sm
 *
 *
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class RedisSetService<T extends Serializable> extends AbstractRedisService {

	private SetOperations<String, T> setOperations;

	public RedisSetService(SetOperations<String, T> setOperations, String sysName) {
		this.setOperations = setOperations;
		this.sysName = sysName;
	}

	public void add(String type, String key, long timeout, TimeUnit unit, T... value) {
		addWithoutTimeout(type, key, value);
		setOperations.getOperations().expire(genKey(type, key), timeout, unit);
	}

	public void addWithoutTimeout(String type, String key, T... value) {
		setOperations.add(genKey(type, key), value);
	}

	/**
	 * 添加,1小时
	 * 
	 * @param type
	 * @param key
	 * @param value
	 */
	public void add(String type, String key, T... value) {
		add(type, key, 1l, TimeUnit.HOURS, value);
	}

	public void remove(String type, String key, T value) {
		setOperations.remove(genKey(type, key), value);
	}

	public Set<T> getAll(String type, String key) {
		return setOperations.members(genKey(type, key));

	}

}
