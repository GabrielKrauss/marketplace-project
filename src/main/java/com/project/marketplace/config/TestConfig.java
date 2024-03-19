package com.project.marketplace.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.project.marketplace.entities.User;
import com.project.marketplace.repositories.UserRepository;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

	@Autowired
	private UserRepository userrepository;

	@Override
	public void run(String... args) throws Exception {
		User u1 = new User(null, "gabrielkrauscosta@gmail.com", "luanalinda");
		User u2 = new User(null, "luana@gmail.com", "gabriellindo");
		
		userrepository.saveAll(Arrays.asList(u1, u2));
	}
	
	
}
