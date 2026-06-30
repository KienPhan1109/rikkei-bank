package com.ptit.rikkei_bank.service.impl;
import java.util.Date;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.BadCredentialsException;

import com.ptit.rikkei_bank.dto.request.LoginRequest;
import com.ptit.rikkei_bank.dto.request.RefreshTokenRequest;
import com.ptit.rikkei_bank.dto.request.RegisterRequest;
import com.ptit.rikkei_bank.dto.response.AuthResponse;
import com.ptit.rikkei_bank.dto.response.UserResponse;
import com.ptit.rikkei_bank.entity.RefreshToken;
import com.ptit.rikkei_bank.entity.Role;
import com.ptit.rikkei_bank.entity.User;
import com.ptit.rikkei_bank.exception.InvalidTokenException;
import com.ptit.rikkei_bank.exception.LoginErrorException;
import com.ptit.rikkei_bank.mapper.UserMapper;
import com.ptit.rikkei_bank.repository.RefreshTokenRepository;
import com.ptit.rikkei_bank.repository.RoleRepository;
import com.ptit.rikkei_bank.repository.UserRepository;
import com.ptit.rikkei_bank.security.JwtTokenProvider;
import com.ptit.rikkei_bank.service.AuthService;
import com.ptit.rikkei_bank.service.TokenBlacklistService;
import com.ptit.rikkei_bank.config.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistService tokenBlacklistService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        Role userRole = roleRepository.findByName("CUSTOMER")
                .orElseGet(() -> roleRepository.save(new Role(null, "CUSTOMER", "Khách hàng")));
        user.setRole(userRole);

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateAccessToken(authentication);
            
            User user = userRepository.findByUsernameAndIsDeletedFalse(request.getUsername())
                    .orElseThrow(() -> new LoginErrorException("Không tìm thấy người dùng"));

            // Create or update Refresh Token
            RefreshToken refreshToken = refreshTokenRepository.findByUser(user).orElse(new RefreshToken());
            refreshToken.setUser(user);
            refreshToken.setToken(UUID.randomUUID().toString());
            // Token hợp lệ sử dụng cấu hình từ properties
            refreshToken.setExpiryDate(Instant.now().plusMillis(jwtProperties.getRefreshExpiration()));
            refreshToken.setRevoked(false);
            if (refreshToken.getId() == null) {
                refreshToken.setCreatedAt(LocalDateTime.now());
            }
            refreshTokenRepository.save(refreshToken);

            return new AuthResponse(jwt, refreshToken.getToken(), userMapper.toResponse(user));
        } catch (DisabledException | LockedException e) {
            log.warn("Login failed: User account is locked/disabled - {}", request.getUsername());
            throw new LoginErrorException("Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên!");
        } catch (BadCredentialsException e) {
            log.warn("Login failed: Bad credentials - {}", request.getUsername());
            throw new LoginErrorException("Đăng nhập thất bại. Sai tài khoản hoặc mật khẩu!");
        } catch (Exception e) {
            log.error("Login failed: Unexpected error - {}", e.getMessage(), e);
            throw new LoginErrorException("Đăng nhập thất bại. Đã có lỗi xảy ra!");
        }
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Refresh token không tồn tại trong hệ thống!"));

        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new InvalidTokenException("Refresh token đã hết hạn, vui lòng đăng nhập lại!");
        }

        if (token.getRevoked()) {
            throw new InvalidTokenException("Refresh token đã bị thu hồi!");
        }

        String accessToken = tokenProvider.generateAccessTokenFromUsername(token.getUser().getUsername());
        
        return new AuthResponse(accessToken, token.getToken(), userMapper.toResponse(token.getUser()));
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String jwt = bearerToken.substring(7);
            if (tokenProvider.validateToken(jwt)) {
                try {
                    Date expiration = tokenProvider.getExpirationFromJWT(jwt);
                    long ttlSeconds = (expiration.getTime() - System.currentTimeMillis()) / 1000;
                    log.info("[Logout] Token TTL remaining: {} seconds", ttlSeconds);
                    if (ttlSeconds > 0) {
                        tokenBlacklistService.blacklistToken(jwt, ttlSeconds);
                    } else {
                        log.warn("[Logout] Token is already expired or near-expired, skip blacklisting");
                    }
                } catch (Exception e) {
                    log.warn("[Logout] Error calculating TTL, blacklisting with default 300s. Error: {}", e.getMessage());
                    tokenBlacklistService.blacklistToken(jwt, 300);
                }

                // Revoke Refresh Token in database
                try {
                    String username = tokenProvider.getUsernameFromJWT(jwt);
                    userRepository.findByUsernameAndIsDeletedFalse(username).ifPresent(user -> {
                        refreshTokenRepository.findByUser(user).ifPresent(refreshToken -> {
                            refreshToken.setRevoked(true);
                            refreshTokenRepository.save(refreshToken);
                            log.info("[Logout] Successfully revoked refresh token for user: {}", username);
                        });
                    });
                } catch (Exception e) {
                    log.error("[Logout] Failed to revoke refresh token: {}", e.getMessage());
                }
            } else {
                log.warn("[Logout] Token validation failed during logout");
            }
        } else {
            log.warn("[Logout] Authorization header is missing or does not start with Bearer");
        }
    }
}
