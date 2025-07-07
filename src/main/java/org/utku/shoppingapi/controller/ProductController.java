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
     * <p>This endpoint returns a paginated list of all products in the catalog.
     * By default, results are sorted by product ID in ascending order and
     * limited to the default page size. Both active and inactive products
     * are included in the results.
     * 
     * <p>Pagination parameters can be customized using query parameters:
     * <ul>
     *   <li>page: Zero-based page index (default: 0)</li>
     *   <li>size: Number of products per page (default: 20)</li>
     *   <li>sort: Sort criteria in format 'property,direction' (default: 'id,asc')</li>
     * </ul>
     * 
     * <p>Available sort fields include: id, name, price, category, createdAt, updatedAt
     * 
     * <p>Example usage:
     * <pre>
     * GET /api/products?page=0&size=10&sort=name,asc
     * GET /api/products?page=1&size=5&sort=price,desc
     * </pre>
     * 
     * @param pageable Pagination and sorting parameters automatically bound from request
     * @return Page containing ProductDto objects with pagination metadata
     * @see ProductDto
     * @see AppConstants#DEFAULT_PAGE_SIZE
     * @see AppConstants#DEFAULT_SORT_FIELD
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
     * Retrieves detailed information about a specific product by its unique identifier.
     * 
     * <p>This endpoint fetches comprehensive information about a single product
     * including its name, description, price, stock quantity, category, and
     * availability status. Both active and inactive products can be retrieved.
     * 
     * <p>Response scenarios:
     * <ul>
     *   <li>200 OK: Product found and returned successfully</li>
     *   <li>404 Not Found: No product exists with the specified ID</li>
     * </ul>
     * 
     * <p>The response includes:
     * <ul>
     *   <li>Product identification and basic information</li>
     *   <li>Pricing and inventory details</li>
     *   <li>Category and description</li>
     *   <li>Availability and active status</li>
     *   <li>Creation and last update timestamps</li>
     * </ul>
     * 
     * <p>Example usage:
     * <pre>
     * GET /api/products/123
     * </pre>
     * 
     * @param id The unique identifier of the product to retrieve.
     *          Must be a positive long value.
     * @return ResponseEntity containing ProductDto if product exists,
     *         or 404 Not Found response if product doesn't exist
     * @see ProductDto
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
     * Creates a new product in the catalog with comprehensive validation.
     * 
     * <p>This endpoint handles the creation of new products in the e-commerce
     * catalog. All input data is validated according to business rules and
     * constraints defined in the CreateProductRequest class.
     * 
     * <p>Validation includes:
     * <ul>
     *   <li>Product name uniqueness and format validation</li>
     *   <li>Price validation (must be positive)</li>
     *   <li>Stock quantity validation (must be non-negative)</li>
     *   <li>Category format validation</li>
     *   <li>Description length constraints</li>
     * </ul>
     * 
     * <p>Upon successful creation:
     * <ul>
     *   <li>Product is automatically set as active</li>
     *   <li>Creation timestamp is automatically set</li>
     *   <li>Unique product ID is generated</li>
     *   <li>Product becomes immediately available for purchase (if in stock)</li>
     * </ul>
     * 
     * <p>Example request body:
     * <pre>
     * {
     *   "name": "Wireless Bluetooth Headphones",
     *   "description": "High-quality wireless headphones with noise cancellation",
     *   "price": 99.99,
     *   "stockQuantity": 50,
     *   "category": "Electronics"
     * }
     * </pre>
     * 
     * @param request CreateProductRequest containing all required product information.
     *               Must pass validation constraints.
     * @return ResponseEntity containing the newly created ProductDto with generated
     *         ID and timestamps
     * @throws ValidationException if request data fails validation
     * @throws DataIntegrityViolationException if product name already exists
     * @see CreateProductRequest
     * @see ProductDto
     */
    @PostMapping
    @Operation(summary = "2. Create new product", description = "Create a new product in the catalog")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequest request) {
        // Convert request to entity using mapper
        Product product = mapper.toEntity(request);
        
        // Save product and return DTO representation
        return ResponseEntity.ok(mapper.toDto(productService.createProduct(product)));
    }

    /**
     * Updates an existing product's information with partial update support.
     * 
     * <p>This endpoint allows modification of product information using a
     * partial update approach. Only fields provided in the request body
     * will be updated, while null or missing fields will remain unchanged.
     * 
     * <p>Updatable fields include:
     * <ul>
     *   <li>name - Product name (must be unique)</li>
     *   <li>description - Product description</li>
     *   <li>price - Product price (must be positive)</li>
     *   <li>stockQuantity - Available inventory (must be non-negative)</li>
     *   <li>category - Product category</li>
     *   <li>active - Product availability status</li>
     * </ul>
     * 
     * <p>Business rules:
     * <ul>
     *   <li>Price changes affect product availability calculations</li>
     *   <li>Stock quantity changes update inventory levels</li>
     *   <li>Setting active=false performs soft delete</li>
     *   <li>Update timestamp is automatically refreshed</li>
     * </ul>
     * 
     * <p>Example request body (partial update):
     * <pre>
     * {
     *   "price": 89.99,
     *   "stockQuantity": 75
     * }
     * </pre>
     * 
     * @param id The unique identifier of the product to update.
     *          Must correspond to an existing product.
     * @param request UpdateProductRequest containing the fields to update.
     *               Only non-null fields will be applied.
     * @return ResponseEntity containing the updated ProductDto with all current information
     * @throws ResourceNotFoundException if no product exists with the specified ID
     * @throws ValidationException if request data fails validation
     * @see UpdateProductRequest
     * @see ProductDto
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
     * Removes a product from the catalog using soft delete functionality.
     * 
     * <p>This endpoint handles product removal in a way that preserves data
     * integrity and order history. Instead of permanently deleting the product
     * record, the system performs a soft delete by setting the product's
     * active status to false.
     * 
     * <p>Soft delete process:
     * <ul>
     *   <li>Product active status set to false</li>
     *   <li>Product becomes unavailable for new purchases</li>
     *   <li>Existing order history is preserved</li>
     *   <li>Product data remains in database for reporting</li>
     *   <li>Update timestamp is refreshed</li>
     * </ul>
     * 
     * <p>This approach ensures:
     * <ul>
     *   <li>Order history integrity is maintained</li>
     *   <li>Business reporting remains accurate</li>
     *   <li>No cascading deletion issues</li>
     *   <li>Product can be reactivated if needed</li>
     * </ul>
     * 
     * <p>Response status:
     * <ul>
     *   <li>204 No Content: Product successfully deactivated</li>
     *   <li>404 Not Found: No product exists with the specified ID</li>
     * </ul>
     * 
     * <p>Note: To permanently remove a product (hard delete), use administrative
     * tools or contact system administrators.
     * 
     * @param id The unique identifier of the product to remove from active catalog.
     *          Must correspond to an existing product.
     * @return ResponseEntity with 204 No Content status indicating successful
     *         soft deletion (deactivation)
     * @throws ResourceNotFoundException if no product exists with the specified ID
     * @see ProductService#deleteProduct(Long)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "5. Delete product", description = "Permanently delete a product from the catalog")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}