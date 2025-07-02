package org.utku.shoppingapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utku.shoppingapi.constants.AppConstants;
import org.utku.shoppingapi.dto.ProductDto;
import org.utku.shoppingapi.dto.request.CreateProductRequest;
import org.utku.shoppingapi.dto.request.UpdateProductRequest;
import org.utku.shoppingapi.entity.Product;
import org.utku.shoppingapi.mapper.EntityMapper;
import org.utku.shoppingapi.service.ProductService;

/**
 * REST Controller for managing product operations.
 * This controller handles all HTTP requests related to product management including:
 * - Creating new products
 * - Retrieving product information
 * - Updating existing products
 * - Deleting products
 * 
 * All endpoints are prefixed with '/api/products' and support pagination where applicable.
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "2. Product Management", description = "API for product management operations")
public class ProductController {

    private final ProductService productService;
    private final EntityMapper mapper;

    /**
     * Constructor for dependency injection.
     * 
     * @param productService Service layer for product business logic
     * @param mapper Entity to DTO mapper for data transformation
     */
    public ProductController(ProductService productService, EntityMapper mapper) {
        this.productService = productService;
        this.mapper = mapper;
    }

    /**
     * Retrieves all products with pagination support.
     * Returns a paginated list of products sorted by ID in ascending order by default.
     * 
     * @param pageable Pagination parameters (page, size, sort)
     * @return Page of ProductDto objects containing product information
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
     * Retrieves a specific product by its ID.
     * 
     * @param id The unique identifier of the product
     * @return ResponseEntity containing ProductDto if found, or 404 Not Found if product doesn't exist
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
     * Creates a new product in the catalog.
     * Validates the request data and creates a new product with the provided information.
     * The product is automatically set as active upon creation.
     * 
     * @param request CreateProductRequest containing product information
     * @return ResponseEntity containing the newly created ProductDto
     */
    @PostMapping
    @Operation(summary = "2. Create new product", description = "Create a new product in the catalog")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequest request) {
        // Convert request to entity using mapper
        Product product = mapper.toEntity(request);
        
        // Save product and return DTO representation
        return ResponseEntity.ok(mapper.toDto(productService.createProduct(product)));
    }

    /**
     * Updates an existing product's information.
     * Only updates fields that are provided in the request (partial update).
     * 
     * @param id The unique identifier of the product to update
     * @param request UpdateProductRequest containing updated information
     * @return ResponseEntity containing the updated ProductDto
     */
    @PutMapping("/{id}")
    @Operation(summary = "4. Update product", description = "Update an existing product's information")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        Product existingProduct = productService.findProductById(id).orElseThrow();
        mapper.updateEntityFromRequest(existingProduct, request);
        return ResponseEntity.ok(mapper.toDto(productService.updateProduct(id, existingProduct)));
    }

    /**
     * Deletes a product from the catalog.
     * Permanently removes the product from the system.
     * 
     * @param id The unique identifier of the product to delete
     * @return ResponseEntity with 204 No Content status indicating successful deletion
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "5. Delete product", description = "Permanently delete a product from the catalog")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}