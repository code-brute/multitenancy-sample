package com.github.pires.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.pires.example.entity.Permission;

/**
 * DAO for {@link Permission}.
 */
public interface PermissionRepository extends JpaRepository<Permission,String> {

}
