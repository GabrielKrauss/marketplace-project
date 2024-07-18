package com.project.marketplace.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.marketplace.entities.Role;
import com.project.marketplace.entities.User;
import com.project.marketplace.repositories.RoleRepository;
import com.project.marketplace.repositories.UserRepository;
import com.project.marketplace.services.exceptions.ResourceNotFoundException;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;

	public List<User> findAll() {
		return repository.findAll();
	}

	public User findById(Long id) {
		Optional<User> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ResourceNotFoundException(id));
	}

	public User insert(User obj) {
		List<Role> roles = new ArrayList<>();
		for (Long x : obj.getRolesId()) {
			roles.add(roleRepository.findById(x).get());
			obj.setRoles(roles);
		}
		return repository.save(obj);
	}
	
	public void delete(Long id) {
		repository.deleteById(id);
	}
	
	public User update(Long id, User obj) {
		User entity = repository.getReferenceById(id);
		updateData(entity, obj);
		return repository.save(entity);
	}

	private void updateData(User entity, User obj) {
		entity.setEmail(obj.getEmail());
		// Adicionar outros campos se precisar
	}
}
