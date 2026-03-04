package com.example.spirituality_be.controller;

import com.example.spirituality_be.dto.ReadingResultDto;
import com.example.spirituality_be.model.Accounts;
import com.example.spirituality_be.repository.AccountsRepository;
import com.example.spirituality_be.security.UserPrincipal;
import com.example.spirituality_be.service.ReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reading")
@RequiredArgsConstructor
public class ReadingController {

    private final ReadingService readingService;
    private final AccountsRepository accountsRepository;

    @PostMapping("/draw")
    public ResponseEntity<?> draw(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                  @RequestBody Map<String, String> request) {
        String question = request.getOrDefault("question", "Lời khuyên tổng hợp cho tôi");

        Accounts account = accountsRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        try {
            ReadingResultDto result = readingService.performReading(account, question);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/save-journal")
    public ResponseEntity<?> saveToJournal(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                          @RequestBody Map<String, Object> request) {
        Accounts account = accountsRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        try {
            String question = (String) request.get("question");


            Integer tarotId = parseId(request.get("tarotId"));
            Integer hexId = parseId(request.get("hexId"));
            String aiInsight = (String) request.get("aiInterpretation");

            readingService.saveToJournalManual(account, question, tarotId, hexId, aiInsight);
            return ResponseEntity.ok("Đã lưu vào nhật ký tâm linh!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Không thể lưu: " + e.getMessage());
        }
    }

    private Integer parseId(Object val) {
        if (val == null) return null;
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        try {
            return Integer.parseInt(val.toString());
        } catch (Exception e) {
            return null;
        }
    }
}