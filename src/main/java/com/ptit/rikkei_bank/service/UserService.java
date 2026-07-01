package com.ptit.rikkei_bank.service;

import com.ptit.rikkei_bank.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse getUserById(Long id);
    void lockUser(Long userId);
    void unlockUser(Long userId);
    void changePassword(Long userId, String oldPassword, String newPassword);
}
