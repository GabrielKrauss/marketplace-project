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
import com.project.marketplace.entities.Category;
import com.project.marketplace.services.CategoryService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CategoryResource.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService service;

    private Category category1;
    private Category category2;
    
    @BeforeEach
    void setUp() {
        category1 = new Category(1L, "Electronics");
        category2 = new Category(2L, "Books");
    }

    @Test
    void testFindAll() throws Exception {
        List<Category> list = Arrays.asList(category1, category2);
        when(service.findAll()).thenReturn(list);

        mockMvc.perform(get("/categories"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1L))
               .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void testFindById() throws Exception {
        when(service.findById(1L)).thenReturn(category1);

        mockMvc.perform(get("/categories/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L));
    }

    
    @Test
    @WithMockUser(authorities = "Admin")
    void testInsert() throws Exception {
        when(service.insert(any(Category.class))).thenReturn(category1);

        mockMvc.perform(post("/categories")
               .contentType(MediaType.APPLICATION_JSON)
               .content(new ObjectMapper().writeValueAsString(category1)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(authorities = "Admin")
    void testDelete() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/categories/1"))
               .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "Admin")
    void testUpdate() throws Exception {
        when(service.update(eq(1L), any(Category.class))).thenReturn(category1);

        mockMvc.perform(patch("/categories/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(new ObjectMapper().writeValueAsString(category1)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L));
    }
}
