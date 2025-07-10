package com.dink3.auth;

import com.dink3.common.Argon2PasswordHasher;
import com.dink3.jooq.tables.pojos.User;
import com.dink3.user.UserRepository;
import com.dink3.user.dto.UserSubscriptionDto;
import com.dink3.user.dto.UserDto;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import com.dink3.util.UuidGenerator;
import com.dink3.plaid.repository.UserSubscriptionRepository;
import com.dink3.jooq.tables.pojos.UserSubscription;
import java.security.Key;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final Argon2PasswordHasher passwordHasher;
    private final SecretKey jwtSecretKey;
    private final long jwtExpirationMs;

    public AuthService(UserRepository userRepository,
                       UserSubscriptionRepository userSubscriptionRepository,
                       Argon2PasswordHasher passwordHasher,
                       @Value("${jwt.secret}") String jwtSecret,
                       @Value("${jwt.expiration}") long jwtExpirationMs) {
        this.userRepository = userRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.passwordHasher = passwordHasher;
        this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public User register(String email, String username, String password)  {
        // Check if user already exists
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Unable to register with the provided information");
        }
        
        String hashed = passwordHasher.hash(password);
        User user = UserDto.builder()
                .id(UuidGenerator.generateUuid())
                .email(email)
                .username(username)
                .passwordHash(hashed)
                .role("user")
                .createdAt(LocalDateTime.now().toString())
                .build()
                .toUser();
        userRepository.save(user);
        
        // Create default subscription for the user
        UserSubscription subscription = UserSubscriptionDto.createBasicSubscription(user.getId())
                .toUserSubscription();
        subscription.setId(UuidGenerator.generateUuid());
        userSubscriptionRepository.save(subscription);
        
        return user;
    }

    public Optional<User> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordHasher.matches(password, user.getPasswordHash()));
    }

    public String generateJwtToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);
        Key key = Keys.hmacShaKeyFor(jwtSecretKey.getEncoded());
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(User user) {
        // In production, store in DB and associate with user
        return UUID.randomUUID().toString();
    }
} 