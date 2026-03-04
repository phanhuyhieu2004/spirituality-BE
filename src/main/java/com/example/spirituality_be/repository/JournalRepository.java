package com.example.spirituality_be.repository;

import com.example.spirituality_be.model.Journal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
    Page<Journal> findByAccount_IdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT j FROM Journal j WHERE j.account.id = :accountId " +
           "AND (:keyword IS NULL OR j.question LIKE %:keyword% OR j.aiInsight LIKE %:keyword% OR j.userNotes LIKE %:keyword%) " +
           "AND (:type IS NULL OR j.readingType = :type) " +
           "AND (:tarotId IS NULL OR j.tarotCardId = :tarotId) " +
           "AND (:hexId IS NULL OR j.ichingHexId = :hexId) " +
           "AND (:date IS NULL OR CAST(j.createdAt AS string) LIKE :date%) " +
           "ORDER BY j.createdAt DESC")
    Page<Journal> advancedSearch(Long accountId, String keyword, String type, Integer tarotId, Integer hexId, String date, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT j FROM Journal j WHERE j.account.id = :accountId AND " +
           "(j.question LIKE %:keyword% OR j.aiInsight LIKE %:keyword% OR j.userNotes LIKE %:keyword% " +
           "OR j.tarotSnapshot LIKE %:keyword% OR j.ichingSnapshot LIKE %:keyword%)")
    Page<Journal> searchByKeyword(Long accountId, String keyword, Pageable pageable);

    java.util.List<Journal> findByAccount_IdOrderByCreatedAtDesc(Long accountId);
}