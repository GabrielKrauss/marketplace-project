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
import com.project.marketplace.entities.Coupon;
import com.project.marketplace.services.CouponService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CouponResource.class)
@AutoConfigureMockMvc(addFilters = false)
class CouponResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService service;

    private Coupon coupon1;
    private Coupon coupon2;

    @BeforeEach
    void setUp() {
        coupon1 = new Coupon(1L, "CODE10", 10.0, 0.0, true);   // desconto fixo
        coupon2 = new Coupon(2L, "CODE20", 0.0, 20.0, false);  // desconto percentual
    }

    @WithMockUser(authorities = "Admin")
    @Test
    void testFindAll() throws Exception {
        List<Coupon> list = Arrays.asList(coupon1, coupon2);
        when(service.findAll()).thenReturn(list);

        mockMvc.perform(get("/coupons"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1L))
               .andExpect(jsonPath("$[1].id").value(2L))
               .andExpect(jsonPath("$[0].code").value("CODE10"))
               .andExpect(jsonPath("$[1].code").value("CODE20"));
    }

    @WithMockUser(authorities = "Admin")
    @Test
    void testFindById() throws Exception {
        when(service.findById(1L)).thenReturn(coupon1);

        mockMvc.perform(get("/coupons/id/{id}", 1L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.code").value("CODE10"))
               .andExpect(jsonPath("$.discountValue").value(10.0))
               .andExpect(jsonPath("$.discountPercentage").value(0.0));
    }

    @Test
    void testFindByCode() throws Exception {
        when(service.findByCode("CODE10")).thenReturn(coupon1);

        mockMvc.perform(get("/coupons/code/{code}", "CODE10"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.code").value("CODE10"))
               .andExpect(jsonPath("$.discountValue").value(10.0))
               .andExpect(jsonPath("$.discountPercentage").value(0.0));
    }

    @WithMockUser(authorities = "Admin")
    @Test
    void testFindAllActive() throws Exception {
        List<Coupon> list = Arrays.asList(coupon1);
        when(service.findAllActive()).thenReturn(list);

        mockMvc.perform(get("/coupons/activeCoupons"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1L))
               .andExpect(jsonPath("$[0].code").value("CODE10"))
               .andExpect(jsonPath("$[0].discountValue").value(10.0))
               .andExpect(jsonPath("$[0].discountPercentage").value(0.0));
    }

    @Test
    @WithMockUser(authorities = "Admin")
    void testInsert() throws Exception {
        when(service.insert(any(Coupon.class))).thenReturn(coupon1);

        mockMvc.perform(post("/coupons")
               .contentType(MediaType.APPLICATION_JSON)
               .content(new ObjectMapper().writeValueAsString(coupon1)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.code").value("CODE10"))
               .andExpect(jsonPath("$.discountValue").value(10.0))
               .andExpect(jsonPath("$.discountPercentage").value(0.0));
    }

    @Test
    @WithMockUser(authorities = "Admin")
    void testDelete() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/coupons/{id}", 1L))
               .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "Admin")
    void testUpdate() throws Exception {
        when(service.update(eq(1L), any(Coupon.class))).thenReturn(coupon1);

        mockMvc.perform(patch("/coupons/{id}", 1L)
               .contentType(MediaType.APPLICATION_JSON)
               .content(new ObjectMapper().writeValueAsString(coupon1)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.code").value("CODE10"))
               .andExpect(jsonPath("$.discountValue").value(10.0))
               .andExpect(jsonPath("$.discountPercentage").value(0.0));
    }
}
