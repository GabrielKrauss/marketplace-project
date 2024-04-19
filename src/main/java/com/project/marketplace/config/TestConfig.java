package com.project.marketplace.config;

import java.time.Instant;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.project.marketplace.entities.Category;
import com.project.marketplace.entities.Customer;
import com.project.marketplace.entities.Order;
import com.project.marketplace.entities.Product;
import com.project.marketplace.entities.User;
import com.project.marketplace.entities.enums.OrderStatus;
import com.project.marketplace.repositories.CategoryRepository;
import com.project.marketplace.repositories.CustomerRepository;
import com.project.marketplace.repositories.OrderRepository;
import com.project.marketplace.repositories.ProductRepository;
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

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Override
	public void run(String... args) throws Exception {
		
		Category cat1 = new Category(null, "Action");
		Category cat2 = new Category(null, "Roguelike");
		Category cat3 = new Category(null, "Multiplayer"); 

		categoryRepository.saveAll(Arrays.asList(cat1, cat2, cat3));
		
		Product p1 = new Product(null, "Ori and the blind forest", "Explora uma história profundamente emocional sobre amor e sacrifício, além da esperança que existe em todos nós.", 99.00, true);
		Product p2 = new Product(null, "Hollow Knight", "Explore um vasto mundo interligado de caminhos esquecidos, florestas frondosas e cidades em ruínas.", 46.99, false);
		Product p3 = new Product(null, "Cuphead", "A classic run and gun action game heavily focused on boss battles. Inspired by cartoons of the 1930s", 36.99, true);
		Product p4 = new Product(null, "Hades", "Na pele do imortal Príncipe do Submundo, você usará os poderes e as armas míticas do Olimpo para se libertar das garras do deus dos mortos", 73.99, true);
		Product p5 = new Product(null, "Resident Evil 4 (2005)", "Special agent Leon S. Kennedy is sent on a mission to rescue the U.S. President’s daughter who has been kidnapped", 39.99, true);
		
		
		p1.getCategories().add(cat3);
		p2.getCategories().add(cat1);
		p3.getCategories().add(cat3);
		p4.getCategories().add(cat2);
		p4.getCategories().add(cat1);
		p5.getCategories().add(cat1);
		
		productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));
		
		User u1 = new User(null, "gabrielkrauscosta@gmail.com", "luanalinda");
		User u2 = new User(null, "luana@gmail.com", "gabriellindo");
		
		Customer c1 = new Customer(null, "Gabriel", "gabriel@gmail.com", "987654321", "2000", "luanalinda");
		Customer c2 = new Customer(null, "Luana", "luana2@gmail.com", "12345678", "3000", "gabriellindo");

		Order o1 = new Order(null, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, c1); 
		Order o2 = new Order(null, Instant.parse("2019-07-21T03:42:10Z"), OrderStatus.WAITING_PAYMENT, c2);
		Order o3 = new Order(null, Instant.parse("2019-07-22T15:21:22Z"), OrderStatus.SHIPPED, c1);
		
		userRepository.saveAll(Arrays.asList(u1, u2));
		customerRepository.saveAll(Arrays.asList(c1, c2));
		orderRepository.saveAll(Arrays.asList(o1, o2, o3));
	}
	
	
}
