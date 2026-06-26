package com.ptit.rikkei_bank.service.impl;

import com.ptit.rikkei_bank.dto.response.UserResponse;
import com.ptit.rikkei_bank.mapper.UserMapper;
import com.ptit.rikkei_bank.repository.UserRepository;
import com.ptit.rikkei_bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }
}
