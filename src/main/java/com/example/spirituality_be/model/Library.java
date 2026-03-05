package com.example.spirituality_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "`spiritual_library`")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "`category`", length = 50, nullable = false)
    private String category;

    @Column(name = "item_id", length = 50, nullable = false)
    private String itemId;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "meaning_upright", columnDefinition = "TEXT")
    private String meaningUpright;

    @Column(name = "meaning_reversed", columnDefinition = "TEXT")
    private String meaningReversed;

    @Column(columnDefinition = "TEXT")
    private String correlation;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
