package com.example.spirituality_be.security.oauth2;

import com.example.spirituality_be.model.Accounts;
import com.example.spirituality_be.repository.AccountsRepository;
import com.example.spirituality_be.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private AccountsRepository accountsRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");

        if(!StringUtils.hasText(email)) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<Accounts> accountOptional = accountsRepository.findByEmail(email);
        Accounts account;
        if(accountOptional.isPresent()) {
            account = accountOptional.get();
            account = updateExistingAccount(account, oAuth2User);
        } else {
            account = registerNewAccount(oAuth2User);
        }

        return UserPrincipal.create(account, oAuth2User.getAttributes());
    }

    @org.springframework.transaction.annotation.Transactional
    private Accounts registerNewAccount(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        System.out.println("Đang đăng ký tài khoản mới từ Google: " + email);
        Accounts account = new Accounts();
        account.setEmail(email);
        // Đặt một giá trị mặc định tránh lỗi NOT NULL nếu có trong database
        account.setPassword_hash("OAUTH2_USER");
        account.setRole(Accounts.Role.user);
        account.setStatus(Accounts.Status.active);
        account.setLast_login_at(java.time.LocalDateTime.now());
        try {
            Accounts savedAccount = accountsRepository.save(account);
            System.out.println("Đăng ký thành công tài khoản ID: " + savedAccount.getId());
            return savedAccount;
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu tài khoản vào Database: " + e.getMessage());
            throw e;
        }
    }

    private Accounts updateExistingAccount(Accounts existingAccount, OAuth2User oAuth2User) {
        existingAccount.setLast_login_at(java.time.LocalDateTime.now());
        return accountsRepository.save(existingAccount);
    }
}
