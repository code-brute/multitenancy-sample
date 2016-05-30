package org.zama.examples.multitenant.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.zama.examples.multitenant.annotation.TenantTransactional;
import org.zama.examples.multitenant.util.Constants;

/**
 * MultiTenancyJpaConfiguration
 * 
 * @author Minly Wang
 * @since 2016年5月24日
 *
 */
@Configuration
@ComponentScan({"org.zama.examples.multitenant.context.event","org.zama.examples.multitenant.tenant"})
@EnableConfigurationProperties(JpaProperties.class)
@EnableJpaRepositories(entityManagerFactoryRef = Constants.TENANT_ENTITY_MANAGER_NAME, transactionManagerRef = TenantTransactional.DEFAULT_NAME, basePackages = {
		"com.github.pires.example.repository" })
@EnableTransactionManagement
public class MultiTenancyJpaConfiguration {
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		return new HibernateJpaVendorAdapter();
	}

	@Bean(name = Constants.TENANT_ENTITY_MANAGER_NAME)
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, MultiTenantConnectionProvider connectionProvider, CurrentTenantIdentifierResolver tenantResolver) {
		LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
		emfBean.setDataSource(dataSource);
		emfBean.setPackagesToScan(buildPackagesToScanOfEntity());
		emfBean.setJpaVendorAdapter(jpaVendorAdapter());

		Map<String, Object> properties = new HashMap<>();
		properties.put(org.hibernate.cfg.Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
		properties.put(org.hibernate.cfg.Environment.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
		properties.put(org.hibernate.cfg.Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);

		properties.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");

		emfBean.setJpaPropertyMap(properties);
		return emfBean;
	}

	private String[] buildPackagesToScanOfEntity() {
		List<String> packages = new ArrayList<>();
		packages.add("com.github.pires.example.entity");
		return packages.toArray(new String[packages.size()]);
	}

	@Bean(name = TenantTransactional.DEFAULT_NAME)
	public JpaTransactionManager transactionManager(EntityManagerFactory tenantEntityManager) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(tenantEntityManager);
		return transactionManager;
	}
}