package com.github.pires.example.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.zama.examples.multitenant.master.repository.TenantRepository;

import com.github.pires.example.filter.TenantFilter;
import com.github.pires.example.repository.UrlRepository;
import com.github.pires.example.shiro.DaoShiroRealm;
import com.github.pires.example.shiro.HazelcastSessionDao;
import com.github.pires.example.web.MultiTenantCookieRememberMeManager;

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

	// @Bean(name="rememberMeCookie")
	// public SimpleCookie rememberMeCookie(){
	//
	// }

	@Bean(name = "rememberMeManager")
	public CookieRememberMeManager cookieRememberMeManager() {
		return new MultiTenantCookieRememberMeManager();
	}

	@Bean(name = "securityManager")
	public DefaultWebSecurityManager securityManager(RememberMeManager rememberMeManager) {
		final DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(realm());
		securityManager.setRememberMeManager(rememberMeManager);
		securityManager.setSessionManager(sessionManager());
		return securityManager;
	}

	@Bean
	public DefaultWebSessionManager sessionManager() {
		final DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setSessionDAO(sessionDao());
		sessionManager.setGlobalSessionTimeout(43200000); // 12 hours
		return sessionManager;
	}

	@Bean
	public SessionDAO sessionDao() {
		return new HazelcastSessionDao();
	}

	@Bean(name = "realm")
	@DependsOn("lifecycleBeanPostProcessor")
	public DaoShiroRealm realm() {
		final DaoShiroRealm realm = new DaoShiroRealm();
		realm.setCredentialsMatcher(credentialsMatcher());
		return realm;
	}

	@Bean(name = "credentialsMatcher")
	public PasswordMatcher credentialsMatcher() {
		final PasswordMatcher credentialsMatcher = new PasswordMatcher();
		credentialsMatcher.setPasswordService(passwordService());
		return credentialsMatcher;
	}

	@Bean(name = "passwordService")
	public DefaultPasswordService passwordService() {
		return new DefaultPasswordService();
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

	/**
	 * 加载shiroFilter权限控制规则（从数据库读取然后配置）
	 * 
	 * @param shiroFilterFactoryBean
	 */
	private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilterFactoryBean) {
		/////////////////////// 下面这些规则配置最好配置到配置文件中 ///////////////////////
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
		// authc：该过滤器下的页面必须验证后才能访问，它是Shiro内置的一个拦截器org.apache.shiro.web.filter.authc.FormAuthenticationFilter
		// filterChainDefinitionMap.put("/user", "authc,roles[ADMIN]");//
		// 这里为了测试，只限制/user，实际开发中请修改为具体拦截的请求规则
		// anon：它对应的过滤器里面是空的,什么都没做
		logger.info("##################从数据库读取权限规则，加载到shiroFilter中##################");
		filterChainDefinitionMap.put("/", "anon");
		filterChainDefinitionMap.put("/index", "anon");
		filterChainDefinitionMap.put("/login", "authc");
		filterChainDefinitionMap.put("/logout", "logout");
		filterChainDefinitionMap.put("/user/edit/**", "authc,perms[user:edit]");// 这里为了测试，固定写死的值，也可以从数据库或其他配置中读取
		filterChainDefinitionMap.put("/home", "authc");
		filterChainDefinitionMap.put("/angularjs", "authc");
		filterChainDefinitionMap.put("/resource", "anon");
		filterChainDefinitionMap.put("/**", "user");

		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
	}

	@Bean
	public TenantFilter tenantFilter() {
		return new TenantFilter();
	}

	@Bean(name = "shiroFilter")
	public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager, TenantRepository tenantRepository, UrlRepository urlRepository) {

		ShiroFilterFactoryBean shiroFilterFactoryBean = new CustomShiroFilterFactoryBean();
		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilterFactoryBean.setLoginUrl("/login");
		// 登录成功后要跳转的连接
		shiroFilterFactoryBean.setSuccessUrl("/home");
		shiroFilterFactoryBean.setUnauthorizedUrl("/403");

		loadShiroFilterChain(shiroFilterFactoryBean);
		return shiroFilterFactoryBean;
	}

}
