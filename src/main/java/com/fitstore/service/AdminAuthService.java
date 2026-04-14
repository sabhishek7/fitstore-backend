package com.fitstore.service;

import com.fitstore.dto.AdminLoginDto;
import com.fitstore.dto.AdminLoginResponseDto;
import com.fitstore.entity.AdminUser;
import com.fitstore.repository.AdminUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AdminAuthService {

    private final AdminUserRepository adminUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // In-memory token store: token -> expiry time
    private final ConcurrentHashMap<String, LocalDateTime> tokenStore = new ConcurrentHashMap<>();
    private static final long TOKEN_EXPIRY_HOURS = 24;

    public AdminAuthService(AdminUserRepository adminUserRepository, BCryptPasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticate admin and return a session token.
     */
    public AdminLoginResponseDto login(AdminLoginDto loginDto) {
        AdminUser admin = adminUserRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(loginDto.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = UUID.randomUUID().toString();
        tokenStore.put(token, LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS));

        log.info("Admin logged in: {}", admin.getUsername());
        return new AdminLoginResponseDto(token, admin.getName());
    }

    /**
     * Validate an admin token. Returns true if the token exists and hasn't expired.
     */
    public boolean validateToken(String token) {
        LocalDateTime expiry = tokenStore.get(token);
        if (expiry == null) return false;
        if (LocalDateTime.now().isAfter(expiry)) {
            tokenStore.remove(token);
            return false;
        }
        return true;
    }

    /**
     * Invalidate a token (logout).
     */
    public void logout(String token) {
        tokenStore.remove(token);
    }
}
