package org.zama.examples.multitenant.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.zama.examples.multitenant.context.MultiTenantEvent;
import org.zama.examples.multitenant.context.MultiTenantListener;

public interface MultiTenantEventMulticaster {

	/**
	 * Add a listener to be notified of all events.
	 * 
	 * @param listener the listener to add
	 */
	void addMultiTenantListener(MultiTenantListener<?> listener);

	/**
	 * Remove a listener from the notification list.
	 * 
	 * @param listener the listener to remove
	 */
	void removeMultiTenantListener(MultiTenantListener<?> listener);

	/**
	 * Remove all listeners registered with this multicaster.
	 * <p>
	 * After a remove call, the multicaster will perform no action on event
	 * notification until new listeners are being registered.
	 */
	void removeAllListeners();

	/**
	 * Multicast the given application event to appropriate listeners.
	 * <p>
	 * Consider using {@link #multicastEvent(ApplicationEvent, ResolvableType)}
	 * if possible as it provides a better support for generics-based events.
	 * 
	 * @param event the event to multicast
	 */
	void multicastEvent(MultiTenantEvent event);

}
