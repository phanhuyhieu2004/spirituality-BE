package com.example.spirituality_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_cache")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiCache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_hash", length = 64)
    private String questionHash;

    @Column(name = "tarot_card_id")
    private Integer tarotCardId;

    @Column(name = "iching_hex_id")
    private Integer ichingHexId;

    @Column(name = "ai_response", columnDefinition = "TEXT")
    private String aiResponse;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}