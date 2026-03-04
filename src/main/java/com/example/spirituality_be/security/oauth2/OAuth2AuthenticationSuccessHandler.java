package com.example.spirituality_be.security.oauth2;

import com.example.spirituality_be.security.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            return;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        com.example.spirituality_be.security.UserPrincipal userPrincipal = (com.example.spirituality_be.security.UserPrincipal) authentication.getPrincipal();
        String token = tokenProvider.generateTokenFromUsername(userPrincipal.getEmail());

        return UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect")
                .queryParam("token", token)
                .build().toUriString();
    }
}