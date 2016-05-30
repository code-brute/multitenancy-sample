package org.zama.examples.multitenant.context.event;

import org.springframework.aop.support.AopUtils;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;
import org.zama.examples.multitenant.context.MultiTenantEvent;
import org.zama.examples.multitenant.context.MultiTenantListener;

public class GenericMultiTenantListenerAdapter implements GenericMultiTenantListener {

	private final MultiTenantListener<MultiTenantEvent> delegate;

	private final ResolvableType declaredEventType;

	/**
	 * Create a new GenericApplicationListener for the given delegate.
	 * 
	 * @param delegate the delegate listener to be invoked
	 */
	@SuppressWarnings("unchecked")
	public GenericMultiTenantListenerAdapter(MultiTenantListener<?> delegate) {
		Assert.notNull(delegate, "Delegate listener must not be null");
		this.delegate = (MultiTenantListener<MultiTenantEvent>) delegate;
		this.declaredEventType = resolveDeclaredEventType(this.delegate);
	}

	@Override
	public void onMultiTenantEvent(MultiTenantEvent event) {
		this.delegate.onMultiTenantEvent(event);
	}

	@Override
	public boolean supportsEventType(ResolvableType eventType) {
		return (this.declaredEventType == null || this.declaredEventType.isAssignableFrom(eventType));
	}

	@Override
	public boolean supportsSourceType(Class<?> sourceType) {
		return true;
	}

	@Override
	public int getOrder() {
		return (this.delegate instanceof Ordered ? ((Ordered) this.delegate).getOrder() : Ordered.LOWEST_PRECEDENCE);
	}

	static ResolvableType resolveDeclaredEventType(Class<?> listenerType) {
		ResolvableType resolvableType = ResolvableType.forClass(listenerType).as(MultiTenantListener.class);
		if (resolvableType == null || !resolvableType.hasGenerics()) {
			return null;
		}
		return resolvableType.getGeneric();
	}

	private static ResolvableType resolveDeclaredEventType(MultiTenantListener<MultiTenantEvent> listener) {
		ResolvableType declaredEventType = resolveDeclaredEventType(listener.getClass());
		if (declaredEventType == null || declaredEventType.isAssignableFrom(ResolvableType.forClass(MultiTenantEvent.class))) {
			Class<?> targetClass = AopUtils.getTargetClass(listener);
			if (targetClass != listener.getClass()) {
				declaredEventType = resolveDeclaredEventType(targetClass);
			}
		}
		return declaredEventType;
	}
}
