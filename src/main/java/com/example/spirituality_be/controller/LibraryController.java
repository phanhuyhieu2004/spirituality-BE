package com.example.spirituality_be.controller;

import com.example.spirituality_be.model.Library;
import com.example.spirituality_be.repository.LibraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryRepository libraryRepository;

    @GetMapping("/list")
    public ResponseEntity<Page<Library>> getLibraryList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {

        Pageable pageable = PageRequest.of(page, size);


        String sanitizedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String filterType = (type != null && !type.trim().isEmpty()) ? type.trim() : null;

        return ResponseEntity.ok(libraryRepository.search(filterType, sanitizedKeyword, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Library> getLibraryDetail(@PathVariable Integer id) {
        return libraryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}