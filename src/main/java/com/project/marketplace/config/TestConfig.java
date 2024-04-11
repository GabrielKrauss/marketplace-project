package com.project.marketplace.config;

import java.time.Instant;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.project.marketplace.entities.Customer;
import com.project.marketplace.entities.Order;
import com.project.marketplace.entities.User;
import com.project.marketplace.repositories.CustomerRepository;
import com.project.marketplace.repositories.OrderRepository;
import com.project.marketplace.repositories.UserRepository;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository; //userRepository (sempre usar camelCase)
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public void run(String... args) throws Exception {
		User u1 = new User(null, "gabrielkrauscosta@gmail.com", "luanalinda");
		User u2 = new User(null, "luana@gmail.com", "gabriellindo");
		
		Customer c1 = new Customer(null, "Gabriel", "gabriel@gmail.com", "987654321", "2000", "luanalinda");
		Customer c2 = new Customer(null, "Luana", "luana2@gmail.com", "12345678", "3000", "gabriellindo");

		Order o1 = new Order(null, Instant.parse("2019-06-20T19:53:07Z"), c1); 
		Order o2 = new Order(null, Instant.parse("2019-07-21T03:42:10Z"), c2);
		Order o3 = new Order(null, Instant.parse("2019-07-22T15:21:22Z"), c1);
		
		userRepository.saveAll(Arrays.asList(u1, u2));
		customerRepository.saveAll(Arrays.asList(c1, c2));
		orderRepository.saveAll(Arrays.asList(o1, o2, o3));
	}
	
	
}
