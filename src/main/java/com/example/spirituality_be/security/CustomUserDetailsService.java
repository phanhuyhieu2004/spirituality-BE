package com.example.spirituality_be.security;

import com.example.spirituality_be.model.Accounts;
import com.example.spirituality_be.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    AccountsRepository accountsRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Accounts account = accountsRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Account Not Found with email: " + email));

        return UserPrincipal.create(account);
    }
}