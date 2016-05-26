package com.github.minly.multitenancy.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.github.minly.multitenancy.demo.model.Employee;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long>{

}
