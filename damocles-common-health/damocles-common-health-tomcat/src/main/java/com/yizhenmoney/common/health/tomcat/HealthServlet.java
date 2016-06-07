package com.yizhenmoney.common.health.tomcat;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yizhenmoney.common.health.core.indicator.HealthIndicator;
import com.yizhenmoney.common.health.core.indicator.impl.CompositeHealthIndicator;

public class HealthServlet extends HttpServlet {

	private CompositeHealthIndicator compositeHealthIndicator;

	private ObjectMapper objectMapper = new ObjectMapper();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!req.getPathInfo().equals("/"))
			return;
		resp.setContentType("application/json;charset=utf-8");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().print(objectMapper.writeValueAsString(compositeHealthIndicator.health()));
		resp.getWriter().flush();
	}

	public HealthServlet(Set<HealthIndicator> healthIndicators) {
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		compositeHealthIndicator = new CompositeHealthIndicator(healthIndicators);

	}
}
