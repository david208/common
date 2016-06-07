package com.yizhenmoney.common.redis.configuration;

import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class YizhenRedisCachePrefix implements RedisCachePrefix {

	private final RedisSerializer<String> serializer = new StringRedisSerializer();
	private final String delimiter = ":";

	private String prefix;

	public byte[] prefix(String cacheName) {
		return serializer.serialize((prefix.concat(delimiter.concat(cacheName))));
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
