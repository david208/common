package com.yizhenmoney.common.health.jetty;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yizhenmoney.common.health.core.indicator.HealthIndicator;
import com.yizhenmoney.common.health.core.indicator.impl.CompositeHealthIndicator;

public class HealthHandler extends AbstractHandler {

	private CompositeHealthIndicator compositeHealthIndicator;

	private ObjectMapper objectMapper = new ObjectMapper();

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (!request.getPathInfo().equals("/"))
			return;
		response.setContentType("application/json;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		response.getWriter().print(objectMapper.writeValueAsString(compositeHealthIndicator.health()));
	}

	public HealthHandler(Set<HealthIndicator> healthIndicators) {
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		compositeHealthIndicator = new CompositeHealthIndicator(healthIndicators);

	}

}