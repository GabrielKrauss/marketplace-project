package com.project.marketplace.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.project.marketplace.entities.Customer;
import com.project.marketplace.repositories.CustomerRepository;
import com.project.marketplace.services.exceptions.DatabaseException;
import com.project.marketplace.services.exceptions.ObjectAlreadyExistsException;
import com.project.marketplace.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
class CustomerServiceTest {

	@InjectMocks
    private CustomerService service;

    @Mock
    private CustomerRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

	
	
    @Test
    void testFindAll() {
        Customer customer1 = new Customer();
        Customer customer2 = new Customer();
        when(repository.findAll()).thenReturn(Arrays.asList(customer1, customer2));

        var result = service.findAll();

        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }
    
    @Test
    void testFindAllActive() {
        Customer customer1 = new Customer();
        Customer customer2 = new Customer();
        when(repository.findAllActiveCustomers()).thenReturn(Arrays.asList(customer1, customer2));

        var result = service.findAllActive();

        assertEquals(2, result.size());
        verify(repository, times(1)).findAllActiveCustomers();
    }

    @Test
    void testFindByKeycloakIdSuccess() {
        Customer customer = new Customer();
        when(repository.findByKeycloakId("keycloak-123")).thenReturn(customer);

        var result = service.findByKeycloakId("keycloak-123");

        assertNotNull(result);
        verify(repository, times(1)).findByKeycloakId("keycloak-123");
    }

    @Test
    void testFindByKeycloakId_whenNotFound_shouldReturnNull() {
        when(repository.findByKeycloakId("keycloak-123")).thenReturn(null);

        var result = service.findByKeycloakId("keycloak-123");

        assertEquals(null, result);
        verify(repository, times(1)).findByKeycloakId("keycloak-123");
    }

    @Test
    void testInsert_whenDocumentExistsAndIsDeleted_shouldReactivateCustomer() {
        Customer existingCustomer = new Customer();
        existingCustomer.setDocumentNumber("123456789");
        existingCustomer.setIsDeleted(true);

        when(repository.existsByDocumentNumber("123456789")).thenReturn(true);
        when(repository.findByDocumentNumber("123456789")).thenReturn(existingCustomer);
        when(repository.save(any(Customer.class))).thenReturn(existingCustomer);

        Customer newCustomer = new Customer();
        newCustomer.setDocumentNumber("123456789");

        var result = service.insert(newCustomer);

        assertNotNull(result);
        assertFalse(existingCustomer.getIsDeleted()); // Verifica se existingCustomer foi atualizado
        verify(repository, times(1)).save(existingCustomer);
    }

    @Test
    void testFindByIdSuccess() {
        Customer customer = new Customer();
        when(repository.findById(1L)).thenReturn(Optional.of(customer));

        var result = service.findById(1L);

        assertNotNull(result);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testFindById_whenNotFound_shouldThrowResourceNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
        verify(repository, times(1)).findById(1L);
    }
    
    @Test
    void testInsertSuccess() {
        Customer customer = new Customer();
        when(repository.existsByDocumentNumber("123456789")).thenReturn(false);
        when(repository.save(any(Customer.class))).thenReturn(customer);

        var result = service.insert(customer);

        assertNotNull(result);
        verify(repository, times(1)).save(customer);
    }

    @Test
    void testInsert_whenDocumentAlreadyExists_shouldThrowObjectAlreadyExistsException() {
    	 Customer existingCustomer = new Customer();
    	    existingCustomer.setDocumentNumber("123456789");
    	    existingCustomer.setIsDeleted(false);

    	    when(repository.existsByDocumentNumber("123456789")).thenReturn(true);
    	    when(repository.findByDocumentNumber("123456789")).thenReturn(existingCustomer);

    	    Customer newCustomer = new Customer();
    	    newCustomer.setDocumentNumber("123456789");

    	    assertThrows(ObjectAlreadyExistsException.class, () -> service.insert(newCustomer));

    	    verify(repository, times(1)).existsByDocumentNumber("123456789");
    	    verify(repository, times(1)).findByDocumentNumber("123456789");
    	    verify(repository, never()).save(any());
    }

    @Test
    void testDeleteSuccess() {
        Customer customer = new Customer();
        when(repository.findById(1L)).thenReturn(Optional.of(customer));

        service.delete(1L);

        verify(repository, times(1)).save(customer);
        assertTrue(customer.getIsDeleted());
    }

    @Test
    void testDelete_whenNotFound_shouldThrowResourceNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }

    @Test
    void testDeleteDatabaseException() {
        when(repository.findById(1L)).thenThrow(new DataIntegrityViolationException("Database error"));

        assertThrows(DatabaseException.class, () -> service.delete(1L));
    }
    
    @Test
    void testUpdateSuccess() {
        Customer entity = new Customer();
        Customer updatedData = new Customer();
        updatedData.setName("Updated Name");
        when(repository.getReferenceById(1L)).thenReturn(entity);
        when(repository.existsByDocumentNumber(anyString())).thenReturn(false);
        when(repository.save(any(Customer.class))).thenReturn(entity);

        var result = service.update(1L, updatedData);

        assertNotNull(result);
        assertEquals("Updated Name", entity.getName());
        verify(repository, times(1)).save(entity);
    }

    @Test
    void testUpdate_whenNotFound_shouldThrowResourceNotFoundException() {
        when(repository.getReferenceById(1L)).thenThrow(new EntityNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, new Customer()));
    }

    @Test
    void testUpdate_whenDocumentAlreadyExists_shouldThrowObjectAlreadyExistsException() {
        Customer existingCustomer = new Customer();
        existingCustomer.setId(2L); // Simulando um cliente diferente do que está sendo atualizado
        existingCustomer.setDocumentNumber("123456789");

        Customer updatedData = new Customer();
        updatedData.setDocumentNumber("123456789");

        when(repository.findByDocumentNumber("123456789")).thenReturn(existingCustomer);

        assertThrows(ObjectAlreadyExistsException.class, () -> service.update(1L, updatedData));

        verify(repository, times(1)).findByDocumentNumber("123456789");
    }
    
    @Test
    void testUpdateData_AllFieldsUpdated() {
        Customer entity = new Customer();
        entity.setName("Original Name");
        entity.setPhone("123456789");
        entity.setDocumentNumber("111111111");
        entity.setIsDeleted(false);

        Customer updateObj = new Customer();
        updateObj.setName("Updated Name");
        updateObj.setPhone("987654321");
        updateObj.setDocumentNumber("222222222");
        updateObj.setIsDeleted(true);

        service.updateData(entity, updateObj);

        assertEquals("Updated Name", entity.getName());
        assertEquals("987654321", entity.getPhone());
        assertEquals("222222222", entity.getDocumentNumber());
        assertTrue(entity.getIsDeleted());
    }

    @Test
    void testUpdateData_whenPartialUpdate_shouldUpdateOnlyChangedFields() {
        Customer entity = new Customer();
        entity.setName("Original Name");
        entity.setPhone("123456789");
        entity.setDocumentNumber("111111111");
        
        entity.setIsDeleted(false);

        Customer updateObj = new Customer();
        updateObj.setName(null);
        updateObj.setPhone("987654321");
        updateObj.setDocumentNumber(null);
        updateObj.setIsDeleted(true);

        service.updateData(entity, updateObj);

        assertEquals("Original Name", entity.getName());
        assertEquals("987654321", entity.getPhone());
        assertEquals("111111111", entity.getDocumentNumber());
        assertTrue(entity.getIsDeleted());
    }

    @Test
    void testUpdateData_whenNoUpdate_shouldKeepOriginalValues() {
        Customer entity = new Customer();
        entity.setName("Original Name");
        entity.setPhone("123456789");
        entity.setDocumentNumber("111111111");
        entity.setIsDeleted(false);

        Customer updateObj = new Customer();

        service.updateData(entity, updateObj);

        assertEquals("Original Name", entity.getName());
        assertEquals("123456789", entity.getPhone());
        assertEquals("111111111", entity.getDocumentNumber());
        assertFalse(entity.getIsDeleted());
    }
    
}
