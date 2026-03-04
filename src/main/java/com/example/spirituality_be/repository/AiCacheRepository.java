package com.example.spirituality_be.repository;

import com.example.spirituality_be.model.AiCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AiCacheRepository extends JpaRepository<AiCache, Long> {
    Optional<AiCache> findByQuestionHashAndTarotCardIdAndIchingHexId(String questionHash, Integer tarotCardId, Integer ichingHexId);
}