package org.utku.shoppingapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.utku.shoppingapi.constants.AppConstants;
import org.utku.shoppingapi.dto.ProductDto;
import org.utku.shoppingapi.dto.request.CreateProductRequest;
import org.utku.shoppingapi.dto.request.UpdateProductRequest;
import org.utku.shoppingapi.entity.Product;
import org.utku.shoppingapi.mapper.EntityMapper;
import org.utku.shoppingapi.service.ProductService;

/**
 * REST Controller for managing product catalog operations in the Shopping API.
 * 
 * <p>This controller provides comprehensive product management functionality for
 * an e-commerce platform, including:
 * <ul>
 *   <li>Product catalog browsing with pagination and sorting</li>
 *   <li>Individual product information retrieval</li>
 *   <li>New product creation and catalog management</li>
 *   <li>Product information updates with partial update support</li>
 *   <li>Product removal with soft delete functionality</li>
 * </ul>
 * 
 * <p>All endpoints follow RESTful conventions and are prefixed with '/api/products'.
 * The controller implements proper HTTP status codes and error handling,
 * uses DTOs for data transfer, and includes comprehensive input validation.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Pagination support for large product catalogs</li>
 *   <li>Soft delete implementation to preserve order history</li>
 *   <li>Inventory management integration</li>
 *   <li>Product availability tracking</li>
 * </ul>
 * 
 * @author Shopping API Development Team
 * @version 1.0
 * @since 1.0
 * @see ProductService
 * @see ProductDto
 * @see CreateProductRequest
 * @see UpdateProductRequest
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "2. Product Management", description = "API for product management operations")
public class ProductController {

    private final ProductService productService;
    private final EntityMapper mapper;

    /**
     * Constructs a new ProductController with required dependencies.
     * 
     * <p>This constructor is used by Spring's dependency injection container
     * to inject the required service and mapper dependencies for product
     * management operations.
     * 
     * @param productService The service layer component that handles product
     *                      business logic, validation, and data persistence
     * @param mapper The mapper component responsible for converting between
     *              Product entities and DTOs
     * @throws IllegalArgumentException if any parameter is null
     */
    public ProductController(ProductService productService, EntityMapper mapper) {
        this.productService = productService;
        this.mapper = mapper;
    }

    /**
     * Retrieves all products from the catalog with pagination and sorting support.
     *
     * @param pageable Pagination and sorting parameters
     * @return Page containing ProductDto objects
     */
    @GetMapping
    @Operation(summary = "1. List all products", description = "Retrieve paginated list of all products")
    public Page<ProductDto> getAllProducts(
            @Parameter(hidden = true) @PageableDefault(
                size = AppConstants.DEFAULT_PAGE_SIZE, 
                sort = AppConstants.DEFAULT_SORT_FIELD
            ) Pageable pageable) {
        return productService.getAllProducts(pageable).map(mapper::toDto);
    }

    /**
     * Retrieves detailed information about a specific product by its ID.
     *
     * @param id Unique identifier of the product
     * @return ProductDto object representing the product
     */
    @GetMapping("/{id}")
    @Operation(summary = "3. Get product by ID", description = "Retrieve a specific product by its unique identifier")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return productService.findProductById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new product in the catalog. Only accessible by ADMIN users.
     *
     * @param request CreateProductRequest containing product information
     * @return ProductDto object representing the created product
     */
    @PostMapping
    @Operation(summary = "2. Create new product", description = "Create a new product in the catalog")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = mapper.toEntity(request);
        return ResponseEntity.ok(mapper.toDto(productService.createProduct(product)));
    }

    /**
     * Updates an existing product's information. Only accessible by ADMIN users.
     *
     * @param id Unique identifier of the product
     * @param request UpdateProductRequest containing updated information
     * @return ProductDto object representing the updated product
     */
    @PutMapping("/{id}")
    @Operation(summary = "4. Update product", description = "Update an existing product's information")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        Product existingProduct = productService.findProductById(id).orElseThrow();
        mapper.updateEntityFromRequest(existingProduct, request);
        return ResponseEntity.ok(mapper.toDto(productService.updateProduct(id, existingProduct)));
    }

    /**
     * Deletes a product from the catalog (soft delete). Only accessible by ADMIN users.
     *
     * @param id Unique identifier of the product
     * @return Void response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "5. Delete product", description = "Permanently delete a product from the catalog")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}