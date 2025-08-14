package com.gradlemedium100.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * User entity model for security context with Spring Security integration.
 * Implements UserDetails interface to provide user information for authentication 
 * and authorization purposes.
 * 
 * This class is the core security model that represents an authenticated user
 * within the Spring Security framework.
 */
public class SecurityUser implements UserDetails {

    private static final long serialVersionUID = 1L;
    
    /**
     * Unique identifier for the user
     */
    private Long id;
    
    /**
     * Username for authentication
     */
    private String username;
    
    /**
     * Encrypted password for authentication
     */
    private String password;
    
    /**
     * User email address
     */
    private String email;
    
    /**
     * User's first name
     */
    private String firstName;
    
    /**
     * User's last name
     */
    private String lastName;
    
    /**
     * List of roles assigned to this user
     */
    private List<Role> roles;
    
    /**
     * Flag indicating if the user account is active
     */
    private boolean enabled;
    
    /**
     * Flag indicating if the user account is not expired
     */
    private boolean accountNonExpired;
    
    /**
     * Flag indicating if the user account is not locked
     */
    private boolean accountNonLocked;
    
    /**
     * Flag indicating if the user credentials are not expired
     */
    private boolean credentialsNonExpired;

    /**
     * Default constructor
     */
    public SecurityUser() {
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
    }
    
    /**
     * Constructor with essential fields for user creation
     * 
     * @param username Username for authentication
     * @param password Encrypted password for authentication
     * @param email User email address
     * @param firstName User's first name
     * @param lastName User's last name
     * @param roles List of roles assigned to this user
     */
    public SecurityUser(String username, String password, String email, 
                        String firstName, String lastName, List<Role> roles) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }
    
    /**
     * Returns the authorities granted to the user as a collection of GrantedAuthority objects.
     * Converts the Role enums to SimpleGrantedAuthority objects required by Spring Security.
     *
     * @return Collection of GrantedAuthority objects
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert Role enums to SimpleGrantedAuthority objects
        // FIXME: Handle case when roles is null to prevent NullPointerException
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return the username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true if the user's account is valid (non-expired), false otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return true if the user is not locked, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    /**
     * Indicates whether the user's credentials have expired.
     *
     * @return true if the user's credentials are valid (non-expired), false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return true if the user is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // Getters and Setters
    
    /**
     * Gets the user's unique identifier
     * 
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the user's unique identifier
     * 
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the username
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the password
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's email address
     * 
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address
     * 
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's first name
     * 
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name
     * 
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the user's last name
     * 
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name
     * 
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the user's assigned roles
     * 
     * @return the roles
     */
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * Sets the user's assigned roles
     * 
     * @param roles the roles to set
     */
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    /**
     * Sets whether the user account is enabled
     * 
     * @param enabled the enabled status to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets whether the user account is non-expired
     * 
     * @param accountNonExpired the accountNonExpired status to set
     */
    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    /**
     * Sets whether the user account is non-locked
     * 
     * @param accountNonLocked the accountNonLocked status to set
     */
    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    /**
     * Sets whether the user credentials are non-expired
     * 
     * @param credentialsNonExpired the credentialsNonExpired status to set
     */
    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }
    
    /**
     * Returns the full name of the user (first name + last name)
     * 
     * @return the full name as a string
     */
    public String getFullName() {
        // TODO: Handle null cases for firstName or lastName
        return firstName + " " + lastName;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityUser that = (SecurityUser) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "SecurityUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", roles=" + roles +
                ", enabled=" + enabled +
                '}';
    }
}