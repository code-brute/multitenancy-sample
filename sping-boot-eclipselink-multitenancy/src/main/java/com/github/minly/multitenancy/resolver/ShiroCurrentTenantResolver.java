package com.github.minly.multitenancy.resolver;

import org.springframework.boot.autoconfigure.web.ServerProperties.Session;

public class ShiroCurrentTenantResolver implements CurrentTenantResolver<String> {

	@Override
	public String getCurrentTenantId() {
		Session session = SecurityUtils.getSubject().getSession();
		return (String) session.getAttribute("tenantId");
	}

}
