package com.example.spirituality_be.repository;

import com.example.spirituality_be.model.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Integer> {
    List<Library> findByType(String type);

    @org.springframework.data.jpa.repository.Query("SELECT l FROM Library l WHERE " +
            "(:type IS NULL OR l.type = :type) AND " +
            "(:keyword IS NULL OR LOWER(l.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    org.springframework.data.domain.Page<Library> search(
            @org.springframework.data.repository.query.Param("type") String type,
            @org.springframework.data.repository.query.Param("keyword") String keyword,
            org.springframework.data.domain.Pageable pageable);
}