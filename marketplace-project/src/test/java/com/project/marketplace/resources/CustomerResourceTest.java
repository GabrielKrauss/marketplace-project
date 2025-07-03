package com.project.marketplace.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.marketplace.entities.Address;
import com.project.marketplace.entities.Customer;
import com.project.marketplace.entities.Product;
import com.project.marketplace.entities.enums.AddressType;
import com.project.marketplace.entities.enums.CustomerType;
import com.project.marketplace.services.CustomerService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CustomerResource.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService service;

    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    void setUp() {
        customer1 = createSampleCustomer();
        customer2 = new Customer(2L, "Jane Doe", "987654321", "987-654-321", "B", CustomerType.NATURAL_PERSON, null, null);
    }

    private Customer createSampleCustomer() {
        Address address = new Address(1L, "Street 123", 101, "Downtown", 12345, "USA", "City 1", AddressType.HOME_ADDRESS, null);
        Product product = new Product(1L, "Sample Product", "Description", 100.0, 10, true, true, null, null);
        return new Customer(1L, "John Doe", "123456789", "123-456-789", "A", CustomerType.LEGAL_PERSON, List.of(address), List.of(product));
    }

    @Test
    @WithMockUser(authorities = "Admin")
    void testFindAll() throws Exception {
        List<Customer> list = Arrays.asList(customer1, customer2);
        when(service.findAll()).thenReturn(list);

        mockMvc.perform(get("/customers"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1L))
               .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @WithMockUser(authorities = "Admin")
    void testFindById() throws Exception {
        when(service.findById(1L)).thenReturn(customer1);

        mockMvc.perform(get("/customers/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(authorities = "Operator")
    void testInsert() throws Exception {
        when(service.insert(any(Customer.class))).thenReturn(customer1);

        mockMvc.perform(post("/customers")
               .contentType(MediaType.APPLICATION_JSON)
               .content(new ObjectMapper().writeValueAsString(customer1)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(authorities = "Admin")
    void testDelete() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/customers/1"))
               .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "Admin")
    void testUpdate() throws Exception {
        when(service.update(eq(1L), any(Customer.class))).thenReturn(customer1);

        mockMvc.perform(patch("/customers/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(new ObjectMapper().writeValueAsString(customer1)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L));
    }
    
    @Test
    @WithMockUser(authorities = "Admin")
    void testFindAllActive() throws Exception {
        List<Customer> list = Arrays.asList(customer1, customer2);
        when(service.findAllActive()).thenReturn(list);

        mockMvc.perform(get("/customers/activeCustomers"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1L))
               .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @WithMockUser(authorities = "Admin")
    void testFindByKeycloakIdWhenCustomerExists() throws Exception {
        when(service.findByKeycloakId("keycloak-id")).thenReturn(customer1);

        mockMvc.perform(get("/customers/keycloak/keycloak-id"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L));
    }
    
    @Test
    @WithMockUser(authorities = "Admin")
    void testFindByKeycloakIdWhenCustomerDoesNotExist() throws Exception {
        when(service.findByKeycloakId("non-existing-id")).thenReturn(null);

        mockMvc.perform(get("/customers/keycloak/non-existing-id"))
               .andExpect(status().isNotFound());
    }
}
