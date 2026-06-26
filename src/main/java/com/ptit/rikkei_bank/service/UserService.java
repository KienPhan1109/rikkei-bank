package com.ptit.rikkei_bank.service;

import com.ptit.rikkei_bank.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
}
