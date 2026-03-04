package com.example.spirituality_be.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret:SpiritualitySecretKeyForJwtTokenGenerationShouldBeLongAndSecure2024}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs:604800000}")
    private int jwtExpirationInMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    public String generateTokenFromUsername(String username) {
        return generateTokenWithCustomExpiration(username, (long) jwtExpirationInMs);
    }

    public String generateResetToken(String username) {
        return generateTokenWithCustomExpiration(username, 900000L);
    }

    private String generateTokenWithCustomExpiration(String username, Long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SecurityException | MalformedJwtException ex) {
            System.err.println("Chữ ký JWT không hợp lệ");
        } catch (ExpiredJwtException ex) {
            System.err.println("Token JWT đã hết hạn");
        } catch (UnsupportedJwtException ex) {
            System.err.println("Token JWT không được hỗ trợ");
        } catch (IllegalArgumentException ex) {
            System.err.println("Dữ liệu JWT trống");
        }
        return false;
    }
}

