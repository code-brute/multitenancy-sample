package org.zama.examples.multitenant.confighelper;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;
import org.zama.examples.multitenant.util.Constants;
import org.zama.examples.multitenant.util.MultiTenantUtils;

/**
 * CurrentTenantResolverImpl
 * 
 * @author Minly Wang
 * @since 2016年5月24日
 *
 */
@Component
public class CurrentTenantResolverImpl implements CurrentTenantIdentifierResolver {
	@Override
	public String resolveCurrentTenantIdentifier() {
		if (MultiTenantUtils.getCurrentTenantId() != null) {
			return MultiTenantUtils.getCurrentTenantId();
		}
		// RequestAttributes requestAttributes =
		// RequestContextHolder.getRequestAttributes();
		// if (requestAttributes != null) {
		// String identifier = (String)
		// requestAttributes.getAttribute(Constants.CURRENT_TENANT_IDENTIFIER,
		// RequestAttributes.SCOPE_REQUEST);
		// if (identifier != null) {
		// return identifier;
		// }else{
		// HttpServletRequest request =
		// ((ServletRequestAttributes)requestAttributes).getRequest();
		// if(request!=null){
		// String tenantId=request.getHeader(Constants.TENANT_ID);
		// if(tenantId != null){
		// return tenantId;
		// }
		// }
		// }
		// }
		return Constants.UNKNOWN_TENANT;
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}
}
