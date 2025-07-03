package com.project.marketplace.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.project.marketplace.entities.Address;
import com.project.marketplace.entities.Coupon;
import com.project.marketplace.entities.Customer;
import com.project.marketplace.entities.Order;
import com.project.marketplace.entities.OrderItem;
import com.project.marketplace.entities.Product;
import com.project.marketplace.entities.enums.OrderStatus;
import com.project.marketplace.repositories.AddressRepository;
import com.project.marketplace.repositories.CouponRepository;
import com.project.marketplace.repositories.CustomerRepository;
import com.project.marketplace.repositories.OrderItemRepository;
import com.project.marketplace.repositories.OrderRepository;
import com.project.marketplace.repositories.ProductRepository;
import com.project.marketplace.services.exceptions.DatabaseException;
import com.project.marketplace.services.exceptions.InvalidDataException;
import com.project.marketplace.services.exceptions.ObjectAlreadyExistsException;
import com.project.marketplace.services.exceptions.ResourceNotAvailableException;
import com.project.marketplace.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class OrderService {

	@Autowired
	private OrderRepository repository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private CouponRepository couponRepository;

	@Autowired
	private AddressRepository addressRepository;

	public List<Order> findAllActives() {
		return repository.findAllActiveOrders();
	}

	public List<Order> findAll() {
		return repository.findAll();
	}

	public Order findById(Long id) {
		Optional<Order> obj = repository.findById(id);
		return obj.get();
	}

	public List<Order> findAllActivesByCustomerId(Long id) {
		return repository.findAllActivesByCustomerId(id);
	}

	public Order insert(Order obj) {
		obj.setIsDeleted(false);

		Customer customer = customerRepository.findById(obj.getCustomerId())
				.orElseThrow(() -> new ResourceNotFoundException("Customer Doesn't Exist. Id: " + obj.getCustomerId()));
		obj.setCustomer(customer);

		Address address = addressRepository.findById(obj.getAddressId())
				.orElseThrow(() -> new ResourceNotFoundException("Address Doesn't Exist. Id: " + obj.getAddressId()));

		if (address.getCustomer().getId() != customer.getId()) {
			throw new InvalidDataException("Invalid delivery address for this customer.");
		}

		obj.setDeliveryAddress(address);

		obj.setMoment(Instant.now());

		if (obj.getCoupon() != null) {
			Coupon coupon = couponRepository.findById(obj.getCoupon().getId()).orElseThrow(
					() -> new ResourceNotFoundException("Coupon  Doesn't Exists. Id:" + obj.getCoupon().getId()));
			if (!coupon.getIsActive()) {
				throw new InvalidDataException("Coupon is not active");
			}
			obj.setCoupon(coupon);
		}

		repository.save(obj);

		boolean allProductsAreNotPhysical = true;
		boolean allProductsArePaid = false;

		for (OrderItem item : obj.getItems()) {
			item.getProductId();
			item.getQuantity();
			Product product = productRepository.findById(item.getProductId())
					.orElseThrow(() -> new ResourceNotFoundException("Product not found. Id: " + item.getProductId()));
			if (product.getIsDeleted() || !product.getSellIndicator()) {
				throw new ResourceNotAvailableException(product.getId());
			}
			if (!product.getIsPhysical() && customer.getLibrary().contains(product)) {
				obj.setOrderStatus(OrderStatus.CANCELED);
				throw new ObjectAlreadyExistsException(product.getId());
			}
			if (!product.getIsPhysical() && obj.getOrderStatus() == OrderStatus.PAID) {
				addToLibrary(customer, product);
				allProductsArePaid = true;
			}
			if (product.getIsPhysical()) {
				allProductsAreNotPhysical = false;
				if (product.getStock() < item.getQuantity()) {
					if (product.getStock() == 0) {
						product.setSellIndicator(false);
						productRepository.save(product);
					}
					throw new InvalidDataException("Insufficient stock for product ID: " + product.getId());
				}
				product.setStock(product.getStock() - item.getQuantity());
				productRepository.save(product);
			} else {
				item.setQuantity(1);
			}
			item.setProduct(product);
			item.setOrder(obj);
			orderItemRepository.save(item);
		}

		if (allProductsAreNotPhysical && allProductsArePaid) {
			obj.setOrderStatus(OrderStatus.SHIPPED);
		} else {
			obj.setOrderStatus(obj.getOrderStatus());
		}

		obj.setTotal(obj.getTotal());
		repository.save(obj);

		return obj;
	}

	public void delete(Long id) {
		try {
			Order order = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
			order.setIsDeleted(true);
			repository.save(order);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public Order update(Long id, Order obj) {
		try {
			Order entity = repository.getReferenceById(id);
			updateData(entity, obj);
			if (obj.getCoupon() != null) {
				Coupon coupon = couponRepository.findById(obj.getCoupon().getId())
						.orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
				if (!coupon.getIsActive()) {
					throw new InvalidDataException("Coupon is not active");
				}
				entity.setCoupon(coupon);
			}
			entity.setTotal(obj.getTotal());
			return repository.save(entity);
		} catch (NoSuchElementException | EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		}

	}

	protected void updateData(Order entity, Order obj) {
		if (entity == null || obj == null) {
			throw new InvalidDataException("Order data is invalid");
		}
		if (obj.getCoupon() != null && !obj.getCoupon().getIsActive()) {
			throw new InvalidDataException("Coupon is inactive");
		}
		if (entity.getOrderStatus() == null) {
			throw new InvalidDataException("Order status is null");
		}
		if (obj.getOrderStatus() == OrderStatus.CANCELED && entity.getOrderStatus() == OrderStatus.DELIVERED) {
			throw new InvalidDataException("Can't Cancel a Delivered Order");
		}
		if (obj.getOrderStatus() == OrderStatus.CANCELED) {
			for (OrderItem item : entity.getItems()) {
				Product product = productRepository.findById(item.getProductId()).orElseThrow(
						() -> new ResourceNotFoundException("Product not found. Id: " + item.getProductId()));

				if (product.getIsPhysical()) {
					product.setStock(product.getStock() + item.getQuantity());
					productRepository.save(product);
				}
			}
		}

		entity.setOrderStatus((obj.getOrderStatus() != null) ? obj.getOrderStatus() : entity.getOrderStatus());
		entity.setDeliveryAddress(
				(obj.getDeliveryAddress() != null) ? obj.getDeliveryAddress() : entity.getDeliveryAddress());

		boolean allProductsAreNotPhysical = true;

		Customer customer = customerRepository.findById(entity.getCustomer().getId())
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

		for (OrderItem item : entity.getItems()) {
			Product product = productRepository.findById(item.getProductId())
					.orElseThrow(() -> new ResourceNotFoundException("Product not found. Id: " + item.getProductId()));

			if (!product.getIsPhysical() && customer.getLibrary().contains(product)
					&& entity.getOrderStatus() == OrderStatus.WAITING_PAYMENT) {
				obj.setOrderStatus(OrderStatus.CANCELED);
				throw new ObjectAlreadyExistsException(product.getId());
			}
			if (!product.getIsPhysical() && entity.getOrderStatus() == OrderStatus.PAID) {
				addToLibrary(customer, product);
			}
			if (product.getIsPhysical()) {
				allProductsAreNotPhysical = false;
			}
		}
		if (allProductsAreNotPhysical) {
			obj.setOrderStatus(OrderStatus.DELIVERED);
		}
	}

	public void addToLibrary(Customer customer, Product product) {
		List<Product> library = new ArrayList<>(customer.getLibrary());

		if (!library.contains(product)) {
			library.add(product);
			customer.setLibrary(library);
			customerRepository.save(customer);
			System.out.println("Produto adicionado com sucesso. Novo tamanho da library: " + library.size());
		} else {
			System.out.println("Produto já existe na library do cliente.");
		}
	}

	public Order operatorUpdate(Long id, Order obj) {
		try {
			Order entity = repository.getReferenceById(id);
			updateDeliveryAddress(entity, obj);
			return repository.save(entity);
		} catch (NoSuchElementException | EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		}
	}

	protected void updateDeliveryAddress(Order entity, Order obj) {
		if (obj.getDeliveryAddress() == null || obj.getDeliveryAddress().getId() == null) {
			throw new InvalidDataException("Delivery address ID is required");
		}

		Address newAddress = addressRepository.findById(obj.getDeliveryAddress().getId())
				.orElseThrow(() -> new ResourceNotFoundException("Address not found"));

		entity.setDeliveryAddress(newAddress);
	}
}
