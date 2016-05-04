package com.github.minly.multitenancy.resolver;

import java.io.Serializable;

public interface CurrentTenantResolver<T extends Serializable> {
	T getCurrentTenantId();
}
