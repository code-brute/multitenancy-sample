package com.github.pires.example.web.filter;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
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
	public final static String BEAN_ID = "multiTenantPathMatchingFilterChainResolver";
	private CustomFilterChainManager customFilterChainManager;

	public void setCustomFilterChainManager(CustomFilterChainManager customDefaultFilterChainManager) {
		this.customFilterChainManager = customDefaultFilterChainManager;
		setFilterChainManager(customDefaultFilterChainManager);
	}

	public FilterChain getChain(ServletRequest request, ServletResponse response, FilterChain originalChain) {
		FilterChainManager filterChainManager = getFilterChainManager();
		if (!filterChainManager.hasChains()) {
			return null;
		}

		String requestURI = getPathWithinApplication(request);

		List<String> chainNames = new ArrayList<String>();
		// the 'chain names' in this implementation are actually path patterns
		// defined by the user. We just use them
		// as the chain name for the FilterChainManager's requirements
		for (String pathPattern : filterChainManager.getChainNames()) {

			// If the path does match, then pass on to the subclass
			// implementation for specific checks:
			if (pathMatches(pathPattern, requestURI)) {
				chainNames.add(pathPattern);
//				break;
			}
		}

		if (chainNames.size() == 0) {
			return null;
		}

		return customFilterChainManager.proxy(originalChain, chainNames);
	}

	@Override
	protected String getPathWithinApplication(ServletRequest request) {
		String requestUri = WebUtils.getPathWithinApplication(WebUtils.toHttp(request));
		if (StringUtils.isNotBlank(requestUri) && (!requestUri.equals("/logout")) && MultiTenantUtils.getCurrentTenantId()!=null) {
			return MultiTenantUtils.getTenantPrefix() + requestUri;
		}
		return requestUri;
	}
}
