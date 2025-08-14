package com.gradlemedium100.security.service.impl;

import com.gradlemedium100.security.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Implementation of TokenService for JWT token operations.
 */
@Service
public class TokenServiceImpl implements TokenService {

    private final SecretKey secretKey;
    private final long tokenValidityMs;

    /**
     * Constructor that initializes the token service with configuration values.
     *
     * @param secret The JWT secret key
     * @param validityInMs The token validity in milliseconds
     */
    public TokenServiceImpl(
            @Value("${security.jwt.token.secret-key:secretkey}") String secret,
            @Value("${security.jwt.token.validity-ms:3600000}") long validityInMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.tokenValidityMs = validityInMs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateToken(String userId, String[] roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenValidityMs);
        
        return Jwts.builder()
                .setSubject(userId)
                .claim("roles", String.join(",", roles))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String roles = claims.get("roles", String.class);
        return roles != null ? roles.split(",") : new String[0];
    }
    
    /**
     * Extracts claims from the given JWT token.
     *
     * @param token The JWT token
     * @return Claims extracted from the token
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}