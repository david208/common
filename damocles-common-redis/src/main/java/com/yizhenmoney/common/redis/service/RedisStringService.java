package com.yizhenmoney.common.redis.service;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.ValueOperations;

/**
 * 
 * redis的string操作
 * 
 * @author sm
 *
 * @param <T>
 */
public class RedisStringService<T extends Serializable> {

	private ValueOperations<String, T> valueOperations;

	private String sysName;

	public RedisStringService(ValueOperations<String, T> valueOperations, String sysName) {
		this.valueOperations = valueOperations;
		this.sysName = sysName;
	}

	/**
	 * @param type
	 * @param key
	 * @param value
	 * @param timeout
	 * @param unit
	 */
	public void set(String type, String key, T value, long timeout, TimeUnit unit) {
		valueOperations.set(sysName + type + key, value, timeout, unit);
	}

	/**
	 * 设值，一分钟
	 * 
	 * @param type
	 * @param key
	 * @param value
	 */
	public void set(String type, String key, T value) {
		this.set(type, key, value, 1, TimeUnit.MINUTES);
	}

	public void setWithoutTimeout(String type, String key, T value) {
		valueOperations.set(sysName + type + key, value);
	}

	/**
	 * 取值
	 * 
	 * @param type
	 * @param key
	 */
	public void get(String type, String key) {
		valueOperations.get(sysName + type + key);
	}

}
