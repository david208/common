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
public class RedisListService<T extends Serializable> extends AbstractRedisService {

	private ListOperations<String, T> listOperations;

	public RedisListService(ListOperations<String, T> listOperations, String sysName) {
		this.listOperations = listOperations;
		this.sysName = sysName;
	}

	public void leftPush(String type, String key, T value, long timeout, TimeUnit unit) {
		leftPushWithoutTimeout(type, key, value);
		listOperations.getOperations().expire(genKey(type, key), timeout, unit);
	}

	public void leftPushWithoutTimeout(String type, String key, T value) {
		listOperations.leftPush(genKey(type, key), value);
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
		listOperations.getOperations().expire(genKey(type, key), 1l, TimeUnit.HOURS);
	}

	public void rightPop(String type, String key) {
		listOperations.rightPop(genKey(type, key));
	}

	public List<T> getAll(String type, String key) {
		return listOperations.range(genKey(type, key), 0, -1);

	}
	
	/**  清除
	 * @param type
	 * @param key
	 */
	public void clear(String type, String key) {
		listOperations.getOperations().delete(genKey(type, key));
	}

}
