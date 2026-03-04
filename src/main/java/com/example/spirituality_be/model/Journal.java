package com.example.spirituality_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "journals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Journal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Accounts account;

    @Column(columnDefinition = "TEXT")
    private String question;

    @Column(name = "reading_type", length = 50)
    private String readingType;

    @Column(name = "tarot_card_id")
    private Integer tarotCardId;

    @Column(name = "tarot_snapshot", columnDefinition = "TEXT")
    private String tarotSnapshot;

    @Column(name = "iching_hex_id")
    private Integer ichingHexId;

    @Column(name = "iching_snapshot", columnDefinition = "TEXT")
    private String ichingSnapshot;

    @Column(name = "ai_insight", columnDefinition = "TEXT")
    private String aiInsight;

    @Column(name = "user_notes", columnDefinition = "TEXT")
    private String userNotes;


    private Integer mentalEnergy;
    private Integer emotionalBalance;
    private Integer focusLevel;
    private Integer actionReadiness;
    private Integer socialHarmony;
    private Integer personalDayNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}