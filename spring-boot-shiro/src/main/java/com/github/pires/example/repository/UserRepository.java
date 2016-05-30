package com.github.pires.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.pires.example.entity.User;

/**
 * DAO for {@link User}.
 */
public interface UserRepository extends JpaRepository<User,String> {
	
	User findByName(String name);

    User findByEmail(String email);

    User findByEmailAndActive(String email, boolean active);

}
