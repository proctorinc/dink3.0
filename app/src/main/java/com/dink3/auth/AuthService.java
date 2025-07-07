package com.dink3.auth;

import com.dink3.common.Argon2PasswordHasher;
import com.dink3.jooq.tables.pojos.Users;
import com.dink3.jooq.tables.pojos.RefreshTokens;
import com.dink3.user.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final Argon2PasswordHasher passwordHasher;
    private final SecretKey jwtSecretKey;
    private final long jwtExpirationMs;

    public AuthService(UserRepository userRepository,
                       Argon2PasswordHasher passwordHasher,
                       @Value("${jwt.secret}") String jwtSecret,
                       @Value("${jwt.expiration}") long jwtExpirationMs) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public Users register(String email, String username, String password) {
        String hashed = passwordHasher.hash(password);
        Users user = new Users();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(hashed);
        user.setRole("user");
        user.setCreatedAt(LocalDateTime.now().toString());
        userRepository.save(user);
        return user;
    }

    public Optional<Users> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordHasher.matches(password, user.getPasswordHash()));
    }

    public String generateJwtToken(Users user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Users user) {
        // In production, store in DB and associate with user
        return UUID.randomUUID().toString();
    }

    public boolean validatePasswordStrength(String password) {
        // OWASP: min 8, max 64, at least 1 letter and 1 number
        return password != null && password.length() >= 8 && password.length() <= 64
                && password.matches(".*[A-Za-z].*") && password.matches(".*[0-9].*");
    }
} 