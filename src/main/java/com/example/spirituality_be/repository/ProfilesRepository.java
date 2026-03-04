package com.example.spirituality_be.repository;

import com.example.spirituality_be.model.Profiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfilesRepository extends JpaRepository<Profiles, Long> {
    Optional<Profiles> findByAccountId(Long accountId);

    @org.springframework.data.jpa.repository.Query(value = "SELECT YEAR(dob) as year, COUNT(*) as count FROM profiles WHERE dob IS NOT NULL GROUP BY YEAR(dob)", nativeQuery = true)
    java.util.List<Object[]> getBirthYearDistribution();
}