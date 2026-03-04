package com.example.spirituality_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_energy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyEnergy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Accounts account;

    private LocalDate dateOfRecord;

    private Integer mentalEnergy;
    private Integer emotionalBalance;
    private Integer focusLevel;
    private Integer actionReadiness;
    private Integer socialHarmony;
    private Integer personalDayNumber;

    @Column(columnDefinition = "TEXT")
    private String affirmation;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}