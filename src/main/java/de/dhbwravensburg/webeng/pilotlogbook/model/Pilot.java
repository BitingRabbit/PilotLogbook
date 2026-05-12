package de.dhbwravensburg.webeng.pilotlogbook.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Pilot account - JPA entity and Spring Security {@link UserDetails} principal.
 * {@link #email} is the login username; {@link #password} holds the BCrypt hash.
 * <p>
 * Both fields have no Lombok setter to avoid accidental direct assignment
 * (e.g. storing a plaintext password)
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "pilots")
public class Pilot implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    private Long id;

    /** Pilot's first name */
    @Column(nullable = false, length = 50)
    private String firstName;

    /** Pilot's last name */
    @Column(nullable = false, length = 50)
    private String lastName;

    /**
     * Email used as login identifier. Unique across the system.
     * Immutable through the generic setter — re-assigning the email of an
     * existing account requires a dedicated service-level flow.
     */
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * BCrypt-hashed password
     */
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, length = 255)
    private String password;

    /** Timestamp set once on first persist. Never updated afterward. */
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Creates a new pilot. Caller is responsible for ensuring {@code password}
     * is already hashed by the configured {@code PasswordEncoder}.
     *
     * @param firstName first name
     * @param lastName  last name
     * @param email     unique login email
     * @param password  hashed password
     */
    @Builder
    public Pilot(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}