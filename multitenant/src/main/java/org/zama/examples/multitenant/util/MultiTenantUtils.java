package org.zama.examples.multitenant.util;

/**
 * 
 * @author Minly Wang
 * @since 2016年5月26日
 *
 */
public class MultiTenantUtils {

	private static ThreadLocal<String> currentTenantId = new InheritableThreadLocal<>();

	public static void setCurrentTenantId(String tenantId) {
		currentTenantId.set(tenantId);
	}

	public static String getCurrentTenantId() {
		return currentTenantId.get();
	}

	public static void clear() {
		if (currentTenantId.get() != null) {
			currentTenantId.remove();
		}
	}
}
