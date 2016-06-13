package com.yizhenmoney.common.redis.service;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.ListOperations;

/**
 * 封装redis的list操作
 * 
 * @author sm
 *
 *
 * @param <T>
 */
public class RedisListService<T extends Serializable> {

	private ListOperations<String, T> listOperations;

	private String sysName;

	public RedisListService(ListOperations<String, T> listOperations, String sysName) {
		this.listOperations = listOperations;
		this.sysName = sysName;
	}

	public void leftPush(String type, String key, T value, long timeout, TimeUnit unit) {
		leftPushWithoutTimeout(type, key, value);
		listOperations.getOperations().expire(sysName + type + key, timeout, unit);
	}

	public void leftPushWithoutTimeout(String type, String key, T value) {
		listOperations.leftPush(sysName + type + key, value);
	}

	/**
	 * push一小时
	 * 
	 * @param type
	 *            类型
	 * @param key
	 *            ID
	 * @param value
	 *            内容
	 */
	public void leftPush(String type, String key, T value) {
		leftPushWithoutTimeout(type, key, value);
		listOperations.getOperations().expire(sysName + type + key, 1l, TimeUnit.HOURS);
	}

	public void rightPop(String type, String key) {
		listOperations.rightPop(sysName + type + key);
	}

	public List<T> getAll(String type, String key) {
		return listOperations.range(sysName + type + key, 0, -1);

	}

}
