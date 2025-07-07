package org.utku.shoppingapi.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.utku.shoppingapi.entity.Role;
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
    
    private Long id;
    private String username;
    private String email;
    private String password;
    private Boolean enabled;
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

    // Getters
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

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