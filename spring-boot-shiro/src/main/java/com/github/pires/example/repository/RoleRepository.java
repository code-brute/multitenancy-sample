package com.github.pires.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.pires.example.entity.Role;

/**
 * DAO for {@link Role}.
 */
public interface RoleRepository extends JpaRepository<Role, String> {

	Optional<Role> findOneByName(String name);
}
