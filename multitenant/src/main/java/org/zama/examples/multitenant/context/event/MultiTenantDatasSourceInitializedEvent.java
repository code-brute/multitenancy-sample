package org.zama.examples.multitenant.context.event;

import org.zama.examples.multitenant.context.MultiTenantEvent;

@SuppressWarnings("serial")
public class MultiTenantDatasSourceInitializedEvent extends MultiTenantEvent {

	public MultiTenantDatasSourceInitializedEvent(Object source) {
		super(source);
	}

}
