package com.yizhenmoney.common.health.tag;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 
 * @author sm
 */
public class ManagementNsHandler extends NamespaceHandlerSupport {

	private static final String HEALTH = "health";

	public void init() {
		registerBeanDefinitionParser(HEALTH, new HealthBeanDefinitionParser());
	}

}