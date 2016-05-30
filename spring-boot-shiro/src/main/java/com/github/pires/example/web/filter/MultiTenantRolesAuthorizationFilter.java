package com.github.pires.example.web.filter;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.zama.examples.multitenant.util.MultiTenantUtils;

public class MultiTenantRolesAuthorizationFilter extends RolesAuthorizationFilter {

	protected String getPathWithinApplication(ServletRequest request) {
		String requestUri = WebUtils.getPathWithinApplication(WebUtils.toHttp(request));
		if (StringUtils.isNotBlank(requestUri) && (!requestUri.equals("/logout")) && MultiTenantUtils.getCurrentTenantId() != null) {
			return MultiTenantUtils.getTenantPrefix() + requestUri;
		}
		return requestUri;
	}
}
