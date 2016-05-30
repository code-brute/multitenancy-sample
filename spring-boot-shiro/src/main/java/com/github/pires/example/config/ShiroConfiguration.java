package com.github.pires.example.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.github.pires.example.shiro.DaoShiroRealm;
import com.github.pires.example.shiro.HazelcastSessionDao;
import com.github.pires.example.web.CustomShiroFilterFactoryBean;
import com.github.pires.example.web.MultiTenantCookieRememberMeManager;
import com.github.pires.example.web.filter.CustomFilterChainManager;
import com.github.pires.example.web.filter.MultiTenantPathMatchingFilterChainResolver;
import com.github.pires.example.web.filter.MultiTenantRolesAuthorizationFilter;

/**
 * Shiro 配置
 * 
 * @author Minly Wang
 * @since 2016年5月26日
 *
 */
@Configuration
public class ShiroConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(ShiroConfiguration.class);

	/**
	 * 注册DelegatingFilterProxy（Shiro） 集成Shiro有2种方法： 1.
	 * 按这个方法自己组装一个FilterRegistrationBean（这种方法更为灵活，可以自己定义UrlPattern，
	 * 在项目使用中你可能会因为一些很但疼的问题最后采用它， 想使用它你可能需要看官网或者已经很了解Shiro的处理原理了） 2.
	 * 直接使用ShiroFilterFactoryBean（这种方法比较简单，其内部对ShiroFilter做了组装工作，
	 * 无法自己定义UrlPattern， 默认拦截 /*）
	 */
	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
		filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
		// 该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理
		filterRegistration.addInitParameter("targetFilterLifecycle", "true");
		filterRegistration.setEnabled(true);
		filterRegistration.addUrlPatterns("/*");// 可以自己灵活的定义很多，避免一些根本不需要被Shiro处理的请求被包含进来
		return filterRegistration;
	}

	@Bean(name = "rememberMeManager")
	public CookieRememberMeManager cookieRememberMeManager() {
		return new MultiTenantCookieRememberMeManager();
	}

	@Bean
	public SessionDAO sessionDao() {
		return new HazelcastSessionDao();
	}

	@Bean
	public DefaultWebSessionManager sessionManager() {
		final DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setSessionDAO(sessionDao());
		sessionManager.setGlobalSessionTimeout(43200000); // 12 hours
		return sessionManager;
	}

	@Bean(name = "passwordService")
	public DefaultPasswordService passwordService() {
		return new DefaultPasswordService();
	}

	@Bean(name = "credentialsMatcher")
	public PasswordMatcher credentialsMatcher() {
		final PasswordMatcher credentialsMatcher = new PasswordMatcher();
		credentialsMatcher.setPasswordService(passwordService());
		return credentialsMatcher;
	}

	@Bean(name = "realm")
	@DependsOn("lifecycleBeanPostProcessor")
	public DaoShiroRealm realm() {
		final DaoShiroRealm realm = new DaoShiroRealm();
		realm.setCredentialsMatcher(credentialsMatcher());
		return realm;
	}

	@Bean(name = "securityManager")
	public DefaultWebSecurityManager securityManager() {
		final DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(realm());
		securityManager.setRememberMeManager(cookieRememberMeManager());
		securityManager.setSessionManager(sessionManager());
		return securityManager;
	}

	@Bean
	public MultiTenantRolesAuthorizationFilter multiTenantRolesAuthorizationFilter() {
		return new MultiTenantRolesAuthorizationFilter();
	}

	@Bean(name = CustomFilterChainManager.BEAN_ID)
	public CustomFilterChainManager customFilterChainManager() {
		CustomFilterChainManager customFilterChainManager = new CustomFilterChainManager();
		Map<String, Filter> customFilters = new HashMap<String, Filter>(1);
		customFilters.put("roles", multiTenantRolesAuthorizationFilter());
		customFilterChainManager.setCustomFilters(customFilters);
		customFilterChainManager.setLoginUrl("/login");
		customFilterChainManager.setSuccessUrl("/home");
		customFilterChainManager.setUnauthorizedUrl("/unauthorized");
		Map<String, String> filterChainDefinitionMap = customFilterChainManager.getFilterChainDefinitionMap();
		logger.info("##################从数据库读取权限规则，加载到shiroFilter中##################");
		filterChainDefinitionMap.put("/", "anon");
		filterChainDefinitionMap.put("/index", "anon");
		filterChainDefinitionMap.put("/login", "authc");
		filterChainDefinitionMap.put("/logout", "logout");
		filterChainDefinitionMap.put("/404/**", "anon");
		filterChainDefinitionMap.put("/home", "authc");
		filterChainDefinitionMap.put("/angularjs", "authc");
		filterChainDefinitionMap.put("/resource", "anon");
		filterChainDefinitionMap.put("/**", "user");
		customFilterChainManager.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return customFilterChainManager;
	}

	@Bean(name = MultiTenantPathMatchingFilterChainResolver.BEAN_ID)
	public PathMatchingFilterChainResolver PathMatchingFilterChainResolver() {
		MultiTenantPathMatchingFilterChainResolver pathMatchingFilterChainResolver = new MultiTenantPathMatchingFilterChainResolver();
		pathMatchingFilterChainResolver.setCustomFilterChainManager(customFilterChainManager());
		return pathMatchingFilterChainResolver;
	}

	@Bean
	public MethodInvokingFactoryBean methodInvokingFactoryBeanSetSecurityManager() {
		MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
		methodInvokingFactoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
		methodInvokingFactoryBean.setArguments(new Object[] { securityManager() });
		return methodInvokingFactoryBean;
	}

	// /**
	// * 加载shiroFilter权限控制规则（从数据库读取然后配置）
	// *
	// * @param shiroFilterFactoryBean
	// */
	// private void loadShiroFilterChain(ShiroFilterFactoryBean
	// shiroFilterFactoryBean) {
	// /////////////////////// 下面这些规则配置最好配置到配置文件中 ///////////////////////
	// Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String,
	// String>();
	// //
	// authc：该过滤器下的页面必须验证后才能访问，它是Shiro内置的一个拦截器org.apache.shiro.web.filter.authc.FormAuthenticationFilter
	// // filterChainDefinitionMap.put("/user", "authc,roles[ADMIN]");//
	// // 这里为了测试，只限制/user，实际开发中请修改为具体拦截的请求规则
	// // anon：它对应的过滤器里面是空的,什么都没做
	// logger.info("##################从数据库读取权限规则，加载到shiroFilter中##################");
	// filterChainDefinitionMap.put("/", "anon");
	// filterChainDefinitionMap.put("/index", "anon");
	// filterChainDefinitionMap.put("/login", "authc");
	// filterChainDefinitionMap.put("/logout", "logout");
	// filterChainDefinitionMap.put("/user/edit/**",
	// "authc,perms[user:edit]");// 这里为了测试，固定写死的值，也可以从数据库或其他配置中读取
	// filterChainDefinitionMap.put("/angularjs", "authc");
	// filterChainDefinitionMap.put("/resource", "anon");
	// filterChainDefinitionMap.put("/users", "authc,roles[ADMIN]");
	//
	// shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
	// }

	// @Bean
	// public TenantFilter tenantFilter() {
	// return new TenantFilter();
	// }

	@Bean(name = CustomShiroFilterFactoryBean.BEAN_ID)
	public ShiroFilterFactoryBean getShiroFilterFactoryBean() {

		ShiroFilterFactoryBean shiroFilterFactoryBean = new CustomShiroFilterFactoryBean();
		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager());
		// // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		// shiroFilterFactoryBean.setLoginUrl("/login");
		// // 登录成功后要跳转的连接
		// shiroFilterFactoryBean.setSuccessUrl("/home");
		// shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");

		// loadShiroFilterChain(shiroFilterFactoryBean);
		return shiroFilterFactoryBean;
	}

	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
		daap.setProxyTargetClass(true);
		return daap;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
		aasa.setSecurityManager(securityManager);
		return aasa;
	}

}
