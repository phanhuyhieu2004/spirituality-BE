package com.example.spirituality_be.repository;

import com.example.spirituality_be.model.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Long> {
    Optional<Accounts> findByEmail(String email);
    Boolean existsByEmail(String email);

    @org.springframework.data.jpa.repository.Query(value = "SELECT DATE(created_at) as date, COUNT(*) as count FROM accounts GROUP BY DATE(created_at) ORDER BY date DESC LIMIT 30", nativeQuery = true)
    java.util.List<Object[]> getUserGrowthData();

    @org.springframework.data.jpa.repository.Query(value = "SELECT COUNT(*) FROM accounts WHERE status = 'active'", nativeQuery = true)
    long countActiveUsers();

    java.util.List<com.example.spirituality_be.model.Accounts> findTop10ByOrderByIdDesc();
}