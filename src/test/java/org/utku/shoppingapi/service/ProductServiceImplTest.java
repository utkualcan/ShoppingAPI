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

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void getAllProducts_ShouldReturnPagedProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal("99.99"));
        
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("149.99"));
        
        Page<Product> productPage = new PageImpl<>(Arrays.asList(product1, product2), pageable, 2);
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // When
        Page<Product> result = productService.getAllProducts(pageable);

        // Then
        assertEquals(2, result.getContent().size());
        assertEquals("Product 1", result.getContent().get(0).getName());
        verify(productRepository).findAll(pageable);
    }

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

    @Test
    void deleteProduct_ShouldCallRepositoryDelete() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);
        
        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }
}