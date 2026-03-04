package com.example.spirituality_be.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Accounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash")
    private String password_hash;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column(name = "last_login_at")
    private LocalDateTime last_login_at;

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
        if (role == null) role = Role.user;
        if (status == null) status = Status.active;
    }

    public enum Role {
        user, admin
    }

    public enum Status {
        active, banned, pending
    }
}