package org.zama.examples.multitenant.util;

import org.springframework.util.StringUtils;

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

	public static String getTenantPrefix() {
		String tenantId = getCurrentTenantId();
		if (StringUtils.hasText(tenantId)) {
			return tenantId + ":";
		}
		return Constants.UNKNOWN_TENANT + ":";
	}
}
