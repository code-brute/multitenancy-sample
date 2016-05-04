package com.github.minly.multitenancy.repository;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.github.minly.multitenancy.resolver.CurrentTenantResolver;

public class MultiTenantSimpleJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> {
	private final CurrentTenantResolver tenantResolver;
	private final EntityManager em;

	public MultiTenantSimpleJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager em,
			CurrentTenantResolver tenantResolver) {
		super(entityInformation, em);
		this.tenantResolver = tenantResolver;
		this.em = em;
	}

	public MultiTenantSimpleJpaRepository(Class<T> domainClass, EntityManager em,
			CurrentTenantResolver tenantResolver) {
		super(domainClass, em);
		this.tenantResolver = tenantResolver;
		this.em = em;
	}

	protected void setCurrentTenant() {
		em.setProperty(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, tenantResolver.getCurrentTenantId());
	}

	@Override
	public <S extends T> S save(S entity) {
		setCurrentTenant();
		return super.save(entity);
	}
	// override the other methods
}