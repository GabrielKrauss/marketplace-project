package com.project.marketplace.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.project.marketplace.entities.Role;
import com.project.marketplace.entities.User;
import com.project.marketplace.repositories.RoleRepository;
import com.project.marketplace.repositories.UserRepository;
import com.project.marketplace.services.exceptions.DatabaseException;
import com.project.marketplace.services.exceptions.EmailAlreadyExistsException;
import com.project.marketplace.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private Validator validator;

	public List<User> findAll() {
		return repository.findAll();
	}

	public User findById(Long id) {
		Optional<User> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ResourceNotFoundException(id));
	}

	public User insert(User obj) {
		if (repository.existsByEmail(obj.getEmail())) {
			throw new EmailAlreadyExistsException(obj.getEmail());
		}
		Set<ConstraintViolation<User>> violations = validator.validate(obj);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
		List<Role> roles = new ArrayList<>();
		for (Long x : obj.getRolesId()) {
			roles.add(roleRepository.findById(x).get());
			obj.setRoles(roles);
		}
		return repository.save(obj);
	}

	public void delete(Long id) {
		try {
			if (!repository.existsById(id)) {
				throw new ResourceNotFoundException(id);
			}
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public User update(Long id, User obj) {
		try {
			User entity = repository.getReferenceById(id);
			updateData(entity, obj);
			return repository.save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		}

	}

	private void updateData(User entity, User obj) {
		entity.setEmail(obj.getEmail());
		// Adicionar outros campos se precisar

	}
}
