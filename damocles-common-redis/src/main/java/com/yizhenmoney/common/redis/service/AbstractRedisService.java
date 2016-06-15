package com.yizhenmoney.common.redis.service;

public abstract class AbstractRedisService {

	public static final String UNDERLINE = "_";
	protected String sysName;

	protected String genKey(String type) {
		return sysName + AbstractRedisService.UNDERLINE + type;
	}

	protected String genKey(String type, String key) {
		return sysName + AbstractRedisService.UNDERLINE + type + AbstractRedisService.UNDERLINE + key;
	}

}
