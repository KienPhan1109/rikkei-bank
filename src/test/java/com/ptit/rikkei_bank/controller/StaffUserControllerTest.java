package com.ptit.rikkei_bank.controller;

import com.ptit.rikkei_bank.dto.response.UserResponse;
import com.ptit.rikkei_bank.exception.GlobalExceptionHandler;
import com.ptit.rikkei_bank.exception.ResourceNotFoundException;
import com.ptit.rikkei_bank.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class StaffUserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        StaffUserController staffUserController = new StaffUserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(staffUserController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        userResponse = new UserResponse(
                1L, "testuser", "test@test.com", "0987654321", "CUSTOMER", false, true, null
        );
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        Page<UserResponse> page = new PageImpl<>(Collections.singletonList(userResponse));
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/staff/users")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Lấy danh sách người dùng thành công"))
                .andExpect(jsonPath("$.data.content[0].username").value("testuser"));

        verify(userService, times(1)).getAllUsers(PageRequest.of(0, 10));
    }

    @Test
    void testGetUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/staff/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Lấy thông tin người dùng thành công"))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(2L)).thenThrow(new ResourceNotFoundException("Không tìm thấy người dùng với ID: 2"));

        mockMvc.perform(get("/api/v1/staff/users/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Không tìm thấy người dùng với ID: 2"));

        verify(userService, times(1)).getUserById(2L);
    }

    @Test
    void testLockUser_Success() throws Exception {
        doNothing().when(userService).lockUser(1L);

        mockMvc.perform(post("/api/v1/staff/users/1/lock")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Khóa người dùng thành công!"));

        verify(userService, times(1)).lockUser(1L);
    }

    @Test
    void testUnlockUser_Success() throws Exception {
        doNothing().when(userService).unlockUser(1L);

        mockMvc.perform(post("/api/v1/staff/users/1/unlock")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Mở khóa người dùng thành công!"));

        verify(userService, times(1)).unlockUser(1L);
    }
}
