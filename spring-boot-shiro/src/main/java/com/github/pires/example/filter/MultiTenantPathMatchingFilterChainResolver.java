package com.github.pires.example.filter;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.util.WebUtils;
import org.zama.examples.multitenant.util.MultiTenantUtils;

/**
 * 
 * @author Minly Wang
 * @since 2016年5月26日
 *
 */
public class MultiTenantPathMatchingFilterChainResolver extends PathMatchingFilterChainResolver {
	@Override
	protected String getPathWithinApplication(ServletRequest request) {
		String requestUri = WebUtils.getPathWithinApplication(WebUtils.toHttp(request));
		String currentTenantId = MultiTenantUtils.getCurrentTenantId();
		if (StringUtils.isNotBlank(requestUri) && currentTenantId != null && (!requestUri.equals("/logout"))) {
			return currentTenantId + ":" + requestUri;
		}
		return requestUri;
	}
}
