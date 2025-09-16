package klu.auth;

import klu.mail.MailService;
import klu.user.User;
import klu.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    private final Map<String, TokenRecord> tokenStore = new ConcurrentHashMap<>();
    private final long tokenTtlSeconds = 15 * 60; // 15 minutes

    public PasswordResetService(UserRepository userRepository, PasswordEncoder passwordEncoder, MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    public void issueResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return; // silently ignore
        }
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, new TokenRecord(userOpt.get().getId(), Instant.now().plusSeconds(tokenTtlSeconds)));
        mailService.sendPasswordResetEmail(email, token);
    }

    public void resetPassword(String token, String newPassword) {
        TokenRecord record = tokenStore.get(token);
        if (record == null || Instant.now().isAfter(record.expiresAt())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        User user = userRepository.findById(record.userId()).orElseThrow();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenStore.remove(token);
    }

    private record TokenRecord(String userId, Instant expiresAt) {}
}


