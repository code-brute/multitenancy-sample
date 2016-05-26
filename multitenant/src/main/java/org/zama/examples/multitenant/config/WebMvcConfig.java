package org.zama.examples.multitenant.config;

import javax.inject.Inject;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.zama.examples.multitenant.tenant.TenantIdentifierInterceptorAdapter;

/**
 * WebMvcConfig
 * 
 * @author Minly Wang
 * @since 2016年5月26日
 *
 */
// @Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Inject
	private TenantIdentifierInterceptorAdapter multiTenancyInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(multiTenancyInterceptor);
	}
}