package org.zama.examples.multitenant.context.event;

import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.zama.examples.multitenant.context.MultiTenantEvent;
import org.zama.examples.multitenant.context.MultiTenantListener;

public interface GenericMultiTenantListener extends MultiTenantListener<MultiTenantEvent>, Ordered {

	/**
	 * Determine whether this listener actually supports the given event type.
	 */
	boolean supportsEventType(ResolvableType eventType);

	/**
	 * Determine whether this listener actually supports the given source type.
	 */
	boolean supportsSourceType(Class<?> sourceType);

}
