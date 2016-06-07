package com.yizhenmoney.common.health.tomcat;

import java.io.File;
import java.util.Set;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yizhenmoney.common.health.core.EmbeddedHealthContainer;
import com.yizhenmoney.common.health.core.indicator.HealthIndicator;
import com.yizhenmoney.common.health.core.indicator.impl.ApplicationHealthIndicator;
import com.yizhenmoney.common.health.core.indicator.impl.DiskSpaceHealthIndicator;
import com.yizhenmoney.common.health.core.indicator.impl.DiskSpaceHealthIndicatorProperties;
import com.yizhenmoney.common.health.core.indicator.impl.SystemHealthIndicator;
import com.yizhenmoney.common.health.core.metric.SystemPublicMetrics;

public class EmbeddedHealthTomcat implements EmbeddedHealthContainer {

	private static Log LOGGER = LogFactory.getLog(EmbeddedHealthTomcat.class);

	private static final int DEFAULT_PORT = 7005;

	private Tomcat server;

	public EmbeddedHealthTomcat(Set<HealthIndicator> healthIndicators, Integer port) {
		try {
			if (null == port || port == 0)
				port = DEFAULT_PORT;
			server = new Tomcat();
			server.setPort(port);
			server.getConnector().setURIEncoding("UTF-8");
			healthIndicators.add(new DiskSpaceHealthIndicator(new DiskSpaceHealthIndicatorProperties()));
			healthIndicators.add(new ApplicationHealthIndicator());
			healthIndicators.add(new SystemHealthIndicator(new SystemPublicMetrics()));

			Context ctx = server.addContext("/", new File(".").getAbsolutePath());
			Tomcat.addServlet(ctx, "health", new HealthServlet(healthIndicators));
			ctx.addServletMapping("/*", "health");
			server.start();
		} catch (Exception e) {
			LOGGER.error("监控失败", e);
		}
	}
}
