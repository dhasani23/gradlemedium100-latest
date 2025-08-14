package com.gradlemedium100.security.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.gradlemedium100.security.service.JwtService;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementation of {@link JwtService} that handles JWT token generation,
 * validation, and extraction of claims.
 */
@Service
public class JwtServiceImpl implements JwtService {

    /**
     * Secret key used for signing JWT tokens.
     * Should be at least 256 bits long for HS256 algorithm.
     * In a real production environment, this should be stored securely and
     * not hardcoded in the source code.
     */
    @Value("${jwt.secret-key:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String SECRET_KEY;

    /**
     * JWT token expiration time in milliseconds.
     * Default: 24 hours
     */
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    /**
     * Extracts the subject (username) from a JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Generates a new JWT token for the given user with no extra claims.
     *
     * @param userDetails the user details
     * @return the generated JWT token
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a new JWT token with extra claims for the given user.
     *
     * @param extraClaims extra claims to add to the token
     * @param userDetails the user details
     * @return the generated JWT token
     */
    @Override
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Validates if the token belongs to the given user and is not expired.
     *
     * @param token       the JWT token to validate
     * @param userDetails the user details
     * @return true if the token is valid, false otherwise
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token the JWT token
     * @return the claims
     */
    @Override
    public Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // FIXME: Better exception handling strategy needed
            throw new RuntimeException("Invalid token or token expired", e);
        }
    }

    /**
     * Checks if a token has expired.
     *
     * @param token the JWT token
     * @return true if the token has expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Builds a JWT token with the given claims, user, and expiration.
     *
     * @param extraClaims extra claims to add to the token
     * @param userDetails the user details
     * @param expiration  the token expiration time in milliseconds
     * @return the built JWT token
     */
    public String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        Date issuedAt = new Date();
        Date expirationDate = new Date(issuedAt.getTime() + expiration);

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(expirationDate)
                // TODO: Add additional standard claims like issuer, audience, etc.
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts a specific claim from a JWT token.
     *
     * @param token           the JWT token
     * @param claimsResolver  function to extract a specific claim
     * @param <T>             the claim type
     * @return the extracted claim
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Gets the signing key for JWT token verification.
     *
     * @return the signing key
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}