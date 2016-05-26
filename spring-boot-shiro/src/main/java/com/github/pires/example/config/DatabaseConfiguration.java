package com.github.pires.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zama.examples.multitenant.config.MultiTenancyJpaConfiguration;

/**
 * 
 * @author Minly Wang
 * @since 2016年5月26日
 *
 */
// @Configuration
// @ComponentScan("org.zama.examples.multitenant.confighelper")
// @EnableConfigurationProperties(JpaProperties.class)
// @EnableJpaRepositories(entityManagerFactoryRef = "tenantEntityManager",
// transactionManagerRef = "tenantTransactionManager", basePackages = {
// "com.github.pires.example.repository" })
// @EnableTransactionManagement
public class DatabaseConfiguration extends MultiTenancyJpaConfiguration {
	private final static Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);

}
