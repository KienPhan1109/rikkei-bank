package com.ptit.rikkei_bank.service;

import com.ptit.rikkei_bank.dto.response.UserResponse;
import com.ptit.rikkei_bank.entity.User;
import com.ptit.rikkei_bank.exception.BusinessException;
import com.ptit.rikkei_bank.repository.UserRepository;
import com.ptit.rikkei_bank.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("hashedpassword");
        user.setIsActive(true);

        userResponse = new UserResponse(
                1L, "testuser", "test@test.com", "0987654321", "CUSTOMER", false, true, null
        );
    }

    @Test
    void testGetAllUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserResponse> page = new PageImpl<>(Collections.singletonList(userResponse));
        
        when(userRepository.findAllProjected(pageable)).thenReturn(page);

        Page<UserResponse> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).getUsername());
        verify(userRepository, times(1)).findAllProjected(pageable);
    }

    @Test
    void testToggleUserStatus_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.toggleUserStatus(1L, false);

        assertFalse(user.getIsActive());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testToggleUserStatus_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> userService.toggleUserStatus(1L, false));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(BusinessException.class, () -> userService.deleteUser(1L));
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void testChangePassword_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldpass", "hashedpassword")).thenReturn(true);
        when(passwordEncoder.encode("newpass")).thenReturn("newhashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.changePassword(1L, "oldpass", "newpass");

        assertEquals("newhashedpassword", user.getPassword());
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).matches("oldpass", "hashedpassword");
        verify(passwordEncoder, times(1)).encode("newpass");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testChangePassword_PasswordMismatch() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongoldpass", "hashedpassword")).thenReturn(false);

        assertThrows(BusinessException.class, () -> userService.changePassword(1L, "wrongoldpass", "newpass"));
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).matches("wrongoldpass", "hashedpassword");
        verify(userRepository, never()).save(any(User.class));
    }
}
