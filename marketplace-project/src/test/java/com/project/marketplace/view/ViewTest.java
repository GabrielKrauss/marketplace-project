package com.project.marketplace.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ViewTest {

    @Test
    void testCategoriesHierarchy() {
        // Verifica se CategoriesById é uma subclasse de Categories
        assertTrue(View.CategoriesById.class.getSuperclass() == View.Categories.class);
    }

    @Test
    void testProductsHierarchy() {
        // Verifica se ProductsById é uma subclasse de Products
        assertTrue(View.ProductsById.class.getSuperclass() == View.Products.class);
    }

    @Test
    void testOrdersHierarchy() {
        // Verifica se OrdersById é uma subclasse de Orders
        assertTrue(View.OrdersById.class.getSuperclass() == View.Orders.class);
    }

    @Test
    void testCustomersHierarchy() {
        // Verifica se CustomersById é uma subclasse de Customers
        assertTrue(View.CustomersById.class.getSuperclass() == View.Customers.class);
    }

    @Test
    void testAddressHierarchy() {
        // Verifica se AddressById é uma subclasse de Address
        assertTrue(View.AddressById.class.getSuperclass() == View.Address.class);
    }

    @Test
    void testCouponsHierarchy() {
        // Verifica se CouponsById é uma subclasse de Coupons
        assertTrue(View.CouponsById.class.getSuperclass() == View.Coupons.class);
    }

    @Test
    void testInstantiation() {
        // Verifica se as classes podem ser instanciadas
        View.Categories categories = new View.Categories();
        View.CategoriesById categoriesById = new View.CategoriesById();
        View.Products products = new View.Products();
        View.ProductsById productsById = new View.ProductsById();
        View.Orders orders = new View.Orders();
        View.OrdersById ordersById = new View.OrdersById();
        View.Customers customers = new View.Customers();
        View.CustomersById customersById = new View.CustomersById();
        View.Address address = new View.Address();
        View.AddressById addressById = new View.AddressById();
        View.Coupons coupons = new View.Coupons();
        View.CouponsById couponsById = new View.CouponsById();

        // Assert que as instâncias não são nulas
        assertTrue(categories != null);
        assertTrue(categoriesById != null);
        assertTrue(products != null);
        assertTrue(productsById != null);
        assertTrue(orders != null);
        assertTrue(ordersById != null);
        assertTrue(customers != null);
        assertTrue(customersById != null);
        assertTrue(address != null);
        assertTrue(addressById != null);
        assertTrue(coupons != null);
        assertTrue(couponsById != null);
    }
}
