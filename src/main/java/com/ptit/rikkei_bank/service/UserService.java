package com.ptit.rikkei_bank.service;

import com.ptit.rikkei_bank.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponse> getAllUsers(Pageable pageable);
    void toggleUserStatus(Long userId, boolean active);
    void deleteUser(Long userId);
    void changePassword(Long userId, String oldPassword, String newPassword);
}
