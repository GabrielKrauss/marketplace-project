package com.project.marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.marketplace.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);
}
	