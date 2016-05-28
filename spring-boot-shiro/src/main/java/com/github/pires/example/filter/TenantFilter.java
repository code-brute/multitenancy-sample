package com.github.pires.example.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.zama.examples.multitenant.util.Constants;
import org.zama.examples.multitenant.util.MultiTenantUtils;

import com.github.pires.example.web.CookieUtils;
import com.github.pires.example.web.SessionService;

/**
 * Servlet {@code Filter} that ensures a tenant is associated and then removed
 * before and after request processing, respectively.
 *
 * @since 0.1
 */
public class TenantFilter extends AdviceFilter {

	private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);

	@Autowired
	private SessionService sessionService;

	@Override
	public void afterCompletion(ServletRequest request, ServletResponse response, Exception exception) throws Exception {
		MultiTenantUtils.clear();
	}

	@Override
	protected boolean preHandle(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		String tenantId = initOrGetCurrentTenantId(request);
		MultiTenantUtils.setCurrentTenantId(tenantId);
		log.info("TENANT ID {}", tenantId);
		return true;
	}

	private String initOrGetCurrentTenantId(HttpServletRequest request) {
		String sessionId = CookieUtils.readValue(request, ShiroHttpSession.DEFAULT_SESSION_ID_NAME);
		Session session = sessionService.getSession(sessionId);
		String tenantId = (String) request.getParameter("tenantId");
		if (StringUtils.isNotBlank(tenantId)) {
			initCurrentTenantIdentifier(session, tenantId);
		} else {
			if (session != null) {
				tenantId = (String) session.getAttribute(Constants.CURRENT_TENANT_IDENTIFIER);
			}
			if (tenantId == null) {
				String principal = CookieUtils.getRememberMeInfo(request, CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME);
				if (principal != null) {
					String[] principalInfo = principal.split(":");
					if (principalInfo.length > 1) {
						tenantId = principalInfo[0];
						initCurrentTenantIdentifier(session, tenantId);
					}
				}
			}
		}
		if (StringUtils.isNotBlank(tenantId)) {
			return tenantId.trim();
		}
		return tenantId;
	}

	private void initCurrentTenantIdentifier(Session session, String tenantId) {
		if (session != null) {
			session.setAttribute(Constants.CURRENT_TENANT_IDENTIFIER, tenantId);
			sessionService.updateSession(session);
		}
	}

}
