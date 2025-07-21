package org.utku.shoppingapi.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.utku.shoppingapi.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of UserDetails interface for Spring Security.
 * Represents the user principal used in authentication and authorization.
 */
public class UserPrincipal implements UserDetails {
    /**
     * User ID of the authenticated principal.
     */
    private Long id;
    /**
     * Username of the authenticated principal.
     */
    private String username;
    /**
     * Email address of the authenticated principal.
     */
    private String email;
    /**
     * Password hash of the authenticated principal.
     */
    private String password;
    /**
     * Indicates whether the user account is enabled.
     */
    private Boolean enabled;
    /**
     * Authorities granted to the user (roles and permissions).
     */
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String username, String email, String password, Boolean enabled, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    /**
     * Create UserPrincipal from User entity.
     *
     * @param user User entity
     * @return UserPrincipal instance
     */
    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getEnabled(),
                authorities
        );
    }

    /**
     * Returns the user ID.
     * @return user ID
     */
    public Long getId() {
        return id;
    }
    /**
     * Returns the email address.
     * @return email address
     */
    public String getEmail() {
        return email;
    }
    /**
     * Returns the username.
     * @return username
     */
    @Override
    public String getUsername() {
        return username;
    }
    /**
     * Returns the password hash.
     * @return password hash
     */
    @Override
    public String getPassword() {
        return password;
    }
    /**
     * Returns the authorities granted to the user.
     * @return authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    /**
     * Indicates whether the account is non-expired.
     * @return always true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    /**
     * Indicates whether the account is non-locked.
     * @return always true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    /**
     * Indicates whether the credentials are non-expired.
     * @return always true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    /**
     * Indicates whether the user account is enabled.
     * @return enabled status
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}