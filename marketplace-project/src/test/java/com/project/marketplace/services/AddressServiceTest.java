package com.project.marketplace.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;

import com.project.marketplace.entities.Address;
import com.project.marketplace.entities.Customer;
import com.project.marketplace.entities.enums.AddressType;
import com.project.marketplace.repositories.AddressRepository;
import com.project.marketplace.repositories.CustomerRepository;
import com.project.marketplace.services.exceptions.DatabaseException;
import com.project.marketplace.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class AddressServiceTest {

    @InjectMocks
    private AddressService service;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerService customerService;

    private Address mockAddress;
    private Customer mockCustomer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockCustomer = new Customer();
        mockCustomer.setId(1L);
        mockCustomer.setName("John Doe");

        mockAddress = new Address();
        mockAddress.setId(1L);
        mockAddress.setStreet("123 Main St");
        mockAddress.setCustomer(mockCustomer);
    }

    @Test
    void testFindAllSuccess() {
        when(addressRepository.findAll()).thenReturn(List.of(mockAddress));

        List<Address> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockAddress.getStreet(), result.get(0).getStreet());
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdSuccess() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(mockAddress));

        Address result = service.findById(1L);

        assertNotNull(result);
        assertEquals(mockAddress.getStreet(), result.getStreet());
        verify(addressRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_whenNotFound_shouldThrowResourceNotFoundException() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
        verify(addressRepository, times(1)).findById(1L);
    }

    @Test
    void testInsertSuccess() {
        when(customerService.findById(1L)).thenReturn(mockCustomer);
        when(addressRepository.save(mockAddress)).thenReturn(mockAddress);

        Address result = service.insert(1L, mockAddress);

        assertNotNull(result);
        assertEquals(mockCustomer, result.getCustomer());
        verify(customerService, times(1)).findById(1L);
        verify(addressRepository, times(1)).save(mockAddress);
    }

    @Test
    void testDeleteSuccess() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(mockAddress));

        service.delete(1L);

        verify(addressRepository, times(1)).findById(1L);
        verify(addressRepository, times(1)).save(mockAddress); // Mudado de deleteById para save
        assertEquals(false, mockAddress.getIsActive()); // Verifica se foi marcado como inativo
    }
    
    @Test
    void testDelete_whenNotFound_shouldThrowResourceNotFoundException() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));

        verify(addressRepository, times(1)).findById(1L);
        verify(addressRepository, never()).save(mockAddress); // Certifica que não tentou salvar nada
    }

    @Test
    void testDelete_whenDatabaseException_shouldThrowDatabaseException() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(mockAddress));
        doThrow(new DataIntegrityViolationException("Integrity violation")).when(addressRepository).save(mockAddress);

        assertThrows(DatabaseException.class, () -> service.delete(1L));

        verify(addressRepository, times(1)).findById(1L);
        verify(addressRepository, times(1)).save(mockAddress);
    }
    @Test
    void testUpdateSuccess() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        when(addressRepository.getReferenceById(1L)).thenReturn(mockAddress);
        when(addressRepository.save(mockAddress)).thenReturn(mockAddress);

        Address updatedAddress = new Address();
        updatedAddress.setStreet("456 Elm St");
        Address result = service.update(1L, 1L, updatedAddress);

        assertNotNull(result);
        assertEquals("456 Elm St", result.getStreet());
        verify(addressRepository, times(1)).getReferenceById(1L);
        verify(addressRepository, times(1)).save(mockAddress);
    }

    @Test
    void testUpdate_whenCustomerNotFound_shouldThrowResourceNotFoundException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Address updatedAddress = new Address();
        updatedAddress.setStreet("456 Elm St");

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, 1L, updatedAddress));
        verify(customerRepository, times(1)).findById(1L);
        verify(addressRepository, never()).getReferenceById(anyLong());
    }

    @Test
    void testUpdate_whenAddressNotFound_shouldThrowResourceNotFoundException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        doThrow(new EntityNotFoundException()).when(addressRepository).getReferenceById(1L);

        Address updatedAddress = new Address();
        updatedAddress.setStreet("456 Elm St");

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, 1L, updatedAddress));
        verify(addressRepository, times(1)).getReferenceById(1L);
    }
    
    @Test
    void testUpdateData_whenAllFieldsUpdated_shouldUpdateAllFields() {
        Address entity = new Address();
        entity.setStreet("Old Street");
        entity.setNeighborhood("Old Neighborhood");
        entity.setHouseNumber(123);
        entity.setZipCode(12345);
        entity.setCountry("Old Country");
        entity.setAddressType(AddressType.HOME_ADDRESS);

        Address updateObj = new Address();
        updateObj.setStreet("New Street");
        updateObj.setNeighborhood("New Neighborhood");
        updateObj.setHouseNumber(456);
        updateObj.setZipCode(67890);
        updateObj.setCountry("New Country");
        updateObj.setAddressType(AddressType.BUSINES_ADDRESS);

        service.updateData(entity, updateObj);

        assertEquals("New Street", entity.getStreet());
        assertEquals("New Neighborhood", entity.getNeighborhood());
        assertEquals(456, entity.getHouseNumber());
        assertEquals(67890, entity.getZipCode());
        assertEquals("New Country", entity.getCountry());
        assertEquals(AddressType.BUSINES_ADDRESS, entity.getAddressType());
    }

    @Test
    void testUpdateData_whenPartialUpdate_shouldUpdateOnlyChangedFields() {
        Address entity = new Address();
        entity.setStreet("Old Street");
        entity.setNeighborhood("Old Neighborhood");
        entity.setHouseNumber(123);
        entity.setZipCode(12345);
        entity.setCountry("Old Country");
        entity.setAddressType(AddressType.HOME_ADDRESS);

        Address updateObj = new Address();
        updateObj.setStreet("New Street");
        updateObj.setNeighborhood(null);
        updateObj.setHouseNumber(456);
        updateObj.setZipCode(0); // Não vai mudar
        updateObj.setCountry(null);
        updateObj.setAddressType(AddressType.BUSINES_ADDRESS);

        service.updateData(entity, updateObj);

        assertEquals("New Street", entity.getStreet());
        assertEquals("Old Neighborhood", entity.getNeighborhood());
        assertEquals(456, entity.getHouseNumber());
        assertEquals(12345, entity.getZipCode());
        assertEquals("Old Country", entity.getCountry());
        assertEquals(AddressType.BUSINES_ADDRESS, entity.getAddressType());
    }

    @Test
    void testUpdateData_whenNoUpdate_shouldKeepOriginalValues() {
        Address entity = new Address();
        entity.setStreet("Old Street");
        entity.setNeighborhood("Old Neighborhood");
        entity.setHouseNumber(123);
        entity.setZipCode(12345);
        entity.setCountry("Old Country");
        entity.setAddressType(AddressType.HOME_ADDRESS);

        Address updateObj = new Address(); // Nenhum campo foi atualizado

        service.updateData(entity, updateObj);

        assertEquals("Old Street", entity.getStreet());
        assertEquals("Old Neighborhood", entity.getNeighborhood());
        assertEquals(123, entity.getHouseNumber());
        assertEquals(12345, entity.getZipCode());
        assertEquals("Old Country", entity.getCountry());
        assertEquals(AddressType.HOME_ADDRESS, entity.getAddressType());
    }
}
