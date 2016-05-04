package com.github.minly.multitenancy.repository;

import org.springframework.data.repository.CrudRepository;

import com.github.minly.multitenancy.domain.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Long>{

}
