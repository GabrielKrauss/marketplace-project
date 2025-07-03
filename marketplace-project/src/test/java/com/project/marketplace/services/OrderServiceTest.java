package com.project.marketplace.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

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

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService service;

    @Mock
    private OrderRepository repository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private AddressRepository addressRepository;

    private Order order;
    private Customer customer;
    private Address address;
    private Coupon coupon;
    private Product product;
    private OrderItem orderItem;

    @BeforeEach
    void setup() {
        customer = new Customer();
        customer.setId(1L);
        customer.setLibrary(new ArrayList<>());

        address = new Address();
        address.setId(1L);
        address.setCustomer(customer);

        coupon = new Coupon();
        coupon.setId(1L);
        coupon.setIsActive(true);
        coupon.setDiscountValue(10.0);
        coupon.setDiscountPercentage(0.0);

        product = new Product();
        product.setId(1L);
        product.setIsPhysical(true);
        product.setStock(10);
        product.setUnitPrice(2.0);
        product.setSellIndicator(true);
        product.setIsDeleted(false);

        orderItem = new OrderItem();
        orderItem.setProductId(product.getId());
        orderItem.setQuantity(1);
        orderItem.setProduct(product);

        order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        order.setCustomerId(customer.getId());
        order.setAddressId(address.getId());
        order.setItems(List.of(orderItem));
        Mockito.lenient().when(couponRepository.findById(anyLong())).thenReturn(Optional.of(new Coupon()));
    }

    @Test
    void testFindAllActives_ShouldReturnActiveOrders() {
        when(repository.findAllActiveOrders()).thenReturn(List.of(order));
        var result = service.findAllActives();
        assertEquals(1, result.size());
    }

    @Test
    void testFindById_ShouldReturnOrder_WhenIdExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(order));
        var result = service.findById(1L);
        assertNotNull(result);
    }

    @Test
    void testFindById_ShouldThrowException_WhenIdDoesNotExist() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.findById(1L));
    }

    @Test
    void testInsert_ShouldThrowException_WhenCustomerNotFound() {
        when(customerRepository.findById(order.getCustomerId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.insert(order));
    }

    @Test
    void testInsert_ShouldThrowException_WhenAddressNotFound() {
        when(customerRepository.findById(order.getCustomerId())).thenReturn(Optional.of(customer));
        when(addressRepository.findById(order.getAddressId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.insert(order));
    }

    @Test
    void testInsert_ShouldThrowException_WhenCouponIsInactive() {
        coupon.setIsActive(false);
        order.setCoupon(coupon);

        when(customerRepository.findById(order.getCustomerId())).thenReturn(Optional.of(customer));
        when(addressRepository.findById(order.getAddressId())).thenReturn(Optional.of(address));
        when(couponRepository.findById(coupon.getId())).thenReturn(Optional.of(coupon));

        assertThrows(InvalidDataException.class, () -> service.insert(order));
    }

    @Test
    void testInsert_ShouldThrowException_WhenProductNotFound() {
        when(customerRepository.findById(order.getCustomerId())).thenReturn(Optional.of(customer));
        when(addressRepository.findById(order.getAddressId())).thenReturn(Optional.of(address));
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.insert(order));
    }
    @Test
    void testInsertInvalidAddressForCustomer_ShouldThrowInvalidDataException() {
        // Reutiliza o objeto order do setUp e altera o endereço para um inválido.
        // O endereço do setUp possui id = 1 e customer associado (id = 1).
        // Aqui, criamos um endereço inválido (com id 2 e com um customer diferente).
        Address invalidAddress = new Address();
        invalidAddress.setId(2L);
        // Cria um novo customer (com id diferente, por exemplo, 99)
        Customer diffCustomer = new Customer();
        diffCustomer.setId(99L);
        invalidAddress.setCustomer(diffCustomer);
        
        // Atualiza o order para usar o endereço inválido
        order.setAddressId(invalidAddress.getId());
        
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(addressRepository.findById(invalidAddress.getId())).thenReturn(Optional.of(invalidAddress));
        
        // Act & Assert: deve lançar InvalidDataException
        assertThrows(InvalidDataException.class, () -> service.insert(order));
    }

    @Test
    void testInsertProductNotAvailable_ShouldThrowResourceNotAvailableException() {
        // Reutiliza o objeto order do setUp; alteramos o produto para que não esteja disponível.
        // Modificamos o produto do orderItem do setUp.
        product.setIsDeleted(true);        // Produto marcado como deletado
        product.setSellIndicator(false);     // Produto não disponível para venda

        // Configura os mocks para retornar customer e address do setUp.
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        // Act & Assert: deve lançar ResourceNotAvailableException
        assertThrows(ResourceNotAvailableException.class, () -> service.insert(order));
    }

    @Test
    void testInsertInsufficientStock_ShouldThrowInvalidDataException() {
        // Reutiliza o order do setUp, mas altera o estoque do produto para 0 e a quantidade do item para 2.
        product.setStock(0);                // Estoque insuficiente
        // Atualiza a quantidade do item:
        orderItem.setQuantity(2);
        
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        
        // Act & Assert
        assertThrows(InvalidDataException.class, () -> service.insert(order));
    }

    @Test
    void testInsertProductAlreadyInLibrary_ShouldThrowObjectAlreadyExistsException() {
        // Reutiliza o order do setUp.
        // Configura o customer do setUp para já ter o produto na biblioteca.
        customer.setLibrary(List.of(product));
        
        // Configura o produto como não físico para disparar a exceção desejada.
        product.setIsPhysical(false);
        
        // Os repositórios já estão configurados no setUp para customer e address:
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        
        // Act & Assert
        assertThrows(ObjectAlreadyExistsException.class, () -> service.insert(order));
    }
    @Test
    void testInsertOrderSuccessfully_ShouldReturnOrder_WhenAllDataIsValid() {
        // Reutiliza o order do setUp que já possui:
        // - Customer (id 1), Address (id 1) com customer associado, Coupon ativo e OrderItem com product disponível.
    	
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        when(repository.save(order)).thenReturn(order);
        
        // Act
        Order result = service.insert(order);
        
        // Assert
        assertNotNull(result);
        // Como no setUp o status já está definido como WAITING_PAYMENT, espera-se esse status.
        assertEquals(OrderStatus.WAITING_PAYMENT, result.getOrderStatus());
    }


    @Test
    void testDelete_ShouldMarkOrderAsDeleted_WhenIdExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(order));
        service.delete(1L);
        assertTrue(order.getIsDeleted());
    }

    @Test
    void testDelete_ShouldThrowException_WhenDataIntegrityViolation() {
        when(repository.findById(1L)).thenThrow(DataIntegrityViolationException.class);
        assertThrows(DatabaseException.class, () -> service.delete(1L));
    }

    @Test
    void testUpdate_ShouldThrowException_WhenOrderNotFound() {
        when(repository.getReferenceById(1L)).thenThrow(EntityNotFoundException.class);
        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, order));
    }
    
    @Test
    void testUpdateData_ShouldThrowInvalidDataException_WhenEntityIsNull() {
        assertThrows(InvalidDataException.class, () -> service.updateData(null, order));
    }

    @Test
    void testUpdateData_ShouldThrowInvalidDataException_WhenUpdateObjectIsNull() {
        assertThrows(InvalidDataException.class, () -> service.updateData(order, null));
    }

    @Test
    void testUpdateData_ShouldThrowInvalidDataException_WhenCouponIsInactive() {
        // Reaproveita o coupon do setUp e o torna inativo
        coupon.setIsActive(false);
        Order update = new Order();
        update.setCoupon(coupon);
        // Como o coupon inativo deve gerar exceção, o teste espera InvalidDataException.
        assertThrows(InvalidDataException.class, () -> service.updateData(order, update));
        // Restaura o valor para os demais testes
        coupon.setIsActive(true);
    }

    @Test
    void testUpdateData_ShouldThrowInvalidDataException_WhenEntityOrderStatusIsNull() {
        // Cria um spy para a entidade, para sobrescrever o getter de orderStatus
        Order entityTest = Mockito.spy(new Order());
        entityTest.setCustomer(customer);          // Garante que o customer esteja definido
        entityTest.setItems(new ArrayList<>());      // Sem itens para evitar iterar
        // Sobrescreve o getter para que retorne null sem disparar NPE
        Mockito.doReturn(null).when(entityTest).getOrderStatus();
        
        Order update = new Order();
        update.setOrderStatus(OrderStatus.PAID);
        
        // Agora, quando updateData for chamado, o getter retornará null e a lógica deverá lançar InvalidDataException.
        assertThrows(InvalidDataException.class, () -> service.updateData(entityTest, update));
    }

    @Test
    void testUpdateData_ShouldThrowInvalidDataException_WhenCancelDeliveredOrder() {
        // Define que a entity já foi entregue
        order.setOrderStatus(OrderStatus.DELIVERED);
        Order update = new Order();
        update.setOrderStatus(OrderStatus.CANCELED);
        assertThrows(InvalidDataException.class, () -> service.updateData(order, update));
        // Restaura
        order.setOrderStatus(OrderStatus.WAITING_PAYMENT);
    }

    @Test
    void testUpdateData_ShouldProcessCancellation_ForPhysicalProducts() {
        // Neste cenário, quando a atualização for para CANCELLED, para cada item físico,
        // o estoque deverá ser ajustado (somado a quantidade do item).
        int estoqueAntigo = product.getStock();
        Order update = new Order();
        update.setOrderStatus(OrderStatus.CANCELED);
        order.setCustomer(customer);
        // Supondo que order já possui um item com 'product' (definido no setUp)
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        service.updateData(order, update);
        // Verifica se o estoque foi incrementado corretamente
        assertEquals(estoqueAntigo + orderItem.getQuantity(), product.getStock());
    }

    @Test
    void testUpdateData_ShouldSetOrderStatusToDelivered_WhenAllProductsAreNonPhysical() {
        // Configura o produto para ser não físico
        product.setIsPhysical(false);
        // Garante que a biblioteca do customer esteja vazia
        customer.setLibrary(new ArrayList<>());
        Order update = new Order();
        update.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        order.setCustomer(customer);
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        service.updateData(order, update);
        // Conforme a lógica, se todos os produtos não são físicos, o update deve ser CANCELLED e
        // o método atribui o status DELIVERED a update (ou à entity – conforme a implementação).
        // Aqui, assumindo que update é alterado:
        assertEquals(OrderStatus.DELIVERED, update.getOrderStatus());
        // Restaura para os demais testes
        product.setIsPhysical(true);
    }

    @Test
    void testUpdateData_ShouldThrowObjectAlreadyExistsException_WhenNonPhysicalProductAlreadyInLibrary_AndStatusWaitingPayment() {
        // Configura o produto como não físico
        product.setIsPhysical(false);
        
        // Garante que o order (entity) possua o customer
        order.setCustomer(customer);
        
        // Adiciona o produto à biblioteca do customer para simular que ele já existe
        customer.setLibrary(List.of(product));
        
        // Mock do repositório para o customer, garantindo que o customer é encontrado
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        
        Order update = new Order();
        update.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        
        // Act & Assert: Espera a exceção ObjectAlreadyExistsException ser lançada
        assertThrows(ObjectAlreadyExistsException.class, () -> service.updateData(order, update));
        
        // Restaura
        product.setIsPhysical(true);
        customer.setLibrary(new ArrayList<>());
    }

    @Test
    void testUpdateData_ShouldAddNonPhysicalProductToLibrary_WhenStatusIsPaid() {
        // Configura o produto como não físico e garante que a biblioteca do customer esteja vazia
        product.setIsPhysical(false);
        customer.setLibrary(new ArrayList<>());
        // Certifique-se de que o order tem o customer definido
        order.setCustomer(customer);
        
        Order update = new Order();
        update.setOrderStatus(OrderStatus.PAID);
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        service.updateData(order, update);
        
        // Verifica se o produto foi adicionado à biblioteca do customer
        assertTrue(customer.getLibrary().contains(product));
        
        // Restaura para os demais testes
        product.setIsPhysical(true);
    }

    @Test
    void testFindAll_ShouldReturnAllOrders() {
        // Arrange: Criação de objetos Order simulados
        Order order1 = new Order();
        Order order2 = new Order();
        List<Order> orders = List.of(order1, order2);

        // Mock do repositório para retornar a lista de pedidos
        when(repository.findAll()).thenReturn(orders);

        // Act: Chama o método findAll
        List<Order> result = service.findAll();

        // Assert: Verifica se a lista de pedidos retornada é igual à lista mockada
        assertEquals(orders.size(), result.size());
        assertTrue(result.contains(order1));
        assertTrue(result.contains(order2));
    }
    
    @Test
    void testUpdate_ShouldUpdateOrder_WhenValidData() {
        // Arrange: Criação de objetos Order e Customer simulados
        Customer customer = new Customer();
        customer.setId(1L);
        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setCustomer(customer);
        existingOrder.setOrderStatus(OrderStatus.WAITING_PAYMENT);  // Definir um status inicial válido

        Order updateOrder = new Order();
        updateOrder.setId(1L);
        updateOrder.setOrderStatus(OrderStatus.PAID);

        // Mock do repositório
        when(repository.getReferenceById(1L)).thenReturn(existingOrder);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(repository.save(existingOrder)).thenReturn(existingOrder); // Mock de save para retornar o updatedOrder

        // Act: Chama o método update
        Order updatedOrder = service.update(1L, updateOrder);

        // Assert: Verifica se o pedido foi atualizado corretamente
        assertNotNull(updatedOrder); // Certifique-se de que o updatedOrder não é null
        assertEquals(OrderStatus.PAID, updatedOrder.getOrderStatus()); // Verifica se o status foi atualizado corretamente
    }
    
    @Test
    void testUpdate_ShouldNotThrowException_WhenCouponIsNull() {
        // Arrange: Criação de objetos Order e Customer simulados
        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        existingOrder.setCustomer(customer);  // Já utilizando o 'customer' do setup
        existingOrder.setCoupon(null);  // Coupon é null
        
        Order updateOrder = new Order();
        updateOrder.setId(1L);
        updateOrder.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        updateOrder.setCoupon(null);  // Coupon é null

        // Mock do repositório
        when(repository.getReferenceById(1L)).thenReturn(existingOrder);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(repository.save(existingOrder)).thenReturn(existingOrder);

        // Act
        Order updatedOrder = service.update(1L, updateOrder);

        // Assert: Verifica se o pedido foi atualizado sem alterações no coupon
        assertNull(updatedOrder.getCoupon());
    }

    @Test
    void testUpdate_ShouldThrowResourceNotFoundException_WhenCouponIsNotFound() {
        // Arrange: Criação de objetos Order e Customer simulados
        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        existingOrder.setCustomer(customer);  // Já utilizando o 'customer' do setup
        
        Coupon coupon = new Coupon();
        coupon.setId(2L);  // ID do coupon será diferente do que será passado
        coupon.setIsActive(true);

        Order updateOrder = new Order();
        updateOrder.setId(1L);
        updateOrder.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        updateOrder.setCoupon(coupon);  // Coupon que não existe no repositório

        // Mock do repositório
        when(repository.getReferenceById(1L)).thenReturn(existingOrder);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(couponRepository.findById(2L)).thenReturn(Optional.empty());  // Coupon não encontrado

        // Act & Assert: Espera a exceção ResourceNotFoundException ser lançada
        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, updateOrder));
    }

    @Test
    void testUpdate_ShouldThrowInvalidDataException_WhenCouponIsNotActive() {
        // Arrange: Criação de objetos Order e Customer simulados
        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        existingOrder.setCustomer(customer);  // Já utilizando o 'customer' do setup
        existingOrder.setCoupon(coupon);  // Já utilizando o 'coupon' do setup

        // Criando um coupon inativo
        Coupon inactiveCoupon = new Coupon();
        inactiveCoupon.setId(1L);
        inactiveCoupon.setIsActive(false);  // Coupon está inativo

        Order updateOrder = new Order();
        updateOrder.setId(1L);
        updateOrder.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        updateOrder.setCoupon(inactiveCoupon);  // Coupon inativo

        // Mock do repositório
        when(repository.getReferenceById(1L)).thenReturn(existingOrder);
        
        // Act & Assert: Espera a exceção InvalidDataException ser lançada
        assertThrows(InvalidDataException.class, () -> service.update(1L, updateOrder));
    }
    
    @Test
    void testUpdate_ShouldThrowInvalidDataException_WhenCouponIsInactive() {
        // Arrange: Criação de objetos Order e Customer simulados
        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        existingOrder.setCustomer(customer);  // Já utilizando o 'customer' do setup
        
        Coupon coupon = new Coupon();
        coupon.setId(2L);
        coupon.setIsActive(false);  // Coupon está inativo

        Order updateOrder = new Order();
        updateOrder.setId(1L);
        updateOrder.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        updateOrder.setCoupon(coupon);  // Coupon inativo

        // Mock do repositório
        when(repository.getReferenceById(1L)).thenReturn(existingOrder);

        // Act & Assert: Espera a exceção InvalidDataException ser lançada
        assertThrows(InvalidDataException.class, () -> service.update(1L, updateOrder));
    }
    
    @Test
    void testUpdate_ShouldUpdateOrder_WhenCouponIsValidAndActive() {
        // Arrange: Criação de objetos Order e Customer simulados
        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        existingOrder.setCustomer(customer);  // Já utilizando o 'customer' do setup
        
        Coupon coupon = new Coupon();
        coupon.setId(2L);
        coupon.setIsActive(true);  // Coupon está ativo

        Order updateOrder = new Order();
        updateOrder.setId(1L);
        updateOrder.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        updateOrder.setCoupon(coupon);  // Coupon ativo

        // Mock do repositório
        when(repository.getReferenceById(1L)).thenReturn(existingOrder);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(couponRepository.findById(2L)).thenReturn(Optional.of(coupon));
        when(repository.save(existingOrder)).thenReturn(existingOrder);

        // Act
        Order updatedOrder = service.update(1L, updateOrder);

        // Assert: Verifica se o coupon foi associado corretamente ao pedido
        assertEquals(coupon, updatedOrder.getCoupon());
    }
}
