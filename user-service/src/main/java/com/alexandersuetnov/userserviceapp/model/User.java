package com.alexandersuetnov.userserviceapp.model;

import com.alexandersuetnov.userserviceapp.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "users")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", unique = true, updatable = false)
    private String email;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(unique = true, updatable = false)
    private String username;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false)
    private String name;

    @Column(name = "active")
    private boolean active;

    @Column(length = 3000)
    private String password;

    @ElementCollection(targetClass = Role.class)
    @CollectionTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> roles = new HashSet<>();

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime createDate;

    public boolean isActive() {
        return active;
    }

    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    public User(Long id,
                String email,
                String phoneNumber,
                String name,
                String password,
                Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {return username;}

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
    }

    @Override
    public String getPassword() {
        return password;
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
        return true;
    }

}
