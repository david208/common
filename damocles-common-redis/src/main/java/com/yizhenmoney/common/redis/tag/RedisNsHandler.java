package com.yizhenmoney.common.redis.tag;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 
 * @author sm
 */
public class RedisNsHandler extends NamespaceHandlerSupport {

	private static final String CONFIG = "config";
	private static final String AOP = "aop";

	public void init() {
		registerBeanDefinitionParser(CONFIG, new RedisBeanDefinitionParser());
		registerBeanDefinitionParser(AOP, new RedisAopBeanDefinitionParser());
	}

}