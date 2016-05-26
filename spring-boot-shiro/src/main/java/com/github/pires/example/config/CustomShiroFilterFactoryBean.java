package com.github.pires.example.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.zama.examples.multitenant.master.model.Tenant;
import org.zama.examples.multitenant.master.repository.TenantRepository;
import org.zama.examples.multitenant.util.Constants;
import org.zama.examples.multitenant.util.MultiTenantUtils;

import com.github.pires.example.filter.MultiTenantPathMatchingFilterChainResolver;
import com.github.pires.example.model.Role;
import com.github.pires.example.model.Url;
import com.github.pires.example.repository.UrlRepository;
import com.github.pires.example.servlet.SessionService;
import com.github.pires.example.servlet.SessionUtils;

/**
 * 继承 ShiroFilterFactoryBean 处理拦截资源文件问题。
 * 
 * @author Minly Wang
 * @since 2016年5月20日
 *
 */

public class CustomShiroFilterFactoryBean extends ShiroFilterFactoryBean implements ApplicationListener<ContextRefreshedEvent> {
	@Autowired
	private TenantRepository tenantRepository;
	@Autowired
	private UrlRepository urlRepository;
	@Autowired
	private SessionService sessionService;
	// 对ShiroFilter来说，需要直接忽略的请求
	private Set<String> ignoreExt;

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

		FilterChainManager manager = createFilterChainManager();

		PathMatchingFilterChainResolver chainResolver = new MultiTenantPathMatchingFilterChainResolver();
		chainResolver.setFilterChainManager(manager);

		return new CustomShiroFilter((WebSecurityManager) securityManager, chainResolver);
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
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			String str = request.getRequestURI().toLowerCase();
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
			String tenantId = initOrGetCurrentTenantId(request);
			MultiTenantUtils.setCurrentTenantId(tenantId);
			System.out.println("TENANT_ID>>>>" + tenantId);
			if (flag) {
				super.doFilterInternal(servletRequest, servletResponse, chain);
			} else {
				chain.doFilter(servletRequest, servletResponse);
			}
			MultiTenantUtils.clear();
		}

		private String initOrGetCurrentTenantId(HttpServletRequest request) {
			String sessionId = SessionUtils.readValue(request, ShiroHttpSession.DEFAULT_SESSION_ID_NAME);
			Session session = sessionService.getSession(sessionId);
			String tenantId = (String) request.getParameter("tenantId");
			if (StringUtils.isNotBlank(tenantId)) {
				if (session != null) {
					session.setAttribute(Constants.CURRENT_TENANT_IDENTIFIER, tenantId);
					sessionService.updateSession(session);
				}
			} else {
				if (session != null) {
					tenantId = (String) session.getAttribute(Constants.CURRENT_TENANT_IDENTIFIER);
				}
			}
			if (StringUtils.isNotBlank(tenantId)) {
				return tenantId.trim();
			}
			return tenantId;
		}

	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		List<Tenant> tenants = tenantRepository.findAll();
		if (tenants.isEmpty()) {
			return;
		}
		Map<String, String> filterChainDefinitionMap = this.getFilterChainDefinitionMap();
		if (filterChainDefinitionMap == null) {
			filterChainDefinitionMap = new LinkedHashMap<String, String>();
		}
		for (Tenant tenant : tenants) {
			String tenantId = tenant.getTenantId();
			MultiTenantUtils.setCurrentTenantId(tenantId);
			List<Url> urls = urlRepository.findValidUrl();
			for (Url url : urls) {
				List<Role> roles = url.getRoles();
				List<String> roleNames = new ArrayList<>(roles.size());
				for (Role role : roles) {
					roleNames.add(role.getName());
				}
				filterChainDefinitionMap.put(tenantId + ":" + url.getUrl(), "authc,roles" + roleNames.toString());
			}
			MultiTenantUtils.clear();
		}
		try {
			AbstractShiroFilter filter = (AbstractShiroFilter) getObject();
			FilterChainManager manager = createFilterChainManager();
			((PathMatchingFilterChainResolver) filter.getFilterChainResolver()).setFilterChainManager(manager);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
