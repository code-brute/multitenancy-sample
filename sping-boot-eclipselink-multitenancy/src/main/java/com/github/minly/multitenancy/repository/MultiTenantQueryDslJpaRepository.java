package com.github.minly.multitenancy.repository;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;

import com.github.minly.multitenancy.resolver.CurrentTenantResolver;

public class MultiTenantQueryDslJpaRepository<T, ID extends Serializable> extends QueryDslJpaRepository<T, ID> {
	private final CurrentTenantResolver tenantResolver;
	private final EntityManager em;

	public MultiTenantQueryDslJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager em,
			CurrentTenantResolver tenantResolver) {
		super(entityInformation, em);
		this.tenantResolver = tenantResolver;
		this.em = em;
	}

	public MultiTenantQueryDslJpaRepository(Class<T> domainClass, EntityManager em,
			CurrentTenantResolver tenantResolver) {
		super(domainClass, em);
		this.tenantResolver = tenantResolver;
		this.em = em;
	}

	protected void setCurrentTenant() {
		em.setProperty(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, tenantResolver.getCurrentTenantId());
	}

	// override the other methods
}