package com.ptit.rikkei_bank.service;

import com.ptit.rikkei_bank.dto.request.LoginRequest;
import com.ptit.rikkei_bank.dto.request.RefreshTokenRequest;
import com.ptit.rikkei_bank.dto.request.RegisterRequest;
import com.ptit.rikkei_bank.dto.response.AuthResponse;
import com.ptit.rikkei_bank.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    UserResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(HttpServletRequest request);
}
