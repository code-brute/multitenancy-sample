package com.github.minly.multitenancy.identifier;

import com.github.minly.multitenancy.exception.TenantNotResolveableException;

/**
 * Created by Jannik on 21.04.15.
 */
public interface TenantIdentifierStorageProxy {
    String getCurrentTenantId() throws TenantNotResolveableException;

    void setCurrentTenantId(String tenantId);
}
