package com.project.marketplace.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.marketplace.entities.Address;
import com.project.marketplace.entities.Customer;
import com.project.marketplace.entities.Order;
import com.project.marketplace.entities.OrderItem;
import com.project.marketplace.entities.Product;
import com.project.marketplace.repositories.AddressRepository;
import com.project.marketplace.repositories.CustomerRepository;
import com.project.marketplace.repositories.OrderItemRepository;
import com.project.marketplace.repositories.OrderRepository;
import com.project.marketplace.repositories.ProductRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository repository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	

	public List<Order> findAll() {
		return repository.findAll();
	}

	public Order findById(Long id) {
		Optional<Order> obj = repository.findById(id);
		return obj.get();
	}
	
	public Order insert(Order obj) {
		Address address = addressRepository.findById(obj.getAddressId()).get();
		obj.setDeliveryAddress(address);
		
		Customer customer = customerRepository.findById(obj.getCustomerId()).get();
		obj.setCustomer(customer);
		
		repository.save(obj);
		
//		Product product = new Product();
//		for(OrderItem item : obj.getItems()) {
//			for (Long x : item.getProductsId()) {
//			    product = productRepository.findById(x).get();
//				item.setProduct(product);
//				item.setOrder(obj);
//				orderItemRepository.save(item);
//			}			
//		}
		
		
		for(OrderItem item : obj.getItems()) {
			Product product = productRepository.findById(item.getProductId()).get();
			item.setProduct(product);
			item.setOrder(obj);
			orderItemRepository.save(item);
		}
		return repository.save(obj);
	}
}
