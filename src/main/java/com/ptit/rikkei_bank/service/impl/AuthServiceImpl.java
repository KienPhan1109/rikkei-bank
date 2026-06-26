package com.ptit.rikkei_bank.service.impl;

import com.ptit.rikkei_bank.dto.request.LoginRequest;
import com.ptit.rikkei_bank.dto.request.RefreshTokenRequest;
import com.ptit.rikkei_bank.dto.request.RegisterRequest;
import com.ptit.rikkei_bank.dto.response.AuthResponse;
import com.ptit.rikkei_bank.dto.response.UserResponse;
import com.ptit.rikkei_bank.entity.RefreshToken;
import com.ptit.rikkei_bank.entity.Role;
import com.ptit.rikkei_bank.entity.TokenBlackList;
import com.ptit.rikkei_bank.entity.User;
import com.ptit.rikkei_bank.exception.LoginErrorException;
import com.ptit.rikkei_bank.mapper.UserMapper;
import com.ptit.rikkei_bank.repository.RefreshTokenRepository;
import com.ptit.rikkei_bank.repository.RoleRepository;
import com.ptit.rikkei_bank.repository.TokenBlackListRepository;
import com.ptit.rikkei_bank.repository.UserRepository;
import com.ptit.rikkei_bank.security.JwtTokenProvider;
import com.ptit.rikkei_bank.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlackListRepository tokenBlackListRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new com.ptit.rikkei_bank.exception.DuplicateResourceException("Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new com.ptit.rikkei_bank.exception.DuplicateResourceException("Email đã tồn tại!");
        }

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
            
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new LoginErrorException("Không tìm thấy người dùng"));

            // Create or update Refresh Token
            RefreshToken refreshToken = refreshTokenRepository.findByUser(user).orElse(new RefreshToken());
            refreshToken.setUser(user);
            refreshToken.setToken(UUID.randomUUID().toString());
            // Token hợp lệ trong 30 ngày
            refreshToken.setExpiryDate(Instant.now().plusMillis(30L * 24 * 60 * 60 * 1000));
            refreshToken.setRevoked(false);
            if (refreshToken.getId() == null) {
                refreshToken.setCreatedAt(LocalDateTime.now());
            }
            refreshTokenRepository.save(refreshToken);

            return AuthResponse.builder()
                    .accessToken(jwt)
                    .refreshToken(refreshToken.getToken())
                    .user(userMapper.toResponse(user))
                    .build();
        } catch (Exception e) {
            throw new LoginErrorException("Đăng nhập thất bại. Sai tài khoản hoặc mật khẩu!");
        }
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại trong hệ thống!"));

        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token đã hết hạn, vui lòng đăng nhập lại!");
        }

        if (token.getRevoked()) {
            throw new RuntimeException("Refresh token đã bị thu hồi!");
        }

        String accessToken = tokenProvider.generateAccessTokenFromUsername(token.getUser().getUsername());
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(token.getToken())
                .user(userMapper.toResponse(token.getUser()))
                .build();
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String jwt = bearerToken.substring(7);
            if (tokenProvider.validateToken(jwt)) {
                // Đưa access token vào blacklist
                TokenBlackList blackList = new TokenBlackList();
                blackList.setAccessToken(jwt);
                // Thời gian blacklist mặc định là thời điểm hiện tại
                blackList.setBlacklistedAt(LocalDateTime.now());
                blackList.setCreatedAt(LocalDateTime.now());
                // Cần tính toán expiryAt chính xác từ token, ở đây tạm gán +5phút
                blackList.setExpiryAt(LocalDateTime.now().plusMinutes(5));
                
                tokenBlackListRepository.save(blackList);
            }
        }
    }
}
