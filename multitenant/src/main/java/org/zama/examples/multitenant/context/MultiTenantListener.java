package org.zama.examples.multitenant.context;

import java.util.EventListener;

public interface MultiTenantListener<E extends MultiTenantEvent> extends EventListener {

	/**
	 * Handle an application event.
	 * 
	 * @param event the event to respond to
	 */
	void onMultiTenantEvent(E event);

}
