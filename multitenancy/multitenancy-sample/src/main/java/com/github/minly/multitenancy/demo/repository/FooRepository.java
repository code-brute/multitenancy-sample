package com.github.minly.multitenancy.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.github.minly.multitenancy.demo.model.Foo;

/**
 * Created by Jannik on 02.07.15.
 */
@Repository
public interface FooRepository extends CrudRepository<Foo, Long>{
}
