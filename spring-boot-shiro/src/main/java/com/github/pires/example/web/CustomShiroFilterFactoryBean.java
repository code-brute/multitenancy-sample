package com.github.pires.example.web;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.zama.examples.multitenant.util.Constants;
import org.zama.examples.multitenant.util.MultiTenantUtils;

import com.github.pires.example.service.SessionService;

/**
 * 继承 ShiroFilterFactoryBean 处理拦截资源文件问题。
 * 
 * @author Minly Wang
 * @since 2016年5月20日
 *
 */

public class CustomShiroFilterFactoryBean extends ShiroFilterFactoryBean {
	public final static String BEAN_ID = "shiroFilter";
		
	private static final Logger log = LoggerFactory.getLogger(CustomShiroFilterFactoryBean.class);
	// 对ShiroFilter来说，需要直接忽略的请求
	private Set<String> ignoreExt;
	
	@Autowired
	private PathMatchingFilterChainResolver pathMatchingFilterChainResolver;
	
	@Autowired
	private SessionService sessionService;


	public CustomShiroFilterFactoryBean() {
		super();
		ignoreExt = new HashSet<>();
		ignoreExt.add(".jpg");
		ignoreExt.add(".png");
		ignoreExt.add(".gif");
		ignoreExt.add(".bmp");
		ignoreExt.add(".js");
		ignoreExt.add(".css");
	}

	@Override
	protected AbstractShiroFilter createInstance() throws Exception {

		SecurityManager securityManager = getSecurityManager();
		if (securityManager == null) {
			String msg = "SecurityManager property must be set.";
			throw new BeanInitializationException(msg);
		}

		if (!(securityManager instanceof WebSecurityManager)) {
			String msg = "The security manager does not implement the WebSecurityManager interface.";
			throw new BeanInitializationException(msg);
		}

//		FilterChainManager manager = createFilterChainManager();
//
//		PathMatchingFilterChainResolver chainResolver = new MultiTenantPathMatchingFilterChainResolver();
//		chainResolver.setFilterChainManager(manager);

		return new CustomShiroFilter((WebSecurityManager) securityManager, pathMatchingFilterChainResolver);
	}

	private final class CustomShiroFilter extends AbstractShiroFilter {

		protected CustomShiroFilter(WebSecurityManager webSecurityManager, FilterChainResolver resolver) {
			super();
			if (webSecurityManager == null) {
				throw new IllegalArgumentException("WebSecurityManager property cannot be null.");
			}
			setSecurityManager(webSecurityManager);
			if (resolver != null) {
				setFilterChainResolver(resolver);
			}
		}

		@Override
		protected void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
			try{
				HttpServletRequest request = (HttpServletRequest) servletRequest;
				String str = request.getRequestURI().toLowerCase();
				boolean flag = isAnonymous(str);
				String tenantId = initCurrentTenantId(request);
				MultiTenantUtils.setCurrentTenantId(tenantId);
				log.info("TENANT ID {}", tenantId);
				if (flag) {
					super.doFilterInternal(servletRequest, servletResponse, chain);
				} else {
					chain.doFilter(servletRequest, servletResponse);
				}
			}finally{
				
			}
			
		}
		
		private String initCurrentTenantId(HttpServletRequest request) {
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

		private boolean isAnonymous(String str) {
			// 因为ShiroFilter默认拦截所有请求，而在每次请求里面都做了session的读取和更新访问时间等操作，这样在集群部署session共享的情况下，数量级的加大了处理量负载。
			// 所以我们这里将一些能忽略的请求忽略掉。
			// 当然如果你的集群系统使用了动静分离处理，静态资料的请求不会到Filter这个层面，便可以忽略。
			boolean flag = true;
			int idx = 0;
			if ((idx = str.indexOf(".")) > 0) {
				str = str.substring(idx);
				if (ignoreExt.contains(str.toLowerCase())) {
					flag = false;
				}
			}
			return flag;
		}

	}
}
