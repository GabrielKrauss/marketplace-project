package com.project.marketplace.resources;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.marketplace.entities.Product;
import com.project.marketplace.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;


@WebMvcTest(ProductResource.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;
    private Page<Product> productPage;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Product", "Description", 100.0, 10, true, true, null, null);
        productPage = new PageImpl<>(List.of(product));

        when(productService.findAll(any(Pageable.class))).thenReturn(productPage);
        when(productService.findAllActive(any(Pageable.class))).thenReturn(productPage);
        when(productService.findById(1L)).thenReturn(product);
        when(productService.insert(any(Product.class))).thenReturn(product);
        when(productService.update(eq(1L), any(Product.class))).thenReturn(product);
        when(productService.findFilteredProducts(anyString(), any(), any(Pageable.class))).thenReturn(productPage);
        when(productService.findFilteredActiveProducts(anyString(), any(), any(Pageable.class))).thenReturn(productPage);
        doNothing().when(productService).delete(1L);
    }

    @Test
    @WithMockUser(authorities = "Admin")
    void testFindAll_WithoutFilters() throws Exception {
        mockMvc.perform(get("/products")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "Admin")
    void testFindAll_WithFilters() throws Exception {
        mockMvc.perform(get("/products")
               .param("searchTerm", "example")
               .param("categoryIds", "1,2,3")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }

    @Test
    void testFindAllActives_WithoutFilters() throws Exception {
        mockMvc.perform(get("/products/activeProducts")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }

    @Test
    void testFindAllActives_WithFilters() throws Exception {
        mockMvc.perform(get("/products/activeProducts")
               .param("searchTerm", "example")
               .param("categoryIds", "1,2,3")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }

    @Test
    void testFindById() throws Exception {
        mockMvc.perform(get("/products/1")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(product.getId()));
    }

    @Test
    @WithMockUser(authorities = "Admin")
    void testInsert() throws Exception {
        mockMvc.perform(post("/products")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(product)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(product.getId()));
    }

    @Test
    @WithMockUser(authorities = "Admin")
    void testUpdate() throws Exception {
        mockMvc.perform(patch("/products/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(product)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(product.getId()));
    }

    @Test
    @WithMockUser(authorities = "Admin")
    void testDelete() throws Exception {
        mockMvc.perform(delete("/products/1"))
               .andExpect(status().isNoContent());
    }
}
