package com.parking.auth;

import jakarta.persistence.*;

@Entity
@Table(name = "user_accounts", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class UserAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, length = 500)
    private String passwordHash;
    @Column(nullable = false)
    private String role;

    public UserAccount() {}
    public UserAccount(String fullName, String email, String passwordHash, String role) {
        this.fullName = fullName; this.email = email; this.passwordHash = passwordHash; this.role = role;
    }
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
}
