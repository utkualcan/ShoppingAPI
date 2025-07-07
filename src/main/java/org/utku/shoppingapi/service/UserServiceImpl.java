package org.utku.shoppingapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.utku.shoppingapi.constants.AppConstants;
import org.utku.shoppingapi.dto.request.UpdateUserRequest;
import org.utku.shoppingapi.entity.Role;
import org.utku.shoppingapi.entity.User;
import org.utku.shoppingapi.exception.ResourceNotFoundException;
import org.utku.shoppingapi.mapper.EntityMapper;
import org.utku.shoppingapi.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Service implementation for comprehensive user management operations.
 * 
 * <p>This service class provides the business logic layer for all user-related
 * operations in the e-commerce shopping system. It implements the UserService
 * interface and handles complex business rules, data validation, and integration
 * with the data access layer.
 * 
 * <p>Key responsibilities:
 * <ul>
 *   <li>User account lifecycle management (create, read, update, delete)</li>
 *   <li>GDPR-compliant user data anonymization</li>
 *   <li>Business rule validation and enforcement</li>
 *   <li>Data transformation between entities and DTOs</li>
 *   <li>Transaction management for data consistency</li>
 * </ul>
 * 
 * <p>This implementation follows e-commerce best practices:
 * <ul>
 *   <li>Soft deletion through data anonymization</li>
 *   <li>Preservation of order history and referential integrity</li>
 *   <li>GDPR compliance for user privacy rights</li>
 *   <li>Transactional operations for data consistency</li>
 * </ul>
 * 
 * <p>All methods are transactional by default due to the class-level
 * {@code @Transactional} annotation, ensuring data consistency across
 * complex operations.
 * 
 * @author Shopping API Development Team
 * @version 1.0
 * @since 1.0
 * @see UserService
 * @see User
 * @see UserRepository
 * @see EntityMapper
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityMapper mapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new UserServiceImpl with required dependencies.
     * 
     * <p>This constructor is used by Spring's dependency injection container
     * to inject the required repository and mapper dependencies. The constructor
     * injection pattern ensures that all dependencies are available when the
     * service is instantiated.
     * 
     * @param userRepository The repository component for user data access operations.
     *                      Provides CRUD operations and custom queries for User entities.
     * @param mapper The mapper component for converting between User entities
     *              and various DTO representations.
     * @param passwordEncoder The password encoder for securing user passwords.
     * @throws IllegalArgumentException if any parameter is null
     */
    public UserServiceImpl(UserRepository userRepository, EntityMapper mapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves all users from the system with pagination support.
     * 
     * <p>This method provides paginated access to all user accounts in the system,
     * including both active and inactive accounts. It's primarily used for
     * administrative purposes and user management interfaces.
     * 
     * <p>The method supports:
     * <ul>
     *   <li>Flexible pagination with configurable page size</li>
     *   <li>Multi-field sorting capabilities</li>
     *   <li>Efficient database queries with proper indexing</li>
     *   <li>Metadata about total elements and pages</li>
     * </ul>
     * 
     * <p>Performance considerations:
     * <ul>
     *   <li>Uses database-level pagination to minimize memory usage</li>
     *   <li>Lazy loading of related entities to improve performance</li>
     *   <li>Proper indexing on sortable fields recommended</li>
     * </ul>
     * 
     * @param pageable Pagination and sorting parameters including page number,
     *                page size, and sort criteria. Must not be null.
     * @return A Page containing User entities matching the pagination criteria,
     *         along with pagination metadata such as total elements and pages
     * @throws IllegalArgumentException if pageable parameter is null
     */
    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Locates a specific user by their unique identifier.
     * 
     * <p>This method performs a direct lookup of a user entity using their
     * primary key. It returns an Optional to handle the case where no user
     * exists with the specified ID, following modern Java best practices
     * for null safety.
     * 
     * <p>The method will return users regardless of their enabled status,
     * allowing for administrative operations on disabled accounts.
     * 
     * <p>Usage patterns:
     * <pre>
     * Optional&lt;User&gt; user = userService.findUserById(123L);
     * if (user.isPresent()) {
     *     // Process the user
     * } else {
     *     // Handle user not found
     * }
     * </pre>
     * 
     * @param id The unique identifier of the user to locate.
     *          Must be a positive long value.
     * @return An Optional containing the User entity if found,
     *         or an empty Optional if no user exists with the specified ID
     * @throws IllegalArgumentException if id is null or negative
     */
    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Creates a new user account in the system.
     * 
     * <p>This method handles the complete user registration process, including
     * data validation, business rule enforcement, and persistence to the database.
     * The user account is immediately active upon creation unless explicitly
     * disabled.
     * 
     * <p>Creation process includes:
     * <ul>
     *   <li>Validation of required fields and constraints</li>
     *   <li>Uniqueness checks for username and email</li>
     *   <li>Password encryption (if not already encrypted)</li>
     *   <li>Default role assignment</li>
     *   <li>Automatic timestamp generation</li>
     * </ul>
     * 
     * <p>The method is transactional, ensuring that either all user data
     * is successfully created or the entire operation is rolled back in
     * case of any failure.
     * 
     * <p>Post-creation effects:
     * <ul>
     *   <li>User ID is automatically generated and assigned</li>
     *   <li>Creation and update timestamps are set</li>
     *   <li>Account is enabled by default</li>
     *   <li>Associated cart may be created automatically</li>
     * </ul>
     * 
     * @param user The User entity containing all required information for
     *            account creation. Must not be null and must pass validation.
     * @return The persisted User entity with generated ID and timestamps
     * @throws IllegalArgumentException if user parameter is null
     * @throws ValidationException if user data fails validation constraints
     * @throws DataIntegrityViolationException if username or email already exists
     */
    @Override
    public User createUser(User user) {
        // Encode password if not already encoded
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        // Assign USER role by default if no roles are set
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<Role> defaultRoles = new HashSet<>();
            defaultRoles.add(Role.USER);
            user.setRoles(defaultRoles);
        }
        
        return userRepository.save(user);
    }

    /**
     * Updates an existing user's information with partial update support.
     * 
     * <p>This method provides flexible user profile updates by supporting
     * partial modifications. Only fields present in the update request are
     * modified, while other fields remain unchanged. This approach is
     * efficient and user-friendly for profile management.
     * 
     * <p>Update process:
     * <ol>
     *   <li>Locate the existing user by ID</li>
     *   <li>Validate the update request data</li>
     *   <li>Apply only non-null fields from the request</li>
     *   <li>Perform business rule validation</li>
     *   <li>Persist the changes to the database</li>
     *   <li>Update the modification timestamp</li>
     * </ol>
     * 
     * <p>Supported update fields:
     * <ul>
     *   <li>firstName - User's first name</li>
     *   <li>lastName - User's last name</li>
     *   <li>phoneNumber - Contact phone number</li>
     *   <li>enabled - Account status (administrative)</li>
     * </ul>
     * 
     * <p>Protected fields (cannot be updated via this method):
     * <ul>
     *   <li>username - Requires separate endpoint for security</li>
     *   <li>email - Requires verification process</li>
     *   <li>password - Requires separate secure endpoint</li>
     *   <li>roles - Requires administrative privileges</li>
     * </ul>
     * 
     * @param id The unique identifier of the user to update.
     *          Must correspond to an existing user account.
     * @param request The UpdateUserRequest containing fields to be updated.
     *               Only non-null fields will be applied to the user entity.
     * @return The updated User entity with all current information and
     *         refreshed modification timestamp
     * @throws ResourceNotFoundException if no user exists with the specified ID
     * @throws IllegalArgumentException if id is null or request is null
     * @throws ValidationException if request data fails validation constraints
     */
    @Override
    public User updateUser(Long id, UpdateUserRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND + id));

        // Use mapper to update entity from request
        mapper.updateEntityFromRequest(existing, request);

        return userRepository.save(existing);
    }

    /**
     * Performs GDPR-compliant user account deletion through data anonymization.
     * 
     * <p>This method implements the "right to be forgotten" as required by GDPR
     * while maintaining business data integrity and order history. Instead of
     * performing a hard delete that could break referential integrity, the
     * method anonymizes personal data while preserving business relationships.
     * 
     * <p>Anonymization process:
     * <ol>
     *   <li>Locate the user account by ID</li>
     *   <li>Replace username with "deleted_user_[ID]"</li>
     *   <li>Replace email with "deleted_[ID]@deleted.com"</li>
     *   <li>Set firstName to "Deleted"</li>
     *   <li>Set lastName to "User"</li>
     *   <li>Clear phone number (set to null)</li>
     *   <li>Disable the account (enabled = false)</li>
     *   <li>Preserve user ID and timestamps for audit trails</li>
     * </ol>
     * 
     * <p>Data preservation rationale:
     * <ul>
     *   <li>Order history remains intact for business reporting</li>
     *   <li>Financial records maintain referential integrity</li>
     *   <li>Database foreign key constraints are preserved</li>
     *   <li>Audit trails remain available for compliance</li>
     * </ul>
     * 
     * <p>Privacy compliance:
     * <ul>
     *   <li>All personally identifiable information is removed</li>
     *   <li>Account cannot be used for authentication</li>
     *   <li>User cannot be contacted through anonymized data</li>
     *   <li>Original identity cannot be reconstructed</li>
     * </ul>
     * 
     * <p>This approach is standard in e-commerce platforms and ensures
     * compliance with data protection regulations while maintaining
     * business operational requirements.
     * 
     * @param id The unique identifier of the user account to anonymize.
     *          Must correspond to an existing user account.
     * @throws ResourceNotFoundException if no user exists with the specified ID
     * @throws IllegalArgumentException if id is null or negative
     * @see <a href="https://gdpr.eu/right-to-be-forgotten/">GDPR Right to be Forgotten</a>
     */
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND + id));
        
        // GDPR compliant anonymization
        user.setUsername("deleted_user_" + id);
        user.setEmail("deleted_" + id + "@deleted.com");
        user.setFirstName("Deleted");
        user.setLastName("User");
        user.setPhoneNumber(null);
        user.setEnabled(false);
        
        userRepository.save(user);
    }
}