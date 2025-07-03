package com.project.marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.marketplace.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
