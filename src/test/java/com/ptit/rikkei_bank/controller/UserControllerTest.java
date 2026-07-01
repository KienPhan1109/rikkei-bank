package com.ptit.rikkei_bank.controller;

import com.ptit.rikkei_bank.dto.response.UserResponse;
import com.ptit.rikkei_bank.entity.Role;
import com.ptit.rikkei_bank.entity.User;
import com.ptit.rikkei_bank.security.CustomUserDetails;
import com.ptit.rikkei_bank.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    private User user;
    private CustomUserDetails customUserDetails;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        UserController userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setIsActive(true);
        Role role = new Role(1L, "CUSTOMER", "Customer");
        user.setRole(role);

        customUserDetails = new CustomUserDetails(user);

        userResponse = new UserResponse(
                1L, "testuser", "test@test.com", "0987654321", "CUSTOMER", false, true, null
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }




    @Test
    void testChangePassword_Success() throws Exception {
        String requestJson = "{\"oldPassword\":\"oldpassword\",\"newPassword\":\"newpassword\"}";
        
        // Thiết lập Authentication vào SecurityContextHolder thủ công để phục vụ @AuthenticationPrincipal
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );
        
        doNothing().when(userService).changePassword(1L, "oldpassword", "newpassword");

        mockMvc.perform(put("/api/v1/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Đổi mật khẩu thành công!"));

        verify(userService, times(1)).changePassword(1L, "oldpassword", "newpassword");
    }

    @Test
    void testChangePassword_ValidationFailed() throws Exception {
        String requestJsonInvalid = "{\"oldPassword\":\"\",\"newPassword\":\"\"}";
        
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );

        mockMvc.perform(put("/api/v1/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonInvalid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).changePassword(anyLong(), anyString(), anyString());
    }
}
