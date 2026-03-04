package com.example.spirituality_be.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Accounts account;

    @Column(name = "full_name")
    private String fullName;

    private LocalDate dob;

    @Column(name = "birth_hour")
    private Byte birthHour;

    @Column(name = "birth_minute")
    private Byte birthMinute;

    @Column(name = "god_number")
    private Integer godNumber;

    @Column(columnDefinition = "json")
    private String elements;

    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    @Column(name = "free_turns_today")
    private Integer freeTurnsToday = 5;

    @Column(name = "last_reset_date")
    private LocalDate lastResetDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastResetDate = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Transient
    private boolean hasPassword;

    @Transient
    private String role;
}