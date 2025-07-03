package com.project.marketplace.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.project.marketplace.entities.Category;
import com.project.marketplace.entities.Product;
import com.project.marketplace.repositories.CategoryRepository;
import com.project.marketplace.repositories.ProductRepository;
import com.project.marketplace.services.exceptions.DatabaseException;
import com.project.marketplace.services.exceptions.ObjectAlreadyExistsException;
import com.project.marketplace.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

class ProductServiceTest {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        category = new Category(1L, "Electronics");
        product = new Product(1L, "Product 1", "Description", 10.0, true,  false, null, null);
    }

    @Test
    void testFindAll_ShouldReturnPageOfProducts() {
        // Arrange: Mock do repositório
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(new Product(), new Product()));
        when(productRepository.findAll(pageable)).thenReturn(page);

        // Act: Chamada ao método
        Page<Product> result = service.findAll(pageable);

        // Assert: Verificação do resultado
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
    }
    
    @Test
    void testFindAllActive_ShouldReturnPageOfActiveProducts() {
        // Arrange: Mock do repositório
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(new Product(), new Product()));
        when(productRepository.findAllActiveProducts(pageable)).thenReturn(page);

        // Act: Chamada ao método
        Page<Product> result = service.findAllActive(pageable);

        // Assert: Verificação do resultado
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
    }
    
    @Test
    void testFindFilteredActiveProducts_ShouldReturnPageOfFilteredActiveProducts() {
        // Arrange: Mock do repositório
        String searchTerm = "product";
        List<Long> categoryIds = List.of(1L, 2L);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(new Product(), new Product()));
        when(productRepository.findFilteredActiveProducts(searchTerm, categoryIds, pageable)).thenReturn(page);

        // Act: Chamada ao método
        Page<Product> result = service.findFilteredActiveProducts(searchTerm, categoryIds, pageable);

        // Assert: Verificação do resultado
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
    }
    
    @Test
    void testFindFilteredProducts_ShouldReturnPageOfFilteredProducts() {
        // Arrange: Mock do repositório
        String searchTerm = "product";
        List<Long> categoryIds = List.of(1L, 2L);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(new Product(), new Product()));
        when(productRepository.findFilteredProducts(searchTerm, categoryIds, pageable)).thenReturn(page);

        // Act: Chamada ao método
        Page<Product> result = service.findFilteredProducts(searchTerm, categoryIds, pageable);

        // Assert: Verificação do resultado
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
    }

    @Test
    void testInsert_ShouldSetCategoriesCorrectly() {
        // Arrange: Mock do repositório de categorias
        Category category1 = new Category();
        category1.setId(1L);
        Category category2 = new Category();
        category2.setId(2L);
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category2));
        
        // Mock para o repositório de produtos
        Product newProduct = new Product();
        newProduct.setId(1L);
        newProduct.setName("Test Product");
        newProduct.setCategoriesId(Set.of(1L, 2L));  // Simulando os IDs das categorias
        newProduct.setCategories(new HashSet<>());  // Inicializando a lista de categorias (será modificada)

        // Mock do comportamento do repositório de produtos
        when(productRepository.findByName(anyString())).thenReturn(List.of());  // Nenhum produto com o mesmo nome
        when(productRepository.save(any(Product.class))).thenReturn(newProduct);

        // Act: Chamada ao método
        Product result = service.insert(newProduct);

        // Assert: Verificar se as categorias foram corretamente atribuídas ao produto
        assertNotNull(result);
        assertEquals(2, result.getCategories().size());  // Espera-se que duas categorias tenham sido atribuídas
        assertTrue(result.getCategories().contains(category1));
        assertTrue(result.getCategories().contains(category2));
    }
    
    @Test
    void testFindById_whenFound_shouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = service.findById(1L);

        assertNotNull(result);
        assertEquals(product.getName(), result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_whenNotFound_shouldThrowResourceNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));

        assertEquals("Product Doesn't Exist. Id: 1", exception.getMessage());
    }

    @Test
    void testInsert_whenNewProduct_shouldSaveProduct() {
        when(productRepository.findByName(product.getName())).thenReturn(List.of());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = service.insert(product);

        assertNotNull(result);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testInsert_whenProductAlreadyExists_shouldThrowObjectAlreadyExistsException() {
        Product existingProduct = new Product(1L, "Product 1", "Description", 10.0, true,  false, null, null);
        when(productRepository.findByName(product.getName())).thenReturn(List.of(existingProduct));

        ObjectAlreadyExistsException exception = assertThrows(ObjectAlreadyExistsException.class, () -> service.insert(product));

        assertEquals("Product 1 already exists.", exception.getMessage());
    }
    
    @Test
    void testInsert_shouldAddCategoriesFromProductCategories() {
        // Criando produto com categorias
        Product product = new Product(1L, "Product Name", "Description", 10.0, 20, true, false, null, null);

        // Criando categorias
        Category category1 = new Category(1L, "Category 1");
        Category category2 = new Category(2L, "Category 2");

        // Adicionando categorias ao produto
        product.setCategories(Set.of(category1, category2));

        // Mocking do repositório de categorias
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category2));

        when(productRepository.save(product)).thenReturn(product);
        // Chamando o método insert
        Product savedProduct = service.insert(product);

        // Verificando se as categorias foram corretamente associadas ao produto
        assertTrue(savedProduct.getCategories().contains(category1));
        assertTrue(savedProduct.getCategories().contains(category2));

        // Verificando o tamanho do Set de categorias para garantir que as categorias foram adicionadas corretamente
        assertEquals(2, savedProduct.getCategories().size());
    }


    @Test
    void testDelete_whenProductExists_shouldSetIsDeletedTrue() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        service.delete(1L);

        assertTrue(product.getIsDeleted());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testDelete_whenProductNotFound_shouldThrowResourceNotFoundException() {
        when(productRepository.existsById(1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));

        assertEquals("Resource Not Found. Id: 1", exception.getMessage());
    }

    @Test
    void testDelete_whenDatabaseException_shouldThrowDatabaseException() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenThrow(new DataIntegrityViolationException("Database error"));

        DatabaseException exception = assertThrows(DatabaseException.class, () -> service.delete(1L));

        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void testUpdate_whenProductExists_shouldUpdateProduct() {
        Product updatedProduct = new Product(1L, "UpdateName 1", "Description", 10.0, true,  false, null, null);
        when(productRepository.getReferenceById(1L)).thenReturn(product);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = service.update(1L, updatedProduct);

        assertEquals("UpdateName 1", result.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testUpdate_whenProductNotFound_shouldThrowResourceNotFoundException() {
        when(productRepository.getReferenceById(1L)).thenThrow(new EntityNotFoundException());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.update(1L, product));

        assertEquals("Resource Not Found. Id: 1", exception.getMessage());
    }

    @Test
    void testCompareProducts_whenTwoProductsExistAndFirstMatches_shouldThrowException() {
        Product existingProduct1 = new Product(1L, "Product 1", "Description", 10.0, true,  false, null, null);
        Product existingProduct2 = new Product(2L, "Product 1", "Description", 10.0, true,  false, null, null);
        Product newProduct = new Product(3L, "Product 1", "Description", 10.0, true,  false, null, null);

        List<Product> productsInDatabase = Arrays.asList(existingProduct1, existingProduct2);

        ObjectAlreadyExistsException exception = assertThrows(ObjectAlreadyExistsException.class, () -> service.compareProducts(productsInDatabase, newProduct)
        );

        assertEquals("Product 1 already exists.", exception.getMessage());
    }

    @Test
    void testCompareProducts_whenTwoProductsExistAndSecondMatches_shouldThrowException() {
    	Product existingProduct1 = new Product(1L, "Product 1", "Description", 10.0, false,  true, null, null);
        Product existingProduct2 = new Product(2L, "Product 1", "Description", 10.0, true,  false, null, null);
        Product newProduct = new Product(3L, "Product 1", "Description", 10.0, true,  false, null, null);

        List<Product> productsInDatabase = Arrays.asList(existingProduct1, existingProduct2);

        ObjectAlreadyExistsException exception = assertThrows(ObjectAlreadyExistsException.class, () -> service.compareProducts(productsInDatabase, newProduct)
        );

        assertEquals("Product 1 already exists.", exception.getMessage());
    }

    @Test
    void testCompareProducts_whenOneProductExistsAndMatches_shouldThrowException() {
    	Product existingProduct = new Product(1L, "Product 1", "Description", 10.0, true,  false, null, null);
        Product newProduct = new Product(3L, "Product 1", "Description", 10.0, true,  false, null, null);

        List<Product> productsInDatabase = List.of(existingProduct);

        ObjectAlreadyExistsException exception = assertThrows(ObjectAlreadyExistsException.class, () -> service.compareProducts(productsInDatabase, newProduct)
        );

        assertEquals("Product 1 already exists.", exception.getMessage());
    }

    @Test
    void testCompareProducts_whenTwoProductsExistAndNeitherMatches_shouldReturnFalse() {
    	Product existingProduct1 = new Product(1L, "Product 1", "Description", 10.0, true,  true, null, null);
        Product existingProduct2 = new Product(2L, "Product 1", "Description", 10.0, false,  true, null, null);
        Product newProduct = new Product(3L, "Product 1", "Description", 10.0, true,  false, null, null);

        existingProduct2.setIsDeleted(true);
        List<Product> productsInDatabase = Arrays.asList(existingProduct1, existingProduct2);

        Boolean result = service.compareProducts(productsInDatabase, newProduct);

        assertFalse(result);
    }

    @Test
    void testCompareProducts_whenOneProductExistsAndDoesNotMatch_shouldReturnFalse() {
    	Product existingProduct = new Product(1L, "Product 1", "Description", 10.0, true,  true, null, null);
        Product newProduct = new Product(3L, "Product 1", "Description", 10.0, false,  false, null, null);
        
        List<Product> productsInDatabase = List.of(existingProduct);

        Boolean result = service.compareProducts(productsInDatabase, newProduct);

        assertFalse(result);
    }

    @Test
    void testCompareProducts_whenNoProductsExist_shouldReturnFalse() {
        Product newProduct = new Product(3L, "Product 1", "Description", 10.0, true,  false, null, null);

        List<Product> productsInDatabase = List.of();

        Boolean result = service.compareProducts(productsInDatabase, newProduct);

        assertFalse(result);
    }

    @Test
    void testUpdateData_whenAllFieldsProvided_shouldUpdateEntity() {
        Product existingProduct = new Product(1L, "Old Name", "Description", 20.0, 10, true, false, null, null);
        Product updatedProduct = new Product(3L, "New Name", "New Description", 30.0, 20, true, true, null, null);

        Category category1 = new Category(1L, "Category 1");
        Category category2 = new Category(2L, "Category 2");

        updatedProduct.setCategoriesId(Set.of(1L, 2L));  // Atualizando as categorias
        updatedProduct.setIsDeleted(true);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category2));

        service.updateData(existingProduct, updatedProduct);

        assertEquals("New Name", existingProduct.getName());
        assertEquals(30.0, existingProduct.getUnitPrice());
        assertEquals("New Description", existingProduct.getDescription());
        assertTrue(existingProduct.getSellIndicator());
        assertTrue(existingProduct.getIsDeleted());
        assertTrue(existingProduct.getCategories().contains(category1));
        assertTrue(existingProduct.getCategories().contains(category2));
    }

    @Test
    void testUpdateData_whenSomeFieldsNull_shouldPreserveOriginalValues() {
    	Product existingProduct = new Product(1L, "Old Name", "Description", 20.0, 10, true, false, null, null);
        Product updatedProduct = new Product(null, null, null, null, null, null, null, null);  // Nenhum campo atualizado

        service.updateData(existingProduct, updatedProduct);

        assertEquals("Old Name", existingProduct.getName());
        assertEquals(20.0, existingProduct.getUnitPrice());
        assertEquals("Description", existingProduct.getDescription());
        assertTrue(existingProduct.getSellIndicator());
        assertFalse(existingProduct.getIsDeleted());
        assertTrue(existingProduct.getCategories().isEmpty());
    }

    @Test
    void testUpdateData_whenCategoriesIdProvided_shouldUpdateCategories() {
        Product existingProduct = new Product(1L, "Old Name", "Description", 20.0, 10, true, false, null, null);
        Product updatedProduct = new Product(null, null, null, null, null, null, null, null);

        Category category1 = new Category(1L, "Category 1");

        updatedProduct.setCategoriesId(Set.of(1L));  // Atualizando as categorias
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));

        service.updateData(existingProduct, updatedProduct);

        assertTrue(existingProduct.getCategories().contains(category1));
    }

    @Test
    void testUpdateData_whenCategoriesIdEmpty_shouldNotChangeCategories() {
        Set<Category> originalCategories = new HashSet<>();
        originalCategories.add(new Category(1L, "Category 1"));

        Product existingProduct = new Product(1L, "Old Name", "Old Description", 20.0, 10, true, false, null, null);
        Product updatedProduct = new Product(null, null, null, null, null, null, null, null);

        existingProduct.setCategories(originalCategories);
        service.updateData(existingProduct, updatedProduct);

        assertEquals(originalCategories, existingProduct.getCategories()); // As categorias devem permanecer as mesmas
    }

    @Test
    void testUpdateData_whenCategoryIdNotFound_shouldThrowException() {
        Product existingProduct = new Product(1L, "Old Name", "Old Description", 20.0, 10, true, false, null, null);
        Product updatedProduct = new Product(null, null, null, null, null, null, null, null);

        updatedProduct.setCategoriesId(Set.of(99L));  // ID de categoria não encontrado

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateData(existingProduct, updatedProduct));
    }
}
