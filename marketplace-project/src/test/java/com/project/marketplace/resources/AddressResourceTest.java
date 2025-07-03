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
import com.project.marketplace.entities.enums.AddressType;
import com.project.marketplace.services.AddressService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AddressResource.class)
@AutoConfigureMockMvc(addFilters = false)
class AddressResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService service;

    private Address address1;
    private Address address2;
    
    @BeforeEach
    void setUp() {
        address1 = new Address(1L, "Street 1", 123, "Neighborhood 1", 12345, "Country 1", "City 1",
                        AddressType.HOME_ADDRESS, null);
        address2 = new Address(2L, "Street 2", 456, "Neighborhood 2", 67890, "Country 2", "City 2",
                        AddressType.BUSINES_ADDRESS, null);
    }

    @WithMockUser(authorities = {"Admin"})
    @Test
    void testFindAll() throws Exception {
        List<Address> list = Arrays.asList(address1, address2);
        when(service.findAll()).thenReturn(list);

        mockMvc.perform(get("/addresses"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1L))
               .andExpect(jsonPath("$[1].id").value(2L));
    }

    @WithMockUser(authorities = {"Admin"})
    @Test
    void testFindById() throws Exception {
        when(service.findById(1L)).thenReturn(address1);

        mockMvc.perform(get("/addresses/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L));
    }

    @WithMockUser(authorities = {"Admin"})
    @Test
    void testInsert() throws Exception {
        when(service.insert(eq(1L), any(Address.class))).thenReturn(address1);

        mockMvc.perform(post("/addresses?customerId=1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(new ObjectMapper().writeValueAsString(address1)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L));
    }

    @WithMockUser(authorities = {"Admin"})
    @Test
    void testDelete() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/addresses/1"))
               .andExpect(status().isNoContent());
    }

    @WithMockUser(authorities = {"Admin"})
    @Test
    void testUpdate() throws Exception {
        when(service.update(eq(1L), eq(1L), any(Address.class))).thenReturn(address1);

        mockMvc.perform(patch("/addresses/1?customerId=1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(new ObjectMapper().writeValueAsString(address1)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L));
    }
}
