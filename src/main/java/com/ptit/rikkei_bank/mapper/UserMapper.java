package com.ptit.rikkei_bank.mapper;

import com.ptit.rikkei_bank.dto.request.RegisterRequest;
import com.ptit.rikkei_bank.dto.response.UserResponse;
import com.ptit.rikkei_bank.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        return user;
    }

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole() != null ? user.getRole().getName() : null);
        response.setIsKyc(user.getIsKyc());
        response.setIsActive(user.getIsActive());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
