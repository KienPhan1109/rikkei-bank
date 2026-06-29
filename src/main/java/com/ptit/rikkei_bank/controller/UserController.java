package com.ptit.rikkei_bank.controller;

import com.ptit.rikkei_bank.dto.request.ChangePasswordRequest;
import com.ptit.rikkei_bank.dto.response.ApiResponse;
import com.ptit.rikkei_bank.dto.response.PageResponse;
import com.ptit.rikkei_bank.dto.response.UserResponse;
import com.ptit.rikkei_bank.security.CustomUserDetails;
import com.ptit.rikkei_bank.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> usersPage = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), 
                "Lấy danh sách người dùng thành công", 
                PageResponse.of(usersPage)
        ));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(
            @PathVariable("id") Long id,
            @RequestParam("active") boolean active) {
        userService.toggleUserStatus(id, active);
        String message = active ? "Kích hoạt người dùng thành công!" : "Khóa người dùng thành công!";
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), 
                message, 
                null
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), 
                "Xóa người dùng thành công!", 
                null
        ));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        Long userId = userDetails.getUser().getId();
        userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), 
                "Đổi mật khẩu thành công!", 
                null
        ));
    }
}
