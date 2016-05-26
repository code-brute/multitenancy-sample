package org.zama.examples.multitenant;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.zama.examples.multitenant.config.MasterDatabaseConfiguration;
import org.zama.examples.multitenant.config.MultiTenancyJpaConfiguration;

/**
 * 
 * @author Minly Wang
 * @since 2016年5月26日
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ComponentScan
@Import({MasterDatabaseConfiguration.class,MultiTenancyJpaConfiguration.class})
public @interface EnableMultiTenancy {
}
