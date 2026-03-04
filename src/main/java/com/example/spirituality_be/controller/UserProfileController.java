package com.example.spirituality_be.controller;

import com.example.spirituality_be.dto.OnboardingRequest;
import com.example.spirituality_be.model.Accounts;
import com.example.spirituality_be.model.Profiles;
import com.example.spirituality_be.repository.AccountsRepository;
import com.example.spirituality_be.repository.ProfilesRepository;
import com.example.spirituality_be.security.UserPrincipal;
import com.example.spirituality_be.util.SpiritualityCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profiles")
public class UserProfileController {

    @Autowired
    private ProfilesRepository profilesRepository;

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private com.example.spirituality_be.repository.DailyEnergyRepository dailyEnergyRepository;


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Phiên đăng nhập không hợp lệ hoặc đã hết hạn");
        }

        Optional<Accounts> accountOpt = accountsRepository.findById(userPrincipal.getId());
        if (accountOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản không tồn tại");
        }

        Optional<Profiles> profileOpt = profilesRepository.findByAccountId(userPrincipal.getId());
        if (profileOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chưa hoàn tất onboarding");
        }

        Profiles profile = profileOpt.get();
        profile.setHasPassword(accountOpt.get().getPassword_hash() != null);
        profile.setRole(accountOpt.get().getRole().name());
        return ResponseEntity.ok(profile);
    }


    @PostMapping("/onboard")
    public ResponseEntity<?> onboardUser(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody OnboardingRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Họ tên không được để trống");
        }
        if (request.getDob() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ngày sinh không được để trống");
        }
        if (request.getHour() == null || request.getMinute() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Giờ và phút sinh không được để trống");
        }

        Optional<Accounts> accountOpt = accountsRepository.findById(userPrincipal.getId());
        if (accountOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản không tồn tại");
        }

        Optional<Profiles> existingProfile = profilesRepository.findByAccountId(userPrincipal.getId());
        if (existingProfile.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn đã hoàn tất Onboarding rồi");
        }

        Profiles profile = new Profiles();
        profile.setAccount(accountOpt.get());
        profile.setFullName(request.getFullName());
        profile.setDob(request.getDob());
        profile.setBirthHour(request.getHour());
        profile.setBirthMinute(request.getMinute());


        profile.setGodNumber(SpiritualityCalculator.calculateLifePathNumber(request.getDob()));
        profile.setElements(SpiritualityCalculator.calculateElementsJson(request.getDob()));

        profilesRepository.save(profile);

        return ResponseEntity.ok(profile);
    }


    @PutMapping("/me")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody OnboardingRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Họ tên không được để trống");
        }
        if (request.getDob() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ngày sinh không được để trống");
        }
        if (request.getDob().isAfter(java.time.LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ngày sinh không thể ở tương lai");
        }
        if (request.getHour() == null || request.getMinute() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Giờ và phút sinh không được để trống");
        }

        Optional<Accounts> accountOpt = accountsRepository.findById(userPrincipal.getId());
        if (accountOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản không tồn tại");
        }

        Optional<Profiles> existingProfileOpt = profilesRepository.findByAccountId(userPrincipal.getId());
        if (existingProfileOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile chưa tồn tại, hãy hoàn tất Onboarding trước.");
        }

        Profiles profile = existingProfileOpt.get();
        profile.setFullName(request.getFullName());

        boolean dobChanged = !profile.getDob().equals(request.getDob());
        boolean timeChanged = !profile.getBirthHour().equals(request.getHour()) || !profile.getBirthMinute().equals(request.getMinute());

        if (dobChanged || timeChanged) {
            profile.setDob(request.getDob());
            profile.setBirthHour(request.getHour());
            profile.setBirthMinute(request.getMinute());


            profile.setGodNumber(SpiritualityCalculator.calculateLifePathNumber(request.getDob()));
            profile.setElements(SpiritualityCalculator.calculateElementsJson(request.getDob()));


            dailyEnergyRepository.deleteByAccountId(accountOpt.get().getId());

            System.out.println("====== [CẬP NHẬT SINH MỆNH] ======");
            System.out.println("Đã thay đổi thông tin gốc. Xóa toàn bộ dữ liệu năng lượng cũ để tính toán lại chính xác.");
            System.out.println("=================================");
        }

        profilesRepository.save(profile);
        profile.setHasPassword(accountOpt.get().getPassword_hash() != null);
        profile.setRole(accountOpt.get().getRole().name());
        return ResponseEntity.ok(profile);
    }
}