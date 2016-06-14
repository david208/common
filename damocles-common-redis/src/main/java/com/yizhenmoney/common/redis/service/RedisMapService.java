package com.yizhenmoney.common.redis.service;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.HashOperations;

/**
 * redis的hash操作
 * 
 * @author sm
 *
 * @param <T>
 */
public class RedisMapService<T extends Serializable> {

	private HashOperations<String, String, T> hashOperations;

	private String sysName;

	public RedisMapService(HashOperations<String, String, T> hashOperations, String sysName) {
		this.hashOperations = hashOperations;
		this.sysName = sysName;
	}

	public void put(String type, String key, T value, long timeout, TimeUnit unit) {
		putWithoutTimeout(type, key, value);
		hashOperations.getOperations().expire(sysName + type, timeout, unit);
	}

	public void putWithoutTimeout(String type, String key, T value) {
		hashOperations.put(sysName + type, key, value);
	}

	/**
	 * put一小时
	 * 
	 * @param type
	 * @param key
	 * @param value
	 */
	public void put(String type, String key, T value) {
		putWithoutTimeout(type, key, value);
		hashOperations.getOperations().expire(sysName + type, 1l, TimeUnit.HOURS);
	}

	public void remove(String type, String key) {
		hashOperations.delete(sysName + type, key);
	}

	public T get(String type, String key) {
		return hashOperations.get(sysName + type, key);
	}

	public Map<String, T> getAll(String type) {
		return hashOperations.entries(sysName + type);

	}

}