package com.example.spirituality_be.controller;

import com.example.spirituality_be.repository.AccountsRepository;
import com.example.spirituality_be.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AccountsRepository accountsRepository;
    private final com.example.spirituality_be.repository.ProfilesRepository profilesRepository;
    private final com.example.spirituality_be.repository.LibraryRepository libraryRepository;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getStatistics(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Map<String, Object> stats = new HashMap<>();


        stats.put("totalUsers", accountsRepository.count());


        stats.put("activeUsers", accountsRepository.countActiveUsers());


        List<Object[]> growthRaw = accountsRepository.getUserGrowthData();
        List<Map<String, Object>> growthData = new ArrayList<>();
        for (Object[] row : growthRaw) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("date", row[0].toString());
            entry.put("count", row[1]);
            growthData.add(entry);
        }
        stats.put("userGrowth", growthData);


        List<com.example.spirituality_be.model.Accounts> recent = accountsRepository.findTop10ByOrderByIdDesc();
        List<Map<String, Object>> recentData = new ArrayList<>();
        for (com.example.spirituality_be.model.Accounts acc : recent) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", acc.getId());
            m.put("email", acc.getEmail());
            m.put("created_at", acc.getCreated_at());
            recentData.add(m);
        }
        stats.put("recentUsers", recentData);


        String[] earthlyBranches = {"Thân", "Dậu", "Tuất", "Hợi", "Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ", "Ngọ", "Mùi"};
        List<Object[]> yearRaw = profilesRepository.getBirthYearDistribution();
        Map<String, Integer> zodiacCounts = new HashMap<>();


        for (String branch : earthlyBranches) zodiacCounts.put(branch, 0);

        for (Object[] row : yearRaw) {
            if (row[0] != null) {
                int year = ((Number) row[0]).intValue();
                int count = ((Number) row[1]).intValue();
                String branch = earthlyBranches[year % 12];
                zodiacCounts.put(branch, zodiacCounts.get(branch) + count);
            }
        }

        List<Map<String, Object>> zodiacList = new ArrayList<>();
        for (String branch : earthlyBranches) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("label", branch);
            entry.put("count", zodiacCounts.get(branch));
            zodiacList.add(entry);
        }
        stats.put("zodiacDistribution", zodiacList);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getUsers(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String search,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String startDate,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String endDate
    ) {

        List<com.example.spirituality_be.model.Accounts> allAccounts = accountsRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (com.example.spirituality_be.model.Accounts acc : allAccounts) {

            if (acc.getRole() == com.example.spirituality_be.model.Accounts.Role.admin) continue;


            if (search != null && !search.isEmpty()) {
                if (!acc.getEmail().toLowerCase().contains(search.toLowerCase())) {
                    continue;
                }
            }


            if (startDate != null && !startDate.isEmpty()) {
                if (acc.getCreated_at().toString().compareTo(startDate) < 0) continue;
            }
            if (endDate != null && !endDate.isEmpty()) {
                if (acc.getCreated_at().toString().compareTo(endDate) > 0) continue;
            }

            Map<String, Object> m = new HashMap<>();
            m.put("id", acc.getId());
            m.put("email", acc.getEmail());
            m.put("role", acc.getRole());
            m.put("status", acc.getStatus());
            m.put("created_at", acc.getCreated_at());


            profilesRepository.findByAccountId(acc.getId()).ifPresent(p -> m.put("fullName", p.getFullName()));

            result.add(m);
        }

        return ResponseEntity.ok(result);
    }

    @org.springframework.web.bind.annotation.PutMapping("/users/{id}/toggle-status")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> toggleUserStatus(@org.springframework.web.bind.annotation.PathVariable Long id) {
        com.example.spirituality_be.model.Accounts account = accountsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getRole() == com.example.spirituality_be.model.Accounts.Role.admin) {
            return ResponseEntity.badRequest().body("Không thể thay đổi trạng thái của quản trị viên");
        }

        if (account.getStatus() == com.example.spirituality_be.model.Accounts.Status.active) {
            account.setStatus(com.example.spirituality_be.model.Accounts.Status.banned);
        } else {
            account.setStatus(com.example.spirituality_be.model.Accounts.Status.active);
        }

        accountsRepository.save(account);
        return ResponseEntity.ok("Vũ trụ đã thay đổi trạng thái linh hồn!");
    }

    @GetMapping("/library")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getLibraryItems(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String type,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String search
    ) {

        List<com.example.spirituality_be.model.Library> items;
        if (type != null && !type.isEmpty()) {
            items = libraryRepository.findByType(type);
        } else {
            items = libraryRepository.findAll();
        }

        if (search != null && !search.isEmpty()) {
            String keyword = search.toLowerCase();
            items = items.stream()
                    .filter(i -> i.getName().toLowerCase().contains(keyword) ||
                                (i.getDescription() != null && i.getDescription().toLowerCase().contains(keyword)))
                    .collect(java.util.stream.Collectors.toList());
        }

        return ResponseEntity.ok(items);
    }

    @PutMapping("/library/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> updateLibraryItem(
            @org.springframework.web.bind.annotation.PathVariable Integer id,
            @org.springframework.web.bind.annotation.RequestBody com.example.spirituality_be.model.Library updated
    ) {
        com.example.spirituality_be.model.Library existing = libraryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setMeaningUpright(updated.getMeaningUpright());
        existing.setMeaningReversed(updated.getMeaningReversed());
        existing.setImageUrl(updated.getImageUrl());
        existing.setCorrelation(updated.getCorrelation());


        libraryRepository.save(existing);
        return ResponseEntity.ok("Dữ liệu huyền học đã được cập nhập!");
    }
}
