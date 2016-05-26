package com.github.minly.multitenancy.manager;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.github.minly.multitenancy.resolver.CurrentTenantResolver;

public class MultiTenantJpaTransactionManager extends JpaTransactionManager {

	@Inject
	private CurrentTenantResolver tenantResolver;

	@Override
	protected void doBegin(final Object transaction, final TransactionDefinition definition) {
		super.doBegin(transaction, definition);
		final EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager.getResource(getEntityManagerFactory());
		final EntityManager em = emHolder.getEntityManager();
		final Serializable tenantId = tenantResolver.getCurrentTenantId();
		if (tenantId != null) {
			em.setProperty("eclipselink.tenant-id", tenantId);
		} else {
			// some error handling here
		}
	}
}