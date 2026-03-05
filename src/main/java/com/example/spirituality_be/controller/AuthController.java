package com.example.spirituality_be.controller;

import com.example.spirituality_be.dto.JwtAuthenticationResponse;
import com.example.spirituality_be.dto.LoginRequest;
import com.example.spirituality_be.dto.SignUpRequest;
import com.example.spirituality_be.model.Accounts;
import com.example.spirituality_be.repository.AccountsRepository;
import com.example.spirituality_be.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AccountsRepository accountsRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    com.example.spirituality_be.service.EmailService emailService;

    @org.springframework.beans.factory.annotation.Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Optional<Accounts> accountOpt = accountsRepository.findByEmail(loginRequest.getEmail());
        if (accountOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tài khoản với email này");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            com.example.spirituality_be.security.UserPrincipal userPrincipal = (com.example.spirituality_be.security.UserPrincipal) authentication.getPrincipal();
            Accounts account = accountOpt.get();
            account.setLast_login_at(java.time.LocalDateTime.now());
            accountsRepository.save(account);

            String jwt = tokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mật khẩu không chính xác, vui lòng kiểm tra lại");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi hệ thống xảy ra, vui lòng thử lại sau");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        if(accountsRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>("Email đã tồn tại trong hệ thống!", HttpStatus.BAD_REQUEST);
        }

        String password = signUpRequest.getPassword();
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";
        if (password == null || !password.matches(passwordRegex)) {
            return new ResponseEntity<>("Mật khẩu không đạt yêu cầu bảo mật: Phải có ít nhất 8 ký tự, 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt!", HttpStatus.BAD_REQUEST);
        }

        Accounts account = new Accounts();
        account.setEmail(signUpRequest.getEmail());
        account.setPassword_hash(passwordEncoder.encode(password));

        accountsRepository.save(account);

        return new ResponseEntity<>("Đăng ký tài khoản thành công!", HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        Accounts account = accountsRepository.findByEmail(email).orElse(null);
        if (account == null) {
            return new ResponseEntity<>("Không tìm thấy tài khoản với email này", HttpStatus.NOT_FOUND);
        }


        String resetToken = tokenProvider.generateResetToken(email);

        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
        try {
            emailService.sendResetPasswordEmail(email, resetLink);
            System.out.println("====== ĐÃ GỬI EMAIL THẬT ĐẾN: " + email + " ======");
        } catch (Exception e) {
            System.err.println("Lỗi gửi mail: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể gửi email lúc này, vui lòng thử lại sau.");
        }

        return ResponseEntity.ok("Vui lòng kiểm tra email để đặt lại mật khẩu.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody java.util.Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (!tokenProvider.validateToken(token)) {
            return new ResponseEntity<>("Link đã hết hạn hoặc không hợp lệ, vui lòng yêu cầu lại", HttpStatus.BAD_REQUEST);
        }

        String email = tokenProvider.getUsernameFromJWT(token);
        Accounts account = accountsRepository.findByEmail(email).orElse(null);
        if (account == null) {
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.NOT_FOUND);
        }

        account.setPassword_hash(passwordEncoder.encode(newPassword));
        accountsRepository.save(account);

        System.out.println("====== THÔNG BÁO BẢO MẬT ======");
        System.out.println("Tài khoản: " + email + " đã đổi mật khẩu thành công.");
        System.out.println("===============================");

        try {
            emailService.sendPasswordChangedNotification(email);
        } catch (Exception e) {
            System.err.println("Lỗi gửi thông báo đổi pass: " + e.getMessage());
        }

        return ResponseEntity.ok("Đặt lại mật khẩu thành công!");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@org.springframework.security.core.annotation.AuthenticationPrincipal com.example.spirituality_be.security.UserPrincipal userPrincipal, @RequestBody java.util.Map<String, String> request) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        Accounts account = accountsRepository.findById(userPrincipal.getId()).orElse(null);
        if (account == null) {
            return new ResponseEntity<>("Không tìm thấy tài khoản", HttpStatus.NOT_FOUND);
        }



        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";
        if (newPassword == null || !newPassword.matches(passwordRegex)) {
            return new ResponseEntity<>("Mật khẩu mới không đạt yêu cầu bảo mật: Phải có ít nhất 8 ký tự, 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt!", HttpStatus.BAD_REQUEST);
        }

        account.setPassword_hash(passwordEncoder.encode(newPassword));
        accountsRepository.save(account);

        try {
            emailService.sendPasswordChangedNotification(account.getEmail());
        } catch (Exception e) {
            System.err.println("Lỗi gửi thông báo đổi pass: " + e.getMessage());
        }

        return ResponseEntity.ok("Đổi mật khẩu thành công!");
    }
}
