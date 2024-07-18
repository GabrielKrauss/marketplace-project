package com.project.marketplace.config;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.project.marketplace.entities.Address;
import com.project.marketplace.entities.Category;
import com.project.marketplace.entities.Customer;
import com.project.marketplace.entities.Order;
import com.project.marketplace.entities.OrderItem;
import com.project.marketplace.entities.Product;
import com.project.marketplace.entities.Role;
import com.project.marketplace.entities.User;
import com.project.marketplace.entities.enums.AddressType;
import com.project.marketplace.entities.enums.Authorities;
import com.project.marketplace.entities.enums.CustomerType;
import com.project.marketplace.entities.enums.OrderStatus;
import com.project.marketplace.repositories.AddressRepository;
import com.project.marketplace.repositories.CategoryRepository;
import com.project.marketplace.repositories.CustomerRepository;
import com.project.marketplace.repositories.OrderItemRepository;
import com.project.marketplace.repositories.OrderRepository;
import com.project.marketplace.repositories.ProductRepository;
import com.project.marketplace.repositories.RoleRepository;
import com.project.marketplace.repositories.UserRepository;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository; // userRepository (sempre usar camelCase)

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public void run(String... args) throws Exception {

		Category cat1 = new Category(null, "Action");
		Category cat2 = new Category(null, "Roguelike");
		Category cat3 = new Category(null, "Multiplayer");

		categoryRepository.saveAll(Arrays.asList(cat1, cat2, cat3));

		Product p1 = new Product(null, "Ori and the blind forest",
				"Explora uma história profundamente emocional sobre amor e sacrifício, além da esperança que existe em todos nós.",
				99.00, true, true);
		Product p2 = new Product(null, "Hollow Knight",
				"Explore um vasto mundo interligado de caminhos esquecidos, florestas frondosas e cidades em ruínas.",
				46.99, false, true);
		Product p3 = new Product(null, "Cuphead",
				"A classic run and gun action game heavily focused on boss battles. Inspired by cartoons of the 1930s",
				36.99, true, false);
		Product p4 = new Product(null, "Hades",
				"Na pele do imortal Príncipe do Submundo, você usará os poderes e as armas míticas do Olimpo para se libertar das garras do deus dos mortos",
				73.99, true, true);
		Product p5 = new Product(null, "Resident Evil 4 (2005)",
				"Special agent Leon S. Kennedy is sent on a mission to rescue the U.S. President’s daughter who has been kidnapped",
				39.99, true, true);

		p1.getCategories().add(cat3);
		p2.getCategories().add(cat1);
		p3.getCategories().add(cat3);
		p4.getCategories().add(cat2);
		p4.getCategories().add(cat1);
		p5.getCategories().add(cat1);

		productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));

		User u1 = new User(null, "gabrielkrauscosta@gmail.com", "luanalinda");
		User u2 = new User(null, "luana@gmail.com", "gabriellindo");

		Customer c1 = new Customer(null, "Gabriel", "987654321", "2000", CustomerType.LEGALPERSON);
		Customer c2 = new Customer(null, "Luana", "12345678", "3000", CustomerType.NATURALPERSON);
		Customer c3 = new Customer(null, "Gabriel2", "9876543211", "5000", CustomerType.LEGALPERSON, u1);

		Order o1 = new Order(null, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, c1);
		Order o2 = new Order(null, Instant.parse("2019-07-21T03:42:10Z"), OrderStatus.WAITING_PAYMENT, c2);
		Order o3 = new Order(null, Instant.parse("2019-07-22T15:21:22Z"), OrderStatus.SHIPPED, c1);

		userRepository.saveAll(Arrays.asList(u1, u2));
		customerRepository.saveAll(Arrays.asList(c1, c2));
		orderRepository.saveAll(Arrays.asList(o1, o2, o3));

		OrderItem oi1 = new OrderItem(o1, p1, 2);
		OrderItem oi2 = new OrderItem(o1, p3, 1);
		OrderItem oi3 = new OrderItem(o2, p3, 2);
		OrderItem oi4 = new OrderItem(o3, p5, 2);

		Set<Product> library = new HashSet<>();
		library.add(p5);
		c1.setLibrary(library);

		Address ad1 = new Address(null, "Rua Doná Joaquina", 201, "Avenida", 37504048, "Brazil",
				AddressType.HOMEADDRESS, c3);

//		c1.setUser(u1);

		Role r1 = new Role(null, Authorities.ADMIN);
		List<Role> roles = new ArrayList<>();
		roles.add(r1);

		User u3 = new User(null, "testeRole@gmail.com", "luanalinda", roles);

		o3.setDeliveryAddress(ad1);
		customerRepository.saveAll(Arrays.asList(c3));
		addressRepository.saveAll(Arrays.asList(ad1));
		orderRepository.saveAll(Arrays.asList(o3));
		roleRepository.saveAll(Arrays.asList(r1));
		
		productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));
		orderItemRepository.saveAll(Arrays.asList(oi1, oi2, oi3, oi4));
		userRepository.saveAll(Arrays.asList(u3));
	}

}
