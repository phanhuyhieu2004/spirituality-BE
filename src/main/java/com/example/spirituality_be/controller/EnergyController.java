package com.example.spirituality_be.controller;

import com.example.spirituality_be.dto.DailyEnergyDto;
import com.example.spirituality_be.model.Accounts;
import com.example.spirituality_be.repository.AccountsRepository;
import com.example.spirituality_be.service.DailyEnergyService;
import com.example.spirituality_be.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/energy")
@RequiredArgsConstructor
public class EnergyController {

    private final DailyEnergyService dailyEnergyService;
    private final AccountsRepository accountsRepository;

    @GetMapping("/daily")
    public ResponseEntity<?> getDailyEnergy(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Accounts account = retrieveAccount(userPrincipal);
        if (account == null) return ResponseEntity.status(401).body("Phiên đăng nhập không hợp lệ");

        try {
            DailyEnergyDto dto = dailyEnergyService.getDailyEnergy(account, false);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshDailyEnergy(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Accounts account = retrieveAccount(userPrincipal);
        if (account == null) return ResponseEntity.status(401).body("Phiên đăng nhập không hợp lệ");

        try {
            DailyEnergyDto dto = dailyEnergyService.getDailyEnergy(account, true);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Accounts retrieveAccount(UserPrincipal userPrincipal) {
        if (userPrincipal == null) return null;
        return accountsRepository.findById(userPrincipal.getId()).orElse(null);
    }
}
