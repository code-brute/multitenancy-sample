package org.zama.examples.multitenant.tenant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.zama.examples.multitenant.util.MultiTenantUtils;

/**
 * TenantIdentifierInterceptorAdapter
 * 
 * @author Minly Wang
 * @since 2016年5月24日
 *
 */
@Component
public class TenantIdentifierInterceptorAdapter extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
		String tenantId = req.getHeader("tenantId");
//		if (req.getSession().getAttribute(Constants.CURRENT_TENANT_IDENTIFIER) == null) {
//			req.getSession().setAttribute(Constants.CURRENT_TENANT_IDENTIFIER, tenantId);
//		}
		if (StringUtils.hasText(tenantId)) {
			MultiTenantUtils.setCurrentTenantId(tenantId.trim());
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, ex);
		MultiTenantUtils.clear();
	}

}