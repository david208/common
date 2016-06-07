package com.yizhenmoney.common.redis.tag;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 
 * @author sm
 */
public class RedisNsHandler extends NamespaceHandlerSupport {

	private static final String HEALTH = "config";

	public void init() {
		registerBeanDefinitionParser(HEALTH, new RedisBeanDefinitionParser());
	}

}