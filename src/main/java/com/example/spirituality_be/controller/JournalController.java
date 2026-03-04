package com.example.spirituality_be.controller;

import com.example.spirituality_be.model.*;
import com.example.spirituality_be.repository.*;
import com.example.spirituality_be.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/journal")
@RequiredArgsConstructor
public class JournalController {

    private final JournalRepository journalRepository;
    private final AccountsRepository accountsRepository;
    private final LibraryRepository libraryRepository;
    private final DailyEnergyRepository dailyEnergyRepository;

    @GetMapping("/list")
    public ResponseEntity<Page<Journal>> getMyJournals(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer tarotId,
            @RequestParam(required = false) Integer hexId,
            @RequestParam(required = false) String date) {

        Pageable pageable = PageRequest.of(page, size);


        String sanitizedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String sanitizedType = (type != null && !type.trim().isEmpty()) ? type.trim() : null;
        String sanitizedDate = (date != null && !date.trim().isEmpty()) ? date.trim() : null;

        return ResponseEntity.ok(journalRepository.advancedSearch(
            userPrincipal.getId(),
            sanitizedKeyword,
            sanitizedType,
            tarotId,
            hexId,
            sanitizedDate,
            pageable
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJournalDetail(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long id) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nhật ký không tồn tại"));

        if (!journal.getAccount().getId().equals(userPrincipal.getId())) {
            return ResponseEntity.status(403).body("Bạn không có quyền xem nhật ký này");
        }


        return ResponseEntity.ok(journal);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createJournal(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody Map<String, Object> request) {
        Accounts account = accountsRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Journal j = new Journal();
        j.setAccount(account);
        j.setQuestion((String) request.get("question"));
        j.setUserNotes((String) request.get("notes"));
        j.setReadingType("manual_entry");

        Boolean linkEnergy = (Boolean) request.get("linkEnergy");
        if (Boolean.TRUE.equals(linkEnergy)) {
            dailyEnergyRepository.findFirstByAccountIdAndDateOfRecordOrderByCreatedAtDesc(
                    account.getId(), java.time.LocalDate.now()).ifPresent(e -> {
                j.setMentalEnergy(e.getMentalEnergy());
                j.setEmotionalBalance(e.getEmotionalBalance());
                j.setFocusLevel(e.getFocusLevel());
                j.setActionReadiness(e.getActionReadiness());
                j.setSocialHarmony(e.getSocialHarmony());
                j.setPersonalDayNumber(e.getPersonalDayNumber());
            });
        }

        journalRepository.save(j);
        return ResponseEntity.ok("Đã lưu nhật ký mới!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJournal(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                           @PathVariable Long id,
                                           @RequestBody Map<String, Object> request) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nhật ký không tồn tại"));

        if (!journal.getAccount().getId().equals(userPrincipal.getId())) {
            return ResponseEntity.status(403).body("Bạn không có quyền chỉnh sửa nhật ký này");
        }

        if (request.containsKey("notes")) {
            journal.setUserNotes((String) request.get("notes"));
        }
        if (request.containsKey("question") && journal.getReadingType().equals("manual_entry")) {
            journal.setQuestion((String) request.get("question"));
        }

        journalRepository.save(journal);
        return ResponseEntity.ok("Cập nhật nhật ký thành công!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJournal(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long id) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nhật ký không tồn tại"));

        if (!journal.getAccount().getId().equals(userPrincipal.getId())) {
            return ResponseEntity.status(403).body("Bạn không có quyền xóa nhật ký này");
        }

        journalRepository.delete(journal);
        return ResponseEntity.ok("Đã xóa nhật ký!");
    }
}