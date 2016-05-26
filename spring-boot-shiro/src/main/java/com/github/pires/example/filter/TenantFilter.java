package com.github.pires.example.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.web.servlet.AdviceFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet {@code Filter} that ensures a tenant is associated and then removed
 * before and after request processing, respectively.
 *
 * @since 0.1
 */
public class TenantFilter extends AdviceFilter {

	private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);

	@Override
	public void afterCompletion(ServletRequest request, ServletResponse response, Exception exception) throws Exception {
	}

	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		System.out.println(httpRequest.getHeader("cookie"));
		log.trace("Resolved tenant {}", httpRequest.getRequestedSessionId());

		return true;
	}

}
