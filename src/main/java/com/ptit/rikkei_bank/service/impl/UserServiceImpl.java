package com.ptit.rikkei_bank.service.impl;

import com.ptit.rikkei_bank.dto.response.UserResponse;
import com.ptit.rikkei_bank.entity.User;
import com.ptit.rikkei_bank.exception.BusinessException;
import com.ptit.rikkei_bank.repository.UserRepository;
import com.ptit.rikkei_bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAllProjected(pageable);
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng!"));
        
        if (!active && user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole().getName())) {
            throw new BusinessException("Không được phép khóa tài khoản quản trị viên (ADMIN)!");
        }
        
        user.setIsActive(active);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng!"));

        if (user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole().getName())) {
            throw new BusinessException("Không được phép xóa tài khoản quản trị viên (ADMIN)!");
        }

        user.setIsDeleted(true);
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng!"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("Mật khẩu cũ không chính xác!");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
