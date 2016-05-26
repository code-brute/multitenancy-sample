package com.github.minly.multitenancy;

import org.springframework.context.annotation.Import;

import com.github.minly.multitenancy.configuration.MultitenancyConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Jannik on 30.06.15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(MultitenancyConfig.class)
public @interface EnableMultiTenancy {
}
