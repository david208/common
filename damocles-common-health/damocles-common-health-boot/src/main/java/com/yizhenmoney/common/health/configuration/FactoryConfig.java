package com.yizhenmoney.common.health.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yizhenmoney.common.health.core.EmbeddedHealthFactory;
import com.yizhenmoney.common.health.jetty.EmbeddedHealthJettyFactory;
import com.yizhenmoney.common.health.tomcat.EmbeddedHealthTomcatFactory;

@Configuration
public class FactoryConfig {

	@Autowired(required = false)
	TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory;

	@Autowired(required = false)
	JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory;

	@Bean
	public EmbeddedHealthFactory embeddeHealthFactory() {
		if (null != tomcatEmbeddedServletContainerFactory) {
			return new EmbeddedHealthTomcatFactory();
		}
		return new EmbeddedHealthJettyFactory();

	}

}
