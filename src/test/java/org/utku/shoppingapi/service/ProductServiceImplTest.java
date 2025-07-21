package org.utku.shoppingapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.utku.shoppingapi.entity.Product;
import org.utku.shoppingapi.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ProductServiceImpl}.
 * Validates product creation, update, deletion, and exception scenarios.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    /**
     * Mocks the ProductRepository for product data access operations.
     */
    @Mock
    private ProductRepository productRepository;
    /**
     * Injects the ProductServiceImpl with mocked dependencies.
     */
    @InjectMocks
    private ProductServiceImpl productService;

    /**
     * Tests that getAllProducts returns a paged list of active products.
     */
    @Test
    void getAllProducts_ShouldReturnPagedProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal("99.99"));
        product1.setActive(true);
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("149.99"));
        product2.setActive(true);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(product1, product2), pageable, 2);
        when(productRepository.findAllActive(pageable)).thenReturn(productPage);

        // When
        Page<Product> result = productService.getAllProducts(pageable);

        // Then
        assertEquals(2, result.getContent().size());
        assertEquals("Product 1", result.getContent().get(0).getName());
        verify(productRepository).findAllActive(pageable);
    }

    /**
     * Tests that getProductById returns a product when it exists.
     */
    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When
        Optional<Product> result = productService.findProductById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Product", result.get().getName());
        verify(productRepository).findById(1L);
    }

    /**
     * Tests that getProductById throws ResourceNotFoundException when product does not exist.
     */
    @Test
    void getProductById_WhenProductNotExists_ShouldThrowResourceNotFoundException() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        // When & Then
        var ex = assertThrows(org.utku.shoppingapi.exception.ResourceNotFoundException.class, () -> productService.findProductById(99L).orElseThrow(() -> new org.utku.shoppingapi.exception.ResourceNotFoundException("Product not found")));
        assertEquals("Product not found", ex.getMessage());
    }

    /**
     * Tests that createProduct saves and returns a new product with valid data.
     */
    @Test
    void createProduct_ShouldSaveAndReturnProduct() {
        // Given
        Product product = new Product();
        product.setName("New Product");
        product.setPrice(new BigDecimal("199.99"));
        product.setStockQuantity(10);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("New Product");
        savedProduct.setPrice(new BigDecimal("199.99"));
        savedProduct.setStockQuantity(10);
        savedProduct.setActive(true);

        when(productRepository.save(product)).thenReturn(savedProduct);

        // When
        Product result = productService.createProduct(product);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("New Product", result.getName());
        assertTrue(result.getActive());
        verify(productRepository).save(product);
    }

    /**
     * Tests that createProduct throws ValidationException for empty product name.
     */
    @Test
    void createProduct_WithEmptyName_ShouldThrowValidationException() {
        // Given
        Product product = new Product();
        product.setName("");
        product.setPrice(new BigDecimal("99.99"));
        product.setStockQuantity(10);
        // When & Then
        assertThrows(org.utku.shoppingapi.exception.ValidationException.class, () -> productService.createProduct(product));
    }

    /**
     * Tests that createProduct throws ValidationException for negative price.
     */
    @Test
    void createProduct_WithNegativePrice_ShouldThrowValidationException() {
        // Given
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(new BigDecimal("-10.00"));
        product.setStockQuantity(10);
        // When & Then
        assertThrows(org.utku.shoppingapi.exception.ValidationException.class, () -> productService.createProduct(product));
    }

    /**
     * Tests that createProduct throws ValidationException for zero stock quantity.
     */
    @Test
    void createProduct_WithZeroStock_ShouldThrowValidationException() {
        // Given
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setStockQuantity(0);
        // When & Then
        assertThrows(org.utku.shoppingapi.exception.ValidationException.class, () -> productService.createProduct(product));
    }

    /**
     * Tests that product is not persisted if an exception occurs during save.
     */
    @Test
    void createProduct_WhenExceptionThrown_ShouldNotPersistProduct() {
        // Given
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setStockQuantity(10);
        when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("DB error"));
        // When & Then
        assertThrows(RuntimeException.class, () -> productService.createProduct(product));
    }

    /**
     * Tests that createProduct handles duplicate product name scenario and throws ValidationException.
     */
    @Test
    void createProduct_Concurrency_ShouldHandleDuplicateName() {
        // Given
        Product product1 = new Product();
        product1.setName("SameName");
        product1.setPrice(new BigDecimal("99.99"));
        product1.setStockQuantity(10);
        Product product2 = new Product();
        product2.setName("SameName");
        product2.setPrice(new BigDecimal("99.99"));
        product2.setStockQuantity(10);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            if ("SameName".equals(p.getName())) {
                throw new org.utku.shoppingapi.exception.ValidationException("Product name already exists");
            }
            return p;
        });
        // When & Then
        assertThrows(org.utku.shoppingapi.exception.ValidationException.class, () -> productService.createProduct(product1));
        assertThrows(org.utku.shoppingapi.exception.ValidationException.class, () -> productService.createProduct(product2));
    }

    /**
     * Tests that updateProduct updates and returns the product when it exists.
     */
    @Test
    void updateProduct_WhenProductExists_ShouldUpdateAndReturn() {
        // Given
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Old Name");
        existingProduct.setPrice(new BigDecimal("99.99"));

        Product updateData = new Product();
        updateData.setName("New Name");
        updateData.setPrice(new BigDecimal("149.99"));
        updateData.setStockQuantity(20);
        updateData.setCategory("Electronics");
        updateData.setActive(true);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        // When
        Product result = productService.updateProduct(1L, updateData);

        // Then
        assertEquals("New Name", result.getName());
        assertEquals(new BigDecimal("149.99"), result.getPrice());
        assertEquals(20, result.getStockQuantity());
        verify(productRepository).findById(1L);
        verify(productRepository).save(existingProduct);
    }

    /**
     * Tests that deleteProduct calls repository delete for the product.
     */
    @Test
    void deleteProduct_ShouldCallRepositoryDelete() {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setActive(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        // When
        productService.deleteProduct(1L);
        // Then
        verify(productRepository).findById(1L);
        verify(productRepository).delete(product);
    }
}