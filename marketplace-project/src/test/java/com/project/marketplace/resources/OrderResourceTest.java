package com.project.marketplace.resources;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.marketplace.entities.Coupon;
import com.project.marketplace.entities.Customer;
import com.project.marketplace.entities.Order;
import com.project.marketplace.entities.enums.OrderStatus;
import com.project.marketplace.services.OrderService;

public class OrderResourceTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderResource orderResource;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        // Inicializa os mocks
        MockitoAnnotations.openMocks(this);

        // Configura o MockMvc para testar o OrderResource com injeção de dependências correta
        mockMvc = MockMvcBuilders.standaloneSetup(orderResource).build();

        // Configura o ObjectMapper para suportar Java 8 Date/Time API
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private Order createSampleOrder() {
        Customer customer = new Customer(1L, "John Doe", "123456789", "123-456-789", "A", null, null, null);
        Coupon coupon = new Coupon(1L, "CODE10", 10.0, 0.0, true);
        return new Order(1L, OrderStatus.PAID, customer, null, coupon);
    }

    @Test
    public void testFindAllActives() throws Exception {
        Order order = createSampleOrder();
        when(orderService.findAllActives()).thenReturn(List.of(order));

        mockMvc.perform(get("/orders/activeOrders")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].orderStatus").value("PAID"));
    }

    @Test
    public void testFindAll() throws Exception {
        Order order = createSampleOrder();
        when(orderService.findAll()).thenReturn(List.of(order));

        mockMvc.perform(get("/orders")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].orderStatus").value("PAID"));
    }

    @Test
    public void testFindById() throws Exception {
        Order order = createSampleOrder();
        when(orderService.findById(1L)).thenReturn(order);

        mockMvc.perform(get("/orders/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderStatus").value("PAID"));
    }

    @Test
    public void testInsert() throws Exception {
        Order order = createSampleOrder();
        when(orderService.insert(order)).thenReturn(order);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderStatus").value("PAID"));
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/orders/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdate() throws Exception {
        Order order = createSampleOrder();
        when(orderService.update(1L, order)).thenReturn(order);

        mockMvc.perform(patch("/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderStatus").value("PAID"));
    }
}
