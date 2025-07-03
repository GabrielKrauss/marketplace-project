package com.project.marketplace.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import com.project.marketplace.entities.Category;
import com.project.marketplace.repositories.CategoryRepository;
import com.project.marketplace.services.exceptions.DatabaseException;
import com.project.marketplace.services.exceptions.ObjectAlreadyExistsException;
import com.project.marketplace.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

class CategoryServiceTest {

    @InjectMocks
    private CategoryService service;

    @Mock
    private CategoryRepository repository;

    private Category category;
    private Category existingCategory;
    private Category updatedCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        category = new Category(1L, "Electronics");
        existingCategory = new Category(1L, "Electronics");
    }

    @Test
    void testFindAllSuccess() {
        Category cat1 = new Category(1L, "Electronics");
        Category cat2 = new Category(2L, "Books");
        List<Category> categories = List.of(cat1, cat2);
        when(repository.findAll()).thenReturn(categories);

        List<Category> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).getName());
        assertEquals("Books", result.get(1).getName());
    }

    @Test
    void testFindByIdSuccess() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(category));

        Category result = service.findById(id);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
    }

    @Test
    void testFindById_whenNotFound_shouldThrowResourceNotFoundException() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }

    @Test
    void testInsert_whenCategoryExists_shouldThrowObjectAlreadyExistsException() {
        when(repository.existsByName(category.getName())).thenReturn(true);

        assertThrows(ObjectAlreadyExistsException.class, () -> service.insert(category));
    }

    @Test
    void testInsertSuccess() {
        when(repository.existsByName(category.getName())).thenReturn(false);
        when(repository.save(category)).thenReturn(category);

        Category result = service.insert(category);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
    }

    @Test
    void testDelete_whenNotFound_shouldThrowResourceNotFoundException() {
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(id));
    }

    @Test
    void testDelete_whenDatabaseException_shouldThrowDatabaseException() {
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(id);

        assertThrows(DatabaseException.class, () -> service.delete(id));
    }

    @Test
    void testDeleteSuccess() {
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void testUpdate_whenCategoryNotFound_shouldThrowResourceNotFoundException() {
        Long id = 1L;
        Category updatedCategory = new Category(1L, "Home Appliances");

        when(repository.getReferenceById(id)).thenThrow(EntityNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.update(id, updatedCategory));
    }

    @Test
    void testUpdate_whenCategoryExists_shouldThrowObjectAlreadyExistsException() {
        Long id = 1L;
        Category updatedCategory = new Category(1L, "Electronics");
        when(repository.getReferenceById(id)).thenReturn(category);
        when(repository.existsByName(updatedCategory.getName())).thenReturn(true);

        assertThrows(ObjectAlreadyExistsException.class, () -> service.update(id, updatedCategory));
    }


    @Test
    void testUpdateSuccess() {
        Long id = 1L;
        Category updatedCategory = new Category(1L, "Home Appliances");

        when(repository.getReferenceById(id)).thenReturn(category);
        when(repository.existsByName(updatedCategory.getName())).thenReturn(false);
        when(repository.save(category)).thenReturn(updatedCategory);

        Category result = service.update(id, updatedCategory);

        assertNotNull(result);
        assertEquals("Home Appliances", result.getName());
    }
}
