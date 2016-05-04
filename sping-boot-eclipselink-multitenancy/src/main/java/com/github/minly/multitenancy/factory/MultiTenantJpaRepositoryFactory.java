package com.github.minly.multitenancy.factory;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.core.RepositoryMetadata;

import com.github.minly.multitenancy.repository.MultiTenantQueryDslJpaRepository;
import com.github.minly.multitenancy.repository.MultiTenantSimpleJpaRepository;
import com.github.minly.multitenancy.resolver.CurrentTenantResolver;

public class MultiTenantJpaRepositoryFactory extends JpaRepositoryFactory {
	private final CurrentTenantResolver currentTenantResolver;

	public MultiTenantJpaRepositoryFactory(EntityManager entityManager, CurrentTenantResolver currentTenantResolver) {
		super(entityManager);
		this.currentTenantResolver = currentTenantResolver;
	}

	@SuppressWarnings("unchecked")
	protected JpaRepository<?, ?> getTargetRepository(RepositoryMetadata metadata, EntityManager entityManager) {
		final Class repositoryInterface = metadata.getRepositoryInterface();
		final JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

		final SimpleJpaRepository<?, ?> repo = isQueryDslExecutor(repositoryInterface)
				? new MultiTenantQueryDslJpaRepository(entityInformation, entityManager, currentTenantResolver)
				: new MultiTenantSimpleJpaRepository(entityInformation, entityManager, currentTenantResolver);
		repo.setLockMetadataProvider(LockModeRepositoryPostProcessor.INSTANCE.getLockMetadataProvider());
		return repo;
	}

	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		if (isQueryDslExecutor(metadata.getRepositoryInterface())) {
			return MultiTenantQueryDslJpaRepository.class;
		} else {
			return MultiTenantSimpleJpaRepository.class;
		}
	}

	private boolean isQueryDslExecutor(Class<?> repositoryInterface) {
		return QUERY_DSL_PRESENT && QueryDslPredicateExecutor.class.isAssignableFrom(repositoryInterface);
	}
}