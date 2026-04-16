package de.dhbwravensburg.webeng.pilotlogbook.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * JPA entity representing a pilot who can log into the system
 * Implements {@link UserDetails} for Spring Security authentication.
 */
@Entity
@Table(name = "pilots")
public class Pilot implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 20)
    private String firstName;

    @Column(nullable = false, length = 20)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Pilot() {
    }

    /**
     * Creates a new Pilot with the given details
     *
     * @param firstName pilots first name
     * @param lastName  pilots last name
     * @param email     unique email used as login username
     * @param password  encoded password
     */
    public Pilot(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }

    // UserDetails methods
    /**
     * Returns a collection of authorities granted to the user. Here, every pilot is assigned the "ROLE_USER" authority
     * @return A collection of GrantedAuthority objects representing the user's roles and permissions.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority role = new SimpleGrantedAuthority("ROLE_USER");
        return List.of(role);
    }

    // Getters and Setters

    /**
     * Returns the pilot's email as the username for Spring Security.
     *
     * @return the pilot's email address
     */
    @Override
    public String getUsername() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

