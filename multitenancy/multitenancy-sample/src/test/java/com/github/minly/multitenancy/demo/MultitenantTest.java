package com.github.minly.multitenancy.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.minly.multitenancy.demo.model.Foo;
import com.github.minly.multitenancy.demo.repository.FooRepository;
import com.github.minly.multitenancy.demo.service.EmployeeService;
import com.github.minly.multitenancy.exception.TenantDataSourceNotResolvableException;
import com.github.minly.multitenancy.identifier.TenantIdentifierStorage;

/**
 * Created by Jannik on 02.07.15.
 */
@SpringApplicationConfiguration(classes = Application.class)
@TestExecutionListeners(inheritListeners = false, listeners = {
        TransactionalTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
public class MultitenantTest extends AbstractTestNGSpringContextTests {

    @Autowired
    FooRepository fooRepository;
    @Autowired
    EmployeeService employeeService;

    @Autowired
    TenantIdentifierStorage tenantIdentifierStorage;

    @BeforeClass
    public void dataTenant1() {
        TenantIdentifierStorage.setThreadTenantId("tenant_1");
        fooRepository.save(new Foo(1, "Foo-11"));
        fooRepository.save(new Foo(2, "Foo-12"));
        fooRepository.save(new Foo(3, "Foo-13"));
    }

    @BeforeClass
    public void dataTenant2() {
        TenantIdentifierStorage.setThreadTenantId("tenant_2");
//        fooRepository.save(new Foo(1, "Foo-21"));
//        fooRepository.save(new Foo(2, "Foo-22"));
//        fooRepository.save(new Foo(3, "Foo-23"));
        employeeService.save(new String[]{"Foo11","Foo12","Foo13","Foo14","Foo15","Foo6","Foo7","Foo8","Foo9"});
    }

    @Test
    public void testTenantData() {
        TenantIdentifierStorage.setThreadTenantId("tenant_2");
        Assert.assertEquals(fooRepository.findOne(1l).getName(), "Foo-21");
        Assert.assertEquals(fooRepository.findOne(3l).getName(), "Foo-23");
        Assert.assertEquals(fooRepository.findOne(2l).getName(), "Foo-22");
        TenantIdentifierStorage.setThreadTenantId("tenant_1");
        Assert.assertEquals(fooRepository.findOne(1l).getName(), "Foo-11");
        Assert.assertEquals(fooRepository.findOne(2l).getName(), "Foo-12");
        Assert.assertEquals(fooRepository.findOne(3l).getName(), "Foo-13");
    }

    @Test(expectedExceptions = TenantDataSourceNotResolvableException.class)
    public void testInvalidTenant(){
        TenantIdentifierStorage.setThreadTenantId("invalid_tenant");
        fooRepository.findOne(1l);
    }
}
