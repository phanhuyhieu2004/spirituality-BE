package com.example.spirituality_be.repository;

import com.example.spirituality_be.model.DailyEnergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyEnergyRepository extends JpaRepository<DailyEnergy, Long> {
    Optional<DailyEnergy> findFirstByAccountIdAndDateOfRecordOrderByCreatedAtDesc(Long accountId, LocalDate date);

    @org.springframework.transaction.annotation.Transactional
    void deleteByAccountIdAndDateOfRecord(Long accountId, LocalDate date);

    @org.springframework.transaction.annotation.Transactional
    void deleteByAccountId(Long accountId);
}